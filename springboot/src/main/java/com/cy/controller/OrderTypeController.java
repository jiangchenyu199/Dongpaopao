package com.cy.controller;

import com.cy.common.R;
import com.cy.entity.OrderType;
import com.cy.service.OrderTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order-type")
public class OrderTypeController {

    @Autowired
    private OrderTypeService orderTypeService;

    @PostMapping("/save")
    public R save(@RequestBody OrderType orderType) {
        orderTypeService.saveOrUpdate(orderType);
        return R.success();
    }

    @GetMapping("/detail")
    public R detail(@RequestParam("orderTypeId") String orderTypeId) {
        return R.success(orderTypeService.getById(orderTypeId));
    }

    @GetMapping("/list")
    public R list() {
        return R.success(orderTypeService.list());
    }

    @DeleteMapping("/delete")
    public R delete(@RequestParam("orderTypeId") String orderTypeId) {
        orderTypeService.removeById(orderTypeId);
        return R.success();
    }
}
