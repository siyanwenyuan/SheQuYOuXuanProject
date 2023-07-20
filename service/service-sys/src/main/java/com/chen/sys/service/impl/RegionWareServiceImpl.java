package com.chen.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chen.search.common.exception.SsyxException;
import com.chen.search.common.result.ResultCodeEnum;
import com.chen.search.model.sys.RegionWare;
import com.chen.search.vo.sys.RegionWareQueryVo;
import com.chen.sys.mapper.RegionWareMapper;
import com.chen.sys.service.RegionWareService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 城市仓库关联表 服务实现类
 * </p>
 *
 * @author chenwan
 * @since 2023-07-09
 */
@Service
public class RegionWareServiceImpl extends ServiceImpl<RegionWareMapper, RegionWare> implements RegionWareService {

    @Override
    public IPage<RegionWare> selectPageRegionWare(Page<RegionWare> pageParam, RegionWareQueryVo regionWareQueryVo) {
        //首先获取条件值
        String keyword = regionWareQueryVo.getKeyword();
        //对条件值进行判空，如果不为空，则封装条件值
        LambdaQueryWrapper<RegionWare> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(keyword)) {
            //如果不为空，则进行封装，
            //注意这里是两个区域的
            lambdaQueryWrapper.like(RegionWare::getRegionName, keyword).
                    or().like(RegionWare::getWareName, keyword);
        }
        IPage<RegionWare> regionWarePage = baseMapper.selectPage(pageParam, lambdaQueryWrapper);
        return regionWarePage;
    }

    @Override
    public void saveRegionWare(RegionWare regionWare) {
        //判断该区域是否已经开通
        LambdaQueryWrapper<RegionWare> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(RegionWare::getRegionId, regionWare.getRegionId());
        Integer count = baseMapper.selectCount(lambdaQueryWrapper);

        if (count > 0) {
            //说明通过区域的id已经查询到了说明已经存在
            //则抛出异常 此时使用自定义异常
            throw new SsyxException(ResultCodeEnum.REGION_OPEN);
        }
        //添加
        baseMapper.insert(regionWare);
    }

    @Override
    public void updateStatus(Long id, Integer status) {

        RegionWare regionWare = baseMapper.selectById(id);
       regionWare.setStatus(status);
       baseMapper.updateById(regionWare);
    }
}
