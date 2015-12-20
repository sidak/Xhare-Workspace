package preprocessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import index.SpatialIndex;
import index.TemporalIndex;
import model.Grid;
import model.Point;
import util.DistanceHelper;
import util.DoubleHelper;

public class Preprocessor {
	protected List< List<Double> > gridDistMatrix;
	protected List< List<Double> > gridTimeMatrix;
	protected List<Grid> grids;
	protected double minMapLat, minMapLng;
	protected double maxMapLat, maxMapLng;
	protected double gridLength, gridBreadth;
	protected int numGrids;
	protected int numGridsX, numGridsY;
	protected List<Double> gridLngs, gridLats;
	protected Map<Integer, List<SpatialIndex>> spatialGridIndex;
	protected Map<Integer, List<TemporalIndex>> temporalGridIndex;
	protected Scanner scan;

	public List<List<Double>> getGridDistMatrix() {
		return gridDistMatrix;
	}

	public List<List<Double>> getGridTimeMatrix() {
		return gridTimeMatrix;
	}

	public List<Double> getGridLngs() {
		return gridLngs;
	}

	public List<Double> getGridLats() {
		return gridLats;
	}

	public Map<Integer, List<SpatialIndex>> getSpatialGridIndex() {
		return spatialGridIndex;
	}

	public Map<Integer, List<TemporalIndex>> getTemporalGridIndex() {
		return temporalGridIndex;
	}
	
	public Preprocessor(){
		grids = new ArrayList<Grid>();
		gridDistMatrix = new ArrayList< List<Double> >();
		gridTimeMatrix = new ArrayList< List<Double> >();
		scan = new Scanner(System.in);
	}
	
	public void doPreprocessing() {
		System.out.println("Starting Work");
		takeInput();
		makeGrids();
		setAllGridCenters();
		calcGridDistAndTimeMatrix();
		computeSpatioTemporalGridIndex();
		System.out.println("Done Everything");
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
	
	public void printGridDistAndTimeMatrix() {
		System.out.println("The grid distance matrix is as follows\n");
		printGridMatrix(gridDistMatrix);
		System.out.println("The grid time matrix is as follows\n");
		printGridMatrix(gridTimeMatrix);
	}
	
	protected void takeInput() {
		takeMapInput();
		takeGridSizeInput();
	}

	protected void setAllGridCenters() {
		
		for(int i = 0; i<numGrids; i++){
			grids.get(i).setCenter(grids.get(i).getGeoCenter());
		}
		
	}
	
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
					double [] distTimePair = new double[2];
					distTimePair[0] = DistanceHelper.distBetween(srcGrid.getCenter(), destGrid.getCenter());
					distTimePair[1] = DistanceHelper.timeBetween(srcGrid.getCenter(), destGrid.getCenter());
					distances.add(distTimePair[0]);
					times.add(distTimePair[1]);
				}
			}
			gridDistMatrix.add(distances);
			gridTimeMatrix.add(times);
		}
		
		printGridDistAndTimeMatrix();
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
	
}
