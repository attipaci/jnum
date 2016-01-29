package test;

import jnum.data.GaussianPSF;
import jnum.util.Unit;

public class PSFTest {

	public static void main(String[] args) {
		GaussianPSF p1 = new GaussianPSF(4.0, 3.0, -55.0 * Unit.deg);
		GaussianPSF p2 = new GaussianPSF(9.0, 2.0, 22.0 * Unit.deg);
		
		System.err.println(p1);
		
		p1.convolveWith(p2);
		System.err.println(p1);
		
		p1.deconvolveWith(p2);
		System.err.println(p1);
		
		System.err.println();
		
		/*
		p1 = new GaussianPSF(4.0, 3.0, -55.0 * Unit.deg);
		p2 = new GaussianPSF(4.0, 3.0, 35.0 * Unit.deg);
		
		p1.intersect(p2);
		System.err.println(p1);
		*/
	}
	
}
