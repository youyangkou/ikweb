package com.kouyy.ikweb.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.jupiter.api.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;

public class QueryIndex {

	/**
	 * 基于Lucene的API完成索引库文档数据查询操作
	 * @throws Exception 
	 */
	@Test
	public void searchIndexTest() throws Exception{
		
		//指定索引库位置
		String indexPath = "E:\\personal_code\\ikweb\\src\\main\\resources\\index";
		//获取读取索引库位置的对象
		DirectoryReader reader = DirectoryReader.open(FSDirectory.open(new File(indexPath)));
		
		//1 获取查询索引库文档数据的核心对象  参数：读取索引库文件的对象
		IndexSearcher indexSearcher = new IndexSearcher(reader);
		
		//2指定查询内容
		String qname = "全文检索";
		//指定分词器（该分词器必须与创建索引时分词器一致）
		Analyzer analyzer = new IKAnalyzer();
		//3指定解析对象，对搜索内容解析 参数一：指定查询字段 参数二：指定分词器（该分词器必须与创建索引时分词器一致）
		QueryParser parser = new QueryParser(Version.LUCENE_4_10_3,"title", analyzer);
		//4解析搜索内容，返回一个查询对象
		Query query = parser.parse(qname);
		
		//5执行查询操作 参数一：查询对象 参数二：返回多少条记录
		//topDocs分词了文档基本信息，例如：文档id、文档得分、满足查询条件的总记录数
		TopDocs topDocs = indexSearcher.search(query, 10);
		//获取总记录数
		System.out.println("满足查询条件的总记录数："+topDocs.totalHits);
		
		//获取得分和文档id
		ScoreDoc[] docs = topDocs.scoreDocs;
		for (ScoreDoc scoreDoc : docs) {
			//获取文档id
			int docId = scoreDoc.doc;
			System.out.println("文档id："+docId);
			//获取文档得分
			float score = scoreDoc.score;
			System.out.println("文档得分："+score);
			
			//根据文档id获取文档对象
			Document doc = indexSearcher.doc(docId);
			System.out.println("文档域字段id："+doc.get("id"));
			System.out.println("文档标题title："+doc.get("title"));
			System.out.println("文档描述："+doc.get("desc"));
			System.out.println("文档内容："+doc.get("content"));
		}
		
	}
	
}
