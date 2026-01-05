package com.github.solanej.service.app.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.solanej.common.R;
import com.github.solanej.entity.app.Message;
import com.github.solanej.mapper.app.MessageMapper;
import com.github.solanej.service.app.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageMapper messageMapper;

    @Override
    public R sendMessage(JSONObject params) {
        /*
        {
            "cid": "1977717142045904898",
            "sender": "e2876d4f-00bf-44b6-b443-8d388e6a7ec2",
            "receiver": "b2e6a3d8-300d-43b1-a212-3aa7a49e2a9b",
            "type": "TEXT",
            "content": "hello,world!"
        }
        */
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

