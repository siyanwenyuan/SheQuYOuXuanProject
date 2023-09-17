package com.chen.search.service;

import com.chen.search.model.search.SkuEs;
import com.chen.search.vo.search.SkuEsQueryVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SkuService {
    void upper(Long skuId);

    void low(Long skuId);

    List<SkuEs> findHotSkuList();

    Page<SkuEs> search(Pageable pageModel, SkuEsQueryVo skuEsQueryVo);

    void incrHotScore(Long skuId);
}
