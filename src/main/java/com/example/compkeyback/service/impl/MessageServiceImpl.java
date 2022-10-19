package com.example.compkeyback.service.impl;

import ch.qos.logback.core.db.dialect.DBUtil;
import com.example.compkeyback.domain.Index;
import com.example.compkeyback.persistence.IndexMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class MessageServiceImpl {
    private final static String insertWordSQL = "insert into keymap(word,fileNum,time,location) " +
            "values(?,?,?,?)";

    @Autowired
    private IndexMapper indexMapper;

    public void insertSearchRecordByWord(){
        Index index = new Index();

        indexMapper.insert(index);

    }
}
