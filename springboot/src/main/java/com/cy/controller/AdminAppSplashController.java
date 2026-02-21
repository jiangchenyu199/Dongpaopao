package com.cy.controller;

import com.cy.common.R;
import com.cy.entity.AppSplash;
import com.cy.service.AppSplashService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/app/splash")
@RequiredArgsConstructor
public class AdminAppSplashController {

    private final AppSplashService appSplashService;

    @GetMapping
    public R get() {
        return R.success(appSplashService.getForAdmin());
    }

    @PostMapping
    public R save(@RequestBody AppSplash splash) {
        appSplashService.save(splash);
        return R.success();
    }
}
