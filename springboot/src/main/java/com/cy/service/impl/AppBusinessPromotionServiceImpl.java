package com.cy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cy.entity.AppBusinessPromotion;
import com.cy.mapper.AppBusinessPromotionMapper;
import com.cy.service.AppBusinessPromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppBusinessPromotionServiceImpl implements AppBusinessPromotionService {

    private final AppBusinessPromotionMapper appBusinessPromotionMapper;

    @Override
    public List<AppBusinessPromotion> listForApp() {
        return appBusinessPromotionMapper.selectList(
                new LambdaQueryWrapper<AppBusinessPromotion>()
                        .eq(AppBusinessPromotion::getStatus, 1)
                        .orderByAsc(AppBusinessPromotion::getSort));
    }

    @Override
    public List<AppBusinessPromotion> listForAdmin() {
        return appBusinessPromotionMapper.selectList(
                new LambdaQueryWrapper<AppBusinessPromotion>().orderByAsc(AppBusinessPromotion::getSort));
    }

    @Override
    public void save(AppBusinessPromotion entity) {
        if (entity.getSort() == null) entity.setSort(0);
        if (entity.getStatus() == null) entity.setStatus(1);
        if (entity.getId() == null) {
            appBusinessPromotionMapper.insert(entity);
        } else {
            appBusinessPromotionMapper.updateById(entity);
        }
    }

    @Override
    public void removeById(Long id) {
        appBusinessPromotionMapper.deleteById(id);
    }
}
