package com.campus.trade.service.impl;

import com.campus.trade.entity.MeetupLocation;
import com.campus.trade.mapper.MeetupLocationMapper;
import com.campus.trade.service.MeetupLocationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MeetupLocationServiceImpl implements MeetupLocationService {
    private static final Logger log = LoggerFactory.getLogger(MeetupLocationServiceImpl.class);

    private final MeetupLocationMapper locationMapper;

    @Autowired
    public MeetupLocationServiceImpl(MeetupLocationMapper locationMapper) {
        this.locationMapper = locationMapper;
    }

    /**
     * @Cacheable: 查询操作，优先从缓存获取。
     */
    @Override
    @Cacheable("meetup_locations")
    public List<MeetupLocation> getAllLocations() {
        log.info(">>> [缓存未命中] 正在从数据库查询交易地点列表...");
        return locationMapper.findAll();
    }

    /**
     * 【最终修正】
     * @CacheEvict: 在每次新增、更新或删除数据后，我们都必须清除缓存。
     * allEntries = true: 表示将名为 "meetup_locations" 的缓存中的所有条目全部清除。
     */
    @Override
    @CacheEvict(value = "meetup_locations", allEntries = true)
    public MeetupLocation addLocation(MeetupLocation location) {
        log.info(">>> [新增地点] 清除 'meetup_locations' 缓存。");
        locationMapper.insert(location);
        return location;
    }

    @Override
    @CacheEvict(value = "meetup_locations", allEntries = true)
    public MeetupLocation updateLocation(MeetupLocation location) {
        log.info(">>> [更新地点] 清除 'meetup_locations' 缓存。");
        locationMapper.update(location);
        return location;
    }

    @Override
    @CacheEvict(value = "meetup_locations", allEntries = true)
    public void deleteLocation(Integer id) {
        log.info(">>> [删除地点] 清除 'meetup_locations' 缓存。");
        locationMapper.delete(id);
    }
}