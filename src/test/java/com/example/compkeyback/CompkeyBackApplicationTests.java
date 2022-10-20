package com.example.compkeyback;

import com.example.compkeyback.domain.Index;
import com.example.compkeyback.service.MessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@SpringBootTest
@Component
class CompkeyBackApplicationTests {

    @Autowired
    private MessageService messageService;

    @Test
    void contextLoads() throws IOException {
//        messageService.insertSearchRecord();
    }

}
