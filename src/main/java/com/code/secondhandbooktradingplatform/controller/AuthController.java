package com.code.secondhandbooktradingplatform.controller;

import com.code.secondhandbooktradingplatform.common.Result;
import com.code.secondhandbooktradingplatform.dto.LoginDTO;
import com.code.secondhandbooktradingplatform.dto.RegisterDTO;
import com.code.secondhandbooktradingplatform.entity.User;
import com.code.secondhandbooktradingplatform.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public Result<User> login(@RequestBody LoginDTO dto, HttpSession session) {
        User user = authService.login(dto.getUsername(), dto.getPassword(), dto.getRole());
        session.setAttribute("userId", user.getId());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("nickname", user.getNickname());
        session.setAttribute("role", user.getRole());
        return Result.ok(user);
    }

    @PostMapping("/register")
    public Result<Void> register(@RequestBody RegisterDTO dto) {
        authService.register(dto.getUsername(), dto.getPassword(), dto.getNickname());
        return Result.ok();
    }

    @PostMapping("/logout")
    public Result<Void> logout(HttpSession session) {
        session.invalidate();
        return Result.ok();
    }

    @GetMapping("/me")
    public Result<User> me(HttpSession session) {
        User user = new User();
        user.setId((Long) session.getAttribute("userId"));
        user.setUsername((String) session.getAttribute("username"));
        user.setNickname((String) session.getAttribute("nickname"));
        user.setRole((String) session.getAttribute("role"));
        return Result.ok(user);
    }
}
