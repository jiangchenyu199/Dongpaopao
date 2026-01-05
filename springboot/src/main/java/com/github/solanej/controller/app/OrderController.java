package com.github.solanej.controller.app;

import com.alibaba.fastjson2.JSONObject;
import com.github.solanej.common.R;
import com.github.solanej.service.app.OrderService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
                        @RequestParam("serviceId") @Nullable String serviceId) {
        return orderService.hallOrders(uid, serviceId);
    }

    /**
     * 我的订单列表
     */
    @GetMapping("/list-mine")
    public R listMyOrders(
            @RequestParam("uid") String uid,
            @Nullable @RequestParam("role") String role,
            @Nullable @RequestParam("status") String status,
            @Nullable @RequestParam("type") String type,
            @Nullable @RequestParam("pageNum") Integer pageNum,
            @Nullable @RequestParam("pageSize") Integer pageSize
    ) {
        return orderService.listMyOrders(uid, role, status, type, pageNum, pageSize);
    }

    /**
     * @param oid 订单id
     * @return 订单详情
     */
    @GetMapping("/detail")
    public R detailOrder(@RequestParam("oid") String oid) {
        return orderService.detailOrder(oid);
    }

    /**
     * 获取进行中的订单列表
     *
     * @param uid 用户id
     * @return 进行中的订单列表
     */
    @GetMapping("/progressing")
    public R progressingOrder(@Nullable @RequestParam("type") String type, @RequestParam("uid") String uid) {
        return orderService.progressingOrder(type, uid);
    }
}
