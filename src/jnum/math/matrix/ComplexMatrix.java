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


import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Arrays;

import jnum.ViewableAsDoubles;
import jnum.data.fitting.ConvergenceException;
import jnum.data.image.Index2D;
import jnum.math.Complex;
import jnum.math.ComplexAddition;
import jnum.math.ComplexConjugate;
import jnum.math.ComplexScaling;
import jnum.math.MathVector;
import jnum.math.Multiplication;
import jnum.math.SymmetryException;

/**
 * A class representing a complex valued full matrix. At every slot in this matrix there is a {@link Complex} type
 * matrix element. Complex matrices are otherwise very much like their real counterparts, providing almost identical
 * functionality and support for heterogeneous cross products, as well as transforming both real-valued and 
 * complex-valued vectors alike.
 * 
 * <h3>Complex Matrices</h3>
 * 
 * Complex matrices work very similarly to their real counterparts, with the exception that they operate
 * with complex numbers and algebra. Nearly all operations of {@link Matrix} can also be performed on 
 * {@link ComplexMatrix} including LU decomposition, inversion, or real-valued eigensystem determination 
 * (for Hermitian) matrices, using Jacobi transforms. The notable exception is SVD, which is implemented only for
 * real-valued {@link Matrix} objects at this time.
 * 
 * Best of all, you can intermingle real and complex matrices at your pleasure, such as when calculating 
 * a heterogeneous dot product:
 * 
 * <pre>
 *  ComplexMatrix C = new ComplexMatrix(3, 3);
 *  Matrix R = new Matrix(3, 5);
 *  
 *  // populate matrices with data
 *  // ...
 *  
 *  ComplexMatrix P = C.dot(R);
 * </pre>
 * 
 * or operating the real matrix on a complex vector:
 * 
 * <pre>
 *  ComplexVector v = new ComplexVector(...);
 *  Matrix M = new Matrix(...);
 *  
 *  ComplexVector transformed = M.dot(v);
 * </pre>
 * 
 * @author Attila Kovacs
 *
 */
public class ComplexMatrix extends ObjectMatrix<Complex> implements ComplexScaling, ComplexConjugate, Multiplication<Complex>, ComplexAddition {

    private static final long serialVersionUID = 8842229635231169797L;

    /**
     * Constructs a new complex matrix with the specified dimensions. All elements are intialized to zero values.
     * 
     * @param rows      Number of rows in this matrix
     * @param cols      Number of columns in this matrix
     */
    public ComplexMatrix(int rows, int cols) {
        super(Complex.class, rows, cols);
    }

    /**
     * Constructs a new complex matrix of square shape, with the specified size.
     * 
     * @param size      Size of square matrix.
     */
    public ComplexMatrix(int size) {
        super(Complex.class, size, size);
    }

    /**
     * Constructs a new complex matrix with the specified 2D complex array as the backing data element.
     * The matrix will reference the specified array, so changes to the elements in the array will
     * necessarily become changes to the matrix itself.
     * 
     * @param data              The 2D complex array to serve as the backing storage for this matrix.
     * @throws ShapeException   If the supplied data has non-rectangular shape, i.e. has rows of
     *                          different length.
     */
    public ComplexMatrix(Complex[][] data) throws ShapeException {
        super(data);
    }

    /**
     * Constructs a new Complex matrix from a real-valued matrix. The complex data in this
     * matrix is initialized with the real part supplied by the specified matrix, and with
     * imaginary parts initialized to zero for all elements.
     * 
     * @param M     The real-valued matrix that specifies the real parts of this complex matrix.
     */
    public ComplexMatrix(Matrix M) {
        this(M.rows(), M.cols());
        for(int i=rows(); --i >= 0; ) for(int j = cols(); --j >= 0; ) get(i, j).set(M.get(i, j), 0.0);  
    }
    
