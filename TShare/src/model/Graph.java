package model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
		
		// check if the list corresponding to u has not been initialised
		if(!adjList.containsKey(u)) adjList.put(u, new HashSet<Edge>());
		
		adjList.get(u).add(e);
		
	}
	
	// use A* to calculate distance with heuristic as euclidean distance
	public double calcEdgeDist(Point src, Point dest){
		return aStarDist(src, dest);
	}
	
	private double aStarDist(Point src, Point dest) {
		// TODO Auto-generated method stub
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
