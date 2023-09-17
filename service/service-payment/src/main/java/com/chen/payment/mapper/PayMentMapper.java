package com.chen.payment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chen.search.model.order.PaymentInfo;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface PayMentMapper extends BaseMapper<PaymentInfo> {
}
