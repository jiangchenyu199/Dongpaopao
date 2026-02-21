package com.cy.controller;

import com.cy.common.R;
import com.cy.entity.AppNotice;
import com.cy.entity.AppSplash;
import com.cy.service.AppNoticeService;
import com.cy.service.AppSplashService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/app")
@RequiredArgsConstructor
public class AppConfigController {

    private final AppSplashService appSplashService;
    private final AppNoticeService appNoticeService;

    @GetMapping("/splash")
    public R splash() {
        AppSplash current = appSplashService.getCurrent();
        return R.success(current);
    }

    @GetMapping("/notices")
    public R notices() {
        List<AppNotice> list = appNoticeService.listForApp();
        return R.success(list);
    }
}
