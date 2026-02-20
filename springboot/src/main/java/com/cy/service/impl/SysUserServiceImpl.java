package com.cy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cy.common.R;
import com.cy.entity.User;
import com.cy.mapper.UserMapper;
import com.cy.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements SysUserService {

    private final UserMapper userMapper;

    @Override
    public R list(Integer pageNum, Integer pageSize, String keyword) {
        Page<User> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            qw.and(w -> w
                    .like(User::getNickname, keyword)
                    .or().like(User::getPhone, keyword)
                    .or().like(User::getUid, keyword));
        }
        qw.orderByDesc(User::getCtime);
        Page<User> result = userMapper.selectPage(page, qw);
        return R.success(result);
    }

    @Override
    public R updateStatus(String uid, Integer status) {
        if (uid == null || uid.isBlank()) {
            return R.error("用户ID不能为空");
        }
        if (status == null || (status != 0 && status != 1)) {
            return R.error("状态值无效");
        }
        int rows = userMapper.update(null, new LambdaUpdateWrapper<User>()
                .set(User::getStatus, status)
                .eq(User::getUid, uid));
        return rows > 0 ? R.success() : R.error("更新失败");
    }
}
