package com.example.compkeyback.util;

import java.io.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static com.example.compkeyback.util.Util.getFilePath;

/***
 * 存储的格式是：某一个单词+（所在的文档+(在相应的文档中出现的次数+行号）)
 */
public class InvertedEngine {
    public static void main(String[] args) throws IOException{
        String filePath = getFilePath("divideFile");
        String docIndex = getFilePath("indexRecord/docIndex.txt");
        String wordIndex = getFilePath("indexRecord/index.txt");
        FileInputStream inputStream = new FileInputStream(getFilePath("1000counted_filterCut.txt"));
        getFileIndex(filePath, docIndex);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String everyStr = null;
        while((everyStr = bufferedReader.readLine()) != null)
        {
            System.out.println(everyStr);
            getWordsFrequency(everyStr, docIndex, wordIndex);

        }
        System.out.println("Work Done!");
    }



    public static void getFileIndex(String filePath , String docIndex)
    {
        //通过传入的filePath找到文件所在，并将该文件下所有文件信息写到docIndex.txt中,这个文件专门用于储存对应的文件的信息
        File file = new File(filePath);
        File[] fileList = file.listFiles();
        BufferedWriter bufw = null;
        try
        {
            //将所有filePath下的文件路径写到docIndex文件中
            bufw = new BufferedWriter(new FileWriter(docIndex));
            for(int x = 0 ; x <fileList.length ; x++ )
            {
                String docPath = fileList[x].getAbsolutePath();
                bufw.write("DocID_" + x + "\t" + docPath);
                bufw.newLine();
                bufw.flush();//刷新写入
            }
        }
        catch (IOException e)
        {
            System.out.println("打开文件失败" + e);
        }
        finally
        {
            try
            {
                if(bufw != null)
                    bufw.close();
            }
            catch (IOException ex)
            {
                System.out.println("关闭文件失败" + ex);
            }
        }
    }

    public static void getWordsFrequency(String str, String docIndex, String wordIndex) throws IOException{
        int line = 0;
        //通过docIndex文件中的内容找到每个文件，并将文件中的内容做单词统计，有助于后续更新文件，不再合并为一个文件，目前还没有更新的时候只是原来一个文件
        TreeMap<String,TreeMap<Integer,String>> tmp = new TreeMap<>();//统计map
        BufferedReader bufferedReader = new BufferedReader(new FileReader(docIndex));//读取docIndex.txt
        // 一旦缓冲区被填充或者关闭写入器，缓冲区中的所有字符将被写入磁盘，因此减少了与磁盘的通信次数，故而使用bufferedWriter写入字符更快
        BufferedReader bufrDoc = null;
        String docIDandPath = null;
        while ((docIDandPath = bufferedReader.readLine())!=null){
            String[] docInfo = docIDandPath.split("\t");
            String docID = docInfo[0];
            String docPath = docInfo[1];//获取到docID和文件的路径
            bufrDoc = new BufferedReader(new FileReader(docPath));
            String  wordLine = null;
            while( (wordLine = bufrDoc.readLine()) != null)
            {
                line++;
                String[] words = wordLine.split("\n");//搜索记录是按照换行来区分的
                for(String wordOfDoc : words) {
                    //System.out.println(wordOfDoc);//正常
                    if (!wordOfDoc.equals("")&&wordOfDoc.contains(str))
                    {
                        wordDeal(str,wordOfDoc,docID,tmp,line);//将从docIndex读取到对应文件内容对做统计处理
                    }

                }
//                System.out.println("这里是文件"+docID);
            }
            System.out.println("文件"+docPath+"处理完毕");
            //将处理后的结果写入到wordIndex.txt文件中
        }
        String wordFreInfo = null;
        //entrySet方法得到的是HashMap中各个键值对映射关系的集合，然后Map.Entry中包含了getKey(),nextNode()方法的作用就是返回下一个结点
        Set<Map.Entry<String,TreeMap<Integer,String>>> entrySet = tmp.entrySet();
        Iterator<Map.Entry<String,TreeMap<Integer,String>>> it = entrySet.iterator();
        File file = new File(wordIndex);
        BufferedWriter bufferedWriter = null;
        //与写入的方法也有关系
        if(!file.exists()){
            bufferedWriter = new BufferedWriter(new FileWriter(wordIndex));
        }else {
            bufferedWriter = new BufferedWriter(new FileWriter(wordIndex,true));
        }
        while(it.hasNext())
        {
            Map.Entry<String,TreeMap<Integer,String>> em = it.next();
            wordFreInfo = em.getKey() +"\t" + em.getValue();
            bufferedWriter.write(wordFreInfo);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        }
        bufferedWriter.close();
        bufferedWriter.close();
        bufrDoc.close();
    }

    public static void wordDeal(String str, String wordOfDoc,String docID, TreeMap<String, TreeMap<Integer, String>>tmp, Integer i){
        wordOfDoc = wordOfDoc.toLowerCase();
        str = str.toLowerCase();
        if (!tmp.containsKey(str+"+"+docID)){//第一个string
            //单词在统计中是首次出现
            TreeMap<Integer, String> tmpST = new TreeMap<>();
            String record = ""+i;
            tmpST.put(1,record);//首次出现
            tmp.put(str+"+"+docID,tmpST);
        }else {//若不是首次出现，则将count++后，再将信息回写到tmpST中
            TreeMap<Integer, String> tmpST = tmp.get(str+"+"+docID);
            Integer count = tmpST.lastKey();
            String record = tmpST.get(count);
            record +="+"+i;//以加号作为分割
            tmpST.pollLastEntry();
            count++;
            tmpST.put(count,record);
            tmp.put(str+"+"+docID,tmpST);	//将最新结果回写到tmp中
        }
    }
    
}
