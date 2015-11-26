package org.ai;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
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
	public static int count_result = 0;

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
		System.out.println("Creating Index File ... ");
		String sql = "select id,title,publish_date,enable,image,content,userid,name from v_list_all_article";
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
				Document doc = new Document();
				doc.add(new IntField("id", rs.getInt("id"), Field.Store.YES));
				doc.add(new TextField("title", rs.getString("title"), Field.Store.YES));
				doc.add(new StringField("publish_date",
						new SimpleDateFormat("yyyyMMdd:HHmmss").format(rs.getTimestamp("publish_date")),
						Field.Store.YES));
				doc.add(new StringField("enable", rs.getString("enable"), Field.Store.YES));
				doc.add(new StringField("image", rs.getString("image"), Field.Store.YES));
				doc.add(new TextField("content", rs.getString("content"), Field.Store.YES));
				doc.add(new IntField("userid", rs.getInt("userid"), Field.Store.YES));
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

	public ArrayList<ArticleDto> search(String searchquery, int limit, int page) {
		if (page <= 0)
			page = 1;
		int startIndex = (page - 1) * limit;
		ArrayList<ArticleDto> articles = new ArrayList<ArticleDto>();
		Analyzer analyzer = new StandardAnalyzer();
		QueryParser parser = new MultiFieldQueryParser(
				new String[] { "id", "title", "publish_date", "enable", "image", "content", "userid", "name" },
				analyzer);
		try (IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(INDEX_DIRECTORY.toString())));) {
			Query query = parser.parse(searchquery);
			TopScoreDocCollector collector = TopScoreDocCollector.create(page * limit);
			IndexSearcher searcher = new IndexSearcher(reader);
			searcher.search(query, collector);
			ScoreDoc[] hits = collector.topDocs(startIndex, limit).scoreDocs;
			count_result = collector.getTotalHits();
			for (int i = 0; i < hits.length; i++) {
				Document doc = searcher.doc(hits[i].doc);
				ArticleDto a = new ArticleDto();
				a.setId(Integer.parseInt(doc.get("id")));
				a.setTitle(doc.get("title"));
				//a.setPdate(new SimpleDateFormat("yyyyMMdd:HHmmss").parse(doc.get("publish_date")));
				//a.setEnable(doc.get("enable").equals("t"));
				a.setImage(doc.get("image"));
				a.setContent(doc.get("content"));
				//a.setUserid(Integer.parseInt(doc.get("userid")));
				a.setUsername(doc.get("name"));
				articles.add(a);
			}
			return articles;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void deleteDocument(String queryStr) {
		Analyzer analyzer = new StandardAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		QueryParser parser = new MultiFieldQueryParser(new String[] { "id", "title", "publish_date", "enable", "image", "content", "userid", "name" }, analyzer);
		try(Directory dir = FSDirectory.open(Paths.get(INDEX_DIRECTORY.toString()));
			IndexWriter write = new IndexWriter(dir, config);) {			
			Query query = parser.parse(queryStr);
			write.deleteDocuments(query);
			write.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void updateDocument(Document doc) {
		Analyzer analyzer = new StandardAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		try(Directory dir = FSDirectory.open(Paths.get(INDEX_DIRECTORY.toString()));
			IndexWriter write = new IndexWriter(dir, config);) {
/*			Document doc = new Document();
			doc.add(new IntField("id", article.getId(), Field.Store.YES));
			doc.add(new TextField("title", article.getTitle(), Field.Store.YES));
			doc.add(new StringField("publish_date", new SimpleDateFormat("yyyyMMdd:HHmmss").format(article.getPdate()), Field.Store.YES));
			doc.add(new StringField("enable", article.getEnable()?"t":"f", Field.Store.YES));
			doc.add(new StringField("image", article.getImage(), Field.Store.YES));
			doc.add(new TextField("content", article.getContent(), Field.Store.YES));
			doc.add(new IntField("userid", article.getUserid(), Field.Store.YES));
			doc.add(new StringField("name", article.getUsername(), Field.Store.YES));*/
			write.updateDocument(new Term("id"), doc);
			write.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[])  {				
		long startTime = System.currentTimeMillis();
		CrudLucent searching = new CrudLucent();
		//searching.createIndex(OpenMode.CREATE);
		ArrayList<ArticleDto> articles = searching.search("title:bitch", 10, 0);
		for (ArticleDto a : articles) {
			System.out.println(a);
		}
		System.out.println("Found " + count_result);
		
		
		
/*		ArticleDto a = new ArticleDto();
		a.setId(15);
		a.setTitle("bitch");
		a.setPdate(new Date());*/
/*		Document doc = new Document();
		doc.add(new IntField("id", 15, Field.Store.YES));
		doc.add(new TextField("title", "bitch", Field.Store.YES));
		searching.updateDocument(doc);*/
		
		
		
		ArrayList<ArticleDto> articles1 = searching.search("title:war*", 10, 0);
		for (ArticleDto a1 : articles1) {
			System.out.println(a1);
		}
		System.out.println("Found " + count_result);
		//searching.deleteDocument("title:war");
		// searching.startSearch("title:khmer AND date:[20150130 TO 20151115]");
		long endTime = System.currentTimeMillis();
		long elapsedMilliSeconds = endTime - startTime;
		double elapsedSeconds = elapsedMilliSeconds / 1000.0;
		System.out.println(elapsedSeconds);
	}
}
