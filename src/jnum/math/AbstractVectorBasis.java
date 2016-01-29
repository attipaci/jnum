/*******************************************************************************
 * Copyright (c) 2014 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
 * All rights reserved. 
 * 
 * This file is part of jnum.
 * 
 *     kovacs.util is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     kovacs.util is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with kovacs.util.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Attila Kovacs <attila_kovacs[AT]post.harvard.edu> - initial API and implementation
 ******************************************************************************/


package jnum.math;

import java.util.*;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractVectorBasis.
 *
 * @param <T> the generic type
 */
public abstract class AbstractVectorBasis<T> extends Vector<AbstractVector<T>> {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4045327046654212525L;

	/**
	 * Instantiates a new abstract vector basis.
	 */
	public AbstractVectorBasis() {}
	
	/**
	 * Orthogonalize.
	 */
	public void orthogonalize() {
		for(int i=1; i<size(); i++) for(int j=0; j<i; j++) get(i).orthogonalizeTo(get(j));	
	}
	
	/**
	 * Normalize.
	 */
	public void normalize() {
		for(int i=0; i<size(); i++) get(i).normalize();
	}
	
	/**
	 * Orthonormalize.
	 */
	public void orthonormalize() { 
		orthogonalize();
		normalize();		
	}
	
	/**
	 * As matrix.
	 *
	 * @return the abstract matrix
	 */
	public abstract AbstractMatrix<T> asMatrix();

	/**
	 * As matrix.
	 *
	 * @param M the m
	 */
	public void asMatrix(AbstractMatrix<T> M) {
		M.assertSize(get(0).size(), size());
		for(int j=0; j<size(); j++) {
			AbstractVector<T> v = get(j);
			for(int i=0; i<v.size(); i++) M.setValue(i, j, v.getComponent(i));
		}		
	}
	
}
