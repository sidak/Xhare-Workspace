package model;

public class Grid {
	private int id;
	private double minLat;
	private double maxLat;
	private double minLongi;
	private double maxLongi;
	private Point center;
	
	public boolean isInGrid(Point pt){
		if(compareRange(pt.getLat(), minLat, maxLat) &&
				compareRange(pt.getLongi(), minLongi, maxLongi)){
			return true;
		}
		return false;
	}
	// ask about the corners of the grid 
	public boolean compareRange(double val, double min, double max){
		if(Double.compare(val, min) >= 0 && Double.compare(val, max) <=0 ){
			return true;
		}
		return false;
	}
	
	public Point getGeoCenter(){
		return new Point(-1,(minLat+maxLat)/2, (minLongi + maxLongi)/2);
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getMinLat() {
		return minLat;
	}

	public void setMinLat(double minLat) {
		this.minLat = minLat;
	}

	public double getMaxLat() {
		return maxLat;
	}

	public void setMaxLat(double maxLat) {
		this.maxLat = maxLat;
	}

	public double getMinLongi() {
		return minLongi;
	}

	public void setMinLongi(double minLongi) {
		this.minLongi = minLongi;
	}

	public double getMaxLongi() {
		return maxLongi;
	}

	public void setMaxLongi(double maxLongi) {
		this.maxLongi = maxLongi;
	}

	public Point getCenter() {
		return center;
	}

	public void setCenter(Point center) {
		this.center = center;
	}

}
