package com.code.secondhandbooktradingplatform.interceptor;

import com.code.secondhandbooktradingplatform.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录拦截器：/api/** 需要登录；/api/admin/** 需要管理员角色
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();
        HttpSession session = request.getSession();
        Object userId = session.getAttribute("userId");
        if (userId == null) {
            write(response, Result.error(401, "尚未登录，请先登录"));
            return false;
        }
        if (path.startsWith("/api/admin/") && !"ADMIN".equals(session.getAttribute("role"))) {
            write(response, Result.error(403, "无权限访问"));
            return false;
        }
        return true;
    }

    private void write(HttpServletResponse response, Result<Void> result) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":" + result.getCode() + ",\"msg\":\"" + result.getMsg() + "\",\"data\":null}");
    }
}
