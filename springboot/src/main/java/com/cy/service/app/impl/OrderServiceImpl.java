package com.cy.service.app.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cy.common.R;
import com.cy.entity.app.Conversation;
import com.cy.entity.app.Order;
import com.cy.entity.app.User;
import com.cy.mapper.app.AddressMapper;
import com.cy.mapper.app.ConversationMapper;
import com.cy.mapper.app.OrderMapper;
import com.cy.mapper.app.UserMapper;
import com.cy.service.app.OrderService;
import com.cy.service.app.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;

    private final AddressMapper addressMapper;

    private final ConversationMapper conversationMapper;

    private final TransactionService transactionService;

    private final UserMapper userMapper;

    @Override
    @Transactional
    public R createOrder(JSONObject params) {
        JSONObject formData = params.getJSONObject("formData");
        JSONObject deliverInfo = params.getJSONObject("deliverInfo");
        JSONObject feeInfo = params.getJSONObject("feeInfo");

        BigDecimal totalFee = feeInfo.getBigDecimal("totalFee");
        String uid = params.getString("uid");

        // 参数校验
        if (totalFee == null || totalFee.compareTo(BigDecimal.ZERO) <= 0) {
            return R.error("金额非法");
        }

        // 1. 先扣减余额（原子操作）
        boolean deductSuccess = userMapper.update(null, new LambdaUpdateWrapper<User>()
                .setSql("balance = balance - " + totalFee)
                .eq(User::getUid, uid)
                .ge(User::getBalance, totalFee)) > 0;

        if (!deductSuccess) {
            return R.error("余额不足");
        }

        // 2. 创建订单
        Order order = new Order();
        order.setXdr(uid);
        order.setOrderTypeId(params.getString("orderTypeId"));
        order.setExpectTime(deliverInfo.getLocalDateTime("expectTime"));
        order.setAid(deliverInfo.getString("aid"));
        order.setDetail(formData.toString());
        order.setAmount(totalFee);
        order.setCreateTime(LocalDateTime.now());

        int insertResult = orderMapper.insert(order);
        if (insertResult <= 0) {
            // 如果订单创建失败，需要回滚余额（这里需要事务支持）
            userMapper.update(null, new LambdaUpdateWrapper<User>()
                    .setSql("balance = balance + " + totalFee)
                    .eq(User::getUid, uid));
            return R.error("创建订单失败");
        }

        // 3. 记录交易流水
        JSONObject transactionParam = new JSONObject();
        transactionParam.put("oid", order.getOid());
        transactionParam.put("uid", uid);
        transactionParam.put("type", "ORDER");
        transactionParam.put("amount", totalFee);
        transactionService.create(transactionParam);

        return R.success();
    }

    @Override
    public R acceptOrder(JSONObject params) {
        String oid = params.getString("oid");
        String uid = params.getString("uid");

        /* 采用数据库状态更新方案，防止并发问题 */
        int effectRows = orderMapper.update(new LambdaUpdateWrapper<Order>()
                .set(Order::getStatus, 'J')         // 接单状态改为进行中
                .set(Order::getJdr, uid)
                .set(Order::getAcceptTime, LocalDateTime.now())
                .eq(Order::getOid, oid)
                .eq(Order::getStatus, 'D'));
        if (effectRows == 0) {
            return R.error("订单不存在或已被接单");
        }
        return R.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R updateOrderStatus(JSONObject params) {
        String oid = params.getString("oid");
        String status = params.getString("status");

        // 先查询订单信息，获取金额和用户ID
        Order order = orderMapper.selectOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getOid, oid));

        if (order == null) {
            return R.error("订单不存在");
        }

        // 更新订单状态
        int updateResult = orderMapper.update(new LambdaUpdateWrapper<Order>()
                .set(Order::getStatus, status.charAt(0))
                .set(Order::getCompleteTime, LocalDateTime.now())
                .eq(Order::getOid, oid));

        if (updateResult <= 0) {
            return R.error("更新订单状态失败");
        }

        // 如果状态更新为已完成（S），则处理金额返还和流水记录
        if ('S' == status.charAt(0)) {
            // 1. 将金额返还给用户（增加余额）
            boolean refundSuccess = userMapper.update(null, new LambdaUpdateWrapper<User>()
                    .setSql("balance = balance + " + order.getAmount())
                    .eq(User::getUid, order.getJdr())) > 0;

            if (!refundSuccess) {
                throw new RuntimeException("返还金额失败");
            }

            // 2. 在流水表中添加完成订单记录（收入为正数）
            JSONObject transactionParam = new JSONObject();
            transactionParam.put("oid", oid);
            transactionParam.put("uid", order.getXdr());
            transactionParam.put("type", "COMPLETE_ORDER");
            transactionParam.put("amount", order.getAmount()); // 收入为正数
            transactionService.create(transactionParam);
        }

        if ('C' == status.charAt(0)) {
            // 3. 如果状态更新为已取消（C），则处理金额返还和流水记录
            // 1. 将金额返还给用户（增加余额）
            boolean refundSuccess = userMapper.update(null, new LambdaUpdateWrapper<User>()
                    .setSql("balance = balance + " + order.getAmount())
                    .eq(User::getUid, order.getXdr())) > 0;

            if (!refundSuccess) {
                throw new RuntimeException("返还金额失败");
            }

            // 2. 在流水表中添加取消订单记录（收入为正数）
            JSONObject transactionParam = new JSONObject();
            transactionParam.put("oid", oid);
            transactionParam.put("uid", order.getXdr());
            transactionParam.put("type", "CANCEL_ORDER");
            transactionParam.put("amount", order.getAmount()); // 收入为正数
            transactionService.create(transactionParam);
        }

        return R.success();
    }

    @Override
    public R hallOrders(String uid, String orderTypeId, Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        Page<JSONObject> page = new Page<>(pageNum, pageSize);
        Page<JSONObject> hallOrders = orderMapper.getHallOrders(page, uid, orderTypeId);
        return R.success(hallOrders);
    }

    @Override
    public R listMyOrders(String uid, String role, String status, String type, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
                .eq(Order::getXdr, uid).or().eq(Order::getJdr, uid);
        if (role != null && !role.isEmpty()) {
            if ("xdr".equals(role)) {
                wrapper.eq(Order::getXdr, uid);
            } else if ("jdr".equals(role)) {
                wrapper.eq(Order::getJdr, uid);
            }
        } else {
            wrapper.eq(Order::getXdr, uid).or().eq(Order::getJdr, uid);
        }

        if (status != null && !status.isEmpty()) {
            wrapper.eq(Order::getStatus, status.charAt(0));
        }
        wrapper.orderByDesc(Order::getCreateTime);
        return R.success(orderMapper.selectList(wrapper));
    }

    @Override
    public R detail(String oid) {
        JSONObject result = new JSONObject();

        JSONObject order = orderMapper.detail(oid);
        result.put("order", order);
        // 地址
        result.put("address", addressMapper.selectById(order.getString("aid")));
        // 如果已接单，就查询会话信息
        if (order.getObject("status", Character.class) == 'J') {
            Conversation conversation = conversationMapper.selectOne(new LambdaQueryWrapper<Conversation>().eq(Conversation::getOid, oid));
            result.put("conversationId", conversation.getCid());
        }
        return R.success(result);
    }

    

    @Override
    public R rateOrder(String oid, BigDecimal rating) {
        // 验证评分范围 (0~5星)
        if (rating == null || rating.compareTo(BigDecimal.ZERO) < 0 || rating.compareTo(new BigDecimal("5.0")) > 0) {
            return R.error("评分必须在0到5星之间");
        }

        // 更新订单评分
        int updateResult = orderMapper.update(new LambdaUpdateWrapper<Order>()
                .set(Order::getRating, rating)
                .eq(Order::getOid, oid));

        if (updateResult <= 0) {
            return R.error("更新评分失败，订单不存在");
        }

        return R.success();
    }
}
