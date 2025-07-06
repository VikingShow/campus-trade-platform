package com.campus.trade.controller;

import com.campus.trade.common.Result;
import com.campus.trade.dto.DeliveryStatsDTO;
import com.campus.trade.dto.ShipmentDTO;
import com.campus.trade.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/admin/delivery")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDeliveryController {

    private final OrderService orderService;

    @Autowired
    public AdminDeliveryController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 获取配送统计数据
     */
    @GetMapping("/stats")
    public Result<DeliveryStatsDTO> getDeliveryStats() {
        return Result.success(orderService.getDeliveryStats());
    }

    /**
     * 导出配送订单数据
     */
    @GetMapping("/export")
    public void exportDeliveryOrders(
            @RequestParam(required = false) String orderId,
            @RequestParam(required = false) String deliveryMethod,
            @RequestParam(required = false) String orderStatus,
            HttpServletResponse response) throws IOException {
        
        orderService.exportDeliveryOrders(orderId, deliveryMethod, orderStatus, response);
    }
} 