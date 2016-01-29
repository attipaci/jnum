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

import java.io.FileOutputStream;
import java.util.Locale;

import nom.tam.fits.BasicHDU;
import nom.tam.fits.Fits;
import nom.tam.fits.Header;
import nom.tam.fits.ImageHDU;
import nom.tam.util.BufferedDataOutputStream;

// TODO: Auto-generated Javadoc
/**
 * The Class FitsDeferredTest.
 */
public class FitsDeferredTest {
	
	/** The serial no. */
	int serialNo;
	
	/** The images. */
	float[][][] images = new float[4][][];
	
	/** The hdus. */
	BasicHDU[] hdus;
	
	static { Locale.setDefault(Locale.GERMANY); }
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws Exception the exception
	 */
	public static void main(String[] args) throws Exception {
		FitsDeferredTest test = new FitsDeferredTest();
		test.read(args[0]);
		System.gc();
		test.write("test.fits");
	}
	
	/**
	 * Read.
	 *
	 * @param fileName the file name
	 * @throws Exception the exception
	 */
	public void read(String fileName) throws Exception {
		Fits f = new Fits(fileName);
		hdus = f.read();
		for(int i=0; i<4; i++) images[i] = (float[][]) hdus[i].getData().getData();
		
		Header header = hdus[4].getHeader();
		serialNo = header.getIntValue("SCANNO");
	}
	
	/**
	 * Write.
	 *
	 * @param fileName the file name
	 * @throws Exception the exception
	 */
	public void write(String fileName) throws Exception {
		Fits g = new Fits();
		for (int i=0; i<4; i += 1) {
			ImageHDU nw = (ImageHDU) Fits.makeHDU(images[i]);
			g.addHDU(nw);
		}

		BasicHDU nw = Fits.makeHDU(hdus[4].getData());
		nw.addValue("SCANNO", serialNo, "");
		g.addHDU(nw);
		
		//BufferedFile bf = new BufferedFile(fileName, "rw");
		BufferedDataOutputStream bf = new BufferedDataOutputStream(new FileOutputStream(fileName));
		
		System.err.println("PI = " + Math.PI);
		
		g.write(bf);		
	}
	
} 
