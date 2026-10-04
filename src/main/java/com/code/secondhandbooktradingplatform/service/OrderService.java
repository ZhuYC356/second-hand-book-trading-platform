package com.code.secondhandbooktradingplatform.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.code.secondhandbooktradingplatform.common.ServiceException;
import com.code.secondhandbooktradingplatform.entity.Book;
import com.code.secondhandbooktradingplatform.entity.Order;
import com.code.secondhandbooktradingplatform.mapper.BookMapper;
import com.code.secondhandbooktradingplatform.mapper.OrderMapper;
import com.code.secondhandbooktradingplatform.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private BookMapper bookMapper;

    /** 购买：生成待付款订单并锁定图书 */
    @Transactional
    public void buy(Long bookId, Long buyerId) {
        Book book = bookMapper.selectById(bookId);
        if (book == null) {
            throw new ServiceException("图书不存在");
        }
        if (!"ON_SALE".equals(book.getStatus())) {
            throw new ServiceException("该图书当前不可购买");
        }
        if (book.getSellerId().equals(buyerId)) {
            throw new ServiceException("不能购买自己发布的图书");
        }
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setBookId(bookId);
        order.setBuyerId(buyerId);
        order.setSellerId(book.getSellerId());
        order.setPrice(book.getPrice());
        order.setStatus("PENDING");
        order.setCreateTime(LocalDateTime.now());
        orderMapper.insert(order);
        book.setStatus("SOLD");
        bookMapper.updateById(book);
    }

    /** 买家付款：待付款 -> 已完成 */
    public void pay(Long id, Long buyerId) {
        Order order = getBuyerOrder(id, buyerId);
        order.setStatus("COMPLETED");
        order.setFinishTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    /** 买家取消订单：待付款 -> 已取消，图书重新上架 */
    @Transactional
    public void cancel(Long id, Long buyerId) {
        Order order = getBuyerOrder(id, buyerId);
        order.setStatus("CANCELLED");
        orderMapper.updateById(order);
        Book book = bookMapper.selectById(order.getBookId());
        if (book != null) {
            book.setStatus("ON_SALE");
            bookMapper.updateById(book);
        }
    }

    /** 我买到的 */
    public Page<OrderVO> bought(Long buyerId, long pageNo, long pageSize, String status) {
        return orderMapper.selectOrderPage(new Page<>(pageNo, pageSize), buyerId, null, status, null);
    }

    /** 我卖出的 */
    public Page<OrderVO> sold(Long sellerId, long pageNo, long pageSize, String status) {
        return orderMapper.selectOrderPage(new Page<>(pageNo, pageSize), null, sellerId, status, null);
    }

    private Order getBuyerOrder(Long id, Long buyerId) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new ServiceException("订单不存在");
        }
        if (!order.getBuyerId().equals(buyerId)) {
            throw new ServiceException("只能操作自己的订单");
        }
        if (!"PENDING".equals(order.getStatus())) {
            throw new ServiceException("订单状态已变更，无法操作");
        }
        return order;
    }

    private String generateOrderNo() {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = ThreadLocalRandom.current().nextInt(100, 1000);
        return "SB" + time + random;
    }
}
