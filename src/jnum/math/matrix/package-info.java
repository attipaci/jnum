package jnum.math.matrix;

/**
 * @author Attila Kovacs
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
 * {@link jnum.math.Complex} type elements.
 * 
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
 */
