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
package jnum.plot;

// TODO: Auto-generated Javadoc
/**
 * The Interface PlotSide.
 */
public interface PlotSide {

	/**
	 * Gets the side.
	 *
	 * @return the side
	 */
	public int getSide();
	
	/**
	 * Sets the side.
	 *
	 * @param side the new side
	 */
	public void setSide(int side);
		
	/**
	 * Checks if is horizontal.
	 *
	 * @return true, if is horizontal
	 */
	public boolean isHorizontal();
	
	/**
	 * Checks if is vertical.
	 *
	 * @return true, if is vertical
	 */
	public boolean isVertical(); 
	
}
