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
package test;

import jnum.Unit;
import jnum.data.image.Data2D;
import jnum.data.image.GridMap2D;
import jnum.math.Coordinate2D;


// TODO: Auto-generated Javadoc
/**
 * The Class RegridTest.
 */
public class RegridTest {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		try {
			GridMap2D<?> map = new GridMap2D<Coordinate2D>();
			map.read("/home/pumukli/data/sharc2/images/VESTA.8293.fits");
			
			map.setVerbose(true);
			map.setInterpolationType(Data2D.BICUBIC_SPLINE);
			map.regrid(1.0 * Unit.arcsec);
			//map.clean();
			map.write("test.fits");
			
		}
		catch(Exception e) {
			e.printStackTrace();			
		}
	}
	
	
}
