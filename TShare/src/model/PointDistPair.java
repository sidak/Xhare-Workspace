package model;

public class PointDistPair implements Comparable<PointDistPair>{
	private Point point;
	private double pathDist;
	private double heuristicDist;
	public PointDistPair(){
		
	}
	public PointDistPair(Point point, double pathDist, double heurisiticDist){
		this.point = point;
		this.pathDist = pathDist;
		this.heuristicDist = heurisiticDist;
		
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
	
	@Override
	public int compareTo(PointDistPair pdp) {
		return Double.compare((getPathDist() + getHeuristicDist()), (pdp.getPathDist() + pdp.getHeuristicDist()));
	}
	
	
}
