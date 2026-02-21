package com.cy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cy.entity.AppNotice;
import com.cy.mapper.AppNoticeMapper;
import com.cy.service.AppNoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppNoticeServiceImpl implements AppNoticeService {

    private final AppNoticeMapper appNoticeMapper;

    @Override
    public List<AppNotice> listForApp() {
        return appNoticeMapper.selectList(
                new LambdaQueryWrapper<AppNotice>()
                        .eq(AppNotice::getStatus, 1)
                        .orderByAsc(AppNotice::getSort));
    }

    @Override
    public List<AppNotice> listForAdmin() {
        return appNoticeMapper.selectList(
                new LambdaQueryWrapper<AppNotice>().orderByAsc(AppNotice::getSort));
    }

    @Override
    public void save(AppNotice notice) {
        if (notice.getSort() == null) {
            notice.setSort(0);
        }
        if (notice.getStatus() == null) {
            notice.setStatus(1);
        }
        if (notice.getId() == null) {
            appNoticeMapper.insert(notice);
        } else {
            appNoticeMapper.updateById(notice);
        }
    }

    @Override
    public void removeById(Long id) {
        appNoticeMapper.deleteById(id);
    }
}
