package com.campus.trade;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@MapperScan("com.campus.trade.mapper")
@EnableCaching
public class CampusTradeApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(CampusTradeApiApplication.class, args);
    }
}