package model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Graph{
	// it is a directed graph
	private int numVertices;
	private Map<Point, Set<Edge>> adjList = new HashMap<Point, Set<Edge>>();
	private Set<Point> vertices = new HashSet<Point>();
	
	public void addEdge(double la1, double lo1, double la2, double lo2, double dist, double sl){
		Point u = new Point(la1, lo1);
		Point v = new Point(la2, lo2);
		Edge e = new Edge(v, dist, sl);
		
		vertices.add(u);
		vertices.add(v);
		
		// check if the list corresponding to u has not been initialised
		if(!adjList.containsKey(u)) adjList.put(u, new HashSet<Edge>());
		
		adjList.get(u).add(e);
		
	}
	
	
}
