package com.example.compkeyback.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("cache")
@Data
public class Cache {
    @TableField("seed_word")
    private String seedWord;
    @TableField("comp_word")
    private String compWord;
    @TableField("comp_degree")
    private String compDegree;
}
