package com.campus.trade.service;

import com.campus.trade.dto.AdminOrderCreateDTO;
import com.campus.trade.dto.AdminOrderUpdateDTO;
import com.campus.trade.dto.CreateOrderDTO;
import com.campus.trade.dto.DeliveryStatsDTO;
import com.campus.trade.dto.ShipmentDTO;
import com.campus.trade.dto.PageResult; // 【新增】
import com.campus.trade.entity.Order;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface OrderService {
    Order createOrder(String buyerId, CreateOrderDTO createOrderDTO);
    Order getOrderDetails(String orderId);
    List<Order> getMyPurchases(String userId);
    List<Order> getMySales(String userId);
    Order updateOrderStatus(String orderId, String userId, String status);

    // 【修改】为管理员查询所有订单的方法，增加分页参数
    PageResult<Order> findAllOrdersForAdmin(String orderId, String deliveryMethod, Integer page, Integer size);

    void updateOrderStatusByAdmin(String orderId, String status);
    void deleteOrder(String orderId);
    Order updateOrderByAdmin(String orderId, AdminOrderUpdateDTO orderDTO);
    Order createOrderByAdmin(AdminOrderCreateDTO orderDTO);
    
    // 配送管理相关方法
    DeliveryStatsDTO getDeliveryStats();
    void shipOrder(String orderId, ShipmentDTO shipmentDTO);
    void exportDeliveryOrders(String orderId, String deliveryMethod, String orderStatus, HttpServletResponse response) throws IOException;
}