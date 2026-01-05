package com.cy.service.admin;

import com.cy.common.R;

public interface AdminOrderService {

    R list(Integer pageNum, Integer pageSize, String keyword, String status);

    R detail(String oid);
}
