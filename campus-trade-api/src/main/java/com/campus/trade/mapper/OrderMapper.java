package com.campus.trade.mapper;

import com.campus.trade.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface OrderMapper {
    Order findOrderById(String orderId);
    List<Order> findOrdersByBuyerId(String buyerId);
    List<Order> findOrdersBySellerId(String sellerId);
    void insertOrder(Order order);
    void updateOrderStatus(@Param("orderId") String orderId, @Param("status") String status);

    // 【新增】为管理员查询所有订单的方法
    List<Order> findAllForAdmin(@Param("orderId") String orderId, @Param("deliveryMethod") String deliveryMethod);

    long countTotalOrders();

    void deleteById(String orderId);

    void updateOrderByAdmin(Order order);

}