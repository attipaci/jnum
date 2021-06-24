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


public interface SquareMatrixAlgebra<M, T> extends MatrixAlgebra<M, T>, Inversion, IdentityValue {
	
    public T getDiagonal(int i);
    
    public void setDiagonal(int i, T value);
    
    public T copyOfDiagonal(int i);
    
    public void addDiagonal(int i, T value);
    
    public void scaleDiagonal(int i, double factor);

    public void zeroDiagonal(int i);
    
    public boolean isNullDiagonal(int i);
    
    
    public T trace();
    
    /**
     * Gets the determinant of this matrix.
     * 
     * @return the determinant.
     */
    public T getDeterminant();
    
    SquareMatrixAlgebra<M, T> getInverse();
    
	public void addIdentity(double scaling);

	public void addIdentity();
	
	public void subtractIdentity();
	

	

	
}
