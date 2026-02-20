package com.cy.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("user")
public class User {

    @TableId
    private String uid;

    private String openid;

    @Max(value = 20, message = "昵称长度不能超过20")
    private String nickname;

    private int sex;

    private int sid;

    private String phone;

    private String avatar;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime ctime;

    private BigDecimal balance;

    /** 0禁用 1正常 */
    private Integer status;
}
