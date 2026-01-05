package com.cy.service.app;

import com.cy.common.R;

public interface AuthService {

    R login(String code);

    R getPhoneNumber(String uid, String code);
}
