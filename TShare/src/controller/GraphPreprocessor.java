package controller;

import java.beans.PropertyChangeListenerProxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import index.SpatialIndex;
import index.TemporalIndex;
import model.DirectedGraph;
import model.Grid;
import model.Point;
import util.DoubleHelper;
import util.DistanceHelper;

public class GraphPreprocessor extends Preprocessor{
	
	private List< List<Double> > gridDistMatrix;
	private List< List<Double> > gridTimeMatrix;
	private DirectedGraph graph;
	private List<Grid> grids;
	private Scanner scan;
	private double minMapLat, minMapLng;
	private double maxMapLat, maxMapLng;
	private double gridLength, gridBreadth;
	private int numGrids;
	private int numGridsX, numGridsY;
	private List<Double> gridLngs, gridLats;
	private Map<Integer, List<Point> > gridIdxMap;
	private Map<Integer, List<SpatialIndex>> spatialGridIndex;
	private Map<Integer, List<TemporalIndex>> temporalGridIndex;
	
	public GraphPreprocessor(){
		doPreprocessing();
	}
	public void doPreprocessing() {
		graph = new DirectedGraph();
		grids = new ArrayList<Grid>();
		gridDistMatrix = new ArrayList< List<Double> >();
		gridTimeMatrix = new ArrayList< List<Double> >();
		scan = new Scanner(System.in);
		System.out.println("Starting Work");
		takeInput();
		makeGrids();
		setAllGridCenters();
		calcGridDistAndTimeMatrix();
		computeSpatioTemporalGridIndex();
		saveToFile();
		System.out.println("Done Everything");
	}

	private void saveToFile() {
		// TODO Auto-generated method stub
		
	}

	private void computeSpatioTemporalGridIndex() {
		computeSpatialGridIndex();
		computeTemporalGridIndex();
	}

	private void computeTemporalGridIndex() {
		temporalGridIndex = new HashMap<Integer, List<TemporalIndex>>();
		for(int i=0; i<numGrids; i++){
			List<Double> times = gridTimeMatrix.get(i);
			
			List<TemporalIndex> temporalIndex = new ArrayList<TemporalIndex>();
			for(int j=0; j<times.size(); j++){
				if(i==j) continue;
				temporalIndex.add(new TemporalIndex(j, times.get(j)));
			}
			Collections.sort(temporalIndex);
			
			int lastNegIdx = -1;
			for(int j=0; j<temporalIndex.size(); j++){
				if(DoubleHelper.equals(temporalIndex.get(j).getGridTime(), -1.0)){
					lastNegIdx = j;
				}
				else break;
			}
			int idxSize = temporalIndex.size();
			temporalIndex = temporalIndex.subList(lastNegIdx +1, idxSize);
			
			temporalGridIndex.put(i, temporalIndex);
			
			if(temporalIndex.size()>0){
				System.out.println("Printing temporalIndex for Grid " +i);
				for(int j=0; j<temporalIndex.size(); j++){
					System.out.print(temporalIndex.get(j).getGridIdx() + " ");
				}
				System.out.print("\n");
			}
		}
		
	}

	private void computeSpatialGridIndex() {
		spatialGridIndex = new HashMap<Integer, List<SpatialIndex>>();
		for(int i=0; i<numGrids; i++){
			List<Double> distances = gridDistMatrix.get(i);
			
			List<SpatialIndex> spatialIndex = new ArrayList<SpatialIndex>();
			for(int j=0; j<distances.size(); j++){
				if(i==j) continue;
				spatialIndex.add(new SpatialIndex(j, distances.get(j)));
			}
			Collections.sort(spatialIndex);
			
			int lastNegIdx = -1;
			for(int j=0; j<spatialIndex.size(); j++){
				if(DoubleHelper.equals(spatialIndex.get(j).getGridDist(), -1.0)){
					lastNegIdx = j;
				}
				else break;
			}
			
			int idxSize = spatialIndex.size();
			spatialIndex = spatialIndex.subList(lastNegIdx +1, idxSize);
			spatialGridIndex.put(i, spatialIndex);
			
			if(spatialIndex.size()>0){
				System.out.println("Printing spatialIndex for Grid " +i);
				for(int j=0; j<spatialIndex.size(); j++){
					System.out.print(spatialIndex.get(j).getGridIdx() + " ");
				}
				System.out.print("\n");
			}
		}
		
	}

	private void printGridMatrix(List<List<Double>> gridMatrix) {
		System.out.println("Size of Matrix is "+ gridMatrix.size() +"\n");
		for(int i=0; i<gridMatrix.size(); i++){
			for(int j=0; j<gridMatrix.get(i).size(); j++){
				System.out.print("" + gridMatrix.get(i).get(j) + " ");
			}
			System.out.print("\n");
		}
	}

	private void takeInput() {
		takeGraphInput();
		takeMapInput();
		takeGridSizeInput();
	}

	private void setAllGridCenters() {
		
		mapPointsToGrids();
		for(int i = 0; i<numGrids; i++){
			Point roadNetworkCenter = findNearestPointToGridCenter(i);
			System.out.println("Grid center for grid " + i + " is " + roadNetworkCenter.toString());
			grids.get(i).setCenter(roadNetworkCenter);
		}
		
	}

