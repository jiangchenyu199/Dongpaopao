package com.cy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cy.common.R;
import com.cy.entity.SysUser;
import com.cy.mapper.SysUserMapper;
import com.cy.service.SysLoginService;
import com.cy.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SysLoginServiceImpl implements SysLoginService {

    private final SysUserMapper sysUserMapper;
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
}
