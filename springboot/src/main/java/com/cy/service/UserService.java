package com.cy.service;

import com.alibaba.fastjson2.JSONObject;
import com.cy.common.R;
import com.cy.entity.User;
import org.springframework.web.multipart.MultipartFile;

public interface UserService{

    R updateUserInfo(User user);

    R getUserInfo(String uId);

    R getBalance(String uid);

    R uploadAvatar(MultipartFile file, String uid);

    R recharge(JSONObject params);

    R withdraw(JSONObject params);
}
