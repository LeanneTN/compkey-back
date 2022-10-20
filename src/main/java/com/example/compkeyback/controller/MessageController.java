package com.example.compkeyback.controller;

import com.example.compkeyback.domain.Message;
import com.example.compkeyback.service.MessageService;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/message")
public class MessageController {
    @Autowired
    private MessageService messageService;

    @PostMapping("/send")
    public List<Message> sendMessage(@RequestParam @NotNull(message = "statement can't be null") String statement){
        // 传入搜索语句statement，输出含有竞争关键词和竞争度的Message list


        List<Message> messages = new ArrayList<>();

        return messages;
    }


}
