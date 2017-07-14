/*******************************************************************************
 * Copyright (c) 2017 Attila Kovacs <attila[AT]sigmyne.com>.
 * All rights reserved. 
 * 
 * This file is part of crush.
 * 
 *     crush is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     crush is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with crush.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Attila Kovacs <attila[AT]sigmyne.com> - initial API and implementation
 ******************************************************************************/

package jnum.data.cube2;

import jnum.data.image.Abstract2D;
import jnum.data.image.Observation2D;

public class Observation2D1 extends AbstractMap2D1<Observation2D> {

    public Observation2D1(Class<? extends Number> dataType, int flagType) {
        super(dataType, flagType);
    }

    @Override
    public Observation2D getImage2DInstance() { return new Observation2D(getElementType(), getFlagType()); }
  
    public Abstract2D1<Abstract2D> getWeights() {
        Abstract2D1<Abstract2D> weight = new Abstract2D1<Abstract2D>() {
            @Override
            public Abstract2D getImage2DInstance(int sizeX, int sizeY) { return null; }
        };
        for(int i=0; i<sizeZ(); i++) weight.addPlane(getPlane(i).getWeights());
        return weight;
    }
    
    public Abstract2D1<Abstract2D> getExposures() {
        Abstract2D1<Abstract2D> weight = new Abstract2D1<Abstract2D>() {
            @Override
            public Abstract2D getImage2DInstance(int sizeX, int sizeY) { return null; }
        };
        for(int i=0; i<sizeZ(); i++) weight.addPlane(getPlane(i).getExposures());
        return weight;
    }
    
    
    
}
