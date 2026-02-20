package com.cy.controller;

import com.cy.common.R;
import com.cy.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端 C 端用户管理
 */
@RestController
@RequestMapping("/admin/c-user")
@RequiredArgsConstructor
public class AdminUserController {

    private final SysUserService sysUserService;

    @GetMapping("/list")
    public R list(@RequestParam(defaultValue = "1") Integer page,
                 @RequestParam(defaultValue = "10") Integer size,
                 @RequestParam(required = false) String keyword) {
        return sysUserService.list(page, size, keyword);
    }

    @PutMapping("/status")
    public R updateStatus(@RequestBody java.util.Map<String, Object> params) {
        String uid = (String) params.get("uid");
        Object s = params.get("status");
        Integer status = s instanceof Number ? ((Number) s).intValue() : s != null ? Integer.parseInt(s.toString()) : null;
        return sysUserService.updateStatus(uid, status);
    }
}
