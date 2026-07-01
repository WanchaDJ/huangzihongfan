package com.yupi.yuoj.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.yuoj.common.BaseResponse;
import com.yupi.yuoj.common.DeleteRequest;
import com.yupi.yuoj.common.ErrorCode;
import com.yupi.yuoj.common.ResultUtils;
import com.yupi.yuoj.exception.BusinessException;
import com.yupi.yuoj.exception.ThrowUtils;
import com.yupi.yuoj.model.dto.order.EventOrderCreateRequest;
import com.yupi.yuoj.model.dto.order.OrderCreateRequest;
import com.yupi.yuoj.model.entity.ConsumptionRecord;
import com.yupi.yuoj.model.entity.Event;
import com.yupi.yuoj.model.entity.GrowthRecord;
import com.yupi.yuoj.model.entity.OrderInfo;
import com.yupi.yuoj.model.entity.OrderItem;
import com.yupi.yuoj.model.entity.PointsRecord;
import com.yupi.yuoj.model.entity.Product;
import com.yupi.yuoj.model.entity.User;
import com.yupi.yuoj.model.entity.UserProfile;
import com.yupi.yuoj.service.ConsumptionRecordService;
import com.yupi.yuoj.service.EventService;
import com.yupi.yuoj.service.GrowthRecordService;
import com.yupi.yuoj.service.OrderInfoService;
import com.yupi.yuoj.service.OrderItemService;
import com.yupi.yuoj.service.PointsRecordService;
import com.yupi.yuoj.service.ProductService;
import com.yupi.yuoj.service.UserProfileService;
import com.yupi.yuoj.service.UserService;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

/**
 * 订单接口
 */
@RestController
@RequestMapping("/order")
@Slf4j
public class OrderInfoController {

    @Resource
    private OrderInfoService orderInfoService;

    @Resource
    private OrderItemService orderItemService;

    @Resource
    private ProductService productService;

    @Resource
    private UserService userService;

    @Resource
    private EventService eventService;

    @Resource
    private UserProfileService userProfileService;

    @Resource
    private PointsRecordService pointsRecordService;

    @Resource
    private GrowthRecordService growthRecordService;

    @Resource
    private ConsumptionRecordService consumptionRecordService;

