package com.example.compkeyback.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("score")
public class Score {
    @TableField("seed")
    private String seed;
    @TableField("comp_word")
    private String compWord;
    @TableField("frequency")
    private int frequency;
    @TableField("avg_score")
    private double avgScore;
}
