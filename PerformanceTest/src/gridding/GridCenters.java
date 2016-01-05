package gridding;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GridCenters {
	
	private static double baseLat, baseLng;
	private static double boundLat, boundLng;
	private static final String boundsInputFileName = "C:\\Users\\50003152\\workspace\\PerformanceTest\\InputFiles\\bounds.txt";
	private static final String gridCentersFileName = "C:\\Users\\50003152\\workspace\\PerformanceTest\\InputFiles\\gridCenters.txt";
	
	private static final double HUNDRED_METER_PRECISION = 0.001;
	
	private static List<Double> gridLats;
	private static List<Double> gridLngs;
	private static List<Grid> grids;
	
	public static void main(String[] args) {
		initialiseBoundsFromFile(boundsInputFileName);
		
		gridLats = new ArrayList<Double>();
		gridLngs = new ArrayList<Double>();
		grids = new ArrayList<Grid>();
		
		computeGridLists();
		computeGridCenters();
		saveGridCenters(gridCentersFileName);
	}
	
	private static void computeGridCenters() {
		for(int i=0; i<gridLats.size()-1; i++){
			for(int j=0; j<gridLngs.size()-1; j++){
				grids.add(new Grid(0, gridLats.get(i), gridLngs.get(j), gridLats.get(i+1), gridLngs.get(j+1)));
			}
		}
		
	}
	
	private static void computeGridLists() {
		
		double lat = baseLat;
		while(lat<boundLat){
			gridLats.add(lat);
			lat+= HUNDRED_METER_PRECISION;
		}
		gridLats.add(boundLat);
		System.out.println(gridLats.size());
		
		double lng = baseLng;
		while(lng<boundLng){
			gridLngs.add(lng);
			lng+= HUNDRED_METER_PRECISION;
		}
		gridLngs.add(boundLng);
		System.out.println(gridLngs.size());
	}
	private static void saveGridCenters(String outputFileName) {
		
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(outputFileName);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.append(""+grids.size());
			bufferedWriter.newLine();
			
			for(int i=0; i<grids.size(); i++){
				Grid grid = grids.get(i);
				bufferedWriter.append(""+ grid.getGeoCenter().getLat()+"\t" + grid.getGeoCenter().getLng());
				bufferedWriter.newLine();
			}
			
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	private static void initialiseBoundsFromFile(String inputFileName) {
		String line = null;
        int lineCount = 0;
        try {
            FileReader fileReader = new FileReader(inputFileName);

            BufferedReader bufferedReader = new BufferedReader(fileReader);
            
            double[] cord = new double[4];
            
            while((line = bufferedReader.readLine()) != null) {
                cord[lineCount] = Double.parseDouble(line);
                lineCount++;
            }   
            baseLat = cord[0];
            baseLng = cord[1];
            boundLat = cord[2];
            boundLng = cord[3];
            
            bufferedReader.close();         
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + inputFileName + "'");                
        }
        catch(IOException ex) {
            System.out.println("Error reading file '" + inputFileName + "'");                  
            ex.printStackTrace();
        }
	}
	
	
}
