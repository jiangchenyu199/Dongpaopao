package com.cy.mapper.app;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cy.entity.app.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    List<JSONObject> getHallOrders(@Param("uid") String uid, @Param("orderTypeId") String orderTypeId);

    JSONObject detail(@Param("oid") String oid);

    List<JSONObject> processingOrder(@Param("type") String type, @Param("uid") String uid);

    Page<JSONObject> getAdminOrderList(Page<JSONObject> page, @Param("oid") String oid, @Param("keyword") String keyword, @Param("status") String status);

    JSONObject getAdminOrderDetail(@Param("oid") String oid);
}
