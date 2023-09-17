package com.chen.activity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chen.activity.mapper.ActivityInfoMapper;
import com.chen.activity.mapper.ActivityRuleMapper;
import com.chen.activity.mapper.ActivitySkuMapper;
import com.chen.activity.service.ActivityInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.activity.service.CouponInfoService;
import com.chen.client.product.ProductFeignClient;
import com.chen.search.enums.ActivityType;
import com.chen.search.model.activity.ActivityInfo;
import com.chen.search.model.activity.ActivityRule;
import com.chen.search.model.activity.ActivitySku;
import com.chen.search.model.activity.CouponInfo;
import com.chen.search.model.order.CartInfo;
import com.chen.search.model.product.SkuInfo;
import com.chen.search.vo.activity.ActivityRuleVo;
import com.chen.search.vo.order.CartInfoVo;
import com.chen.search.vo.order.OrderConfirmVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


/**
 * <p>
 * 活动表 服务实现类
 * </p>
 *
 * @author chenwan
 * @since 2023-07-23
 */
@Service
public class ActivityInfoServiceImpl extends ServiceImpl<ActivityInfoMapper, ActivityInfo> implements ActivityInfoService {

    /**
     * 类型参数“E”的推断类型“E”不在其边界内;
     * 应该实现'com.baomidou.mybatisplus.core.metadata.IPage<ActivityInfo>
     *
     * @param pageParam
     * @return
     */

    @Autowired
    private ActivityRuleMapper activityRuleMapper;
    @Autowired
    private ActivitySkuMapper activitySkuMapper;

 /*   @Autowired
    private ProductFeignClient productFeignClient;*/

    @Autowired
    private CouponInfoService couponInfoService;


    @Autowired
    private ProductFeignClient productFeignClient;

    @Override
    public IPage<ActivityInfo> selectPageList(Page<ActivityInfo> pageParam) {
        IPage<ActivityInfo> ListPage = baseMapper.selectPage(pageParam, null);
        List<ActivityInfo> records = ListPage.getRecords();
        records.stream().forEach(item -> {
            item.setActivityTypeString(item.getActivityType().getComment());
        });
        return ListPage;


    }

    @Override
    public Map<String, Object> selectRule(Long id) {
        Map map = new HashMap<>();
        LambdaQueryWrapper<ActivityRule> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ActivityRule::getActivityId, id);
        List<ActivityRule> activityRules = activityRuleMapper.selectList(lambdaQueryWrapper);
        map.put("activityRuleList", activityRules);

        List<ActivitySku> activitySkus = activitySkuMapper.selectList(new LambdaQueryWrapper<ActivitySku>().eq(ActivitySku::getActivityId, id));
        //获取所有的skuid

