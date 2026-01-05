package com.github.solanej.service.admin.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.solanej.entity.admin.AdminSystemConfig;
import com.github.solanej.mapper.admin.AdminSystemConfigMapper;
import com.github.solanej.service.admin.AdminSystemConfigService;
import lombok.RequiredArgsConstructor;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminSystemConfigServiceImpl extends ServiceImpl<AdminSystemConfigMapper, AdminSystemConfig> implements AdminSystemConfigService {

    @Override
    public String getConfigValue(String configKey) {
        AdminSystemConfig config = baseMapper.selectOne(new LambdaQueryWrapper<AdminSystemConfig>()
                .eq(AdminSystemConfig::getConfigKey, configKey));
        return config != null ? config.getConfigValue() : null;
    }

    @Override
    public void setConfigValue(String configKey, String configValue) {
        AdminSystemConfig config = baseMapper.selectOne(new LambdaQueryWrapper<AdminSystemConfig>()
                .eq(AdminSystemConfig::getConfigKey, configKey));
        if (config != null) {
            config.setConfigValue(configValue);
            config.setUpdateTime(LocalDateTime.now());
            baseMapper.updateById(config);
        } else {
            config = new AdminSystemConfig();
            config.setConfigKey(configKey);
            config.setConfigValue(configValue);
            config.setCreateTime(LocalDateTime.now());
            baseMapper.insert(config);
        }
    }

    @Override
    public Map<String, Object> getSystemInfo() {
        Map<String, Object> systemInfo = new HashMap<>();
        
        try {
            SystemInfo systemInfoObj = new SystemInfo();
            HardwareAbstractionLayer hardware = systemInfoObj.getHardware();
            OperatingSystem os = systemInfoObj.getOperatingSystem();
            
            CentralProcessor processor = hardware.getProcessor();
            GlobalMemory memory = hardware.getMemory();
            
            Map<String, Object> cpuInfo = new HashMap<>();
            cpuInfo.put("model", processor.getProcessorIdentifier().getName());
            cpuInfo.put("cores", processor.getLogicalProcessorCount());
            cpuInfo.put("physicalCores", processor.getPhysicalProcessorCount());
            cpuInfo.put("systemCpuLoad", String.format("%.2f%%", processor.getSystemCpuLoad(1000) * 100));
            cpuInfo.put("userCpuLoad", String.format("%.2f%%", processor.getSystemCpuLoad(1000) * 100));
            systemInfo.put("cpu", cpuInfo);
            
            Map<String, Object> memoryInfo = new HashMap<>();
            long totalMemory = memory.getTotal();
            long availableMemory = memory.getAvailable();
            long usedMemory = totalMemory - availableMemory;
            
            memoryInfo.put("total", formatBytes(totalMemory));
            memoryInfo.put("available", formatBytes(availableMemory));
            memoryInfo.put("used", formatBytes(usedMemory));
            memoryInfo.put("usage", String.format("%.2f%%", (double) usedMemory / totalMemory * 100));
            systemInfo.put("memory", memoryInfo);
            
            Map<String, Object> osInfo = new HashMap<>();
            osInfo.put("name", os.toString());
            osInfo.put("version", os.getVersionInfo().toString());
            osInfo.put("arch", System.getProperty("os.arch"));
            systemInfo.put("os", osInfo);
            
            Map<String, Object> jvmInfo = new HashMap<>();
            Runtime runtime = Runtime.getRuntime();
            long jvmTotalMemory = runtime.totalMemory();
            long jvmFreeMemory = runtime.freeMemory();
            long jvmUsedMemory = jvmTotalMemory - jvmFreeMemory;
            long jvmMaxMemory = runtime.maxMemory();
            
            jvmInfo.put("total", formatBytes(jvmTotalMemory));
            jvmInfo.put("free", formatBytes(jvmFreeMemory));
            jvmInfo.put("used", formatBytes(jvmUsedMemory));
            jvmInfo.put("max", formatBytes(jvmMaxMemory));
            jvmInfo.put("usage", String.format("%.2f%%", (double) jvmUsedMemory / jvmMaxMemory * 100));
            systemInfo.put("jvm", jvmInfo);
            
        } catch (Exception e) {
            systemInfo.put("error", "获取系统信息失败: " + e.getMessage());
        }
        
        return systemInfo;
    }
    
    private String formatBytes(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", bytes / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024));
        }
    }
}
