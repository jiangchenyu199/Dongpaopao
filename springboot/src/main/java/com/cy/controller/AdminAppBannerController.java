package com.cy.controller;

import com.cy.common.R;
import com.cy.entity.AppBanner;
import com.cy.service.AppBannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/app/banner")
@RequiredArgsConstructor
public class AdminAppBannerController {

    private final AppBannerService appBannerService;

    @GetMapping("/list")
    public R list() {
        List<AppBanner> list = appBannerService.listForAdmin();
        return R.success(list);
    }

    @PostMapping("/save")
    public R save(@RequestBody AppBanner entity) {
        appBannerService.save(entity);
        return R.success();
    }

    @DeleteMapping("/delete/{id}")
    public R delete(@PathVariable Long id) {
        appBannerService.removeById(id);
        return R.success();
    }
}
