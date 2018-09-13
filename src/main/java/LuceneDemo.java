import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 * Lucene案例
 * 
 * @author CodeNoob
 * @date 2018年9月12日
 */
public class LuceneDemo {

    private static List<Passage> passageList = null;

    // 写对象
    private static IndexWriter indexWriter = null;

    static {
        passageList = new ArrayList<Passage>();

        passageList = new ArrayList<Passage>();

        // 产生一堆数据
        passageList.add(new Passage(1, "yellowcong", "717350389@qq.com", "逗比", 23, "I LOVE YOU ", new Date()));
        passageList.add(new Passage(2, "张三", "zhangshan@qq.com", "逗比", 23, "三炮", new Date()));
        passageList.add(new Passage(3, "李四", "lisi@neusoft.com", "逗比", 23, "三炮", new Date()));
        passageList.add(new Passage(4, "王五", "wangwu@aliyun.com", "逗比", 23, "三炮", new Date()));
        passageList.add(new Passage(5, "赵六", "zhaoliu@baidu.com", "逗比", 23, "三炮", new Date()));
        passageList.add(new Passage(7, "罗昭钦", "zhangsha@baidu.com", "逗比", 23, "三炮", new Date()));
        passageList.add(new Passage(8, "罗小号", "lisi@neusoft.com", "逗比", 23, "三炮", new Date()));
        passageList.add(new Passage(9, "罗大号", "wangwu@aliyun.com", "逗比", 23, "三炮", new Date()));
        passageList.add(new Passage(10, "罗某某", "zhaoliu@baidu.com", "逗比", 23, "三炮", new Date()));
    }

    public static void main(String[] args) throws ParseException {
        // 删除所有索引
        deleteAllIndex();

        // 建立索引
        createIndex();

        getByTermQuery();
        // 范围查询——TermRangeQuery
        getByRange();
        // 前缀匹配查询——PrefixQuery
        getByPrefix();
        // 通配符查询——WildcardQuery
        getByWildcard();
        // 短语查询——PhraseQuery
        getByPhrase();
        // 模糊查询——FuzzyQuery
        getByFuzzy();

        // 综合查询——QueryPhase
        getByQueryPhase();
    }

    /**
     * @throws ParseException
     * @Description 综合查询——QueryPhase
     *
     */
    private static void getByQueryPhase() throws ParseException {
        QueryParser parser = new QueryParser(Version.LUCENE_45, "username", new StandardAnalyzer(Version.LUCENE_45));
//        parse.setDefaultOperator(Operator.AND);//将空格默认 定义为AND,原本默认为OR
        // 设定第一个* 可以匹配
        parser.setAllowLeadingWildcard(true);
        Query query = parser.parse("yellow*");
        System.out.println("------------- 一 ----------------");
        excuteQuery(query);

        // 其中空格默认就是OR
        query = parser.parse("yellow* *cong");
        System.out.println("------------- 二 ----------------");
        excuteQuery(query);

        // 改变搜索域，搜索域 为 content
        query = parser.parse("content:三炮");
        System.out.println("------------- 三 ----------------");
        excuteQuery(query);

        // 使用通配符 ， 设定查询类容为 以 y 开头的数据
        query = parser.parse("username:y*"); // 其中* 不可以放在字符串的首位
        System.out.println("------------- 四 ----------------");
        excuteQuery(query);

        // 将字符串放在首位，默认情况下回报错
        query = parser.parse("email:*@qq.com"); // 其中我们可以更改 第一个通配值得功能
        System.out.println("------------- 五 ----------------");
        excuteQuery(query);

//        // 其中 + - 表示有 和没有 其中需要有空格 ，而且第一个+ 或者 - 需要放在第一个位置(无用)
//        query = parser.parse("- email: zhangshan + zhangsha "); // 这个表示的是 中不含有 cong ，但是含有i
//        System.out.println("------------- 六 ----------------");
//        excuteQuery(query);
//
//        // 匹配区间， 其中TO 必须是大写的，还有有空格（无用）
//        query = parser.parse("id:[1 TO 4]"); // 设定查询的Id为 1-4
//        System.out.println("------------- 七 ----------------");
//        excuteQuery(query);
//        // 开区间匹配（无用）
//        query = parser.parse("id:(1 TO 4)");
//        System.out.println("------------- 八 ----------------");
//        excuteQuery(query);
//
//        // 匹配连起来的String（无用）
//        query = parser.parse("content:三炮");
//        query = parser.parse("\"I like yellow cong\""); // 这个是查询的一个一个词 ，匹配String
//        System.out.println("------------- 九 ----------------");
//        excuteQuery(query);
//        
//        // 匹配一个或者多个数据
//        query = parser.parse("\"I cong\"~2"); // 表示中间含有一个单词
//        System.out.println("------------- 十----------------");
//        excuteQuery(query);
//        // 模糊查询
//        query = parser.parse("username:yellow~");
//        System.out.println("------------- 十一 ----------------");
//        excuteQuery(query);
    }

