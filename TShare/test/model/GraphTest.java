package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class GraphTest {

	@Test
	public void testAddEdge() {
		DirectedGraph testGraph = new DirectedGraph();
		testGraph.addEdge(5, 5, 10, 10, 0.44, 11);
		Map <Point, Set<Edge>> adj = testGraph.getAdjList();
		Set<Edge> edges = adj.get(new Point(5,5));
		assertEquals("only 1 edge should be there",1, edges.size());
		assertTrue("edge should contain pt 2", edges.contains(new Edge(new Point(10, 10), 0.44, 11)));
		assertEquals("only 1 entry shld be there", 1,  adj.size());
	}

	@Test
	public void testCalcEdgeDist() {
		DirectedGraph testGraph = new DirectedGraph();
		testGraph.addEdge(0.125, 0.125, 0.25, 0.125, 13.9, 10);
		testGraph.addEdge(0.25, 0.125, 0.375, 0.125, 13.9, 10);
		testGraph.addEdge(0.375, 0.125, 0.50, 0.125, 13.9, 10);
		testGraph.addEdge(0.50, 0.125, 0.625, 0.125, 13.9, 10);
		testGraph.addEdge(0.625, 0.125, 0.625, 0.25, 13.9, 10);
		testGraph.addEdge(0.625, 0.25, 0.625, 0.375, 13.9, 10);
		testGraph.addEdge(0.625, 0.375, 0.625, 0.5, 13.9, 10);
		testGraph.addEdge(0.625, 0.5, 0.5, 0.5, 13.9, 10);
		
		testGraph.addEdge(0.125, 0.125, 0.125, 0.25, 13.9, 10);
		testGraph.addEdge(0.125, 0.25, 0.125, 0.375, 13.9, 10);
		testGraph.addEdge(0.125, 0.375, 0.25, 0.375, 13.9, 10);
		testGraph.addEdge(0.25, 0.375, 0.375, 0.375, 13.9, 10);
		testGraph.addEdge(0.375, 0.375, 0.5, 0.375, 13.9, 10);
		testGraph.addEdge(0.5, 0.375, 0.5, 0.5, 13.9, 10);
		
		assertEquals("distance should be 83.4 km", 83.4, 
				testGraph.calcEdgeDist(new Point(0.125, 0.125), new Point(0.5, 0.5)), 0.0000001);
		
	}

	@Test
	public void testGetNumVertices() {
		DirectedGraph testGraph = new DirectedGraph();
		testGraph.addEdge(5, 5, 10, 10, 0.44, 11);
		assertEquals("2 vertices shld be there", 2, testGraph.getNumVertices());
		testGraph.addEdge(5, 5.1, 10, 10, 0.44, 11);
		assertEquals("3 vertices shld be there", 3, testGraph.getNumVertices());
		testGraph.addEdge(5.11, 5.1, 10.1, 10, 0.44, 11);
		assertEquals("5 vertices shld be there", 5, testGraph.getNumVertices());
		testGraph.addEdge(5, 5.1, 10.1, 10, 0.44, 11);
		assertEquals("5 vertices shld be there", 5, testGraph.getNumVertices());
		
	}

	@Test
	public void testGetVertices() {
		DirectedGraph testGraph = new DirectedGraph();
		testGraph.addEdge(5, 5, 10, 10, 0.44, 11);
		assertTrue("point 1 should be there", testGraph.getVertices().contains(new Point(5,5)));
		assertTrue("point 2 should be there", testGraph.getVertices().contains(new Point(10,10)));
		assertFalse("other point should not be there", testGraph.getVertices().contains(new Point(5.1,5)));
	}

}
