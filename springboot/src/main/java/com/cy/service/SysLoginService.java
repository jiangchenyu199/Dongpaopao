package com.cy.service;

import com.cy.common.R;

/**
 * 管理端登录服务
 */
public interface SysLoginService {

    /**
     * 用户名密码登录，返回 JWT
     */
    R login(String username, String password);
}
