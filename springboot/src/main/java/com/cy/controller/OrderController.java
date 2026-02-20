package com.cy.controller;

import com.alibaba.fastjson2.JSONObject;
import com.cy.common.R;
import com.cy.service.OrderService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/create")
    public R createOrder(@RequestBody JSONObject params) {
        return orderService.createOrder(params);
    }

    @PutMapping("/accept")
    public R acceptOrder(@RequestBody JSONObject params) {
        return orderService.acceptOrder(params);
    }

    @PutMapping("/status")
    public R updateOrderStatus(@RequestBody JSONObject params) {
        return orderService.updateOrderStatus(params);
    }

    @GetMapping("/hall")
    public R hallOrders(@RequestParam("uid") @Nullable String uid,
                        @RequestParam("orderTypeId") @Nullable String orderTypeId,
                        @Nullable @RequestParam("pageNum") Integer pageNum,
                        @Nullable @RequestParam("pageSize") Integer pageSize) {
        return orderService.hallOrders(uid, orderTypeId, pageNum, pageSize);
    }

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

    @GetMapping("/detail")
    public R detail(@RequestParam("oid") String oid) {
        return orderService.detail(oid);
    }

    @PutMapping("/rate")
    public R rateOrder(@RequestBody JSONObject params) {
        String oid = params.getString("oid");
        BigDecimal rating = params.getBigDecimal("rating");
        return orderService.rateOrder(oid, rating);
    }
}
