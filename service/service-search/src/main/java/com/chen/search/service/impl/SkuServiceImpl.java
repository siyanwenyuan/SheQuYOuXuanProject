package com.chen.search.service.impl;

import com.chen.client.product.ProductFeignClient;
import com.chen.search.common.auth.AuthContextHolder;
import com.chen.search.enums.SkuType;
import com.chen.search.model.product.Category;
import com.chen.search.model.product.SkuInfo;
import com.chen.search.model.search.SkuEs;
import com.chen.search.repository.SkuRepository;
import com.chen.search.service.SkuService;
import com.chen.search.vo.search.SkuEsQueryVo;
import com.chen.ssyx.activity.client.ActivityFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    private SkuRepository skuRepository;

    @Autowired
    private RedisTemplate redisTemplate;


    @Qualifier(value = "activityFeignClient")
    private ActivityFeignClient activityFeignClient;

    @Qualifier(value = "productFeignClient")
    private ProductFeignClient productFeignClient;

    @Override
    public void upper(Long skuId) {

        //先得到商品的分类信息和商品信息
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        if (skuInfo == null) {
            return;
        }
        Category category = productFeignClient.getCategoryId(skuInfo.getCategoryId());
        if (category == null) {
            return;
        }
        //对这两个信息进行封装
        SkuEs skuEs = new SkuEs();
        skuEs.setCategoryId(category.getId());
        skuEs.setCategoryName(category.getName());

        skuEs.setId(skuInfo.getId());
        skuEs.setKeyword(skuInfo.getSkuName() + "," + skuEs.getCategoryName());
        skuEs.setWareId(skuInfo.getWareId());
        skuEs.setIsNewPerson(skuInfo.getIsNewPerson());
        skuEs.setImgUrl(skuInfo.getImgUrl());
        skuEs.setTitle(skuInfo.getSkuName());
        if (skuInfo.getSkuType() == SkuType.COMMON.getCode()) {
            skuEs.setSkuType(0);
            skuEs.setPrice(skuInfo.getPrice().doubleValue());
            skuEs.setStock(skuInfo.getStock());
            skuEs.setSale(skuInfo.getSale());
            skuEs.setPerLimit(skuInfo.getPerLimit());
        }

        //添加到ES中去
        skuRepository.save(skuEs);


    }


    /**
     * 下架也就是从es中删除数据 直接根据id删除即可
     *
     * @param skuId
     */
    @Override
    public void low(Long skuId) {
        skuRepository.deleteById(skuId);
    }

    @Override
    public List<SkuEs> findHotSkuList() {


        //利用es进行分页查询，注意此处的第一页不是1 而是0
        Pageable page = PageRequest.of(0, 10);
        Page<SkuEs> pageModel = skuRepository.findByOrderByHotScoreDesc(page);
        //得到数据
        List<SkuEs> content = pageModel.getContent();
        //返回数据
        return content;

    }

    @Override
    public Page<SkuEs> search(Pageable pageable, SkuEsQueryVo skuEsQueryVo) {


        //向skuvo中存入仓库id
        skuEsQueryVo.setWareId(AuthContextHolder.getWareId());
        //通过条件进行查询
        Page<SkuEs> pagemodel = null;
        String keyword = skuEsQueryVo.getKeyword();
        if (StringUtils.isEmpty(keyword)) {
            //如果keyword为空，则通过仓库id 和分类id进行查询
            //通过springdata操作es 满足其中的命名规范
            pagemodel = skuRepository.findByCategoryIdAndWareId(skuEsQueryVo.getCategoryId(),
                    skuEsQueryVo.getWareId(),
                    pageable);
        } else {
            //如果不为空，则通过关键字和仓库id和分类id进行查询
            pagemodel = skuRepository.findByKeywordAndCatgoryIdAndWareId(skuEsQueryVo.getKeyword(),
                    skuEsQueryVo.getCategoryId(),
                    skuEsQueryVo.getWareId(),
                    pageable);


        }

        //查询商品参加的优惠活动
        List<SkuEs> content = pagemodel.getContent();
        //判断是否为空
        if (!CollectionUtils.isEmpty(content)) {
            //如果不为空则对该集合进行遍历，得到其中的id
            List<Long> skuId = content.stream().map(item -> item.getId()).collect(Collectors.toList());
            //根据id进行远程调用，调用skuservice-activity中的接口

            Map<Long, List<String>> skuMapListRule = activityFeignClient.findActivity(skuId);

            //获取数据封装到skues的ruleList中去
            if (skuMapListRule != null) {
                content.forEach(skuEs -> {
                    skuEs.setRuleList(skuMapListRule.get(skuEs.getId()));
                });
            }


        }

        return pagemodel;
    }

    @Override
    public void incrHotScore(Long skuId) {
        String key = "hotScore";
        //使用redis进行热度的增加，其中核心是通过redis中的ZSet数据集合，特点是：有序且不可重复
        //通过redis保存数据，每次加1
        Double hotScore = redisTemplate.opsForZSet().incrementScore(key, "hotValue" + skuId, 1);
        //约定好规则，当满足此规则后，则在es中执行更新操作
        if (hotScore % 10 == 0) {
            Optional<SkuEs> optional = skuRepository.findById(skuId);
            SkuEs skuEs = optional.get();
            skuEs.setHotScore(Math.round(hotScore));
            skuRepository.save(skuEs);

        }


    }


}
