package com.code.secondhandbooktradingplatform.dto;

import lombok.Data;

@Data
public class LoginDTO {
    private String username;
    private String password;
    /** USER-前台用户 ADMIN-后台管理员 */
    private String role;
}
