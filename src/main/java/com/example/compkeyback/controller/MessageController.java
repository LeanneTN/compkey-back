package com.example.compkeyback.controller;

import com.example.compkeyback.domain.Message;
import com.example.compkeyback.domain.Score;
import com.example.compkeyback.dto.CompList;
import com.example.compkeyback.dto.CompkeyResult;
import com.example.compkeyback.dto.ScoreDTO;
import com.example.compkeyback.service.CompkeyService;
import com.example.compkeyback.service.MessageService;
import javax.validation.constraints.NotNull;

import com.example.compkeyback.service.impl.CompkeyServiceImpl;
import com.example.compkeyback.util.Util;
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
            if(!(messageList == null)){
                for(Message message : messageList){
                    message.setSeedWord(keyword);
                    ScoreDTO scoreDTO = new ScoreDTO();
                    scoreDTO.setSeedWord(keyword);
                    scoreDTO.setCompkeyWord(message.getKey());
                    Score score = compkeyService.getScoreByCompkey(scoreDTO);
                    if(score!=null){
                        int frequency = score.getFrequency();
                        double avgScore = score.getAvgScore();
                        Double degree = Double.parseDouble(message.getValue());
                        //返回动态结果
                        Double resultcomp = compkeyService.compDegreeCompute(avgScore,degree,frequency);
                        //用于显示的message的竞争度为动态竞争度resultcomp
                        message.setValue(resultcomp.toString());
                    }
                }
                messages.addAll(messageList);
                continue;
            }
            CompkeyResult tempList = compkeyService.compkey(keyword, 6);
            List<String> compkey = tempList.getCompkeyList();
            List<Double> compkeyResult = tempList.getCompkeyResult();
            ScoreDTO scoreDTO = new ScoreDTO();
            //空指针判断
            if (compkey.isEmpty()){
                return messages;
            }
            for(int i = 0; i < compkey.size(); i++){
                Message message = new Message();
                scoreDTO.setSeedWord(keyword);
                scoreDTO.setCompkeyWord(compkey.get(i));
                Score score = compkeyService.getScoreByCompkey(scoreDTO);
                double degree = compkeyResult.get(i);
                Double resultcomp = 3.5;
                if(score == null){
                    int frequency = 1;
                    double avgScore = 3.5;
                    resultcomp = compkeyService.compDegreeCompute(avgScore, degree, frequency);
                    compkeyService.setScoreByCompkey(scoreDTO);
                }else {
                    int frequency = score.getFrequency();
                    double avgScore = score.getAvgScore();
                    degree = compkeyResult.get(i);
                    //返回动态结果
                    resultcomp = compkeyService.compDegreeCompute(avgScore, degree, frequency);
                }
                message.setKey(compkey.get(i));
                message.setValue(Double.toString(degree));
                message.setSeedWord(keyword);
                //插入缓存数据库的message的竞争度为compkey算法的结果值
                messageService.insertIntoCache(message, keyword);
                //用于显示的message的竞争度为动态竞争度resultcomp
                message.setValue(resultcomp.toString());
                messages.add(message);
            }
        }
        System.out.println(messages);
        return messages;
    }

    @PostMapping("/score")
    public void setScore(
            @RequestParam String seed,
            @RequestParam String compkey,
            @RequestParam int score,
            @RequestParam double compDegree
    ){
        ScoreDTO scoreDTO = new ScoreDTO();
        scoreDTO.setScore(score);
        scoreDTO.setCompDegree(compDegree);
        scoreDTO.setSeedWord(seed);
        scoreDTO.setCompkeyWord(compkey);
        compkeyService.setScoreByCompkey(scoreDTO);
    }

    @PostMapping("/mylist")
    public void getListInfo(@RequestParam String seedWords,
                            @RequestParam String compWords,
                            @RequestParam double comp){
        CompList compList = new CompList();
        compList.setCompWord(compWords);
        compList.setSeedWord(seedWords);
        compList.setComp(comp);
        System.out.println(compList);
        compkeyService.getListInfo(compList);
    }

}
