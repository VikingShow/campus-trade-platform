package com.campus.trade.service;

import com.campus.trade.dto.RatingDTO;
import com.campus.trade.entity.Rating;
import java.util.List;

public interface RatingService {
    Rating submitRating(String orderId, String raterId, RatingDTO ratingDTO);
    List<Rating> getRatingsForUser(String userId);
}