package com.cy.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 角色菜单关联
 */
@Data
@TableName("sys_role_menu")
public class SysRoleMenu {

    private Long roleId;
    private Long menuId;
}
