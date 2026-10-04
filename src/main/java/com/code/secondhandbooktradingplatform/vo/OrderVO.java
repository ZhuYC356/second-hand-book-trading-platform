package com.code.secondhandbooktradingplatform.vo;

import com.code.secondhandbooktradingplatform.entity.Order;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class OrderVO extends Order {
    private String bookTitle;
    private String bookCover;
    private String buyerName;
    private String sellerName;
}
