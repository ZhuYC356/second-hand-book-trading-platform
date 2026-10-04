package com.code.secondhandbooktradingplatform.vo;

import com.code.secondhandbooktradingplatform.entity.Book;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BookVO extends Book {
    private String sellerName;
    private String categoryName;
}
