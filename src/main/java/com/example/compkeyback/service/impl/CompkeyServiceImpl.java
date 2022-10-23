package com.example.compkeyback.service.impl;

import com.example.compkeyback.dto.CompkeyResult;
import com.example.compkeyback.service.CompkeyService;
import com.example.compkeyback.util.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Service("compkeyService")
@Component
public class CompkeyServiceImpl implements CompkeyService {
    //原方法
//    @Override
//    public CompkeyResult compkey(String seedKey, int minNum) throws IOException {
//        System.out.println("查询种子关键词的相关搜索记录...");
//        //从清洗过的数据中提取出与种子关键字相关的搜索信息并保存
//        int infoCounter = Util.search(seedKey,"src/main/resources/compkeyFiles/seedSearchResult.txt");
//
//        System.out.println("========================================================");
//        System.out.println("开始查找中介关键词...");
//        //分词
//        //方法一:单线程
//        // AnsjCutData.cut_clean("seedSearchResult.txt");
//        //方法二:多线程
//        CutThread.divide("seedSearchResult.txt",infoCounter);
//        //词频统计
//        CountData.wordCount("cutted_seedSearchResult.txt",15);
//        //确定中介关键词及相关搜索量
//        //定义中介关键词列表
//        List<String> midKeyList = new ArrayList<>();
//        //定义种子搜索量s
//        int s = 0;
//        try(Scanner sc = new Scanner(new FileReader("src/main/resources/compkeyFiles/counted_cutted_seedSearchResult.txt"))) {
//            int i = 0;
//            String key;//定义关键词
//            String value;//定义对应的词频
//            while (sc.hasNextLine()&&i<minNum+1) {
//                String line = sc.nextLine();
//                key = line.split("=")[0];
//                value = line.split("=")[1];
//                //词频统计的第一行为种子，通过此确定种子的搜索量s
//                if(i==0){
//                    s = Integer.parseInt(value);
////                    System.out.println("确定种子搜索量为："+s);
//                }else {
//                    midKeyList.add(key);
//                }
//                i++;
//            }
//            System.out.println("确定中介关键词为："+midKeyList);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        //确定搜索量sa：种子和中介关键词一起出现的搜索量
//        //定义搜索量sa
//        List<Integer> sa = new ArrayList<>();
//        for (int i =0;i<midKeyList.size();i++){
//            try(Scanner sc = new Scanner(new FileReader("src/main/resources/compkeyFiles/seedSearchResult.txt"))) {
//                int count = 0;
//                String midkey = midKeyList.get(i);//确定此次循环下的中介关键词
//                while (sc.hasNextLine()) {
//                    String line = sc.nextLine();
//                    if (line.contains(seedKey)&&line.contains(midkey)){
//                        count++;
//                    }
//                }
//                sa.add(count);
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }
////        System.out.println("中介关键词与种子关键词同时出现的搜索量分别为："+sa);
//
//        //计算权重：sa/s
//        //定义权重weightList
//        List<Double> weightList = new ArrayList<>();
//        for (int i =0;i<midKeyList.size();i++){
//            double value = (double)sa.get(i) / (double)s;
//            weightList.add(value);
//        }
////        System.out.println("中介关键词对应的权重分别为："+weightList);
//
//
//        System.out.println("========================================================");
//        System.out.println("开始查找竞争性关键词...");
//        //寻找竞争性关键词：按照中介关键词对搜索内容分词，进行词频统计确定竞争性关键词，同时确定相关搜索量
//        //定义搜索量ka：中介关键字和竞争关键字同时出现的搜索量(不含种子关键词)
//        List<Integer> ka = new ArrayList<>();
//        //定义竞争关键词列表compKeyList
//        List<String> compKeyList = new ArrayList<>();
//        for(int i =0;i< midKeyList.size();i++){
//            String midKey = midKeyList.get(i);
//            //筛选出不含种子关键词但含有中介关键字的搜索数据，存储在相应的文件中
//            int infCounter = CompSearch.search(seedKey,midKey,String.format("src/main/resources/compkeyFiles/%sCompSearchResult.txt",midKey));
//            //分词
//            //单线程
//            // AnsjCutData.cut_clean(String.format("%sCompSearchResult.txt",midKey));
//            //多线程
//            CutThread.divide(String.format("%sCompSearchResult.txt",midKey),infCounter);
//            //词频统计
//            CountData.wordCount(String.format("cutted_%sCompSearchResult.txt",midKey),10);
//
//            //分析词频统计情况，确定中介关键词的对应竞争关键词并保存到compKeyList中，同时将|{ka}|的值保存到ka中
//            try(Scanner sc = new Scanner(new FileReader(String.format("src/main/resources/compkeyFiles/counted_cutted_%sCompSearchResult.txt",midKey)))) {
//                int j = 0;
//                String key;//定义关键词
//                String value;//定义对应的词频
//                while (sc.hasNextLine()&&j<2) {
//                    String line = sc.nextLine();
//                    key = line.split("=")[0];
//                    value = line.split("=")[1];
//                    //词频统计的第一行为中介关键词，排除
//                    //保存第二行的数据且不重复包含过作为竞争性关键词
//                    if(j!=0){
//                        if(!compKeyList.contains(key)){
//                            compKeyList.add(key);
//                            ka.add(Integer.parseInt(value));
//                        }else {
//                            System.out.println(midKey+"为重复竞争词，删除");
//                            midKeyList.remove(i);
//                            weightList.remove(i);
//                            sa.remove(i);
//                            i--;
//                        }
//                        System.out.println(compKeyList);
//                    }
//                    j++;
//                }
//                //当无相关搜索记录时需要更正最终输出数量
//                if(j==0){
//                    System.out.println(midKey+"无符合条件的相关搜索，删除");
//                    midKeyList.remove(i);
//                    weightList.remove(i);
//                    sa.remove(i);
//                    i--;
//                }
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }
//        System.out.println("确定竞争关键词为："+compKeyList);
////        System.out.println("确定中介关键字和竞争关键字同时出现的搜索量ka分别为："+ka);
//
//        //定义中介关键词的搜索量a：中介关键词的搜索量
//        List<Integer> a = new ArrayList<>();
//        for (int i =0;i<midKeyList.size();i++){
//            try(Scanner sc = new Scanner(new FileReader("src/main/resources/compkeyFiles/cleanResult.txt"))) {
//                int count = 0;
//                String midkey = midKeyList.get(i);//确定此次循环下的中介关键词
//                while (sc.hasNextLine()) {
//                    String line = sc.nextLine();
//                    if (line.contains(midkey)){
//                        count++;
//                    }
//                }
//                a.add(count);
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }
////        System.out.println("确定中介关键词的搜索量分别为："+a);
//
//
//        System.out.println("========================================================");
//        System.out.println("开始计算竞争度...");
//        System.out.println("中介关键词："+midKeyList);
//        System.out.println("竞争关键词："+compKeyList);
//        System.out.println("确定种子搜索量s为："+s);
//        System.out.println("确定中介关键词与种子关键词同时出现的搜索量sa为："+sa);
//        System.out.println("确定中介关键词对应的权重weight分别为："+weightList);
//        System.out.println("确定中介关键字和竞争关键字同时出现的搜索量ka为："+ka);
//        System.out.println("确定中介关键词的搜索量a分别为："+a);
//        //计算comp
//        List<Double> compResult = new ArrayList();
//        for(int i = 0;i< midKeyList.size();i++){
//            double ans;
//            if(a.get(i) - sa.get(i) == 0) {
//                ans = -1;
//            } else {
//                ans = (double)ka.get(i) / (double)(a.get(i) - sa.get(i));
//            }
//            double value = weightList.get(i)*(ans);
//            compResult.add(value);
//        }
//        System.out.println("确定竞争度分别为："+compResult);
//
//        //竞争性关键词排序打印
//        System.out.println("“" + seedKey + "”的竞争性关键词关于竞争度排序如下:");
//        Util.compMap(compKeyList,compResult);
//
//
//        System.out.println("CompKey算法结束...");
//
//        CompkeyResult compkey_result = new CompkeyResult();
//        compkey_result.setCompkeyList(compKeyList);
//        compkey_result.setCompkeyResult(compResult);
//
//        return compkey_result;
//    }

    //new method
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
}
