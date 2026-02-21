package com.cy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cy.entity.AppSplash;
import com.cy.mapper.AppSplashMapper;
import com.cy.service.AppSplashService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppSplashServiceImpl implements AppSplashService {

    private final AppSplashMapper appSplashMapper;

    @Override
    public AppSplash getCurrent() {
        return appSplashMapper.selectOne(
                new LambdaQueryWrapper<AppSplash>()
                        .eq(AppSplash::getStatus, 1)
                        .orderByDesc(AppSplash::getId)
                        .last("LIMIT 1"));
    }

    @Override
    public AppSplash getForAdmin() {
        AppSplash one = appSplashMapper.selectOne(
                new LambdaQueryWrapper<AppSplash>().orderByDesc(AppSplash::getId).last("LIMIT 1"));
        return one;
    }

    @Override
    public void save(AppSplash splash) {
        if (splash.getId() == null) {
            appSplashMapper.insert(splash);
        } else {
            appSplashMapper.updateById(splash);
        }
    }
}
