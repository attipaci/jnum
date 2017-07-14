package test;

import jnum.Unit;
import jnum.data.image.Gaussian2D;

// TODO: Auto-generated Javadoc
/**
 * The Class PSFTest.
 */
public class PSFTest {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		Gaussian2D p1 = new Gaussian2D(4.0, 3.0, -55.0 * Unit.deg);
		Gaussian2D p2 = new Gaussian2D(9.0, 2.0, 22.0 * Unit.deg);
		
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
