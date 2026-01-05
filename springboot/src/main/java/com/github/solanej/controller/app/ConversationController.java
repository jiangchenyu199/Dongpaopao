package com.github.solanej.controller.app;

import com.alibaba.fastjson2.JSONObject;
import com.github.solanej.common.R;
import com.github.solanej.service.app.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/conversation")
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationService conversationService;

    /**
     * 当前用户抢单成功后与订单下单人创建会话
     *
     * @param params 下单人uid，订单oid
     * @return 会话id
     */
    @PostMapping("/create")
    public R createConversation(@RequestBody JSONObject params) {
        return conversationService.createConversation(params);
    }

    @GetMapping("/list")
    public R listConversation(@RequestParam("uid") String uid) {
        return conversationService.listConversation(uid);
    }
}