    /**
     * @Description 精确String类型查询——TermQuery
     *
     */
    private static void getByTermQuery() {
        System.out.println("------------- 精确查询:用户名 ----------------");
        Query query = new TermQuery(new Term("username", "罗"));
        // 执行查询
        excuteQuery(query);
    }

    /**
     * @Description 范围查询——TermRangeQuery
     *
     */
    private static void getByRange() {
        System.out.println("------------- 范围查询:id在1-3 ----------------");
        // 后面两个true,表示的是是否包含头和尾
        Query query = NumericRangeQuery.newIntRange("id", 1, 3, true, true);
        // 执行查询
        excuteQuery(query);
    }

    /**
     * @Description 前缀匹配查询——PrefixQuery
     *
     */
    private static void getByPrefix() {
        System.out.println("------------- 前缀查询:邮箱以z开头 ----------------");
        Query query = new PrefixQuery(new Term("email", "z"));
        excuteQuery(query);
    }

    /**
     * @Description // 通配符查询——WildcardQuery
     *
     */
    private static void getByWildcard() {
        // “*”表示0到多个字符，而使用“？”表示一个字符就行了
        System.out.println("-------------通配符查询:查询email 以 @qq结尾的数据--------------");
        // 查询email 以 @qq结尾的数据
        Query query = new WildcardQuery(new Term("email", "*@qq.com"));
        // 执行查询
        excuteQuery(query);
    }

    /**
     * @Description // 短语查询——PhraseQuery
     *
     */
    private static void getByPhrase() {
        System.out.println("-------------短语查询：查询内容中，有I　LOVE　YOU 的数据---------------");
        // 短语查询，但是对于中文没有太多的用，其中查询的时候还有
        PhraseQuery query = new PhraseQuery();
        // 设定有几跳，表示中间存在一个单词
        query.setSlop(1);
        // 查询
        query.add(new Term("content", "i"));

        // I XX you 就可以被查询出来
        query.add(new Term("content", "you"));

        excuteQuery(query);

    }

    /**
     * @Description // 模糊查询——FuzzyQuery
     *
     */
    private static void getByFuzzy() {
        System.out.println("-------------模糊查询---------------");
        FuzzyQuery query = new FuzzyQuery(new Term("username", "zhangsan~0"));
        excuteQuery(query);
    }

