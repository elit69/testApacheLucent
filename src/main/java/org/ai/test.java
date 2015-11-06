package org.ai;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;



public class test {
	public static String value;
	public static Connection getConnection() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		// Assuming database bookstore exists
		return DriverManager.getConnection(
				"jdbc:postgresql://localhost:5432/bookstore", "postgres",
				"1234");
	}
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		try {
		      
		      BufferedReader br = new BufferedReader(new InputStreamReader (new FileInputStream("war and peace.txt"),"UTF-8"));
		      int i = 1;
		      StringBuilder sb = new StringBuilder();
		      String line = br.readLine();
		      while (line != null) {
		        if(i==100){		        	
		        	sb.setLength(sb.length() - 1);
		        	add(sb.toString());		        	
		        	sb = new StringBuilder();
		        	i=1;
		        }
		        
		        sb.append("('sdfsd', '"+ line +"'),");
		        i++;
		        line = br.readLine();
		      }		      
		      br.close();      		       
		    } catch (Exception e) {
		      e.printStackTrace();
		      System.err.println(e);      
		    }
	}
	public static boolean add( String s) {
		String sql = "INSERT INTO books(book_title,book_details) "	+ "VALUES " + s + ';';
		System.out.println(sql);
		try (Connection cnn = getConnection();) {
			PreparedStatement ps = cnn.prepareStatement(sql);
			if (ps.executeUpdate() > 0)
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
