package com.campus.trade.controller;

import com.campus.trade.common.Result;
import com.campus.trade.entity.MeetupLocation;
import com.campus.trade.service.MeetupLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * 一个公开的控制器，用于让所有用户获取交易地点列表
 */
@RestController
@RequestMapping("/locations") // 注意：路径没有 /api 或 /admin 前缀
public class MeetupLocationController {

    private final MeetupLocationService locationService;

    @Autowired
    public MeetupLocationController(MeetupLocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping
    public Result<List<MeetupLocation>> getAllLocations() {
        // 这个方法会利用我们之前设置好的缓存
        return Result.success(locationService.getAllLocations());
    }
}