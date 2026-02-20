package com.cy.controller;

import com.alibaba.fastjson2.JSONObject;
import com.cy.common.R;
import com.cy.service.SysLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端登录
 */
@RestController
@RequestMapping("/admin/login")
@RequiredArgsConstructor
public class SysLoginController {

    private final SysLoginService sysLoginService;

    @PostMapping
    public R login(@RequestBody JSONObject params) {
        String username = params.getString("username");
        String password = params.getString("password");
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return R.error("用户名和密码不能为空");
        }
        return sysLoginService.login(username.trim(), password);
    }
}
