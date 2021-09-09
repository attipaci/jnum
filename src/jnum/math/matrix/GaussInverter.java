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


import jnum.math.MathVector;


/**
 * An intermediate object class for calculating the inverse of a matrix using Gauss-Jordan
 * elimination. Apart from providing the inverse, and matrix solutions, it can also
 * be used for obtaining the rank of the matrix at very little extra cost.
 * 
 * @author Attila Kovacs
 *
 * @param <T>
 */
public abstract class GaussInverter<T> implements MatrixInverter<T>, MatrixSolver<T> {
    private AbstractMatrix<T> inverse;
    private int rank = -1;

    /**
     * Construct a new matrix inverter object for the specified matrix. The inverse
     * is calculated by Gauss-Jordan elimination of a matrix constructed of
     * the argument and the identity matrix adjoint to it.
     * 
     * @param M                         The matrix that is to be inverted.
     * @throws SquareMatrixException    If the matrix argument is not a square matrix.
     * @throws SingularMatrixException  If the matrix argument cannot be inverted because it is singular (degenerate).
     */
    protected GaussInverter(AbstractMatrix<T> M) throws SquareMatrixException, SingularMatrixException {
        eliminate(M);
    }

    private void eliminate(AbstractMatrix<T> M) throws SquareMatrixException, SingularMatrixException {
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
        
        double tiny2 = 1e-12 * combo.getMagnitude(0, 0, size, size);
        tiny2 *= tiny2;
        
        // Get the basis vectors from the irreducible columns of the original matrix
        for(int i=0, col=0; i<size; i++) {
            for(int j=col; j<size; j++) if(e.from(i, j).absSquared() > tiny2) {
                rank++;
                col = j+1;
                break;
            }
        }
        
        if(rank != size) throw new SingularMatrixException();
        
        // Get the inverse as the second half of columns in this matrix...
        // Elements that are zero within rounding errors are set to zero.
        for(int i=size; --i >= 0; ) for(int j=size; --j >= 0; ) {
            if(e.from(i, j).absSquared() > tiny2) inverse.set(i, j, combo.get(i, size + j));
            else inverse.clear(i, j);
        }
       
    }

    /**
     * Gets the master inverse matrix. This is for use by sub-class implementations only, and only
     * as long as they do not modify the returned master, which should never be corrupted.
     * Any operations that do require modifying the returned inverse should call {@link #getInverseMatrix()}
     * instead. 
     * 
     * @return  the master copy the inverse. It should be used very carefully and only by subclass
     *          implementattions lest the inverse matrix should get corrupted!
     */
    protected AbstractMatrix<T> getMasterInverse() {
       return inverse; 
    }
    
    @Override
    public AbstractMatrix<T> getInverseMatrix() {
        return inverse.copy();
    }

    @Override
    public void solveFor(T[] y, T[] x) {
        inverse.dot(y, x);
    }

    @Override
    public AbstractVector<T> solveFor(MathVector<? extends T> y) {
        return inverse.dot(y);
    }

    @Override
    public void solveFor(MathVector<? extends T> y, MathVector<T> x) {
        inverse.dot(y, x);        
    }  


}
