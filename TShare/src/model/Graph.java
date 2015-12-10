package model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import util.DistanceHelper;

public class Graph{
	// it is a directed graph
	private Map<Point, Set<Edge>> adjList;
	private Set<Point> vertices;
	
	public Graph(){
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
	
	public double calcEdgeDist(Point src, Point dest){
		return aStarDist(src, dest);
	}
	
	private double aStarDist(Point src, Point dest) {
		PriorityQueue<PointDistPair> minQueue = new PriorityQueue<PointDistPair>();
		// the heuristic distance is calculated in kilometers
		double heurisiticDist = DistanceHelper.distBetween(src, dest);
		PointDistPair srcPair = new PointDistPair(src, 0.0, heurisiticDist);
		
		minQueue.add(srcPair);
		
		while(minQueue.size()!=0){
			PointDistPair pdp = minQueue.peek();
			minQueue.remove();
			
			if(!adjList.containsKey(pdp.getPoint())){
				return -1.0;
			}
			Set<Edge> edges = adjList.get(pdp.getPoint());
			Iterator<Edge> it = edges.iterator();
			while(it.hasNext()){	
				Edge edge = it.next();
				Point childPt = edge.getDest();
				double edgeDist = edge.getDist();
				double pathDist = pdp.getPathDist() + edgeDist;
				double heurDist = DistanceHelper.distBetween(childPt, dest);
				if(childPt.isGeoEqual(dest)){
					return pathDist;
				}
				PointDistPair childPdp = new PointDistPair(childPt, pathDist, heurDist);
				minQueue.add(childPdp);
			}
		}
		return 0;
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
