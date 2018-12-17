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


public class Rainbow extends ColorScheme {
	
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

		if(I < 0.25) {
			b = I / 0.25F;
			g = r = 0.0F;
		}
		else if(I < 0.5) {
			b = 1.0F;
			g = (I - 0.25F) / 0.25F;
			r = 0.0F;			
		}
		else if(I < 0.75) {
			b = 1.0F - (I - 0.5F) / 0.25F;
			g = 1.0F;
			r = (I - 0.5F) / 0.25F;
		}
		else {
			b = 0.0F;
			g = 1.0F - (I - 0.75F) / 0.25F;
			r = 1.0F;
		}
		
		return ColorScheme.getRGB(r, g, b);	
	}

	/* (non-Javadoc)
	 * @see jnum.plot.ColorScheme#getHighlight()
	 */
	@Override
	public Color getHighlight() {
		return Color.WHITE;
	}
}
