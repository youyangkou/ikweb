package com.kouyy.ikweb.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.jupiter.api.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;

public class CreateIndex {

	/**
	 * lucene基本API操作
	 * 创建索引库，并添加文档数据
	 * 
	 * @throws Exception
	 */
	@Test
	public void addIndexTest() throws Exception {

		// 指定索引库位置
		String indexPath = "E:\\personal_code\\ikweb\\src\\main\\resources\\index";

		// 关联指定索引库磁盘位置
		FSDirectory directory = FSDirectory.open(new File(indexPath));
		//使用ik分词器分词
		Analyzer analyzer = new IKAnalyzer();
		
		// 操作索引库核心对象的配置信息对象 参数一：Lucene版本信息 参数二：指定分词器（因为需要对文档内容分词）
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_3,
				analyzer);

		// 获取创建索引库核心对象 参数一：索引库磁盘目录 参数二：操作索引库核心对象的配置信息对象
		IndexWriter indexWriter = new IndexWriter(directory, config);

		// 构建文档对象
		Document document = new Document();

		// 构建文档字段和字段值 参数一：文档字段名称 参数二：文档字段值 参数三：是否存储（需要显示时，需要存储）
		// StringField特点：不分词
		document.add(new StringField("id", "1001", Store.NO));

		// 添加title字段 参数一：文档字段名称 参数二：文档字段值 参数三：是否存储（需要显示时，需要存储）
		// TextField特点：分词
		document.add(new TextField("title", "黄晓明在传智播客学习Java，出任CEO，成为高富帅，迎娶白富美。走上人生巅峰。lucene教程--全文检索技术详解", Store.YES));

		// 添加desc字段 参数一：文档字段名称 参数二：文档字段值 参数三：是否存储（需要显示时，需要存储）
		// TextField特点：分词
		document.add(new TextField("desc", "全文检索是一种将文件中所有文本与检索项匹配的检索方法。它可以根据需要获得全文中有关章、节、段、句、词等信息。", Store.YES));

		// 添加content字段 参数一：文档字段名称 参数二：文档字段值 参数三：是否存储（需要显示时，需要存储）
		// TextField特点：分词
		document.add(new TextField("content", "计算机程序通过扫描文章中的每一个词，对每一个词建立一个索引，指明该词在文章中出现的次数和位置，当用户查询时根据建立的索引查找，类似于通过字典的检索字表查字的过程。", Store.NO));

		//添加文档数据到索引库
		indexWriter.addDocument(document);
		
		//提交数据
		indexWriter.commit();
		
		//关闭资源
		indexWriter.close();
	}

}
