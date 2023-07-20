package com.chen.sys.service;

import com.chen.search.model.sys.Region;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 地区表 服务类
 * </p>
 *
 * @author chenwan
 * @since 2023-07-09
 */
public interface RegionService extends IService<Region> {

    List<Region> findByWord(String keyword);
}
