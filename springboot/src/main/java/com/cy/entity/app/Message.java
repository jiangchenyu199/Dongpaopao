package com.cy.entity.app;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Message {

    @TableId
    private String mid;

    private String cid;

    private String sender;

    private String receiver;

    private String type;

    private String content;

    private String status;

    private LocalDateTime sendTime;

    private LocalDateTime readTime;
}

