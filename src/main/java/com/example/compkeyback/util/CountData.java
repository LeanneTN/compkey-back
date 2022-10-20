package com.example.compkeyback.util;

import java.io.*;
import java.util.*;

public class CountData {
    /**
     * 找出map中value最大的entry, 返回此entry, 并在map删除此entry
     * @param map
     * @return
     */
    public static Map.Entry<String, Integer> getMax(Map<String, Integer> map){
        if (map.size() == 0){
            return null;
        }
        Map.Entry<String, Integer> maxEntry = null;
        boolean flag = false;
        Iterator<Map.Entry<String, Integer>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, Integer> entry = iterator.next();
            if (!flag){
                maxEntry = entry;
                flag = true;
            }
            if (entry.getValue() > maxEntry.getValue()){
                maxEntry = entry;
            }
        }
        map.remove(maxEntry.getKey());
        return maxEntry;
    }

    public static void wordCount(String filename,int num) throws IOException {
        //定义缓冲字符输入流
        BufferedReader br = new BufferedReader(new FileReader(String.format("src/main/resources/compkeyFiles/%s",filename)));
        //定义输出流，写入搜索到的匹配数据
        OutputStreamWriter outStream = new OutputStreamWriter(new FileOutputStream(new File(String.format("src/main/resources/compkeyFiles/counted_%s",filename))), "UTF-8");
        BufferedWriter bw = new BufferedWriter(outStream);
        //定义词频统计哈希映射
        Map<String, Integer> wc = new HashMap<>();
        // 定义行字符串变量
        String nextline = "";
        // 读取文件，遍历所有行
        while ((nextline = br.readLine()) != null) {
            wc.put(nextline, wc.containsKey(nextline)? wc.get(nextline)+1 : 1);
            // 按空格拆分，得到单词数组
//            String[] words = nextline.split(" ");
            // 遍历单词数组
//            for (String word:words) {
//                wc.put(word, wc.containsKey(word)? wc.get(word)+1 : 1);
//            }
        }
        System.out.println("词频统计完成！！！++++");
        //排序输出
        Iterator<Map.Entry<String, Integer>> iterator = wc.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, Integer> entry = iterator.next();
//            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        List<Map.Entry<String, Integer>> list = new ArrayList<>();
        Map.Entry<String, Integer> entry;
        while ((entry = getMax(wc)) != null){
            list.add(entry);
        }
        //存储到txt中
        System.out.println("============统计结果============");
        int number = (list.size()<num? list.size() : num);
        if(list.size()!=0){
            for (int i=0;i<number;i++){
                bw.append(list.get(i).toString());
                bw.newLine();
                System.out.println("第"+(i+1)+": "+list.get(i));
            }
        }else {
            System.out.println("无数据");
        }

        //关闭
        bw.close();
    }
}
