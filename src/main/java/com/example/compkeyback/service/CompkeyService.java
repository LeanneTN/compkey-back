package com.example.compkeyback.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface CompkeyService {
    public List<Map.Entry<String,Double>> compkey(String seedKey, int minNum) throws IOException;
}
