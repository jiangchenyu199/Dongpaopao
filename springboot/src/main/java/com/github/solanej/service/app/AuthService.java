package com.github.solanej.service.app;

import com.github.solanej.common.R;

public interface AuthService {

    R login(String code);

    R getPhoneNumber(String uid, String code);
}
