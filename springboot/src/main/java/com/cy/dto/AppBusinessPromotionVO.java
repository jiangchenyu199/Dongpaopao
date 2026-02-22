package com.cy.dto;

import lombok.Data;

import java.util.List;

@Data
public class AppBusinessPromotionVO {
    private Long id;
    private String name;
    private String description;
    private String image;
    private List<String> tags;
    private String minPrice;
}
