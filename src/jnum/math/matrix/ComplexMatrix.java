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


import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.ParsePosition;

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


public class ComplexMatrix extends ObjectMatrix<Complex> implements ComplexScaling, ComplexConjugate, Multiplication<Complex>, ComplexAddition {

    private static final long serialVersionUID = 8842229635231169797L;

    public ComplexMatrix(int rows, int cols) {
        super(Complex.class, rows, cols);
    }

    public ComplexMatrix(int size) {
        super(Complex.class, size, size);
    }

    public ComplexMatrix(Complex[][] data) throws ShapeException {
        super(data);
    }

    public ComplexMatrix(Matrix M) {
        this(M.rows(), M.cols());
        for(int i=rows(); --i >= 0; ) for(int j = cols(); --j >= 0; ) get(i, j).set(M.get(i, j), 0.0);  
    }
    
    public ComplexMatrix(String text, ParsePosition pos) throws ParseException, Exception {
        super(Complex.class, text, pos);
    }

    @Override
    public ComplexMatrix getMatrixInstance(int rows, int cols, boolean initialize) {
        if(initialize) return new ComplexMatrix(rows, cols);
        return new ComplexMatrix((Complex[][]) Array.newInstance(Complex.class, new int[] { rows, cols }));
    }

    @Override
    public ComplexMatrix clone() {
        return (ComplexMatrix) super.clone();
    }

    @Override
    public ComplexMatrix copy() {
        return (ComplexMatrix) super.copy();
    }
    
    public JacobiTransform getJacobiTransform() throws SquareMatrixException, SymmetryException, ConvergenceException {
        return new JacobiTransform();
    }
    
    public EigenSystem<Complex, ?> getEigenSystem() throws SquareMatrixException, SymmetryException, ConvergenceException {
        return new JacobiTransform();
    }
    
    @Override
    public LU getLUDecomposition() {
        return new LU();
    }
    
    @Override
    public Gauss getGaussInverter() {
        return new Gauss();
    }
    
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


    public double[][] getRealPart() {
        double[][] dst = new double[rows()][cols()];
        getRealPart(dst);
        return dst;
    }

    public double[][] getImaginaryPart() {
        double[][] dst = new double[rows()][cols()];
        getImaginaryPart(dst);
        return dst;
    }


    public void setRealPart(double[][] re) throws ShapeException {
        assertSize(re.length, re[0].length);
        for(int i=rows(); --i >= 0; ) for(int j = cols(); --j >= 0; ) get(i, j).setRealPart(re[i][j]);
    }

    public void setRealPart(float[][] re) throws ShapeException {
        assertSize(re.length, re[0].length);
        for(int i=rows(); --i >= 0; ) for(int j = cols(); --j >= 0; ) get(i, j).setRealPart(re[i][j]);
    }

    public void setRealPart(Object data) {
        if(data instanceof double[][]) setRealPart((double[][]) data);
        else if(data instanceof float[][]) setRealPart((float[][]) data);
        else if(data instanceof ViewableAsDoubles) setRealPart(((ViewableAsDoubles) data).viewAsDoubles());
        else throw new IllegalArgumentException(" Cannot convert " + data.getClass().getSimpleName() + " into double[][] format.");   
    }
    
    public void setImaginaryPart(double[][] im) throws ShapeException {
        assertSize(im.length, im[0].length);
        for(int i=rows(); --i >= 0; ) for(int j = cols(); --j >= 0; ) get(i, j).setImaginaryPart(im[i][j]);
    }

    public void setImaginaryPart(float[][] im) throws ShapeException {
        assertSize(im.length, im[0].length);
        for(int i=rows(); --i >= 0; ) for(int j = cols(); --j >= 0; ) get(i, j).setImaginaryPart(im[i][j]);
    }
    
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


    /* (non-Javadoc)
     * @see kovacs.math.ComplexConjugate#conjugate()
     */
    @Override
    public void conjugate() {
        for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) get(i, j).conjugate(); 
    }


    public ComplexMatrix getHermitian() {
        ComplexMatrix transpose = getTranspose();
        transpose.conjugate();
        return transpose;
    }


    public static ComplexMatrix product(ComplexMatrix A, ComplexMatrix B) {
        ComplexMatrix product = new ComplexMatrix(A.rows(), B.cols());
        product.setProduct(A, B);
        return product;
    }

    public ComplexMatrix dot(ComplexMatrix B) {
        return (ComplexMatrix) super.dot(B);
    }
    
    @Override
    public ComplexMatrix dot(Matrix B) {
        return (ComplexMatrix) super.dot(B);
    }

    @Override
    public ComplexVector dot(MathVector<Complex> v) {
        return (ComplexVector) super.dot(v);
    }
    
    @Override
    public ComplexVector dot(RealVector v) {
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

    /* (non-Javadoc)
     * @see kovacs.math.Multiplication#multiplyBy(java.lang.Object)
     */
    @Override
    public void multiplyBy(Complex factor) {
        for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) get(i, j).multiplyBy(factor);
    }


    public void multiplyByI() {
        for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) get(i, j).multiplyByI();
    }

    /* (non-Javadoc)
     * @see kovacs.math.ComplexAddition#addComplex(kovacs.math.Complex)
     */
    @Override
    public void addComplex(Complex z) {
        for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) get(i, j).addComplex(z);
    }

    /* (non-Javadoc)
     * @see kovacs.math.ComplexAddition#subtractComplex(kovacs.math.Complex)
     */
    @Override
    public void subtractComplex(Complex z) {
        for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) get(i, j).subtractComplex(z);
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
    
    public class LU extends ObjectMatrix<Complex>.LU {

        @Override
        public ComplexMatrix getMatrix() {
            return (ComplexMatrix) super.getMatrix();
        }


        @Override
        public ComplexMatrix getInverseMatrix() {
            return (ComplexMatrix) super.getInverseMatrix();
        }
        
        @Override
        public ComplexVector solveFor(MathVector<Complex> y) {
            return (ComplexVector) super.solveFor(y);
        }        
    }

    public class Gauss extends ObjectMatrix<Complex>.Gauss {

        @Override
        public ComplexMatrix getI() {
            return (ComplexMatrix) super.getI();
        }

        @Override
        public ComplexMatrix getInverseMatrix() {
            return (ComplexMatrix) super.getInverseMatrix();
        }

        @Override
        public ComplexVector solveFor(MathVector<Complex> y) {
            return getI().dot(y);
        }

    }
    
    public class JacobiTransform implements EigenSystem<Complex, Double> {
        RealVector eigenValues;
        ComplexVector[] eigenVectors;
        
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
            
            int n=  M.rows();
            
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
            eigenVectors = new ComplexVector[n];
            
            for(int i=n; --i >= 0; ) {
                eigenValues.setComponent(i, l.getComponent(i));
                ComplexVector ei = new ComplexVector(n);
                for(int j=n; --j >= 0; ) ei.getComponent(j).set(v[i].getComponent(j), v[n+i].getComponent(j));
                eigenVectors[i] = ei;
            }
        }
        
        @Override
        public RealVector getEigenValues() {
           return eigenValues.copy();
        }

        @Override
        public ComplexVector[] getEigenVectors() {
            ComplexVector[] e = new ComplexVector[eigenVectors.length];
            for(int i=e.length; --i >= 0; ) e[i] = eigenVectors[i].copy();
            return e;
        }
    
    }
    
}
