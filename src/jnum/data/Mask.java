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
package jnum.data;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.StringTokenizer;
import java.util.Vector;

import jnum.Util;
import jnum.math.Coordinate2D;




// TODO: Auto-generated Javadoc
/**
 * The Class Mask.
 *
 * @param <CoordinateType> the generic type
 */
public class Mask<CoordinateType extends Coordinate2D> extends Vector<Region<CoordinateType>> {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2991565823082882993L;
	
	/**
	 * Read.
	 *
	 * @param fileName the file name
	 * @param format the format
	 * @param image the image
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void read(String fileName, int format, GridImage2D<CoordinateType> image) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
		String line = null;
		int startSize = size();
		
		while((line = in.readLine()) != null) if(line.length() > 0) if(line.charAt(0) != '#') {
			StringTokenizer tokens = new StringTokenizer(line);
			String first = tokens.nextToken();
			Region<CoordinateType> r;
			String spec = null;
			
			if(first.equals("begin")) {
				r = regionFor(tokens.nextToken());
				StringBuffer buf = new StringBuffer();
				boolean isComplete = false;
				
				while(!isComplete && (line = in.readLine()) != null) if(line.length() > 0) if(line.charAt(0) != '#') {
					if(line.startsWith("end")) isComplete = true;
					else buf.append(line + "\n");
				}
				
				spec = new String(line);
			}
			else { 
				r = new CircularRegion<CoordinateType>();
				spec = line;
			}
				
			try {
				r.parse(spec, format, image);
				add(r);
			}
			catch(ParseException e) {
				Util.warning(this, "Parse error for:\n" + spec);
			}
			
		}
		
		in.close();
		
		Util.info(this, "Parsed " + (size() - startSize) + " regions.");
	}
	
	/**
	 * Region for.
	 *
	 * @param id the id
	 * @return the region
	 */
	public Region<CoordinateType> regionFor(String id) {
		id = id.toLowerCase();
		if(id.equals("circle")) return new CircularRegion<CoordinateType>();
		else return null;
	}
	
}
