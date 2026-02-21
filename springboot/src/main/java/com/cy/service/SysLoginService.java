package com.cy.service;

import com.cy.common.R;

import java.util.Map;

public interface SysLoginService {

    R login(String username, String password);

    R getInfo(Long userId);
}
