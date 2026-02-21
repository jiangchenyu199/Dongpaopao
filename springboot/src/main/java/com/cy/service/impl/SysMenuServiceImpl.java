package com.cy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cy.entity.SysMenu;
import com.cy.entity.SysRoleMenu;
import com.cy.mapper.SysMenuMapper;
import com.cy.mapper.SysRoleMenuMapper;
import com.cy.service.SysMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysMenuServiceImpl implements SysMenuService {

    private final SysMenuMapper sysMenuMapper;
    private final SysRoleMenuMapper sysRoleMenuMapper;

    @Override
    public List<SysMenu> listTree() {
        List<SysMenu> all = sysMenuMapper.selectList(
                new LambdaQueryWrapper<SysMenu>()
                        .eq(SysMenu::getStatus, 1)
                        .orderByAsc(SysMenu::getSort));
        return buildTree(all, 0L);
    }

    private List<SysMenu> buildTree(List<SysMenu> list, Long parentId) {
        List<SysMenu> result = new ArrayList<>();
        for (SysMenu m : list) {
            if (parentId.equals(m.getParentId() == null ? 0L : m.getParentId())) {
                m.setChildren(buildTree(list, m.getId()));
                result.add(m);
            }
        }
        return result;
    }

    @Override
    public List<Long> listMenuIdsByRoleIds(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) return new ArrayList<>();
        return roleIds.stream()
                .flatMap(roleId -> sysRoleMenuMapper.selectList(
                        new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId)).stream())
                .map(SysRoleMenu::getMenuId)
                .distinct()
                .collect(Collectors.toList());
    }
}
