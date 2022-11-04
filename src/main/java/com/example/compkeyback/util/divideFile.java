package com.example.compkeyback.util;

import java.io.*;

import static com.example.compkeyback.util.Util.getFilePath;

public class divideFile {
    public static Integer getLines(String FileName) {
        try {
            String filePath = getFilePath(FileName);
            FileReader read = new FileReader(filePath);
            BufferedReader br = new BufferedReader(read);
            String row;

            int rownum = 1;
            while ((row = br.readLine()) != null) {
                rownum ++;
            }
            System.out.println("rownum="+rownum);
            return rownum;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }
    public static void splitFile(String FileName) {
        try {
            String filePath = getFilePath(FileName);
            FileReader read = new FileReader(filePath);
            BufferedReader br = new BufferedReader(read);
            String row;
            int rownum = 1;
            int fileNo = 1;
            int each = getLines(FileName)/10;//把文件分成十份，这里后面可以作为一个参数再来调节
            String DFile = getFilePath("divideFile/"+FileName+"_d"+fileNo+".txt");
            FileWriter fw = new FileWriter(DFile);
            while ((row = br.readLine()) != null) {
                rownum ++;
                fw.append(row + "\n");
                if((rownum / each) > (fileNo - 1)){
                    fw.close();
                    fileNo ++ ;
                    fw = new FileWriter(getFilePath("divideFile/"+FileName+"_d"+fileNo+".txt"));
                }
            }
            fw.close();
            System.out.println("rownum="+rownum+";fileNo="+fileNo);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String args[]){
        splitFile("cleanResult.txt");

    }
}