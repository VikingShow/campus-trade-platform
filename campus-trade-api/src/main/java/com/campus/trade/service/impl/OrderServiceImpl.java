package com.campus.trade.service.impl;

import com.campus.trade.dto.CreateOrderDTO;
import com.campus.trade.entity.Order;
import com.campus.trade.entity.Product;
import com.campus.trade.exception.CustomException;
import com.campus.trade.mapper.OrderMapper;
import com.campus.trade.mapper.ProductMapper;
import com.campus.trade.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired private OrderMapper orderMapper;
    @Autowired private ProductMapper productMapper;

    @Override
    @Transactional
    @CacheEvict(value = {"product", "products"}, allEntries = true)
    public Order createOrder(String buyerId, CreateOrderDTO createOrderDTO) {
        Product product = productMapper.findProductById(createOrderDTO.getProductId());
        if (product == null) throw new CustomException("商品不存在");
        if (!"AVAILABLE".equals(product.getStatus())) throw new CustomException("商品已售出或已下架");
        if (Objects.equals(product.getSellerId(), buyerId)) throw new CustomException("不能购买自己发布的商品");

        Order order = new Order();
        order.setProductId(product.getId());
        order.setBuyerId(buyerId);
        order.setSellerId(product.getSellerId());
        order.setOrderStatus("AWAITING_MEETUP");
        order.setTotalPrice(product.getPrice());
        order.setDeliveryMethod("ON_CAMPUS_MEETUP");
        order.setMeetupLocationId(createOrderDTO.getMeetupLocationId());
        order.setMeetupTimeSlot(createOrderDTO.getMeetupTimeSlot());
        orderMapper.insertOrder(order);

        productMapper.updateProductStatus(product.getId(), "SOLD");
        return orderMapper.findOrderById(order.getId());
    }

    @Override
    @Cacheable(value = "order", key = "#orderId")
    public Order getOrderDetails(String orderId) {
        return orderMapper.findOrderById(orderId);
    }

    @Override
    public List<Order> getMyPurchases(String userId) {
        return orderMapper.findOrdersByBuyerId(userId);
    }

    @Override
    public List<Order> getMySales(String userId) {
        return orderMapper.findOrdersBySellerId(userId);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"order", "product"}, allEntries = true)
    public Order updateOrderStatus(String orderId, String currentUserId, String status) {
        Order order = orderMapper.findOrderById(orderId);
        if (order == null) throw new CustomException("订单不存在");

        if (!Objects.equals(order.getBuyerId(), currentUserId) && !Objects.equals(order.getSellerId(), currentUserId)) {
            throw new CustomException("无权操作此订单");
        }

        if ("COMPLETED".equals(status) && Objects.equals(order.getSellerId(), currentUserId)) {
            orderMapper.updateOrderStatus(orderId, status);
        } else if ("CANCELLED".equals(status) && Objects.equals(order.getBuyerId(), currentUserId)) {
            if (!"AWAITING_MEETUP".equals(order.getOrderStatus())) {
                throw new CustomException("当前状态无法取消订单");
            }
            orderMapper.updateOrderStatus(orderId, status);
            productMapper.updateProductStatus(order.getProductId(), "AVAILABLE");
        } else {
            throw new CustomException("无效的操作或权限不足");
        }

        return orderMapper.findOrderById(orderId);
    }

    // 【新增】为管理员查询所有订单的方法实现
    @Override
    public List<Order> findAllOrdersForAdmin(String orderId) {
        return orderMapper.findAllForAdmin(orderId);
    }
}