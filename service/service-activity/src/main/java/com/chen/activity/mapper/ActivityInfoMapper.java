package com.chen.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chen.search.model.activity.ActivityInfo;
import com.chen.search.model.activity.ActivityRule;
import com.chen.search.model.activity.ActivitySku;
import com.chen.search.model.activity.CouponInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 活动表 Mapper 接口
 * </p>
 *
 * @author chenwan
 * @since 2023-07-23
 */

@Repository
public interface ActivityInfoMapper extends BaseMapper<ActivityInfo> {

    List<Long> selectSkuIdListExist(@Param("skuInfoList") List<Long> collect);

    List<ActivityRule> findActicityRule(@Param("skuId") Long skuId);

    List<CouponInfo> selectCouponInfoList(@Param("skuId") Long skuId, @Param("categoryId")Long categoryId, @Param("userId") Long userId);

    List<ActivitySku> selectCartActivity(@Param("cartSkuId") List<Long> cartSkuId);
}
