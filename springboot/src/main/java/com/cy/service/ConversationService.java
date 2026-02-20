package com.cy.service;

import com.alibaba.fastjson2.JSONObject;
import com.cy.common.R;

public interface ConversationService {

    R createConversation(JSONObject params);

    R listConversation(String uid, Integer pageNum, Integer pageSize);
}
