package org.ai;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
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
		return DriverManager.getConnection("jdbc:postgresql://localhost:5432/articledb", "postgres", "1234");
	}

	public static void main(String[] args) throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException {
		write();
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

	public static int randInt(int min, int max) {
		return new Random().nextInt((max - min) + 1) + min;
	}

	private static String currentDate() {
		return new SimpleDateFormat("YYYY-MM-dd").format(new Date());
	}
}
