package com.cy.controller;

import com.cy.common.R;
import com.cy.entity.School;
import com.cy.service.SchoolService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/school")
@RequiredArgsConstructor
public class AdminSchoolController {

    private final SchoolService schoolService;

    @GetMapping("/list")
    public R list() {
        return schoolService.list();
    }

    @PostMapping("/save")
    public R save(@RequestBody School school) {
        schoolService.saveSchool(school);
        return R.success();
    }

    @DeleteMapping("/delete/{sid}")
    public R delete(@PathVariable Integer sid) {
        schoolService.removeSchool(sid);
        return R.success();
    }
}
