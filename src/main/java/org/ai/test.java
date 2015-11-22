package org.ai;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class test {


	public static Connection getConnection()
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		Class.forName("org.postgresql.Driver").newInstance();
		return DriverManager.getConnection("jdbc:postgresql://localhost:5432/articledb", "postgres", "12345");
	}

	public static void main(String[] args)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		int record =0;
		int times = 4;
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < times; i++) {
			File INDEX_DIRECTORY = new File("books");
			if(INDEX_DIRECTORY.exists()){
				 String[] myFiles = INDEX_DIRECTORY.list();
	              for (int y=0; y<myFiles.length; y++) {
	            	  if(myFiles[y].equals("books.7z")) continue;
	            	  File myFile = new File(INDEX_DIRECTORY, myFiles[y]); 
	                  record+=write(myFile.toPath().toString());
	              }
			}	
		}		
		long endTime = System.currentTimeMillis();
		long elapsedMilliSeconds = endTime - startTime;
		double elapsedSeconds = elapsedMilliSeconds / 1000.0;
		System.out.println(elapsedSeconds + "seconds");
		System.out.println(record + " records");
		
		//write("books/1984 - George Orwell.txt");
		
/*		long startTime = System.currentTimeMillis();
		ArrayList<ArticleDto> listarticle = search1("A.content", "as", 15, 7600);
		long endTime = System.currentTimeMillis();
		long elapsedMilliSeconds = endTime - startTime;
		double elapsedSeconds = elapsedMilliSeconds / 1000.0;
		System.out.println(elapsedSeconds);
		System.out.println(listarticle.size());*/
		
/*		long startTime = System.currentTimeMillis();
		ArrayList<ArticleDto> listarticle = search2("as", 15, 7600);
		long endTime = System.currentTimeMillis();
		long elapsedMilliSeconds = endTime - startTime;
		double elapsedSeconds = elapsedMilliSeconds / 1000.0;
		System.out.println(elapsedSeconds);
		System.out.println(listarticle.size());*/
		
/*		long startTime = System.currentTimeMillis();
		search3("as", 15, 7600);
		long endTime = System.currentTimeMillis();
		long elapsedMilliSeconds = endTime - startTime;
		double elapsedSeconds = elapsedMilliSeconds / 1000.0;
		System.out.println(elapsedSeconds);		*/
	}

	public static int write(String bookname) {
		System.out.println(bookname);
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(bookname), "UTF-8"));
			int i = 0;
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			while (line != null) {
				if (line.length() > 20) {
					String title = line.substring(0, 20);
					if (line.contains("'")) {
						line = line.replace("'", "''");
						title = title.replace("'", "''");
					}
					sb.append("('" + title + " ', " + randInt(1, 6) + " , " + "'" + line + " ', " + "'" + currentDate()
							+ " ', " + true + " , " + "'some image'" + "),");					
					i++;
				}
				line = br.readLine();
			}
			sb.setLength(sb.length() - 1);
			add(sb.toString());
			sb = null;
			br.close();
            System.out.println(i + " records");
			return i;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	private static boolean add(String s) {
		// String sql = "INSERT INTO books(book_title,book_details) " + "VALUES
		// " + s + ';';
		String sql1 = "INSERT INTO tbarticle(title,userid,CONTENT,publish_date,ENABLE,image) " + "	values" + s + ";";
		//System.out.println(sql1);
		try (Connection cnn = getConnection();) {
			PreparedStatement ps = cnn.prepareStatement(sql1);
			if (ps.executeUpdate() > 0)
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static ArrayList<ArticleDto> search1(String type, String keyword, int limit, int offset) {
		String sql = "SELECT A.ID, A.title, A.userid, U.username, A.publish_date, A.ENABLE, A.image "
				+ "FROM tbarticle A INNER JOIN tbuser U " 
				+ "ON U.ID = A.userid " 
				+ "WHERE LOWER (" + type + ") LIKE LOWER (?) " + "ORDER BY A.ID " 
				+ "LIMIT "+ limit +" OFFSET " + offset;
		try (Connection cnn = getConnection();) {
			PreparedStatement ps = cnn.prepareStatement(sql);
			ps.setString(1, "%" + keyword + "%");
			ResultSet rs = ps.executeQuery();
			//System.out.println(ps.toString());
			ArrayList<ArticleDto> listArticle = new ArrayList<ArticleDto>();
			while (rs.next()) {
				ArticleDto s = new ArticleDto();
				s.setId(rs.getInt("id"));
				s.setTitle(rs.getString("title"));
				s.setUserid(rs.getInt("userid"));
				s.setUsername(rs.getString("username"));
				s.setPdate(rs.getDate("publish_date"));
				s.setEnable(rs.getBoolean("ENABLE"));
				s.setImage(rs.getString("image"));
				listArticle.add(s);
			}
			return listArticle;
		} catch (Exception e) {
			e.printStackTrace();
			e.printStackTrace();
		}
		return null;
	}

	public static ArrayList<ArticleDto> search2(String keyword, int limit, int offset) {
		String sql = "{call search_article_content(?,?,?)}";
		try (Connection cnn = getConnection();) {			
			CallableStatement cstm = getConnection().prepareCall(sql);
			cstm.setString(1, keyword);
			cstm.setInt(2, limit);
			cstm.setInt(3, offset);
			ResultSet rs = cstm.executeQuery();
			System.out.println(cstm.toString());
			ArrayList<ArticleDto> listArticle = new ArrayList<ArticleDto>();
			while (rs.next()) {
				ArticleDto s = new ArticleDto();
				s.setId(rs.getInt("id"));
				s.setTitle(rs.getString("title"));
				s.setUserid(rs.getInt("userid"));
				s.setUsername(rs.getString("name"));
				s.setPdate(rs.getDate("publish_date"));
				s.setEnable(rs.getBoolean("ENABLE"));
				s.setImage(rs.getString("image"));
				listArticle.add(s);
			}
			return listArticle;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}

	public static String search3(String keyword, int limit, int offset) {
		String sql = "{call search_article_content_json(?,?,?)}";
		try (Connection cnn = getConnection();) {			
			CallableStatement cstm = getConnection().prepareCall(sql);
			cstm.setString(1, keyword);
			cstm.setInt(2, limit);
			cstm.setInt(3, offset);
			ResultSet rs = cstm.executeQuery();
			System.out.println(cstm.toString());
			if (rs.next()) {
				return rs.getString(1);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}
	
	public static int randInt(int min, int max) {
		return new Random().nextInt((max - min) + 1) + min;
	}

	private static String currentDate() {
		return new SimpleDateFormat("YYYY-MM-dd").format(new Date());
	}
}