        List<Long> collects = activitySkus.stream().map(ActivitySku::getActivityId).collect(Collectors.toList());
        //远程调用接口
        List<SkuInfo> skuInfoList = productFeignClient.findSkuInfoList(collects);
        map.put("skuInfoList", skuInfoList);
        return map;


    }

    @Override
    public void saveActivityrule(ActivityRuleVo activityRuleVo) {
        //根据活动id删除rule 和 sku 中的
        activityRuleMapper.delete(new LambdaQueryWrapper<ActivityRule>().eq(ActivityRule::getActivityId, activityRuleVo.getActivityId()));
        //根据id删除sku中的信息
        activitySkuMapper.delete(new LambdaQueryWrapper<ActivitySku>().eq(ActivitySku::getActivityId, activityRuleVo.getActivityId()));
        //获取规则中数据
        List<ActivityRule> activityRuleList = activityRuleVo.getActivityRuleList();

        // 获取type
        ActivityInfo activityInfo = baseMapper.selectById(activityRuleVo.getActivityId());
        ActivityType activityType = activityInfo.getActivityType();

        for (ActivityRule activityRule : activityRuleList) {
            //其中的id 和type没有被封装，这里需要被封装
            activityRule.setActivityId(activityRuleVo.getActivityId());
            activityRule.setActivityType(activityType);
            activityRuleMapper.insert(activityRule);
        }

        //获取规则范围数据
        List<ActivitySku> activitySkuList = activityRuleVo.getActivitySkuList();
        for (ActivitySku activitySku : activitySkuList) {
            activitySku.setActivityId(activityRuleVo.getActivityId());
            activitySkuMapper.insert(activitySku);

        }

    }

    @Override
    public List<SkuInfo> findSkuInfoByKeyword(String keyword) {

        List<SkuInfo> skuInfoList = productFeignClient.findSkuInfoByKeyword(keyword);

        if (skuInfoList.size() == 0) {
            return skuInfoList;

        }


        //从skuIndoList中获取所有的skuid
        List<Long> collect = skuInfoList.stream().map(SkuInfo::getId).collect(Collectors.toList());
        //得到了商品的信息，如果商品已经在活动表中则不需要进行活动
        //从activity_info 和activity_sku中查询，进行判定
        List<Long> SkuIdListExist = baseMapper.selectSkuIdListExist(collect);
        //进行逻辑判断
        List<SkuInfo> skuInfoList1 = new ArrayList<>();
        for (SkuInfo skuInfo :
                skuInfoList) {
            if (!SkuIdListExist.contains(skuInfo.getId())) {
                skuInfoList1.add(skuInfo);
            }
        }


        return skuInfoList1;
// cannot access User
    }

    /**
     * file does not contain class Userf
     *
     * @param skuIdList Please remove or make sure it appears in the correct subdirectory of the sourcepath
     * @return
     */

    @Override
    public Map<Long, List<String>> findActivity(List<Long> skuIdList) {

        Map<Long, List<String>> result = new HashMap<>();


        //先将skuIdList进行遍历，得到每个具体的id

        skuIdList.forEach(skuId -> {

            List<ActivityRule> activityRuleList = baseMapper.findActicityRule(skuId);

            //此处进行数据的封装,封装规则名称
            if (!CollectionUtils.isEmpty(activityRuleList)) {
                for (ActivityRule activityRule : activityRuleList
                ) {
                    activityRule.setRuleDesc(this.getRuleDesc(activityRule));

                }
                List<String> ruleList = activityRuleList.stream().map(activityRule -> activityRule.getRuleDesc()).collect(Collectors.toList());
                result.put(skuId, ruleList);
            }

        });
        //通过id进行查询，此处牵涉到多张表的查询


        //数据封装
        return result;


    }

    @Override
    public Map<String, Object> findActivityAndCoupon(Long skuId, Long userId) {

        //根据skuId得到优惠活动的规则
        Map<String, Object> stringObjectMap = this.selectRule(skuId);
        //根据skuId和userId查询优惠卷信息
        List<CouponInfo> couponInfoList = couponInfoService.findCouponInfoList(skuId, userId);

        Map<String, Object> map = new HashMap<>();
        //将数据封装到map集合中

        //其中put存放单个集合
        map.put("couponInfoList", couponInfoList);
        //其中putAll存放的数据结果是map集合
        map.putAll(stringObjectMap);

        return map;
    }


    /**
     * 查询购物车优惠卷列表信息
     *
     * @param cartInfoVos
     * @param userId
     * @return
     */
    @Override
    public OrderConfirmVo findCartActivityAndCoupon(List<CartInfoVo> cartInfoVos, Long userId) {
        this.findCartActivityList(cartInfoVos);
        return null;
    }


    //获取购物车对应的规则数据
    @Override
    public List<CartInfoVo> findCartActivityList(List<CartInfoVo> cartInfoList) {

        //得到所有的skuId
        List<CartInfoVo> cartInfoVos = new ArrayList<>();
        List<Long> cartSkuId = cartInfoVos.stream().map(CartInfoVo::getSkuId).collect(Collectors.toList());
        //根据skuIdList获取到参与活动
        List<ActivitySku> activitySkus = baseMapper.selectCartActivity(cartSkuId);
        //根据活动进行分组，每个活动里面都有那些skuId信息
        //其中转换为set集合的原因是：其中的skuId不能重复，set集合封装原则是不可重复
        Map<Long, Set<Long>> activityIdToSkuMap = activitySkus.stream().
                collect(Collectors.groupingBy(ActivitySku::getActivityId, Collectors.
                        mapping(ActivitySku::getSkuId, Collectors.toSet()
                        )));

        //获取活动中的规则的数据
        //key是活动id value是每个活动中对应的规则信息
        Map<Long, List<ActivityRule>> mapRule = new HashMap<>();
        //首先得到所有的活动id
        Set<Long> acticitySkuIdSet = activitySkus.stream().map(ActivitySku::getActivityId).collect(Collectors.toSet());
        if (!StringUtils.isEmpty(acticitySkuIdSet)) {
            LambdaQueryWrapper<ActivityRule> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            //设置其中的条件
            //设置一个排序条件
            lambdaQueryWrapper.orderByDesc(ActivityRule::getConditionAmount, ActivityRule::getConditionNum);
            //设置查询条件
            lambdaQueryWrapper.in(ActivityRule::getActivityId, acticitySkuIdSet);
            List<ActivityRule> activityRuleList = activityRuleMapper.selectList(lambdaQueryWrapper);


            //先根据活动id进行分组
            mapRule = activityRuleList.stream().collect(Collectors.groupingBy(activityRule -> activityRule.getActivityId()));

            //最后将数据封装到集合中去

            //第一组封装到有活动中去
            Set<Long> activitySkuIdSet = new HashSet<>();
            if (!StringUtils.isEmpty(mapRule)) {
                //遍历集合中的元素 这里使用迭代器
                //使用迭代器，访问其中的键值对
                Iterator<Map.Entry<Long, List<ActivityRule>>> iterator = mapRule.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Long, List<ActivityRule>> entry = iterator.next();
                    Long activityId = entry.getKey();
                    List<ActivityRule> activityRules = entry.getValue();
                    //
                    List<CartInfoVo> collect = cartInfoList.stream().
                            filter(cartInfoVo -> activityRules.contains(cartInfoVo.getSkuId()))
                            .collect(Collectors.toList());
                    //计算总金额和数量
                    BigDecimal bigDecimal = this.computeToTalAmount(collect);
                    //得到总数量
                    int number = this.computeNum(collect);
                    //得到当前id的活动规则
                    List<ActivityRule> currentActivityRule = mapRule.get(activityId);
                    ActivityType activityType = currentActivityRule.get(0).getActivityType();

                    ActivityRule activityRule = null;
                    if (activityType == ActivityType.FULL_REDUCTION) {
                        //满减
                        activityRule = this.computeReduction(bigDecimal, currentActivityRule);
                    } else {
                        //满量
                        activityRule = this.computeRe(number, currentActivityRule);
                    }
                    //封装数据到cartinfvo中去
                    CartInfoVo cartInfoVo = new CartInfoVo();
                    cartInfoVo.setActivityRule(activityRule);
                    cartInfoList.add(cartInfoVo);
                }

            }


        }

  /*      //获取没有参加活动的商品信息
       if(!StringUtils.isEmpty(acticitySkuIdSet)){
           Map<Long,CartInfoVo> collect = acticitySkuIdSet.stream().collect(Collectors.toMap(CartInfoVo::getSkuId, CartInfoVo -> CartInfoVo));

       }*/


        return cartInfoList;
    }

    private ActivityRule computeRe(int number, List<ActivityRule> currentActivityRule) {
        return new ActivityRule();

    }

    private ActivityRule computeReduction(BigDecimal bigDecimal, List<ActivityRule> currentActivityRule) {
        return new ActivityRule();

    }

    private int computeNum(List<CartInfoVo> collect) {
        return 0;

    }

    private BigDecimal computeToTalAmount(List<CartInfoVo> collect) {

        return new BigDecimal(1);

    }

    private String getRuleDesc(ActivityRule activityRule) {
        ActivityType activityType = activityRule.getActivityType();
        StringBuffer ruleDesc = new StringBuffer();
        if (activityType == ActivityType.FULL_REDUCTION) {
            ruleDesc
                    .append("满")
                    .append(activityRule.getConditionAmount())
                    .append("元减")
                    .append(activityRule.getBenefitAmount())
                    .append("元");
        } else {
            ruleDesc
                    .append("满")
                    .append(activityRule.getConditionNum())
                    .append("元打")
                    .append(activityRule.getBenefitDiscount())
                    .append("折");
        }
        return ruleDesc.toString();
    }

}
