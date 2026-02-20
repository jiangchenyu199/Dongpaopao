package com.cy.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cy.common.R;
import com.cy.entity.Conversation;
import com.cy.entity.Order;
import com.cy.entity.User;
import com.cy.mapper.AddressMapper;
import com.cy.mapper.ConversationMapper;
import com.cy.mapper.OrderMapper;
import com.cy.mapper.UserMapper;
import com.cy.service.OrderService;
import com.cy.service.TransactionService;
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

        BigDecimal totalFee = feeInfo.getBigDecimal("totalAmount");
        String uid = params.getString("uid");

        if (totalFee == null || totalFee.compareTo(BigDecimal.ZERO) <= 0) {
            return R.error("金额非法");
        }

        boolean deductSuccess = userMapper.update(null, new LambdaUpdateWrapper<User>()
                .setSql("balance = balance - " + totalFee)
                .eq(User::getUid, uid)
                .ge(User::getBalance, totalFee)) > 0;

        if (!deductSuccess) {
            return R.error("余额不足");
        }

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
            userMapper.update(null, new LambdaUpdateWrapper<User>()
                    .setSql("balance = balance + " + totalFee)
                    .eq(User::getUid, uid));
            return R.error("创建订单失败");
        }

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

        int effectRows = orderMapper.update(new LambdaUpdateWrapper<Order>()
                .set(Order::getStatus, 'J')
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

        Order order = orderMapper.selectOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getOid, oid));

        if (order == null) {
            return R.error("订单不存在");
        }

        int updateResult = orderMapper.update(new LambdaUpdateWrapper<Order>()
                .set(Order::getStatus, status.charAt(0))
                .set(Order::getCompleteTime, LocalDateTime.now())
                .eq(Order::getOid, oid));

        if (updateResult <= 0) {
            return R.error("更新订单状态失败");
        }

        if ('S' == status.charAt(0)) {
            boolean refundSuccess = userMapper.update(null, new LambdaUpdateWrapper<User>()
                    .setSql("balance = balance + " + order.getAmount())
                    .eq(User::getUid, order.getJdr())) > 0;

            if (!refundSuccess) {
                throw new RuntimeException("返还金额失败");
            }

            JSONObject transactionParam = new JSONObject();
            transactionParam.put("oid", oid);
            transactionParam.put("uid", order.getXdr());
            transactionParam.put("type", "COMPLETE_ORDER");
            transactionParam.put("amount", order.getAmount());
            transactionService.create(transactionParam);
        }

        if ('C' == status.charAt(0)) {
            boolean refundSuccess = userMapper.update(null, new LambdaUpdateWrapper<User>()
                    .setSql("balance = balance + " + order.getAmount())
                    .eq(User::getUid, order.getXdr())) > 0;

            if (!refundSuccess) {
                throw new RuntimeException("返还金额失败");
            }

            JSONObject transactionParam = new JSONObject();
            transactionParam.put("oid", oid);
            transactionParam.put("uid", order.getXdr());
            transactionParam.put("type", "CANCEL_ORDER");
            transactionParam.put("amount", order.getAmount());
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
    public R listOrders(String uid, String role, String status, String type, Integer pageNum, Integer pageSize) {
        if ("jsr".equals(role)) {
            role = "jdr";
        }
        List<JSONObject> list = orderMapper.listMyOrders(uid, role, status, type);
        if (list == null) {
            list = new java.util.ArrayList<>();
        }
        return R.success(list);
    }

    @Override
    public R detail(String oid) {
        JSONObject result = new JSONObject();

        JSONObject order = orderMapper.detail(oid);
        result.put("order", order);
        result.put("address", addressMapper.selectById(order.getString("aid")));
        if (order.getObject("status", Character.class) == 'J') {
            Conversation conversation = conversationMapper.selectOne(new LambdaQueryWrapper<Conversation>().eq(Conversation::getOid, oid));
            result.put("conversationId", conversation.getCid());
        }
        return R.success(result);
    }

    @Override
    public R rateOrder(String oid, BigDecimal rating) {
        if (rating == null || rating.compareTo(BigDecimal.ZERO) < 0 || rating.compareTo(new BigDecimal("5.0")) > 0) {
            return R.error("评分必须在0到5星之间");
        }

        int updateResult = orderMapper.update(new LambdaUpdateWrapper<Order>()
                .set(Order::getRating, rating)
                .eq(Order::getOid, oid));

        if (updateResult <= 0) {
            return R.error("更新评分失败，订单不存在");
        }

        return R.success();
    }
}
