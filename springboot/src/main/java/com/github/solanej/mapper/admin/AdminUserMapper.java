package com.github.solanej.mapper.admin;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.solanej.entity.admin.AdminUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 管理员用户Mapper
 */
@Mapper
public interface AdminUserMapper extends BaseMapper<AdminUser> {
}
