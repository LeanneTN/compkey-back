package com.example.compkeyback.service;

import com.example.compkeyback.dto.CompkeyResult;
import com.example.compkeyback.dto.ScoreDTO;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface CompkeyService {
    public CompkeyResult compkey(String seedKey, int minNum) throws IOException, ExecutionException, InterruptedException;

    public List<String> getStringValue(String statement);

    public void setScoreByCompkey(ScoreDTO scoreDTO);

    public double getScoreByCompkey(ScoreDTO scoreDTO);
}
