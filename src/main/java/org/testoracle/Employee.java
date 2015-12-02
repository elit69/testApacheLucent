package org.testoracle;

public class Employee {
	private long salary;
	private int department_id;
	private int manager_id;
	public int getManager_id() {
		return manager_id;
	}
	public void setManager_id(int manager_id) {
		this.manager_id = manager_id;
	}
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
	@Override
	public String toString() {
		return "Employee [salary=" + salary + ", department_id=" + department_id + ", manager_id=" + manager_id + "]";
	}

}
