package model;

import util.DoubleHelper;

public class Point {
	private double lat;
	private double lng;
	
	public Point(double lat, double lng){
		this.lat = lat;
		this.lng = lng;
	}
	
	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}
	
	public boolean isGeoEqual(Point pt){
		if(DoubleHelper.equals(getLat(), pt.getLat()) &&
				DoubleHelper.equals(getLng(), pt.getLng()) ) return true;
		
		return false;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(lat);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(lng);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}
	
	// TODO: This currently compares doubles with 1E-15 of precision. 
	// Check if there is a need for handling them with lesser precision
	// so as to approximate nearby vertices 
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point other = (Point) obj;
		if (Double.doubleToLongBits(lat) != Double.doubleToLongBits(other.lat))
			return false;
		if (Double.doubleToLongBits(lng) != Double.doubleToLongBits(other.lng))
			return false;
		return true;
	}
	
	public String toString(){
		return (""+getLat() + " " +getLng());
	}
}
