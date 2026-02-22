package com.cy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cy.entity.AppBanner;
import com.cy.mapper.AppBannerMapper;
import com.cy.service.AppBannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppBannerServiceImpl implements AppBannerService {

    private final AppBannerMapper appBannerMapper;

    @Override
    public List<AppBanner> listForApp() {
        return appBannerMapper.selectList(
                new LambdaQueryWrapper<AppBanner>()
                        .eq(AppBanner::getStatus, 1)
                        .orderByAsc(AppBanner::getSort));
    }

    @Override
    public List<AppBanner> listForAdmin() {
        return appBannerMapper.selectList(
                new LambdaQueryWrapper<AppBanner>().orderByAsc(AppBanner::getSort));
    }

    @Override
    public void save(AppBanner entity) {
        if (entity.getSort() == null) entity.setSort(0);
        if (entity.getStatus() == null) entity.setStatus(1);
        if (entity.getId() == null) {
            appBannerMapper.insert(entity);
        } else {
            appBannerMapper.updateById(entity);
        }
    }

    @Override
    public void removeById(Long id) {
        appBannerMapper.deleteById(id);
    }
}
