package model;

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
	
	public boolean isGeoEqual(Point p1, Point p2){
		if(Double.compare(p1.getLat(), p2.getLat()) == 0 &&
				Double.compare(p1.getLng(), p2.getLng()) == 0 ) return true;
		
		return false;
	}
}
