package com.cy.mapper.app;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cy.entity.app.Log;
import org.apache.ibatis.annotations.Mapper;

/**
 * 日志Mapper接口
 * 继承BaseMapper，使用MyBatis-Plus提供的基础CRUD操作
 */
@Mapper
public interface LogMapper extends BaseMapper<Log> {
    
}