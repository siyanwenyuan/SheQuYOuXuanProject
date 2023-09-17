package com.chen.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chen.activity.mq.constant.MqConst;
import com.chen.activity.mq.service.RabbitService;
import com.chen.cart.client.CartFeignClient;
import com.chen.client.product.ProductFeignClient;
import com.chen.client.user.UserFeignClient;
import com.chen.order.mapper.OrderInfoMapper;
import com.chen.order.mapper.OrderItemMapper;
import com.chen.order.service.OrderInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.search.common.auth.AuthContextHolder;
import com.chen.search.common.constant.RedisConst;
import com.chen.search.common.exception.SsyxException;
import com.chen.search.common.result.ResultCodeEnum;
import com.chen.search.enums.*;
import com.chen.search.model.activity.ActivityRule;
import com.chen.search.model.activity.CouponInfo;
import com.chen.search.model.order.CartInfo;
import com.chen.search.model.order.OrderInfo;
import com.chen.search.model.order.OrderItem;
import com.chen.search.model.order.PaymentInfo;
import com.chen.search.vo.order.CartInfoVo;
import com.chen.search.vo.order.OrderConfirmVo;
import com.chen.search.vo.order.OrderSubmitVo;
import com.chen.search.vo.order.OrderUserQueryVo;
import com.chen.search.vo.product.SkuStockLockVo;
import com.chen.search.vo.user.LeaderAddressVo;
import com.chen.ssyx.activity.client.ActivityFeignClient;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.xml.transform.Result;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * 订单 服务实现类
 * </p>
 *
 * @author chenwan
 * @since 2023-09-11
 */
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {


    @Qualifier("userFeignClient")
    private UserFeignClient userFeignClient;

    @Qualifier("productFeignClient")
    private ProductFeignClient productFeignClient;


    @Qualifier("cartFeignClient")
    private CartFeignClient cartFeignClient;

    @Autowired
    private RedisTemplate redisTemplate;

    @Qualifier("activityFeignClient")
    private ActivityFeignClient activityFeignClient;

    @Qualifier("rabbitService")
    private RabbitService rabbitService;

    @Qualifier("orderItemMapper")
    private OrderItemMapper orderItemMapper;


    @Override
    public OrderConfirmVo confirmOrder() {
        //首先获取对应的用户id
        Long userId = AuthContextHolder.getUserId();
        //通过远程调用获取对应的团长信息
        LeaderAddressVo leaderAddressVo = userFeignClient.getUserAddressByUserId(userId);
        //获取购物车中对应的商品
        List<CartInfo> cartCheckList = cartFeignClient.getCartCheckList(userId);


        //设置唯一表示订单
        String time = System.currentTimeMillis() + "";
        //设置到/redis中去
        redisTemplate.opsForValue().set(RedisConst.ORDER_REPEAT + time, time, 24, TimeUnit.MINUTES);

        CartInfoVo cartInfoVo = new CartInfoVo();


        List<CartInfo> cartInfoList = cartInfoVo.getCartInfoList();
        cartInfoVo.setCartInfoList(cartInfoList);
        OrderConfirmVo activityAndCoupon = activityFeignClient.findCartActivityAndCoupon((List<CartInfoVo>) cartInfoVo, userId);


        return activityAndCoupon;
    }

    @Override
    public Long submitOrder(OrderSubmitVo orderParamVo) {

        Long userId = AuthContextHolder.getUserId();
        //向其中设置我们的uerId 目的是为了确定是哪个用户的商品
        orderParamVo.setUserId(userId);
        String orderNo = orderParamVo.getOrderNo();
        if (StringUtils.isEmpty(orderNo)) {

            throw new SsyxException(ResultCodeEnum.ILLEGAL_REQUEST);

        }


        //此处使用的lua脚本，目的是为了保证原子性
        String script = "if(redis.call('get', KEYS[1]) == ARGV[1]) then return redis.call('del', KEYS[1]) else return 0 end";

        //此处运行lua脚本
        Boolean flag = (Boolean) redisTemplate.execute(new DefaultRedisScript(script, Boolean.class),

                Arrays.asList(RedisConst.ORDER_REPEAT + orderNo));

        if (!flag) {

            //重复提交，则不再继续进行，直接熬出异常
            throw new SsyxException(ResultCodeEnum.REPEAT_SUBMIT);
        }

        List<CartInfo> cartInfoList = cartFeignClient.getCartCheckList(userId);
        //其中只需要处理普通类型的商品，此处通过filter得到普通类型的商品
        List<CartInfo> commonSkuList = cartInfoList.stream().filter(cartInfo ->
                cartInfo.getSkuType() == SkuType.COMMON.getCode()).collect(Collectors.toList());
        //将其中的类型进行转换
        if (!CollectionUtils.isEmpty(commonSkuList)) {
            List<SkuStockLockVo> commomStockSkuList = commonSkuList.stream().map(item -> {
                SkuStockLockVo skuStockLockVo = new SkuStockLockVo();
                skuStockLockVo.setSkuId(item.getSkuId());
                skuStockLockVo.setSkuNum(item.getSkuNum());
                return skuStockLockVo;
            }).collect(Collectors.toList());
            /**
             * 此处对商品进行库存的验证，并在库存中进行锁定，
             */
            Boolean isCheck = productFeignClient.checkAndLock(commomStockSkuList, orderNo);
            if (!isCheck) {
                throw new SsyxException(ResultCodeEnum.ORDER_STOCK_FALL);

            }

        }

        //此时完成下单过程
        //向两张表中添加数据 sku_info sku_item
        Long orderId = this.saveOrder(orderParamVo, commonSkuList);


        return orderId;

    }

    private Long saveOrder(OrderSubmitVo orderParamVo, List<CartInfo> commonSkuList) {

        //
        if (CollectionUtils.isEmpty(commonSkuList)) {
            throw new SsyxException(ResultCodeEnum.DATA_ERROR);

        }
        //查询提货点和团长信息
        Long userId = AuthContextHolder.getUserId();
        LeaderAddressVo userAddressByUser = userFeignClient.getUserAddressByUserId(userId);
        if (userAddressByUser == null) {
            throw new SsyxException(ResultCodeEnum.DATA_ERROR);

        }


        Map<String, BigDecimal> acticityId = this.computeActivitySplitAmount(commonSkuList);
        Map<String, BigDecimal> couponId = this.computeCouponInfoSplitAmount(commonSkuList, orderParamVo.getCouponId());
        List<OrderItem> orderItemList = new ArrayList<>();
        for (CartInfo cartInfo : commonSkuList) {
            OrderItem orderItem = new OrderItem();


            BigDecimal bigDecimal = acticityId.get("activity" + orderItem.getSkuId());
            if (bigDecimal == null) {
                bigDecimal = new BigDecimal(0);
            }
            BigDecimal bigDecimal1 = couponId.get("coupon" + orderItem.getSkuId());
            if (bigDecimal1 == null) {
                bigDecimal1 = new BigDecimal(0);
            }


            //保存订单
            OrderInfo order = new OrderInfo();
            order.setUserId(userId);
//		private String nickName;
           /* order.setOrderNo(orderSubmitVo.getOrderNo());
            order.setOrderStatus(OrderStatus.UNPAID);
            order.setProcessStatus(ProcessStatus.UNPAID);
            order.setCouponId(orderSubmitVo.getCouponId());
            order.setLeaderId(orderSubmitVo.getLeaderId());
            order.setLeaderName(leaderAddressVo.getLeaderName());
            order.setLeaderPhone(leaderAddressVo.getLeaderPhone());
            order.setTakeName(leaderAddressVo.getTakeName());
            order.setReceiverName(orderSubmitVo.getReceiverName());
            order.setReceiverPhone(orderSubmitVo.getReceiverPhone());
            order.setReceiverProvince(leaderAddressVo.getProvince());
            order.setReceiverCity(leaderAddressVo.getCity());
            order.setReceiverDistrict(leaderAddressVo.getDistrict());
            order.setReceiverAddress(leaderAddressVo.getDetailAddress());
            order.setWareId(cartInfoList.get(0).getWareId());*/


            BigDecimal multiply = orderItem.getSkuPrice().multiply(new BigDecimal(orderItem.getSkuNum()));
            BigDecimal subtract = multiply.subtract(bigDecimal).subtract(bigDecimal1);
            orderItem.setSplitTotalAmount(subtract);


            orderItemList.add(orderItem);


        }

        return null;

    }

    @Override
    public OrderInfo getOrderInfoById(Long orderId) {
        return null;
    }

    @Override
    public OrderInfo getorderInfo(String orderNo) {

        OrderInfo orderInfo = baseMapper.selectOne(new LambdaQueryWrapper<OrderInfo>().
                eq(OrderInfo::getOrderNo, orderNo));

        return orderInfo;
    }


    //更新状态和扣减库存
    @Override
    public void updatePayStatus(String orderNo) {

        //先查询是否已经支付
        OrderInfo orderInfo = baseMapper.selectOne(new LambdaQueryWrapper<OrderInfo>().eq(
                OrderInfo::getOrderNo, orderNo
        ));
        //
        if (orderInfo == null || orderInfo.getOrderStatus() != OrderStatus.UNPAID) {
            return;
        }
        //如果未支付，才继续更新状态
        this.updateOrderStatus(orderInfo.getId());

        //扣减库存
        //此处使用rabbitmq发送消息到product模块中

        //此处发送消息
        rabbitService.sendMsg(MqConst.EXCHANGE_ORDER_DIRECT,
                MqConst.ROUTING_MINUS_STOCK,
                orderNo);


    }

    @Override
    public IPage<OrderInfo> getPageModel(Page<OrderInfo> infoPage, OrderUserQueryVo orderUserQueryVo) {
        LambdaQueryWrapper<OrderInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(OrderInfo::getUserId, orderUserQueryVo.getUserId());
        lambdaQueryWrapper.eq(OrderInfo::getOrderStatus, orderUserQueryVo.getOrderStatus());
        Page<OrderInfo> orderInfoPage = baseMapper.selectPage(infoPage, lambdaQueryWrapper);

        //得到了每个订单，在继续查询每个订单中的订单项（详细信息 ）再把其中的每个订单项进行数据封装
        List<OrderInfo> recoredsList = orderInfoPage.getRecords();
        for (OrderInfo orderInfo : recoredsList) {

            List<OrderItem> orderItemList = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>().
                    eq(OrderItem::getOrderId, orderInfo.getId()));

            //封装数据
            orderInfo.setOrderItemList(orderItemList);
            //封装订单项中不同状态名称的显示
            orderInfo.getParam().put("orderStatusName", orderInfo.getOrderStatus().getComment());

        }
        return orderInfoPage;

        //

    }

    private void updateOrderStatus(Long id) {
        OrderInfo orderInfo = baseMapper.selectById(id);
        orderInfo.setOrderStatus(OrderStatus.WAITING_DELEVER);
        orderInfo.setProcessStatus(ProcessStatus.WAITING_DELEVER);
        baseMapper.updateById(orderInfo);
    }


    //计算总金额
    private BigDecimal computeTotalAmount(List<CartInfo> cartInfoList) {
        BigDecimal total = new BigDecimal(0);
        for (CartInfo cartInfo : cartInfoList) {
            BigDecimal itemTotal = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
            total = total.add(itemTotal);
        }
        return total;
    }

    /**
     * 计算购物项分摊的优惠减少金额
     * 打折：按折扣分担
     * 现金：按比例分摊
     *
     * @param cartInfoParamList
     * @return
     */
    private Map<String, BigDecimal> computeActivitySplitAmount(List<CartInfo> cartInfoParamList) {
        Map<String, BigDecimal> activitySplitAmountMap = new HashMap<>();

        //促销活动相关信息
        List<CartInfoVo> cartInfoVoList = activityFeignClient.findCartActivityList(cartInfoParamList);

        //活动总金额
        BigDecimal activityReduceAmount = new BigDecimal(0);
        if (!CollectionUtils.isEmpty(cartInfoVoList)) {
            for (CartInfoVo cartInfoVo : cartInfoVoList) {
                ActivityRule activityRule = cartInfoVo.getActivityRule();
                List<CartInfo> cartInfoList = cartInfoVo.getCartInfoList();
                if (null != activityRule) {
                    //优惠金额， 按比例分摊
                    BigDecimal reduceAmount = activityRule.getReduceAmount();
                    activityReduceAmount = activityReduceAmount.add(reduceAmount);
                    if (cartInfoList.size() == 1) {
                        activitySplitAmountMap.put("activity:" + cartInfoList.get(0).getSkuId(), reduceAmount);
                    } else {
                        //总金额
                        BigDecimal originalTotalAmount = new BigDecimal(0);
                        for (CartInfo cartInfo : cartInfoList) {
                            BigDecimal skuTotalAmount = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
                            originalTotalAmount = originalTotalAmount.add(skuTotalAmount);
                        }
                        //记录除最后一项是所有分摊金额， 最后一项=总的 - skuPartReduceAmount
                        BigDecimal skuPartReduceAmount = new BigDecimal(0);
                        if (activityRule.getActivityType() == ActivityType.FULL_REDUCTION) {
                            for (int i = 0, len = cartInfoList.size(); i < len; i++) {
                                CartInfo cartInfo = cartInfoList.get(i);
                                if (i < len - 1) {
                                    BigDecimal skuTotalAmount = cartInfo.getCartPrice().
                                            multiply(new BigDecimal(cartInfo.getSkuNum()));
                                    //sku分摊金额
                                    BigDecimal skuReduceAmount = skuTotalAmount.divide(originalTotalAmount, 2,
                                            RoundingMode.HALF_UP).multiply(reduceAmount);
                                    activitySplitAmountMap.put("activity:" + cartInfo.getSkuId(), skuReduceAmount);

                                    skuPartReduceAmount = skuPartReduceAmount.add(skuReduceAmount);
                                } else {
                                    BigDecimal skuReduceAmount = reduceAmount.subtract(skuPartReduceAmount);
                                    activitySplitAmountMap.put("activity:" + cartInfo.getSkuId(), skuReduceAmount);
                                }
                            }
                        } else {
                            for (int i = 0, len = cartInfoList.size(); i < len; i++) {
                                CartInfo cartInfo = cartInfoList.get(i);
                                if (i < len - 1) {
                                    BigDecimal skuTotalAmount = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));

                                    //sku分摊金额
                                    BigDecimal skuDiscountTotalAmount = skuTotalAmount.multiply(activityRule.getBenefitDiscount()
                                            .divide(new BigDecimal("10")));
                                    BigDecimal skuReduceAmount = skuTotalAmount.subtract(skuDiscountTotalAmount);
                                    activitySplitAmountMap.put("activity:" + cartInfo.getSkuId(), skuReduceAmount);

                                    skuPartReduceAmount = skuPartReduceAmount.add(skuReduceAmount);
                                } else {
                                    BigDecimal skuReduceAmount = reduceAmount.subtract(skuPartReduceAmount);
                                    activitySplitAmountMap.put("activity:" + cartInfo.getSkuId(), skuReduceAmount);
                                }
                            }
                        }
                    }
                }
            }
        }
        activitySplitAmountMap.put("activity:total", activityReduceAmount);
        return activitySplitAmountMap;
    }

    //优惠卷优惠金额
    private Map<String, BigDecimal> computeCouponInfoSplitAmount(List<CartInfo> cartInfoList, Long couponId) {
        Map<String, BigDecimal> couponInfoSplitAmountMap = new HashMap<>();

        if (null == couponId) return couponInfoSplitAmountMap;
        CouponInfo couponInfo = activityFeignClient.findRangeSkuIdList(cartInfoList, couponId);

        if (null != couponInfo) {
            //sku对应的订单明细
            Map<Long, CartInfo> skuIdToCartInfoMap = new HashMap<>();
            for (CartInfo cartInfo : cartInfoList) {
                skuIdToCartInfoMap.put(cartInfo.getSkuId(), cartInfo);
            }
            //优惠券对应的skuId列表
            List<Long> skuIdList = couponInfo.getSkuIdList();
            if (CollectionUtils.isEmpty(skuIdList)) {
                return couponInfoSplitAmountMap;
            }
            //优惠券优化总金额
            BigDecimal reduceAmount = couponInfo.getAmount();
            if (skuIdList.size() == 1) {
                //sku的优化金额
                couponInfoSplitAmountMap.put("coupon:" + skuIdToCartInfoMap.get(skuIdList.get(0)).getSkuId(), reduceAmount);
            } else {
                //总金额
                BigDecimal originalTotalAmount = new BigDecimal(0);
                for (Long skuId : skuIdList) {
                    CartInfo cartInfo = skuIdToCartInfoMap.get(skuId);
                    BigDecimal skuTotalAmount = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
                    originalTotalAmount = originalTotalAmount.add(skuTotalAmount);
                }
                //记录除最后一项是所有分摊金额， 最后一项=总的 - skuPartReduceAmount
                BigDecimal skuPartReduceAmount = new BigDecimal(0);
                if (couponInfo.getCouponType() == CouponType.CASH || couponInfo.getCouponType() == CouponType.FULL_REDUCTION) {
                    for (int i = 0, len = skuIdList.size(); i < len; i++) {
                        CartInfo cartInfo = skuIdToCartInfoMap.get(skuIdList.get(i));
                        if (i < len - 1) {
                            BigDecimal skuTotalAmount = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
                            //sku分摊金额
                            BigDecimal skuReduceAmount = skuTotalAmount.divide(originalTotalAmount, 2, RoundingMode.HALF_UP).multiply(reduceAmount);
                            couponInfoSplitAmountMap.put("coupon:" + cartInfo.getSkuId(), skuReduceAmount);

                            skuPartReduceAmount = skuPartReduceAmount.add(skuReduceAmount);
                        } else {
                            BigDecimal skuReduceAmount = reduceAmount.subtract(skuPartReduceAmount);
                            couponInfoSplitAmountMap.put("coupon:" + cartInfo.getSkuId(), skuReduceAmount);
                        }
                    }
                }
            }
            couponInfoSplitAmountMap.put("coupon:total", couponInfo.getAmount());


        }
        return couponInfoSplitAmountMap;


    }
}
