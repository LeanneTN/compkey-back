package com.example.compkeyback;

import com.example.compkeyback.domain.Index;
import com.example.compkeyback.service.CompkeyService;
import com.example.compkeyback.service.DyUpdateService;
import com.example.compkeyback.service.MessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@SpringBootTest
@Component
class CompkeyBackApplicationTests {

    @Autowired
    private MessageService messageService;

    @Autowired
    private CompkeyService compkeyService;

    @Autowired
    private DyUpdateService dyUpdateService;

    @Test
    void contextLoads() throws IOException {
        messageService.insertNewRecord("你好");
//        compkeyService.compkey("图片",3);
    }

    @Test
    void testUpdate() throws IOException, ExecutionException, InterruptedException {
        dyUpdateService.dyUpdate();
    }

}
