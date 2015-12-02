package org.testoracle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class testoracle {
	public static Connection getConnection() throws SQLException, InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
		// return
		// DriverManager.getConnection("jdbc:oracle:thin:@192.168.178.254:1521:HRDDB",
		// "hrd01", "hrd01");
		return DriverManager.getConnection("jdbc:oracle:thin:@192.168.178.254:1521:ORCL", "hr", "hr");
	}

	public static void main(String[] args) {
		//natural join employee and department
		ArrayList<EmployeeDepartmentLocation> listedl = new ArrayList<EmployeeDepartmentLocation>();
		int i = 0;
		for (Employee e : listEmployee()) {
			for (Department d : listDepartment()) {
				if (e.getDepartment_id() == d.getDepartment_id() && e.getManager_id() == d.getManager_id()) {
					EmployeeDepartmentLocation edl = new EmployeeDepartmentLocation();
					edl.setDepartment_id(e.getDepartment_id());
					edl.setLocation_id(d.getLocation_id());
					edl.setSalary(e.getSalary());
					listedl.add(edl);
					i++;
				}
			}
		}
		System.out.println(i);
		 
		//natural join location
		int y=0;
		for (EmployeeDepartmentLocation edl : listedl) {
			for (Location l : listLocation()) {
				if (edl.getLocation_id() == l.getLocation_id()) {
					edl.setCountry_id(l.getCountry_id());
					y++;
				}
			}
		}
		System.out.println(y);
		
		//generate string country id(no duplicate)
		ArrayList<String> list1 = new ArrayList<String>();
		list1.add(listedl.get(0).getCountry_id());
		for (EmployeeDepartmentLocation edl : listedl) {
			if (!list1.contains((edl.getCountry_id()))) {				
				list1.add(edl.getCountry_id());
			}
		}
		System.out.println(list1);
		
		//groupby countryid into arraylist of arraylist
		ArrayList<ArrayList<EmployeeDepartmentLocation>> list = new ArrayList<ArrayList<EmployeeDepartmentLocation>>();
		ArrayList<EmployeeDepartmentLocation> tmp = new ArrayList<EmployeeDepartmentLocation>();
		for (String a : list1) {
			for (EmployeeDepartmentLocation edl : listedl) {
				if(edl.getCountry_id().equals(a)){
					tmp.add(edl);
				}
			}
			list.add(tmp);
			tmp = new ArrayList<EmployeeDepartmentLocation>();
		}
		
		//calculate avg of salary
		for(ArrayList<EmployeeDepartmentLocation> a:list){
			int sum = 0;
			for(EmployeeDepartmentLocation edl:a){
				sum+=edl.getSalary();
			}
			float avg = (float)sum/a.size();
			System.out.println(a.get(0).getCountry_id() + "\t" + avg);
		}
	}

	public static ArrayList<Employee> listEmployee() {
		String sql = "SELECT salary,department_id,manager_id FROM employees";
		try (Connection cnn = getConnection();) {
			PreparedStatement ps = cnn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			ArrayList<Employee> list = new ArrayList<Employee>();
			while (rs.next()) {
				Employee s = new Employee();
				s.setSalary(rs.getLong("salary"));
				s.setDepartment_id(rs.getInt("department_id"));
				s.setManager_id(rs.getInt("manager_id"));
				list.add(s);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ArrayList<Department> listDepartment() {
		String sql = "SELECT department_id,location_id,manager_id FROM DEPARTMENTS";
		try (Connection cnn = getConnection();) {
			PreparedStatement ps = cnn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			ArrayList<Department> list = new ArrayList<Department>();
			while (rs.next()) {
				Department s = new Department();
				s.setDepartment_id(rs.getInt("department_id"));
				s.setLocation_id(rs.getInt("location_id"));
				s.setManager_id(rs.getInt("manager_id"));
				list.add(s);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ArrayList<Location> listLocation() {
		String sql = "SELECT location_id,country_id FROM locations";
		try (Connection cnn = getConnection();) {
			PreparedStatement ps = cnn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			ArrayList<Location> list = new ArrayList<Location>();
			while (rs.next()) {
				Location s = new Location();
				s.setLocation_id(rs.getInt("location_id"));
				s.setCountry_id(rs.getString("country_id"));
				list.add(s);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
