package com.cy.service.app;

import com.alibaba.fastjson2.JSONObject;
import com.cy.common.R;

import java.math.BigDecimal;

public interface OrderService {

    R createOrder(JSONObject params);

    R acceptOrder(JSONObject params);

    R updateOrderStatus(JSONObject params);

    R hallOrders(String uid, String orderTypeId, Integer pageNum, Integer pageSize);

    R listMyOrders(String uid, String role, String status, String type, Integer pageNum, Integer pageSize);

    R detail(String oid);

    R rateOrder(String oid, BigDecimal rating);
}
