package com.example.compkeyback.service.impl;

import ch.qos.logback.core.db.dialect.DBUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.compkeyback.domain.Index;
import com.example.compkeyback.domain.Message;
import com.example.compkeyback.dto.Cache;
import com.example.compkeyback.dto.CompkeyResult;
import com.example.compkeyback.persistence.CacheMapper;
import com.example.compkeyback.persistence.IndexMapper;
import com.example.compkeyback.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

@Service("messageService")
@Component
public class MessageServiceImpl implements MessageService {

    @Autowired
    private IndexMapper indexMapper;

    @Autowired
    private CacheMapper cacheMapper;

    @Override
    public void insertSearchRecord() throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(new File("src/main/resources/files/index.txt")), "UTF-8");
        BufferedReader br = new BufferedReader(inputStreamReader);
        String valueString = null;
        String temp = null;
        String temp1 = null;
        String str1 = null;
        String str2 = null;
        String str3 = null;
        String str4 = null;
        int flag = -1;
        int length = 0;
        int templength = 0;
        int k=0;

        while(k==0) {
            Index index = new Index();
            while ((valueString = br.readLine()) != null){
//                if(valueString.contains("}")) {
//                    length = valueString.length();
//                    flag = valueString.indexOf("}");
//                    str4 += valueString.substring(0,flag-1);
//                    index.setLocation(str4);
//                    //System.out.println("str4"+str4);
//                    break;
//                }
                if(valueString.contains("}")) {
                    flag = valueString.indexOf("\t");
                    temp = valueString.substring(0,flag);
                    length = valueString.length();
                    temp1 = valueString.substring(flag+1,length);
                    flag = temp1.indexOf("=");
                    str3 = temp1.substring(1,flag);
                    templength = temp1.length();
                    str4 = temp1.substring(flag+1,templength-1);
                    templength = temp.length();
                    flag = temp.indexOf("+");
                    str1 = temp.substring(0,flag);
                    str2 = temp.substring(flag+1,templength);
                    str1 = new String(str1.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
                    index.setWord(str1);
                    index.setFileNum(str2);
                    index.setTime(str3);
                    index.setLocation(str4);
                    System.out.println("str1"+str1);
                    System.out.println("str2"+str2);
                    System.out.println("str3"+str3);
                    break;
                }
            }
            if(valueString == null) {
                k=1;
                continue;
            }
            indexMapper.insert(index);
        }
    }

    @Override
    public List<Index> selectSearchRecord(String keyword) {
        Index index = new Index();
        QueryWrapper<Index> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("word", keyword);
        List<Index> indexList = new ArrayList<>();
        indexList = indexMapper.selectList(queryWrapper);
        return indexList;
    }

    @Override
    public List<Message> getResultFromCache(String keyword) {
        QueryWrapper<Cache> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("seed_word", keyword);
        List<Cache> cacheList = cacheMapper.selectList(queryWrapper);
        if (cacheList.isEmpty()){
            return null;
        }
        List<Message> messageList = new ArrayList<>();
        for (Cache cache : cacheList){
            Message message = new Message();
            message.setKey(cache.getCompWord());
            message.setValue(cache.getCompDegree());
            messageList.add(message);
        }
        return messageList;
    }

    @Override
    public void insertIntoCache(Message message, String seedWord) {
        Cache cache = new Cache();
        cache.setSeedWord(seedWord);
        cache.setCompWord(message.getKey());
        cache.setCompDegree(message.getValue());
        cacheMapper.insert(cache);
    }
}
