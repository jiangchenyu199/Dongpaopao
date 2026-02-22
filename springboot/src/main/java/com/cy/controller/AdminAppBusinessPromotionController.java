package com.cy.controller;

import com.cy.common.R;
import com.cy.entity.AppBusinessPromotion;
import com.cy.service.AppBusinessPromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/app/business-promotion")
@RequiredArgsConstructor
public class AdminAppBusinessPromotionController {

    private final AppBusinessPromotionService appBusinessPromotionService;

    @GetMapping("/list")
    public R list() {
        List<AppBusinessPromotion> list = appBusinessPromotionService.listForAdmin();
        return R.success(list);
    }

    @PostMapping("/save")
    public R save(@RequestBody AppBusinessPromotion entity) {
        appBusinessPromotionService.save(entity);
        return R.success();
    }

    @DeleteMapping("/delete/{id}")
    public R delete(@PathVariable Long id) {
        appBusinessPromotionService.removeById(id);
        return R.success();
    }
}
