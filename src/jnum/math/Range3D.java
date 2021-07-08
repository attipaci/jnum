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

package jnum.math;

import java.io.Serializable;
import java.text.NumberFormat;

import jnum.Copiable;
import jnum.Util;
import jnum.text.NumberFormating;

/**
 * A range of points in 3D space, with inclusive minimum and
 * and exclusive maximum corners of a bounding box.
 * 
 * @author Attila Kovacs
 *
 */
public class Range3D implements Cloneable, Copiable<Range3D>, Serializable, NumberFormating {
    /**
     * 
     */
    private static final long serialVersionUID = -4101853016412600821L;
    
    private Range xRange, yRange, zRange;

    public Range3D() {
        xRange = new Range();
        yRange = new Range();
        zRange = new Range();
    }
    

    public Range3D(double x0, double y0, double z0) {
        xRange = new Range(x0);
        yRange = new Range(y0); 
        zRange = new Range(z0);
    }
    

    public Range3D(double xmin, double ymin, double zmin, double xmax, double ymax, double zmax) {
        setRange(xmin, ymin, zmin, xmax, ymax, zmax);
    }
    

    public Range3D(Range xRange, Range yRange, Range zRange) {
        setRange(xRange, yRange, zRange);
    }
    

    public Range3D(Coordinate3D coord) {
       this(coord.x(), coord.y(), coord.z());
    }

    @Override
    public Range3D clone() {
        try { return (Range3D) super.clone(); }
        catch(CloneNotSupportedException e) { return null; }
    }

    @Override
    public Range3D copy() {
        Range3D copy = clone();
        if(xRange != null) copy.xRange = xRange.copy();
        if(yRange != null) copy.yRange = yRange.copy();
        if(zRange != null) copy.zRange = zRange.copy();
        return copy;
    }

    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(!(o instanceof Range3D)) return false;
     
        Range3D r = (Range3D) o;
        if(r.isEmpty() && isEmpty()) return true;
        if(!Util.equals(xRange, r.xRange)) return false;
        if(!Util.equals(yRange, r.yRange)) return false;
        if(!Util.equals(zRange, r.zRange)) return false;
        return true;
    }
    

    @Override
    public int hashCode() {
        return super.hashCode() ^ xRange.hashCode() ^ yRange.hashCode() ^ zRange.hashCode();
    }
    

    public void empty() {
        xRange.empty();
        yRange.empty();
        zRange.empty();
    }
    

    public boolean isEmpty() { return xRange.isEmpty() || yRange.isEmpty() || zRange.isEmpty(); }
    

    public void full() {
        xRange.full();
        yRange.full();
        zRange.full();
    }
    

    public final void setRange(Range xRange, Range yRange, Range zRange) {
        setRange(xRange.min(), yRange.min(), zRange.min(), xRange.max(), yRange.max(), zRange.max());
    }
    

    public void setRange(double xmin, double ymin, double zmin, double xmax, double ymax, double zmax) {
        xRange.setRange(xmin, xmax);
        yRange.setRange(ymin, ymax);
        zRange.setRange(zmin, zmax);
    }
    

    public final void grow(double factor) {
        grow(factor, factor, factor);
    }
    

    public void grow(double xFactor, double yFactor, double zFactor) {
        xRange.grow(xFactor);
        yRange.grow(yFactor);
        zRange.grow(zFactor);
    }  
    

    public final Range getXRange() { return xRange; }
    
    public final Range getYRange() { return yRange; }
    
    public final Range getZRange() { return zRange; }
    

    public boolean contains(double x, double y, double z) {
        return xRange.contains(x) && yRange.contains(y) && zRange.contains(z);
    }
    

    public final boolean contains(Coordinate3D coord) {
        return contains(coord.x(), coord.y(), coord.z());
    }
   
    public void include(double x, double y, double z) {
        xRange.include(x);
        yRange.include(y);
        zRange.include(z);
    }
    

    public final void include(Coordinate3D coord) {
        include(coord.x(), coord.y(), coord.z());
    }
    

    public final void include(Range3D r) {
        if(r.isEmpty()) return;
        xRange.include(r.xRange);
        yRange.include(r.yRange);
        zRange.include(r.zRange);
    }
   

    public void intersectWith(Range3D r) {
        xRange.intersectWith(r.xRange);
        yRange.intersectWith(r.yRange);
        zRange.intersectWith(r.zRange);
    }
   
 
    public void intersectWith(double xmin, double ymin, double zmin, double xmax, double ymax, double zmax) {
        xRange.intersectWith(xmin, xmax);
        yRange.intersectWith(ymin, ymax);
        zRange.intersectWith(zmin, zmax);
    }
    

    public boolean isIntersecting(Range3D r) {
        if(r.isEmpty()) return false;
        if(isEmpty()) return false;
        return xRange.isIntersecting(r.xRange) && yRange.isIntersecting(r.yRange) && zRange.isIntersecting(r.zRange);
    }
    

    public void invert() {
        xRange.flip();
        yRange.flip();
        zRange.flip();
    }
    

    public boolean isBounded() { return xRange.isBounded() && yRange.isBounded() && zRange.isBounded(); }
    
  

    @Override
    public String toString() { return xRange + ", " + yRange + ", " + zRange; }
    

    @Override
    public String toString(NumberFormat nf) {
        return xRange.toString(nf) + ", " + yRange.toString(nf) + ", " + zRange.toString(nf);
    }
    

    public static Range3D intersectionOf(Range3D a, Range3D b) {
        Range3D i = a.copy();
        i.intersectWith(b);
        return i;
    }
    

    public static Range3D compositeOf(Range3D a, Range3D b) {
        Range3D c = a.copy();
        c.include(b);
        return c;
    }
 
}
