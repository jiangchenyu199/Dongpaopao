package com.github.solanej.service.app;

import com.alibaba.fastjson2.JSONObject;
import com.github.solanej.common.R;

public interface ConversationService {

    R createConversation(JSONObject params);

    R listConversation(String uid);
}
