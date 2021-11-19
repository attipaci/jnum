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

package jnum.math.matrix;

import java.io.Serializable;

import jnum.CopiableContent;
import jnum.Util;
import jnum.math.MathVector;
import jnum.text.DecimalFormating;


/**
 * An abstract vector class representing a mathematical vector for some generic type element. It has two principal 
 * subclasses, {@link RealVector}, which is a real-valued vector with essentially primitive <code>double</code> 
 * elements, and {@link ObjectVector}, which handles vector for generic type objects as long as they provide the 
 * required algebra to support matrix operation. For example {@link ComplexVector} with {@link jnum.math.Complex} elements 
 * is an example subtype, but one could construct vectors e.g. with {@link Matrix} or {@link ObjectMatrix} elements 
 * (for a vecor of matrices), or vectors with other more complex types...
 * 
 * @author Attila Kovacs
 *
 * @param <T>       The generic type of the elements in this vector.
 */
public abstract class AbstractVector<T> implements MathVector<T>, Serializable, Cloneable, CopiableContent<AbstractVector<T>>, DecimalFormating {

    /** */
	private static final long serialVersionUID = 785522803183758105L;

	/**
	 * Gets the underlying data object, normally an simple array, either and object array of type T[]
	 * or a primitive array such as double[].
	 * 
	 * @return     The data object (array) that holds the underlying data of this vector.
	 */
	public abstract Object getData();
	
	/**
	 * Sets the underlying data in this vector to the specified data object, which is usually a simple
	 * array, either an object array T[], or a primitive array such as double[]. Whatever it is
	 * it expected the type of object as returned by {@link #getData()}.
	 * 
	 * @param data     The new data object (array) for this vector.
	 */
	public abstract void setData(Object data);

	/**
	 * Gets a new instance of a vector of the same type as this one, with the specified size. The new 
	 * vector is initialized with zero content.
	 * 
	 * @param size     The size (number of components) of the new vector instance.
	 * @return         A new vetor of the same type as this, with the specified size, and zero inital values.
	 */
	public abstract AbstractVector<T> getVectorInstance(int size);
	
	
	@SuppressWarnings("unchecked")
    @Override
	public AbstractVector<T> clone() {
		try { return (AbstractVector<T>) super.clone(); }
		catch(CloneNotSupportedException e) { return null; }		
	}
	   
    @Override
    public void reflectOn(final MathVector<? extends T> v) {
        AbstractVector<T> ortho = copy();
        ortho.orthogonalizeTo(v);
        addScaled(ortho, -2.0);        
    }
    
    @Override
    public String toString() {
        return toString(Util.s4);
    }
    
}
