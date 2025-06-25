package com.campus.trade.controller;

import com.campus.trade.common.Result;
import com.campus.trade.entity.MeetupLocation;
import com.campus.trade.service.MeetupLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/admin/locations")
@PreAuthorize("hasRole('ADMIN')")
public class AdminMeetupLocationController {

    private final MeetupLocationService locationService;

    @Autowired
    public AdminMeetupLocationController(MeetupLocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping
    public Result<List<MeetupLocation>> getAllLocations() {
        return Result.success(locationService.getAllLocations());
    }

    @PostMapping
    public Result<MeetupLocation> addLocation(@RequestBody MeetupLocation location) {
        return Result.success(locationService.addLocation(location));
    }

    @PutMapping("/{id}")
    public Result<MeetupLocation> updateLocation(@PathVariable Integer id, @RequestBody MeetupLocation location) {
        location.setId(id);
        return Result.success(locationService.updateLocation(location));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteLocation(@PathVariable Integer id) {
        locationService.deleteLocation(id);
        return Result.success();
    }
}