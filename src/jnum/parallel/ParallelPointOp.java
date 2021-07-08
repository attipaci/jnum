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

package jnum.parallel;


import jnum.PointOp;
import jnum.data.WeightedPoint;

/**
 * Represents a operation on a self-contained (point) argument of a generic type, which can be performed in 
 * parallel. The operation produces a result of another generic type object.
 * 
 * These operations are used for processing object data in generic parallel manner. By providing a common 
 * implementation for typical parallel operations, it reduces the chance of bugs that may arise from 
 * divergent individual implementations.
 *
 * In many ways this class is similar to parallel streams that were introduced in Java 8. However this 
 * implementation predates Java streams. The main reason for its continued existence it extra tunability 
 * that is not available in the built-in Java parallel streams, such as the ability to configure the number 
 * of theads in which the operation is parallel processed.
 * 
 * @author Attila Kovacs
 *
 * @param <PointType>   The generic type of the point (self-contained object) upon which the operation acts.
 * @param <ReturnType>  The generic return type of the operation/
 */
public abstract class ParallelPointOp<PointType, ReturnType> extends PointOp<PointType, ReturnType> {
   
    @Override
    protected ParallelPointOp<PointType, ReturnType> clone() {
        return (ParallelPointOp<PointType, ReturnType>) super.clone();
    }
      
    @Override
    public ParallelPointOp<PointType, ReturnType> newInstance() {
        ParallelPointOp<PointType, ReturnType> clone = clone();
        clone.reset();
        return clone;
    }

    /**
     * Merges partial results obtained from parallel-procesed threads, each with a distinct sub-section
     * of the data.
     * 
     * @param localResult   Accumulates the partial result from one thread into the global result
     *                      to be returned for the operation as a whole.
     */
    public abstract void mergeResult(ReturnType localResult);

    
    /**
     * A base parallel operation on a self-contained (point) object that does not result in
     * a return value.
     * 
     * @author Attila Kovacs
     *
     * @param <PointType>   The generic type of the point (self-contained object) upon which the operation acts. 
     */
    public abstract static class Simple<PointType> extends ParallelPointOp<PointType, Void> {

        @Override
        public void mergeResult(Void localResult) {}

        @Override
        protected void init() {}
       
        @Override
        public Void getResult() { return null; }
        
    }
    
    /**
     * A base parallel operation for cumulative counting on a self-contained (point) objects.
     * 
     * @author Attila Kovacs
     *
     * @param <PointType>   The generic type of the point (self-contained object) upon which the operation acts. 
     */
    public abstract static class Count<PointType> extends ParallelPointOp<PointType, Long> {
        private long sum = 0L;
       
        /**
         * Returns the number of counts for a given point object.
         * 
         * @param point     the generic type object that contributes to the counting.
         * @return          the number counts to include from this object in the total tally. 
         */
        public abstract long getCount(PointType point);
           
        @Override
        protected void init() {
            sum = 0L;
        }
        
        @Override
        public final void process(PointType point) {
            sum += getCount(point);
        }
        
        @Override
        public final void mergeResult(Long localCount) {
            sum += localCount;
        }
        
        @Override
        public final Long getResult() {
           return sum;
        }

    }
    
    /**
     * A base parallel operation for cumulative counting of the number of objects presents.
     * 
     * @author Attila Kovacs
     *
     * @param <PointType>   The generic type of the point (self-contained object) upon which the operation acts. 
     */
    public static class ElementCount<PointType> extends Count<PointType> {

        @Override
        public final long getCount(PointType point) {
            return 1;
        }
        
    }
    
    /**
     * A base parallel operation for summing over self-contained (point) objects.
     * 
     * @author Attila Kovacs
     *
     * @param <PointType>   The generic type of the point (self-contained object) upon which the operation acts. 
     */
    public abstract static class Sum<PointType> extends ParallelPointOp<PointType, Double> {
        private double sum = Double.NaN;
       
        @Override
        protected void init() {
            sum = Double.NaN;
        }
        
        /**
         * Extracts the value to be summed from the argument.
         * 
         * @param point     the generic type object that contributes to the summation.
         * @return          the value to include in the summation for the argument.
         */
        protected abstract double getValue(PointType point);
        
        @Override
        public final void process(PointType point) {
            if(Double.isNaN(sum)) sum = 0.0;
            sum += getValue(point);
        }

        @Override
        public final void mergeResult(Double localSum) {
            if(!localSum.isNaN()) sum += localSum;
        }
        
        @Override
        public final Double getResult() {
           return sum;
        }

    }
    
    /**
     * A base parallel operation for calculating averages over self-contained (point) objects.
     * 
     * @author Attila Kovacs
     *
     * @param <PointType>   The generic type of the point (self-contained object) upon which the operation acts. 
     */
    public abstract static class Average<PointType> extends ParallelPointOp<PointType, WeightedPoint> {
        private double sum, sumw;
       
        @Override
        protected void init() {
            sum = sumw = 0.0;
        }

        /**
         * Extracts the value to be averaged from the argument.
         * 
         * @param point     the generic type object that contributes to the average.
         * @return          the value to include in the average for the argument.
         */
        public abstract double getValue(PointType point);
        
        /**
         * Extracts the weight to be used when averaging the argument.
         * 
         * @param point     the generic type object that contributes to the average.
         * @return          the weight (relative or noise weight) to use for averaging the argument.
         */
        public abstract double getWeight(PointType point);
        
        @Override
        public final void process(PointType point) {
            final double w = getWeight(point);
            sum += w * getValue(point);
            sumw += w;
        }
        
        @Override
        public final void mergeResult(WeightedPoint localAverage) {
            sum += localAverage.weight() * localAverage.value();
            sumw += localAverage.weight();
        }
        
        @Override
        public final WeightedPoint getResult() {
           return new WeightedPoint(sum / sumw, sumw);
        }

    }
    
}
