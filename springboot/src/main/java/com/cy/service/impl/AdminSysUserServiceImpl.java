package com.cy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cy.entity.SysUser;
import com.cy.entity.SysUserRole;
import com.cy.mapper.SysUserMapper;
import com.cy.mapper.SysUserRoleMapper;
import com.cy.service.AdminSysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminSysUserServiceImpl implements AdminSysUserService {

    private final SysUserMapper sysUserMapper;
    private final SysUserRoleMapper sysUserRoleMapper;

    @Override
    public Page<SysUser> list(Integer pageNum, Integer pageSize, String keyword) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysUser> qw = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            qw.and(w -> w.like(SysUser::getUsername, keyword)
                    .or().like(SysUser::getNickname, keyword));
        }
        qw.orderByDesc(SysUser::getId);
        return sysUserMapper.selectPage(page, qw);
    }

    @Override
    public SysUser getById(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user != null) {
            List<Long> roleIds = sysUserRoleMapper.selectList(
                    new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, id))
                    .stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
            user.setRoleIds(roleIds);
        }
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(SysUser user) {
        if (user.getId() == null) {
            if (StringUtils.hasText(user.getPassword())) {
                user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
            }
            sysUserMapper.insert(user);
        } else {
            SysUser exist = sysUserMapper.selectById(user.getId());
            if (exist == null) return;
            if (StringUtils.hasText(user.getPassword())) {
                user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
            } else {
                user.setPassword(exist.getPassword());
            }
            sysUserMapper.updateById(user);
        }
        sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, user.getId()));
        if (user.getRoleIds() != null && !user.getRoleIds().isEmpty()) {
            for (Long roleId : user.getRoleIds()) {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(user.getId());
                ur.setRoleId(roleId);
                sysUserRoleMapper.insert(ur);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Long id) {
        sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, id));
        sysUserMapper.deleteById(id);
    }
}
