package com.github.solanej.service.app.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.solanej.common.R;
import com.github.solanej.entity.app.School;
import com.github.solanej.mapper.app.SchoolMapper;
import com.github.solanej.service.app.SchoolService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SchoolServiceImpl implements SchoolService {

    private final SchoolMapper schoolMapper;

    @Override
    public R list() {
        return R.success(schoolMapper.selectList(null));
    }

    @Override
    public R search(String keyword) {
        return R.success(schoolMapper.selectList(new LambdaQueryWrapper<School>().like(School::getSname, keyword)));
    }
}
