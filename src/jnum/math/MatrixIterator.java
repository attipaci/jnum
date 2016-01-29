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

import java.util.Iterator;

// TODO: Auto-generated Javadoc
/**
 * The Class MatrixIterator.
 *
 * @param <T> the generic type
 */
public class MatrixIterator<T> implements Iterator<T> {
	
	/** The matrix. */
	AbstractMatrix<T> matrix;
	
	/** The cols. */
	protected int i=0, j=-1, rows, cols;
	
	/**
	 * Instantiates a new matrix iterator.
	 *
	 * @param M the m
	 */
	public MatrixIterator(AbstractMatrix<T> M) {
		matrix = M;
		rows = M.rows();
		cols = M.cols();
	}
	
	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		if(i < rows) return true;
		return j<cols;
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	@Override
	public T next() {
		j++;
		if(j >= cols) {
			j=0;
			i++;
		}
		return matrix.getValue(i, j);
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException("Cannot remove elements from a matrix type object.");
	}

}