    /**
     * Constructs a new complex matrix from a string representation, which must be in the expected
     * format
     * 
     * @param text          String representation of a complex matrix.
     * @param pos           Position in string at which to begin parsing. The parse position
     *                      will be updated to the last character parsed successfully.
     * @throws ParseException   If there was some problem with parsing the matrix from the 
     *                          supplied string.
     */
    public ComplexMatrix(String text, ParsePosition pos) throws ParseException, Exception {
        super(Complex.class, text, pos);
    }

    @Override
    public ComplexMatrix getMatrixInstance(int rows, int cols, boolean initialize) {
        if(initialize) return new ComplexMatrix(rows, cols);
        return new ComplexMatrix((Complex[][]) Array.newInstance(Complex.class, rows, cols));
    }

    @Override
    public ComplexMatrix clone() {
        return (ComplexMatrix) super.clone();
    }

    @Override
    public ComplexMatrix copy() {
        return (ComplexMatrix) super.copy();
    }
    
    /**
     * Gets the Jacobi transform of this complex matrix. The transform requires that this matrix is
     * a Hermitian matrix (square matrix in which the the element at <code>[i][j]</code> is the complex conjugate
     * of the element at <code>[j][i]</code> for all <code>i != j</code>).
     * 
     * The Jacobi transform is a series of element rotations that are used to bring the matrix
     * into a diagonal form, and hence can be used to obtain the {@link EigenSystem} of this
     * matrix. It is a O(<i>N</i><sup>3</sup>) process, which is not the fastest, but it is foolproof. 
     * 
     * @return          The diagonalized Jacobi transform of this matrix, if possible.
     * @throws SquareMatrixException    If this matrix is not a square matrix
     * @throws SymmetryException        If this matrix is not Hermitian
     * @throws ConvergenceException     If the Jacobi transform did not converge within the alotted number of attempts.
     */
    public JacobiTransform getJacobiTransform() throws SquareMatrixException, SymmetryException, ConvergenceException {
        return new JacobiTransform();
    }
    
    /**
     * Gets the eigenvalues, and eigenvectors of this matrix. Essentially the diagonalized form of
     * this matrix, and the basis transformations to and from its eigen basis. Currently it will
     * return the Jacobi transform (i.e. the same as {@link #getJacobiTransform()}.
     * 
     * @return          The eigen system of this matrix, if possible.
     * @throws SquareMatrixException    If this matrix is not a square matrix
     * @throws SymmetryException        If this matrix does not have the required symmetry to perform the operation
     *                                  (e.g. if eigen systems are only avagilable for Hermitian matrices).
     * @throws ConvergenceException     If the diagonalizaton attempt did not converge.
     */
    public EigenSystem<Complex, ?> getEigenSystem() throws SquareMatrixException, SymmetryException, ConvergenceException {
        return getJacobiTransform();
    }
    
    @Override
    public LU getLUDecomposition() {
        return new LU();
    }
    
    @Override
    public Gauss getGaussInverter() {
        return new Gauss();
    }
    
    /**
     * Checks if this matrix is Hermitian, i.e. the values at <code>[i][j]</code> are the complex conjugate 
     * of the values at <code>[j][i]</code> for all elements <code>i != j</code>.
     * 
     * @return      <code>true</code> if this matrix is Hermitian. Ortherwise <code>false</code>.
     */
    public boolean isHermitian() {
        for(int i=rows(); --i >= 0; ) for(int j=cols(); --j > i; ) {
            Complex a = get(i, j);
            Complex b = get(j, i);
            
            if(a.re() != b.re()) return false;
            if(a.im() + b.im() != 0.0) return false;
            
            return false;
        }
        return true;
    }
    

    @Override
    public ComplexMatrix subspace(int[] rows, int[] cols) {
        return (ComplexMatrix) super.subspace(rows, cols);    
    }

    @Override
    public ComplexMatrix subspace(int fromRow, int fromCol, int toRow, int toCol) {
        return (ComplexMatrix) super.subspace(fromRow, fromCol, toRow, toCol);
    }

    @Override
    public ComplexMatrix subspace(Index2D from, Index2D to) {
        return (ComplexMatrix) super.subspace(from, to);
    }
    

