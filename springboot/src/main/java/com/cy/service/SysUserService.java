package com.cy.service;

import com.cy.common.R;

/**
 * 管理端 C 端用户管理
 */
public interface SysUserService {

    R list(Integer pageNum, Integer pageSize, String keyword);

    R updateStatus(String uid, Integer status);
}
