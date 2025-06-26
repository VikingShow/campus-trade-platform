package com.campus.trade.controller;

import com.campus.trade.common.Result;
import com.campus.trade.dto.PageResult; // 【新增】
import com.campus.trade.entity.Order;
import com.campus.trade.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/orders")
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderController {

    private final OrderService orderService;

    @Autowired
    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // 【修改】获取所有订单列表的接口，增加分页参数
    @GetMapping
    public Result<PageResult<Order>> getAllOrders(
            @RequestParam(required = false) String orderId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(orderService.findAllOrdersForAdmin(orderId, page, size));
    }
}