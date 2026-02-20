package com.cy.service;

import com.cy.common.R;

/**
 * 管理端订单服务
 */
public interface AdminOrderService {

    R list(Integer pageNum, Integer pageSize, String keyword, String status);

    R detail(String oid);
}
