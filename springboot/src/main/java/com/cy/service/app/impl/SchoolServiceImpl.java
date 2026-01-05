package com.cy.service.app.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cy.common.R;
import com.cy.entity.app.School;
import com.cy.mapper.app.SchoolMapper;
import com.cy.service.app.SchoolService;
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
