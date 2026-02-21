package com.cy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cy.entity.SysRole;
import com.cy.entity.SysRoleMenu;
import com.cy.mapper.SysRoleMapper;
import com.cy.mapper.SysRoleMenuMapper;
import com.cy.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl implements SysRoleService {

    private final SysRoleMapper sysRoleMapper;
    private final SysRoleMenuMapper sysRoleMenuMapper;

    @Override
    public List<SysRole> list() {
        return sysRoleMapper.selectList(
                new LambdaQueryWrapper<SysRole>().orderByAsc(SysRole::getSort));
    }

    @Override
    public SysRole getById(Long id) {
        SysRole role = sysRoleMapper.selectById(id);
        if (role != null) {
            List<Long> menuIds = sysRoleMenuMapper.selectList(
                    new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, id))
                    .stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());
            role.setMenuIds(menuIds);
        }
        return role;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(SysRole role, List<Long> menuIds) {
        if (role.getId() == null) {
            sysRoleMapper.insert(role);
        } else {
            sysRoleMapper.updateById(role);
        }
        sysRoleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, role.getId()));
        if (menuIds != null && !menuIds.isEmpty()) {
            for (Long menuId : menuIds) {
                SysRoleMenu rm = new SysRoleMenu();
                rm.setRoleId(role.getId());
                rm.setMenuId(menuId);
                sysRoleMenuMapper.insert(rm);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Long id) {
        sysRoleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, id));
        sysRoleMapper.deleteById(id);
    }
}
