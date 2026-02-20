package com.cy.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.cy.config.WeixinConfig;
import com.cy.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final RestTemplate restTemplate;

    private final StringRedisTemplate redisTemplate;

    private final WeixinConfig weixinConfig;

    @Override
    public String getAccessToken() {
        String token = redisTemplate.opsForValue().get("access_token");
        if (token == null) {
            synchronized (TokenServiceImpl.class) {
                token = redisTemplate.opsForValue().get("access_token");
                if (token == null) {
                    token = fetchTokenFromWeixin();
                    redisTemplate.opsForValue().set("access_token", token, 7200, TimeUnit.SECONDS);
                }
            }
        }
        return token;
    }

    private String fetchTokenFromWeixin() {
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + weixinConfig.getAppId() + "&secret=" + weixinConfig.getAppSecret();
        JSONObject result = restTemplate.getForObject(url, JSONObject.class);
        if (result != null) {
            return result.getString("access_token");
        }
        return null;
    }
}
