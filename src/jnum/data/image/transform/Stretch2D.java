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

package jnum.data.image.transform;

import jnum.data.Transforming;
import jnum.math.Vector2D;
import jnum.util.HashCode;

public class Stretch2D implements Transforming<Vector2D> {
    private double scaleX;
    private double scaleY;
    
    
    public Stretch2D(double scaleX, double scaleY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }
    
    @Override
    public int hashCode() { return super.hashCode() ^ HashCode.from(scaleX) ^ HashCode.from(scaleY); }
    
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof Stretch2D)) return false;
        
        Stretch2D s = (Stretch2D) o;
        if(scaleX != s.scaleX) return false;
        if(scaleY != s.scaleY) return false;
        return true;
    }
    
    @Override
    public final void transform(Vector2D v) {
        v.scaleX(scaleX);
        v.scaleY(scaleY);
    }

}
