package com.cy.controller;

import com.cy.common.R;
import com.cy.entity.AppNotice;
import com.cy.service.AppNoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/app/notice")
@RequiredArgsConstructor
public class AdminAppNoticeController {

    private final AppNoticeService appNoticeService;

    @GetMapping("/list")
    public R list() {
        List<AppNotice> list = appNoticeService.listForAdmin();
        return R.success(list);
    }

    @PostMapping("/save")
    public R save(@RequestBody AppNotice notice) {
        appNoticeService.save(notice);
        return R.success();
    }

    @DeleteMapping("/delete/{id}")
    public R delete(@PathVariable Long id) {
        appNoticeService.removeById(id);
        return R.success();
    }
}
