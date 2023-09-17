package com.chen.order.service.impl;

import com.chen.order.entity.OrderInfo;
import com.chen.order.mapper.OrderInfoMapper;
import com.chen.order.service.OrderInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}
