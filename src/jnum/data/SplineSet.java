/* *****************************************************************************
 * Copyright (c) 2018 Attila Kovacs <attila[AT]sigmyne.com>.
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

package jnum.data;

import jnum.math.Coordinates;

/**
 * Cubic splines in multiple dimensions.
 * 
 * @author Attila Kovacs
 *
 * @param <VectorType>      the generic type of position vectors to use for the positioning of interpolated data
 *                          with this set of splines.
 */
public class SplineSet<VectorType extends Coordinates<? extends Number>> {
    private CubicSpline[] splines;
    
    /**
     * Instantiates a new set of cubic splines for interpolating in a space with the specified dimensions,
     * 
     * @param dim       the dimensionality of the space in which the splines are to be used for interpolating.
     */
    public SplineSet(int dim) {
        splines = new CubicSpline[dim];
        for(int i=dim; --i >=0; ) splines[i] = new CubicSpline();
    }
    
    /**
     * Configures the spline for interpolating at the specified data index position. 
     * 
     * @param offset        The data index for which you want to get an interpolated value for. Each component in the
     *                      position vector corresponds either to an integer data index, or else a fractional position in-between 
     *                      two consecutive integer data indixes.
     *                      
     * @see #centerOn(double...)
     * @see #centerOn(float...)
     */
    public void centerOn(VectorType offset) {
        for(int i=splines.length; --i >= 0; ) splines[i].centerOn(offset.getComponent(i).doubleValue());
    }
    
    /**
     * Configures the spline for interpolating at the specified data index position. 
     * 
     * @param offsets        The data index for which you want to get an interpolated value for. Each component in the
     *                      position vector corresponds either to an integer data index, or else a fractional position in-between 
     *                      two consecutive integer data indixes.
     *                      
     * @see #centerOn(float...)
     * @see #centerOn(Coordinates)
     */
    public void centerOn(double... offsets) {
        for(int i=splines.length; --i >= 0; ) splines[i].centerOn(offsets[i]);
    }
    
    /**
     * Configures the spline for interpolating at the specified data index position. 
     * 
     * @param offsets        The data index for which you want to get an interpolated value for. Each component in the
     *                      position vector corresponds either to an integer data index, or else a fractional position in-between 
     *                      two consecutive integer data indixes.
     *                      
     * @see #centerOn(double...)
     * @see #centerOn(Coordinates)
     */
    public void centerOn(float... offsets) {
        for(int i=splines.length; --i >= 0; ) splines[i].centerOn(offsets[i]);
    }
    
    /**
     * Returns the cubic spline along the specified dimension/index.
     * 
     * @param index     the index of the dimension for which to obtain the cubic spline
     * @return          the spline at that index, ascurrently configured.
     * 
     * @see #centerOn(Coordinates)
     * @see #centerOn(double...)
     * @see #centerOn(float...)
     * 
     */
    public CubicSpline getSpline(int index) { return splines[index]; }
}
