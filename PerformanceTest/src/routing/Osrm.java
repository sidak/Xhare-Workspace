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

public class Osrm {
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		int num = scan.nextInt();
		
		List<Double> lats = new ArrayList<Double>();
		List<Double> lngs = new ArrayList<Double>();
		
		for(int i=0; i<num; i++){
			double lat, lng;
			lat = scan.nextDouble();
			lng = scan.nextDouble();
			lats.add(lat);
			lngs.add(lng);
		}
		
		long startTime = System.nanoTime();
		for(int i=0; i<num; i++){
			double srcLat, srcLng;
			srcLat = lats.get(i);
			srcLng = lngs.get(i);
			for(int j=0; j<num; j++){
				if(i==j) continue;
				double destLat, destLng;
				destLat = lats.get(j);
				destLng = lngs.get(j);
				doRoutingFromJSONResponse(srcLat, srcLng, destLat, destLng);
			}
		}
		long endTime = System.nanoTime();
		double timeTakenInMillis = ((double)(endTime - startTime))/1000000.0;
		System.out.println("Time taken for finding all pair shortest distance for 10 points: \n" + timeTakenInMillis);
		scan.close();
		
	}
	
	private static void doRoutingFromJSONResponse(double srcLat, double srcLng, double destLat, double destLng) {
		
		String url = makeUrlString(srcLat, srcLng, destLat, destLng);
		try {
			JSONObject jsonResponse = JSONReader.readJsonFromUrl(url, true);
			JSONObject routeSummaryObject = jsonResponse.getJSONObject("route_summary");
			
			double dist = (double)routeSummaryObject.getInt("total_distance");
			System.out.println("Distance between points in metres " + dist);
			long timeTakenInMillis = routeSummaryObject.getLong("total_time")*1000;
			System.out.println("Time taken to find distance between a pair of points " + timeTakenInMillis);
			
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	
	private static String makeUrlString(double srcLat, double srcLng, double destLat, double destLng) {
		// http://router.project-osrm.org/viaroute?loc=12.750005,77.780467&loc=13.1489682,77.553344
		String baseUrl = "http://router.project-osrm.org/viaroute?";
		String srcLoc = "loc=" + srcLat + "," + srcLng;
		String destLoc = "loc=" + destLat + "," + destLng;
		String urlString = baseUrl + srcLoc + "&" + destLoc;
		return urlString;
	}
	
}

