package com.cy.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cy.entity.SysUser;

public interface AdminSysUserService {

    Page<SysUser> list(Integer pageNum, Integer pageSize, String keyword);

    SysUser getById(Long id);

    void save(SysUser user);

    void removeById(Long id);
}
