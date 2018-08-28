package com.kouyy.ikweb.tfidf;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.*;
import java.util.*;

/**
 * 文件夹里面所有文件的TFIDF计算，增加了排序
 * @author kouyy
 */
public class TfIdfUtil {

    /**
     * 文件夹下所有文件绝对路径集合
     */
    private static ArrayList<String> FileList = new ArrayList<String>();

    /**
     * 读取文件夹filepath里的所有文件
     * @param filepath
     * @return 所有文件的绝对路径集合
     */
    public static List<String> readDirs(String filepath){
        try
        {
            File file = new File(filepath);
            if(!file.isDirectory())
            {
                System.out.println("输入的[]");
                System.out.println("filepath:" + file.getAbsolutePath());
            }
            else
            {
                String[] flist = file.list();
                for(int i = 0; i < flist.length; i++)
                {
                    File newfile = new File(filepath + "\\" + flist[i]);
                    if(!newfile.isDirectory())
                    {
                        FileList.add(newfile.getAbsolutePath());
                    }
                    else if(newfile.isDirectory())
                    {
                        readDirs(filepath + "\\" + flist[i]);
                    }
                }
            }
        }catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
        return FileList;
    }

    /**
     * 读文件
     * @param file
     * @return
     * @throws IOException
     */
    public static String readFile(String file) throws IOException
    {
        StringBuffer strSb = new StringBuffer();
        InputStreamReader inStrR = new InputStreamReader(new FileInputStream(file), "utf-8");
        BufferedReader br = new BufferedReader(inStrR);
        String line = br.readLine();
        while(line != null){
            strSb.append(line).append("\r\n");
            line = br.readLine();
        }
        return strSb.toString();
    }

    /**
     * 利用IK分词器切词
     * @param file  文件的绝对路径
     * @return
     * @throws IOException
     */
    public static ArrayList<String> cutWords(String file) throws IOException{
        ArrayList<String> words = new ArrayList<String>();
        String text = TfIdfUtil.readFile(file);
        //创建分词器
        Analyzer analyzer = new IKAnalyzer(true);
        //创建读取字符串的输入流
        StringReader reader = new StringReader(text);

        //分词器通过TokenStream有效的获取到分词单元
        TokenStream ts = analyzer.tokenStream("", reader);
        ts.reset();

        while(ts.incrementToken()){
            CharTermAttribute term=ts.getAttribute(CharTermAttribute.class);
            words.add(term.toString());
        }
        analyzer.close();
        reader.close();
        return words;
    }

    /**
     * 计算每个单词在该文件中的次数
     * @param cutwords
     * @return
     */
    public static HashMap<String, Integer> normalTF(ArrayList<String> cutwords){
        HashMap<String, Integer> resTF = new HashMap<String, Integer>();
        for(String word : cutwords){
            if(resTF.get(word) == null){
                resTF.put(word, 1);
            }
            else{
                resTF.put(word, resTF.get(word) + 1);
            }
        }
        return resTF;
    }

    /**
     * 计算单个文章的tf，并从高向低存储
     * @param cutwords
     * @return
     */
    public static HashMap<String, Float> tf(ArrayList<String> cutwords){
        HashMap<String, Float> resTF = new HashMap<String, Float>();
        //获取所有单词的数量
        int wordLen = cutwords.size();
        HashMap<String, Integer> intTF = TfIdfUtil.normalTF(cutwords);
        Iterator iter = intTF.entrySet().iterator();
        while(iter.hasNext()){
            Map.Entry entry = (Map.Entry)iter.next();
            //计算每个单词的词频并存入resTF中
            resTF.put(entry.getKey().toString(), Float.parseFloat(entry.getValue().toString()) / wordLen);
        }
        return RankMapUtil.sortMapByValue(resTF);
    }

    /**
     * 暂时没用
     * @param dirc
     * @return
     * @throws IOException
     */
    public static HashMap<String, HashMap<String, Integer>> normalTFAllFiles(String dirc) throws IOException{
        HashMap<String, HashMap<String, Integer>> allNormalTF = new HashMap<String, HashMap<String,Integer>>();

        List<String> filelist = TfIdfUtil.readDirs(dirc);
        for(String file : filelist){
            HashMap<String, Integer> dict = new HashMap<String, Integer>();
            ArrayList<String> cutwords = TfIdfUtil.cutWords(file);
            dict = TfIdfUtil.normalTF(cutwords);
            allNormalTF.put(file, dict);
        }
        return allNormalTF;
    }

