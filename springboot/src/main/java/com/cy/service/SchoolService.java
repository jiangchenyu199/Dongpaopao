package com.cy.service;

import com.cy.common.R;
import com.cy.entity.School;

public interface SchoolService {

    R list();

    R search(String keyword);

    void saveSchool(School school);

    void removeSchool(Integer sid);
}
