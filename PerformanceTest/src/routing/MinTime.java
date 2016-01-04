package routing;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import util.DoubleHelper;

public class MinTime {
	
	private static String fileBaseName = "C:\\Users\\50003152\\workspace\\PerformanceTest\\OutputFiles\\SmallOutput_";
	public static void main(String[] args) {
		double distLowerBound = 4000.0;
		long minTime = Long.MAX_VALUE;
		
		for(int i=0; i<4; i++){
			String inputFileName = fileBaseName + i + ".txt";
			
			String line = null;
			
	        try {
	            FileReader fileReader = new FileReader(inputFileName);
	            BufferedReader bufferedReader = new BufferedReader(fileReader);
	            
	            while((line = bufferedReader.readLine()) != null) {
	                String[] record = new String[7];
	                record = line.split(" ");
	                double dist = Double.parseDouble(record[4]);
	                long time = Long.parseLong(record[5]);
	                if(DoubleHelper.greaterThan(dist, distLowerBound)){
	                	if(time< minTime){
	                		minTime = time;
	                	}
	                }
	            }   

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
		
		System.out.println(minTime);
	}
}
