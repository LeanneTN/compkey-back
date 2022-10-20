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
        //遍历排序好的Entry-list,输出结果
        for(Map.Entry<String,Double> entry:entrys){
            System.out.println(entry.getKey()+" : "+entry.getValue());
        }
        return entrys;
    }

    public static String getFilePath(String FileName){
        String root = System.getProperty("user.dir");
        String subPath = "src/main/resources/files/";
        String filePath = root+File.separator+subPath+FileName;
        return filePath;
    }
}
