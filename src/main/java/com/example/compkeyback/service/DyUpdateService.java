package com.example.compkeyback.service;

import com.example.compkeyback.domain.Score;
import com.example.compkeyback.dto.CompkeyResult;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface DyUpdateService {

    public CompkeyResult compkey(String seedKey, int midNum) throws IOException, ExecutionException, InterruptedException;

    public Score getScoreByCompkey(String seedWord, String compWord);

    public double compDegreeCompute(double avgScore, double degree, int frequency);

    public void updateDB(String seed, HashMap<String,Double> newHash, List<String> deleteList);

    public void dyUpdate() throws IOException, ExecutionException, InterruptedException;

}
