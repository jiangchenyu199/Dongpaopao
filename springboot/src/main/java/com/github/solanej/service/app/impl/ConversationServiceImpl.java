package com.github.solanej.service.app.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.solanej.common.R;
import com.github.solanej.entity.app.Conversation;
import com.github.solanej.entity.app.Order;
import com.github.solanej.mapper.app.ConversationMapper;
import com.github.solanej.mapper.app.OrderMapper;
import com.github.solanej.service.app.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {

    private final ConversationMapper conversationMapper;

    private final OrderMapper orderMapper;

    @Override
    public R createConversation(JSONObject params) {

        /* 订单信息 */
        String oid = params.getString("oid");
        Order order = orderMapper.selectOne(new LambdaQueryWrapper<Order>().eq(Order::getOid, oid));
        String xdr = order.getXdr();
        String uid = params.getString("uid");

        Conversation conversation = new Conversation();
        conversation.setOid(oid);

        conversationMapper.insert(conversation);
        return R.success(conversation.getCid());
    }

    @Override
    public R listConversation(String uid) {
        return R.success(conversationMapper.listConversation(uid));
    }
}