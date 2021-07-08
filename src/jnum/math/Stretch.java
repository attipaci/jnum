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

package jnum.math;

import java.util.Arrays;

import jnum.Copiable;
import jnum.util.HashCode;

/**
 * A simple coordinate stretch, which rescales each coordinate by a specified factor, for example
 * as a result of a change of unit along the various coordinate axes. It can operate on any dimension
 * vector, smaller or larger than the specified scaling factors. When operated on vectors of lower
 * dimension <i>n</i>, only the first <i>n</i> scalars are used to transform the input vectors. 
 * When operated on a vector of higher dimensionality, only as many components of that vector
 * will undergo a strech as many scaling factors were set. 
 * 
 * @param <T>  the generic type of coordinates on which this strech operates on.
 * 
 * @author Attila Kovacs
 */

public class Stretch<T extends Coordinates<Double>> implements CoordinateTransform<T> {
    
    /** the scaling values along each coordinate */
    private double[] scaling;
   
    /**
     * Instantiates a new coordinate stretch, with the specified scaling factors along
     * each of the coordinate directions. The scaling
     * factors used are independent from the input content, such that changes to the
     * components of the argument afterwards will not affect this transformation.
     * 
     * @param scaling   the coordinate scaling factors along each axis subjected to scaling.
     */
    public Stretch(Coordinates<Double> scaling) {
        this.scaling = new double[scaling.size()];
        for(int i=scaling.size(); --i >= 0; ) this.scaling[i] = scaling.getComponent(i);
    }
    
    /**
     * Instantiates a new coordinate stretch, with the specified scaling factors along
     * each of the coordinate directions.  The scaling
     * factors used are independent from the input content, such that changes to the
     * components of the argument afterwards will not affect this transformation.
     * 
     * @param scaling   the coordinate scaling factors along each axis subjected to scaling.
     */
    public Stretch(double... scaling) {
        this.scaling = new double[scaling.length];
        for(int i=scaling.length; --i >= 0; ) this.scaling[i] = scaling[i];
    }
     
    
    /**
     * Instantiates a new coordinate stretch, with the specified scaling factors along
     * each of the coordinate directions.  The scaling
     * factors used are independent from the input content, such that changes to the
     * components of the argument afterwards will not affect this transformation.
     * 
     * @param scaling   the coordinate scaling factors along each axis subjected to scaling.
     */
    public Stretch(float... scaling) {
        this.scaling = new double[scaling.length];
        for(int i=scaling.length; --i >= 0; ) this.scaling[i] = scaling[i];
    }
     
    @Override
    public int hashCode() { return super.hashCode() ^ HashCode.from(scaling); }
    
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof Stretch)) return false;
        
        @SuppressWarnings("unchecked")
        Stretch<T> s = (Stretch<T>) o;
        if(s.scaling.length != scaling.length) return false;
        for(int i=scaling.length; --i >= 0; ) if(s.scaling[i] != scaling[i]) return false;
        return true;
    }
    
    @Override
    public final void transform(T v) {
        // Figure out how many elements can be scaled between the scalars and the vector...
        int n = Math.min(this.scaling.length, v.size());
        while(--n >= 0) v.setComponent(n, v.getComponent(n) * scaling[n]);
    }
    
    @Override
    public T getTransformed(T v) { 
        @SuppressWarnings({ "unchecked" })
        T vt = (T) ((Copiable<? extends Coordinates<Double>>) v).copy();
        transform(v);
        return vt;
    }

    /**
     * Transforms an input coordinates in situ.
     * 
     * @param v the coordinates that are streched in situ.
     */
    public final void transform(double[] v) {
        // Figure out how many elements can be scaled between the scalars and the vector...
        int n = Math.min(this.scaling.length, v.length);
        while(--n >= 0) v[n] *= scaling[n];
    }

    /**
     * Gets transformed coordinates from the input.
     * 
     * @param v     the input coordinates.
     * @return      the streched coordinates.
     */
    public double[] getTransformed(double... v) { 
        double[] vt = Arrays.copyOf(v, v.length);
        transform(v);
        return vt;
    }
    
    /**
     * Transforms an input coordinates in situ.
     * 
     * @param v the coordinates that are streched in situ.
     */
    public final void transform(float[] v) {
        // Figure out how many elements can be scaled between the scalars and the vector...
        int n = Math.min(this.scaling.length, v.length);
        while(--n >= 0) v[n] *= scaling[n];
    }

    /**
     * Gets transformed coordinates from the input.
     * 
     * @param v     the input coordinates.
     * @return      the streched coordinates.
     */
    public float[] getTransformed(float... v) { 
        float[] vt = Arrays.copyOf(v, v.length);
        transform(v);
        return vt;
    }
}
