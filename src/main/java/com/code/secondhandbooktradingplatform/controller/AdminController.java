package com.code.secondhandbooktradingplatform.controller;

import com.code.secondhandbooktradingplatform.common.Result;
import com.code.secondhandbooktradingplatform.service.AdminService;
import com.code.secondhandbooktradingplatform.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/stats/summary")
    public Result<SummaryVO> summary() {
        return Result.ok(adminService.summary());
    }

    @GetMapping("/stats/order-trend")
    public Result<List<TrendPoint>> orderTrend() {
        return Result.ok(adminService.orderTrend());
    }

    @GetMapping("/stats/category-dist")
    public Result<List<NameValue>> categoryDist() {
        return Result.ok(adminService.categoryDist());
    }

    @GetMapping("/stats/top-books")
    public Result<List<TopBook>> topBooks() {
        return Result.ok(adminService.topBooks());
    }

    @GetMapping("/users")
    public Result<?> users(@RequestParam(required = false) String keyword,
                           @RequestParam(defaultValue = "1") long page,
                           @RequestParam(defaultValue = "10") long size) {
        return Result.ok(adminService.userPage(keyword, page, size));
    }

    @PutMapping("/users/{id}/status")
    public Result<Void> updateUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        adminService.updateUserStatus(id, status);
        return Result.ok();
    }

    @GetMapping("/books")
    public Result<?> books(@RequestParam(required = false) String keyword,
                           @RequestParam(required = false) String status,
                           @RequestParam(defaultValue = "1") long page,
                           @RequestParam(defaultValue = "10") long size) {
        return Result.ok(adminService.bookPage(keyword, status, page, size));
    }

    @PutMapping("/books/{id}/status")
    public Result<Void> updateBookStatus(@PathVariable Long id, @RequestParam String status) {
        adminService.updateBookStatus(id, status);
        return Result.ok();
    }

    @GetMapping("/orders")
    public Result<?> orders(@RequestParam(required = false) String keyword,
                            @RequestParam(required = false) String status,
                            @RequestParam(defaultValue = "1") long page,
                            @RequestParam(defaultValue = "10") long size) {
        return Result.ok(adminService.orderPage(keyword, status, page, size));
    }

    @DeleteMapping("/orders/{id}")
    public Result<Void> deleteOrder(@PathVariable Long id) {
        adminService.deleteOrder(id);
        return Result.ok();
    }
}
