package com.example.compkeyback;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.example.compkeyback.persistence")
public class CompkeyBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(CompkeyBackApplication.class, args);
    }

}
