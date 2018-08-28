package com.kouyy.ikweb.controller;

import com.kouyy.ikweb.config.MyConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.wltea.analyzer.dic.Dictionary;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 调用Dictionary的addwords方法实现动态添加新词的功能，同时持久化新词
 * @author kouyy
 */
@RestController
public class AddWordsController {

    private static final Logger logService = LogManager.getLogger(AddWordsController.class);
    /**
     * 持久化新词的路径
     */
    private static String filePath="E:\\personal_code\\ikweb\\src\\main\\resources\\dic\\new_word.dic";

    /**
     * 实现动态添加新词并持久化
     * @param word 要添加的新词
     * @return
     */
    @GetMapping("add")
    public String addWordsController(@RequestParam("word")  String word){
        List list=new ArrayList();
        list.add(word);
        //Configuration为单实例
        MyConfiguration conf = new MyConfiguration();
        //设置为智能分词
        conf.setUseSmart(true);
        //dic为单实例
        Dictionary dic = Dictionary.initial(conf);

        File file= null;
        try {
            file = new File(filePath);
            List<String> words = FileUtils.readLines(file, "utf-8");
            for (String str : words) {
                if(word.equals(str)){
                    return "要添加的新词在ik文件中已存在，请不要重复添加！";
                }
            }
            FileUtils.writeStringToFile(file,word+"\r\n","utf-8",true);
            dic.addWords(list);
            logService.info("ik动态词库添加新词成功："+word);
            return "ik动态词库添加新词成功："+word;
        } catch (Exception e) {
            logService.error("ik动态词库添加新词失败"+e);
            return "ik动态词库添加新词失败";
        }
    }
}
