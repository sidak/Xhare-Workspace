package routing;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Main {
	private static final double GEO_CAR_DIST_BOUND_IN_KM = 5;
	private static final double GEO_WALK_DIST_BOUND_IN_KM = 2;
	private static final String CAR_MODE = "CAR";
	private static final String WALK_MODE = "WALK";
	private static final long TIME_BOUND_IN_MILLIS = 5*60*1000;
	private static final double SPEEDLIMIT_IN_KM_HR = 60.0;
	
	private static double[] lats;
	private static double[] lngs;
	private static String inputFileName = "C:\\Users\\50003152\\workspace\\PerformanceTest\\InputFiles\\gridCentersSmall.txt";
	private static int numLandmarks = 0;
	private static final int NUM_THREADS = 4;
		
	private static void takeInputFromFileAndInitialise() {
        String line = null;
        int lineCount = 0;
        try {
            FileReader fileReader = new FileReader(inputFileName);

            BufferedReader bufferedReader = new BufferedReader(fileReader);
            
            while((line = bufferedReader.readLine()) != null) {
                if(lineCount == 0){
                	numLandmarks = Integer.parseInt(line);
                	lats = new double[numLandmarks];
                	lngs = new double[numLandmarks];
                }
                else{
                	String[] point = new String[2];
                	point = line.split("\t");
                	
                	lats[lineCount-1] = Double.parseDouble(point[0]);
                	lngs[lineCount-1] = Double.parseDouble(point[1]);
                }
                lineCount ++;
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
	
	public static void main(String[] args) {
		
		takeInputFromFileAndInitialise();
		Otp otp = new Otp(lats, lngs, numLandmarks, TIME_BOUND_IN_MILLIS, SPEEDLIMIT_IN_KM_HR, CAR_MODE);
		//Otp otp = new Otp(lats, lngs, numLandmarks, GEO_WALK_DIST_BOUND_IN_KM, WALK_MODE);
		
		for(int i=0; i<NUM_THREADS; i++){
			OtpThread otpThread = new OtpThread(i, NUM_THREADS, otp);
			otpThread.start();
		}
	}
	
	private static void takeInputFromConsoleAndInitialise() {
		Scanner scan = new Scanner(System.in);
		numLandmarks = scan.nextInt();
		
		lats = new double[numLandmarks];
		lngs = new double[numLandmarks];
		
		for(int i=0; i<numLandmarks; i++){
			lats[i] = scan.nextDouble();
			lngs[i] = scan.nextDouble();
		}
		scan.close();
	}
}
