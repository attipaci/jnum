package jnum.math.matrix;

import jnum.data.image.Index2D;
import jnum.math.AbsoluteValue;
import jnum.math.AbstractAlgebra;
import jnum.math.LinearAlgebra;
import jnum.math.Metric;
import jnum.math.Normalizable;

/**
 * A container class for temporarilu holding and manipulating a matrix element of generic type. Wrapping
 * the underlying matrix element in this way allows to define a common set of mathematical operations on
 * the matrix entries, regardless if these are primitives (such as double values stored in {@link Matrix} 
 * or object references, such as in {@link ObjectMatrix} or {@link ComplexMatrix}).
 * 
 * @author Attila Kovacs
 *
 * @param <T>       The generic type of matrix entries.
 */
public abstract class MatrixElement<T> implements LinearAlgebra<T>, AbstractAlgebra<T>, Metric<T>, AbsoluteValue , Normalizable {

        /**
         * Sets a new, freshly initialized, underlying matrix elements to operate on.
         * 
         * @return itself
         */
        public abstract MatrixElement<T> fresh();
    
        /**
         * Gets the underlying value stored in this matrix element object. This may be a reference or
         * a primitive value.
         * 
         * @return
         */
        public abstract T value();
        
        /**
         * Gets an independent copy of the underlying matrix element, such that changes to the returned
         * value do not affect the orgininal matrix element represented. This is always true for
         * primitive type elements (such as in {@link Matrix}, and is therefore only improtant for
         * elements of {@link ObjectMatrix} type matrices, where normally references are passed by
         * default.
         * 
         * @return
         */
        public abstract T copyOfValue();
        
        /**
         * Sets the matrix entry at (i, j) to the value wrapped in this element container. This is
         * mainly important when manipulating {@link Matrix} objects with primitives as underlying
         * elements. For example, consider the code for flipping the sign of all entries in
         * an <code>AbstractMatrix&lt;T&gt; M</code>, regardless whether it has pritive or {@link Object}
         * type entries:
         * 
         * <pre>
         *   MatrixElement&lt;?&gt; e = M.getElementInstance();
         *   
         *   for(int j=M.rows(); --j &gt;= 0; ) for(int j=cols(); --j &gt;= 0; ) {
         *      e.from(i, j).scale(-1.0);
         *      a.applyTo(i, j);
         *   }
         * </pre>
         * 
         * For {@link ObjectMatrix} types, <code>e.from(i, j)</code> returns a reference to the
         * underlying element object at (i, j), which will then be scaled in place. Thus, for
         * matrices with Object references in entry slots, the <code>applyTo(i, j)</code> call 
         * seems unnecessary. However, for {@link Matrix} types, which hold primitives, the
         * <code>from(i, j)</code> will return an independent (boxed) primitive double value,
         * the subsequent scaling of which does not change the element stored at (i, j) in the
         * matrix. Therefore, the <code>applyTo(i, j)</code> is necessary to reinsert the
         * manipulated value into the primitive storage array of the {@link Matrix}. 
         * There is no downside to calling <code>applyTo()</code> even if the operations did
         * already take place on the underlying entry object. It is therefore good practice
         * to always call <code>applyTo()</code> regardless of what type of matrix we
         * operate on with the MatrixElement wrapper.
         * 
         * 
         * @param i     Row index of selement to set.
         * @param j     Column index of element to set.
         */
        public abstract void applyTo(int i, int j);
        
        /**
         * Wraps the specified underlying element in this container. The wrapped value may be a reference
         * or an independent primitive value. Thus, operations on the wrapped value may or
         * may not directly change a value stored in the matrix, depending on the matrix object type,
         * or if the value was in fact borrowed from a matrix.
         * When you do not intend to change the matrix, you should use {@link #copy(int, int)} instead
         * to operate on an independent copy of the underlying value. On the flipside, if you do
         * intend to modify the value stored in the matrix itself, then you should call
         * {@link #applyTo(int, int)} at the end of the operations to ensure that the underlying
         * data in the matrix carries the result of the operations perfomed on the element
         * represented in this object.
         * 
         * @param value     The underlying generic type value to wrap in this element. 
         * @return  itself
         */
        public abstract MatrixElement<T> from(T value);
        
        /**
         * Wraps the element contained at (i, j) in the matrix. The wrapped value may be a reference
         * or an independent primitive value. Thus, operations on the wrapped value may or
         * may not directly change the value stored in the matrix, depending on the matrix object type.
         * When you do not intend to change the matrix, you should use {@link #copy(int, int)} instead
         * to operate on an independent copy of the underlying value. On the flipside, if you do
         * intend to modify the value stored in the matrix itself, then you should call
         * {@link #applyTo(int, int)} at the end of the operations to ensure that the underlying
         * data in the matrix carries the result of the operations perfomed on the element
         * represented in this object.
         * 
         * @param i     Row index of matrix entry to wrap
         * @param j     Column index of matrix entry to wrap
         * @return  itself
         */
        public abstract MatrixElement<T> from(int i, int j);
        
        /**
         * Same as {@link from(int, int)} but with an {@link Index2D} object specifying the row, col
         * indices to wrap in this elemment. Please refer to {@link from(int, int)} on how to use 
         * this call safely. 
         * 
         * @param index (row, col) index of the matrix entry to wrap.
         * @return  itself
         */
        public final MatrixElement<T> from(Index2D index) {
            from(index.i(), index.j());
            return this;
        }
          
        /**
         * Wrap an independent copy of the matrix element at (i, j), s.t. operations on the wrapped
         * element cannot end up changing the value stored in the matrix at (i, j), regardless
         * of whether the matrix stores primitives or object types.
         * 
         * @param i     Row index of matrix entry to wrap
         * @param j     Column index of matrix entry to wrap
         * @return  itself
         */
        public abstract MatrixElement<T> copy(int i, int j);
 
        /**
         * Like {@link copy(int, int)} except with an {@link Index2D} object specifying the
         * (row, col) index of the matrix element, whose copy to wrap.
         * 
         * @param index     (row, col) index of the matrix entry, whose copy to wrap.
         * @return  itself
         */
        public final MatrixElement<T> copy(Index2D index) {
            copy(index.i(), index.j());
            return this;
        }
        
        /**
         * Wrap an independent copy of the underlying generic type value, s.t. operations on the wrapped
         * element cannot end up changing the original value, regardless of whether it is a primitive or
         * an object type.
         * 
         * @param value     The value, whose copy to to wrap
         * @return  itself
         */
        public abstract MatrixElement<T> copy(T value);
 
        
    }