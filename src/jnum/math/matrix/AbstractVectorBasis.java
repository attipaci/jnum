/*******************************************************************************
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
 *     Attila Kovacs <attila[AT]sigmyne.com> - initial API and implementation
 ******************************************************************************/


package jnum.math.matrix;

import java.util.*;


public abstract class AbstractVectorBasis<T> extends Vector<AbstractVector<T>> {

	private static final long serialVersionUID = -4045327046654212525L;


	public AbstractVectorBasis() {}
	
	public abstract AbstractVector<T> getVectorInstance(int size);
	
	public void orthogonalize() {
		for(int i=1; i<size(); i++) for(int j=0; j<i; j++) get(i).orthogonalizeTo(get(j));	
	}
	

	public void normalize() {
		for(int i=0; i<size(); i++) get(i).normalize();
	}
	

	public void orthonormalize() { 
		orthogonalize();
		normalize();		
	}

	public abstract AbstractMatrix<T> asMatrix();


	public void asMatrix(AbstractMatrix<T> M) {
		M.assertSize(get(0).size(), size());
		for(int j=0; j<size(); j++) {
			AbstractVector<T> v = get(j);
			for(int i=0; i<v.size(); i++) M.set(i, j, v.getComponent(i));
		}		
	}
	
}
