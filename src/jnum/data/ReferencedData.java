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

import jnum.math.TrueVector;

public class ReferencedData<IndexType extends Index<IndexType>, VectorType extends TrueVector<Double>>
implements Referenced<IndexType, VectorType> {
    public RegularData<IndexType, VectorType> data;
    public VectorType refIndex;
    
    public ReferencedData() { this(null, null); }
    
    public ReferencedData(RegularData<IndexType, VectorType> data, VectorType refIndex) {
        setData(data);
        setReferenceIndex(refIndex);
    }
    
    @Override
    public RegularData<IndexType, VectorType> getData() {
        return data;
    }
    
    public void setData(RegularData<IndexType, VectorType> data) {
        this.data = data;
    }
    
    @Override
    public VectorType getReferenceIndex() {
        return refIndex;
    }
    
    @Override
    public void setReferenceIndex(VectorType index) {
        this.refIndex = index;
    }   
}
