package com.cy.service;

import com.cy.entity.AppBanner;

import java.util.List;

public interface AppBannerService {

    List<AppBanner> listForApp();

    List<AppBanner> listForAdmin();

    void save(AppBanner entity);

    void removeById(Long id);
}
