package com.code.secondhandbooktradingplatform.controller;

import com.code.secondhandbooktradingplatform.common.Result;
import com.code.secondhandbooktradingplatform.service.OrderService;
import com.code.secondhandbooktradingplatform.vo.OrderVO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /** 购买图书 */
    @PostMapping("/buy")
    public Result<Void> buy(@RequestBody Map<String, Long> body, HttpSession session) {
        orderService.buy(body.get("bookId"), (Long) session.getAttribute("userId"));
        return Result.ok();
    }

    /** 我买到的 */
    @GetMapping("/bought")
    public Result<?> bought(@RequestParam(defaultValue = "1") long page,
                            @RequestParam(defaultValue = "10") long size,
                            @RequestParam(required = false) String status,
                            HttpSession session) {
        return Result.ok(orderService.bought((Long) session.getAttribute("userId"), page, size, status));
    }

    /** 我卖出的 */
    @GetMapping("/sold")
    public Result<?> sold(@RequestParam(defaultValue = "1") long page,
                          @RequestParam(defaultValue = "10") long size,
                          @RequestParam(required = false) String status,
                          HttpSession session) {
        return Result.ok(orderService.sold((Long) session.getAttribute("userId"), page, size, status));
    }

    /** 付款 */
    @PutMapping("/{id}/pay")
    public Result<Void> pay(@PathVariable Long id, HttpSession session) {
        orderService.pay(id, (Long) session.getAttribute("userId"));
        return Result.ok();
    }

    /** 取消订单 */
    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id, HttpSession session) {
        orderService.cancel(id, (Long) session.getAttribute("userId"));
        return Result.ok();
    }
}
