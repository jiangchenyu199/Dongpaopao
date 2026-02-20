package com.cy.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cy.entity.SysUser;
import com.cy.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 首次启动时初始化默认管理员
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SysUserInitRunner implements ApplicationRunner {

    private final SysUserMapper sysUserMapper;

    @Override
    public void run(ApplicationArguments args) {
        long count = sysUserMapper.selectCount(null);
        if (count > 0)
            return;
        SysUser admin = new SysUser();
        admin.setUsername("admin");
        admin.setPassword(new BCryptPasswordEncoder().encode("admin123"));
        admin.setNickname("管理员");
        admin.setStatus(1);
        sysUserMapper.insert(admin);
        log.info("已初始化默认管理员: admin / admin123");
    }
}
