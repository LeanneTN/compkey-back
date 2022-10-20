package com.example.compkeyback.util;


import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

public class CutTread {
    public static class Thread_writeFile extends Thread{

        private String inFilename;
        private int startNum;
        private int endNum;

        //构造函数
        public Thread_writeFile(String inFilename,int startNum,int endNum){
            this.inFilename = inFilename;
            this.startNum = startNum;
            this.endNum = endNum;
        }

        public void run(){
            Calendar calstart=Calendar.getInstance();
            File file=new File(String.format("src/main/resources/compkeyFiles/cutted_%s",inFilename));
            try {
                //对输入的字符串进行分词并存储
                StringBuffer sb = new StringBuffer();
                Scanner sc = new Scanner(new File(String.format("src/main/resources/compkeyFiles/%s",inFilename)));
                int counter=0;
                while (sc.hasNextLine()&&counter<=endNum){
                    String line = sc.nextLine();
                    counter++;
                    if(counter>=startNum){
                        Result result = ToAnalysis.parse(line);
                        List<Term> terms = result.getTerms();
                        for (Term term : terms) {
                            if(Util.ifRemoveStopWords(term.getName())&&term.getName().length()>=2){
                                sb.append(term.getName());
                                sb.append("\n");
                            }
                        }
                    }
                }
                if(!file.exists())
                    file.createNewFile();
                //对该文件加锁
                RandomAccessFile out = new RandomAccessFile(file, "rw");
                FileChannel fcout=out.getChannel();
                FileLock flout=null;

                while(true){
                    try {
                        flout = fcout.tryLock();
                        break;
                    } catch (Exception e) {
                        System.out.println("有其他线程正在操作该文件，当前线程休眠1000毫秒");
                        sleep(1000);
                    }

                }

                //定位当前待写入的位置
                long pos = out.length();
                out.seek(pos);

                sleep((10));
                //写入分词结果
                System.out.println(sb);
                out.write(sb.toString().getBytes("utf-8"));

                flout.release();
                fcout.close();
                out.close();
                out=null;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Calendar calend=Calendar.getInstance();
            System.out.println("写文件共花了"+(calend.getTimeInMillis()-calstart.getTimeInMillis())+"毫秒");
        }
    }


    public static class Thread_readFile extends Thread {
        public void run() {
            try {
                Calendar calstart = Calendar.getInstance();
                sleep(5000);
                File file = new File("D:/test.txt");

                //给该文件加锁
                RandomAccessFile fis = new RandomAccessFile(file, "rw");
                FileChannel fcin = fis.getChannel();
                FileLock flin = null;
                while (true) {
                    try {
                        flin = fcin.tryLock();
                        break;
                    } catch (Exception e) {
                        System.out.println("有其他线程正在操作该文件，当前线程休眠1000毫秒");
                        sleep(1000);
                    }

                }
                byte[] buf = new byte[1024];
                StringBuffer sb = new StringBuffer();
                while ((fis.read(buf)) != -1) {
                    sb.append(new String(buf, "utf-8"));
                    buf = new byte[1024];
                }

                System.out.println(sb.toString());

                flin.release();
                fcin.close();
                fis.close();
                fis = null;

                Calendar calend = Calendar.getInstance();
                System.out.println("读文件共花了" + (calend.getTimeInMillis() - calstart.getTimeInMillis()) + "秒");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void divide(String inFilename,int totalNum) throws IOException {
        String outFilename = String.format("src/main/resources/compkeyFiles/cutted_%s",inFilename);
        File file=new File(outFilename);
        if (file.exists()){
            FileWriter fw = new FileWriter(file);
            fw.write("");
        }
        //定义线程数
        int threadNum = 9;
        int startNum = 1;
        int evNum = totalNum/threadNum;
        Thread_writeFile thf1=new Thread_writeFile(inFilename,startNum,startNum+evNum-1);
        thf1.start();
        startNum = startNum+evNum;
        Thread_writeFile thf2=new Thread_writeFile(inFilename,startNum,startNum+evNum-1);
        thf2.start();
        startNum = startNum+evNum;
        Thread_writeFile thf3=new Thread_writeFile(inFilename,startNum,startNum+evNum-1);
        thf3.start();
        startNum = startNum+evNum;
        Thread_writeFile thf4=new Thread_writeFile(inFilename,startNum,startNum+evNum-1);
        thf4.start();
        startNum = startNum+evNum;
        Thread_writeFile thf5=new Thread_writeFile(inFilename,startNum,startNum+evNum-1);
        thf5.start();
        startNum = startNum+evNum;
        Thread_writeFile thf6=new Thread_writeFile(inFilename,startNum,startNum+evNum-1);
        thf6.start();
        startNum = startNum+evNum;
        Thread_writeFile thf7=new Thread_writeFile(inFilename,startNum,startNum+evNum-1);
        thf7.start();
        startNum = startNum+evNum;
        Thread_writeFile thf8=new Thread_writeFile(inFilename,startNum,startNum+evNum-1);
        thf8.start();
        startNum = startNum+evNum;
        Thread_writeFile thf9=new Thread_writeFile(inFilename,startNum,totalNum);
        thf9.start();


        //进程全部结束才返回值
        while (thf1.isAlive()||thf2.isAlive()||thf3.isAlive()||thf4.isAlive()||thf5.isAlive()||thf6.isAlive()||thf7.isAlive()||thf8.isAlive()||thf9.isAlive()){
            continue;
        }
    }
}
