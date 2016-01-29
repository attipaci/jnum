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


// TODO: Auto-generated Javadoc
/**
 * The Class GenericVectorBasis.
 *
 * @param <T> the generic type
 */
public class GenericVectorBasis<T extends LinearAlgebra<? super T> & AbstractAlgebra<? super T> & Metric<? super T> & AbsoluteValue> extends AbstractVectorBasis<T> {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 196973970496491957L;

	/**
	 * Instantiates a new generic vector basis.
	 */
	public GenericVectorBasis() {}
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractVectorBasis#asMatrix()
	 */
	@Override
	public AbstractMatrix<T> asMatrix() {
		GenericMatrix<T> M = new GenericMatrix<T>(get(0).getType());
		asMatrix(M);
		return M;
	}


}
