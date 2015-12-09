package controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import model.Graph;
import model.Grid;
import model.Point;
import util.Helper;

public class Preprocessor {
	
	private static List< List<Double> > gridDistMatrix;
	private static Graph graph;
	private static List<Grid> grids;
	private static Scanner scan;
	private static double minMapLat, minMapLng;
	private static double maxMapLat, maxMapLng;
	private static double gridLength, gridBreadth;
	private static int numGrids;
	private static int numGridsX, numGridsY;
	private static List<Double> gridLngs, gridLats;
	private static Map<Integer, List<Point> > gridIdxMap;
	
	public static void main(String[] args) {
		graph = new Graph();
		grids = new ArrayList<Grid>();
		gridDistMatrix = new ArrayList< List<Double> >();
		scan = new Scanner(System.in);
		
		takeInput();
		makeGrids();
		setAllGridCenters();
		calcGridDistMatrix();
		
	}

	private static void takeInput() {
		takeGraphInput();
		takeMapInput();
		takeGridSizeInput();
	}

	private static void setAllGridCenters() {
		
		mapPointsToGrids();
		for(int i = 0; i<numGrids; i++){
			Point roadNetworkCenter = findNearestPointToGridCenter(i);
			grids.get(i).setCenter(roadNetworkCenter);
		}
		
	}

	private static Point findNearestPointToGridCenter(int gridIdx) {
		List<Point> ptList = gridIdxMap.get(gridIdx);
		
		Grid grid = grids.get(gridIdx);
		double minDist = Double.MAX_VALUE;
		
		Point geoCenter = grid.getGeoCenter();
		Point roadNetworkCenter = null;
		
		for(int j = 0; j<ptList.size(); j++){
			double ptDist = Helper.distBetween(geoCenter, ptList.get(j));
			if(Double.compare(ptDist, minDist) <= 0 ){
				minDist = ptDist;
				roadNetworkCenter = ptList.get(j);
			}
		}
		return roadNetworkCenter;
	}

	private static void mapPointsToGrids() {
		gridIdxMap = new HashMap<Integer, List<Point> >();
		
		Iterator<Point> vertexIterator = graph.getVertices().iterator();
		
		while(vertexIterator.hasNext()){
			int idx = calcGridIndex(vertexIterator.next());
			
			if(!gridIdxMap.containsKey(idx)){
				ArrayList<Point> ptList = new ArrayList<Point>();
				ptList.add(vertexIterator.next());
				gridIdxMap.put(idx, ptList);
			}
			gridIdxMap.get(idx).add(vertexIterator.next());
		}
	}
	
	private static int calcGridIndex(Point pt){
		double lat = pt.getLat();
		double lng = pt.getLng();
		int idxLat = Collections.binarySearch(gridLats, lat);
		int idxLng = Collections.binarySearch(gridLngs, lng);
		
		return (idxLat*numGridsX + idxLng);
	}

	private static void calcGridDistMatrix() {
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
		numGridsX = (int)Math.ceil(mapLength/gridLength);
		numGridsY = (int)Math.ceil(mapBreadth/gridBreadth);
		numGrids = numGridsX*numGridsY;
		
		gridLngs = new ArrayList<Double>();
		gridLats = new ArrayList<Double>();
		
		double startLat = minMapLat;
		double startLng = minMapLng;
		double maxLat, maxLng;
		
		gridLngs.add(startLng);
		gridLats.add(startLat);
		
		for(int i = 0; i<numGridsY; i++){
			
			if(isUnevenDivisionAlongY(startLat, startLng)){
				maxLat = maxMapLat;
			}
			else maxLat = Helper.findLatTowardsNorth(gridBreadth, startLat);
			
			for(int j = 0; j<numGridsX; j++){
				
				if(isUnevenDivisionAlongX(startLat, startLng)){
					maxLng = maxMapLng;
				}
				else maxLng = Helper.findLngTowardEast(gridLength, startLat, startLng);
				
				if(j==0) gridLngs.add(maxLng);
				
				int gridId = i*numGridsX + j;
				grids.add(new Grid(gridId, startLat, startLng, maxLat, maxLng));
				
				startLng = maxLng;
				
			}
			startLat = maxLat;
			startLng = minMapLng;
			
			gridLats.add(startLat);
		}
	}

	private static boolean isUnevenDivisionAlongX(double startLat, double startLng) {
		double distFromMaxMapLng = Helper.distBetween(startLat, startLng, startLat, maxMapLng);
		return (Double.compare(distFromMaxMapLng, gridLength) == -1);		
	}

	private static boolean isUnevenDivisionAlongY(double startLat, double startLng) {
		double distFromMaxMapLat = Helper.distBetween(startLat, startLng, maxMapLat, startLng);
		return (Double.compare(distFromMaxMapLat, gridBreadth) == -1);	
	}

	private static void takeGridSizeInput() {
		gridLength = scan.nextDouble();
		gridBreadth = scan.nextDouble();		
	}

	private static void takeMapInput() {
		inputLowerLeftPoint();
		inputUpperRightPoint();
	}

	private static void inputUpperRightPoint() {
		maxMapLat = scan.nextDouble();
		maxMapLng = scan.nextDouble();
	}

	private static void inputLowerLeftPoint() {
		minMapLat = scan.nextDouble();
		minMapLng = scan.nextDouble();
	}
	// TODO: take care of units of speed limit
	private static  void takeGraphInput() {
		double lat1, lng1, lat2, lng2, distInKiloMeters, sl;
		
		lat1 = scan.nextDouble();
		lng1 = scan.nextDouble();
		lat2 = scan.nextDouble();
		lng2 = scan.nextDouble();
		distInKiloMeters = scan.nextDouble();
		sl = scan.nextDouble();
		
		while(isMoreInput(distInKiloMeters)){
			graph.addEdge(lat1, lng1, lat2, lng2, distInKiloMeters, sl);
			lat1 = scan.nextDouble();
			lng1 = scan.nextDouble();
			lat2 = scan.nextDouble();
			lng2 = scan.nextDouble();
			distInKiloMeters = scan.nextDouble();
			sl = scan.nextDouble();
		}
		
	}
	
	private static boolean isMoreInput(double dist){
		return Double.compare(dist, -1.0) == 1 ;
	}
}
