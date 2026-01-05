package com.cy.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "weixin")
public class WeixinConfig {

    // 微信小程序配置
    private String appId = "";
    private String appSecret = "";
    
    // 可以在这里添加其他微信相关配置
    private String apiBaseUrl = "https://api.weixin.qq.com";
    private int connectTimeout = 5000;
    private int readTimeout = 5000;
}
