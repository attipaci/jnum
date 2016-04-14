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

package jnum.math;

import java.io.Serializable;

import jnum.Constant;
import jnum.Copiable;
import jnum.util.HashCode;

public class Angle implements Cloneable, Serializable, Copiable<Angle>, Additive<Angle>, Inversion, Metric<Angle>,
Comparable<Angle> {
    /**
     * 
     */
    private static final long serialVersionUID = -3107020652545972613L;
    private double value, c, s;
    
    public Angle() {}
    
    public Angle(double value) {
        this();
        set(value);
    }
    
    public Angle(Vector2D v) {
        value = v.angle();
        double l = v.length();
        c = v.x() / l;
        s = v.y() / l;
    }
    
    @Override
    public Object clone() {
        try { return super.clone(); }
        catch(CloneNotSupportedException e) { return null; }
    }
    
    @Override
    public Angle copy() {
        return (Angle) clone();
    }
    
    @Override
    public int hashCode() { return HashCode.from(value); }
    
    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Angle)) return false;
        return value == ((Angle) o).value;
    }
    
    public void copy(Angle a) {
        value = a.value;
        c = a.c;
        s = a.s;
    }
    
    public void set(double value) {
        this.value = value;
        c = Math.cos(value);
        s = Math.sin(value);
    }
    
    private final void setCosSin(final double c, final double s) {
        this.c = c;
        this.s = s;
    }
    
    public final void canonize() {
        value = Math.IEEEremainder(value, Constant.twoPi);
    }
    
    public final double value() { return value; }
    
    public final double cos() { return c; }
    
    public final double sin() { return s; }
    
    public final double tan() { return s/c; }
    
    public Complex toComplex(double length) {
        return new Complex(c * length, s * length);
    }
    
    public void add(double theta) {
        set(value() + theta);
    }
 
    @Override
    public void add(Angle theta) {
        value += theta.value();
        setCosSin(c * theta.c - s * theta.s, s * theta.c + c * theta.s);
    }

    @Override
    public void subtract(Angle theta) {
        value -= theta.value();
        setCosSin(c * theta.c + s * theta.s, s * theta.c - c * theta.s);
    }
    
    public void subtract(double theta) {
        set(value() - theta);
    }
    
    @Override
    public void invert() { 
        value *= -1.0;
        s *= -1.0;
    }

    @Override
    public void setSum(Angle a, Angle b) {
        copy(a);
        add(b);
    }

    @Override
    public void setDifference(Angle a, Angle b) {
        copy(a);
        subtract(b);
    }

    @Override
    public int compareTo(Angle a) {
        return Double.compare(value, a.value);
    }

    @Override
    public double distanceTo(Angle other) {
        return Math.abs(Math.IEEEremainder(value - other.value, Constant.twoPi));
    }

}
 