    /**
     * @Description 执行查询
     * @param query
     */
    private static void excuteQuery(Query query) {
        IndexReader reader = null;
        try {
            reader = getIndexReader();

            IndexSearcher searcher = new IndexSearcher(reader);

            // 100好像是用来设置搜索文件数（待验证）
            TopDocs topDocs = searcher.search(query, 100);

            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                Document document = reader.document(scoreDoc.doc);
                // @test
                System.out.println(document.get("id") + ":" + document.get("username") + ":" + document.get("email"));
            }

        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            coloseReader(reader);
        }

    }

    /**
     * @Description 建立索引
     */
    private static void createIndex() {
        IndexWriter indexWriter = null;

        try {
            // 获取IndexWriter
            indexWriter = getIndexWriter();

            // 建立索引
            for (Passage passage : passageList) {
                Document document = new Document();

                // IntField 不能直接检索到，需要结合
                document.add(new IntField("id", passage.getId(), Field.Store.YES));

                // 用户String类型的字段的存储，StringField是只索引不分词
                document.add(new TextField("username", passage.getUsername(), Field.Store.YES));

                // 主要对int类型的字段进行存储，需要注意的是如果需要对InfField进行排序使用SortField.Type.INT来比较，如果进范围查询或过滤，需要采用NumericRangeQuery.newIntRange()
                document.add(new IntField("age", passage.getAge(), Field.Store.YES));

                // 对String类型的字段进行存储，TextField和StringField的不同是TextField既索引又分词
                document.add(new TextField("content", passage.getContent(), Field.Store.YES));

                document.add(new StringField("keyword", passage.getKeyword(), Field.Store.YES));

                document.add(new StringField("email", passage.getEmail(), Field.Store.YES));

                // 日期数据添加索引
                document.add(new LongField("addDate", passage.getAddDate().getTime(), Field.Store.YES));

                // 3、添加文档
                indexWriter.addDocument(document);
            }

            // @test
            int count = indexWriter.numDocs();
            System.out.println("索引条数" + count);

        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            coloseWriter(indexWriter);
        }
    }

    /**
     * @Description 删除所有索引
     */
    private static void deleteAllIndex() {
        IndexWriter indexWriter = null;
        try {
            // 获取IndexWriter
            indexWriter = getIndexWriter();
            // 删除所有数据
            indexWriter.deleteAll();

            // @Test
            int count = indexWriter.numDocs();
            System.out.println("索引条数" + count);

            indexWriter.commit();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            coloseWriter(indexWriter);
        }

    }

    /**
     * @Description 获取索引目录
     * @return 目录
     */
    private static String getIndexPath() {
        // 获取索引的目录
        String path = LuceneDemo.class.getClassLoader().getResource("index").getPath();

        // 不存在就创建目录
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    /**
     * @Description 获取IndexWriter 同一时间，只能打开一个IndexWriter，独占写锁
     * @return IndexWriter
     * @throws Exception
     * @Deprecation
     */
    @SuppressWarnings({ "static-access" })
    private static IndexWriter getIndexWriter() throws Exception {
        // 创建IndexWriter
        String path = getIndexPath();
        Directory fsDirectory = FSDirectory.open(new File(path));

        // 判断资源是否占用
        if (indexWriter == null || !indexWriter.isLocked(fsDirectory)) {
            synchronized (LuceneDemo.class) {
                if (indexWriter == null || !indexWriter.isLocked(fsDirectory)) {
                    // 创建writer对象
                    indexWriter = new IndexWriter(fsDirectory,
                            new IndexWriterConfig(Version.LUCENE_45, new StandardAnalyzer(Version.LUCENE_45)));
                }
            }
        }
        return indexWriter;
    }

    /**
     * @Description 关闭IndexWriter
     * @param indexWriter
     */
    private static void coloseWriter(IndexWriter indexWriter) {
        try {
            if (indexWriter != null) {
                indexWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @Description 获取多个IndexReader，任意多个Indexreaders可同时打开，可以跨JVM
     * @return
     * @throws IOException
     */
    private static IndexReader getIndexReader() throws IOException {
        // 创建IndexWriter
        String path = getIndexPath();
        FSDirectory fsDirectory = FSDirectory.open(new File(path));
        return DirectoryReader.open(fsDirectory);
    }

    /**
     * @Description 关闭IndexReader
     * @param reader
     */
    private static void coloseReader(IndexReader reader) {
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
