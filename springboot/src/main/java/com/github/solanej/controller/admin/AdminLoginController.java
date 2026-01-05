package com.github.solanej.controller.admin;

import com.github.solanej.common.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/login")
public class AdminLoginController {

    @GetMapping("/index")
    public R index() {
        return R.success("admin login index");
    }
}
