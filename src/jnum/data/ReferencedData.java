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

package jnum.data;

import jnum.data.index.Index;
import jnum.math.MathVector;

/**
 * Wraps a regularly sampled (gridded) data with a reference position (origin location) on
 * the data grid. 
 * 
 * @author Attila Kovacs
 *
 * @param <IndexType>
 * @param <VectorType>
 */
public class ReferencedData<IndexType extends Index<IndexType>, VectorType extends MathVector<Double>>
implements Referenced<IndexType, VectorType> {
    private RegularData<IndexType, VectorType> data;
    private VectorType refIndex;
    
    /**
     * Instantiates a new data object that referenced to some grid location or origin.
     * 
     * @param data      the underlying regularly sampled (gridded) data 
     * @param refIndex  the location of the reference point (origin) on the data grid.
     */
    public ReferencedData(RegularData<IndexType, VectorType> data, VectorType refIndex) {
        this.data = data;
        setReferenceIndex(refIndex);
    }
    
    @Override
    public RegularData<IndexType, VectorType> getData() {
        return data;
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
