package com.cy.service.app;

import com.alibaba.fastjson2.JSONObject;
import com.cy.common.R;

public interface OrderService {

    R createOrder(JSONObject params);

    R acceptOrder(JSONObject params);

    R updateOrderStatus(JSONObject params);

    R hallOrders(String uid, String orderTypeId);

    R listMyOrders(String uid, String role, String status, String type, Integer pageNum, Integer pageSize);

    R detail(String oid);

    R progressingOrder(String type, String uid);
}
