/*******************************************************************************
 * Copyright (c) 2016 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
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

package jnum.math;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.text.NumberFormat;

import jnum.Copiable;
import jnum.Util;

// TODO: Auto-generated Javadoc
/**
 * The Class Range2D.
 */
public class Range2D implements Cloneable, Copiable<Range2D>, Serializable {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 7648489968457196160L;
    
    /** The y range. */
    private Range xRange, yRange;
    
    /**
     * Instantiates a new range 2 D.
     */
    public Range2D() {
        xRange = new Range();
        yRange = new Range();
    }
    
    /**
     * Instantiates a new range 2 D.
     *
     * @param x0 the x 0
     * @param y0 the y 0
     */
    public Range2D(double x0, double y0) {
        xRange = new Range(x0);
        yRange = new Range(y0); 
    }
    
    /**
     * Instantiates a new range 2 D.
     *
     * @param xmin the xmin
     * @param ymin the ymin
     * @param xmax the xmax
     * @param ymax the ymax
     */
    public Range2D(double xmin, double ymin, double xmax, double ymax) {
        setRange(xmin, ymin, xmax, ymax);
    }
    
    /**
     * Instantiates a new range 2 D.
     *
     * @param xRange the x range
     * @param yRange the y range
     */
    public Range2D(Range xRange, Range yRange) {
        setRange(xRange, yRange);
    }
    
    /**
     * Instantiates a new range 2 D.
     *
     * @param coord the coord
     */
    public Range2D(Coordinate2D coord) {
       this(coord.x(), coord.y());
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() {
        try { return super.clone(); }
        catch(CloneNotSupportedException e) { return null; }
    }
    
    /* (non-Javadoc)
     * @see jnum.Copiable#copy()
     */
    @Override
    public Range2D copy() {
        Range2D copy = (Range2D) clone();
        if(xRange != null) copy.xRange = xRange.copy();
        if(yRange != null) copy.yRange = yRange.copy();
        return copy;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(!(o instanceof Range)) return false;
        if(!super.equals(o)) return false;

        Range2D r = (Range2D) o;
        if(r.isEmpty() && isEmpty()) return true;
        if(!Util.equals(xRange, r.xRange)) return false;
        if(!Util.equals(yRange, r.yRange)) return false;
        return true;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return super.hashCode() ^ xRange.hashCode() ^ yRange.hashCode();
    }
    
    /**
     * Empty.
     */
    public void empty() {
        xRange.empty();
        yRange.empty();
    }
    
    /**
     * Checks if is empty.
     *
     * @return true, if is empty
     */
    public boolean isEmpty() { return xRange.isEmpty() || yRange.isEmpty(); }
    
    /**
     * Full.
     */
    public void full() {
        xRange.full();
        yRange.full();
    }
    
    /**
     * Sets the range.
     *
     * @param xRange the x range
     * @param yRange the y range
     */
    public final void setRange(Range xRange, Range yRange) {
        setRange(xRange.min(), yRange.min(), xRange.max(), yRange.max());
    }
    
    /**
     * Sets the range.
     *
     * @param xmin the xmin
     * @param ymin the ymin
     * @param xmax the xmax
     * @param ymax the ymax
     */
    public void setRange(double xmin, double ymin, double xmax, double ymax) {
        xRange.setRange(xmin, xmax);
        yRange.setRange(ymin, ymax);
    }
    
    /**
     * Grow.
     *
     * @param factor the factor
     */
    public final void grow(double factor) {
        grow(factor, factor);
    }
    
    /**
     * Grow.
     *
     * @param xFactor the x factor
     * @param yFactor the y factor
     */
    public void grow(double xFactor, double yFactor) {
        xRange.grow(xFactor);
        yRange.grow(yFactor);
    }  
    
    /**
     * Gets the x range.
     *
     * @return the x range
     */
    public final Range getXRange() { return xRange; }
    
    /**
     * Gets the y range.
     *
     * @return the y range
     */
    public final Range getYRange() { return yRange; }
    
    /**
     * Contains.
     *
     * @param x the x
     * @param y the y
     * @return true, if successful
     */
    public boolean contains(double x, double y) {
        return xRange.contains(x) && yRange.contains(y);
    }
    
    /**
     * Contains.
     *
     * @param coord the coord
     * @return true, if successful
     */
    public final boolean contains(Coordinate2D coord) {
        return contains(coord.x(), coord.y());
    }
    
    /**
     * Contains.
     *
     * @param p the p
     * @return true, if successful
     */
    public final boolean contains(Point2D p) {
        return contains(p.getX(), p.getY());
    }
    
    /**
     * Include.
     *
     * @param x the x
     * @param y the y
     */
    public void include(double x, double y) {
        xRange.include(x);
        yRange.include(y);
    }
    
    /**
     * Include.
     *
     * @param coord the coord
     */
    public final void include(Coordinate2D coord) {
        include(coord.x(), coord.y());
    }
    
    /**
     * Include.
     *
     * @param p the p
     */
    public final void include(Point2D p) {
        include(p.getX(), p.getY());
    }
    
    /**
     * Include.
     *
     * @param r the r
     */
    public final void include(Range2D r) {
        if(r.isEmpty()) return;
        xRange.include(r.xRange);
        yRange.include(r.yRange);
    }
    
    /**
     * Include.
     *
     * @param r the r
     */
    public final void include(Rectangle2D r) {
        xRange.include(r.getMinX());
        xRange.include(r.getMaxX());
        yRange.include(r.getMinY());
        yRange.include(r.getMaxY());
    }

    /**
     * Restrict.
     *
     * @param r the r
     */
    public void restrict(Range2D r) {
        xRange.restrict(r.xRange);
        yRange.restrict(r.yRange);
    }
   
    /**
     * Restrict.
     *
     * @param r the r
     */
    public void restrict(Rectangle2D r) {
        xRange.restrict(r.getMinX(), r.getMaxX());
        yRange.restrict(r.getMinY(), r.getMaxY());
    }
    
    /**
     * Restrict.
     *
     * @param xmin the xmin
     * @param ymin the ymin
     * @param xmax the xmax
     * @param ymax the ymax
     */
    public void restrict(double xmin, double ymin, double xmax, double ymax) {
        xRange.restrict(xmin, xmax);
        yRange.restrict(ymin, ymax);
    }
    
    /**
     * Intersects.
     *
     * @param r the r
     * @return true, if successful
     */
    public boolean intersects(Range2D r) {
        if(r.isEmpty()) return false;
        if(isEmpty()) return false;
        return xRange.intersects(r.xRange) && yRange.intersects(r.yRange);
    }
    
    /**
     * Invert.
     */
    public void invert() {
        xRange.flip();
        yRange.flip();
    }
    
    /**
     * Checks if is bounded.
     *
     * @return true, if is bounded
     */
    public boolean isBounded() { return xRange.isBounded() && yRange.isBounded(); }
    
    /**
     * Gets the rectange 2 D.
     *
     * @return the rectange 2 D
     */
    public Rectangle2D getRectange2D() {
        return new Rectangle2D.Double(xRange.min(), yRange.min(), xRange.span(), yRange.span());
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() { return "x:" + xRange.toString() + ", y:" + yRange.toString(); }
    
    /**
     * To string.
     *
     * @param nf the nf
     * @return the string
     */
    public String toString(NumberFormat nf) {
        return "x:" + xRange.toString(nf) + ", y:" + yRange.toString(nf);
    }
 
}
