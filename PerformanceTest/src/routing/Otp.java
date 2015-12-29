package routing;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import jsonParser.JSONReader;
import util.DistanceHelper;
import util.DoubleHelper;


public class Otp {
	private static final String outputFileBaseName = "C:\\Users\\50003152\\workspace\\PerformanceTest\\OutputFiles\\SmallOutput";
	
	private double geoDistBoundInKm;
	private double[] lats;
	private double[] lngs;
	private int numLandmarks;
	private int requestCounter;
	
	public Otp(double[] lats, double[] lngs, int numLandmarks, double geoDistBoundInKm){
		this.lats = lats;
		this.lngs = lngs;
		this.numLandmarks = numLandmarks;
		this.requestCounter = 0;
		this.geoDistBoundInKm = geoDistBoundInKm;
	}
	
	
	/**
	 * Performs routing assuming that distance from A to B is equal to distance from B to A
	 * @param numLandmarks number of landmarks
	 */
	public void doRouting(int startIdx, int mod) {
		long routingStartTime = System.nanoTime();
		try {
			String outputFileName = outputFileBaseName +"_" + startIdx + ".txt"; 
			FileWriter fileWriter = new FileWriter(outputFileName);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			
			
			int i,j;
			for(i=startIdx; i<numLandmarks; i+=mod){
				double srcLat, srcLng;
				srcLat = lats[i];
				srcLng = lngs[i];
				
				System.out.println("Finding distances and times to all points from the point: " + srcLat + ", " + srcLng + " and index = " + i +" and thread id is " + startIdx );
				
				for(j=i+1; j<numLandmarks; j++){
					
					bufferedWriter.write(""+srcLat + " " + srcLng + " ");
					bufferedWriter.write(""+lats[j] + " " + lngs[j] + " ");
					System.out.println("To point " + lats[j] + ", " + lngs[j] + " and index = " + j);
					long requestStartTime = System.nanoTime();
					doConditionalRouting(srcLat, srcLng, lats[j], lngs[j], bufferedWriter);
					long requestEndTime = System.nanoTime();
					double requestTimeInMillis = ((double)(requestEndTime - requestStartTime))/1000000.0;
					bufferedWriter.write("" + requestTimeInMillis);
					//System.out.println("Request Time: " + requestTimeInMillis);
					bufferedWriter.newLine();
				}
			}
			
			long routingEndTime = System.nanoTime();
			double routingTimeTakenInMillis = ((double)(routingEndTime - routingStartTime))/1000000.0;
			System.out.println("Total time taken: \n" + routingTimeTakenInMillis);
			System.out.println("Total number of requests: \n" + requestCounter);
			
			
			
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
		
		
	}
	
	private void doConditionalRouting(double srcLat, double srcLng, double destLat, double destLng, 
			BufferedWriter bufferedWriter) {
		try{
			double geoDist = DistanceHelper.distBetween(srcLat, srcLng, destLat, destLng);
			if(DoubleHelper.lessThan(geoDist, geoDistBoundInKm)){
				doRoutingFromJSONResponse(srcLat, srcLng, destLat, destLng, bufferedWriter);
				requestCounter++;
			}
			else{
				bufferedWriter.write("0 -1 ");
				//System.out.println("Distance: 0 and Time: -1, outside distance bound");
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		
	}
	
	private void doRoutingFromJSONResponse(double srcLat, double srcLng, double destLat, double destLng,
			BufferedWriter bufferedWriter) {
		
		String url = makeUrlString(srcLat, srcLng, destLat, destLng);
		try {
			JSONObject jsonResponse = JSONReader.readJsonFromUrl(url, false);
			if(!jsonResponse.has("plan")){
				bufferedWriter.write("-1 -1 ");
				//System.out.println("Distance: -1 and Time: -1, otp path not found");
				return;
			}
			JSONObject planObject = jsonResponse.getJSONObject("plan");
			JSONArray itineraries = planObject.getJSONArray("itineraries");
			JSONObject itinerary = (JSONObject) itineraries.get(0);
			JSONObject leg = (JSONObject) itinerary.getJSONArray("legs").get(0);
			
			double dist = leg.getDouble("distance");
			long startTime = leg.getLong("startTime");
			long endTime = leg.getLong("endTime");
			long timeTakenInMillis = endTime - startTime;
			bufferedWriter.write("" + dist + " " + timeTakenInMillis + " ");
			//System.out.println("Distance: " + dist + " and Time: " + timeTakenInMillis);
			
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private void doRoutingFromXMLResponse(double srcLat, double srcLng, double destLat, double destLng) {
		String urlString = makeUrlString(srcLat, srcLng, destLat, destLng);
		
		SAXBuilder saxBuilder = new SAXBuilder();
		try {
			URL url = new URL(urlString);
			Document document = saxBuilder.build(url);
			Element responseElement = document.getRootElement();
			Element planElement = responseElement.getChild("plan");
			Element itineraryElements = planElement.getChild("itineraries");
			Element itinerary = itineraryElements.getChild("itineraries");
			
			Element legElements = itinerary.getChild("legs");
			Element leg = legElements.getChild("legs");
			String dist = leg.getChildText("distance");
			System.out.println("Distance between points in metres " + dist);
			long startTime = Long.getLong(leg.getChildText("startTime"));
			long endTime = Long.getLong(leg.getChildText("endTime"));
			long timeTakenInMillis = endTime - startTime;
			System.out.println("Time taken to find distance between a pair of points " + timeTakenInMillis);
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private String makeUrlString(double srcLat, double srcLng, double destLat, double destLng) {
		// http://13.218.151.99:8080/otp/routers/default/plan?fromPlace=13.0206140544476%2C77.486572265625&toPlace=12.940322128384627%2C77.63626098632812&mode=CAR
		String baseUrl = "http://13.218.151.99:8080/otp/routers/default/plan?";
		String srcLoc = "fromPlace=" + srcLat + "%2C" + srcLng;
		String destLoc = "toPlace=" + destLat + "%2C" + destLng;
		String modeString = "mode=CAR";
		String urlString = baseUrl + srcLoc + "&" + destLoc + "&" + modeString;
		return urlString;
	}

	
}

