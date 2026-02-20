package com.cy.service;

import com.cy.common.R;

import java.util.List;

/**
 * 系统配置服务
 */
public interface SysConfigService {

    R list();

    R update(String configKey, String configValue);
}
