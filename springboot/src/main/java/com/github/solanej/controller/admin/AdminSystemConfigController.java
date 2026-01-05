package com.github.solanej.controller.admin;

import com.alibaba.fastjson2.JSONObject;
import com.github.solanej.common.R;
import com.github.solanej.service.admin.AdminSystemConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.github.solanej.common.R.success;

@RestController
@RequestMapping("/admin/system/config")
@RequiredArgsConstructor
public class AdminSystemConfigController {

    private final AdminSystemConfigService adminSystemConfigService;

    @PutMapping("/update")
    public R update(@RequestBody JSONObject jsonBody) {
        String configKey = jsonBody.getString("configKey");
        String configValue = jsonBody.getString("configValue");
        adminSystemConfigService.setConfigValue(configKey, configValue);
        return success();
    }

    @GetMapping("/list")
    public R list() {
        return success(adminSystemConfigService.list());
    }

    @GetMapping("/info")
    public R getSystemInfo() {
        return success(adminSystemConfigService.getSystemInfo());
    }
}
