package com.example.compkeyback.controller;

import com.example.compkeyback.domain.Message;
import com.example.compkeyback.service.CompkeyService;
import com.example.compkeyback.service.MessageService;
import javax.validation.constraints.NotNull;

import com.example.compkeyback.service.impl.CompkeyServiceImpl;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/message")
public class MessageController {
    @Autowired
    private MessageService messageService;

    @Autowired
    private CompkeyService compkeyService;

    @PostMapping("/send")
    public List<Message> sendMessage(@RequestParam @NotNull(message = "statement can't be null") String statement) throws IOException {
        // 传入搜索语句statement，输出含有竞争关键词和竞争度的Message list
        String stringValue = compkeyService.getStringValue(statement);

        List<Message> messages = new ArrayList<>();

        return messages;
    }


}
