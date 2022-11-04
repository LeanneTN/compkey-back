package com.example.compkeyback.util;

import java.io.*;
import java.util.*;

public class Util {
    /***
     * 搜索与种子关键词相关的记录并保存
     * @param key
     * @throws IOException
     */
    public static int search(String key,String fileOut) throws IOException {
        //定义搜索记录数
        int counter = 0;
        //定义输出流，写入搜索到的匹配数据
        OutputStreamWriter outStream = new OutputStreamWriter(new FileOutputStream(new File(fileOut)), "UTF-8");
        BufferedWriter bw = new BufferedWriter(outStream);
        try(Scanner sc = new Scanner(new FileReader("src/main/resources/compkeyFiles/cleanResult.txt"))) {
            String line;
            while (sc.hasNextLine()) {
                line = sc.nextLine();
                if(line.contains(key)){
                    counter++;
                    bw.append(line);
                    bw.newLine();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        bw.close();
        System.out.println("=========="+key+"的相关搜索记录查询已完成==========");
        return counter;
    }

    /***
     * 停用词移除判断
     * @param str
     * @return boolean
     * @throws IOException
     */
    public static boolean ifRemoveStopWords(String str) throws IOException{
        boolean flag = true;
        String value = null;
        String fileName = "src/main/resources/stopWords/cn_stopwords.txt";
        try (Scanner sc = new Scanner(new FileReader(fileName))){
            while (sc.hasNextLine()){
                value = sc.nextLine();
                if(Objects.equals(value, str)){
                    flag = false;
                    break;
                }
            }
        }
        return flag;
    }

    static class MyComparator implements Comparator<Map.Entry>{
        public int compare(Map.Entry o1, Map.Entry o2){
            return ((Double)o1.getValue()).compareTo((Double) o2.getValue());
        }
    }

    public static List<Map.Entry<String,Double>> compMap(List<String> keyList, List<Double> valueList){
        Map<String,Double> map = new TreeMap<>();//用TreeMap储存
        for(int i=0;i< keyList.size();i++){
            map.put(keyList.get(i),valueList.get(i));
        }
        //将map转换为entryset，再转换成保存Entry对象的list
        List<Map.Entry<String,Double>> entrys = new ArrayList<>(map.entrySet());
        //调用Collections.sort(list,comparator)方法把Entry-list排序
        Collections.sort(entrys, new MyComparator());
        Collections.reverse(entrys);
        //遍历排序好的Entry-list,输出结果
        for(Map.Entry<String,Double> entry:entrys){
            System.out.println(entry.getKey()+" : "+entry.getValue());
        }
        return entrys;
    }


    public static Map<String,Integer> sort(Map<String,Integer> map){
        //获取entrySet
        Set<Map.Entry<String, Integer>> mapEntries = map.entrySet();
        //使用链表来对集合进行排序，使用LinkedList，利于插入元素
        List<Map.Entry<String, Integer>> result = new LinkedList<>(mapEntries);
        //自定义比较器来比较链表中的元素
        Collections.sort(result, new Comparator<Map.Entry<String, Integer>>() {
            //基于entry的值（Entry.getValue()），来排序链表
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        //将排好序的存入到LinkedHashMap(可保持顺序)中，需要存储键和值信息对到新的映射中。
        Map<String, Integer> linkMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> newEntry : result) {
            linkMap.put(newEntry.getKey(), newEntry.getValue());
        }
        return linkMap;
    }

    public static Map<String,Double> sortComp(Map<String,Double> map){
        //获取entrySet
        Set<Map.Entry<String, Double>> mapEntries = map.entrySet();
        //使用链表来对集合进行排序，使用LinkedList，利于插入元素
        List<Map.Entry<String, Double>> result = new LinkedList<>(mapEntries);
        //自定义比较器来比较链表中的元素
        Collections.sort(result, new Comparator<Map.Entry<String, Double>>() {
            //基于entry的值（Entry.getValue()），来排序链表
            @Override
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        //将排好序的存入到LinkedHashMap(可保持顺序)中，需要存储键和值信息对到新的映射中。
        Map<String, Double> linkMap = new LinkedHashMap<>();
        for (Map.Entry<String, Double> newEntry : result) {
            linkMap.put(newEntry.getKey(), newEntry.getValue());
        }
        return linkMap;
    }

    public static int compSearch(String seed,String mid,String fileOut) throws IOException {
        //定义搜索记录数
        int counter = 0;
        //定义输出流，写入搜索到的匹配数据
        OutputStreamWriter outStream = new OutputStreamWriter(new FileOutputStream(new File(fileOut)), "UTF-8");
        BufferedWriter bw = new BufferedWriter(outStream);
        try(Scanner sc = new Scanner(new FileReader("src/main/resources/compkeyFiles/cleanResult.txt"))) {
            String line;
            while (sc.hasNextLine()) {
                line = sc.nextLine();
                //包含中介但不包含种子
                if(!line.contains(seed)&&line.contains(mid)){
                    counter++;
                    bw.append(line);
                    bw.newLine();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        bw.close();
        System.out.println("=========="+mid+"的相关搜索记录查询已完成==========");
        return counter;
    }

    public static String getFilePath(String FileName){
        String root = System.getProperty("user.dir");
        String subPath = "src/main/resources/files/";
        String filePath = root+File.separator+subPath+FileName;
        return filePath;
    }

    public static double sigmoid(int value){
        double ey = Math.pow(Math.E,-value);
        double result = 1/(1+ey);
        return result;
    }
}
