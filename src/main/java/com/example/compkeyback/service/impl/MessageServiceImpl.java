package com.example.compkeyback.service.impl;

import ch.qos.logback.core.db.dialect.DBUtil;
import com.example.compkeyback.domain.Index;
import com.example.compkeyback.persistence.IndexMapper;
import com.example.compkeyback.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;

@Service("messageService")
@Component
public class MessageServiceImpl implements MessageService {
    private final static String insertWordSQL = "insert into keymap(word,fileNum,time,location) " +
            "values(?,?,?,?)";

    @Autowired
    private IndexMapper indexMapper;

    public void insertSearchRecordByWord(){
        Index index = new Index();

        indexMapper.insert(index);

    }

    @Override
    public int insertIndex(String Keyword) {
        return 0;
    }
}
