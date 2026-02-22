package com.cy.service;

import com.cy.entity.AppBusinessPromotion;

import java.util.List;

public interface AppBusinessPromotionService {

    List<AppBusinessPromotion> listForApp();

    List<AppBusinessPromotion> listForAdmin();

    void save(AppBusinessPromotion entity);

    void removeById(Long id);
}
