package com.github.solanej.service.admin;

import com.alibaba.fastjson2.JSONObject;
import com.github.solanej.common.R;

public interface AdminUserService {

    R addUser(JSONObject jsonBody);

    R updateUser(JSONObject jsonBody);

    /**
     * 删除管理员
     */
    R deleteUser(Long id);

    /**
     * 管理员登录
     */
    R login(JSONObject jsonBody);

    /**
     * 管理员列表
     */
    R list(Integer pageNum, Integer pageSize, String keyword);
}
