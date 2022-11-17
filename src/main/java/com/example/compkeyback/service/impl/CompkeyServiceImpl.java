package com.example.compkeyback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.compkeyback.domain.Score;
import com.example.compkeyback.dto.CompList;
import com.example.compkeyback.dto.CompkeyResult;
import com.example.compkeyback.dto.ScoreDTO;
import com.example.compkeyback.persistence.ScoreMapper;
import com.example.compkeyback.service.CompkeyService;
import com.example.compkeyback.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Service("compkeyService")
@Component
public class CompkeyServiceImpl implements CompkeyService {
    @Autowired
    private ScoreMapper scoreMapper;

    @Override
    public CompkeyResult compkey(String seedKey, int midNum) throws IOException, ExecutionException, InterruptedException {
        System.out.println("查询种子关键词的相关搜索记录...");
        //从清洗过的数据中提取出与种子关键字相关的搜索信息并保存
        int seedInfoCounter = Util.search(seedKey,"src/main/resources/compkeyFiles/seedSearchResult.txt");

        System.out.println("========================================================");
        System.out.println("开始查找中介关键词...");
        Map<String,Integer> midCountMap = MidKeyThread.cut_count(9,seedInfoCounter);
        List<String> midKey = new ArrayList<>();
        //挑选中介词
        int flag = 0;
        for(String key : midCountMap.keySet()){
            if(!key.equals(seedKey)&&!key.contains(seedKey)&&flag<midNum){
                midKey.add(key);
                flag++;
            }else {
                continue;
            }
            if(flag>=midNum){break;}
        }
        System.out.println("确定中介关键词为："+midKey);

        System.out.println("========================================================");
        System.out.println("开始查找竞争性关键词...");
        Map<String,String> mid_comp = new HashMap<>();
        Map<String,Integer> mid_comp_ka = new HashMap<>();//midKey-ka值
        for(int i=0;i<midKey.size();i++){
            String midkey = midKey.get(i);
            int midInfoCounter = Util.compSearch(seedKey,midkey,"src/main/resources/compkeyFiles/midSearchResult.txt");
            //定义当前中介词的分词词频情况并挑选竞争关键词,同时完成ka搜索量的确认
            Map<String,Integer> compCountMap = CompKeyThread.cut_count(9,midInfoCounter);
            //挑选竞争词
            int compflag = 0;
            for(String key : compCountMap.keySet()){
                if(!key.equals(midkey)&&!key.equals(seedKey)&&!key.contains(seedKey)&&compflag<1&&!mid_comp.containsValue(key)){
                    mid_comp.put(midkey,key);
                    mid_comp_ka.put(midkey,compCountMap.get(key));
                    compflag++;
                }else {
                    continue;
                }
                if(flag>=1){break;}
            }
        }
        System.out.println("确定每个中介对应的竞争关键词为："+mid_comp);


        System.out.println("========================================================");
        System.out.println("开始计算竞争度...");
        //确定相关搜索量
        //种子搜索量
        int s = midCountMap.get(seedKey);
        //种子和中介关键词一起出现的搜索量sa
        Map<String,Integer> seed_mid_sa = new HashMap<>();//midKey-sa
        //定义中介关键词的搜索量a
        Map<String,Integer> mid_a = new HashMap<>();//midKey-a
        //计算权重weight=sa/s
        Map<String,Double> mid_weight = new HashMap<>();//midKey-weight
        //定义竞争关键词的竞争度
        Map<String,Double> comp = new TreeMap<>();
        for (int i =0;i<midKey.size();i++){
            //确定此次循环下的中介关键词
            String midkey = midKey.get(i);

            //sa
            try(Scanner sc = new Scanner(new FileReader("src/main/resources/compkeyFiles/seedSearchResult.txt"))) {
                int count = 0;
                while (sc.hasNextLine()) {
                    String line = sc.nextLine();
                    if (line.contains(seedKey)&&line.contains(midkey)){
                        count++;
                    }
                }
                seed_mid_sa.put(midkey,count);
                //计算权重
                double weight = (double)count/(double)s;
                mid_weight.put(midkey,weight);
            }catch (Exception e){
                e.printStackTrace();
            }

            //a
            try(Scanner sc = new Scanner(new FileReader("src/main/resources/compkeyFiles/cleanResult.txt"))) {
                int count = 0;
                while (sc.hasNextLine()) {
                    String line = sc.nextLine();
                    if (line.contains(midkey)){
                        count++;
                    }
                }
                mid_a.put(midkey,count);
            }catch (Exception e){
                e.printStackTrace();
            }

            //最终竞争度计算
            double ans;
            if((double)mid_a.get(midkey)-(double) seed_mid_sa.get(midkey)==0){
                ans = -1;
            }else {
                ans = (double) mid_comp_ka.get(midkey)/(double) (mid_a.get(midkey)-seed_mid_sa.get(midkey));
            }
            double value = mid_weight.get(midkey)*ans;
            comp.put(mid_comp.get(midkey),value);
        }
        System.out.println("ka:"+mid_comp_ka);
        System.out.println("a:"+mid_a);
        System.out.println("sa:"+seed_mid_sa);
        System.out.println("s:"+s);
        System.out.println("weight:"+mid_weight);

        //排序输出
        comp = Util.sortComp(comp);
        System.out.println("“" + seedKey + "”的竞争性关键词关于竞争度排序如下:");
        List<Map.Entry<String,Double>> result = new ArrayList<>(comp.entrySet());
        System.out.println(result);
        System.out.println("CompKey算法结束...");

        //返回变量
        CompkeyResult compkeyResult = new CompkeyResult();
        List<String> compkey = new ArrayList<>();
        List<Double> compvalue = new ArrayList<>();
        for (int i =0;i<midKey.size();i++){
            String midkey = midKey.get(i);
            compkey.add(mid_comp.get(midkey));
            compvalue.add(comp.get(mid_comp.get(midkey)));
        }
        compkeyResult.setCompkeyList(compkey);
        compkeyResult.setCompkeyResult(compvalue);

        return compkeyResult;
    }

