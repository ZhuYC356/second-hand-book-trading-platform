package com.code.secondhandbooktradingplatform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.code.secondhandbooktradingplatform.common.ServiceException;
import com.code.secondhandbooktradingplatform.entity.Book;
import com.code.secondhandbooktradingplatform.entity.Order;
import com.code.secondhandbooktradingplatform.entity.User;
import com.code.secondhandbooktradingplatform.mapper.BookMapper;
import com.code.secondhandbooktradingplatform.mapper.CategoryMapper;
import com.code.secondhandbooktradingplatform.mapper.OrderMapper;
import com.code.secondhandbooktradingplatform.mapper.UserMapper;
import com.code.secondhandbooktradingplatform.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private CategoryMapper categoryMapper;

    /** 统计总览 */
    public SummaryVO summary() {
        SummaryVO vo = new SummaryVO();
        vo.setUserCount(userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getRole, "USER")));
        vo.setBookCount(bookMapper.selectCount(null));
        vo.setOnSaleCount(bookMapper.selectCount(new LambdaQueryWrapper<Book>().eq(Book::getStatus, "ON_SALE")));
        vo.setOrderCount(orderMapper.selectCount(null));
        BigDecimal total = orderMapper.selectTotalAmount();
        vo.setTotalAmount(total == null ? BigDecimal.ZERO : total);
        return vo;
    }

    /** 近7日订单量与销售额趋势（补齐缺失日期） */
    public List<TrendPoint> orderTrend() {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.minusDays(6).atStartOfDay();
        Map<String, TrendPoint> map = orderMapper.selectTrend(start).stream()
                .collect(Collectors.toMap(TrendPoint::getDate, Function.identity()));
        List<TrendPoint> result = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            String date = today.minusDays(i).toString();
            TrendPoint point = map.get(date);
            if (point == null) {
                point = new TrendPoint();
                point.setDate(date);
                point.setCount(0L);
                point.setAmount(BigDecimal.ZERO);
            }
            result.add(point);
        }
        return result;
    }

    /** 图书分类数量分布 */
    public List<NameValue> categoryDist() {
        return categoryMapper.selectCategoryDist();
    }

    /** 热门书籍排行（按完成订单数） */
    public List<TopBook> topBooks() {
        return orderMapper.selectTopBooks();
    }

    /** 用户分页 */
    public Page<User> userPage(String keyword, long pageNo, long pageSize) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(User::getUsername, keyword).or().like(User::getNickname, keyword));
        }
        wrapper.orderByDesc(User::getCreateTime);
        Page<User> page = userMapper.selectPage(new Page<>(pageNo, pageSize), wrapper);
        page.getRecords().forEach(u -> u.setPassword(null));
        return page;
    }

    /** 启用 / 禁用用户（仅前台用户） */
    public void updateUserStatus(Long id, Integer status) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new ServiceException("用户不存在");
        }
        if (!"USER".equals(user.getRole())) {
            throw new ServiceException("不能禁用管理员账号");
        }
        user.setStatus(status == 0 ? 0 : 1);
        userMapper.updateById(user);
    }

    /** 图书分页（全部状态） */
    public Page<BookVO> bookPage(String keyword, String status, long pageNo, long pageSize) {
        return bookMapper.selectBookPage(new Page<>(pageNo, pageSize), keyword, null, status, null);
    }

    /** 管理员上架 / 下架图书 */
    public void updateBookStatus(Long id, String status) {
        if (!"ON_SALE".equals(status) && !"OFF_SHELF".equals(status)) {
            throw new ServiceException("不支持的状态操作");
        }
        Book book = bookMapper.selectById(id);
        if (book == null) {
            throw new ServiceException("图书不存在");
        }
        book.setStatus(status);
        bookMapper.updateById(book);
    }

    /** 订单分页 */
    public Page<OrderVO> orderPage(String keyword, String status, long pageNo, long pageSize) {
        return orderMapper.selectOrderPage(new Page<>(pageNo, pageSize), null, null, status, keyword);
    }

    /** 删除订单（待付款订单删除后图书重新上架） */
    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new ServiceException("订单不存在");
        }
        orderMapper.deleteById(id);
        if ("PENDING".equals(order.getStatus())) {
            Book book = bookMapper.selectById(order.getBookId());
            if (book != null) {
                book.setStatus("ON_SALE");
                bookMapper.updateById(book);
            }
        }
    }
}
