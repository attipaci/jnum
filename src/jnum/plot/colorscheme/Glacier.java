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
package jnum.plot.colorscheme;

import java.awt.Color;

import jnum.plot.ColorScheme;



public class Glacier extends ColorScheme {
	
	/* (non-Javadoc)
	 * @see jnum.plot.ColorScheme#getRGB(double)
	 */
	@Override
	public int getRGB(double scaledI) {
		if(Double.isNaN(scaledI)) return Color.DARK_GRAY.getRGB();
		float I = (float) scaledI;
		if(I > 1.0F) I = 1.0F;
		if(I < 0.0F) I = 0.0F;
		
		float r, g, b;

		b = I;
		g = I > third ? (I - third) / twothirds : 0.0F;
		r = I > twothirds ? (I - twothirds) / third : 0.0F;
		
		return ColorScheme.getRGB(r, g, b);	
	}
	
	/* (non-Javadoc)
	 * @see jnum.plot.ColorScheme#getHighlight()
	 */
	@Override
	public Color getHighlight() {
		return Color.RED;
	}
	
	private static float third = 1.0F / 3.0F;
	
	private static float twothirds = 2.0F / 3.0F;
	
}
