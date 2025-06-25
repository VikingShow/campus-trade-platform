package com.campus.trade.mapper;

import com.campus.trade.entity.MeetupLocation;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface MeetupLocationMapper {
    List<MeetupLocation> findAll();
    MeetupLocation findById(Integer id);
    void insert(MeetupLocation location);
    void update(MeetupLocation location);
    void delete(Integer id);
}