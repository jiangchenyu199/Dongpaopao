package com.cy.controller.admin;

import com.alibaba.fastjson2.JSONObject;
import com.cy.common.R;
import com.cy.service.admin.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @PostMapping("/add")
    public R addUser(@RequestBody JSONObject jsonBody) {
        return adminUserService.addUser(jsonBody);
    }

    @PutMapping("/update")
    public R updateUser(@RequestBody JSONObject jsonBody) {
        return adminUserService.updateUser(jsonBody);
    }

    @DeleteMapping("/{id}")
    public R deleteUser(@PathVariable Long id) {
        return adminUserService.deleteUser(id);
    }

    @PostMapping("/login")
    public R login(@RequestBody JSONObject jsonBody) {
        return adminUserService.login(jsonBody);
    }

    @GetMapping("/list")
    public R list(@RequestParam(defaultValue = "1") Integer page,
                  @RequestParam(defaultValue = "10") Integer size,
                  @RequestParam String keyword) {
        return adminUserService.list(page, size, keyword);
    }
}