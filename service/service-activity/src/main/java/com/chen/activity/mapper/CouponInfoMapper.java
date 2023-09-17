package com.chen.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chen.search.model.activity.CouponInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 优惠券信息 Mapper 接口
 * </p>
 *
 * @author chenwan
 * @since 2023-07-23
 */
public interface CouponInfoMapper extends BaseMapper<CouponInfo> {


    List<CouponInfo> selectCouponInfoList(@Param("skuId") Long skuId,@Param("categoryId") Long categoryId,@Param("userId") Long userId);
}
