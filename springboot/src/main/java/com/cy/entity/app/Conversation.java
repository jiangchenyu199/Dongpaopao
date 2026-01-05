package com.cy.entity.app;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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

    private LocalDateTime lastMessageSendTime;

    private LocalDateTime lastMessageReadTime;

    private String status;
}
