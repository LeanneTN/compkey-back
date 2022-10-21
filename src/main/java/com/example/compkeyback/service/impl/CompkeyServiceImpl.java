package com.example.compkeyback.service.impl;

import com.example.compkeyback.service.CompkeyService;
import com.example.compkeyback.util.*;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.huaban.analysis.jieba.*;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@Service("compkeyService")
@Component
public class CompkeyServiceImpl implements CompkeyService {
    @Override
    public List<Map.Entry<String, Double>> compkey(String seedKey, int minNum) throws IOException {
        System.out.println("查询种子关键词的相关搜索记录...");
        //从清洗过的数据中提取出与种子关键字相关的搜索信息并保存
        int infoCounter = Util.search(seedKey,"src/main/resources/compkeyFiles/seedSearchResult.txt");

        System.out.println("========================================================");
        System.out.println("开始查找中介关键词...");
        //分词
        //方法一:单线程
        AnsjCutData.cut_clean("seedSearchResult.txt");
        //方法二:多线程
        //CutThread.divide("seedSearchResult.txt",infoCounter);
        //词频统计
        CountData.wordCount("cutted_seedSearchResult.txt",15);
        //确定中介关键词及相关搜索量
        //定义中介关键词列表
        List<String> midKeyList = new ArrayList<>();
        //定义种子搜索量s
        int s = 0;
        try(Scanner sc = new Scanner(new FileReader("src/main/resources/compkeyFiles/counted_cutted_seedSearchResult.txt"))) {
            int i = 0;
            String key;//定义关键词
            String value;//定义对应的词频
            while (sc.hasNextLine()&&i<minNum+1) {
                String line = sc.nextLine();
                key = line.split("=")[0];
                value = line.split("=")[1];
                //词频统计的第一行为种子，通过此确定种子的搜索量s
                if(i==0){
                    s = Integer.parseInt(value);
//                    System.out.println("确定种子搜索量为："+s);
                }else {
                    midKeyList.add(key);
                }
                i++;
            }
            System.out.println("确定中介关键词为："+midKeyList);
        }catch (Exception e){
            e.printStackTrace();
        }
        //确定搜索量sa：种子和中介关键词一起出现的搜索量
        //定义搜索量sa
        List<Integer> sa = new ArrayList<>();
        for (int i =0;i<midKeyList.size();i++){
            try(Scanner sc = new Scanner(new FileReader("src/main/resources/compkeyFiles/seedSearchResult.txt"))) {
                int count = 0;
                String midkey = midKeyList.get(i);//确定此次循环下的中介关键词
                while (sc.hasNextLine()) {
                    String line = sc.nextLine();
                    if (line.contains(seedKey)&&line.contains(midkey)){
                        count++;
                    }
                }
                sa.add(count);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
//        System.out.println("中介关键词与种子关键词同时出现的搜索量分别为："+sa);

        //计算权重：sa/s
        //定义权重weightList
        List<Double> weightList = new ArrayList<>();
        for (int i =0;i<midKeyList.size();i++){
            double value = (double)sa.get(i) / (double)s;
            weightList.add(value);
        }
//        System.out.println("中介关键词对应的权重分别为："+weightList);


        System.out.println("========================================================");
        System.out.println("开始查找竞争性关键词...");
        //寻找竞争性关键词：按照中介关键词对搜索内容分词，进行词频统计确定竞争性关键词，同时确定相关搜索量
        //定义搜索量ka：中介关键字和竞争关键字同时出现的搜索量(不含种子关键词)
        List<Integer> ka = new ArrayList<>();
        //定义竞争关键词列表compKeyList
        List<String> compKeyList = new ArrayList<>();
        for(int i =0;i< midKeyList.size();i++){
            String midKey = midKeyList.get(i);
            //筛选出不含种子关键词但含有中介关键字的搜索数据，存储在相应的文件中
            int infCounter = CompSearch.search(seedKey,midKey,String.format("src/main/resources/compkeyFiles/%sCompSearchResult.txt",midKey));
            //分词
            //单线程
            AnsjCutData.cut_clean(String.format("%sCompSearchResult.txt",midKey));
            //多线程
            //CutThread.divide(String.format("%sCompSearchResult.txt",midKey),infCounter);
            //词频统计
            CountData.wordCount(String.format("cutted_%sCompSearchResult.txt",midKey),10);

            //分析词频统计情况，确定中介关键词的对应竞争关键词并保存到compKeyList中，同时将|{ka}|的值保存到ka中
            try(Scanner sc = new Scanner(new FileReader(String.format("src/main/resources/compkeyFiles/counted_cutted_%sCompSearchResult.txt",midKey)))) {
                int j = 0;
                String key;//定义关键词
                String value;//定义对应的词频
                while (sc.hasNextLine()&&j<2) {
                    String line = sc.nextLine();
                    key = line.split("=")[0];
                    value = line.split("=")[1];
                    //词频统计的第一行为中介关键词，排除
                    //保存第二行的数据且不重复包含过作为竞争性关键词
                    if(j!=0){
                        if(!compKeyList.contains(key)){
                            compKeyList.add(key);
                            ka.add(Integer.parseInt(value));
                        }else {
                            System.out.println(midKey+"为重复竞争词，删除");
                            midKeyList.remove(i);
                            weightList.remove(i);
                            sa.remove(i);
                            i--;
                        }
                        System.out.println(compKeyList);
                    }
                    j++;
                }
                //当无相关搜索记录时需要更正最终输出数量
                if(j==0){
                    System.out.println(midKey+"无符合条件的相关搜索，删除");
                    midKeyList.remove(i);
                    weightList.remove(i);
                    sa.remove(i);
                    i--;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        System.out.println("确定竞争关键词为："+compKeyList);
//        System.out.println("确定中介关键字和竞争关键字同时出现的搜索量ka分别为："+ka);

        //定义中介关键词的搜索量a：中介关键词的搜索量
        List<Integer> a = new ArrayList<>();
        for (int i =0;i<midKeyList.size();i++){
            try(Scanner sc = new Scanner(new FileReader("src/main/resources/compkeyFiles/cleanResult.txt"))) {
                int count = 0;
                String midkey = midKeyList.get(i);//确定此次循环下的中介关键词
                while (sc.hasNextLine()) {
                    String line = sc.nextLine();
                    if (line.contains(midkey)){
                        count++;
                    }
                }
                a.add(count);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
//        System.out.println("确定中介关键词的搜索量分别为："+a);


        System.out.println("========================================================");
        System.out.println("开始计算竞争度...");
        System.out.println("中介关键词："+midKeyList);
        System.out.println("竞争关键词："+compKeyList);
        System.out.println("确定种子搜索量s为："+s);
        System.out.println("确定中介关键词与种子关键词同时出现的搜索量sa为："+sa);
        System.out.println("确定中介关键词对应的权重weight分别为："+weightList);
        System.out.println("确定中介关键字和竞争关键字同时出现的搜索量ka为："+ka);
        System.out.println("确定中介关键词的搜索量a分别为："+a);
        //计算comp
        List<Double> compResult = new ArrayList();
        for(int i = 0;i< midKeyList.size();i++){
            double ans;
            if(a.get(i) - sa.get(i) == 0) {
                ans = -1;
            } else {
                ans = (double)ka.get(i) / (double)(a.get(i) - sa.get(i));
            }
            double value = weightList.get(i)*(ans);
            compResult.add(value);
        }
        System.out.println("确定竞争度分别为："+compResult);

        //竞争性关键词排序打印
        System.out.println("“" + seedKey + "”的竞争性关键词关于竞争度排序如下:");
        Util.compMap(compKeyList,compResult);


        System.out.println("CompKey算法结束...");

        return Util.compMap(compKeyList,compResult);
    }

    @Override
    public String getStringValue(String statement) {
        // return ToAnalysis.parse(statement).toString();

        return null;
    }
}