    @Override
    public List<String> getStringValue(String statement) {
        // return ToAnalysis.parse(statement).toString();
        TfIdfAnalyzer tfIdfAnalyzer = new TfIdfAnalyzer();
        int topN = 5;
        List<Keyword> list = tfIdfAnalyzer.analyze(statement, topN);
        List<String> keywords = new ArrayList<>();
        for(Keyword keyword : list){
            keywords.add(keyword.getName());
        }
        return keywords;
    }

    @Override
    public void setScoreByCompkey(ScoreDTO scoreDTO) {
        String compWord = scoreDTO.getCompkeyWord();
        String seedWord = scoreDTO.getSeedWord();
        int score = scoreDTO.getScore();
        QueryWrapper<Score> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("seed", seedWord).eq("comp_word", compWord);
        Score score1 = scoreMapper.selectOne(queryWrapper);
        if(score1 == null){
            Score temp = new Score();
            int freq = 1;
            temp.setFrequency(freq);
            temp.setAvgScore(((double)5 / (freq + 5)) * 3.5 + (freq / (double)(freq + 5)) * score);
            temp.setSeed(seedWord);
            temp.setCompWord(compWord);
            scoreMapper.insert(temp);
        }
        else {
            int frequency = score1.getFrequency();
            frequency++;
            double avg_score = ((double) 5 / (frequency + 5)) * 3.5 + (frequency / (double) (frequency + 5)) * score;
            score1.setAvgScore(avg_score);
            score1.setFrequency(frequency);
            UpdateWrapper<Score> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("seed", seedWord).eq("comp_word", compWord);
            scoreMapper.update(score1, updateWrapper);
        }
    }

    @Override
    public Score getScoreByCompkey(ScoreDTO scoreDTO) {
        String seedWord = scoreDTO.getSeedWord();
        String compKey = scoreDTO.getCompkeyWord();
        QueryWrapper<Score> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("seed", seedWord).eq("comp_word", compKey);
        Score score = scoreMapper.selectOne(queryWrapper);
        return score;
    }

    @Override
    public double compDegreeCompute(double commark, double degree, int frequency) {
        //double reScore = commark / 5.0;//将0-5的评分转化为0-1
        double scale = 5.0/ (degree*2);
        double reScore = commark/scale;
        double alpha = Util.mysigmoid(frequency,0.001);
        double finalScore = reScore * alpha + degree * (1 - alpha);
        return finalScore;
    }

    @Override
    public void searchEngine() {
        //留了一个使用别的搜索引擎内核的接口
    }

    @Override
    public void getListInfo(CompList compList) {
        String seed = compList.getSeedWord();
        String compWord = compList.getCompWord();
        QueryWrapper<Score> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("seed",seed).eq("comp_word",compWord);
        Score score = scoreMapper.selectOne(queryWrapper);
        if(score==null){
            int freq = 1;
            double equalScore = 3.7;//对于点击的情况,默认用户评分为3.7
            score.setFrequency(freq);
            score.setAvgScore(((double)5 / (freq + 5)) * 3.5 + (freq / (double)(freq + 5)) * equalScore);
            score.setSeed(seed);
            score.setCompWord(compWord);
            scoreMapper.insert(score);
        }else {
            int frequency = score.getFrequency();
            frequency++;
            double equalScore = score.getAvgScore()+0.2;//对于点击的情况,相当于在评分基础上+0.2
            double avg_score = ((double) 5 / (frequency + 5)) * 3.5 + (frequency / (double) (frequency + 5)) * equalScore;
            score.setAvgScore(avg_score);
            score.setFrequency(frequency);
            UpdateWrapper<Score> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("seed", seed).eq("comp_word", compWord);
            scoreMapper.update(score, updateWrapper);
        }
    }

}
