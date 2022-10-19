package com.example.compkeyback.controller;

import com.example.compkeyback.domain.Message;
import com.example.compkeyback.service.MessageService;
import com.sun.istack.internal.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/message")
public class MessageController {
    @Autowired
    private MessageService messageService;

    @PostMapping("/send")
    public Message sendMessage(@RequestParam @NotNull String statement){
        Message message = new Message();
        message.setKey("a");
        message.setValue("b");
        return message;
    }
}
