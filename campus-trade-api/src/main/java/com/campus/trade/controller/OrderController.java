package com.campus.trade.controller;

import com.campus.trade.common.Result;
import com.campus.trade.dto.CreateOrderDTO;
import com.campus.trade.entity.Order;
import com.campus.trade.exception.CustomException;
import com.campus.trade.security.AuthenticatedUser;
import com.campus.trade.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    private String getUserId(AuthenticatedUser user) {
        if (user == null) throw new CustomException("用户未登录");
        return user.getUserId();
    }

    @PostMapping
    public Result<Order> createOrder(@RequestBody CreateOrderDTO createOrderDTO, @AuthenticationPrincipal AuthenticatedUser user) {
        Order order = orderService.createOrder(getUserId(user), createOrderDTO);
        return Result.success(order);
    }

    @GetMapping("/{orderId}")
    public Result<Order> getOrderById(@PathVariable String orderId) {
        Order order = orderService.getOrderDetails(orderId);
        return Result.success(order);
    }

    @GetMapping("/my-purchases")
    public Result<List<Order>> getMyPurchases(@AuthenticationPrincipal AuthenticatedUser user) {
        List<Order> orders = orderService.getMyPurchases(getUserId(user));
        return Result.success(orders);
    }

    @GetMapping("/my-sales")
    public Result<List<Order>> getMySales(@AuthenticationPrincipal AuthenticatedUser user) {
        List<Order> orders = orderService.getMySales(getUserId(user));
        return Result.success(orders);
    }

    @PutMapping("/{orderId}/status")
    public Result<Order> updateStatus(@PathVariable String orderId, @RequestBody Map<String, String> payload, @AuthenticationPrincipal AuthenticatedUser user){
        String status = payload.get("status");
        Order updatedOrder = orderService.updateOrderStatus(orderId, getUserId(user), status);
        return Result.success(updatedOrder);
    }
}