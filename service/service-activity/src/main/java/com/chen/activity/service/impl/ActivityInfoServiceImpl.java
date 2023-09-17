package com.chen.activity.service.impl;

import com.chen.activity.entity.ActivityInfo;
import com.chen.activity.mapper.ActivityInfoMapper;
import com.chen.activity.service.ActivityInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 活动表 服务实现类
 * </p>
 *
 * @author chenwan
 * @since 2023-07-23
 */
@Service
public class ActivityInfoServiceImpl extends ServiceImpl<ActivityInfoMapper, ActivityInfo> implements ActivityInfoService {

}
