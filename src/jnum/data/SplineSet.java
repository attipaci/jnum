/*******************************************************************************
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
 *     Attila Kovacs <attila[AT]sigmyne.com> - initial API and implementation
 ******************************************************************************/

package jnum.data;

import jnum.math.Coordinates;

public class SplineSet<VectorType extends Coordinates<? extends Number>> {
    private CubicSpline[] splines;
    
    public SplineSet(int dim) {
        splines = new CubicSpline[dim];
        for(int i=dim; --i >=0; ) splines[i] = new CubicSpline();
    }
    
    public void centerOn(VectorType offset) {
        for(int i=splines.length; --i >= 0; ) splines[i].centerOn(offset.getComponent(i).doubleValue());
    }
    
    public void centerOn(double ... offsets) {
        for(int i=splines.length; --i >= 0; ) splines[i].centerOn(offsets[i]);
    }
    
    public CubicSpline getSpline(int index) { return splines[index]; }
}
