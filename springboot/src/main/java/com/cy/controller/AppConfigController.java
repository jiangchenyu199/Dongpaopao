package com.cy.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.cy.common.R;
import com.cy.dto.AppBusinessPromotionVO;
import com.cy.entity.AppBusinessPromotion;
import com.cy.entity.AppNotice;
import com.cy.entity.AppSplash;
import com.cy.service.AppBannerService;
import com.cy.service.AppBusinessPromotionService;
import com.cy.service.AppNoticeService;
import com.cy.service.AppSplashService;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/app")
@RequiredArgsConstructor
public class AppConfigController {

    private final AppSplashService appSplashService;
    private final AppNoticeService appNoticeService;
    private final AppBusinessPromotionService appBusinessPromotionService;
    private final AppBannerService appBannerService;
    private final ObjectMapper objectMapper;

    @GetMapping("/splash")
    public R splash() {
        AppSplash current = appSplashService.getCurrent();
        return R.success(current);
    }

    @GetMapping("/notices")
    public R notices() {
        List<AppNotice> list = appNoticeService.listForApp();
        return R.success(list);
    }

    @GetMapping("/business-promotion")
    public R businessPromotion() {
        List<AppBusinessPromotion> list = appBusinessPromotionService.listForApp();
        List<AppBusinessPromotionVO> voList = new ArrayList<>(list.size());
        for (AppBusinessPromotion e : list) {
            AppBusinessPromotionVO vo = new AppBusinessPromotionVO();
            vo.setId(e.getId());
            vo.setName(e.getName());
            vo.setDescription(e.getDescription());
            vo.setImage(e.getImage());
            vo.setMinPrice(e.getMinPrice());
            if (StringUtils.hasText(e.getTags())) {
                try {
                    vo.setTags(objectMapper.readValue(e.getTags(), new TypeReference<List<String>>() {}));
                } catch (Exception ex) {
                    vo.setTags(Collections.emptyList());
                }
            } else {
                vo.setTags(Collections.emptyList());
            }
            voList.add(vo);
        }
        return R.success(voList);
    }

    @GetMapping("/banner")
    public R banner() {
        return R.success(appBannerService.listForApp());
    }
}
