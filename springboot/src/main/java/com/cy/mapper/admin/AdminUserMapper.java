package com.cy.mapper.admin;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cy.entity.admin.AdminUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 管理员用户Mapper
 */
@Mapper
public interface AdminUserMapper extends BaseMapper<AdminUser> {
}
