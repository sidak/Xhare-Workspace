package model;

public class Point {
	private double lat;
	private double longi;
	
	public Point(double la, double lo){
		this.lat = la;
		this.longi = lo;
	}
	
	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLongi() {
		return longi;
	}

	public void setLongi(double longi) {
		this.longi = longi;
	}
	
	public boolean isGeoEqual(Point p1, Point p2){
		if(Double.compare(p1.getLat(), p2.getLat()) == 0 &&
				Double.compare(p1.getLongi(), p2.getLongi()) == 0 ) return true;
		
		return false;
	}
}
