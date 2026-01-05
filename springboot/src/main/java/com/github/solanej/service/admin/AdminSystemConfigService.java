package com.github.solanej.service.admin;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.solanej.entity.admin.AdminSystemConfig;

import java.util.Map;

public interface AdminSystemConfigService extends IService<AdminSystemConfig> {

    /**
     * 根据配置键获取配置值
     *
     * @param configKey 配置键
     * @return 配置值
     */
    String getConfigValue(String configKey);
    /**
     * 根据配置键设置配置值
     *
     * @param configKey  配置键
     * @param configValue 配置值
     */
    void setConfigValue(String configKey, String configValue);

    /**
     * 获取系统信息
     *
     * @return 系统信息
     */
    Map<String, Object> getSystemInfo();
}
