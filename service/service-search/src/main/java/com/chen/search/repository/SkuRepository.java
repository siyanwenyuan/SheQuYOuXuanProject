package com.chen.search.repository;

import com.chen.search.model.search.SkuEs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface SkuRepository extends ElasticsearchRepository<SkuEs, Long> {


    Page<SkuEs> findByOrderByHotScoreDesc(Pageable page);

    Page<SkuEs> findByCategoryIdAndWareId(Long categoryId, Long wareId, Pageable pageable);

    Page<SkuEs> findByKeywordAndCatgoryIdAndWareId(String keyword, Long categoryId, Long wareId, Pageable pageable);
}
