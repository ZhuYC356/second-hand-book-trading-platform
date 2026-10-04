package com.code.secondhandbooktradingplatform.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BookDTO {
    private Long id;
    private Long categoryId;
    private String title;
    private String author;
    private String isbn;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String conditionLevel;
    private String description;
    private String cover;
}
