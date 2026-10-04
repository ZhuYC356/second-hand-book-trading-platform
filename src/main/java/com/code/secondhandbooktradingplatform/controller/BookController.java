package com.code.secondhandbooktradingplatform.controller;

import com.code.secondhandbooktradingplatform.common.Result;
import com.code.secondhandbooktradingplatform.dto.BookDTO;
import com.code.secondhandbooktradingplatform.service.BookService;
import com.code.secondhandbooktradingplatform.vo.BookVO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    /** 在售图书分页（首页） */
    @GetMapping("/page")
    public Result<?> page(@RequestParam(required = false) String keyword,
                          @RequestParam(required = false) Long categoryId,
                          @RequestParam(defaultValue = "1") long page,
                          @RequestParam(defaultValue = "8") long size) {
        return Result.ok(bookService.page(keyword, categoryId, page, size));
    }

    /** 图书详情 */
    @GetMapping("/{id}")
    public Result<BookVO> detail(@PathVariable Long id) {
        return Result.ok(bookService.detail(id));
    }

    /** 我发布的图书 */
    @GetMapping("/my")
    public Result<?> my(@RequestParam(defaultValue = "1") long page,
                        @RequestParam(defaultValue = "100") long size,
                        HttpSession session) {
        return Result.ok(bookService.myBooks((Long) session.getAttribute("userId"), page, size));
    }

    @PostMapping
    public Result<Void> add(@RequestBody BookDTO dto, HttpSession session) {
        bookService.add(dto, (Long) session.getAttribute("userId"));
        return Result.ok();
    }

    @PutMapping
    public Result<Void> update(@RequestBody BookDTO dto, HttpSession session) {
        bookService.update(dto, (Long) session.getAttribute("userId"));
        return Result.ok();
    }

    /** 上架 / 下架 */
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam String status, HttpSession session) {
        bookService.updateStatus(id, status, (Long) session.getAttribute("userId"));
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpSession session) {
        bookService.delete(id, (Long) session.getAttribute("userId"));
        return Result.ok();
    }
}
