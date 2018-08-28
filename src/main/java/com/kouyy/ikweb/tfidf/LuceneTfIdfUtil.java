package com.kouyy.ikweb.tfidf;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.*;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.util.List;

/**
 * 利用lucene获取tf-idf
 * @author kouyy
 */
public class LuceneTfIdfUtil {
    public static final String INDEX_PATH = "E:\\personal_code\\ikweb\\src\\main\\resources\\index";

    public void createIndex() {
        try {
            // 关联指定索引库磁盘位置
            Directory directory = FSDirectory.open(new File(INDEX_PATH));
            //使用ik分词器分词
            Analyzer analyzer = new IKAnalyzer(true);

            // 操作索引库核心对象的配置信息对象 参数一：Lucene版本信息 参数二：指定分词器（因为需要对文档内容分词）
            IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_3,analyzer);
            // 获取创建索引库核心对象 参数一：索引库磁盘目录 参数二：操作索引库核心对象的配置信息对象
            IndexWriter iwriter = new IndexWriter(directory, config);

            FieldType ft = new FieldType();
            ft.setIndexed(true);// 索引
            ft.setStored(true);// 存储
            ft.setStoreTermVectors(true);
            ft.setTokenized(true);
            ft.setStoreTermVectorPositions(true);// 存储位置
            ft.setStoreTermVectorOffsets(true);// 存储偏移量

            // 构建文档对象
            Document doc = new Document();
           // String text = "This is the text to be indexed.I am the text to be stored.";
            String text = "hello寇游洋王碧洲黄钢刘晶晶易车黄家驹寇向峰啦啦啦小镇玻璃杯U盘硬盘石红星咖啡绿茶";
            doc.add(new Field("text", text, ft));
            iwriter.addDocument(doc);
            // 构建新文档对象
            doc = new Document();
            text = "I am the text to be stored.";
            doc.add(new Field("text", text, ft));
            iwriter.addDocument(doc);

            iwriter.forceMerge(1);// 最后一定要合并为一个segment，不然无法计算idf
            iwriter.close();
        } catch (Exception e) {
        }
    }

    /**
     * 读取索引，显示词频
     * 计算TF
     * **/
    public void getTF() {
        try {
            Directory directroy = FSDirectory.open(new File(
                    INDEX_PATH));
            IndexReader reader = DirectoryReader.open(directroy);
            for (int i = 0; i < reader.numDocs(); i++) {
                int docId = i;
                System.out.println("第" + (i + 1) + "篇文档：");
                Terms terms = reader.getTermVector(docId, "text");
                if (terms == null) {
                    continue;
                }
                TermsEnum termsEnum = terms.iterator(null);
                BytesRef thisTerm = null;
                while ((thisTerm = termsEnum.next()) != null) {
                    String termText = thisTerm.utf8ToString();
                    DocsEnum docsEnum = termsEnum.docs(null, null);
                    while ((docsEnum.nextDoc()) != DocIdSetIterator.NO_MORE_DOCS) {
                        System.out.println("termText:" + termText + " TF:  "
                                + 1.0 * docsEnum.freq() / terms.size());
                    }
                }
            }
            reader.close();
            directroy.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 计算IDF
     */
    public void getIDF() {
        try {
            Directory directroy = FSDirectory.open(new File(INDEX_PATH));
            IndexReader reader = DirectoryReader.open(directroy);
            List<AtomicReaderContext> list = reader.leaves();
            System.out.println("文档总数 : " + reader.maxDoc());
            for (AtomicReaderContext ar : list) {
                String field = "text";
                AtomicReader areader = ar.reader();
                Terms terms = areader.terms(field);
                TermsEnum tn = terms.iterator(null);
                BytesRef text;
                while ((text = tn.next()) != null) {
                    System.out.println("field=" + field + "; text="
                                    + text.utf8ToString() + "   IDF : "
                                    + Math.log10(reader.maxDoc() * 1.0 / tn.docFreq())
                            // + " 全局词频 :  " + tn.totalTermFreq()
                    );
                }
            }
            reader.close();
            directroy.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        LuceneTfIdfUtil luceneTfIdfUtil = new LuceneTfIdfUtil();
        luceneTfIdfUtil.createIndex();
        luceneTfIdfUtil.getTF();
        luceneTfIdfUtil.getIDF();
    }
}
