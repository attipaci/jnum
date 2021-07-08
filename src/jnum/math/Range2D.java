/* *****************************************************************************
 * Copyright (c) 2016 Attila Kovacs <attila[AT]sigmyne.com>.
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

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.text.NumberFormat;
import java.util.StringTokenizer;

import jnum.Copiable;
import jnum.Util;
import jnum.text.NumberFormating;

/**
 * A range of points on a 2D plane, with inclusive minimum and
 * and exclusive maximum corners of a bounding rectangle.
 * 
 * @author Attila Kovacs
 *
 */
public class Range2D implements Cloneable, Copiable<Range2D>, Serializable, Scalable, NumberFormating {

    private static final long serialVersionUID = 7648489968457196160L;

    private Range xRange, yRange;
    

    public Range2D() {
        xRange = new Range();
        yRange = new Range();
    }
    

    public Range2D(double x0, double y0) {
        xRange = new Range(x0);
        yRange = new Range(y0); 
    }
    

    public Range2D(double xmin, double ymin, double xmax, double ymax) {
        setRange(xmin, ymin, xmax, ymax);
    }
    

    public Range2D(Range xRange, Range yRange) {
        setRange(xRange, yRange);
    }
    

    public Range2D(Coordinate2D coord) {
       this(coord.x(), coord.y());
    }

    @Override
    public Range2D clone() {
        try { return  (Range2D) super.clone(); }
        catch(CloneNotSupportedException e) { return null; }
    }

    @Override
    public Range2D copy() {
        Range2D copy = clone();
        if(xRange != null) copy.xRange = xRange.copy();
        if(yRange != null) copy.yRange = yRange.copy();
        return copy;
    }

    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(!(o instanceof Range2D)) return false;
     
        Range2D r = (Range2D) o;
        if(r.isEmpty() && isEmpty()) return true;
        if(!Util.equals(xRange, r.xRange)) return false;
        if(!Util.equals(yRange, r.yRange)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ xRange.hashCode() ^ yRange.hashCode();
    }
    

    public void empty() {
        xRange.empty();
        yRange.empty();
    }
    

    public boolean isEmpty() { return xRange.isEmpty() || yRange.isEmpty(); }
    

    public void full() {
        xRange.full();
        yRange.full();
    }
    

    public final void setRange(Range xRange, Range yRange) {
        setRange(xRange.min(), yRange.min(), xRange.max(), yRange.max());
    }
    

    public void setRange(double xmin, double ymin, double xmax, double ymax) {
        xRange.setRange(xmin, xmax);
        yRange.setRange(ymin, ymax);
    }
    
    @Override
    public void scale(double factor) {
        xRange.scale(factor);
        yRange.scale(factor);
    }
    

    public final void grow(double factor) {
        grow(factor, factor);
    }
    

    public void grow(double xFactor, double yFactor) {
        xRange.grow(xFactor);
        yRange.grow(yFactor);
    }  
    

    public final Range getXRange() { return xRange; }
    

    public final Range getYRange() { return yRange; }
    

    public boolean contains(double x, double y) {
        return xRange.contains(x) && yRange.contains(y);
    }

    
    public final boolean contains(Coordinate2D coord) {
        return contains(coord.x(), coord.y());
    }
    

    public final boolean contains(Point2D p) {
        return contains(p.getX(), p.getY());
    }
    

    public void include(double x, double y) {
        xRange.include(x);
        yRange.include(y);
    }
    

    public final void include(Coordinate2D coord) {
        include(coord.x(), coord.y());
    }
    

    public final void include(Point2D p) {
        include(p.getX(), p.getY());
    }
    

    public final void include(Range2D r) {
        if(r.isEmpty()) return;
        xRange.include(r.xRange);
        yRange.include(r.yRange);
    }
    

    public final void include(Rectangle2D r) {
        xRange.include(r.getMinX());
        xRange.include(r.getMaxX());
        yRange.include(r.getMinY());
        yRange.include(r.getMaxY());
    }

    /**
     * Intersect this range with the argument range.
     *
     * @param r the intersecting range.
     */
    public void intersectWith(Range2D r) {
        xRange.intersectWith(r.xRange);
        yRange.intersectWith(r.yRange);
    }
   
    /**
     * Intersect this range with the argument rectangle
     *
     * @param r the intersecting rectangle.
     */
    public void intersectWith(Rectangle2D r) {
        xRange.intersectWith(r.getMinX(), r.getMaxX());
        yRange.intersectWith(r.getMinY(), r.getMaxY());
    }
    

    public void intersectWith(double xmin, double ymin, double xmax, double ymax) {
        xRange.intersectWith(xmin, xmax);
        yRange.intersectWith(ymin, ymax);
    }
    

    public boolean isIntersecting(Range2D r) {
        if(r.isEmpty()) return false;
        if(isEmpty()) return false;
        return xRange.isIntersecting(r.xRange) && yRange.isIntersecting(r.yRange);
    }
    

    public void invert() {
        xRange.flip();
        yRange.flip();
    }
    

    public boolean isBounded() { return xRange.isBounded() && yRange.isBounded(); }
    

    public Rectangle2D getRectange2D() {
        return new Rectangle2D.Double(xRange.min(), yRange.min(), xRange.span(), yRange.span());
    }

    @Override
    public String toString() { return xRange + ", " + yRange; }
    

    @Override
    public String toString(NumberFormat nf) {
        return xRange.toString(nf) + ", " + yRange.toString(nf);
    }
    
    /**
     * Creates a new range that is the intersection of the two ranges in the argument.
     *
     * @param a 
     * @param b 
     * @return the range that is the intersection of a and b.
     */
    public static Range2D intersectionOf(Range2D a, Range2D b) {
        Range2D i = a.copy();
        i.intersectWith(b);
        return i;
    }
    
    /**
     * Creates a new range that is the composite of the two ranges in the argument.
     *
     * @param a
     * @param b
     * @return the smallest range that includes both a and b
     */
    public static Range2D compositeOf(Range2D a, Range2D b) {
        Range2D c = a.copy();
        c.include(b);
        return c;
    }
    
    public void parse(String text) throws NumberFormatException {
        parse(text, false);
    }
    
    public void parse(String text, boolean isPositive) {
        StringTokenizer tokens = new StringTokenizer(text, " \t," + (isPositive ? "-" : ""));
        int n = tokens.countTokens();
        
        if(n == 2) {
            xRange.parse(tokens.nextToken(), isPositive);
            yRange.parse(tokens.nextToken(), isPositive);
        }
        else if(n == 4) {
            xRange.parse(tokens.nextToken() + ":" + tokens.nextToken(), isPositive);
            yRange.parse(tokens.nextToken() + ":" + tokens.nextToken(), isPositive);
        }
    }
    
    public static Range2D from(String text) throws NumberFormatException {
        return from(text, false);
    }
    
    public static Range2D from(String text, boolean isPositive) throws NumberFormatException {
        Range2D range = new Range2D();
        range.parse(text, isPositive);
        return range;
    }
}