    /**
     * Gets the real part of this matrix as a simple 2D Java array.
     * 
     * @return  The real part of this matrix.
     */
    public double[][] getRealPart() {
        double[][] dst = new double[rows()][cols()];
        getRealPart(dst);
        return dst;
    }

    /**
     * Gets the imaginary part of this matrix as a simple 2D Java array.
     * 
     * @return  The imaginary part of this matrix.
     */
    public double[][] getImaginaryPart() {
        double[][] dst = new double[rows()][cols()];
        getImaginaryPart(dst);
        return dst;
    }
    
    
    /**
     * Gets the real part of this matrix as a new matrix.
     * 
     * @return      The real part of this matrix.
     */
    public final Matrix re() {
        return new Matrix(getRealPart());
    }

    /**
     * Gets the imaginary part of this matrix as a new matrix.
     * 
     * @return      The imaginary part of this matrix.
     */
    public final Matrix im() {
        return new Matrix(getImaginaryPart());
    }

    /**
     * Sets the real part of this matrix to the specified values.
     * 
     * @param re                2D array of values to be used for the real parts.
     * @throws ShapeException   If the supplied array has a shape or size different from this matrix.
     */
    public void setRealPart(double[][] re) throws ShapeException {
        assertSize(re.length, re[0].length);
        for(int i=rows(); --i >= 0; ) for(int j = cols(); --j >= 0; ) get(i, j).setRealPart(re[i][j]);
    }

    /**
     * Sets the real part of this matrix to the specified values.
     * 
     * @param re                2D array of values to be used for the real parts.
     * @throws ShapeException   If the supplied array has a shape or size different from this matrix.
     */
    public void setRealPart(float[][] re) throws ShapeException {
        assertSize(re.length, re[0].length);
        for(int i=rows(); --i >= 0; ) for(int j = cols(); --j >= 0; ) get(i, j).setRealPart(re[i][j]);
    }

    /**
     * Sets the real part of this matrix to the specified data. The data must be either <code>double[][]</code>
     * or <code>float[][]</code> type or else implement {@link ViewableAsDoubles} with 
     * {@link ViewableAsDoubles#viewAsDoubles()} returning a <code>double[][]</code>.
     * 
     * @param data                          A suitable representation of 2D real values.
     * @throws IllegalArgumentException     If the type of data is not supported by this method.
     */
    public void setRealPart(Object data) throws IllegalArgumentException {
        if(data instanceof double[][]) setRealPart((double[][]) data);
        else if(data instanceof float[][]) setRealPart((float[][]) data);
        else if(data instanceof ViewableAsDoubles) setRealPart(((ViewableAsDoubles) data).viewAsDoubles());
        else throw new IllegalArgumentException(" Cannot convert " + data.getClass().getSimpleName() + " into double[][] format.");   
    }
    
    /**
     * Sets the imaginary part of this matrix to the specified values.
     * 
     * @param im                2D array of values to be used for the imaginary parts.
     * @throws ShapeException   If the supplied array has a shape or size different from this matrix.
     */
    public void setImaginaryPart(double[][] im) throws ShapeException {
        assertSize(im.length, im[0].length);
        for(int i=rows(); --i >= 0; ) for(int j = cols(); --j >= 0; ) get(i, j).setImaginaryPart(im[i][j]);
    }

    /**
     * Sets the imaginary part of this matrix to the specified values.
     * 
     * @param im                2D array of values to be used for the imaginary parts.
     * @throws ShapeException   If the supplied array has a shape or size different from this matrix.
     */
    public void setImaginaryPart(float[][] im) throws ShapeException {
        assertSize(im.length, im[0].length);
        for(int i=rows(); --i >= 0; ) for(int j = cols(); --j >= 0; ) get(i, j).setImaginaryPart(im[i][j]);
    }
    
