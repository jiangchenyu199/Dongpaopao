package com.cy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cy.common.R;
import com.cy.entity.School;
import com.cy.mapper.SchoolMapper;
import com.cy.service.SchoolService;
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

    @Override
    public void saveSchool(School school) {
        if (school.getSid() == null) {
            schoolMapper.insert(school);
        } else {
            schoolMapper.updateById(school);
        }
    }

    @Override
    public void removeSchool(Integer sid) {
        schoolMapper.deleteById(sid);
    }
}
