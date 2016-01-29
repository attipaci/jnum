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
package jnum.plot.colorscheme;

import java.awt.Color;

import jnum.plot.ColorScheme;



// TODO: Auto-generated Javadoc
/**
 * The Class Doppler.
 */
public class Doppler extends ColorScheme {

	/* (non-Javadoc)
	 * @see kovacs.util.plot.ColorScheme#getRGB(double)
	 */
	@Override
	public int getRGB(double scaledI) {
		if(Double.isNaN(scaledI)) return Color.DARK_GRAY.getRGB();
		float I = (float) scaledI;
		if(I > 1.0F) I = 1.0F;
		if(I < 0.0F) I = 0.0F;
		
		float r, g, b;

		if(I <= 0.15) {
			b = 0.5F + I / 0.30F;
			r = g = 0.0F;
		}
		else if(I <= 0.5) {
			b = 1.0F;
			r = g = (I - 0.15F) / 0.35F;
		}
		else if(I <= 0.85) {
			r = 1.0F;
			b = g = (0.85F - I) / 0.35F;
		}
		else {
			r = 0.5F + (1.0F - I) / 0.30F;
			b = g = 0.0F;
		}

		return ColorScheme.getRGB(r, g, b);	
	}

	/* (non-Javadoc)
	 * @see kovacs.util.plot.ColorScheme#getHighlight()
	 */
	@Override
	public Color getHighlight() {
		return Color.BLACK;
	}
}
