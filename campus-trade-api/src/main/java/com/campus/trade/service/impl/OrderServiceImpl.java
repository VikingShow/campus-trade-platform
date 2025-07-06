package com.campus.trade.service.impl;

import com.campus.trade.dto.AdminOrderCreateDTO;
import com.campus.trade.dto.AdminOrderUpdateDTO;
import com.campus.trade.dto.CreateOrderDTO;
import com.campus.trade.dto.DeliveryStatsDTO;
import com.campus.trade.dto.PageResult;
import com.campus.trade.dto.ShipmentDTO;
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
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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

        // 【关键修改】校验用户选择的配送方式是否被卖家支持
        String chosenMethod = createOrderDTO.getDeliveryMethod();
        if (product.getDeliveryOptions() == null || !product.getDeliveryOptions().contains(chosenMethod)) {
            throw new CustomException("该商品不支持您选择的配送方式");
        }
        Order order = new Order();
        order.setProductId(product.getId());
        order.setBuyerId(buyerId);
        order.setSellerId(product.getSellerId());
        order.setTotalPrice(product.getPrice());
        order.setDeliveryMethod(chosenMethod);

        // 根据不同的配送方式，设置不同的初始状态和关联ID
        if ("MEETUP".equals(chosenMethod)) {
            if (createOrderDTO.getMeetupLocationId() == null) throw new CustomException("请选择交易地点");
            order.setOrderStatus("AWAITING_MEETUP");
            order.setMeetupLocationId(createOrderDTO.getMeetupLocationId());
        } else if ("SHIPPING".equals(chosenMethod)) {
            if (createOrderDTO.getShippingAddressId() == null) throw new CustomException("请选择收货地址");
            order.setOrderStatus("AWAITING_SHIPMENT"); // 新状态：待发货
            order.setShippingAddressId(createOrderDTO.getShippingAddressId());
        } else {
            throw new CustomException("无效的配送方式");
        }

        orderMapper.insertOrder(order);
        productMapper.updateProductStatus(product.getId(), "SOLD");

        String notificationContent = String.format("您发布的商品 “%s” 已被购买！", product.getTitle());
        notificationService.createNotification(product.getSellerId(), "NEW_ORDER", notificationContent, order.getId());

        return orderMapper.findOrderById(order.getId());
    }

    @Override
