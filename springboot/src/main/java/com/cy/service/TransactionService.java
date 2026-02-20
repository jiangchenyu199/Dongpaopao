package com.cy.service;

import com.alibaba.fastjson2.JSONObject;
import com.cy.common.R;

public interface TransactionService {

    R create(JSONObject param);

    R list(String uid);
}
