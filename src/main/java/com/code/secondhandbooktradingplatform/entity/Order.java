package com.code.secondhandbooktradingplatform.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("orders")
public class Order {
    private Long id;
    private String orderNo;
    private Long bookId;
    private Long buyerId;
    private Long sellerId;
    private BigDecimal price;
    /** PENDING-待付款 COMPLETED-已完成 CANCELLED-已取消 */
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime finishTime;
}
