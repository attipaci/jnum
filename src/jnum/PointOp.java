/* *****************************************************************************
 * Copyright (c) 2017 Attila Kovacs <attila[AT]sigmyne.com>.
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

package jnum;

import jnum.data.WeightedPoint;

/**
 * Represents a self-contained operation with a point argument of a generic type. The operation produces a result
 * of another generic type object.
 * 
 * Point operations are used for processing object data in generic sequential loops in a generic way without needing
 * a separate per-loop implemetation each time. By providing a common implementation for typical loop operations, it
 * reduces the chance of bugs that may arise from divergent individual loop implementations.
 * 
 * For parallel processing with similar features, see {@link jnum.parallel.ParallelPointOp}
 * 
 * 
 * @author Attila Kovacs
 *
 * @param <PointType>   The generic type of the point upon which the operation acts.
 * @param <ReturnType>  The generic return type of the operation/
 */
public abstract class PointOp<PointType, ReturnType> implements Cloneable {

    /**
     * Any expection that occured during the operation. Operations can set this field, and usrs can access
     * it to check if there was an exception while performing the operation.
     * 
     */
    public Exception exception;

    /** 
     * Constructs an new point operation in its default state, by calling {@link #reset()} immediate after creation.
     * 
     * @see #reset()
     * 
     */
    public PointOp() { reset(); }
    
    @SuppressWarnings("unchecked")
    @Override
    protected PointOp<PointType, ReturnType> clone() {
        try { return (PointOp<PointType, ReturnType>) super.clone(); }
        catch(CloneNotSupportedException e) { return null; }
    }
    
    /**
     * Creates a new instance of this point operation, with the same type, using {@link #clone()} followed by {@link #reset()}
     * to establish the default state on the newly created object. 
     * 
     * @return  A new point operation of the same type, in its default state.
     * 
     * @see #clone()
     * @see #reset()
     */
    public PointOp<PointType, ReturnType> newInstance() {
        PointOp<PointType, ReturnType> clone = clone();
        clone.reset();
        return clone();
    }
    
    /**
     * Resets, the operation object to its default state. It clears the {@link #exception} and calls {@link #init()}.
     * Subclasses should leave this method alone, and instead override {@link #init()} as needed.
     * 
     * @see #init()
     */
    public final void reset() {
        exception = null;
        init();
    }
    
    /**
     * Initializes the operation object to its default state.
     * 
     * @see #reset()
     * 
     */
    protected abstract void init();
    
    /**
     * Applies the operation to the given point of the expected generic type. 
     * 
     * 
     * The processing does not throw an exception, so as to prevent halting operation. Instead, if an exception occurs 
     * during the operation, it should be caught, and assigned to the {@link #exception} field allowing post-mortem investigation.
     * 
     * 
     * @param point     The point upon which to act the operation.
     */
    public abstract void process(PointType point);
    
    /**
     * Returns the current result. It is possible to make several calls to {@link #process(Object)} before one collects
     * the result of the compund operation with this call.
     * 
     * 
     * @return  The current result of the 
     */
    public abstract ReturnType getResult();
    
    
    /**
     * Returns the approximate number of primitive operations per point operation, which can be used for optimized
     * execution. In principle, any of the following counts as a primitive operation:
     * 
     * <ul>
     *  <li>any unary operator (e.g. ++, --, !, ~)
     *  <li>any binary operator (e.g. +, *, /, %, &lt;&lt;, &gt;&gt;&gt;, ^, &amp; ...)
     *  <li>any assignment operator (e.g. =, +=, &amp;=, ^= ...)
     *  <li>any comparison (e.g. ||, &amp;&amp;, !=, ==, &amp;lt;, &gt;= ...)
     *  <li>any return statements
     * </ul>
     * 
     * For every function call, or loop, inside the operation, the <code>numberOfOperations</code> should include the sum
     * of all operations within recursively.
     * 
     * @return The total approximate number of primitive operations per point operation.
     */
    public int numberOfOperations() { return 2; }

    
    /**
     * A simple cumulative integer counter. It is summular to the double-precision {@link Sum} operation
     * except that it accumulates over a <code>long</code> integer counter.
     * 
     * @author Attila Kovacs
     *
     * @param <PointType>   The generic type of the point object on which this operation acts.
     * 
     * @see Sum
     */
    public abstract static class Count<PointType> extends PointOp<PointType, Long> {
        private long sum;
       
