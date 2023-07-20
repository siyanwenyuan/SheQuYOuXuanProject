package com.chen.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chen.search.model.sys.Region;
import com.chen.sys.mapper.RegionMapper;
import com.chen.sys.service.RegionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 地区表 服务实现类
 * </p>
 *
 * @author chenwan
 * @since 2023-07-09
 */
@Service
public class RegionServiceImpl extends ServiceImpl<RegionMapper, Region> implements RegionService {


    @Override
    public List<Region> findByWord(String keyword) {

        LambdaQueryWrapper<Region> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(Region::getName,keyword);
        List<Region> region = baseMapper.selectList(lambdaQueryWrapper);
        return region;
    }
}