//    @Caching
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
    public PageResult<Order> findAllOrdersForAdmin(String orderId, String deliveryMethod, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        // 【修改】将 deliveryMethod 参数传递给 Mapper
        List<Order> orderList = orderMapper.findAllForAdmin(orderId, deliveryMethod);
        return new PageResult<>(orderList);
    }

    /**
     * 【新增】为管理员提供的更新订单状态方法。
     * 它不进行任何权限检查，因为管理员有权修改任何订单状态。
     */
    @Override
    @CacheEvict(value = {"order::#orderId", "product"}, allEntries = true)
    public void updateOrderStatusByAdmin(String orderId, String status) {
        orderMapper.updateOrderStatus(orderId, status);
        // 如果订单被管理员取消，也应该恢复商品状态
        if ("CANCELLED".equals(status)) {
            Order order = orderMapper.findOrderById(orderId);
            if (order != null) {
                productMapper.updateProductStatus(order.getProductId(), "AVAILABLE");
            }
        }
    }

    /**
     * 【新增】为管理员提供的删除订单方法。
     */
    @Override
    @CacheEvict(value = {"order::#orderId", "product"}, allEntries = true)
    public void deleteOrder(String orderId) {
        orderMapper.deleteById(orderId);
    }

    /**
     * 【新增】为管理员提供的更新订单详情方法。
     */
    @Override
    @CacheEvict(value = "order", key = "#orderId")
    public Order updateOrderByAdmin(String orderId, AdminOrderUpdateDTO orderDTO) {
        Order orderToUpdate = orderMapper.findOrderById(orderId);
        if (orderToUpdate == null) {
            throw new CustomException("订单不存在");
        }

        // 检查并恢复商品状态
        if ("CANCELLED".equals(orderDTO.getOrderStatus()) && !"CANCELLED".equals(orderToUpdate.getOrderStatus())) {
            productMapper.updateProductStatus(orderToUpdate.getProductId(), "AVAILABLE");
        } else if (!"CANCELLED".equals(orderDTO.getOrderStatus()) && "CANCELLED".equals(orderToUpdate.getOrderStatus())) {
            productMapper.updateProductStatus(orderToUpdate.getProductId(), "SOLD");
        }

        orderToUpdate.setOrderStatus(orderDTO.getOrderStatus());
        orderToUpdate.setTotalPrice(orderDTO.getTotalPrice());
        orderToUpdate.setDeliveryMethod(orderDTO.getDeliveryMethod());
        orderToUpdate.setMeetupLocationId(orderDTO.getMeetupLocationId());
        orderToUpdate.setMeetupTimeSlot(orderDTO.getMeetupTimeSlot());
        orderToUpdate.setShippingProvider(orderDTO.getShippingProvider());
        orderToUpdate.setTrackingNumber(orderDTO.getTrackingNumber());
        orderMapper.updateOrderByAdmin(orderToUpdate);
        return orderMapper.findOrderById(orderId);
    }

    /**
     * 【修改】为管理员创建订单的方法实现，增加对配送方式的处理。
     */
    @Override
    @Transactional
    @CacheEvict(value = {"products", "product::*"}, allEntries = true)
    public Order createOrderByAdmin(AdminOrderCreateDTO orderDTO) {
        if (orderDTO.getProductId() == null || orderDTO.getBuyerId() == null) {
            throw new CustomException("必须同时选择商品和买家");
        }
        if (orderDTO.getDeliveryMethod() == null) {
            throw new CustomException("必须选择配送方式");
        }

        Product product = productMapper.findProductById(orderDTO.getProductId());
        if (product == null) {
            throw new CustomException("选择的商品不存在");
        }
        if (!"AVAILABLE".equals(product.getStatus())) {
            throw new CustomException("该商品已售出或已下架，无法创建订单");
        }
        if (Objects.equals(product.getSellerId(), orderDTO.getBuyerId())) {
            throw new CustomException("买家和卖家不能是同一个人");
        }

        Order order = new Order();
        order.setProductId(product.getId());
        order.setBuyerId(orderDTO.getBuyerId());
        order.setSellerId(product.getSellerId());
        order.setTotalPrice(product.getPrice());
        order.setDeliveryMethod(orderDTO.getDeliveryMethod());

        if ("MEETUP".equals(orderDTO.getDeliveryMethod())) {
            order.setOrderStatus("AWAITING_MEETUP");
            order.setMeetupLocationId(orderDTO.getMeetupLocationId());
            order.setMeetupTimeSlot(orderDTO.getMeetupTimeSlot());
        } else if ("SHIPPING".equals(orderDTO.getDeliveryMethod())) {
            order.setOrderStatus("AWAITING_SHIPMENT");
        } else {
            throw new CustomException("无效的配送方式");
        }

        orderMapper.insertOrder(order);

        productMapper.updateProductStatus(product.getId(), "SOLD");

        String notificationContent = String.format("管理员为您创建了一笔新的订单，商品为：“%s”", product.getTitle());
        notificationService.createNotification(product.getSellerId(), "NEW_ORDER", notificationContent, order.getId());

        return orderMapper.findOrderById(order.getId());
    }

    /**
     * 获取配送统计数据
     */
    @Override
    public DeliveryStatsDTO getDeliveryStats() {
        Long awaitingShipment = orderMapper.countOrdersByStatus("AWAITING_SHIPMENT");
        Long shipped = orderMapper.countOrdersByStatus("SHIPPED");
        Long completed = orderMapper.countOrdersByStatus("COMPLETED");
        Long total = orderMapper.countTotalOrders();
        
        return new DeliveryStatsDTO(awaitingShipment, shipped, completed, total);
    }

    /**
     * 发货操作
     */
    @Override
    @Transactional
    @CacheEvict(value = "order", key = "#orderId")
    public void shipOrder(String orderId, ShipmentDTO shipmentDTO) {
        Order order = orderMapper.findOrderById(orderId);
        if (order == null) {
            throw new CustomException("订单不存在");
        }
        
        if (!"AWAITING_SHIPMENT".equals(order.getOrderStatus())) {
            throw new CustomException("订单状态不正确，无法发货");
        }
        
        if (!"SHIPPING".equals(order.getDeliveryMethod())) {
            throw new CustomException("该订单不是快递配送订单");
        }
        
        // 更新订单状态和物流信息
        order.setOrderStatus("SHIPPED");
        order.setShippingProvider(shipmentDTO.getShippingProvider());
        order.setTrackingNumber(shipmentDTO.getTrackingNumber());
        
        orderMapper.updateOrderByAdmin(order);
        
        // 发送通知给买家
        String notificationContent = String.format("您的订单 %s 已发货，快递公司：%s，单号：%s", 
            orderId, shipmentDTO.getShippingProvider(), shipmentDTO.getTrackingNumber());
        notificationService.createNotification(order.getBuyerId(), "ORDER_SHIPPED", notificationContent, orderId);
    }

        /**
     * 导出配送订单数据
     */
    @Override
    public void exportDeliveryOrders(String orderId, String deliveryMethod, String orderStatus, HttpServletResponse response) throws IOException {
        try {
            // 设置响应头
            response.setContentType("text/csv; charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=delivery_orders.csv");
            
            // 获取订单数据
            List<Order> orders = orderMapper.findAllForAdmin(orderId, deliveryMethod);
            
            // 如果指定了订单状态，进行过滤
            if (orderStatus != null && !orderStatus.isEmpty()) {
                orders = orders.stream()
                    .filter(order -> orderStatus.equals(order.getOrderStatus()))
                    .collect(java.util.stream.Collectors.toList());
            }
            
            // 创建简单的CSV内容
            StringBuilder csvContent = new StringBuilder();
            csvContent.append("订单ID,商品标题,买家,卖家,配送方式,订单状态,订单金额\n");
            
            for (Order order : orders) {
                csvContent.append(String.format("%s,%s,%s,%s,%s,%s,%.2f\n",
                    order.getId(),
                    order.getProductTitle() != null ? order.getProductTitle() : "",
                    order.getBuyerNickname() != null ? order.getBuyerNickname() : "",
                    order.getSellerNickname() != null ? order.getSellerNickname() : "",
                    "SHIPPING".equals(order.getDeliveryMethod()) ? "快递配送" : "线下面交",
                    formatOrderStatus(order.getOrderStatus()),
                    order.getTotalPrice()
                ));
            }
            
            // 写入响应
            response.getWriter().write(csvContent.toString());
        } catch (Exception e) {
            throw new CustomException("导出失败: " + e.getMessage());
        }
    }
    
    /**
     * 格式化订单状态
     */
    private String formatOrderStatus(String status) {
        switch (status) {
            case "AWAITING_MEETUP": return "待交易";
            case "AWAITING_SHIPMENT": return "待发货";
            case "SHIPPED": return "已发货";
            case "COMPLETED": return "已完成";
            case "CANCELLED": return "已取消";
            default: return status;
        }
    }
}