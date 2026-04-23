package com.dongpaopao.backend.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.dongpaopao.backend.**.mapper")
public class MybatisPlusConfig {
}