    /**
     * 计算文件夹中所有文章的tf
     * @param dirc 一个文件夹的绝对路径
     * @return idf()的方法参数
     * @throws IOException
     */
    public static HashMap<String,HashMap<String, Float>> allFilesTf(String dirc) throws IOException{
        //key为文件绝对路径，value为该文件的所有分词的tf集合
        HashMap<String, HashMap<String, Float>> allTF = new HashMap<String, HashMap<String, Float>>();
        //获得该文件夹下面所有的文件路径集合
        List<String> filelist = TfIdfUtil.readDirs(dirc);
        for(String file : filelist){
            //每个文章对应的word：tf集合
            HashMap<String, Float> dict = new HashMap<String, Float>();
            ArrayList<String> cutwords = TfIdfUtil.cutWords(file);
            dict = TfIdfUtil.tf(cutwords);
            allTF.put(file, dict);
        }
        return allTF;
    }

    /**
     * 计算所有单词的idf并从高到低存储，key为word,value为IDF值
     * 计算公式为idf=log（M/n+0.01）M为文章总数，n为词出现过的文章数
     * @param all_tf 所有文章的tf集合，key为文章绝对路径，value为文章每个单词对应tf值集合
     * @return
     */
    public static HashMap<String, Float> allWordsIdf(HashMap<String,HashMap<String, Float>> all_tf){
        //key为word,value为IDF值
        HashMap<String, Float> resIdf = new HashMap<String, Float>();
        //key为word,value为word出现过的文章数量
        HashMap<String, Integer> dict = new HashMap<String, Integer>();
        //文件夹下所有文章个数
        int docNum = FileList.size();
        for(int i = 0; i < docNum; i++){
            //获取每个文章的每个单词的tf值集合,key为word，value为tf值
            HashMap<String, Float> temp = all_tf.get(FileList.get(i));
            Iterator iter = temp.entrySet().iterator();
            //进入while循环，针对单个word
            while(iter.hasNext()){
                Map.Entry entry = (Map.Entry)iter.next();
                String word = entry.getKey().toString();
                if(dict.get(word) == null){
                    dict.put(word, 1);
                }else {
                    //循环到这篇文章，如果该词出现过，则加一
                    dict.put(word, dict.get(word) + 1);
                }
            }
        }
        Iterator iter_dict = dict.entrySet().iterator();
        while(iter_dict.hasNext()){
            Map.Entry entry = (Map.Entry)iter_dict.next();
            //计算word的IDF值
            float value = (float)Math.log(docNum / Float.parseFloat(entry.getValue().toString())+0.01);
            resIdf.put(entry.getKey().toString(), value);
        }
        return RankMapUtil.sortMapByValue(resIdf);
    }

    /**
     * 生成所有文章每个单词tfidf值，并从高到低遍历展示
     * @param all_tf  所有文章的每个word的tf集合
     * @param idfs     所有单词的idf集合,key为word
     */
    public static HashMap<String, HashMap<String, Float>> tf_idf(HashMap<String,HashMap<String, Float>> all_tf,HashMap<String, Float> idfs){
        //key为文章绝对路径，value为该文章的每个word的tfidf值集合
        HashMap<String, HashMap<String, Float>> allTfIdf = new HashMap<String, HashMap<String, Float>>();
        //文章总数
        int docNum = FileList.size();
        for(int i = 0; i < docNum; i++){
            String filepath = FileList.get(i);
            //每篇文章每个单词的tfidf集合
            HashMap<String, Float> tfidf = new HashMap<String, Float>();
            HashMap<String, Float> temp = all_tf.get(filepath);
            Iterator iter = temp.entrySet().iterator();
            while(iter.hasNext()){
                Map.Entry entry = (Map.Entry)iter.next();
                String word = entry.getKey().toString();
                //计算tfidf
                Float value = (float)Float.parseFloat(entry.getValue().toString()) * idfs.get(word);
                tfidf.put(word, value);
            }
            allTfIdf.put(filepath, RankMapUtil.sortMapByValue(tfidf));
        }
        return allTfIdf;
    }

    /**
     * ShowAllTfIdf遍历展示tf_idf值
     * @param allTfIdf 所有文章的TFIDF计算结果集
     */
    public static void showAllTfIdf(HashMap<String, HashMap<String, Float>> allTfIdf){
        Iterator iter1 = allTfIdf.entrySet().iterator();
        while(iter1.hasNext()){
            Map.Entry entrys = (Map.Entry)iter1.next();
            System.out.println("FileName: " + entrys.getKey().toString());
            System.out.print("{");
            HashMap<String, Float> temp = (HashMap<String, Float>) entrys.getValue();
            //TFIDF从高向低遍历展示
            Set<Map.Entry<String, Float>> entries = temp.entrySet();
            for (Map.Entry entry : entries) {
                System.out.println(entry.getKey()+" = "+entry.getValue());
            }
            System.out.println("}");
        }
    }



}
