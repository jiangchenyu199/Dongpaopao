package com.cy.mapper.app;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cy.entity.app.Conversation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ConversationMapper extends BaseMapper<Conversation> {

    Page<JSONObject> listConversation(Page<JSONObject> page, @Param("uid") String uid);
}
