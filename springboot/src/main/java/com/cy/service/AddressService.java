package com.cy.service;

import com.alibaba.fastjson2.JSONObject;
import com.cy.common.R;

public interface AddressService {

    R addAddress(JSONObject params);

    R deleteAddress(JSONObject params);

    R updateAddress(JSONObject params);

    R list(String uid);
}
