package com.cy.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cy.common.R;
import com.cy.entity.Conversation;
import com.cy.entity.Order;
import com.cy.mapper.ConversationMapper;
import com.cy.mapper.OrderMapper;
import com.cy.service.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {

    private final ConversationMapper conversationMapper;

    private final OrderMapper orderMapper;

    @Override
    public R createConversation(JSONObject params) {

        String oid = params.getString("oid");
        Order order = orderMapper.selectOne(new LambdaQueryWrapper<Order>().eq(Order::getOid, oid));

        Conversation conversation = new Conversation();
        conversation.setOid(oid);

        conversationMapper.insert(conversation);
        return R.success(conversation.getCid());
    }

    @Override
    public R listConversation(String uid, Integer pageNum, Integer pageSize) {
        Page<JSONObject> page = new Page<>(pageNum, pageSize);
        Page<JSONObject> result = conversationMapper.listConversation(page, uid);
        return R.success(result);
    }
}
