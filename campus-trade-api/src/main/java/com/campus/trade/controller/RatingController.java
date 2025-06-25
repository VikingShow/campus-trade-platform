package com.campus.trade.controller;

import com.campus.trade.common.Result;
import com.campus.trade.dto.RatingDTO;
import com.campus.trade.entity.Rating;
import com.campus.trade.security.AuthenticatedUser;
import com.campus.trade.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class RatingController {

    private final RatingService ratingService;

    @Autowired
    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping("/orders/{orderId}/ratings")
    @PreAuthorize("isAuthenticated()")
    public Result<Rating> submitRating(@PathVariable String orderId, @RequestBody RatingDTO ratingDTO, @AuthenticationPrincipal AuthenticatedUser user) {
        Rating rating = ratingService.submitRating(orderId, user.getUserId(), ratingDTO);
        return Result.success(rating);
    }

    @GetMapping("/users/{userId}/ratings")
    public Result<List<Rating>> getUserRatings(@PathVariable String userId) {
        return Result.success(ratingService.getRatingsForUser(userId));
    }
}
