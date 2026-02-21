package com.cy.service;

import com.cy.entity.SysMenu;

import java.util.List;

public interface SysMenuService {

    List<SysMenu> listTree();

    List<Long> listMenuIdsByRoleIds(List<Long> roleIds);
}
