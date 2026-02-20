package com.cy.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 菜单/权限
 */
@Data
@TableName("sys_menu")
public class SysMenu {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 0 为顶级 */
    private Long parentId;

    private String name;
    private String path;
    private String component;
    private String permission;

    /** M目录 C菜单 F按钮 */
    private String type;
    private String icon;
    private Integer sort;

    /** 0隐藏 1显示 */
    private Integer visible;

    /** 0停用 1正常 */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private List<SysMenu> children;
}
