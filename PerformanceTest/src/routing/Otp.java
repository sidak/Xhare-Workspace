package routing;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import jsonParser.JSONReader;
import util.DoubleHelper;


public class Otp {
	
	private static final double EARTH_RADIUS_IN_MILES=3958.75;
	private static final double MILE_TO_KM=1.609344;
	private static final double GEO_DIST_BOUND_IN_KM = 30;
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		int num = scan.nextInt();
		
		double[] lats = new double[num];
		double[] lngs = new double[num];
		
		for(int i=0; i<num; i++){
			lats[i] = scan.nextDouble();
			lngs[i] = scan.nextDouble();
		}
		
		long startTime = System.nanoTime();
		int i,j;
		for(i=0; i<num; i++){
			double srcLat, srcLng;
			srcLat = lats[i];
			srcLng = lngs[i];
			for(j=0; j<num; j++){
				if(i==j) continue;
				
				System.out.println("i is " + i + " and j is " + j);
				//doRoutingFromJSONResponse(srcLat, srcLng, lats[j], lngs[j]);
				//doGeometricRouting(srcLat, srcLng, lats[j], lngs[j]);
				doConditionalRouting(srcLat, srcLng, lats[j], lngs[j]);
				
			}
		}
		long endTime = System.nanoTime();
		long timeTakenInNano = endTime - startTime;
		System.out.println("Time taken for finding all pair shortest distance for 10 points: \n" + timeTakenInNano);
		long timeTakenPerRequestInMillis = timeTakenInNano/1000000;
		timeTakenPerRequestInMillis/= (num*num);
		System.out.println("Time taken per request in millis: \n" + timeTakenPerRequestInMillis);
		scan.close();
		
	}
	private static void doConditionalRouting(double srcLat, double srcLng, double destLat, double destLng) {
		double geoDist = distBetween(srcLat, srcLng, destLat, destLng);
		if(DoubleHelper.greaterThan(geoDist, GEO_DIST_BOUND_IN_KM)){
			doRoutingFromJSONResponse(srcLat, srcLng, destLat, destLng);
		}
		else{
			System.out.println("Geometric Distance between points in metres " + geoDist);
		}
		
	}
	public static double distBetween(double lat1, double lng1, double lat2,
			double lng2) {
		double dLat = Math.toRadians(lat2 - lat1);
		double dLng = Math.toRadians(lng2 - lng1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2)
				* Math.sin(dLng / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double dist = Math.abs((EARTH_RADIUS_IN_MILES*MILE_TO_KM) * c);
		return dist;
	}
	
	private static void doGeometricRouting(double srcLat, double srcLng, double destLat, double destLng) {
		double dLat = Math.toRadians(destLat - srcLat);
		double dLng = Math.toRadians(destLng - srcLng);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(Math.toRadians(srcLat))
				* Math.cos(Math.toRadians(destLat)) * Math.sin(dLng / 2)
				* Math.sin(dLng / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double dist = Math.abs((EARTH_RADIUS_IN_MILES*MILE_TO_KM) * c);
		System.out.println("Geometric Distance between points in metres " + dist);
		
	}
	private static void doRoutingFromJSONResponse(double srcLat, double srcLng, double destLat, double destLng) {
		
		String url = makeUrlString(srcLat, srcLng, destLat, destLng);
		try {
			JSONObject jsonResponse = JSONReader.readJsonFromUrl(url);
			if(!jsonResponse.has("plan")) return;
			JSONObject planObject = jsonResponse.getJSONObject("plan");
			JSONArray itineraries = planObject.getJSONArray("itineraries");
			JSONObject itinerary = (JSONObject) itineraries.get(0);
			JSONObject leg = (JSONObject) itinerary.getJSONArray("legs").get(0);
			
			double dist = leg.getDouble("distance");
			System.out.println("Distance between points in metres " + dist);
			long startTime = leg.getLong("startTime");
			long endTime = leg.getLong("endTime");
			long timeTakenInMillis = endTime - startTime;
			System.out.println("Time taken to find distance between a pair of points " + timeTakenInMillis);
			
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

