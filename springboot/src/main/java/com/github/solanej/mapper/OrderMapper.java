package com.github.solanej.mapper;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.solanej.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    List<JSONObject> getHallOrders(@Param("uid") String uid, @Param("serviceId") String serviceId);

    List<JSONObject> processingOrder(@Param("type") String type, @Param("uid") String uid);
}
