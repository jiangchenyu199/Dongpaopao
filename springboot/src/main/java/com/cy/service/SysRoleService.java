package com.cy.service;

import com.cy.entity.SysRole;

import java.util.List;

public interface SysRoleService {

    List<SysRole> list();

    SysRole getById(Long id);

    void save(SysRole role, List<Long> menuIds);

    void removeById(Long id);
}
