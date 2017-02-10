/*******************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila[AT]sigmyne.com>.
 * All rights reserved. 
 * 
 * This file is part of jnum.
 * 
 *     jnum is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     jnum is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with jnum.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Attila Kovacs <attila[AT]sigmyne.com> - initial API and implementation
 ******************************************************************************/
package jnum.astro;

import java.io.IOException;
import java.util.Vector;

import jnum.Util;
import jnum.data.GaussianSource;
import jnum.data.GridImage2D;
import jnum.data.Region;
import jnum.io.LineParser;
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
	
	public void flag(GridImage2D<CoordinateType> image, int pattern) {
        for(GaussianSource<CoordinateType> source : this) source.flag(image, pattern);
    }
	
	public void unflag(GridImage2D<CoordinateType> image, int pattern) {
        for(GaussianSource<CoordinateType> source : this) source.unflag(image, pattern);
    }
    
	
	/**
	 * Read.
	 *
	 * @param fileName the file name
	 * @param map the map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void read(String fileName, final GridImage2D<CoordinateType> map) throws IOException {
		new LineParser() {
            @Override
            protected boolean parse(String line) throws Exception {
                add(new GaussianSource<CoordinateType>(line, Region.FORMAT_CRUSH, map));
                return true;
            }
		}.read(fileName);
		
		Util.info(this, "Source catalog loaded: " + size() + " source(s).");
	}
}
