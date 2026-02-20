package com.cy.controller;

import com.cy.common.R;
import com.cy.service.AdminOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端订单
 */
@RestController
@RequestMapping("/admin/order")
@RequiredArgsConstructor
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    @GetMapping("/list")
    public R list(@RequestParam(defaultValue = "1") Integer page,
                  @RequestParam(defaultValue = "10") Integer size,
                  @RequestParam(required = false) String keyword,
                  @RequestParam(required = false) String status) {
        return adminOrderService.list(page, size, keyword, status);
    }

    @GetMapping("/detail/{oid}")
    public R detail(@PathVariable String oid) {
        return adminOrderService.detail(oid);
    }
}
