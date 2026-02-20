package com.cy.mapper;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cy.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    Page<JSONObject> getHallOrders(Page<JSONObject> page, @Param("uid") String uid, @Param("orderTypeId") String orderTypeId);

    List<JSONObject> listMyOrders(@Param("uid") String uid, @Param("role") String role,
                                   @Param("status") String status, @Param("type") String type);

    JSONObject detail(@Param("oid") String oid);
}
