package com.example.compkeyback.service;

import com.example.compkeyback.domain.Index;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public interface MessageService {
    public void insertSearchRecord() throws IOException;
    public List<Index> selectSearchRecord(String keyword);
}
