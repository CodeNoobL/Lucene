import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

/**
 * 
 * @author CodeNoob
 * @date 2018年9月12日
 */
public class TestCode {

    /**
     * @Description
     * @param title
     * @param content
     * @return
     */
    private static Document createDocument(String title, String content, String author) {
        Document doc = new Document();
        doc.add(new Field("title", title, TextField.TYPE_STORED));
        doc.add(new Field("content", content, TextField.TYPE_STORED));
        doc.add(new Field("author", author, TextField.TYPE_STORED));
        return doc;
    }

    /**
     * lucene简单实例 索引 查询 经济,分词器：标准分词器
     */
    public static void testDemo() throws Exception {
        // 定义一个词法分析器。
        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_45);
        // 内存存储
        Directory directory = new RAMDirectory();
        // 创建IndexWriter，进行索引文件的写入。
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_45, analyzer);
        IndexWriter writer = new IndexWriter(directory, config);

        // 内容提取，进行索引的存储。
        writer.addDocument(createDocument("中央政治局研究2017年经济工作", "中共中央政治局12月9日召开会议,2017内容", "罗"));
        writer.addDocument(createDocument("中央政治局研究2016年经济工作", "test中共中央政治局12月9日召开会议，2016内容", "罗昭钦"));
        writer.addDocument(createDocument("中央政治局研究2015年经济工作", "test中共中央政治局12月9日召开会议，2015内容", "罗领域"));
        writer.addDocument(createDocument("中央政治局研究2015年环境工作", "test中共中央政治局12月9日召开会议，2015内容", "罗小号"));
        writer.addDocument(createDocument("中央政治局研究2015年媒体工作", "test中共中央政治局12月9日召开会议，2015内容", "罗大号"));
        writer.addDocument(createDocument("中央政治局研究2015年安全工作", "test中共中央政治局12月9日召开会议，2015内容", "罗兹伟"));
        writer.commit();
        writer.close();

        // 关键字查询
        // 创建搜索器
        Date date1 = new Date();
        IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(directory));
        QueryParser parser = new QueryParser(Version.LUCENE_45, "author", analyzer);
        Query query = parser.parse("罗小");
        TopDocs topDocs = searcher.search(query, 10);
        int count = topDocs.totalHits;
        System.out.println("发现数量：" + count);
        ScoreDoc[] hits2 = topDocs.scoreDocs;
        if (hits2 != null && hits2.length > 0) {
            for (int i = 0; i < hits2.length; i++) {
                Document document = searcher.doc(hits2[i].doc);
                System.out.println(document.get("title"));
                System.out.println(document.get("content"));
                System.out.println(document.get("author"));
            }
        }else {
            
        }
        System.out.println("检索时间：" + (new Date().getTime() - date1.getTime())+"ms");

    }

    public static void main(String[] args) throws Exception {
        testDemo();
    }
}
