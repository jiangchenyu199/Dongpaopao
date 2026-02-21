package com.cy.controller;

import com.alibaba.fastjson2.JSONObject;
import com.cy.common.R;
import com.cy.service.SysLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class SysLoginController {

    private final SysLoginService sysLoginService;

    @PostMapping("/login")
    public R login(@RequestBody JSONObject params) {
        String username = params.getString("username");
        String password = params.getString("password");
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return R.error("用户名和密码不能为空");
        }
        return sysLoginService.login(username.trim(), password);
    }

    @GetMapping("/getInfo")
    public R getInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("adminUserId");
        if (userId == null) {
            return R.error("未登录");
        }
        return sysLoginService.getInfo(userId);
    }
}
