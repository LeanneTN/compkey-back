package com.example.compkeyback.service;

import com.example.compkeyback.domain.Index;
import com.example.compkeyback.domain.Message;
import com.example.compkeyback.dto.CompkeyResult;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public interface MessageService {
    public void insertSearchRecord() throws IOException;
    public List<Index> selectSearchRecord(String keyword);
    public List<Message> getResultFromCache(String keyword);
    public void insertIntoCache(Message message, String seedWord);
    public void insertNewRecord(String search);
}
