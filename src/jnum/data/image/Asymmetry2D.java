/*******************************************************************************
 * Copyright (c) 2017 Attila Kovacs <attila[AT]sigmyne.com>.
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

package jnum.data.image;

import java.io.Serializable;

import jnum.Unit;
import jnum.Util;
import jnum.data.DataPoint;


// TODO: Auto-generated Javadoc
/**
 * The Class Asymmetry2D.
 */
public class Asymmetry2D implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -62094580369071840L;
	
	/** The y. */
	DataPoint x, y;
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() { return super.hashCode() ^ x.hashCode() ^ y.hashCode(); }

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
	    if(o == this) return true;
		if(!(o instanceof Asymmetry2D)) return false;
		
		Asymmetry2D asym = (Asymmetry2D) o;
		if(!Util.equals(x, asym.x)) return false;
		if(!Util.equals(y, asym.y)) return false;
		return true;
	}
	
	/**
	 * Gets the x.
	 *
	 * @return the x
	 */
	public DataPoint getX() { return x; }
	
	/**
	 * Gets the y.
	 *
	 * @return the y
	 */
	public DataPoint getY() { return y; }
	
	/**
	 * Sets the x.
	 *
	 * @param value the new x
	 */
	public void setX(DataPoint value) { this.x = value; }
	
	/**
	 * Sets the y.
	 *
	 * @param value the new y
	 */
	public void setY(DataPoint value) { this.y = value; }
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if(x == null && y == null) return "Asymmetry: empty";
		
		return "  Asymmetry: " 
				+ (x == null ? "" : "x = " + x.toString(Unit.get("%")) + (y == null ? "" : ", ")) 
				+ (y == null ? "" : "y = " + y.toString(Unit.get("%")));	
	}
}
