package org.ai;

import java.io.File;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.DateTools.Resolution;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;

public class CrudLucent {
	public static final File INDEX_DIRECTORY = new File("lucent_index");

	public static Connection getConnection() {
		try {
			Class.forName("org.postgresql.Driver").newInstance();
			return DriverManager.getConnection("jdbc:postgresql://localhost:5432/articledb", "postgres", "12345");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean createIndex(OpenMode mode) {
		System.out.println("creating index file ... ");
		String sql = "select title,content,publish_date,name from v_list_all_article";
		// initilize lucent class and config
		Analyzer analyzer = new StandardAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		config.setOpenMode(mode);
		try (Directory dir = FSDirectory.open(Paths.get(INDEX_DIRECTORY.toString()));
				IndexWriter write = new IndexWriter(dir, config);
				// query all record form database
				Connection cnn = getConnection();
				PreparedStatement ps = cnn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery();) {
			while (rs.next()) {
				Date time = rs.getDate("publish_date");
				String date = buildDate(time);
				Document doc = new Document();
				doc.add(new TextField("title", rs.getString("title"), Field.Store.YES));
				doc.add(new TextField("content", rs.getString("content"), Field.Store.YES));
				doc.add(new StringField("publish_date", date, Field.Store.YES));
				doc.add(new StringField("name", rs.getString("name"), Field.Store.YES));
				write.addDocument(doc);
			}
			System.out.println("Success Indexing");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error Creating Index File");
			return false;
		}
	}

	public void search(String searchquery, int limit, int page) {
		if(page<=0) page = 1;
		int startIndex = (page-1)* limit;
		//ArrayList<ArticleDto> articles = new ArrayList<ArticleDto>();
		Analyzer analyzer = new StandardAnalyzer();
		QueryParser parser = new MultiFieldQueryParser(new String[] { "title", "content", "publish_date", "name" }, analyzer);
		try(IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(INDEX_DIRECTORY.toString())));){
			Query query = parser.parse(searchquery);
			TopScoreDocCollector collector = TopScoreDocCollector.create(page * limit);
			IndexSearcher searcher = new IndexSearcher(reader);						
			searcher.search(query, collector);
			ScoreDoc[] hits = collector.topDocs(startIndex, limit).scoreDocs;
			System.out.println("Total results: " + collector.getTotalHits());
			for (int i = 0; i < hits.length; i++) {
				Document doc = searcher.doc(hits[i].doc);
				System.out.println(i+1 + " | Title: " + doc.get("title").substring(0, 20)  
									 + "\tContent: " + doc.get("content").substring(0, 20) 
									 + "\tPublishDate: " + doc.get("publish_date")
									 + "\tName: " + doc.get("name"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		long startTime = System.currentTimeMillis();
		CrudLucent searching = new CrudLucent();
		//searching.createIndex(OpenMode.CREATE);
		searching.search("title:she gasped",10,1);
		//searching.startSearch("title:khmer AND date:[20150130 TO 20151115]");
		long endTime = System.currentTimeMillis();
		long elapsedMilliSeconds = endTime - startTime;
		double elapsedSeconds = elapsedMilliSeconds / 1000.0;
		System.out.println(elapsedSeconds);
	}

	public String buildDate(Date time) {
		return DateTools.dateToString(time, Resolution.DAY);
	}
}
