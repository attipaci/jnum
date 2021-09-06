/* *****************************************************************************
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
 *     Attila Kovacs  - initial API and implementation
 ******************************************************************************/

package jnum.math;

import java.io.Serializable;

import jnum.Constant;
import jnum.Copiable;
import jnum.CopyCat;
import jnum.Util;
import jnum.util.HashCode;


/**
 * An angle, with its sine and cosine readily calculate for frequent use.
 * 
 * @author Attila Kovacs
 *
 */
public class Angle implements Cloneable, Serializable, Copiable<Angle>, CopyCat<Angle>, Additive<Angle>, Inversion, Metric<Angle>,
Comparable<Angle> {
    /** */
    private static final long serialVersionUID = -3107020652545972613L;

    private double value, c, s;
    
    /**
     * Constructs a new angle initialized to 0.
     * 
     */
    public Angle() {
        c = 1.0;
    }
    

    /**
     * Constructs a new angle with the specified value in radians.
     * 
     * @param value     (rad) The numerical value of this angle
     */
    public Angle(double value) {
        set(value);
    }
   

    /**
     * Constructs a new angle based on the position angle of 2D vector.
     * 
     * @param v     The vector whose angle defines this angle.
     */
    public Angle(Vector2D v) {
        value = v.angle();
        double l = v.length();
        c = v.x() / l;
        s = v.y() / l;
    }
    

    @Override
    public Angle clone() {
        try { return (Angle) super.clone(); }
        catch(CloneNotSupportedException e) { return null; }
    }
    
    @Override
    public Angle copy() {
        return clone();
    }
    

    @Override
    public int hashCode() { return HashCode.from(value); }
    

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof Angle)) return false;
        return Util.equals(value, ((Angle) o).value);
    }
    

    @Override
    public void copy(Angle a) {
        value = a.value;
        c = a.c;
        s = a.s;
    }
    
    /**
     * Sets a new angle, and recalculates the sine and cosine terms for it
     * 
     * @param value    (rad) The new angle.
     */
    public void set(double value) {
        this.value = value;
        c = Math.cos(value);
        s = Math.sin(value);
    }
    
    private final void setCosSin(final double c, final double s) {
        this.c = c;
        this.s = s;
    }

    /**
     * Canonizes the angle to [-Pi:Pi] range.
     * 
     */
    public final void canonize() {
        value = Math.IEEEremainder(value, Constant.twoPi);
    }
    
    /**
     * Gets the numerical value of this angle.
     * 
     * @return      (rad) The angle represented by this object
     */
    public final double value() { return value; }
    
    /**
     * Gets the precalculated cosine of this angle.
     * 
     * @return  The cosine of this angle.
     */
    public final double cos() { return c; }
    

    /**
     * Gets the precalculated sine of this angle.
     * 
     * @return  The sine of this angle.
     */
    public final double sin() { return s; }
    

    /**
     * Gets the tangent of this angle from it's precacluated sine and cosine.
     * 
     * @return  The tangent of this angle.
     */
    public final double tan() { return s/c; }
    
    /**
     * Gets a representation of this angle as a complex value, with the specified
     * absolute value L, i.e. z = L * exp(i*angle).
     * 
     * @param length    The absolute value of the complex representation.
     * @return          The complex value equal to z = L * exp(i * angle).
     */
    public Complex toComplex(double length) {
        return new Complex(c * length, s * length);
    }
    
    /**
     * Increments this angle by the specified amount.
     * 
     * @param theta     (rad) The angle to add to this one.
     */
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
    
    /**
     * Deccrements this angle by the specified amount.
     * 
     * @param theta     (rad) The angle to add to this one.
     */
    public void subtract(double theta) {
        set(value() - theta);
    }
    

    @Override
    public void flip() { 
        value = -value;
        s = -s;
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
 