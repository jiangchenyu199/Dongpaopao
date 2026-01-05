package com.cy.controller.app;

import com.alibaba.fastjson2.JSONObject;
import com.cy.common.R;
import com.cy.service.app.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    /**
     * 发送消息
     *
     * @return 全局统一返回类
     */
    @PostMapping("/send")
    public R sendMessage(@RequestBody JSONObject params) {
        return messageService.sendMessage(params);
    }

    /**
     * 获取所有消息
     *
     * @return 全局统一返回类
     */
    @GetMapping("/history")
    public R fetchMessages(@RequestParam("cid") String cid) {
        return messageService.fetchMessages(cid);
    }
}

