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



// TODO: Auto-generated Javadoc
/**
 * The Class Temperature.
 */
public class Temperature extends ColorScheme {
	
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

		if(I < fifth) {
			// red rise
			r = I / fifth;
			g = b = 0.0F;
		}
		else if(I < 2 * fifth) {
			// green rise
			r = 1.0F;
			g = (I - fifth) / fifth;
			b = 0.0F;			
		}
		else if(I < 3 * fifth) {
			// blue rise
			r = 1.0F;
			g = 1.0F;
			b = (I - 2 * fifth) / fifth;
		}
		else if(I < 4 * fifth) {
			// red fall
			r = 1.0F - (I - 3 * fifth) / fifth;
			g = 1.0F;
			b = 1.0F;
		}
		else {
			// green fall
			r = 0.0F;
			g = 1.0F - (I - 4 * fifth) / fifth;
			b = 1.0F;
		}

		return ColorScheme.getRGB(r, g, b);	
	}
	
	/* (non-Javadoc)
	 * @see jnum.plot.ColorScheme#getHighlight()
	 */
	@Override
	public Color getHighlight() {
		return Color.GREEN;
	}
	
	
	/** The fifth. */
	private static float fifth = 0.2F;

	
}
