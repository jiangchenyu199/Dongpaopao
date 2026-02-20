package com.cy.controller;

import com.cy.common.R;
import com.cy.service.SchoolService;
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

    @RequestMapping("/search")
    public R search(@RequestParam("keyword") String keyword) {
        return schoolService.search(keyword);
    }
}
