package com.campus.trade.service.impl;

import com.campus.trade.dto.RatingDTO;
import com.campus.trade.entity.Order;
import com.campus.trade.entity.Rating;
import com.campus.trade.exception.CustomException;
import com.campus.trade.mapper.OrderMapper;
import com.campus.trade.mapper.RatingMapper;
import com.campus.trade.mapper.UserMapper;
import com.campus.trade.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class RatingServiceImpl implements RatingService {

    private final RatingMapper ratingMapper;
    private final OrderMapper orderMapper;
    private final UserMapper userMapper;

    @Autowired
    public RatingServiceImpl(RatingMapper ratingMapper, OrderMapper orderMapper, UserMapper userMapper) {
        this.ratingMapper = ratingMapper;
        this.orderMapper = orderMapper;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public Rating submitRating(String orderId, String raterId, RatingDTO ratingDTO) {
        Order order = orderMapper.findOrderById(orderId);
        if (order == null) {
            throw new CustomException("订单不存在");
        }
        if (!"COMPLETED".equals(order.getOrderStatus())) {
            throw new CustomException("只有已完成的订单才能评价");
        }
        if (!Objects.equals(order.getBuyerId(), raterId) && !Objects.equals(order.getSellerId(), raterId)) {
            throw new CustomException("无权评价此订单");
        }
        if (ratingMapper.findByOrderAndRater(orderId, raterId) != null) {
            throw new CustomException("您已评价过此订单");
        }

        String rateeId = Objects.equals(order.getBuyerId(), raterId) ? order.getSellerId() : order.getBuyerId();

        Rating rating = new Rating();
        rating.setOrderId(orderId);
        rating.setRaterId(raterId);
        rating.setRateeId(rateeId);
        rating.setScore(ratingDTO.getScore());
        rating.setComment(ratingDTO.getComment());

        ratingMapper.insert(rating);

        // 更新信誉分
        int scoreChange = 0;
        if (ratingDTO.getScore() >= 4) {
            scoreChange = 1; // 好评+1
        } else if (ratingDTO.getScore() <= 2) {
            scoreChange = -2; // 差评-2
        }
        if(scoreChange != 0){
            userMapper.updateCreditScore(rateeId, scoreChange);
        }

        return rating;
    }

    @Override
    public List<Rating> getRatingsForUser(String userId) {
        return ratingMapper.findByRateeId(userId);
    }
}