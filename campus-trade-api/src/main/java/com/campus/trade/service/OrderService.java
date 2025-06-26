package com.campus.trade.service;

import com.campus.trade.dto.CreateOrderDTO;
import com.campus.trade.dto.PageResult; // 【新增】
import com.campus.trade.entity.Order;
import java.util.List;

public interface OrderService {
    Order createOrder(String buyerId, CreateOrderDTO createOrderDTO);
    Order getOrderDetails(String orderId);
    List<Order> getMyPurchases(String userId);
    List<Order> getMySales(String userId);
    Order updateOrderStatus(String orderId, String userId, String status);

    // 【修改】为管理员查询所有订单的方法，增加分页参数
    PageResult<Order> findAllOrdersForAdmin(String orderId, Integer page, Integer size);
}