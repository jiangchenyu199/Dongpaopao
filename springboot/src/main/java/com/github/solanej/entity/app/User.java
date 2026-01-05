package com.github.solanej.entity.app;

import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
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

    private Timestamp ctime;

    private BigDecimal balance;
}
