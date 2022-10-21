package com.example.compkeyback.dto;

import lombok.Data;

import java.util.List;

@Data
public class CompkeyResult {
    private List<String> compkeyList;

    private List<Double> compkeyResult;
}
