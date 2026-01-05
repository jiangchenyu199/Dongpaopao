package com.cy.mapper.app;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cy.entity.app.Message;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {

}

