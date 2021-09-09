/* *****************************************************************************
 * Copyright (c) 2021 Attila Kovacs <attila[AT]sigmyne.com>.
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

package jnum.astro;

import java.io.Serializable;

import jnum.Copiable;
import jnum.CopyCat;
import jnum.ExtraMath;
import jnum.Util;
import jnum.math.Angle;
import jnum.math.Inversion;
import jnum.math.LinearAlgebra;
import jnum.util.HashCode;

/** 
 * <p>
 * Stokes polarization parameters. There are 4 standard Stokes parameters, which fully characterize the
 * polarization of light. These are the total intensity (<i>I</i>), the vertically polarized (<i>Q</i>), 
 * the horizontally polarized (<i>U</i>), and the circularly polarized (<i>V</i>) intensities. The total 
 * intensity <i>I</i> is the sum of the polarized intensity <i>I<sub>p</sub></i> (denoted here simply as <i>P</i>) 
 * and the non-polarized intensity <i>I<sub>np</sub></i> (denoted here simply as <i>N</i>).
 * I.e.:
 * </p>
 * 
 * <p>
 * <i>I</i> = <i>I<sub>p</sub></i> + <i>I<sub>np</sub></i> 
 * </p>
 * 
 * <p>
 * or
 * </p>
 * 
 * <p>
 * <i>I</i> = <i>P</i> + <i>N</i> 
 * </p>
 * 
 * <p>
 * in the notation used here. 
 * </p>
 * 
 * <p>
 * The total polarized intensity <i>P</i> can be calculated from the polarized components as:
 * </p>
 * 
 * <p>
 * <i>P</i><sup>2</sup> = <i>Q</i><sup>2</sup> + <i>U</i><sup>2</sup> + <i>B</i><sup>2</sup>
 * </p>
 * 
 * @author Attila Kovacs
 *
 */
public class Stokes implements LinearAlgebra<Stokes>, Cloneable, Copiable<Stokes>, CopyCat<Stokes>, Inversion, Serializable {
    /** */
    private static final long serialVersionUID = 3607597866105508377L;

    private double N, Q, U, V;
    
