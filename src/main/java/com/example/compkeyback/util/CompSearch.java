package com.example.compkeyback.util;

import java.io.*;
import java.util.Scanner;

public class CompSearch {
    /***
     * 筛选出不含种子关键词，但是含有中介关键字的搜索数据，存储在相应的文件中
     * @param seed
     * @param mid
     * @param fileOut
     * @throws IOException
     */
    public static int search(String seed,String mid,String fileOut) throws IOException {
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
}
