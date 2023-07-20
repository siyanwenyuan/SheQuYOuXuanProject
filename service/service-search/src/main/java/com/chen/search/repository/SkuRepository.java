package com.chen.search.repository;

import com.chen.search.model.search.SkuEs;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SkuRepository extends ElasticsearchRepository<SkuEs, Long> {

}
