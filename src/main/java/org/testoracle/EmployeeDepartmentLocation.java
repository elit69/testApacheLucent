package org.testoracle;

public class EmployeeDepartmentLocation {
	private long salary;
	private int department_id;
	private int location_id;
	private String country_id;
	public long getSalary() {
		return salary;
	}
	public void setSalary(long salary) {
		this.salary = salary;
	}
	public int getDepartment_id() {
		return department_id;
	}
	public void setDepartment_id(int department_id) {
		this.department_id = department_id;
	}
	public int getLocation_id() {
		return location_id;
	}
	public void setLocation_id(int location_id) {
		this.location_id = location_id;	
	}
	public String getCountry_id() {
		return country_id;
	}
	public void setCountry_id(String country_id) {
		this.country_id = country_id;
	}
	@Override
	public String toString() {
		return "EmployeeDepartmentLocation [salary=" + salary + ", department_id=" + department_id + ", location_id="
				+ location_id + ", country_id=" + country_id + "]";
	}	
}
