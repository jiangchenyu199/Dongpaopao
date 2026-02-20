package com.cy.controller;

import com.cy.common.R;
import com.cy.service.AuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Api(tags = "认证接口")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/login")
    @ApiOperation("微信小程序登录换取用户身份")
    public R login(@NotNull @RequestParam("code") String code) {
        return authService.login(code);
    }

    @GetMapping("/getPhoneNumber")
    @ApiOperation("根据一次性code获取手机号")
    public R getPhoneNumber(@RequestParam("uid") String uid, @NotNull @RequestParam("code") String code) {
        return authService.getPhoneNumber(uid, code);
    }
}
