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
// Copyright (c) 2010 Attila Kovacs 

package jnum.data;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

// TODO: Auto-generated Javadoc
/**
 * The Class SimpleInterpolator.
 */
public class SimpleInterpolator extends Interpolator {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1849872877231563147L;

	/**
	 * Instantiates a new simple interpolator.
	 *
	 * @param fileName the file name
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public SimpleInterpolator(String fileName) throws IOException {
		super(fileName);
	}

	/* (non-Javadoc)
	 * @see kovacs.util.data.Interpolator#readData(java.lang.String)
	 */
	@Override
	public void readData(String fileName) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
		String line = null;
		
		while((line = in.readLine()) != null) if(line.length() > 0) if(line.charAt(0) != '#') {
			try {
				StringTokenizer tokens = new StringTokenizer(line);
				Interpolator.Data point = new Interpolator.Data();
				point.ordinate = Double.parseDouble(tokens.nextToken());
				point.value = Double.parseDouble(tokens.nextToken());
				add(point);
			}
			catch(Exception e) {}			
		}
		
		in.close();
	}

}
