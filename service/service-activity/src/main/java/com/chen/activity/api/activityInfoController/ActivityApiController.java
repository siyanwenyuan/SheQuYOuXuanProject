package com.chen.activity.api.activityInfoController;


import com.chen.activity.service.ActivityInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/activity")
public class ActivityApiController {

    @Autowired
    private ActivityInfoService activityInfoService;
    @PostMapping("inner/findActivity")
 public Map<Long, List<String>> findActivity(@RequestBody List<Long> skuIdList){
    Map<Long,List<String>> mapList= activityInfoService.findActivity(skuIdList);
    return mapList;

    }

    @GetMapping("inner/findActivityAndCoupon/{skuId}/{userId}")
    public Map<String ,Object> findActivityAndCoupon(@PathVariable Long skuId,@PathVariable
                                                    Long userId){
      return   activityInfoService.findActivityAndCoupon(skuId,userId);
    }
}
