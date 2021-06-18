/*******************************************************************************
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
 *     Attila Kovacs <attila[AT]sigmyne.com> - initial API and implementation
 ******************************************************************************/
package jnum.plot.colorscheme;

import java.awt.Color;

import jnum.plot.ColorScheme;



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

		if(I < section) {
			// red rise
			r = I / section;
			g = b = 0.0F;
		}
		else if(I < 2 * section) {
			// green rise
			r = 1.0F;
			g = (I - section) / section;
			b = 0.0F;			
		}
		else if(I < 3 * section) {
			// blue rise
			r = 1.0F;
			g = 1.0F;
			b = (I - 2 * section) / section;
		}
		else if(I < 4 * section) {
			// red fall
			r = 1.0F - (I - 3 * section) / section;
			g = 1.0F;
			b = 1.0F;
		}
		else if(I < 5 * section) {
			// green fall
			r = 0.0F;
			g = 1.0F - (I - 4 * section) / section;
			b = 1.0F;
		}
		else {
		 // blue fall
		 r = 0.0F;
		 g = 0.0F;
		 b = 1.0F - (I - 5 * section) / section;
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
	
	private static float section = 1.0F / 6.0F;

	
}
