package com.github.solanej.service.admin.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.solanej.common.R;
import com.github.solanej.entity.admin.AdminUser;
import com.github.solanej.mapper.admin.AdminUserMapper;
import com.github.solanej.service.admin.AdminUserService;
import com.github.solanej.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.github.solanej.common.R.success;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl extends ServiceImpl<AdminUserMapper, AdminUser> implements AdminUserService {

    private final JwtUtil jwtUtil;

    @Override
    public R addUser(JSONObject jsonBody) {
        AdminUser adminUser = jsonBody.toJavaObject(AdminUser.class);
        adminUser.setCreateTime(LocalDateTime.now());
        adminUser.setUpdateTime(LocalDateTime.now());
        baseMapper.insert(adminUser);
        return success();
    }

    @Override
    public R updateUser(JSONObject jsonBody) {
        AdminUser adminUser = jsonBody.toJavaObject(AdminUser.class);
        if (adminUser.getId() == null) {
            return R.error("用户ID不能为空");
        }
        adminUser.setUpdateTime(LocalDateTime.now());
        if (adminUser.getPassword() != null && !adminUser.getPassword().isEmpty()) {
            baseMapper.updateById(adminUser);
        } else {
            adminUser.setPassword(null);
            baseMapper.updateById(adminUser);
        }
        return success();
    }

    @Override
    public R deleteUser(Long id) {
        baseMapper.deleteById(id);
        return success();
    }

    @Override
    public R login(JSONObject jsonBody) {
        String username = jsonBody.getString("username");
        String password = jsonBody.getString("password");

        AdminUser adminUser = baseMapper.selectOne(new LambdaQueryWrapper<AdminUser>()
                .eq(AdminUser::getUsername, username)
                .eq(AdminUser::getPassword, password));
        if (adminUser == null) {
            return R.error("用户名或密码错误");
        }

        String token = jwtUtil.generateToken(adminUser.getId(), adminUser.getUsername());

        LocalDateTime lastLoginTime = adminUser.getLastLoginTime();
        adminUser.setLastLoginTime(LocalDateTime.now());
        baseMapper.updateById(adminUser);
        adminUser.setLastLoginTime(lastLoginTime);

        JSONObject data = new JSONObject();
        data.put("token", token);
        data.put("userInfo", adminUser);

        return success(data);
    }

    @Override
    public R list(Integer pageNum, Integer pageSize, String keyword) {
        return success(baseMapper.selectPage(new Page<>(pageNum, pageSize), new LambdaQueryWrapper<AdminUser>()
                .like(AdminUser::getUsername, keyword)
                .or()
                .like(AdminUser::getNickname, keyword)));
    }
}
