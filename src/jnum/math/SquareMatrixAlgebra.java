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
 * The Interface SquareMatrixAlgebra.
 *
 * @param <T> the generic type
 */
public interface SquareMatrixAlgebra<T> extends Inversion, IdentityValue {
	
	// TODO
	// Polynomial getCharacterizticPolynomial()
	
	// TODO Gaussian Elimination for triangular form...
	
	// TODO
	//Type determinant();
	
	// TODO 
	// public double[] findEigenValues();
	
	// TODO
	// diagonalizes and returns the transformation matrix for diagonalization...
	// public Matrix diagonalize() 
	
	// TODO
	// jordanizes and returns the jordanizing transformation matrix...
	// public Matrix jordanize()
	
	// public double[] eigenValues()
	
	// TODO implement alternative inversions, choose best?
	
	/**
	 * Size.
	 *
	 * @return the int
	 */
	public int size();
	
	/**
	 * Solve.
	 *
	 * @param inputVectors the input vectors
	 */
	public void solve(AbstractVector<T>[] inputVectors);
	
	//public void getLUdecomposition();
	
	
	
}
