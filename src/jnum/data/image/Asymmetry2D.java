/* *****************************************************************************
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
 *     Attila Kovacs  - initial API and implementation
 ******************************************************************************/

package jnum.data.image;

import java.io.Serializable;

import jnum.Unit;
import jnum.Util;
import jnum.data.DataPoint;


public class Asymmetry2D implements Serializable {

	private static final long serialVersionUID = -62094580369071840L;

	DataPoint x, y;
	
	@Override
	public int hashCode() { return super.hashCode() ^ x.hashCode() ^ y.hashCode(); }

	@Override
	public boolean equals(Object o) {
	    if(o == this) return true;
		if(!(o instanceof Asymmetry2D)) return false;
		
		Asymmetry2D asym = (Asymmetry2D) o;
		if(!Util.equals(x, asym.x)) return false;
		if(!Util.equals(y, asym.y)) return false;
		return true;
	}
	

	public DataPoint getX() { return x; }
	

	public DataPoint getY() { return y; }
	
	
	public void setX(DataPoint value) { this.x = value; }
	

	public void setY(DataPoint value) { this.y = value; }
	

	@Override
	public String toString() {
		if(x == null && y == null) return "Asymmetry: empty";
		
		return "  Asymmetry: " 
				+ (x == null ? "" : "x = " + x.toString(Unit.get("%")) + (y == null ? "" : ", ")) 
				+ (y == null ? "" : "y = " + y.toString(Unit.get("%")));	
	}
}
