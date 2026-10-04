package com.code.secondhandbooktradingplatform.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("book")
public class Book {
    private Long id;
    private Long sellerId;
    private Long categoryId;
    private String title;
    private String author;
    private String isbn;
    private BigDecimal price;
    private BigDecimal originalPrice;
    /** 成色：全新/九成新/八成新/七成新及以下 */
    private String conditionLevel;
    private String description;
    private String cover;
    /** ON_SALE-在售 SOLD-已售 OFF_SHELF-已下架 */
    private String status;
    private LocalDateTime createTime;
}
