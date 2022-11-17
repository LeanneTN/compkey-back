package com.example.compkeyback;

import com.alicp.jetcache.anno.config.EnableCreateCacheAnnotation;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
@EnableCreateCacheAnnotation
@SpringBootApplication(scanBasePackages = "com.example.compkeyback")
@MapperScan("com.example.compkeyback.persistence")
public class CompkeyBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(CompkeyBackApplication.class, args);
    }

}
