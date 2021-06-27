/**
 * @author Attila Kovacs <attila@sigmyne.com>
 * 
 * <h2>JNUM Matrix Package</h2>
 * 
 * 
 * This package provides intuitive and versatile support for matrices (bot real and complex, as well as 
 * other types as long as they provide the relevant interfaces for generic matrix elements).
 * 
 * Some of the features of this package:
 * 
 * <ul>
 * <li>Support for real {@link Matrix}, {@link ComplexMatrix} and {@link ObjectMatrix} for generic type elements</li>
 * <li>Support for corresponding {@link RealVector}, {@link ComplexVector} and {@link ObjectVector} types.</li>
 * <li>Diagonal matrices provide more efficient implementation when there are no off-diagonal elements</li>
 * <li>Matrix inversion for all types of matrices</li>
 * <li>LU decomposition and Gauss-Jordan elimination for all matrix types</li>
 * <li>Singular value decomposition (SVD) for real {@link Matrix} types.</li>
 * <li>dot products, including between matrices of different types (e.g. you can always dot a real-valued
 *     matrix with any matrix. And you can dot diagonal and regular matrices just the same.</li>
 * <li>Finding of eigenvalues and eigenvectors for symmetric / Hermitian matrices via {@link EigenSystem},
 *     which also provide basis transforms to/from the diagonalized form.</li>
 * <li>Vector bases for all types, with support for orthogonalization, normalization, and orthonormalization.</li>
 * </ul>
 *
 * 
 * <h3>Getting started with real-valued matrices</h3>
 * 
 * Matrices in this package fall into two broad types, (1) full-populated matrices which have a true 2D backing
 * array (such as <code>double[][]</code> or <code>Complex[][]</code>) with a slot for every element, and (2)
 * {@link DiagonalMatrix} types, which have storage only for the non-zero diagonal elements (hence e.g. <code>
 * double[]</code> or <code>Complex[]</code>. Both packages implememt the interfaces {@link MatrixAlgebra} (for 
 * general <i>N</i> by <i>M</i> matrices and {@link SquareMatrixAlgebra} specific for square matrices.
 * 
 * All fully populated 2D matrices derive from the {@link AbstractMatrix} superclass, which have two principal
 * implementations: (1) {@link Matrix} for matrices with primitive type backing array (specifically <code>
 * double[][]</code>, and (2) {@link ObjectMatrix}, in which each element is a proper (not boxed) Java object,
 * which satisfies certain requirements by implementing a set of interfaces required for supporting matrix
 * algebra. Thus {@link ComplexMatrix} is a specific implementation og {@link ObjectMatrix} for 
 * {@link java.lang.Complex} type elements.
 * 
 *  So, let's start by creating a 3x4 matrix:
 *  
 *  <pre>
 *   Matrix M = new Matrix(3, 4);
 *  </pre>
 *  
 *  The above creates a 3x4 matrix with all zero elements initially. Alternatively, we can create the
 *  another 4x3 matrix with the <code>double[][]</code> backing array that holds it's initial data:
 *  
 *  <pre>
 *   Matrix B = new Matrix(new double[] { { 1, 0, 1, 2}, {2, -3, 1, 4}, {0, 1, 0, 4} });
 *  </pre>
 *  
 *  Matrices stay fixed size throughout their life cycle. That is <b>B</b> will remain a 3x4
 *  matrix as long as it exists, but we can make changes to it's elements, or even swap out the
 *  backing array completely, as long as the new backing array has the same size and shape as
 *  before (3x4). For example:
 *  
 *  <pre>
 *   M.set(0, 3, -6.5);
 *   M.addIdentity(2.2);
 *   M.swap(0, 1, 2, 1);
 *   M.swapRows(1, 2);
 *   M.setColumn(1, new double[] { 3, -3, 2 });
 *  </pre>
 *  
 *  Matrices are most often used for transforming vectors. And, {@link Matrix} can operate a several 
 *  vector types, such as:
 *  
 *  <pre>
 *   double[] d1 = B.dot(new double[] { 1.5, 2.5, -1.0 });
 *   float[] f1 = B.dot(new float[] { 1.5, 2.5, -1.0 });
 *   
 *   RealVector v0 = ...
 *   RealVector v1 = M.dot(v0);
 *   
 *   B.dot(new Vector3D(0.0, 1.0, 0.0), v1);
 *  </pre>
 *  
 *  Just to give a few examples. We can also calculate the dot product <b>M</b> <i>dot</i> <b>B</b>:
 *  
 *  <pre>
 *   Matrix P = M.dot(B)
 *  </pre>
 *  
 *  You can print the dot product, e.g. with the your desired number format, such as using 3 significant
 *  figures using {@link Util#s3}:
 *  
 *  <pre>
 *   System.out.println(" M.B = " + P.toString(Util.s3));
 *  </pre>
 *  
 *  And, since <b>P</b> is a square matrix, we can calculate it's inverse, simply as:
 *  
 *  <pre>
 *   Matrix invP = P.getInverse();
 *  <pre>
 *  
 *  The above calculates the inverse using an LU decomposition (which is fastest). But you can also
 *  calculate the inverse using Gauss-Jordan elimination via {@link Matrix#getGaussInverse()}
 *  or via singular-value decomposition {@link Matrix#getSVDInverse()}.
 *  
 *  While at it, instead of shooting directly for the inverse, you can get one of the intermediate
 *  objects, such a the matrix's LU decomposition (via {@link Matrix#getLUDecomposition()}),
 *  Gauss inverter {via {@link Matrix#getGaussInverter()}, or SVD (via {@link Matrix#getSVD()}.
 *  These objects can give you the inverse again and again at no significant extra cost, and provide
 *  additional useful functions, such as getting the determinant ({@link LUDecomposition#getDeterminant()})
 *  or solve the matrix equation <b>y</b> = <b>M</b> <i>dot</i> <b>x</b>, or get the rank 
 *  ({@link GaussInverter#getRank()} of the matrix at little extra computational cost.
 *  
 *  Since <b>P</b> is a square matrix, we can also find its eigenvalues and eigenvectors via 
 *  {@link Matrix#getEigenSystem} or via {@link Matrix#getJacobiTransform()}:
 *  
 *  <pre>
 *   EigenSystem<Double, Double> e = P.getEigenSystem();
 *   RealVector v = e.getEigenValues();
 *   VectorBasis b = e.getEigenVectors();
 *   Matrix B = e.toEigenBasis();       # Matrix that converts original vector to the eigen basis
 *   Matrix iB = e.fromEigenBasis();    # Matrix that coverts from the eigen basis back to the original basis.
 *  </pre>
 *  
 * 
 * <h3>Matrix specific exceptions</h3>
 * 
 * There is no separate class for square matrices. Instead operations specific to square matrices will throw
 * a {@link SquareMatrixException} if attempted on a non-square matrix. Similarly, any operation that requires
 * the argument to match the size of matrix object will throw a {@link ShapeException} if called
 * with a argument of the wrong shape or size.
 * 
 * 
 * 
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
 *  
 * <h3>Matrices of other generic types</h3>
 * 
 * <h3>Matrix inversion and solving matrix equations</h3>
 * 
 * <h3>Eigenvalues and eigenvectors</h3>
 * 
 * <h3>Efficient manipulation of matrix elements</h3>
 * 
 * <h3>Diagonal Matrices</h3>
 * 
 */
package jnum.math.matrix;

