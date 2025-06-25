package com.campus.trade.service;

import com.campus.trade.entity.MeetupLocation;
import java.util.List;

public interface MeetupLocationService {
    List<MeetupLocation> getAllLocations();
    MeetupLocation addLocation(MeetupLocation location);
    MeetupLocation updateLocation(MeetupLocation location);
    void deleteLocation(Integer id);
}