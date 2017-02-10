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

package jnum.math;

import java.io.Serializable;

import jnum.Constant;
import jnum.Copiable;
import jnum.util.HashCode;

// TODO: Auto-generated Javadoc
/**
 * The Class Angle.
 */
public class Angle implements Cloneable, Serializable, Copiable<Angle>, Additive<Angle>, Inversion, Metric<Angle>,
Comparable<Angle> {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -3107020652545972613L;
    
    /** The s. */
    private double value, c, s;
    
    /**
     * Instantiates a new angle.
     */
    public Angle() {}
    
    /**
     * Instantiates a new angle.
     *
     * @param value the value
     */
    public Angle(double value) {
        this();
        set(value);
    }
    
    /**
     * Instantiates a new angle.
     *
     * @param v the v
     */
    public Angle(Vector2D v) {
        value = v.angle();
        double l = v.length();
        c = v.x() / l;
        s = v.y() / l;
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
    public Angle copy() {
        return (Angle) clone();
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() { return HashCode.from(value); }
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Angle)) return false;
        return value == ((Angle) o).value;
    }
    
    /**
     * Copy.
     *
     * @param a the a
     */
    public void copy(Angle a) {
        value = a.value;
        c = a.c;
        s = a.s;
    }
    
    /**
     * Sets the.
     *
     * @param value the value
     */
    public void set(double value) {
        this.value = value;
        c = Math.cos(value);
        s = Math.sin(value);
    }
    
    /**
     * Sets the cos sin.
     *
     * @param c the c
     * @param s the s
     */
    private final void setCosSin(final double c, final double s) {
        this.c = c;
        this.s = s;
    }
    
    /**
     * Canonize.
     */
    public final void canonize() {
        value = Math.IEEEremainder(value, Constant.twoPi);
    }
    
    /**
     * Value.
     *
     * @return the double
     */
    public final double value() { return value; }
    
    /**
     * Cos.
     *
     * @return the double
     */
    public final double cos() { return c; }
    
    /**
     * Sin.
     *
     * @return the double
     */
    public final double sin() { return s; }
    
    /**
     * Tan.
     *
     * @return the double
     */
    public final double tan() { return s/c; }
    
    /**
     * To complex.
     *
     * @param length the length
     * @return the complex
     */
    public Complex toComplex(double length) {
        return new Complex(c * length, s * length);
    }
    
    /**
     * Adds the.
     *
     * @param theta the theta
     */
    public void add(double theta) {
        set(value() + theta);
    }
 
    /* (non-Javadoc)
     * @see jnum.math.Additive#add(java.lang.Object)
     */
    @Override
    public void add(Angle theta) {
        value += theta.value();
        setCosSin(c * theta.c - s * theta.s, s * theta.c + c * theta.s);
    }

    /* (non-Javadoc)
     * @see jnum.math.Additive#subtract(java.lang.Object)
     */
    @Override
    public void subtract(Angle theta) {
        value -= theta.value();
        setCosSin(c * theta.c + s * theta.s, s * theta.c - c * theta.s);
    }
    
    /**
     * Subtract.
     *
     * @param theta the theta
     */
    public void subtract(double theta) {
        set(value() - theta);
    }
    
    /* (non-Javadoc)
     * @see jnum.math.Inversion#invert()
     */
    @Override
    public void invert() { 
        value *= -1.0;
        s *= -1.0;
    }

    /* (non-Javadoc)
     * @see jnum.math.Additive#setSum(java.lang.Object, java.lang.Object)
     */
    @Override
    public void setSum(Angle a, Angle b) {
        copy(a);
        add(b);
    }

    /* (non-Javadoc)
     * @see jnum.math.Additive#setDifference(java.lang.Object, java.lang.Object)
     */
    @Override
    public void setDifference(Angle a, Angle b) {
        copy(a);
        subtract(b);
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Angle a) {
        return Double.compare(value, a.value);
    }

    /* (non-Javadoc)
     * @see jnum.math.Metric#distanceTo(java.lang.Object)
     */
    @Override
    public double distanceTo(Angle other) {
        return Math.abs(Math.IEEEremainder(value - other.value, Constant.twoPi));
    }

}
 