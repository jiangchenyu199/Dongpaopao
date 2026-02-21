package com.cy.controller;

import com.cy.common.R;
import com.cy.service.SysMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/menu")
@RequiredArgsConstructor
public class AdminMenuController {

    private final SysMenuService sysMenuService;

    @GetMapping("/tree")
    public R tree() {
        return R.success(sysMenuService.listTree());
    }
}
