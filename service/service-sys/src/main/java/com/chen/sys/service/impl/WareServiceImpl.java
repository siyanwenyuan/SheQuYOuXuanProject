package com.chen.sys.service.impl;

import com.chen.search.model.sys.Ware;
import com.chen.sys.mapper.WareMapper;
import com.chen.sys.service.WareService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 仓库表 服务实现类
 * </p>
 *
 * @author chenwan
 * @since 2023-07-09
 */
@Service
public class WareServiceImpl extends ServiceImpl<WareMapper, Ware> implements WareService {

    @Override
    public List<Ware> selectAll() {
        List<Ware> wares = baseMapper.selectList(null);
        return wares;
    }
}
