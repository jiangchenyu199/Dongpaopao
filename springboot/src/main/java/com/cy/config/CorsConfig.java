package com.cy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        // 1. 创建跨域配置对象
        CorsConfiguration config = new CorsConfiguration();

        // 允许的源（前端域名），* 表示允许所有（生产环境建议指定具体域名，如http://localhost:8080）
        config.addAllowedOriginPattern("*"); // Spring Boot 3 推荐用 allowedOriginPattern 替代 allowedOrigin
        // 允许的请求头（如Content-Type、Token等）
        config.addAllowedHeader("*");
        // 允许的HTTP方法（GET/POST/PUT/DELETE等）
        config.addAllowedMethod("*");
        // 是否允许携带Cookie（前后端分离带认证信息时需要开启）
        config.setAllowCredentials(true);
        // 预检请求（OPTIONS）的缓存时间，单位秒（减少OPTIONS请求次数）
        config.setMaxAge(3600L);

        // 2. 配置跨域规则生效的路径（/** 表示所有路径）
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        // 3. 返回跨域过滤器
        return new CorsFilter(source);
    }
}
