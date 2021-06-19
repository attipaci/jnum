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

import jnum.ShapeException;
import jnum.ViewableAsDoubles;
import jnum.math.Complex;
import jnum.math.ComplexAddition;
import jnum.math.ComplexConjugate;
import jnum.math.ComplexScaling;
import jnum.math.Multiplication;




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

    public ComplexMatrix(String text, ParsePosition pos) throws ParseException, Exception {
        super(Complex.class, text, pos);
    }

    @Override
    protected ComplexMatrix createMatrix(int rows, int cols, boolean initialize) {
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

    @Override
    public ComplexMatrix dot(AbstractMatrix<? extends Complex> B) {
        return (ComplexMatrix) super.dot(B);
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
    
}
