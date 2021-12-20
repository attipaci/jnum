/* *****************************************************************************
 * Copyright (c) 2014 Attila Kovacs <attila[AT]sigmyne.com>.
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

import java.util.*;

/**
 * A base class for representing a set of basis vectors spanning some space.
 * 
 * @author Attila Kovacs
 *
 * @param <T>   The generic type of components in the basis vectors.
 */
public abstract class AbstractVectorBasis<T> extends Vector<AbstractVector<T>> {

    /** */
	private static final long serialVersionUID = -4045327046654212525L;
	
	/** the number of components in this vector */
	private int dim;
	
	/**
	 * Creates a vector basis with for vectors with the specified dimension.
	 * 
	 * @param dim      The dimension of vectors in this basis.
	 */
	public AbstractVectorBasis(int dim) {
	    this.dim = dim;
	}
	
	/**
	 * Gets a new vector of the type and size supported by this basis.
     *
	 * @return     a new vector instance of the type supported by this basis.
	 */
	public abstract AbstractVector<T> getVectorInstance();
	
	/**
	 * Gets the size (dimension) of vectors supported by this vector basis.
	 *    
	 * @return     Size (dimension) of vectors in this basis.
	 */
    public final int getVectorSize() {
        return dim;
    }
	
	/**
	 * Orthogonalize the vectors via Gram-Schmidt. After orthogonalization the vectors
	 * in this basis will span the same space as before, but will ve mutually orthogonal
	 * to one another. That is the dot product of any two vectors in this bases
	 * will be zero after the orthogonalization.
	 * 
	 */
	public void orthogonalize() {
		for(int i=1; i<size(); i++) for(int j=0; j<i; j++) get(i).orthogonalizeTo(get(j));	
	}
	

	/**
	 * Normalizes the vectors in this basis. That is they are scaled to be of unit
	 * length, without affecting their directions.
	 * 
	 */
	public void normalize() {
		for(int i=0; i<size(); i++) get(i).normalize();
	}
	
	/**
	 * Orthogonalizes and normalizes the vectors in this basis. Same as calling {@link #orthogonalize()}
	 * followed by {@link #normalize()}.
	 * 
	 */
	public void orthonormalize() { 
		orthogonalize();
		normalize();		
	}

	/**
	 * Returns the basis set as a matrix, in which the basis vectors constitute the 
	 * matrix columns.
	 * 
	 * @return     the basis vectors as row vectors in a matric form.
	 */
	public abstract AbstractMatrix<T> asRowVector();
	
	
	protected final void toMatrix(AbstractMatrix<T> M) {
		M.assertSize(get(0).size(), size());
		for(int j=0; j<size(); j++) {
			AbstractVector<T> v = get(j);
			for(int i=0; i<v.size(); i++) M.set(i, j, v.getComponent(i));
		}		
	}
	
}
