package model;

import util.CompareDouble;

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
		if(CompareDouble.equals(getLat(), pt.getLat()) &&
				CompareDouble.equals(getLng(), pt.getLng()) ) return true;
		
		return false;
	}
}
