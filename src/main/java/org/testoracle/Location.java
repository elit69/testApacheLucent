package org.testoracle;

public class Location {
	private int location_id;
	private String country_id;
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
		return "Location [location_id=" + location_id + ", country_id=" + country_id + "]";
	}
}