	private Point findNearestPointToGridCenter(int gridIdx) {
		if(!gridIdxMap.containsKey(gridIdx)){
			return grids.get(gridIdx).getGeoCenter();
		}
		List<Point> ptList = gridIdxMap.get(gridIdx);
		Grid grid = grids.get(gridIdx);
		double minDist = Double.MAX_VALUE;
		
		Point geoCenter = grid.getGeoCenter();
		Point roadNetworkCenter = null;
	
		for(int j = 0; j<ptList.size(); j++){
			double ptDist = DistanceHelper.distBetween(geoCenter, ptList.get(j));
			if(Double.compare(ptDist, minDist) <= 0 ){
				minDist = ptDist;
				roadNetworkCenter = ptList.get(j);
			}
		}
		return roadNetworkCenter;
	}

	private void mapPointsToGrids() {
		gridIdxMap = new HashMap<Integer, List<Point> >();
		
		Iterator<Point> vertexIterator = graph.getVertices().iterator();
		
		while(vertexIterator.hasNext()){
			Point pt = vertexIterator.next();
			//System.out.println("Mapping the pt: " + pt.toString());
			int idx = calcGridIndex(pt);
			//System.out.println("Grid idx in which it lies " + idx);
			if(!gridIdxMap.containsKey(idx)){
				ArrayList<Point> ptList = new ArrayList<Point>();
				ptList.add(pt);
				gridIdxMap.put(idx, ptList);
			}
			gridIdxMap.get(idx).add(pt);
		}
	}
	
	public int calcGridIndex(Point pt){
		double lat = pt.getLat();
		double lng = pt.getLng();
		int idxLat = Collections.binarySearch(gridLats, lat);
		int idxLng = Collections.binarySearch(gridLngs, lng);
		
		idxLat = (-1*(idxLat+1)) -1;
		idxLng = (-1*(idxLng +1))-1;
		//System.out.println("idxLat is : " + idxLat + " idxLng is : " +idxLng);
		return (idxLat*numGridsX + idxLng);
	}

	private void calcGridDistAndTimeMatrix() {
		for(int i=0; i<numGrids; i++){
			Grid srcGrid = grids.get(i);
			List<Double> distances = new ArrayList<Double>(); 
			List<Double> times = new ArrayList<Double>();
			for(int j=0; j<numGrids; j++){
				
				if(i==j){
					distances.add(0.0);
					times.add(0.0);
				}
				else{
					Grid destGrid = grids.get(j);
					double [] distTimePair = graph.calcEdgeDistAndTime(srcGrid.getCenter(), destGrid.getCenter());
					distances.add(distTimePair[0]);
					times.add(distTimePair[1]);
				}
			}
			gridDistMatrix.add(distances);
			gridTimeMatrix.add(times);
		}
		
		printGridDistAndTimeMatrix();
	}

	private void printGridDistAndTimeMatrix() {
		System.out.println("The grid distance matrix is as follows\n");
		printGridMatrix(gridDistMatrix);
		System.out.println("The grid time matrix is as follows\n");
		printGridMatrix(gridTimeMatrix);
	}
	
	private void makeGrids() {
		double mapLength = DistanceHelper.distBetween(maxMapLat, minMapLng, maxMapLat, maxMapLng);
		double mapBreadth = DistanceHelper.distBetween(minMapLat, minMapLng, maxMapLat, minMapLng);
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
			else maxLat = DistanceHelper.findLatTowardsNorth(gridBreadth, startLat);
			
			for(int j = 0; j<numGridsX; j++){
				
				if(isUnevenDivisionAlongX(startLat, startLng)){
					maxLng = maxMapLng;
				}
				else maxLng = DistanceHelper.findLngTowardEast(gridLength, startLat, startLng);
				
				if(i==0) gridLngs.add(maxLng);
				
				int gridId = i*numGridsX + j;
				grids.add(new Grid(gridId, startLat, startLng, maxLat, maxLng));
				
				startLng = maxLng;
				
			}
			startLat = maxLat;
			startLng = minMapLng;
			
			gridLats.add(startLat);
		}
		System.out.println("The grid boundary latitudes are");
		for(int i=0; i<gridLats.size(); i++){
			System.out.print(gridLats.get(i) +" ");
		}
		System.out.print("\n");
		System.out.println("The grid boundary longitudes are");
		for(int i=0; i<gridLngs.size(); i++){
			System.out.print(gridLngs.get(i) +" ");
		}
		System.out.println("\n");
		
	}

	private boolean isUnevenDivisionAlongX(double startLat, double startLng) {
		double distFromMaxMapLng = DistanceHelper.distBetween(startLat, startLng, startLat, maxMapLng);
		return DoubleHelper.lessThan(distFromMaxMapLng, gridLength);		
	}

	private boolean isUnevenDivisionAlongY(double startLat, double startLng) {
		double distFromMaxMapLat = DistanceHelper.distBetween(startLat, startLng, maxMapLat, startLng);
		return DoubleHelper.lessThan(distFromMaxMapLat, gridBreadth);	
	}

	private void takeGridSizeInput() {
		gridLength = scan.nextDouble();
		gridBreadth = scan.nextDouble();		
	}

	private void takeMapInput() {
		inputLowerLeftPoint();
		inputUpperRightPoint();
	}

	private void inputUpperRightPoint() {
		maxMapLat = scan.nextDouble();
		maxMapLng = scan.nextDouble();
	}
	
	private void inputLowerLeftPoint() {
		minMapLat = scan.nextDouble();
		minMapLng = scan.nextDouble();
	}
	// TODO: take care of units of speed limit
	private void takeGraphInput() {
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
	
	private boolean isMoreInput(double dist){
		return Double.compare(dist, -1.0) == 1 ;
	}
}
