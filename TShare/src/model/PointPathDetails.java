package model;

import util.DoubleHelper;

public class PointPathDetails implements Comparable<PointPathDetails>{
	private Point point;
	private double pathDist;
	private double heuristicDist;
	private double pathTime;
	
	public PointPathDetails(){
		
	}
	public PointPathDetails(Point point, double pathDist, double heurisiticDist, double pathTime){
		this.point = point;
		this.pathDist = pathDist;
		this.heuristicDist = heurisiticDist;
		this.pathTime = pathTime;
	}
	public double getHeuristicDist() {
		return heuristicDist;
	}
	public void setHeuristicDist(double heuristicDist) {
		this.heuristicDist = heuristicDist;
	}
	public Point getPoint() {
		return point;
	}
	public void setPoint(Point point) {
		this.point = point;
	}
	public double getPathDist() {
		return pathDist;
	}
	public void setPathDist(double dist) {
		this.pathDist = dist;
	}
	
	public double getPathTime() {
		return pathTime;
	}
	public void setPathTime(double pathTime) {
		this.pathTime = pathTime;
	}
	
	@Override
	public int compareTo(PointPathDetails pdp) {
		
		double totalDistA, totalDistB;
		totalDistA = getPathDist() + getHeuristicDist();
		totalDistB = pdp.getPathDist() + pdp.getHeuristicDist();
		
		if(DoubleHelper.greaterThan(totalDistA, totalDistB)) return 1;
		else if (DoubleHelper.lessThan(totalDistA, totalDistB)) return -1;
		else return 0;
	}
	
	
}
