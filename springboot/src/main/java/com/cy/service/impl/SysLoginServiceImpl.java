package com.cy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cy.common.R;
import com.cy.entity.SysMenu;
import com.cy.entity.SysUser;
import com.cy.entity.SysUserRole;
import com.cy.mapper.SysUserMapper;
import com.cy.mapper.SysUserRoleMapper;
import com.cy.service.SysLoginService;
import com.cy.service.SysMenuService;
import com.cy.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysLoginServiceImpl implements SysLoginService {

    private final SysUserMapper sysUserMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysMenuService sysMenuService;
    private final JwtUtil jwtUtil;

    @Override
    public R login(String username, String password) {
        SysUser user = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
        if (user == null) {
            return R.error("用户名或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            return R.error("账号已停用");
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(password, user.getPassword())) {
            return R.error("用户名或密码错误");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("username", user.getUsername());
        data.put("nickname", user.getNickname());
        return R.success(data);
    }

    @Override
    public R getInfo(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            return R.error("用户不存在");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            return R.error("账号已停用");
        }
        List<Long> roleIds = sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId))
                .stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
        List<Long> menuIds = sysMenuService.listMenuIdsByRoleIds(roleIds);
        List<SysMenu> menuTree = sysMenuService.listTree();
        List<SysMenu> filteredTree = filterMenuTreeByIds(menuTree, menuIds);

        Map<String, Object> data = new HashMap<>();
        user.setPassword(null);
        data.put("user", user);
        data.put("roleIds", roleIds);
        data.put("menus", filteredTree);
        return R.success(data);
    }

    private List<SysMenu> filterMenuTreeByIds(List<SysMenu> tree, List<Long> menuIds) {
        if (tree == null || tree.isEmpty()) return tree;
        return tree.stream()
                .map(m -> {
                    SysMenu copy = new SysMenu();
                    copy.setId(m.getId());
                    copy.setParentId(m.getParentId());
                    copy.setName(m.getName());
                    copy.setPath(m.getPath());
                    copy.setComponent(m.getComponent());
                    copy.setPermission(m.getPermission());
                    copy.setType(m.getType());
                    copy.setIcon(m.getIcon());
                    copy.setSort(m.getSort());
                    copy.setVisible(m.getVisible());
                    copy.setStatus(m.getStatus());
                    List<SysMenu> childFiltered = filterMenuTreeByIds(m.getChildren(), menuIds);
                    copy.setChildren(childFiltered);
                    boolean selfOrChildAllowed = menuIds.contains(m.getId()) || (childFiltered != null && !childFiltered.isEmpty());
                    return selfOrChildAllowed ? copy : null;
                })
                .filter(m -> m != null)
                .collect(Collectors.toList());
    }
}
