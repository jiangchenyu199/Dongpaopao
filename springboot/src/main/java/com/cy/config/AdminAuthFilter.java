package com.cy.config;

import com.cy.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (path == null) return true;
        if (path.contains("/admin/login")) return true;
        return !path.contains("/admin/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }
        String path = request.getRequestURI();
        if (path != null && path.contains("/admin/login")) {
            filterChain.doFilter(request, response);
            return;
        }
        String auth = request.getHeader("Authorization");
        String token = null;
        if (StringUtils.hasText(auth) && auth.startsWith("Bearer ")) {
            token = auth.substring(7).trim();
        }
        if (!StringUtils.hasText(token)) {
            sendUnauthorized(response, "未登录或登录已过期");
            return;
        }
        try {
            if (jwtUtil.isTokenExpired(token)) {
                sendUnauthorized(response, "登录已过期");
                return;
            }
            Long userId = jwtUtil.extractUserId(token);
            request.setAttribute("adminUserId", userId);
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.warn("admin auth parse token fail", e);
            sendUnauthorized(response, "登录无效");
        }
    }

    private void sendUnauthorized(HttpServletResponse response, String msg) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
        response.getWriter().write("{\"errCode\":401,\"msg\":\"" + msg + "\"}");
    }
}
