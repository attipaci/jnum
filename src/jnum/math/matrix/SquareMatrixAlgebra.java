/*******************************************************************************
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
 *     Attila Kovacs <attila[AT]sigmyne.com> - initial API and implementation
 ******************************************************************************/

package jnum.math.matrix;

import jnum.math.IdentityValue;
import jnum.math.Inversion;


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
	

	public void addIdentity(double scaling);

	public void addIdentity();
	
	public void subtractIdentity();
	
	public void solve(AbstractVector<T>[] inputVectors);
	
	//public void getLUdecomposition();
	
	
	
}
