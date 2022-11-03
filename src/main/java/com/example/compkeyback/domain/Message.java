package com.example.compkeyback.domain;

import lombok.Data;

@Data
public class Message {
    private String key;
    private String value;
    private String seedWord;
}
