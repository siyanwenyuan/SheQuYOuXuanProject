package com.chen.client.search;


import com.chen.search.model.search.SkuEs;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient("service-search")
public interface SkuFeignClient {


    @GetMapping("api/search/sku/inner/findHotSkuList")
    public List<SkuEs> findHotSkuList();
    @GetMapping("api/search/sku/inner/incrHotScore/{skuId}")
    public Boolean incrHotScore(@PathVariable Long skuId);


}
