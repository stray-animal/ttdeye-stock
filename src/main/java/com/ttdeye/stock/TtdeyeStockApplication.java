package com.ttdeye.stock;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.ttdeye.stock.mapper")
public class TtdeyeStockApplication {

    public static void main(String[] args) {
        SpringApplication.run(TtdeyeStockApplication.class, args);
    }

}
