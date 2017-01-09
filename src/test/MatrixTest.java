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
// (C)2008 Attila Kovacs <attila@submm.caltech.edu>

package test;

import java.util.Random;

import jnum.Util;
import jnum.math.matrix.LUDecomposition;
import jnum.math.matrix.Matrix;
import jnum.math.matrix.SVD;
import jnum.math.matrix.SquareMatrix;



// TODO: Auto-generated Javadoc
/**
 * The Class MatrixTest.
 */
public class MatrixTest {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		double[][] data = { { 1, 0, 1 }, { 1, 1, 0}, {0, 0, 1} };
		
		Random random = new Random();
		for(int i=data.length; --i >=0; ) for(int j=data[i].length; --j >=0; ) data[i][j] = random.nextGaussian();
		
		SquareMatrix M = new SquareMatrix(data);
		System.out.println(M.toString(Util.f1));
	
		SquareMatrix I = M.getInverse();
		System.out.println("inverse: " + I.toString(Util.f1));
	
		Matrix P = Matrix.product(M, I);
		System.out.println("M * M-1: " + P.toString(Util.f1));
		
		SVD decomposed = new SVD(M);
		System.out.println("SVD decomp: " + decomposed.getMatrix().toString(Util.f1));
		System.out.println("SVD inverse: " + decomposed.getInverse().toString(Util.f1));
		
		LUDecomposition LU = new LUDecomposition(M);
		System.out.println("LU inverse: " + LU.getInverse().toString(Util.f1));
		
		System.out.println("Gauss inverse: " + M.getGaussInverse().toString(Util.f1));
		
	}
	
}
