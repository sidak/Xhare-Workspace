package model;

import static org.junit.Assert.*;

import org.junit.Test;

public class PointPathDetailsTest {

	@Test
	public void testCompareTo() {
		PointPathDetails pdpA = new PointPathDetails(new Point(5, 5), 100.05, 111.45, 0);
		PointPathDetails pdpB = new PointPathDetails(new Point(15, 15), 100.15, 111.45, 0);
		PointPathDetails pdpC = new PointPathDetails(new Point(15, 15), 100.049999, 111.45, 0);
		PointPathDetails pdpD = new PointPathDetails(new Point(15, 15), 100.0511, 111.45, 0);
		PointPathDetails pdpE = new PointPathDetails(new Point(15, 15), 100.0500, 111.45, 0);
		assertEquals("pdpA should have less dist than pdpB", -1, pdpA.compareTo(pdpB));
		assertNotEquals("pdpA should have greater dist than pdpB", 1, pdpA.compareTo(pdpC));
		assertEquals("pdpA should have less dist than pdpB", -1, pdpA.compareTo(pdpD));
		assertEquals("pdpA should have equal dist than pdpB", 0, pdpA.compareTo(pdpE));
	}

}
