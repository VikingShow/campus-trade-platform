package com.campus.trade.service.impl;

import com.campus.trade.entity.MeetupLocation;
import com.campus.trade.mapper.MeetupLocationMapper;
import com.campus.trade.service.MeetupLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MeetupLocationServiceImpl implements MeetupLocationService {

    private final MeetupLocationMapper locationMapper;

    @Autowired
    public MeetupLocationServiceImpl(MeetupLocationMapper locationMapper) {
        this.locationMapper = locationMapper;
    }

    @Override
    @Cacheable("meetup_locations")
    public List<MeetupLocation> getAllLocations() {
        return locationMapper.findAll();
    }

    @Override
    @CacheEvict(value = "meetup_locations", allEntries = true)
    public MeetupLocation addLocation(MeetupLocation location) {
        locationMapper.insert(location);
        return location;
    }

    @Override
    @CacheEvict(value = "meetup_locations", allEntries = true)
    public MeetupLocation updateLocation(MeetupLocation location) {
        locationMapper.update(location);
        return location;
    }

    @Override
    @CacheEvict(value = "meetup_locations", allEntries = true)
    public void deleteLocation(Integer id) {
        locationMapper.delete(id);
    }
}