package com.example.travelstats.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域配置
 * 允许前端(Vite开发服务器)跨域访问后端API
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*");  // 允许所有来源访问
        config.addAllowedMethod("*");  // 允许所有HTTP方法
        config.addAllowedHeader("*");  //  允许所有请求头
        config.setAllowCredentials(true);  // 允许携带Cookie

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);  //所有路径都生效
        return new CorsFilter(source);
    }
}
