package com.chen.activity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chen.activity.mapper.CouponInfoMapper;
import com.chen.activity.mapper.CouponRangeMapper;
import com.chen.activity.service.CouponInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.client.product.ProductFeignClient;
import com.chen.search.enums.CouponRangeType;
import com.chen.search.model.activity.CouponInfo;
import com.chen.search.model.activity.CouponRange;
import com.chen.search.model.order.CartInfo;
import com.chen.search.model.product.Category;
import com.chen.search.model.product.SkuInfo;
import com.chen.search.vo.activity.CouponRuleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 优惠券信息 服务实现类
 * </p>
 *
 * @author chenwan
 * @since 2023-07-23
 */
@Service
public class CouponInfoServiceImpl extends ServiceImpl<CouponInfoMapper, CouponInfo> implements CouponInfoService {



    @Autowired
    private ProductFeignClient productFeignClient;
    @Autowired
    private CouponRangeMapper couponRangeMapper;
    @Override
    public IPage<CouponInfo> selectPageLimit(Page<CouponInfo> couponInfoPage) {
        Page<CouponInfo> couponInfoPageListAll= baseMapper.selectPage(couponInfoPage, null);
        List<CouponInfo> records = couponInfoPageListAll.getRecords();
        records.stream().forEach(item->{
            item.setCouponTypeString(item.getCouponType().getComment());
            CouponRangeType rangeType = item.getRangeType();
            if(rangeType!=null){
                item.setRangeTypeString(rangeType.getComment());
            }
        });
        return couponInfoPageListAll;
    }

    @Override
    public Map<String, Object> selectList(Long id) {

        //根据id查询coupon_info
        CouponInfo couponInfo = baseMapper.selectById(id);
        //根据id查询coupon_range
        Map<String,Object> map=new HashMap<>();
        List<CouponRange> couponRanges = couponRangeMapper.selectList(new LambdaQueryWrapper<CouponRange>().eq(CouponRange::getCouponId, id));
        List<Long> collect = couponRanges.stream().map(CouponRange::getRangeId).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(collect)){
            if(couponInfo.getRangeType()==CouponRangeType.SKU){
                List<SkuInfo> skuInfoList = productFeignClient.findSkuInfoList(collect);

                map.put("skuInfoList",skuInfoList);
            }else if(couponInfo.getRangeType()==CouponRangeType.CATEGORY){

               List<Category> categoryList= productFeignClient.findCategortList(collect);
               map.put("categoryList",categoryList);
            }
        }

        return map;

    }

    @Override
    public void saveCouponRule(CouponRuleVo couponRuleVo) {
        //首先删除最开始的旧规则
        couponRangeMapper.delete(new LambdaQueryWrapper<CouponRange>().eq(CouponRange::getCouponId,couponRuleVo.getCouponId()));
        //更新数据
        //先将基本信息查询
        CouponInfo couponInfo = baseMapper.selectById(couponRuleVo.getCouponId());
        //将数据进行更新
        couponInfo.setRangeType(couponRuleVo.getRangeType());
        couponInfo.setConditionAmount(couponRuleVo.getConditionAmount());
        couponInfo.setAmount(couponRuleVo.getAmount());
        couponInfo.setConditionAmount(couponRuleVo.getConditionAmount());
        couponInfo.setRangeDesc(couponRuleVo.getRangeDesc());

        baseMapper.updateById(couponInfo);

        //将新数据插入
        List<CouponRange> couponRangeList = couponRuleVo.getCouponRangeList();
        for (CouponRange couponRange: couponRangeList
             ) {
            couponRange.setRangeId(couponRuleVo.getCouponId());
            couponRangeMapper.insert(couponRange);
        }
    }

    @Override
    public List<CouponInfo> findCouponInfoList(Long skuId, Long userId) {
        //首先是通过skuId查询对应SkuInfo
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        //再通过skuId+分类id+userId查询对应的规则信息
     List<CouponInfo> couponInfoList=   baseMapper.selectCouponInfoList(skuId,skuInfo.getCategoryId(),userId);

        return couponInfoList;
    }

    @Override
    public CouponInfo findRangeSkuIdList(List<CartInfo> cartInfoList, Long couponId) {

        CouponInfo couponInfo = baseMapper.selectById(couponId);
        if(couponInfo==null)
        {
            return null;

        }
        List<CouponRange> couponRangeList = couponRangeMapper.selectList(new LambdaQueryWrapper<CouponRange>().
                eq(CouponRange::getCouponId, couponId));
       Map<Long,List<Long>> map=this.findIdSkuMapList(couponInfo,couponRangeList);


        List<Long> mapList = map.entrySet().iterator().next().getValue();

        couponInfo.setSkuIdList(mapList);

        return couponInfo;

    }

    private Map<Long, List<Long>> findIdSkuMapList(CouponInfo couponInfo, List<CouponRange> couponRangeList) {
        return null;

    }
}
