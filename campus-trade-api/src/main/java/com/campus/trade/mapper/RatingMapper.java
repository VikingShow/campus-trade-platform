package com.campus.trade.mapper;

import com.campus.trade.entity.Rating;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface RatingMapper {
    void insert(Rating rating);
    Rating findByOrderAndRater(@Param("orderId") String orderId, @Param("raterId") String raterId);
    List<Rating> findByRateeId(String rateeId);
}
