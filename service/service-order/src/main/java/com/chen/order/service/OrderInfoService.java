package com.chen.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chen.search.model.order.OrderInfo;
import com.chen.search.vo.order.OrderConfirmVo;
import com.chen.search.vo.order.OrderSubmitVo;
import com.chen.search.vo.order.OrderUserQueryVo;

/**
 * <p>
 * 订单 服务类
 * </p>
 *
 * @author chenwan
 * @since 2023-09-11
 */
public interface OrderInfoService extends IService<OrderInfo> {

    OrderConfirmVo confirmOrder();

    Long submitOrder(OrderSubmitVo orderParamVo);

    OrderInfo getOrderInfoById(Long orderId);

    OrderInfo getorderInfo(String orderNo);

    void updatePayStatus(String orderNo);

    IPage<OrderInfo> getPageModel(Page<OrderInfo> infoPage, OrderUserQueryVo orderUserQueryVo);
}