    /**
     * Sets the imaginary part of this matrix to the specified data. The data must be either <code>double[][]</code>
     * or <code>float[][]</code> type or else implement {@link ViewableAsDoubles} with 
     * {@link ViewableAsDoubles#viewAsDoubles()} returning a <code>double[][]</code>.
     * 
     * @param data                          A suitable representation of 2D real values.
     * @throws IllegalArgumentException     If the type of data is not supported by this method.
     */
    public void setImaginaryPart(Object data) {
        if(data instanceof double[][]) setImaginaryPart((double[][]) data);
        else if(data instanceof float[][]) setImaginaryPart((float[][]) data);
        else if(data instanceof ViewableAsDoubles) setImaginaryPart(((ViewableAsDoubles) data).viewAsDoubles());
        else throw new IllegalArgumentException(" Cannot convert " + data.getClass().getSimpleName() + " into double[][] format.");   
    }

    public void getRealPart(double[][] dst) throws ShapeException {
        assertSize(dst.length, dst[0].length);
        for(int i=rows(); --i >= 0; ) for(int j = cols(); --j >= 0; ) dst[i][j] = get(i, j).re();
    }

    public void getRealPart(float[][] dst) throws ShapeException {
        assertSize(dst.length, dst[0].length);
        for(int i=rows(); --i >= 0; ) for(int j = cols(); --j >= 0; ) dst[i][j] = (float) get(i, j).re();
    }

    public void getImaginaryPart(double[][] dst) throws ShapeException {
        assertSize(dst.length, dst[0].length);
        for(int i=rows(); --i >= 0; ) for(int j = cols(); --j >= 0; ) dst[i][j] = get(i, j).im();
    }

    public void getImaginaryPart(float[][] dst) throws ShapeException {
        assertSize(dst.length, dst[0].length);
        for(int i=rows(); --i >= 0; ) for(int j = cols(); --j >= 0; ) dst[i][j] = (float) get(i, j).im();
    }


