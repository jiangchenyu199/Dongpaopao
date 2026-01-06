package com.cy.controller.app;

import com.alibaba.fastjson2.JSONObject;
import com.cy.common.R;
import com.cy.service.app.ConversationService;
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
    public R listConversation(@RequestParam("uid") String uid,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        return conversationService.listConversation(uid, pageNum, pageSize);
    }
}