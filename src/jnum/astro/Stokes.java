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

package jnum.astro;

import java.io.Serializable;

import jnum.Copiable;
import jnum.CopyCat;
import jnum.ExtraMath;
import jnum.math.Angle;
import jnum.math.Inversion;
import jnum.math.LinearAlgebra;

public class Stokes implements LinearAlgebra<Stokes>, Cloneable, Copiable<Stokes>, CopyCat<Stokes>, Inversion, Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 3607597866105508377L;

    public double N, Q, U, V;

    public final double I() { return N + P(); }
  
    public final double P() { return ExtraMath.hypot(Q, U, V); }
    
    public void setIQUV(double I, double Q, double U, double V) {
        this.N = I - ExtraMath.hypot(Q, U, V);
        this.Q = Q;
        this.U = U;
        this.V = V;
    }
    
    public void setNQUV(double N, double Q, double U, double V) {
        this.N = N;
        this.Q = Q;
        this.U = U;
        this.V = V;
    }

    public void rotate(double angle) {
        angle *= 2.0;
        final double c = Math.cos(angle);
        final double s = Math.sin(angle);
     
        final double temp = Q;
        Q = c * temp - s * U;
        U = s * temp + c * U;  
    }
    
    public void rotate(Angle angle) {
        final double c = angle.cos() * angle.cos() - angle.sin() * angle.sin();
        final double s = 2.0 * angle.sin() * angle.cos();
        
        final double temp = Q;
        Q = c * temp - s * U;
        U = s * temp + c * U;  
    }
    
    @Override
    public void invert() {
        V *= -1.0;
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
