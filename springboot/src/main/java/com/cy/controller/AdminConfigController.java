package com.cy.controller;

import com.cy.common.R;
import com.cy.service.SysConfigService;
import lombok.RequiredArgsConstructor;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理端系统配置
 */
@RestController
@RequestMapping("/admin/system/config")
@RequiredArgsConstructor
public class AdminConfigController {

    private final SysConfigService sysConfigService;

    @GetMapping("/list")
    public R list() {
        return sysConfigService.list();
    }

    @PutMapping("/update")
    public R update(@RequestBody java.util.Map<String, String> params) {
        String configKey = params.get("configKey");
        String configValue = params.get("configValue");
        return sysConfigService.update(configKey, configValue);
    }

    @GetMapping("/info")
    public R info() {
        Map<String, Object> info = new HashMap<>();
        SystemInfo si = new SystemInfo();
        Runtime rt = Runtime.getRuntime();

        Map<String, Object> memory = new HashMap<>();
        memory.put("total", formatBytes(rt.totalMemory()));
        memory.put("used", formatBytes(rt.totalMemory() - rt.freeMemory()));
        memory.put("max", formatBytes(rt.maxMemory()));
        info.put("memory", memory);

        Map<String, Object> jvm = new HashMap<>();
        jvm.put("usage", String.format("%.1f%%", (1 - (double) rt.freeMemory() / rt.totalMemory()) * 100));
        info.put("jvm", jvm);

        CentralProcessor cpu = si.getHardware().getProcessor();
        Map<String, Object> cpuInfo = new HashMap<>();
        cpuInfo.put("cores", cpu.getLogicalProcessorCount());
        cpuInfo.put("model", cpu.getProcessorIdentifier().getName());
        info.put("cpu", cpuInfo);

        Map<String, Object> os = new HashMap<>();
        os.put("name", si.getOperatingSystem().getManufacturer() + " " + si.getOperatingSystem().getFamily());
        os.put("version", si.getOperatingSystem().getVersionInfo().getVersion());
        os.put("arch", System.getProperty("os.arch"));
        info.put("os", os);

        Map<String, Object> java = new HashMap<>();
        java.put("version", System.getProperty("java.version"));
        java.put("vendor", System.getProperty("java.vendor"));
        info.put("java", java);
        return R.success(info);
    }

    private static String formatBytes(long bytes) {
        return String.format("%.1f MB", bytes / 1024.0 / 1024);
    }
}
