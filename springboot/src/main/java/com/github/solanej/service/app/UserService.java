package com.github.solanej.service.app;

import com.alibaba.fastjson2.JSONObject;
import com.github.solanej.common.R;
import com.github.solanej.entity.app.User;
import org.springframework.web.multipart.MultipartFile;

public interface UserService{

    R updateUserInfo(User user);

    R getUserInfo(String uId);

    R getBalance(String uid);

    R uploadAvatar(MultipartFile file, String uid);

    R recharge(JSONObject params);

     R withdraw(JSONObject params);
}
