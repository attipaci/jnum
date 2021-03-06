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

package jnum.data.transform;

import jnum.data.Transforming;
import jnum.math.TrueVector;

public class Stretch<V extends TrueVector<Double>> implements Transforming<V> {
    private V scale;
   
    public Stretch(V scale) {
        this.scale = scale;
    }
     
    @Override
    public int hashCode() { return super.hashCode() ^ scale.hashCode(); }
    
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof Stretch)) return false;
        
        Stretch<?> s = (Stretch<?>) o;
        if(!s.scale.equals(scale)) return false;
        return true;
    }
    
    @Override
    public final void transform(V v) {
        for(int i=scale.size(); --i >= 0; ) v.setComponent(i, v.getComponent(i) * scale.getComponent(i));
    }

}
