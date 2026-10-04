package com.code.secondhandbooktradingplatform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.code.secondhandbooktradingplatform.common.ServiceException;
import com.code.secondhandbooktradingplatform.dto.BookDTO;
import com.code.secondhandbooktradingplatform.entity.Book;
import com.code.secondhandbooktradingplatform.entity.Order;
import com.code.secondhandbooktradingplatform.mapper.BookMapper;
import com.code.secondhandbooktradingplatform.mapper.OrderMapper;
import com.code.secondhandbooktradingplatform.vo.BookVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BookService {

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private OrderMapper orderMapper;

    /** 在售图书分页（前台首页） */
    public Page<BookVO> page(String keyword, Long categoryId, long pageNo, long pageSize) {
        return bookMapper.selectBookPage(new Page<>(pageNo, pageSize), keyword, categoryId, "ON_SALE", null);
    }

    /** 我发布的图书 */
    public Page<BookVO> myBooks(Long sellerId, long pageNo, long pageSize) {
        return bookMapper.selectBookPage(new Page<>(pageNo, pageSize), null, null, null, sellerId);
    }

    public BookVO detail(Long id) {
        BookVO book = bookMapper.selectBookById(id);
        if (book == null) {
            throw new ServiceException("图书不存在");
        }
        return book;
    }

    public void add(BookDTO dto, Long sellerId) {
        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            throw new ServiceException("请输入书名");
        }
        if (dto.getPrice() == null || dto.getPrice().signum() < 0) {
            throw new ServiceException("请输入正确的售价");
        }
        Book book = new Book();
        BeanUtils.copyProperties(dto, book);
        book.setSellerId(sellerId);
        book.setStatus("ON_SALE");
        book.setCreateTime(LocalDateTime.now());
        bookMapper.insert(book);
    }

    public void update(BookDTO dto, Long userId) {
        Book book = getOwnedBook(dto.getId(), userId);
        if (dto.getTitle() != null && dto.getTitle().isBlank()) {
            throw new ServiceException("书名不能为空");
        }
        BeanUtils.copyProperties(dto, book, "id");
        bookMapper.updateById(book);
    }

    /** 上架 / 下架 */
    public void updateStatus(Long id, String status, Long userId) {
        if (!"ON_SALE".equals(status) && !"OFF_SHELF".equals(status)) {
            throw new ServiceException("不支持的状态操作");
        }
        Book book = getOwnedBook(id, userId);
        book.setStatus(status);
        bookMapper.updateById(book);
    }

    public void delete(Long id, Long userId) {
        Book book = getOwnedBook(id, userId);
        Long orderCount = orderMapper.selectCount(new LambdaQueryWrapper<Order>().eq(Order::getBookId, id));
        if (orderCount > 0) {
            throw new ServiceException("该图书已有订单记录，无法删除，可选择下架");
        }
        bookMapper.deleteById(book.getId());
    }

    private Book getOwnedBook(Long id, Long userId) {
        Book book = bookMapper.selectById(id);
        if (book == null) {
            throw new ServiceException("图书不存在");
        }
        if (!book.getSellerId().equals(userId)) {
            throw new ServiceException("只能操作自己发布的图书");
        }
        return book;
    }
}
