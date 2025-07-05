package com.campus.trade.service.impl;

import com.campus.trade.dto.CreateOrderDTO;
import com.campus.trade.dto.PageResult;
import com.campus.trade.entity.Order;
import com.campus.trade.entity.Product;
import com.campus.trade.exception.CustomException;
import com.campus.trade.mapper.OrderMapper;
import com.campus.trade.mapper.ProductMapper;
import com.campus.trade.service.NotificationService;
import com.campus.trade.service.OrderService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final ProductMapper productMapper;
    private final NotificationService notificationService;

    @Autowired
    public OrderServiceImpl(OrderMapper orderMapper, ProductMapper productMapper, NotificationService notificationService) {
        this.orderMapper = orderMapper;
        this.productMapper = productMapper;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    @CacheEvict(value = {"products", "product::*"}, allEntries = true)
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

        String notificationContent = String.format("您发布的商品 “%s” 已被购买！", product.getTitle());
        notificationService.createNotification(product.getSellerId(), "NEW_ORDER", notificationContent, order.getId());

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

    @Override
    public PageResult<Order> findAllOrdersForAdmin(String orderId, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        // 【最终修正】采用与 UserServiceImpl 完全一致的、更健壮的实现方式
        List<Order> orderList = orderMapper.findAllForAdmin(orderId);
        return new PageResult<>(orderList);
    }
}