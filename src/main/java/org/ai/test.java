package org.ai;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
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
	public static String value;

	public static Connection getConnection() throws SQLException, InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		// Assuming database bookstore exists
		// return
		// DriverManager.getConnection("jdbc:postgresql://localhost:5432/bookstore",
		// "postgres","1234");
		return DriverManager.getConnection("jdbc:postgresql://localhost:5432/articledb", "postgres", "12345");
	}

	public static void main(String[] args) throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException {
		//write();
		long startTime = System.currentTimeMillis();
/*		for(ArticleDto a:search("A.content", "a")){
			System.out.println(a);
		}*/
		search("A.content", "a");
		long endTime = System.currentTimeMillis();
		long elapsedMilliSeconds = endTime - startTime;
		double elapsedSeconds = elapsedMilliSeconds / 1000.0;
		System.out.println(elapsedSeconds);
	}

	public static void write() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("war and peace.txt"),
					"UTF-8"));
			int i = 1;
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			while (line != null) {
				if (i == 100) {
					 sb.setLength(sb.length() - 1);
					add(sb.toString());
					sb = new StringBuilder();
					i = 1;				
				}
				String title = line.length() <= 5 ? "null" : line.substring(0, 5);
				sb.append("('" + title + "'," + randInt(1, 6) + ", '" + line + "', '" + currentDate() + "'," + true + ",'some image'" +"),");
				i++;
				line = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e);
		}
	}

	public static boolean add(String s) {
		//String sql = "INSERT INTO books(book_title,book_details) " + "VALUES " + s + ';';
		String sql1 = "INSERT INTO tbarticle(title,userid,CONTENT,publish_date,ENABLE,image) " + "	values" + s +";";
		System.out.println(sql1);
		try (Connection cnn = getConnection();) {
			PreparedStatement ps = cnn.prepareStatement(sql1);
			if (ps.executeUpdate() > 0)
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	public static ArrayList<ArticleDto> search(String type, String keyword) {
		String sql =
		"SELECT A.ID, A.title, A.userid, U.username, A.publish_date, A.ENABLE, A.image "+
		"FROM tbarticle A INNER JOIN tbuser U "+
		"ON U.ID = A.userid "+
		"WHERE LOWER ("+ type +") LIKE LOWER (?) "+
		"ORDER BY A.ID ";
		try (Connection cnn = getConnection();) {
			PreparedStatement ps = cnn.prepareStatement(sql);			
			ps.setString(1, "%" +keyword + "%");
			ResultSet rs = ps.executeQuery();
			System.out.println(ps.toString());
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
