package com.cy.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.cy.common.R;
import com.cy.entity.SysConfig;
import com.cy.mapper.SysConfigMapper;
import com.cy.service.SysConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SysConfigServiceImpl implements SysConfigService {

    private final SysConfigMapper sysConfigMapper;

    @Override
    public R list() {
        List<SysConfig> list = sysConfigMapper.selectList(null);
        return R.success(list);
    }

    @Override
    public R update(String configKey, String configValue) {
        if (!StringUtils.hasText(configKey)) {
            return R.error("配置键不能为空");
        }
        int rows = sysConfigMapper.update(null, new LambdaUpdateWrapper<SysConfig>()
                .set(SysConfig::getConfigValue, configValue)
                .eq(SysConfig::getConfigKey, configKey));
        return rows > 0 ? R.success() : R.error("更新失败，配置不存在");
    }
}
