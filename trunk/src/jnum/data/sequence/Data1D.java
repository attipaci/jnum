/*******************************************************************************
 * Copyright (c) 2017 Attila Kovacs <attila[AT]sigmyne.com>.
 * All rights reserved. 
 * 
 * This file is part of crush.
 * 
 *     crush is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     crush is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with crush.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Attila Kovacs <attila[AT]sigmyne.com> - initial API and implementation
 ******************************************************************************/

package jnum.data.sequence;

import jnum.data.CubicSpline;
import jnum.data.ParallelValues;
import jnum.data.cube.Data3D.InterpolatorData;
import jnum.text.TableFormatter;

public abstract class Data1D extends ParallelValues implements Value1D, TableFormatter.Entries {

    private CubicSpline reuseSpline;
    
    @Override
    public Data1D clone() {
        Data1D clone = (Data1D) super.clone();
        clone.reuseSpline = new CubicSpline();
        return clone;
    }
        
    
    
    public double valueAtIndex(double ic, double jc, double kc, CubicSpline spline) {
        // The nearest data point (i,j)
        final int i = (int) Math.round(ic);
        if(i < 0) return Double.NaN;
        else if(i >= size()) return Double.NaN;

        
        if(!isValid(i)) return Double.NaN;

        if(i == ic) return get(i).doubleValue();

        switch(getInterpolationType()) {
        case NEAREST : return get(i).doubleValue();
        case LINEAR : return linearAt(ic);
        case QUADRATIC : return quadraticAt(ic);
        case SPLINE : return spline == null ? splineAt(ic) : splineAt(ic, spline);
        }

        return Double.NaN;
        
    }
    
    // Bilinear interpolation
    public double linearAt(double ic) {        
        final int i = (int)Math.floor(ic);
        final double di = ic - i;

        double sum = 0.0, sumw = 0.0;

        if(isValid(i)) {
            double w = (1.0 - di);
            sum += w * get(i).doubleValue();
            sumw += w;          
        }
        if(isValid(i+1)) {
            double w = di;
            sum += w * get(i+1).doubleValue();
            sumw += w;  
        }
        
        return sum / sumw;

        // ~ 25 ops...
    }
    
    public double splineAt(final double ic, final double jc) {
        synchronized(reuseSpline) { return splineAt(ic, jc, reuseSpline); }
    }


    // Performs a bicubic spline interpolation...
    public double splineAt(final double ic, final double jc, CubicSpline spline) {   
        spline.centerOn(ic);

     
        final int fromi = Math.max(0, spline.minIndex());
        final int toi = Math.min(size(), spline.maxIndex());

        // Do the spline convolution...
        double sum = 0.0, sumw = 0.0;
        for(int i=toi; --i >= fromi; ) if(isValid(i)) {
            final double w = spline.coefficientAt(i);
            sum += w * get(i).doubleValue();
            sumw += w;
        }

        return sum / sumw;

        // ~50 ops...
    }
    
    
    
    @Override
    public Object getTableEntry(String name) {
        if(name.equals("points")) return Integer.toString(countPoints());
        else if(name.equals("size")) return size();
        else if(name.equals("min")) return getMin().doubleValue();
        else if(name.equals("max")) return getMax().doubleValue();
        else if(name.equals("mean")) return mean();
        else if(name.equals("median")) return median();
        else if(name.equals("rms")) return getRMS(true);
        else return TableFormatter.NO_SUCH_DATA;
    }
    
    
    

    public abstract class Loop<ReturnType> {
        public ReturnType process() {
            for(int i=size(); --i >= 0; ) process(i);
            return getResult();
        }

        protected abstract void process(int i);

        protected ReturnType getResult() { return null; }
    }


    
}
