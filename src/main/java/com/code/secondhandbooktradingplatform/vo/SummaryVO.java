package com.code.secondhandbooktradingplatform.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SummaryVO {
    private Long userCount;
    private Long bookCount;
    private Long onSaleCount;
    private Long orderCount;
    private BigDecimal totalAmount;
}
