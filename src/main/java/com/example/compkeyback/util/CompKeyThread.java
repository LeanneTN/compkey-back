package com.example.compkeyback.util;

import com.huaban.analysis.jieba.JiebaSegmenter;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

public class CompKeyThread {
    public static Map<String,Integer> cut_count(int taskSize, int endNum) throws ExecutionException, InterruptedException {
        //定义最终的返回值
        Map<String,Integer> countMap = new TreeMap<>();

        System.out.println("----多线程程序开始运行----");
        Date date1 = new Date();
        // 创建一个线程池
        ExecutorService pool = Executors.newFixedThreadPool(taskSize);
        // 创建多个有返回值的任务
        List<Future> list = new ArrayList();
        int evNum = endNum/taskSize;
        int startNum = 1;
        for (int i = 0; i < taskSize; i++) {
            Callable c = new MidKeyThread.MyCallable(0,0);
            if(i==taskSize-1){
                c = new MidKeyThread.MyCallable(startNum,endNum);
            }else{
                c = new MidKeyThread.MyCallable(startNum,startNum+evNum-1);
            }
            startNum = startNum+evNum;
            // 执行任务并获取Future对象
            Future f = pool.submit(c);
            list.add(f);
        }

        // 关闭线程池
        pool.shutdown();
        // 获取所有并发任务的运行结果
        for (Future f : list) {
            // 从Future对象上获取任务的返回值
            Map<String,Integer> map = (Map<String, Integer>) f.get();
            for(String key : map.keySet()){
                countMap.put(key,countMap.containsKey(key)?countMap.get(key)+map.get(key):map.get(key));
            }
        }
        countMap = Util.sort(countMap);
        Date date2 = new Date();
        System.out.println(countMap);
        System.out.println("----多线程程序结束运行----程序运行时间【" + (date2.getTime() - date1.getTime()) + "毫秒】");
        return countMap;
    }

    static class MyCallable implements Callable<Map<String, Integer>> {
        int startNum;
        int endNum;
        MyCallable(int startNum,int endNum) {
            this.startNum = startNum;
            this.endNum = endNum;
        }

        public Map<String, Integer> call() throws Exception {
            JiebaSegmenter jiebaSegmenter = new JiebaSegmenter();
            Map<String,Integer> map = new TreeMap<>();
            try {
                //对输入的字符串进行分词并存储
                Scanner sc = new Scanner(new File("src/main/resources/compkeyFiles/midSearchResult.txt"));
                int counter=0;
                while (sc.hasNextLine()&&counter<=endNum){
                    String line = sc.nextLine();
                    counter++;
                    if(counter>=startNum){
                        List<String> strings = jiebaSegmenter.sentenceProcess(line);
                        for (String s : strings) {
                            if(Util.ifRemoveStopWords(s)&&s.length()>=2){
                                map.put(s,map.containsKey(s)?map.get(s)+1:1);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return map;
        }
    }
}
