package com.cy.controller.app;

import com.cy.common.R;
import com.cy.service.app.SchoolService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/school")
@RequiredArgsConstructor
public class SchoolController {

    private final SchoolService schoolService;

    @RequestMapping("/list")
    public R list() {
        return schoolService.list();
    }


    /**
     * 搜索学校
     *
     * @param keyword 搜索关键词
     * @return 搜索结果
     */
    @RequestMapping("/search")
    public R search(@RequestParam("keyword") String keyword) {
        return schoolService.search(keyword);
    }
}
