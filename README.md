##文档说明
1.controller包\
AddWordsController：动态添加新词API
IkConfigurationController：测试动态添加分词效果

2.tfidf包\
RankMapUtil：对HashMap根据value的值进行排序的工具类
LuceneTfIdfUtil：利用lucene的API计算TFIDF
MyTfidfUtil：没有利用Lucene的API进行TFIDF的计算

3.config包\
自定义IK分词器的配置类

4.lucene包\
利用lucene的API创建索引，查询索引