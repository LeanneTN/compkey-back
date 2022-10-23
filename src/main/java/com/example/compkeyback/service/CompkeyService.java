package com.example.compkeyback.service;

import com.example.compkeyback.dto.CompkeyResult;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface CompkeyService {
    public CompkeyResult compkey(String seedKey, int minNum) throws IOException, ExecutionException, InterruptedException;

    public List<String> getStringValue(String statement);
}
