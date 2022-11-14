package com.example.compkeyback.dynamicUpdate;

import org.mybatis.spring.annotation.MapperScan;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@MapperScan("com.example.compkeyback.persistence")
public class main {
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        Update update = new Update();
        update.dyUpdate();
    }
}
