/*******************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila[AT]sigmyne.com>.
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
 *     Attila Kovacs <attila[AT]sigmyne.com> - initial API and implementation
 ******************************************************************************/
package test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.StringTokenizer;

import jnum.Util;


// TODO: Auto-generated Javadoc
/**
 * The Class MeanSpec.
 */
public class MeanSpec {
	
	/** The binf. */
	double binf = 0.25;
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String args[]){
		MeanSpec meanSpec = new MeanSpec();
		try { meanSpec.calc(args[0]); }
		catch(IOException e) { e.printStackTrace(); }		
	}
	
	/**
	 * Calc.
	 *
	 * @param fileName the file name
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void calc(String fileName) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
		String line = null;
		
		double tof = binf;
		int n = 0;
		double[] data = null;
		
		while((line = in.readLine()) != null) if(line.length() > 0) if(line.charAt(0) != '#') {
			StringTokenizer tokens = new StringTokenizer(line);
						
			if(data == null) {
				data = new double[tokens.countTokens() - 1]; 
				System.err.println("Found " + data.length + " channels.");
			}
			
			double f = Double.parseDouble(tokens.nextToken());
			
			if(f > tof) {
				double sumw = 0.0;
				for(int i=0; i<data.length; i++) {
					data[i] /= n;
					sumw += 1.0 / data[i];
				}
				System.out.println(tof + "\t" + Util.e3.format(Math.sqrt(1.0/sumw)));
				
				Arrays.fill(data, 0.0);
				tof += binf;
				n = 0;
			}
			else {
				for(int i=0; i<data.length; i++) data[i] += Double.parseDouble(tokens.nextToken());
				n++;
			}
		}
		in.close();
	}
	
	
	
}
