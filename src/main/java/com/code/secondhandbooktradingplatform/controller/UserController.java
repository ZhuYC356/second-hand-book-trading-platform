package com.code.secondhandbooktradingplatform.controller;

import com.code.secondhandbooktradingplatform.common.Result;
import com.code.secondhandbooktradingplatform.dto.PasswordDTO;
import com.code.secondhandbooktradingplatform.dto.ProfileDTO;
import com.code.secondhandbooktradingplatform.entity.User;
import com.code.secondhandbooktradingplatform.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/info")
    public Result<User> info(HttpSession session) {
        return Result.ok(userService.getById((Long) session.getAttribute("userId")));
    }

    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestBody ProfileDTO dto, HttpSession session) {
        userService.updateProfile((Long) session.getAttribute("userId"), dto);
        return Result.ok();
    }

    @PutMapping("/password")
    public Result<Void> updatePassword(@RequestBody PasswordDTO dto, HttpSession session) {
        userService.updatePassword((Long) session.getAttribute("userId"), dto);
        return Result.ok();
    }
}
