package com.code.secondhandbooktradingplatform.vo;

import lombok.Data;

import java.math.BigDecimal;

/** 近7日订单趋势点 */
@Data
public class TrendPoint {
    private String date;
    private Long count;
    private BigDecimal amount;
}
