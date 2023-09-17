package com.chen.activity.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chen.search.model.activity.CouponInfo;
import com.chen.search.model.order.CartInfo;
import com.chen.search.vo.activity.CouponRuleVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 优惠券信息 服务类
 * </p>
 *
 * @author chenwan
 * @since 2023-07-23
 */
public interface CouponInfoService extends IService<CouponInfo> {

    IPage<CouponInfo> selectPageLimit(Page<CouponInfo> couponInfoPage);

    Map<String, Object> selectList(Long id);

    void saveCouponRule(CouponRuleVo couponRuleVo);

    List<CouponInfo> findCouponInfoList(Long skuId, Long userId);

    CouponInfo findRangeSkuIdList(List<CartInfo> cartInfoList, Long couponId);
}
