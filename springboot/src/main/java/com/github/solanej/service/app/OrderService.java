package com.github.solanej.service.app;

import com.alibaba.fastjson2.JSONObject;
import com.github.solanej.common.R;

public interface OrderService {

    R createOrder(JSONObject params);

    R acceptOrder(JSONObject params);

    R updateOrderStatus(JSONObject params);

    R hallOrders(String uid, String serviceId);

    R listMyOrders(String uid, String role, String status, String type, Integer pageNum, Integer pageSize);

    R detailOrder(String oid);

    R progressingOrder(String type, String uid);
}
