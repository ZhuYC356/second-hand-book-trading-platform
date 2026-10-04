package com.code.secondhandbooktradingplatform.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String nickname;
    /** USER-前台用户 ADMIN-管理员 */
    private String role;
    private String phone;
    private String email;
    /** 1-正常 0-禁用 */
    private Integer status;
    private LocalDateTime createTime;
}
