package routing;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

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
	
	private static final double GEO_DIST_BOUND_IN_KM = 5;
	private static double[] lats;
	private static double[] lngs;
	private static int requestCounter=0;
	
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		int num = scan.nextInt();
		
		lats = new double[num];
		lngs = new double[num];
		
		for(int i=0; i<num; i++){
			lats[i] = scan.nextDouble();
			lngs[i] = scan.nextDouble();
		}
		
		doRouting(num);
		
		scan.close();
		
	}
	
	/**
	 * Performs routing assuming that distance from A to B is equal to distance from B to A
	 * @param numLandmarks number of landmarks
	 */
	private static void doRouting(int numLandmarks) {
		long routingStartTime = System.nanoTime();
		
		int i,j;
		for(i=0; i<numLandmarks; i++){
			double srcLat, srcLng;
			srcLat = lats[i];
			srcLng = lngs[i];
			
			System.out.println("Finding distances and times to all points from the point: " + srcLat + ", " + srcLng + " and index = " + i );
			
			for(j=i+1; j<numLandmarks; j++){
				
				System.out.println("To point " + lats[j] + ", " + lngs[j] + " and index = " + j);
				long requestStartTime = System.nanoTime();
				doConditionalRouting(srcLat, srcLng, lats[j], lngs[j]);
				long requestEndTime = System.nanoTime();
				double requestTimeInMillis = ((double)(requestEndTime - requestStartTime))/1000000.0;
				System.out.println("Request Time: " + requestTimeInMillis);
				
			}
		}
		
		long routingEndTime = System.nanoTime();
		double routingTimeTakenInMillis = ((double)(routingEndTime - routingStartTime))/1000000.0;
		System.out.println("Total time taken: \n" + routingTimeTakenInMillis);
		System.out.println("Total number of requests: \n" + requestCounter);
	}
	
	private static void doConditionalRouting(double srcLat, double srcLng, double destLat, double destLng) {
		double geoDist = DistanceHelper.distBetween(srcLat, srcLng, destLat, destLng);
		if(DoubleHelper.lessThan(geoDist, GEO_DIST_BOUND_IN_KM)){
			doRoutingFromJSONResponse(srcLat, srcLng, destLat, destLng);
			requestCounter++;
		}
		else{
			System.out.println("Distance: 0 and Time: -1, outside distance bound");
		}
		
	}
	
	private static void doRoutingFromJSONResponse(double srcLat, double srcLng, double destLat, double destLng) {
		
		String url = makeUrlString(srcLat, srcLng, destLat, destLng);
		try {
			JSONObject jsonResponse = JSONReader.readJsonFromUrl(url);
			if(!jsonResponse.has("plan")){
				System.out.println("Distance: -1 and Time: -1, otp path not found");
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
			System.out.println("Distance: " + dist + " and Time: " + timeTakenInMillis);
			
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private static void doRoutingFromXMLResponse(double srcLat, double srcLng, double destLat, double destLng) {
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
	
	private static String makeUrlString(double srcLat, double srcLng, double destLat, double destLng) {
		// http://13.218.151.99:8080/otp/routers/default/plan?fromPlace=13.0206140544476%2C77.486572265625&toPlace=12.940322128384627%2C77.63626098632812&mode=CAR
		String baseUrl = "http://13.218.151.99:8080/otp/routers/default/plan?";
		String srcLoc = "fromPlace=" + srcLat + "%2C" + srcLng;
		String destLoc = "toPlace=" + destLat + "%2C" + destLng;
		String modeString = "mode=CAR";
		String urlString = baseUrl + srcLoc + "&" + destLoc + "&" + modeString;
		return urlString;
	}
	
}

