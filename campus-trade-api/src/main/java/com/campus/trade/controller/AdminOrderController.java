package com.campus.trade.controller;

import com.campus.trade.common.Result;
import com.campus.trade.dto.AdminOrderCreateDTO;
import com.campus.trade.dto.AdminOrderUpdateDTO;
import com.campus.trade.dto.PageResult;
import com.campus.trade.entity.Order;
import com.campus.trade.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/orders")
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderController {

    private final OrderService orderService;

    @Autowired
    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public Result<PageResult<Order>> getAllOrders(
            @RequestParam(required = false) String orderId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(orderService.findAllOrdersForAdmin(orderId, page, size));
    }

    /**
     * 【最终修正】新增管理员创建订单的接口
     */
    @PostMapping
    public Result<Order> createOrder(@RequestBody AdminOrderCreateDTO orderDTO) {
        return Result.success(orderService.createOrderByAdmin(orderDTO));
    }

    @PutMapping("/{id}")
    public Result<Order> updateOrder(@PathVariable String id, @RequestBody AdminOrderUpdateDTO orderDTO) {
        return Result.success(orderService.updateOrderByAdmin(id, orderDTO));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteOrder(@PathVariable String id) {
        orderService.deleteOrder(id);
        return Result.success();
    }
}