    /**
     * 创建订单
     */
    @PostMapping("/create")
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<Long> createOrder(@RequestBody OrderCreateRequest req, HttpServletRequest request) {
        if (req == null || req.getItems() == null || req.getItems().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        // 生成订单号: ORD + 时间戳 + 随机数
        String orderNo = "ORD" + System.currentTimeMillis()
                + String.format("%04d", new Random().nextInt(10000));
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderNo(orderNo);
        orderInfo.setUserId(loginUser.getId());
        orderInfo.setStatus("pending");
        orderInfo.setStatusText("待付款");
        orderInfo.setCreateTime(new Date());
        orderInfo.setUpdateTime(new Date());
        // 计算总金额和商品数量
        BigDecimal totalAmount = BigDecimal.ZERO;
        int itemCount = 0;
        for (OrderCreateRequest.OrderItemRequest itemReq : req.getItems()) {
            if (itemReq.getProductId() == null || itemReq.getQuantity() == null || itemReq.getQuantity() <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
            Product product = productService.getById(itemReq.getProductId());
            if (product == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "产品不存在");
            }
            int stock = product.getStock() != null ? product.getStock() : 0;
            if (stock < itemReq.getQuantity()) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "库存不足");
            }
            BigDecimal productPrice = product.getPrice() != null ? product.getPrice() : itemReq.getPrice();
            if (productPrice == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "产品价格缺失");
            }
            totalAmount = totalAmount.add(productPrice
                    .multiply(BigDecimal.valueOf(itemReq.getQuantity())));
            itemCount += itemReq.getQuantity();
            // 扣减库存
            product.setStock(stock - itemReq.getQuantity());
            product.setSoldCount((product.getSoldCount() != null ? product.getSoldCount() : 0)
                    + itemReq.getQuantity());
            productService.updateById(product);
        }
        orderInfo.setTotalAmount(totalAmount);
        orderInfo.setItemCount(itemCount);
        boolean saved = orderInfoService.save(orderInfo);
        ThrowUtils.throwIf(!saved, ErrorCode.OPERATION_ERROR);
        // 保存订单项
        for (OrderCreateRequest.OrderItemRequest itemReq : req.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(orderInfo.getId());
            orderItem.setProductId(itemReq.getProductId());
            Product product = productService.getById(itemReq.getProductId());
            orderItem.setName(product != null ? product.getName() : itemReq.getName());
            orderItem.setSpec(itemReq.getSpec());
            orderItem.setPrice(product != null && product.getPrice() != null ? product.getPrice() : itemReq.getPrice());
            orderItem.setQuantity(itemReq.getQuantity());
            orderItem.setImage(product != null ? product.getCoverImage() : itemReq.getImage());
            orderItem.setCreateTime(new Date());
            orderItemService.save(orderItem);
        }
        return ResultUtils.success(orderInfo.getId());
    }

    /**
     * 获取当前用户的订单列表（分页）
     */
    @PostMapping("/my/list/page")
    public BaseResponse<Page<OrderInfo>> getMyOrders(
            @RequestBody Map<String, Object> body,
            HttpServletRequest request) {
        long current = body.get("current") != null ? ((Number) body.get("current")).longValue() : 1;
        long pageSize = body.get("pageSize") != null ? ((Number) body.get("pageSize")).longValue() : 10;
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR);
        Page<OrderInfo> page = orderInfoService.page(
                new Page<>(current, pageSize),
                new QueryWrapper<OrderInfo>()
                        .eq("userId", loginUser.getId())
                        .orderByDesc("createTime"));
        page.getRecords().forEach(this::fillOrderItems);
        return ResultUtils.success(page);
    }

    /**
     * 支付订单
     */
    @PostMapping("/pay/{orderId}")
    public BaseResponse<Boolean> payOrder(@PathVariable Long orderId, HttpServletRequest request) {
        if (orderId == null || orderId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        OrderInfo order = orderInfoService.getById(orderId);
        ThrowUtils.throwIf(order == null, ErrorCode.NOT_FOUND_ERROR);
        if (!order.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        if (!"pending".equals(order.getStatus())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "只能支付待付款订单");
        }
        order.setStatus("paid");
        order.setStatusText("待发货");
        order.setPayTime(new Date());
        order.setUpdateTime(new Date());
        boolean result = orderInfoService.updateById(order);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        recordMemberConsumption(order);
        return ResultUtils.success(true);
    }

    /**
     * 取消订单
     */
    @PostMapping("/cancel/{orderId}")
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<Boolean> cancelOrder(@PathVariable Long orderId, HttpServletRequest request) {
        if (orderId == null || orderId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        OrderInfo order = orderInfoService.getById(orderId);
        ThrowUtils.throwIf(order == null, ErrorCode.NOT_FOUND_ERROR);
        if (!order.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        if (!"pending".equals(order.getStatus())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "只能取消待付款订单");
        }
        order.setStatus("cancelled");
        order.setStatusText("已取消");
        order.setUpdateTime(new Date());
        // 恢复库存
        List<OrderItem> items = orderItemService.list(
                new QueryWrapper<OrderItem>().eq("orderId", orderId));
        for (OrderItem item : items) {
            Product product = productService.getById(item.getProductId());
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
                product.setSoldCount((product.getSoldCount() != null ? product.getSoldCount() : 0)
                        - item.getQuantity());
                productService.updateById(product);
            }
        }
        boolean result = orderInfoService.updateById(order);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 确认收货
     */
    @PostMapping("/confirm/{orderId}")
    public BaseResponse<Boolean> confirmOrder(@PathVariable Long orderId, HttpServletRequest request) {
        if (orderId == null || orderId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        OrderInfo order = orderInfoService.getById(orderId);
        ThrowUtils.throwIf(order == null, ErrorCode.NOT_FOUND_ERROR);
        if (!order.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        order.setStatus("completed");
        order.setStatusText("已完成");
        order.setUpdateTime(new Date());
        boolean result = orderInfoService.updateById(order);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 创建演出订单
     */
    @PostMapping("/create/event")
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<Long> createEventOrder(@RequestBody Map<String, Object> req, HttpServletRequest request) {
        if (req == null || req.get("eventId") == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        Long eventId = null;
        try {
            eventId = Long.parseLong(req.get("eventId").toString());
        } catch (NumberFormatException e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "演出ID格式错误");
        }
        
        if (eventId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        User loginUser = userService.getLoginUser(request);
        Event event = eventService.getById(eventId);
        ThrowUtils.throwIf(event == null, ErrorCode.NOT_FOUND_ERROR, "演出不存在");
        
        // 检查演出状态
        if (!"ON_SALE".equals(event.getStatus()) && !"PRESALE".equals(event.getStatus())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "该演出尚未开售");
        }
        
        // 解析数量
        Integer quantity = null;
        try {
            quantity = Integer.parseInt(req.get("quantity").toString());
        } catch (NumberFormatException e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "数量格式错误");
        }
        
        if (quantity == null || quantity <= 0 || quantity > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "数量必须在1-5之间");
        }
        
        // 解析价格
        BigDecimal price = null;
        try {
            Object priceObj = req.get("price");
            if (priceObj instanceof BigDecimal) {
                price = (BigDecimal) priceObj;
            } else if (priceObj instanceof Number) {
                price = BigDecimal.valueOf(((Number) priceObj).doubleValue());
            } else {
                price = new BigDecimal(priceObj.toString());
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "价格格式错误");
        }
        
        // 检查库存
        int soldCount = event.getSoldCount() != null ? event.getSoldCount() : 0;
        if (soldCount + quantity > event.getStock()) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "库存不足");
        }
        
        // 获取规格描述
        String spec = req.get("spec") != null ? req.get("spec").toString() : "演出票";
        
        // 生成订单号: ORD + 时间戳 + 随机数
        String orderNo = "ORD" + System.currentTimeMillis()
                + String.format("%04d", new Random().nextInt(10000));
        
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderNo(orderNo);
        orderInfo.setUserId(loginUser.getId());
        orderInfo.setStatus("pending");
        orderInfo.setStatusText("待付款");
        orderInfo.setTotalAmount(price.multiply(BigDecimal.valueOf(quantity)));
        orderInfo.setItemCount(quantity);
        orderInfo.setCreateTime(new Date());
        orderInfo.setUpdateTime(new Date());
        
        boolean saved = orderInfoService.save(orderInfo);
        ThrowUtils.throwIf(!saved, ErrorCode.OPERATION_ERROR);
        
        // 保存订单项
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(orderInfo.getId());
        orderItem.setProductId(eventId); // 使用eventId作为productId
        orderItem.setName(event.getTitle());
        orderItem.setSpec(spec);
        orderItem.setPrice(price);
        orderItem.setQuantity(quantity);
        orderItem.setImage(event.getCoverImage());
        orderItem.setCreateTime(new Date());
        orderItemService.save(orderItem);
        
        // 更新演出库存
        event.setSoldCount(soldCount + quantity);
        if (event.getSoldCount() >= event.getStock()) {
            event.setStatus("SOLD_OUT");
        }
        eventService.updateById(event);
        
        log.info("创建演出订单成功: orderNo={}, eventId={}, userId={}", orderNo, eventId, loginUser.getId());
        return ResultUtils.success(orderInfo.getId());
    }

    /**
     * 取消演出订单（恢复库存）
     */
    @PostMapping("/cancel/event/{orderId}")
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<Boolean> cancelEventOrder(@PathVariable Long orderId, HttpServletRequest request) {
        if (orderId == null || orderId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        OrderInfo order = orderInfoService.getById(orderId);
        ThrowUtils.throwIf(order == null, ErrorCode.NOT_FOUND_ERROR);
        if (!order.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        if (!"pending".equals(order.getStatus())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "只能取消待付款的订单");
        }
        
        order.setStatus("cancelled");
        order.setStatusText("已取消");
        order.setUpdateTime(new Date());
        
        // 恢复演出库存
        List<OrderItem> items = orderItemService.list(
                new QueryWrapper<OrderItem>().eq("orderId", orderId));
        for (OrderItem item : items) {
            Event event = eventService.getById(item.getProductId());
            if (event != null) {
                int soldCount = event.getSoldCount() != null ? event.getSoldCount() : 0;
                event.setSoldCount(Math.max(0, soldCount - item.getQuantity()));
                if ("SOLD_OUT".equals(event.getStatus()) && event.getSoldCount() < event.getStock()) {
                    event.setStatus("ON_SALE");
                }
                eventService.updateById(event);
            }
        }
        
        boolean result = orderInfoService.updateById(order);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    private void fillOrderItems(OrderInfo order) {
        if (order == null || order.getId() == null) {
            return;
        }
        List<OrderItem> items = orderItemService.list(new QueryWrapper<OrderItem>().eq("orderId", order.getId()));
        order.setItems(items);
    }

    private void recordMemberConsumption(OrderInfo order) {
        if (order == null || order.getUserId() == null || order.getTotalAmount() == null) {
            return;
        }
        Date now = new Date();
        BigDecimal amount = order.getTotalAmount();
        int points = Math.max(1, amount.intValue());
        int growth = points * 2;

        UserProfile profile = getOrCreateProfile(order.getUserId());
        profile.setPoints((profile.getPoints() == null ? 0 : profile.getPoints()) + points);
        profile.setGrowthValue((profile.getGrowthValue() == null ? 0 : profile.getGrowthValue()) + growth);
        profile.setTotalSpend((profile.getTotalSpend() == null ? BigDecimal.ZERO : profile.getTotalSpend()).add(amount));
        profile.setUpdateTime(now);
        userProfileService.updateById(profile);

        PointsRecord pointsRecord = new PointsRecord();
        pointsRecord.setUserId(order.getUserId());
        pointsRecord.setPoints(points);
        pointsRecord.setType("order");
        pointsRecord.setDescription("订单支付奖励：" + order.getOrderNo());
        pointsRecord.setCreateTime(now);
        pointsRecordService.save(pointsRecord);

        GrowthRecord growthRecord = new GrowthRecord();
        growthRecord.setUserId(order.getUserId());
        growthRecord.setValue(growth);
        growthRecord.setReason("订单支付成长值：" + order.getOrderNo());
        growthRecord.setCreateTime(now);
        growthRecordService.save(growthRecord);

        ConsumptionRecord consumptionRecord = new ConsumptionRecord();
        consumptionRecord.setUserId(order.getUserId());
        consumptionRecord.setAmount(amount);
        consumptionRecord.setType("order");
        consumptionRecord.setDescription("订单支付：" + order.getOrderNo());
        consumptionRecord.setCreateTime(now);
        consumptionRecordService.save(consumptionRecord);
    }

    private UserProfile getOrCreateProfile(Long userId) {
        UserProfile profile = userProfileService.getOne(new QueryWrapper<UserProfile>().eq("userId", userId).last("limit 1"));
        if (profile != null) {
            return profile;
        }
        Date now = new Date();
        profile = new UserProfile();
        profile.setUserId(userId);
        profile.setMemberLevel("regular");
        profile.setGrowthValue(0);
        profile.setPoints(0);
        profile.setTotalSpend(BigDecimal.ZERO);
        profile.setInviteCode("HZHF" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase());
        profile.setInviteCount(0);
        profile.setCreateTime(now);
        profile.setUpdateTime(now);
        userProfileService.save(profile);
        return profile;
    }
}
