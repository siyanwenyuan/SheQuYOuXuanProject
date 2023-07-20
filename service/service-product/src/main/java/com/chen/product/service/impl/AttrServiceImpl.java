package com.chen.product.service.impl;

import com.chen.product.mapper.AttrMapper;
import com.chen.product.service.AttrService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.search.model.product.Attr;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品属性 服务实现类
 * </p>
 *
 * @author chenwan
 * @since 2023-07-11
 */
@Service
public class AttrServiceImpl extends ServiceImpl<AttrMapper, Attr> implements AttrService {

}
