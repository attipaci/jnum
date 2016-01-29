/*******************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
 * All rights reserved. 
 * 
 * This file is part of jnum.
 * 
 *     kovacs.util is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     kovacs.util is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with kovacs.util.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Attila Kovacs <attila_kovacs[AT]post.harvard.edu> - initial API and implementation
 ******************************************************************************/
package test;

// TODO: Auto-generated Javadoc
// This is testing the speed of access to Java fields...
// As of 2012 Mar, all methods of access perform comparably
// to 1%.

/**
 * The Class AccessTest.
 */
public class AccessTest {
	
	/** The Constant rounds. */
	public final static int rounds = 1000000000;
	
	/** The Constant s3o2. */
	public final static double s3o2 = 0.5 * Math.sqrt(3.0);
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		AccessTest access = new AccessTest();
			
		access.test0();
		
		access.test1();
		
		access.test2();
		
		access.test3();	
	}
	
	/**
	 * Test0.
	 */
	public void test0() {
		double x = 1.0, y = 0.0;
		
		System.err.println("local variables:");
		long time = -System.currentTimeMillis();
		
		for(int i=rounds; --i >= 0; ) {
			double temp = x;
			x = 0.5 * x - s3o2 * y;
			y = 0.5 * y + s3o2 * temp;
		}
		
		time += System.currentTimeMillis();
		
		System.err.println("result = " + x + ", " + y);
		System.err.println("time = " + time + "ms");
		System.err.println();
	}
	
	/**
	 * Test1.
	 */
	public void test1() {
		final V1 v = new V1();
		
		System.err.println(v.getName() + ":");
		long time = -System.currentTimeMillis();
		
		for(int i=rounds; --i >= 0; ) {
			double temp = v.x;
			v.x = 0.5 * v.x - s3o2 * v.y;
			v.y = 0.5 * v.y + s3o2 * temp;
		}
		
		time += System.currentTimeMillis();
		
		System.err.println("result = " + v.x + ", " + v.y);
		System.err.println("time = " + time + "ms");
		System.err.println();
	}
	
	/**
	 * Test2.
	 */
	public void test2() {
		final V2 v = new V2();
		
		System.err.println(v.getName() + ":");
		long time = -System.currentTimeMillis();
		
		for(int i=rounds; --i >= 0; ) {
			double temp = v.getX();
			v.setX(0.5 * v.getX() - s3o2 * v.getY());
			v.setY(0.5 * v.getY() + s3o2 * temp);
		}
		
		time += System.currentTimeMillis();
		
		System.err.println("result = " + v.getX() + ", " + v.getY());
		System.err.println("time = " + time + "ms");
		System.err.println();
	}

	/**
	 * Test3.
	 */
	public void test3() {
		final V2 v = new V2();
		
		System.err.println(v.getName() + " (internal):");
		long time = -System.currentTimeMillis();
		
		for(int i=rounds; --i >= 0; ) v.rotate(); 
		
		time += System.currentTimeMillis();
		
		System.err.println("result = " + v.getX() + ", " + v.getY());
		System.err.println("time = " + time + "ms");
		System.err.println();
	}
	
}

class V1 {
	public double x = 1.0, y = 0.0;
	
	public String getName() { return "public direct"; }
}

class V2 {
	private double x = 1.0, y = 0.0;
	public final static double s3o2 = 0.5 * Math.sqrt(3.0);
	
	public final double getX() { return x; }
	
	public final double getY() { return y; }
	
	public final void setX(final double value) { x = value; }
	
	public final void setY(final double value) { y = value; }
	
	public final void rotate() {
		double temp = x;
		x = 0.5 * x - s3o2 * y;
		y = 0.5 * y + s3o2 * temp;
	}
	
	public String getName() { return "private via get/set"; }
	
}

