package com.chen.activity.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chen.search.model.activity.ActivityInfo;
import com.chen.search.model.order.CartInfo;
import com.chen.search.model.product.SkuInfo;
import com.chen.search.vo.activity.ActivityRuleVo;
import com.chen.search.vo.order.CartInfoVo;
import com.chen.search.vo.order.OrderConfirmVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 活动表 服务类
 * </p>
 *
 * @author chenwan
 * @since 2023-07-23
 */
public interface ActivityInfoService extends IService<ActivityInfo> {

    IPage<ActivityInfo> selectPageList(Page<ActivityInfo> pageParam);

    Map<String,Object> selectRule(Long id);

    void saveActivityrule(ActivityRuleVo activityRuleVo);

    List<SkuInfo> findSkuInfoByKeyword(String keyword);

    Map<Long, List<String>> findActivity(List<Long> skuIdList);

    Map<String, Object> findActivityAndCoupon(Long skuId, Long userId);

    OrderConfirmVo findCartActivityAndCoupon(List<CartInfoVo> cartInfoVos, Long userId);

    List<CartInfoVo> findCartActivityList(List<CartInfoVo> cartInfoList);

}
