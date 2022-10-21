package com.example.compkeyback.service;

import com.example.compkeyback.dto.CompkeyResult;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface CompkeyService {
    public CompkeyResult compkey(String seedKey, int minNum) throws IOException;

    public List<String> getStringValue(String statement);
}
