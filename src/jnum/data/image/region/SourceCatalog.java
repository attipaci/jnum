/* *****************************************************************************
 * Copyright (c) 2017 Attila Kovacs <attila[AT]sigmyne.com>.
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
 *     Attila Kovacs  - initial API and implementation
 ******************************************************************************/

package jnum.data.image.region;

import java.io.IOException;
import java.util.Vector;

import jnum.Util;
import jnum.data.image.Map2D;
import jnum.io.LineParser;
import jnum.math.Coordinate2D;


public class SourceCatalog extends Vector<GaussianSource> {
	
	private static final long serialVersionUID = 7728245572373531025L;

	public Class<? extends Coordinate2D> coordinateClass;
	
	public SourceCatalog(Class<? extends Coordinate2D> coordinateClass) {
	    this.coordinateClass = coordinateClass;
	}
	
	
	public void insert(Map2D image) {
		for(GaussianSource source : this) 
		    source.getRepresentation(image.getGrid()).add(image);
	}
	

	public void remove(Map2D image) {
		for(GaussianSource source : this) 
		    source.getRepresentation(image.getGrid()).add(image);
	}
	
	public void flag(Map2D image, long pattern) {
        for(GaussianSource source : this) 
            source.getRepresentation(image.getGrid()).flag(image.getFlags(), pattern);
    }
	
	public void unflag(Map2D image, long pattern) {
        for(GaussianSource source : this) 
            source.getRepresentation(image.getGrid()).unflag(image.getFlags(), pattern);
    }
    
	
	public void read(String fileName) throws IOException {
		new LineParser() {
            @Override
            protected boolean parse(String line) throws Exception {
                add(new GaussianSource(coordinateClass, line, Region2D.FORMAT_CRUSH));
                return true;
            }
		}.read(fileName);
		
		Util.info(this, "Source catalog loaded: " + size() + " source(s).");
	}
}
