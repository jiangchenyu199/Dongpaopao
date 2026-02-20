package com.cy.service;

import com.alibaba.fastjson2.JSONObject;
import com.cy.common.R;

public interface MessageService {

    R sendMessage(JSONObject params);

    R updateMessageStatus(JSONObject params);

    R fetchMessages(String cid);
}
