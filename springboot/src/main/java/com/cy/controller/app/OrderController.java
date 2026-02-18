package com.cy.controller.app;

import com.alibaba.fastjson2.JSONObject;
import com.cy.common.R;
import com.cy.service.app.OrderService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 创建订单
     */
    @PostMapping("/create")
    public R createOrder(@RequestBody JSONObject params) {
        return orderService.createOrder(params);
    }

    /**
     * 接单
     *
     * @return 接单结果
     */
    @PutMapping("/accept")
    public R acceptOrder(@RequestBody JSONObject params) {
        return orderService.acceptOrder(params);
    }

    /**
     * 更新订单状态
     */
    @PutMapping("/status")
    public R updateOrderStatus(@RequestBody JSONObject params) {
        return orderService.updateOrderStatus(params);
    }

    /**
     * 接单大厅-订单列表
     *
     * @param uid 用户id
     */
    @GetMapping("/hall")
    public R hallOrders(@RequestParam("uid") @Nullable String uid,
                        @RequestParam("orderTypeId") @Nullable String orderTypeId,
                        @Nullable @RequestParam("pageNum") Integer pageNum,
                        @Nullable @RequestParam("pageSize") Integer pageSize) {
        return orderService.hallOrders(uid, orderTypeId, pageNum, pageSize);
    }

    /**
     * 订单列表
     */
    @GetMapping("/list")
    public R listOrders(
            @RequestParam("uid") String uid,
            @Nullable @RequestParam("role") String role,
            @Nullable @RequestParam("status") String status,
            @Nullable @RequestParam("type") String type,
            @Nullable @RequestParam("pageNum") Integer pageNum,
            @Nullable @RequestParam("pageSize") Integer pageSize
    ) {
        return orderService.listOrders(uid, role, status, type, pageNum, pageSize);
    }

    /**
     * @param oid 订单id
     * @return 订单详情
     */
    @GetMapping("/detail")
    public R detail(@RequestParam("oid") String oid) {
        return orderService.detail(oid);
    }

    /**
     * 获取进行中的订单列表
     *
     * @param uid 用户id
     * @return 进行中的订单列表
     */
    

    /**
     * 订单评分
     *
     * @param params 包含订单ID和评分值
     * @return 评分结果
     */
    @PutMapping("/rate")
    public R rateOrder(@RequestBody JSONObject params) {
        String oid = params.getString("oid");
        BigDecimal rating = params.getBigDecimal("rating");
        return orderService.rateOrder(oid, rating);
    }
}
