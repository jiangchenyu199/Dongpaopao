package com.github.solanej.service.app;

import com.alibaba.fastjson2.JSONObject;
import com.github.solanej.common.R;

public interface MessageService {

    R sendMessage(JSONObject params);

    R updateMessageStatus(JSONObject params);

    R fetchMessages(String cid);
}

