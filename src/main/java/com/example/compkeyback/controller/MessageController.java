package com.example.compkeyback.controller;

import com.example.compkeyback.domain.Message;
import com.example.compkeyback.dto.CompkeyResult;
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
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/message")
public class MessageController {
    @Autowired
    private MessageService messageService;

    @Autowired
    private CompkeyService compkeyService;

    @PostMapping("/send")
    public List<Message> sendMessage(@RequestParam @NotNull(message = "statement can't be null") String statement) throws IOException, ExecutionException, InterruptedException {
        // 传入搜索语句statement，输出含有竞争关键词和竞争度的Message list
        List<String> stringValue = compkeyService.getStringValue(statement);

        //将前端传入的搜索记录存入数据库中
        messageService.insertNewRecord(statement);

        List<Message> messages = new ArrayList<>();

        for(String keyword : stringValue){
            List<Message> messageList = messageService.getResultFromCache(keyword);
            if(!messageList.isEmpty()){
                messages.addAll(messageList);
                continue;
            }
            CompkeyResult tempList = compkeyService.compkey(keyword, 3);
            List<String> compkey = tempList.getCompkeyList();
            List<Double> compkeyResult = tempList.getCompkeyResult();
            for(int i = 0; i < compkey.size(); i++){
                Message message = new Message();
                message.setKey(compkey.get(i));
                message.setValue(compkeyResult.get(i).toString());
                messages.add(message);
                messageService.insertIntoCache(message, keyword);
            }
        }
        return messages;
    }
}
