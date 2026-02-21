package com.cy.service;

import com.cy.entity.AppNotice;

import java.util.List;

public interface AppNoticeService {

    List<AppNotice> listForApp();

    List<AppNotice> listForAdmin();

    void save(AppNotice notice);

    void removeById(Long id);
}
