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
// Copyright (c) 2007 Attila Kovacs 

package jnum.plot.colorscheme;

import java.awt.Color;

import jnum.plot.ColorScheme;



public class Colorful extends ColorScheme {

	/* (non-Javadoc)
	 * @see jnum.plot.ColorScheme#getRGB(double)
	 */
	@Override
	public int getRGB(double scaled) {
		if(Double.isNaN(scaled)) return noData;

		if(scaled < 0.0) scaled=0.0;
		else if(scaled > 1.0) scaled=1.0;
		
		if(scaled < 0.2) return Color.HSBtoRGB(0.8F, 1.0F, 5.0F * (float) scaled);
		else if(scaled >= 0.8) return Color.HSBtoRGB(0.0F, 5.0F - 5.0F * (float) scaled, 1.0F);
		else return Color.HSBtoRGB(4.0F/3.0F*(0.8F - (float)scaled), 1.0F, 1.0F);		
	}

	/* (non-Javadoc)
	 * @see jnum.plot.ColorScheme#getHighlight()
	 */
	@Override
	public Color getHighlight() {
		return Color.WHITE;
	}
}
