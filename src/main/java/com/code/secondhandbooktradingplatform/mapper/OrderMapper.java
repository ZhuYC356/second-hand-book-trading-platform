package com.code.secondhandbooktradingplatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.code.secondhandbooktradingplatform.entity.Order;
import com.code.secondhandbooktradingplatform.vo.OrderVO;
import com.code.secondhandbooktradingplatform.vo.TopBook;
import com.code.secondhandbooktradingplatform.vo.TrendPoint;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderMapper extends BaseMapper<Order> {

    @Select("""
            <script>
            SELECT o.*, b.title AS bookTitle, b.cover AS bookCover,
                   ub.nickname AS buyerName, us.nickname AS sellerName
            FROM orders o
            LEFT JOIN book b ON o.book_id = b.id
            LEFT JOIN user ub ON o.buyer_id = ub.id
            LEFT JOIN user us ON o.seller_id = us.id
            <where>
                <if test="buyerId != null">AND o.buyer_id = #{buyerId}</if>
                <if test="sellerId != null">AND o.seller_id = #{sellerId}</if>
                <if test="status != null and status != ''">AND o.status = #{status}</if>
                <if test="kw != null and kw != ''">AND b.title LIKE CONCAT('%', #{kw}, '%')</if>
            </where>
            ORDER BY o.create_time DESC
            </script>
            """)
    Page<OrderVO> selectOrderPage(Page<OrderVO> page,
                                  @Param("buyerId") Long buyerId,
                                  @Param("sellerId") Long sellerId,
                                  @Param("status") String status,
                                  @Param("kw") String kw);

    @Select("SELECT DATE_FORMAT(create_time, '%Y-%m-%d') AS date, COUNT(*) AS count, " +
            "IFNULL(SUM(CASE WHEN status = 'COMPLETED' THEN price ELSE 0 END), 0) AS amount " +
            "FROM orders WHERE create_time >= #{start} " +
            "GROUP BY DATE_FORMAT(create_time, '%Y-%m-%d')")
    List<TrendPoint> selectTrend(@Param("start") LocalDateTime start);

    @Select("SELECT b.title AS title, COUNT(*) AS sales FROM orders o " +
            "JOIN book b ON o.book_id = b.id " +
            "WHERE o.status = 'COMPLETED' " +
            "GROUP BY o.book_id, b.title ORDER BY sales DESC LIMIT 5")
    List<TopBook> selectTopBooks();

    @Select("SELECT IFNULL(SUM(price), 0) FROM orders WHERE status = 'COMPLETED'")
    BigDecimal selectTotalAmount();
}
