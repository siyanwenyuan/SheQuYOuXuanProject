package com.chen.order.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chen.order.service.OrderInfoService;
import com.chen.search.common.auth.AuthContextHolder;
import com.chen.search.common.result.Result;
import com.chen.search.model.order.OrderInfo;
import com.chen.search.vo.order.OrderConfirmVo;
import com.chen.search.vo.order.OrderSubmitVo;
import com.chen.search.vo.order.OrderUserQueryVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 订单 前端控制器
 * </p>
 *
 * @author chenwan
 * @since 2023-09-11
 */
@RestController
@RequestMapping("api/order")
public class OrderInfoController {

    @Autowired
    private OrderInfoService orderInfoService;


    @ApiOperation("确认订单")
    @GetMapping("auth/confirmOrder")
    public Result confirm() {
        OrderConfirmVo orderConfirmVo = orderInfoService.confirmOrder();
        return Result.ok(orderConfirmVo);
    }


    @ApiOperation("生成订单")
    @PostMapping("auth/submitOrder")
    public Result submitOrder(@RequestBody OrderSubmitVo orderParamVo) {
        // 获取到用户Id
        Long userId = AuthContextHolder.getUserId();
        Long orderId = orderInfoService.submitOrder(orderParamVo);
        return Result.ok(orderId);
    }

    @ApiOperation("获取订单详情")
    @GetMapping("auth/getOrderInfoById/{orderId}")
    public Result getOrderInfoById(@PathVariable("orderId") Long orderId) {

        OrderInfo orderInfo = orderInfoService.getOrderInfoById(orderId);

        return Result.ok(orderInfo);
    }


    @GetMapping("getorderInfo/{orderNo}")
    public OrderInfo getorderInfo(@PathVariable String orderNo)
    {
       OrderInfo orderInfo= orderInfoService.getorderInfo(orderNo);
        return orderInfo;
    }


    @ApiOperation(value = "获取用户订单分页列表")
    @GetMapping("auth/findUserOrderPage/{page}/{limit}")
    public Result findUserOrderPage(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Long page,

            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Long limit,

            @ApiParam(name = "orderVo", value = "查询对象", required = false)
            OrderUserQueryVo orderUserQueryVo){
        Long userId = AuthContextHolder.getUserId();
        orderUserQueryVo.setUserId(userId);
        Page<OrderInfo> infoPage=new Page<>(page,limit);
      IPage<OrderInfo> pageModel= orderInfoService.getPageModel(infoPage,orderUserQueryVo);
      return Result.ok(pageModel);

    }

}

