package com.cy.service;

import com.cy.common.R;

public interface AuthService {

    R login(String code);

    R getPhoneNumber(String uid, String code);
}
