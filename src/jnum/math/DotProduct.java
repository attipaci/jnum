package jnum.math;


/**
 * An interface for objects that implement a dot product, such as matrices and vectors.
 * 
 * @author Attila Kovacs
 *
 * @param <V>   the generic type of value that can be dotted with this object (from the right).
 * @param <R>   the generic type of result. 
 */
public interface DotProduct<V, R> {

    /**
     * Gets the dot product of this object (left) with the argument (right). 
     * 
     * @param righthand     The value on the right-hand side of the dot product
     * @return              The product of this object (left) dotted with the value (right).
     */
    public R dot(V righthand);
    
}
