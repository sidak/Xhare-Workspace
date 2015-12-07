package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import model.Graph;
import model.Grid;

public class Preprocessor {
	
	// ask about the units of the distances
	
	private static List< List<Double> > gridDistMatrix;
	private static Graph graph;
	private static List<Grid> grids;
	private static double mapLength, mapBreadth;
	private static double gridLength, gridBreadth;
	private static Scanner scan;
	
	public static void main(String[] args) {
		graph = new Graph();
		grids = new ArrayList<Grid>();
		gridDistMatrix = new ArrayList< List<Double> >();
		scan = new Scanner(System.in);
		
		takeGraphInput();
		takeMapSizeInput();
		takeGridSizeInput();
		
		makeGrids();
		calcGridDistMatrix();
		
		
		
	}

	private static void calcGridDistMatrix() {
		// TODO Auto-generated method stub
		
	}

	private static void makeGrids() {
		// TODO Auto-generated method stub
		
	}

	private static void takeGridSizeInput() {
		gridLength = scan.nextDouble();
		gridBreadth = scan.nextDouble();		
	}

	private static void takeMapSizeInput() {
		mapLength = scan.nextDouble();
		mapBreadth = scan.nextDouble();
	}

	private static  void takeGraphInput() {
		double lat1, lng1, lat2, lng2, dist, sl;
		
		lat1 = scan.nextDouble();
		lng1 = scan.nextDouble();
		lat2 = scan.nextDouble();
		lng2 = scan.nextDouble();
		dist = scan.nextDouble();
		sl = scan.nextDouble();
		
		// the end of the input will be denoted by
		// -1.0 -1.0 -1.0 -1.0 -1.0 -1.0
		while(Double.compare(dist, -1.0 )!=0){
			graph.addEdge(lat1, lng1, lat2, lng2, dist, sl);
			lat1 = scan.nextDouble();
			lng1 = scan.nextDouble();
			lat2 = scan.nextDouble();
			lng2 = scan.nextDouble();
			dist = scan.nextDouble();
			sl = scan.nextDouble();
		}
		
	}
}
