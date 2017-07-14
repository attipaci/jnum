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

package jnum.math;

import java.io.Serializable;

import jnum.Copiable;
import jnum.CopyCat;
import jnum.ViewableAsDoubles;
import jnum.text.NumberFormating;
import jnum.text.Parser;


public class Coordinate3D implements Serializable, Cloneable, Copiable<Coordinate3D>, CopyCat<Coordinate3D>, 
ViewableAsDoubles, Parser, NumberFormating {

	private static final long serialVersionUID = 4670218761839380720L;

	private double x, y, z;

	public Coordinate3D() {}
	
	public Coordinate3D(double x, double y, double z) {
	    this();
	    set(x, y, z);
	}

	@Override
    public Coordinate3D clone() {
	    try { return (Coordinate3D) super.clone(); }
	    catch(CloneNotSupportedException e) { return null; }
	}
	
	@Override
    public Coordinate3D copy() {
	    return clone();
	}
	
	@Override
    public void copy(Coordinate3D other) {
	    set(other.x(), other.y(), other.z());
	}
	
	public void zero() {
	    set(0.0, 0.0, 0.0);
	}
	
	public boolean isNull() {
	    if(x != 0.0) return false;
	    if(y != 0.0) return false;
	    if(z != 0.0) return false;
	    return true;
	}
	
	public final double x() { return x; }

	public final double y() { return y; }

	public final double z() { return z; }
	
	public final void set(final double x, final double y, final double z) {
	    this.x = x;
	    this.y = y;
	    this.z = z;
	}

	public final void setX(final double value) { this.x = value; }

	public final void setY(final double value) { this.y = value; }

	public final void setZ(final double value) { this.z = value; }
	
	  
    @Override
    public double[] viewAsDoubles() {
        return new double[] { x, y, z };
    }
    
    @Override
    public void createFromDoubles(Object array) {
        createFromDoubles((double[]) array);
    }
    
    public void createFromDoubles(double[] values) {
        set(values[0], values[1], values[2]);
    }
 
	
}
