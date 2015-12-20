package preprocessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import model.DirectedGraph;
import model.Grid;
import model.Point;
import util.DistanceHelper;

public class GraphPreprocessor extends Preprocessor{
	
	private DirectedGraph graph;
	private Map<Integer, List<Point> > gridIdxMap;
	
	public GraphPreprocessor(){
		super();
		graph = new DirectedGraph();
		gridIdxMap = new HashMap<Integer, List<Point> >();

	}
	
	@Override
	protected void takeInput() {
		takeGraphInput();
		super.takeInput();
	}
	
	@Override
	protected void setAllGridCenters() {
		
		mapPointsToGrids();
		for(int i = 0; i<numGrids; i++){
			Point roadNetworkCenter = findNearestPointToGridCenter(i);
			System.out.println("Grid center for grid " + i + " is " + roadNetworkCenter.toString());
			grids.get(i).setCenter(roadNetworkCenter);
		}
		
	}

	@Override
	protected void calcGridDistAndTimeMatrix() {
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
	
	private void takeGraphInput() {
		double lat1, lng1, lat2, lng2, distInKiloMeters, speedLimitInKmPerHr;
		
		lat1 = scan.nextDouble();
		lng1 = scan.nextDouble();
		lat2 = scan.nextDouble();
		lng2 = scan.nextDouble();
		distInKiloMeters = scan.nextDouble();
		speedLimitInKmPerHr = scan.nextDouble();
		
		while(isMoreInput(distInKiloMeters)){
			graph.addEdge(lat1, lng1, lat2, lng2, distInKiloMeters, speedLimitInKmPerHr);
			lat1 = scan.nextDouble();
			lng1 = scan.nextDouble();
			lat2 = scan.nextDouble();
			lng2 = scan.nextDouble();
			distInKiloMeters = scan.nextDouble();
			speedLimitInKmPerHr = scan.nextDouble();
		}
		
	}
	
	private boolean isMoreInput(double dist){
		return Double.compare(dist, -1.0) == 1 ;
	}
}
