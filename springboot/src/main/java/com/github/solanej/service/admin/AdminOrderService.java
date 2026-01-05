package com.github.solanej.service.admin;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.solanej.common.R;

public interface AdminOrderService {

    R list(Integer pageNum, Integer pageSize, String keyword, String status);

    R detail(String oid);
}
