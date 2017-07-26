/*******************************************************************************
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
 *     Attila Kovacs <attila[AT]sigmyne.com> - initial API and implementation
 ******************************************************************************/

package jnum.parallel;


import jnum.data.WeightedPoint;

public abstract class ParallelPointOp<PointType, ReturnType> extends PointOp<PointType, ReturnType> {
   
    @Override
    public ParallelPointOp<PointType, ReturnType> clone() {
        return (ParallelPointOp<PointType, ReturnType>) super.clone();
    }
      
    public ParallelPointOp<PointType, ReturnType> newInstance() {
        ParallelPointOp<PointType, ReturnType> clone = clone();
        clone.init();
        return clone();
    }
    
    
    public abstract void mergeResult(ReturnType localResult);
    
    
    
    public abstract static class Count<PointType> extends ParallelPointOp<PointType, Long> {
        private long sum = 0L;
       
        public abstract double getCount(PointType point);
           
        @Override
        public void init() {
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
        
        @Override
        public final void mergeResult(Long localCount) {
            sum += localCount;
        }
    }
    
    public abstract static class Sum<PointType> extends ParallelPointOp<PointType, Double> {
        private double sum = 0.0;
       
        @Override
        public void init() {
            sum = 0.0;
        }
        
        public abstract double getValue(PointType point);
        
        @Override
        public final void process(PointType point) {
            sum += getValue(point);
        }
        
        @Override
        public final Double getResult() {
           return sum;
        }
        
        @Override
        public final void mergeResult(Double localSum) {
            sum += localSum;
        }
    }
    
    public abstract static class Average<PointType> extends ParallelPointOp<PointType, WeightedPoint> {
        private WeightedPoint ave;
       
        @Override
        public void init() {
            ave = new WeightedPoint();
        }
        
        public abstract double getValue(PointType point);
        
        public abstract double getWeight(PointType point);
        
        @Override
        public final void process(PointType point) {
            ave.add(getValue(point));
            ave.addWeight(getWeight(point));
        }
        
        @Override
        public final WeightedPoint getResult() {
           ave.scaleValue(1.0 / ave.weight());
           return ave;
        }
        
        @Override
        public final void mergeResult(WeightedPoint localAverage) {
            ave.average(localAverage);
        }
    }
    
}
