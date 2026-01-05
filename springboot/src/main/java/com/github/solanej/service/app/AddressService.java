package com.github.solanej.service.app;

import com.alibaba.fastjson2.JSONObject;
import com.github.solanej.common.R;

public interface AddressService {

    R addAddress(JSONObject params);

    R deleteAddress(JSONObject params);

    R updateAddress(JSONObject params);

    R list(String uid);
}
