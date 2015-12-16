package model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import util.DistanceHelper;

public class DirectedGraph{
	private Map<Point, Set<Edge>> adjList;
	private Set<Point> vertices;
	
	public DirectedGraph(){
		this.adjList = new HashMap<Point, Set<Edge>>();
		this.vertices = new HashSet<Point>();
	}
	public void addEdge(double lat1, double lng1, double lat2, double lng2, double dist, double sl){
		Point u = new Point(lat1, lng1);
		Point v = new Point(lat2, lng2);
		Edge e = new Edge(v, dist, sl);
		
		vertices.add(u);
		vertices.add(v);
		
		if(!adjList.containsKey(u)) adjList.put(u, new HashSet<Edge>());
		
		adjList.get(u).add(e);
		
	}
	
	public double[] calcEdgeDistAndTime(Point src, Point dest){
		return aStarDistAndTime(src, dest);
	}
	
	private double[] aStarDistAndTime(Point src, Point dest) {
		PriorityQueue<PointPathDetails> minQueue = new PriorityQueue<PointPathDetails>();
		// the heuristic distance is calculated in kilometers
		double heurisiticDist = DistanceHelper.distBetween(src, dest);
		PointPathDetails srcPair = new PointPathDetails(src, 0.0, heurisiticDist, 0.0);
		
		minQueue.add(srcPair);
		
		while(minQueue.size()!=0){
			PointPathDetails pointPathDetail = minQueue.peek();
			minQueue.remove();
			
			if(!adjList.containsKey(pointPathDetail.getPoint())){
				return new double[] {-1.0, -1.0};
			}
			Set<Edge> edges = adjList.get(pointPathDetail.getPoint());
			Iterator<Edge> it = edges.iterator();
			while(it.hasNext()){	
				Edge edge = it.next();
				Point childPt = edge.getDest();
				double edgeDist = edge.getDist();
				double pathDist = pointPathDetail.getPathDist() + edgeDist;
				double pathTime = pointPathDetail.getPathTime() + (edgeDist/edge.getSpeedLimit());
				double heurDist = DistanceHelper.distBetween(childPt, dest);
				if(childPt.isGeoEqual(dest)){
					double[] destDetail = new double[2];
					destDetail[0] = pathDist;
					destDetail[1] = pathTime;
					return destDetail;
				}
				PointPathDetails childPdp = new PointPathDetails(childPt, pathDist, heurDist, pathTime);
				minQueue.add(childPdp);
			}
		}
		return new double[] {-1.0, -1.0};
	}
	
	public int getNumVertices() {
		return vertices.size();
	}

	public Map<Point, Set<Edge>> getAdjList() {
		return adjList;
	}
	public Set<Point> getVertices() {
		return vertices;
	}
	
	
}
