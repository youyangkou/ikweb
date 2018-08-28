package com.kouyy.ikweb.tfidf;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;


public class MyTest {

    /**
     * RankMapUtil.sortMapByValue()方法的测试
     */
    @Test
    public void testSort(){
        Map map=new HashMap(16);
        map.put("c",34.232323f);
        map.put("b",34.232423f);
        map.put("a",34.212323f);
        map.put("d",34.222323f);
        map.put("f",34.239323f);
        map.put("e",34.238323f);
        map.put("j",34.292323f);
        LinkedHashMap linkedHashMap = RankMapUtil.sortMapByValue(map);
        Set<Map.Entry> set = linkedHashMap.entrySet();
        for (Map.Entry entry : set) {
            System.out.println(entry.getKey()+":"+entry.getValue());
        }
    }

    /**
     * 计算文件夹所有文章idf的测试
     * @throws IOException
     */
    @Test
    public void idfTest() throws IOException {
        String file = "E:\\personal_code\\ikweb\\src\\main\\resources\\TFIDF";
        HashMap<String,HashMap<String, Float>> tfs = TfIdfUtil.allFilesTf(file);
        HashMap<String, Float> idfs = TfIdfUtil.allWordsIdf(tfs);
        Set<Map.Entry<String, Float>> entries = idfs.entrySet();
        for (Map.Entry<String, Float> entry : entries) {
            System.out.println(entry.getKey()+"=="+entry.getValue());
        }
    }


    /**
     * 计算文件夹所有文章tf的测试
     * @throws IOException
     */
    @Test
    public void tfTest() throws IOException {
        String file = "E:\\personal_code\\ikweb\\src\\main\\resources\\TFIDF";
        HashMap<String,HashMap<String, Float>> tfs = TfIdfUtil.allFilesTf(file);
        Iterator iterator = tfs.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry entrys = (Map.Entry)iterator.next();
            System.out.println("------------------------"+"FileName: " + entrys.getKey().toString()+"------------------------");
            HashMap<String, Float> temp = (HashMap<String, Float>) entrys.getValue();
            Set<Map.Entry<String, Float>> entries = temp.entrySet();
            for (Map.Entry entry : entries) {
                System.out.println(entry.getKey()+" = "+entry.getValue());
            }
        }
    }

    /**
     * 计算文件夹所有文章tfIdf的测试
     * @throws IOException
     */
    @Test
    public void tfIdfTest() throws IOException {
        String file = "E:\\personal_code\\ikweb\\src\\main\\resources\\TFIDF";
        HashMap<String,HashMap<String, Float>> tfs = TfIdfUtil.allFilesTf(file);
        HashMap<String, Float> idfs = TfIdfUtil.allWordsIdf(tfs);
        HashMap<String, HashMap<String, Float>> tfidfs = TfIdfUtil.tf_idf(tfs, idfs);
        TfIdfUtil.showAllTfIdf(tfidfs);
    }

}
