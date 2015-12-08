package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import model.Graph;
import model.Grid;
import model.Point;
import util.Helper;

public class Preprocessor {
	
	// ask about the units of the distances
	
	private static List< List<Double> > gridDistMatrix;
	private static Graph graph;
	private static List<Grid> grids;
	private static double minMapLat, minMapLng;
	private static double maxMapLat, maxMapLng;
	private static double gridLength, gridBreadth;
	private static Scanner scan;
	private static int numGrids;
	// lists which we are going to search in order to 
	// know the grid in which a particular point lies
	private static List<Double> gridLngs, gridLats;
	
	public static void main(String[] args) {
		graph = new Graph();
		grids = new ArrayList<Grid>();
		gridDistMatrix = new ArrayList< List<Double> >();
		scan = new Scanner(System.in);
		
		takeGraphInput();
		takeMapInput();
		takeGridSizeInput();
		
		makeGrids();
		addGridCenters();
		calcGridDistMatrix();
		
		
		
	}

	private static void addGridCenters() {
		// TODO Auto-generated method stub
		
		
	}

	private static void calcGridDistMatrix() {
		// indexing starts from bottom left
		for(int i=0; i<numGrids; i++){
			Grid srcGrid = grids.get(i);
			List<Double> distances = new ArrayList<Double>(); 
			for(int j=0; j<numGrids; j++){
				
				if(i==j)distances.add(0.0);
				Grid destGrid = grids.get(j);
				double gridDistance = graph.calcEdgeDist(srcGrid.getCenter(), destGrid.getCenter());
				distances.add(gridDistance);
				
			}
			gridDistMatrix.get(i).addAll(distances);
			distances.clear();
		}
	}
	
	private static void makeGrids() {
		double mapLength = Helper.distBetween(maxMapLat, minMapLng, maxMapLat, maxMapLng);
		double mapBreadth = Helper.distBetween(minMapLat, minMapLng, maxMapLat, minMapLng);
		int numGridX = (int)Math.ceil(mapLength/gridLength);
		int numGridY = (int)Math.ceil(mapBreadth/gridBreadth);
		
		gridLngs = new ArrayList<Double>();
		gridLats = new ArrayList<Double>();
		
		double startLat = minMapLat;
		double startLng = minMapLng;
		double maxLat, maxLng;
		
		gridLngs.add(startLng);
		gridLats.add(startLat);
		
		for(int i = 0; i<numGridY; i++){
			
			double distFromMaxMapLat = Helper.distBetween(startLat, startLng, maxMapLat, startLng);
			// if there is an uneven division along Y
			if(Double.compare(distFromMaxMapLat, gridBreadth) == -1){
				maxLat = maxMapLat;
			}
			else maxLat = Helper.findLatTowardsNorth(gridBreadth, startLat);
			
			for(int j = 0; j<numGridX; j++){
				
				double distFromMaxMapLng = Helper.distBetween(startLat, startLng, startLat, maxMapLng);
				
				// if there is an uneven division along X
				if(Double.compare(distFromMaxMapLng, gridLength) == -1){
					maxLng = maxMapLng;
				}
				else maxLng = Helper.findLngTowardEast(gridLength, startLat, startLng);
				
				if(j==0) gridLngs.add(maxLng);
				
				int gridId = i*numGridX + j;
				grids.add(new Grid(gridId, startLat, startLng, maxLat, maxLng));
				
				startLng = maxLng;
				
			}
			startLat = maxLat;
			startLng = minMapLng;
			
			gridLats.add(startLat);
		}
	}

	private static void takeGridSizeInput() {
		gridLength = scan.nextDouble();
		gridBreadth = scan.nextDouble();		
	}

	private static void takeMapInput() {
		// the lower left 'base' point 
		minMapLat = scan.nextDouble();
		minMapLng = scan.nextDouble();
		// the upper right 'bound' point
		maxMapLat = scan.nextDouble();
		maxMapLng = scan.nextDouble();
	}

	private static  void takeGraphInput() {
		double lat1, lng1, lat2, lng2, dist, sl;
		
		lat1 = scan.nextDouble();
		lng1 = scan.nextDouble();
		lat2 = scan.nextDouble();
		lng2 = scan.nextDouble();
		dist = scan.nextDouble();
		sl = scan.nextDouble();
		
		// the end of the input will be denoted by
		// -1.0 -1.0 -1.0 -1.0 -1.0 -1.0
		while(Double.compare(dist, -1.0 )!=0){
			graph.addEdge(lat1, lng1, lat2, lng2, dist, sl);
			lat1 = scan.nextDouble();
			lng1 = scan.nextDouble();
			lat2 = scan.nextDouble();
			lng2 = scan.nextDouble();
			dist = scan.nextDouble();
			sl = scan.nextDouble();
		}
		
	}
}
