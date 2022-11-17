package com.example.compkeyback.dynamicUpdate;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.compkeyback.domain.Score;
import com.example.compkeyback.dto.Cache;
import com.example.compkeyback.dto.CompkeyResult;
import com.example.compkeyback.persistence.CacheMapper;
import com.example.compkeyback.persistence.ScoreMapper;
import com.example.compkeyback.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
@Component
public class Update {
    @Autowired
    private ScoreMapper scoreMapper;
    @Autowired
    private CacheMapper cacheMapper;


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

        System.out.println(compkeyResult);
        return compkeyResult;
    }

    public Score getScoreByCompkey(String seedWord,String compWord) {
        QueryWrapper<Score> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("seed", seedWord).eq("comp_word", compWord);
        Score score = scoreMapper.selectOne(queryWrapper);
        return score;
    }

    public double compDegreeCompute(double avgScore, double degree, int frequency) {
        double reScore = avgScore / 5.0;//将0-5的评分转化为0-1
        double alpha = Util.mysigmoid(frequency,0.001);
        double finalScore = reScore*alpha + degree*(1-alpha);
        return finalScore;
    }

    public void updateDB(String seed,HashMap<String,Double>newHash,List<String>deleteList){
        //删除操作
        for (int i=0;i<deleteList.size();i++){
            String compWord = deleteList.get(i);
            QueryWrapper<Cache> cacheQueryWrapper = new QueryWrapper<>();
            QueryWrapper<Score> scoreQueryWrapper = new QueryWrapper<>();
            cacheQueryWrapper.eq("seed_word",seed).eq("comp_word",compWord);
            scoreQueryWrapper.eq("seed", seed).eq("comp_word", compWord);
            cacheMapper.delete(cacheQueryWrapper);
            scoreMapper.delete(scoreQueryWrapper);
        }
        //添加操作
        for (HashMap.Entry<String, Double> entry : newHash.entrySet()){
            String compWord = entry.getKey();
            Double compDegree = entry.getValue();
            Cache cache = new Cache();
            cache.setSeedWord(seed);
            cache.setCompWord(compWord);
            cache.setCompDegree(compDegree.toString());
            cacheMapper.insert(cache);
        }
    }

    public void dyUpdate() throws IOException, ExecutionException, InterruptedException {
        //定义种子及其竞争词的map
        Map<String,HashMap<String,Double>> seedCompPair = new HashMap<>();
        QueryWrapper<Cache> cacheQueryWrapper = new QueryWrapper<>();
        List<Cache> cacheList = cacheMapper.selectList(cacheQueryWrapper);
        HashMap<String,Double> compHash = new HashMap<>();
        for (int i = 0;i<cacheList.size();i++){
            //获取当下的种子词
            String seedWord = cacheList.get(i).getSeedWord();
            //判断读取的数据是否是新的种子
            Set<String> keySet = seedCompPair.keySet();
            for(String key : keySet){
                if(key.equals(seedWord)){
                    compHash = new HashMap<>();
                }
            }
            compHash.put(cacheList.get(i).getCompWord(),new Double(cacheList.get(i).getCompDegree()));
            seedCompPair.put(seedWord,compHash);
        }

        //检查用户评价后的竞争词情况
        for (HashMap.Entry<String, HashMap<String,Double>> entry : seedCompPair.entrySet()) {
            String seedWord = entry.getKey();
            HashMap<String,Double> compDegreePair = entry.getValue();
            //定义该种子关键词下需要保留或删去的词对
            List<String> savePair = new ArrayList<>();
            List<String> deletePair = new ArrayList<>();
            //遍历某个种子的所有竞争词及竞争度
            for (Map.Entry<String,Double> entry1 : compDegreePair.entrySet()){
                String compWord = entry1.getKey();
                Double compDegree = entry1.getValue();
                Score score = getScoreByCompkey(seedWord,compWord);
                Double resultcomp = compDegreeCompute(score.getAvgScore(),compDegree,score.getFrequency());
                //根据用户评价调整后的分数小于原本竞争度时选择删去
                if(resultcomp<compDegree){
                    deletePair.add(compWord);
                }else {
                    savePair.add(compWord);
                }
            }

            //更新数据库
            if(deletePair.size()>0){
                HashMap<String,Double> newHash = new HashMap<>();
                int num = deletePair.size();
                CompkeyResult compkeyResult = compkey(seedWord,10);
                List<String> compWordList = compkeyResult.getCompkeyList();
                List<Double> compDegreeList = compkeyResult.getCompkeyResult();

                for(int j=0;j<compWordList.size()&&num>0;j++){
                    String compWord = compWordList.get(j);
                    boolean flag = true;
                    for(int i=0;i<savePair.size();i++){
                        if(compWord == savePair.get(i)){
                            flag = false;
                            break;
                        }
                    }
                    for(int i=0;i<deletePair.size();i++){
                        if(compWord == deletePair.get(i)){
                            flag = false;
                            break;
                        }
                    }
                    if(flag){
                        newHash.put(compWord,compDegreeList.get(j));
                        num--;
                    }
                }

                updateDB(seedWord,newHash,deletePair);
            }

        }
    }
}
