package com.chen.sys.service;

import com.chen.search.model.sys.Ware;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 仓库表 服务类
 * </p>
 *
 * @author chenwan
 * @since 2023-07-09
 */
public interface WareService extends IService<Ware> {

    List<Ware> selectAll();
}
