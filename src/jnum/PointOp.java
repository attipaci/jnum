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

package jnum;

import jnum.data.WeightedPoint;

public abstract class PointOp<PointType, ReturnType> implements Cloneable {
    public Exception exception;
    
    public PointOp() { reset(); }
    
    @SuppressWarnings("unchecked")
    @Override
    protected PointOp<PointType, ReturnType> clone() {
        try { return (PointOp<PointType, ReturnType>) super.clone(); }
        catch(CloneNotSupportedException e) { return null; }
    }
    
    public PointOp<PointType, ReturnType> newInstance() {
        PointOp<PointType, ReturnType> clone = clone();
        clone.reset();
        return clone();
    }
    
    public final void reset() {
        exception = null;
        init();
    }
    
    protected abstract void init();
    
    public abstract void process(PointType point);
    
    public abstract ReturnType getResult();
    
    public int numberOfOperations() { return 2; }
    
    
    
    
    public abstract static class Count<PointType> extends PointOp<PointType, Long> {
        private long sum;
       
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
    
    public abstract static class Simple<PointType> extends PointOp<PointType, Void> {
        @Override
        protected void init() {}
       
        @Override
        public Void getResult() {
            return null;
        }
        
    }
    
    public static class ElementCount<PointType> extends Count<PointType> {
        @Override
        public final double getCount(PointType point) {
            return 1;
        }
    }
    
    public abstract static class Sum<PointType> extends PointOp<PointType, Double> {
        private double sum;
       
        @Override
        protected void init() {
            sum = 0.0;
        }
        
        protected abstract double getValue(PointType point);
        
        @Override
        public final void process(PointType point) {
            sum += getValue(point);
        }
        
        @Override
        public final Double getResult() {
           return sum;
        }
        
    }
    
    public abstract static class Average<PointType> extends PointOp<PointType, WeightedPoint> {
        private WeightedPoint ave;
       
        @Override
        protected void init() {
            ave = new WeightedPoint();
        }
        
        protected abstract double getValue(PointType point);
        
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

