package com.code.secondhandbooktradingplatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.code.secondhandbooktradingplatform.entity.Book;
import com.code.secondhandbooktradingplatform.vo.BookVO;
import com.code.secondhandbooktradingplatform.vo.NameValue;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface BookMapper extends BaseMapper<Book> {

    @Select("""
            <script>
            SELECT b.*, u.nickname AS sellerName, c.name AS categoryName
            FROM book b
            LEFT JOIN user u ON b.seller_id = u.id
            LEFT JOIN category c ON b.category_id = c.id
            <where>
                <if test="kw != null and kw != ''">
                    AND (b.title LIKE CONCAT('%', #{kw}, '%') OR b.author LIKE CONCAT('%', #{kw}, '%'))
                </if>
                <if test="categoryId != null">AND b.category_id = #{categoryId}</if>
                <if test="status != null and status != ''">AND b.status = #{status}</if>
                <if test="sellerId != null">AND b.seller_id = #{sellerId}</if>
            </where>
            ORDER BY b.create_time DESC
            </script>
            """)
    Page<BookVO> selectBookPage(Page<BookVO> page,
                                @Param("kw") String kw,
                                @Param("categoryId") Long categoryId,
                                @Param("status") String status,
                                @Param("sellerId") Long sellerId);

    @Select("SELECT b.*, u.nickname AS sellerName, c.name AS categoryName " +
            "FROM book b " +
            "LEFT JOIN user u ON b.seller_id = u.id " +
            "LEFT JOIN category c ON b.category_id = c.id " +
            "WHERE b.id = #{id}")
    BookVO selectBookById(@Param("id") Long id);
}
