package com.cy.controller;

import com.alibaba.fastjson2.JSONObject;
import com.cy.common.R;
import com.cy.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/send")
    public R sendMessage(@RequestBody JSONObject params) {
        return messageService.sendMessage(params);
    }

    @GetMapping("/history")
    public R fetchMessages(@RequestParam("cid") String cid) {
        return messageService.fetchMessages(cid);
    }
}
