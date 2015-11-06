package org.ai;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;

public class LuceneExample {
	public static final File INDEX_DIRECTORY = new File("IndexDirectory");
	@SuppressWarnings("deprecation")
	public void createIndex() {
		System.out.println("-- Indexing --");
		try {
			// JDBC Section
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// Assuming database bookstore exists
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bookstore", "root", "");
			Statement stmt = conn.createStatement();
			String sql = "select book_id,book_title,book_details from books";
			ResultSet rs = stmt.executeQuery(sql);

			//delete old file index
			if(INDEX_DIRECTORY.exists()){
				 String[] myFiles = INDEX_DIRECTORY.list();
	               for (int i=0; i<myFiles.length; i++) {
	                   File myFile = new File(INDEX_DIRECTORY, myFiles[i]); 
	                   myFile.delete();
	               }
			}
			
			// Lucene Section
			Directory directory = new SimpleFSDirectory(INDEX_DIRECTORY);
			
			StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_40);
			IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_40,analyzer);
			IndexWriter iWriter = new IndexWriter(directory, config);

			// Looping through resultset and adding to index file
			int count = 0;
			while (rs.next()) {
				Document doc = new Document();

				doc.add(new Field("book_id", rs.getString("book_id"),
						Field.Store.YES, Field.Index.ANALYZED));
				doc.add(new Field("book_title", rs.getString("book_title"),
						Field.Store.YES, Field.Index.ANALYZED));
				doc.add(new Field("book_details", rs.getString("book_details"),
						Field.Store.YES, Field.Index.ANALYZED));

				// Adding doc to iWriter
				iWriter.addDocument(doc);
				count++;
			}

			System.out.println(count + " record indexed");

			// Closing iWriter

			iWriter.commit();
			iWriter.close();

			// Closing JDBC connection
			rs.close();
			stmt.close();
			conn.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void search(String keyword) {
		System.out.println("-- Seaching --");
		try {
			// Searching
			@SuppressWarnings("deprecation")
			IndexReader reader = IndexReader.open(FSDirectory.open(INDEX_DIRECTORY));
			IndexSearcher searcher = new IndexSearcher(reader);
			Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_40);
			// MultiFieldQueryParser is used to search multiple fields
			String[] filesToSearch = { "book_title", "book_details" };
			MultiFieldQueryParser mqp = new MultiFieldQueryParser(Version.LUCENE_40, filesToSearch, analyzer);

			Query query = mqp.parse(keyword);// search the given keyword

			System.out.println("query >> " + query);

			TopDocs hits = searcher.search(query, 100); // run the query

			System.out.println("Results found >> " + hits.totalHits);

			for (int i = 0; i < hits.totalHits; i++) {
				Document doc = searcher.doc(hits.scoreDocs[i].doc);
				System.out.println(doc.get("book_id") + " "	+ doc.get("book_title") );
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		LuceneExample obj = new LuceneExample();

		// creating index
		obj.createIndex();

		// searching keyword
		obj.search("gay");

		// using wild card serach
		/*obj.search("data*");*/

		// using logical operator
		/*obj.search("data1 OR data2");
		obj.search("data1 AND data2");*/

	}
}