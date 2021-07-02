/* *****************************************************************************
 * Copyright (c) 2021 Attila Kovacs <attila[AT]sigmyne.com>.
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


package jnum.data;

import java.io.IOException;

import jnum.io.LineParser;
import jnum.text.SmartTokenizer;

/**
 * A simple 1D interpolator class that can read 2-column (comma or space-separated) ASCII table
 * to specity x,y type set of measurements to interpolate from. 
 * 
 * @author Attila Kovacs
 *
 */
public class SimpleInterpolator extends Interpolator {

	private static final long serialVersionUID = -1849872877231563147L;

	/**
	 * Construct a new simple 1D interpolator. You can then call {@link #readData(String)}
	 * to load some data into the interpolator. Multiple calls to {@link #readData(String)}
	 * will keep adding data into the interpolator without discarding data already loaded,
	 * so you can build up an interpolator dataset from multiple files if need be.
	 * 
	 */
	public SimpleInterpolator() {}
	
	/**
     * Construct a new simple 1D interpolator with data loaded from the specified file. 
     * You can then call {@link #readData(String)} again to add more data into the 
     * interpolator without discarding data already loaded,
     * so you can build up an interpolator dataset from additional files if need be.
     * 
     */
	public SimpleInterpolator(String fileName) throws IOException {
		super(fileName);
	}


	@Override
	public void readData(String fileName) throws IOException {
		new LineParser() {
            @Override
            protected boolean parse(String line) throws Exception {
                SmartTokenizer tokens = new SmartTokenizer(line, " \t\r,;:");
                Interpolator.Data point = new Interpolator.Data();
                point.ordinate = tokens.nextDouble();
                point.value = tokens.nextDouble();
                add(point);
                return true;
            }
		}.read(fileName);

	}

}