    @Override
    public int hashCode() { return super.hashCode() ^ HashCode.from(N) ^ HashCode.from(Q) ^ HashCode.from(U) ^ HashCode.from(V); } 
    
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null) return false;
        if(!(o instanceof Stokes)) return false;
        
        return equals((Stokes) o);
    }
    
    /**
     * Checks equality to another set of Stokes parametes. 
     * 
     * @param s     Another set of Stokes parameters
     * @return      <code>true</code> If these Stokes parameters are an exact match to the other Stokes parameters, otherwise
     *              <code>false</code>
     */
    public boolean equals(Stokes s) {  
        if(!Util.equals(N, s.N)) return false;
        if(!Util.equals(Q, s.Q)) return false;
        if(!Util.equals(U, s.U)) return false;
        if(!Util.equals(V, s.V)) return false;
        return true;    
    }
    
    /**
     * Returns the non-polarized intensity <i>N</i> = <i>I</i> - <i>P</i> (or <i>I<sub>np</sub></i> = <i>I</i> - <i>I<sub>p</sub></i> in the more conventional notation).
     * 
     * @return      the total non-polarized intensity.
     * 
     * @see #P()
     * @see #I()
     * @see #setIQUV(double, double, double, double)
     */
    public final double N() { return N; }
    
    /**
     * Returns the vertically polarized intensity component.
     * 
     * @return  the vertically polarized intensity.
     * 
     * @see #U()
     * @see #P()
     * @see #setIQUV(double, double, double, double)
     */
    public final double Q() { return Q; }
    
    /**
     * Returns the horizontally polarized intensity component.
     * 
     * @return  the horizontally polarized intensity.
     * 
     * @see #Q()
     * @see #P()
     * @see #setIQUV(double, double, double, double)
     */
    public final double U() { return U; }
    
    /**
     * Returns the circularly polarized intensity component.
     * 
     * @return  the circularly polarized intensity.
     * 
     * @see #P()
     * @see #setIQUV(double, double, double, double)
     */
    public final double V() { return V; }

    /**
     * Returns the total intensity <i>I</i> = <i>P</i> + <i>N</i> (or <i>I</i> = <i>I<sub>p</sub></i> + <i>I<sub>np</sub></i> in
     * the more conventional notation).
     * 
     * @return  the total intensity (polarized and non-polarized).
     * 
     * @see #N()
     * @see #P()
     * @see #setIQUV(double, double, double, double)
     */
    public final double I() { return N + P(); }
  
    /**
     * Returns the total polarized intensity <i>P</i><sup>2</sup> = <i>Q</i><sup>2</sup> + <i>U</i><sup>2</sup> + <i>B</i><sup>2</sup>.
     * 
     * @return  the total polarized intensity.
     * 
     * @see #I()
     * @see #Q()
     * @see #U()
     * @see #V()
     */
    public final double P() { return ExtraMath.hypot(Q, U, V); }
    
    /**
     * Sets new standard Stokes parameters (<i>I</i>, <i>Q</i>, <i>U</i>, <i>V</i>). 
     * 
     * @param I     the new total intensity
     * @param Q     the new vertically polarized intensity
     * @param U     the new horizontally polarized intensity
     * @param V     the new circularly polarized intensity
     * 
     * @see #setNQUV(double, double, double, double)
     * @see #I()
     * @see #Q()
     * @see #U()
     * @see #V()
     * @see #P()
     * @see #N()
     */
    public void setIQUV(double I, double Q, double U, double V) {
        this.N = I - ExtraMath.hypot(Q, U, V);
        this.Q = Q;
        this.U = U;
        this.V = V;
    }
    
    /**
     * Sets new orthogonal Stokes parameters (<i>N</i>, <i>Q</i>, <i>U</i>, <i>V</i>). While <i>N</i> is not
     * a conventionat Stokes parameter, it is often a more suitable component to use, being independent of
     * the other 3 (the conventional <i>I</i> component is variant on <i>Q</i>, <i>U</i>, and <i>V</i>).
     * 
     * @param N     the new non-polatized intensity
     * @param Q     the new vertically polarized intensity
     * @param U     the new horizontally polarized intensity
     * @param V     the new circularly polarized intensity
     * 
     * @see #setNQUV(double, double, double, double)
     * @see #I()
     * @see #Q()
     * @see #U()
     * @see #V()
     * @see #P()
     * @see #N()
     */
    public void setNQUV(double N, double Q, double U, double V) {
        this.N = N;
        this.Q = Q;
        this.U = U;
        this.V = V;
    }
    
    /**
     * Rotates the linear polarization (<i>Q</i>, <i>U</i>), for example to align to a different coordinate
     * system with a different vertical direction. 
     * 
     * @param angle     (rad) The clockwise rotation angle.
     * 
     * @see #rotate(Angle)
     * @see #flip()
     */
    public void rotate(double angle) {
        angle *= 2.0;
        final double c = Math.cos(angle);
        final double s = Math.sin(angle);
     
        final double temp = Q;
        Q = c * temp - s * U;
        U = s * temp + c * U;  
    }
    
    /**
     * Rotates the linear polarization (<i>Q</i>, <i>U</i>), for example to align to a different coordinate
     * system with a different vertical direction. 
     * 
     * @param angle     (rad) The clockwise rotation angle.
     * 
     * @see #rotate(double)
     * @see #flip()
     */
    public void rotate(Angle angle) {
        final double c = angle.cos() * angle.cos() - angle.sin() * angle.sin();
        final double s = 2.0 * angle.sin() * angle.cos();
        
        final double temp = Q;
        Q = c * temp - s * U;
        U = s * temp + c * U;  
    }
    
    @Override
    public void flip() {
        V = -V;
    }

    @Override
    public Stokes clone() {
        try { return (Stokes) super.clone(); }
        catch(CloneNotSupportedException e) { return null; }
    }

    @Override
    public Stokes copy() {
        return clone();
    }

    @Override
    public void copy(Stokes o) {
        N = o.N;
        Q = o.Q;
        U = o.U;
        V = o.V;
    }

    @Override
    public void zero() {
        N = Q = U = V = 0.0;
    }

    @Override
    public boolean isNull() {
        return N==0.0 && Q == 0.0 && U == 0.0 && V == 0.0;
    }

    @Override
    public void scale(double factor) {
        N *= factor;
        Q *= factor;
        U *= factor;
        V *= factor;
    }

    @Override
    public void add(Stokes o) {
        N += o.N;
        Q += o.Q;
        U += o.U;
        V += o.V;
    }

    @Override
    public void subtract(Stokes o) {
        N -= o.N;
        Q -= o.Q;
        U -= o.U;
        V -= o.V;
    }

    @Override
    public void setSum(Stokes a, Stokes b) {
        copy(a);
        add(b);
    }

    @Override
    public void setDifference(Stokes a, Stokes b) {
        copy(a);
        subtract(b);
    }

    @Override
    public void addScaled(Stokes o, double factor) {
        N += factor * o.N;
        Q += factor * o.Q;
        U += factor * o.U;
        V += factor * o.V;
    }  
}
