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
/**
 * The Class FloatTest.
 */
public class FloatTest {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		long N = 1000000000;
		long start, end;
		
		long k = 0;
		float f = 1.0F;
		double d = 1.0;
		
		start = System.currentTimeMillis();
		for(int i=0; i<N; i++) k++; 		
		end = System.currentTimeMillis();
		System.err.println("> " + k);
		long base = end - start;
		
		start = System.currentTimeMillis();
		for(int i=0; i<N; i++) {
			f = (1.0F * (f+0.5F)) - 0.5F; 
			k++;
		}
		end = System.currentTimeMillis();
		
		System.err.println("float: " + (end-start-base) + " ms");
		
		start = System.currentTimeMillis();
		for(int i=0; i<N; i++) {
			d = (1.0 * (d+0.5)) - 0.5; 		
			k++;
		}
		end = System.currentTimeMillis();
		
		System.err.println("double: " + (end-start-base) + " ms");
	}
	
	
	
}
