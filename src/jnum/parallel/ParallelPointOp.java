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

    
    public abstract void mergeResult(ReturnType localResult);

    
    public abstract static class Simple<PointType> extends ParallelPointOp<PointType, Void> {

        @Override
        public void mergeResult(Void localResult) {}

        @Override
        protected void init() {}
       
        @Override
        public Void getResult() { return null; }
        
    }
    
    public abstract static class Count<PointType> extends ParallelPointOp<PointType, Long> {
        private long sum = 0L;
       
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
    
    public static class ElementCount<PointType> extends Count<PointType> {

        @Override
        public final long getCount(PointType point) {
            return 1;
        }
        
    }
    
    public abstract static class Sum<PointType> extends ParallelPointOp<PointType, Double> {
        private double sum = Double.NaN;
       
        @Override
        protected void init() {
            sum = Double.NaN;
        }
        
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
    
    public abstract static class Average<PointType> extends ParallelPointOp<PointType, WeightedPoint> {
        private double sum, sumw;
       
        @Override
        protected void init() {
            sum = sumw = 0.0;
        }
        
        public abstract double getValue(PointType point);
        
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
