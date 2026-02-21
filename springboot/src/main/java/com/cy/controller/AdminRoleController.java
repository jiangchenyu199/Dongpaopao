package com.cy.controller;

import com.cy.common.R;
import com.cy.entity.SysRole;
import com.cy.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/role")
@RequiredArgsConstructor
public class AdminRoleController {

    private final SysRoleService sysRoleService;

    @GetMapping("/list")
    public R list() {
        return R.success(sysRoleService.list());
    }

    @GetMapping("/detail/{id}")
    public R detail(@PathVariable Long id) {
        return R.success(sysRoleService.getById(id));
    }

    @PostMapping("/save")
    public R save(@RequestBody SysRole role) {
        List<Long> menuIds = role.getMenuIds();
        sysRoleService.save(role, menuIds);
        return R.success();
    }

    @DeleteMapping("/delete/{id}")
    public R delete(@PathVariable Long id) {
        sysRoleService.removeById(id);
        return R.success();
    }
}
