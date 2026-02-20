package com.cy.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cy.common.R;
import com.cy.entity.Message;
import com.cy.mapper.MessageMapper;
import com.cy.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageMapper messageMapper;

    @Override
    public R sendMessage(JSONObject params) {
        Message message = params.to(Message.class);
        messageMapper.insert(message);
        return R.success();
    }

    @Override
    public R updateMessageStatus(JSONObject params) {
        return null;
    }

    @Override
    public R fetchMessages(String cid) {
        return R.success(messageMapper.selectList(
                new LambdaQueryWrapper<Message>()
                        .eq(Message::getCid, cid)
                        .orderByAsc(Message::getSendTime)));
    }
}