        /**
         * Returns the inherent number of integer counts associated with the argument.
         * 
         * 
         * @param point     The datum from which to retrieve the relevant integer counts.
         * @return          The integer counts to accumulate with this operation.
         */
        public abstract double getCount(PointType point);
        
        @Override
        protected void init() {
            sum = 0L;
        }
        
        @Override
        public final void process(PointType point) {
            sum += getCount(point);
        }
        
        @Override
        public final Long getResult() {
           return sum;
        }
     
    }
    
    /**
     * A simple abstract class of point operations with no return type (i.e. return type of {@link Void}).
     * 
     * 
     * @author Attila Kovacs
     *
     * @param <PointType> The generic type of the point object on which this operation acts.
     */
    public abstract static class Simple<PointType> extends PointOp<PointType, Void> {
        @Override
        protected void init() {}
       
        @Override
        public final Void getResult() {
            return null;
        }
        
    }
    
    /**
     * An operation that simply counts the number of points it is called with.
     * 
     * @author Attila Kovacs
     *
     * @param <PointType>   The generic type of the point object on which this operation acts.
     */
    public static class ElementCount<PointType> extends Count<PointType> {
        @Override
        public final double getCount(PointType point) {
            return 1;
        }
    }
    
    /**
     * A generic summation over points of a generic type. It is similar to {@link Count} operation except that it
     * accumulates a double-precision floating point value instead of an integer type.
     * 
     * 
     * @author Attila Kovacs
     *
     * @param <PointType>   The generic type of the point object on which this operation acts.
     * 
     * @see Count
     */
    public abstract static class Sum<PointType> extends PointOp<PointType, Double> {
        private double sum;
       
        @Override
        protected void init() {
            sum = Double.NaN;
        }
        
        /**
         * Returns the floating point value associated with the given point.
         * 
         * @param point     The datum from which to extract the relevant floating point quantity
         * @return          The floating point quantity that is to be accumulated by this operation
         */
        protected abstract double getValue(PointType point);
        
        @Override
        public final void process(PointType point) {
            if(Double.isNaN(sum)) sum = 0.0;
            sum += getValue(point);
        }
        
        @Override
        public final Double getResult() {
           return sum;
        }
        
    }
    
    /**
     * An operation that averages a floating-point quantity over points of the given generic type, using a weighted
     * averageing recipe.
     * 
     * 
     * @author Attila Kovacs
     *
     * @param <PointType>   The generic type of the point object on which this operation acts.
     * 
     * @see Sum
     */
    public abstract static class Average<PointType> extends PointOp<PointType, WeightedPoint> {
        private WeightedPoint ave;
       
        @Override
        protected void init() {
            ave = new WeightedPoint();
        }
        
        /**
         * Returns the relevant floating-point quantity for the given point for averaging by this operation.
         * 
         * @param point     The datum from which to extract the quantity to be averaged.
         * @return          The floating point quantity to average.
         */
        protected abstract double getValue(PointType point);
        

        /**
         * Returns the relevant weight, such as a noise weight, for the given point for averaging by this operation.
         * For aritmethic averaging, this function should return 1.0.
         * 
         * @param point     The datum from which to extract the quantity to be averaged.
         * @return          The relative weight (such as noise weight) of the point quantity to be averaged.
         */
        protected abstract double getWeight(PointType point);
        
        @Override
        public final void process(PointType point) {
            final double w = getWeight(point);
            ave.add(w * getValue(point));
            ave.addWeight(w);
        }
        
        @Override
        public final WeightedPoint getResult() {
           ave.scaleValue(1.0 / ave.weight());
           return ave;
        }
        
    }
    
   

    
}

