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

import jnum.math.MathVector;

public class GaussInverter<T> implements MatrixInverter<T>, MatrixSolver<T> {
    private AbstractMatrix<T> inverse;
    private int rank = -1;

    protected GaussInverter(AbstractMatrix<T> M) {
        eliminate(M);
    }

    private void eliminate(AbstractMatrix<T> M) {
        if(!M.isSquare()) throw new SquareMatrixException();

        final int size = M.rows();

        inverse = M.getMatrixInstance(size, size, true);
        inverse.addIdentity();

        AbstractMatrix<T> combo = M.getMatrixInstance(size, 2 * size, false);
        combo.paste(M, 0, 0);
        combo.paste(inverse, 0, size);
        combo.gaussJordan();

        rank = 0;

        MatrixElement<T> e = combo.getElementInstance();
        rank = 0;
        
        double tiny2 = 1e-12 * combo.getMagnitude();
        tiny2 *= tiny2;
        
        // Get the basis vectors from the irreducible columns of the original matrix
        for(int i=0, col=0; i<size; i++) {
            for(int j=col; j<size; j++) if(e.from(i, j).absSquared() > tiny2) {
                rank++;
                col = j+1;
                break;
            }
        }
        
        if(rank != size) throw new IllegalArgumentException("Singular imput matrix.");
        
        // Get the inverse as the second half of columns in this matrix...
        for(int i=size; --i >= 0; ) for(int j=size; --j >= 0; ) inverse.set(i, j, combo.get(i, size + j));
       
    }

    private int size() { return inverse.rows(); }

    protected AbstractMatrix<T> getI() {
       return inverse; 
    }
    
    @Override
    public AbstractMatrix<T> getInverseMatrix() {
        return inverse.copy();
    }

    @Override
    public void getInverseTo(AbstractMatrix<T> inverse) {
        inverse.assertSize(size(), size());
        inverse.setData(inverse.getData());
    }


    public final synchronized int getRank() {
        return rank;
    }

    @Override
    public T[] solveFor(T[] y) {
        return inverse.dot(y);
    }

    @Override
    public void solveFor(T[] y, T[] x) {


    }

    @Override
    public MathVector<T> solveFor(MathVector<T> y) {
        return inverse.dot(y);
    }

    @Override
    public void solveFor(MathVector<T> y, MathVector<T> x) {
        inverse.dot(y, x);        
    }  


}