    @Override
    public void conjugate() {
        for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) get(i, j).conjugate(); 
    }
    
    /**
     * Gets the conjugate of this matrix as a new matrix.
     * 
     * @return  A new, fully independent, matrix containing the complex conjugate of this matrix.
     */
    public final ComplexMatrix getConjugate() {
        ComplexMatrix C = copy();
        C.conjugate();
        return C;
    }

    /**
     * Gets the conjugate transpose (a.k.a. Hermitian transpose) of this matrix as a new matrix.
     * 
     * @return  A new, fully independent, matrix containg the Hermitian transpose of this matrix.
     */
    public ComplexMatrix getConjugateTranspose() {
        ComplexMatrix transpose = getTranspose();
        transpose.conjugate();
        return transpose;
    }

    @Override
    public ComplexMatrix dot(MatrixAlgebra<?, ? extends Complex> B) {
        return (ComplexMatrix) super.dot(B);
    }
    
    @Override
    public ComplexMatrix dot(Matrix B) {
        return (ComplexMatrix) super.dot(B);
    }
    
    @Override
    public ComplexMatrix dot(DiagonalMatrix.Real B) {
        return (ComplexMatrix) B.dot(this);
    }
    
    /**
     * Returns the dot product of this matrix and a complex diagonal matrix.
     * 
     * @param B     A complex diagonal matrix
     * @return      The product of this matrix and the argument.
     */
    public ComplexMatrix dot(DiagonalMatrix.Complex B) {
        return B.dot(this);
    }

    @Override
    public ComplexVector dot(double... v) {
        return (ComplexVector) super.dot(v);
    }
    
    @Override
    public ComplexVector dot(float... v) {
        return (ComplexVector) super.dot(v);
    }

    @Override
    public ComplexVector dot(RealVector v) {
        return (ComplexVector) super.dot(v);
    }
    
    @Override
    public ComplexVector dot(MathVector<? extends Complex> v) {
        return (ComplexVector) super.dot(v);
    }
    
    
    @Override
    public ComplexMatrix getTranspose() {        
        return (ComplexMatrix) super.getTranspose();
    }

    @Override
    public ComplexMatrix getInverse() {        
        return (ComplexMatrix) super.getInverse();
    }

    @Override
    public ComplexMatrix getGaussInverse() {
        return (ComplexMatrix) super.getGaussInverse();
    }

    @Override
    public ComplexMatrix getLUInverse() {
        return (ComplexMatrix) super.getLUInverse();
    }

    @Override
    public final void scale(Complex x) { multiplyBy(x); }


    @Override
    public void multiplyBy(Complex factor) {
        for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) get(i, j).multiplyBy(factor);
    }

    /**
     * Multiplies all elements in this matrix by the square-root of -1, i.e. by <i>i</i>.
     */
    public void multiplyByI() {
        for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) get(i, j).multiplyByI();
    }

    @Override
    public void add(double re, double im) {
        for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) get(i, j).add(re, im);
    }
    
    @Override
    public void add(Complex z) {
        add(z.re(), z.im());
    }

    @Override
    public void subtract(Complex z) {
        for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) get(i, j).subtract(z);
    }

    
    public static ComplexMatrix fromReal(double[][] data) {
        ComplexMatrix M = new ComplexMatrix(data.length, data[0].length);
        M.setRealPart(data);
        return M;
    }
    
    public static ComplexMatrix fromReal(float[][] data) {
        ComplexMatrix M = new ComplexMatrix(data.length, data[0].length);
        M.setRealPart(data);
        return M;
    }

    public static ComplexMatrix fromReal(Object data) {
       if(data instanceof double[][]) return fromReal((double[][]) data);
       if(data instanceof float[][]) return fromReal((float[][]) data);
       if(data instanceof ViewableAsDoubles) return fromReal(((ViewableAsDoubles) data).viewAsDoubles());
       throw new IllegalArgumentException(" Cannot convert " + data.getClass().getSimpleName() + " into double[][] format.");    
    }
    
    
    
    
    /**
     * The LU decomposition of the parent complex matrix.
     * 
     * @author Attila Kovacs
     *
     */
    public class LU extends ObjectMatrix<Complex>.LU {

        /**
         * Constructs an LU decomposition of the parent matrix.
         * 
         * @throws SquareMatrixException    If the matrix argument is not of the required square shape for decomposition
         * @throws SingularMatrixException  If the matrix argument is singular (degenerate)
         */
        public LU()throws SquareMatrixException, SingularMatrixException  {}

        /**
         * Constructs an LU decomposition of the parent matrix, using the specified small
         * numerical value that is used for substituting zeroes in the arithmetic.
         * 
         * @param tinyValue     A very small numerical value to replce zeroes in the matrix
         *                      for algorithmic stability.
         * @throws SquareMatrixException    If the matrix argument is not of the required square shape for decomposition
         * @throws SingularMatrixException  If the matrix argument is singular (degenerate)
         */
        public LU(double tinyValue) throws SquareMatrixException, SingularMatrixException {
            super(tinyValue);
        }
        
        @Override
        public ComplexMatrix getMatrix() {
            return (ComplexMatrix) super.getMatrix();
        }


        @Override
        public ComplexMatrix getInverseMatrix() {
            return (ComplexMatrix) super.getInverseMatrix();
        }
        
        @Override
        public ComplexVector solveFor(MathVector<? extends Complex> y) {
            return (ComplexVector) super.solveFor(y);
        }        
    }

    /**
     * The Gauss inverter object for the parent complex matrix.
     * 
     * @author Attila Kovacs
     *
     */
    public class Gauss extends ObjectMatrix<Complex>.Gauss {

        @Override
        public ComplexMatrix getMasterInverse() {
            return (ComplexMatrix) super.getMasterInverse();
        }

        @Override
        public ComplexMatrix getInverseMatrix() {
            return (ComplexMatrix) super.getInverseMatrix();
        }

        @Override
        public ComplexVector solveFor(MathVector<? extends Complex> y) {
            return getMasterInverse().dot(y);
        }

    }
    
    /**
     * The Jacobi transform of the parent complex matrix.
     * 
     * @author Attila Kovacs
     *
     */
    public class JacobiTransform implements EigenSystem<Complex, Double> {
        RealVector eigenValues;
        ComplexMatrix B, iB;
        
        private JacobiTransform() throws SquareMatrixException, SymmetryException, ConvergenceException {
            this(100);
        }
        
        private JacobiTransform(int maxIterations) throws SquareMatrixException, SymmetryException, ConvergenceException {
               transform(ComplexMatrix.this, maxIterations);
        }
        
        /**
         * Based on Numerical Recipes for C (second edition) Section 11.4. It represents the Hermitian NxN complex matrix
         * as a real 2Nx2N real matrix, with a slight overhead (2x) in size and perhaps less than that in speed.
         * 
         * @param M                         Matrix to transform
         * @param maxIterations             Maximum number of iterations (~100 is typically sufficient)
         * @throws SquareMatrixException    If the input matrix is not a square matrix
         * @throws SymmetryException        If the input matrix is not a symmetric matrix
         * @throws ConvergenceException     If the transformation is not complete within the set ceiling for iterations.
         */
        private void transform(ComplexMatrix M, int maxIterations)  throws SquareMatrixException, SymmetryException, ConvergenceException{
            if(!M.isSquare()) throw new SquareMatrixException();
            if(!M.isHermitian()) throw new SymmetryException();
            
            int n = M.rows();
            
            Matrix C = new Matrix(2 * n, 2 * n);
            Matrix re = new Matrix(n, n);
            Matrix im = re.copy();
            
            M.getRealPart(re.getData());
            M.getImaginaryPart(im.getData());
            
            C.paste(re, 0, 0);
            C.paste(re, n, n);
            C.paste(im, n, 0);
            
            im.scale(-1.0);
            C.paste(im, 0, n);
            
            EigenSystem<Double, Double> e = C.new JacobiTransform(maxIterations);
            
            RealVector l = (RealVector) e.getEigenValues();
            RealVector[] v = (RealVector[]) e.getEigenVectors();
            
            eigenValues = new RealVector(n);
            B = M.getMatrixInstance(n, n, true);
            
            for(int i=n; --i >= 0; ) {
                eigenValues.setComponent(i, l.getComponent(i));
                for(int j=n; --j >= 0; ) B.get(i, j).set(v[i].getComponent(j), v[n+i].getComponent(j));
            }
        }
        
        @Override
        public Double getDeterminant() {
            double D = 1.0;
            for(int j=eigenValues.size(); --j >= 0; ) D *= eigenValues.getComponent(j);
            return D;
        }
        
        @Override
        public RealVector getEigenValues() {
           return eigenValues.copy();
        }

        @Override
        public ComplexVector[] getEigenVectors() {
            ComplexVector[] e = new ComplexVector[eigenValues.size()];
            for(int i=e.length; --i >= 0; ) {
                e[i] = new ComplexVector(eigenValues.size());
                B.copyColumnTo(i, e[i]);
            }
            return e;
        }
    
        @Override
        public ComplexMatrix toEigenBasis() {
            return B.copy();
        }
        
        @Override
        public ComplexMatrix fromEigenBasis() {
            if(iB == null) iB = B.getInverse();
            return iB.copy();
        }
        
        @Override
        public ComplexVector toEigenBasis(MathVector<? extends Complex> v) {
            return B.dot(v);
        }
        
        @Override
        public ComplexVector fromEigenBasis(MathVector<? extends Complex> v) {
            if(iB == null) iB = B.getInverse();
            return iB.dot(v);
        }
        
        @Override
        public DiagonalMatrix.Real getDiagonalMatrix() {
            return new DiagonalMatrix.Real(Arrays.copyOf(eigenValues.getData(), eigenValues.size()));
        }
        
        public ComplexMatrix getReconstructedMatrix() {
            if(iB == null) iB = B.getInverse();
            return B.dot(new DiagonalMatrix.Real(eigenValues.getData())).dot(iB);
        }
        
    }
    
}
