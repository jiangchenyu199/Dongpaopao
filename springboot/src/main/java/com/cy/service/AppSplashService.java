package com.cy.service;

import com.cy.entity.AppSplash;

public interface AppSplashService {

    AppSplash getCurrent();

    AppSplash getForAdmin();

    void save(AppSplash splash);
}
