package com.cy.entity.app;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Conversation {

    @TableId(type = IdType.ASSIGN_ID)
    private String cid;

    private String oid;

    private Integer messageCounts;

    private LocalDateTime createTime;

    private String lastMessageContent;
    /**
     * 最后一条消息发送时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastMessageSendTime;

    /**
     * 最后一条消息接收时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastMessageReadTime;

    private String status;
}
