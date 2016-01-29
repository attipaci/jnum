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
package jnum.astro;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Vector;

import jnum.data.GaussianSource;
import jnum.data.GridImage2D;
import jnum.data.Region;
import jnum.math.Coordinate2D;

// TODO: Auto-generated Javadoc
/**
 * The Class SourceCatalog.
 *
 * @param <CoordinateType> the generic type
 */
public class SourceCatalog<CoordinateType extends Coordinate2D> extends Vector<GaussianSource<CoordinateType>> {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7728245572373531025L;

	/**
	 * Insert.
	 *
	 * @param image the image
	 */
	public void insert(GridImage2D<CoordinateType> image) {
		for(GaussianSource<CoordinateType> source : this) source.add(image);
	}
	
	/**
	 * Removes the.
	 *
	 * @param image the image
	 */
	public void remove(GridImage2D<CoordinateType> image) {
		for(GaussianSource<CoordinateType> source : this) source.subtract(image);
	}
	
	/**
	 * Read.
	 *
	 * @param fileName the file name
	 * @param map the map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void read(String fileName, GridImage2D<CoordinateType> map) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
		String line = null;
		while((line = in.readLine()) != null) if(line.length() > 0) if(line.charAt(0) != '#') {
			try { add(new GaussianSource<CoordinateType>(line, Region.FORMAT_CRUSH, map)); }
			catch(ParseException e) { System.err.println("WARNING! Cannot parse: " + line); }
		}
		in.close();
		System.err.println(" Source catalog loaded: " + size() + " source(s).");
	}
}
