package com.cy.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cy.common.R;
import com.cy.entity.SysUser;
import com.cy.service.AdminSysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/sys-user")
@RequiredArgsConstructor
public class AdminSysUserController {

    private final AdminSysUserService adminSysUserService;

    @GetMapping("/list")
    public R list(@RequestParam(defaultValue = "1") Integer page,
                  @RequestParam(defaultValue = "10") Integer size,
                  @RequestParam(required = false) String keyword) {
        Page<SysUser> result = adminSysUserService.list(page, size, keyword);
        return R.success(result);
    }

    @GetMapping("/detail/{id}")
    public R detail(@PathVariable Long id) {
        return R.success(adminSysUserService.getById(id));
    }

    @PostMapping("/save")
    public R save(@RequestBody SysUser user) {
        adminSysUserService.save(user);
        return R.success();
    }

    @DeleteMapping("/delete/{id}")
    public R delete(@PathVariable Long id) {
        adminSysUserService.removeById(id);
        return R.success();
    }
}
