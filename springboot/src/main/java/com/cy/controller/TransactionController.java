package com.cy.controller;

import com.alibaba.fastjson2.JSONObject;
import com.cy.common.R;
import com.cy.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/create")
    public R create(@RequestBody JSONObject param) {
        return transactionService.create(param);
    }

    @GetMapping("/list")
    public R list(@RequestParam("uid") String uid) {
        return transactionService.list(uid);
    }
}
