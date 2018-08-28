package com.kouyy.ikweb.controller;

import com.kouyy.ikweb.config.MyConfiguration;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.dic.Dictionary;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

/**
 * 测试动态添加分词效果
 * @author kouyy
 */
@RestController
public class IkConfigurationController {

    @RequestMapping("text")
    public Set helloController(@RequestParam("text")  String text) throws IOException {
        MyConfiguration conf = new MyConfiguration();
        //设置为智能分词
        conf.setUseSmart(true);
        //设置自定义词库
        conf.setMainDictionary("dic/new_word.dic");
        Dictionary dic = Dictionary.initial(conf);
        //创建读取字符串的输入流
        StringReader reader = new StringReader(text);
        //IK分词器主类
        IKSegmenter seg = new IKSegmenter(reader ,conf);

        //true为智能分词模式，false使用最细粒度分词。
        Analyzer analyzer = new IKAnalyzer(true);

        //分词器通过TokenStream有效的获取到分词单元
        TokenStream ts = analyzer.tokenStream("", reader);
        ts.reset();

        Set set=new HashSet();
        while(ts.incrementToken()){
            CharTermAttribute term=ts.getAttribute(CharTermAttribute.class);
            set.add(term.toString());
            System.out.print(term.toString()+"|");
        }
        analyzer.close();
        reader.close();
        return set;
    }
}
