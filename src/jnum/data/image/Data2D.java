/* *****************************************************************************
 * Copyright (c) 2018 Attila Kovacs <attila[AT]sigmyne.com>.
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

package jnum.data.image;

import java.util.List;

import jnum.Constant;
import jnum.data.DataPoint;
import jnum.data.Interpolator;
import jnum.data.RegularData;
import jnum.data.SplineSet;
import jnum.data.WeightedPoint;
import jnum.data.DataCrawler;
import jnum.data.CubicSpline;
import jnum.data.index.Index2D;
import jnum.data.index.IndexedValues;
import jnum.math.Range;
import jnum.math.Vector2D;
import jnum.parallel.ParallelPointOp;
import jnum.util.HashCode;


/**
 * An abstract 2D image class that operates solely via element access, but does not allow changing size
 * or access the backup storage object directly.
 * 
 * @author Attila Kovacs
 *
 */
public abstract class Data2D extends RegularData<Index2D, Vector2D> implements Values2D {

    @Override
    public int hashCode() {
        int hash = super.hashCode() ^ sizeX() ^ sizeY(); 
        hash ^= HashCode.sampleFrom(this);
        return hash;
    }
    
    @Override
    public Vector2D getVectorInstance() { return new Vector2D(); }

    @Override
    public Image2D newImage() {
        return (Image2D) super.newImage();
    }
    
    @Override
    public Image2D newImage(Index2D size, Class<? extends Number> elementType) {
        Image2D im = Image2D.createType(getElementType(), size.i(), size.j());
        im.copyPoliciesFrom(this);
        return im;
    }

    @Override
    public Data2D newInstance() {
        return (Data2D) super.newInstance();
    }
    
    @Override
    public abstract Data2D newInstance(Index2D size);

    public Image2D getImage() {
        return getImage(getElementType(), getInvalidValue());
    }

    public final Image2D getImage(Number blankingValue) {
        return getImage(getElementType(), blankingValue);
    }

    public final Image2D getImage(Class<? extends Number> elementType) {
        return getImage(elementType, getInvalidValue());
    }

    public Image2D getImage(Class<? extends Number> elementType, Number invalidValue) {
        Image2D image = newImage(getSize(), elementType);
         
        image.setInvalidValue(invalidValue);
        image.setData(this);

        List<String> imageHistory = image.getHistory();
        if(getHistory() != null) imageHistory.addAll(getHistory());

        return image;
    }
   
 
    @Override
    public final boolean isValid(Index2D index) {
        return isValid(index.i(), index.j());
    }


    @Override
    public boolean isValid(int i, int j) {
        return isValid(get(i,j));
    }

   
    @Override
    public void discard(int i, int j) {
        set(i, j, getInvalidValue());
    }
    
    @Override
    public final double valueAtIndex(double ... idx) {
        return valueAtIndex(idx[0], idx[1]);
    }
    
    @Override
    public double splineAtIndex(SplineSet<Vector2D> splines, double ... idx) {
        return splineAtIndex(idx[0], idx[1], splines);
    }

    public final Number getValid(final int i, final int j, final Number defaultValue) {
        if(!isValid(i, j)) return defaultValue;
        return get(i, j);
    }

    @Override
    public final Number getValid(final Index2D index, final Number defaultValue) {
        return getValid(index.i(), index.j(), defaultValue); 
    }

    @Override
    public final void discard(Index2D index) { discard(index.i(), index.j()); }

 
    @Override
    public final boolean containsIndex(Vector2D index) {
        return containsIndex(index.x(), index.y());
    }
    
    public boolean containsIndex(final double i, final double j) {
        if(i < 0) return false;
        if(j < 0) return false;
        if(i >= sizeX()-0.5) return false;
        if(j >= sizeY()-0.5) return false;
        return true;
    }


    public DataPoint getAsymmetry(final Grid2D<?> grid, final Vector2D centerIndex, final double angle, final Range radialRange) {    

        final Vector2D centerOffset = centerIndex.copy();
        grid.toOffset(centerOffset);

        class Moments {
            double m0 = 0.0, mc = 0.0, c2 = 0.0;
        }
        
        ParallelPointOp<Index2D, Moments> op = new ParallelPointOp<Index2D, Moments>() {
            private Moments values;
            private Vector2D v;
            
            @Override
            public void mergeResult(Moments localResult) {
                values.m0 += localResult.m0;
                values.mc += localResult.mc;
                values.c2 += localResult.c2;
            }

            @Override
            protected void init() { 
                values = new Moments();
                v = new Vector2D();
             }

            @Override
            public void process(Index2D point) { 
                if(!isValid(point)) return;

                v.set(point.i(), point.j());
                grid.toOffset(v);
                v.subtract(centerOffset);

                double r = v.length();

                if(r > radialRange.max()) return;

                double p = get(point).doubleValue();

                values.m0 += Math.abs(p);

                if(r < radialRange.min()) return;

                // cos term is gain-like
                double c = Math.cos(v.angle() - angle);
                
                values.mc += p * c;
                values.c2 += c * c;
            }

            @Override
            public Moments getResult() { 
                return values;
            }
            
            @Override
            public int numberOfOperations() {
                return 42;  // more or less...
            }
            
        };
        
        Moments values = smartFork(op);

        if(values.m0 > 0.0) return new DataPoint(values.mc / values.m0, Math.sqrt(values.c2) / values.m0);
        return new DataPoint(); 
    }


    public Asymmetry2D getAsymmetry2D(Grid2D<?> grid, Vector2D centerIndex, double angle, Range radialRange) {
        Asymmetry2D asym = new Asymmetry2D();
        asym.setX(getAsymmetry(grid, centerIndex, angle, radialRange));
        asym.setY(getAsymmetry(grid, centerIndex, angle + Constant.rightAngle, radialRange));
        return asym;
    }

 
  
    public Vector2D getRefinedPeakIndex(Index2D peakIndex) {
        final int i = peakIndex.i();
        final int j = peakIndex.j();

        double a=0.0,b=0.0,c=0.0,d=0.0;

        double y0 = get(i,j).doubleValue();

        if(i>0) if(i<sizeX()-1) if(isValid(i+1, j)) if(isValid(i-1, j)) {
            a = 0.5 * (get(i+1,j).doubleValue() + get(i-1,j).doubleValue()) - y0;
            c = 0.5 * (get(i+1,j).doubleValue() - get(i-1,j).doubleValue());
        }

        if(j>0) if(j<sizeY()-1) if(isValid(i, j+1)) if(isValid(i, j-1)) {
            b = 0.5 * (get(i,j+1).doubleValue() + get(i,j-1).doubleValue()) - y0;
            d = 0.5 * (get(i,j+1).doubleValue() - get(i,j-1).doubleValue());
        }

        double di = (a == 0.0) ? 0.0 : -0.5*c/a;
        double dj = (b == 0.0) ? 0.0 : -0.5*d/b;    

        if(Math.abs(di) > 0.5 || Math.abs(dj) > 0.5) 
            throw new IllegalStateException("Index argument does not mark a peak.");

        return new Vector2D(i + di, j + dj);
    }


    @Override
    public double valueAtIndex(Vector2D index, SplineSet<Vector2D> splines) {
        return valueAtIndex(index.x(), index.y(), splines);
    }

    @Override
    protected double valueAtIndex(Index2D numerator, Index2D denominator, SplineSet<Vector2D> splines) {
        return valueAtIndex((double) numerator.i() / denominator.i(), (double) numerator.j() / denominator.j(), splines);
    }

    

    @Override
    public double valueAtIndex(double ic, double jc) {
        return valueAtIndex(ic, jc, null);
    }


    public double valueAtIndex(double ic, double jc, SplineSet<Vector2D> splines) {  
        // The nearest data point (i,j)
        final int i = (int) Math.round(ic);
        final int j = (int) Math.round(jc);
        
        if(!containsIndex(i, j)) return Double.NaN;
        if(!isValid(i, j)) return Double.NaN;

        if(i == ic) if(j == jc) return get(i, j).doubleValue();

        switch(getInterpolationType()) {
        case NEAREST : return get(i, j).doubleValue();
        case LINEAR : return linearAtIndex(ic, jc);
        case PIECEWISE_QUADRATIC : return quadraticAtIndex(ic, jc);
        case CUBIC_SPLINE : return splines == null ? splineAtIndex(ic, jc) : splineAtIndex(ic, jc, splines);
        }

        return Double.NaN;
    }

    @Override
    public Number nearestValueAtIndex(Vector2D index) {
        return nearestValueAtIndex(index.x(), index.y());
    }
    
    public Number nearestValueAtIndex(double ic, double jc) {
        return get((int) Math.round(ic), (int) Math.round(jc));
    }
    
    @Override
    public final double linearAtIndex(Vector2D index) { return linearAtIndex(index.x(), index.y()); }

    // Bilinear interpolation
    public double linearAtIndex(double ic, double jc) {        
        final int i = (int)Math.floor(ic);
        final int j = (int)Math.floor(jc);

        final double di = ic - i;
        final double dj = jc - j;

        double sum = 0.0, sumw = 0.0;

        if(isValid(i, j)) {
            double w = (1.0 - di) * (1.0 - dj);
            sum += w * get(i, j).doubleValue();
            sumw += w;          
        }
        if(isValid(i+1, j)) {
            double w = di * (1.0 - dj);
            sum += w * get(i+1, j).doubleValue();
            sumw += w;  
        }
        if(isValid(i, j+1)) {
            double w = (1.0 - di) * dj;
            sum += w * get(i, j+1).doubleValue();
            sumw += w;  
        }
        if(isValid(i+1, j+1)) {
            double w = di * dj;
            sum += w * get(i+1, j+1).doubleValue();
            sumw += w;  
        }

        return sum / sumw;

        // ~ 45 ops...
    }

    @Override
    public final double quadraticAtIndex(Vector2D index) { return quadraticAtIndex(index.x(), index.y()); }


    // Interpolate (linear at edges, quadratic otherwise)   
    // Piecewise quadratic...
    /**
     * Piecewise quadratic at.
     *
     * @param ic the ic
     * @param jc the jc
     * @return the double
     */
    public double quadraticAtIndex(double ic, double jc) {
        // Find the nearest data point (i,j)
        final int i = (int)Math.round(ic);
        final int j = (int)Math.round(jc);

        final double y0 = get(i, j).doubleValue();
        double ax=0.0, ay=0.0, bx=0.0, by=0.0;

        if(isValid(i+1,j)) {
            if(isValid(i-1, j)) {
                ax = 0.5 * (get(i+1, j).doubleValue() + get(i-1, j).doubleValue()) - y0;
                bx = 0.5 * (get(i+1, j).doubleValue() - get(i-1, j).doubleValue());
            }
            else bx = get(i+1, j).doubleValue() - y0; // Fall back to linear...
        }
        else if(isValid(i-1, j)) bx = y0 - get(i-1, j).doubleValue();

        if(isValid(i,j+1)) {
            if(isValid(i,j-1)) {
                ay = 0.5 * (get(i, j+1).doubleValue() + get(i, j-1).doubleValue()) - y0;
                by = 0.5 * (get(i, j+1).doubleValue() - get(i, j-1).doubleValue());
            }
            else by = get(i, j+1).doubleValue() - y0; // Fall back to linear...
        }
        else if(isValid(i,j-1)) by = y0 - get(i, j-1).doubleValue();

        ic -= i;
        jc -= j;

        return (ax*ic+bx)*ic + (ay*jc+by)*jc + y0;

        // ~60 ops...
    }
    
    
    public double splineAtIndex(final double ic, final double jc) {
        synchronized(reuseIpolData) { return splineAtIndex(ic, jc, reuseIpolData); }
    }

    @Override
    public final double splineAtIndex(Vector2D index, SplineSet<Vector2D> splines) { 
        return splineAtIndex(index.x(), index.y(), splines); 
    }
 
    // Performs a bicubic spline interpolation...
    public double splineAtIndex(final double ic, final double jc, SplineSet<Vector2D> splines) {   
        splines.centerOn(ic, jc);

        final CubicSpline splineX = splines.getSpline(0);
        final CubicSpline splineY = splines.getSpline(1);

        final int fromi = Math.max(0, splineX.minIndex());
        final int toi = Math.min(sizeX(), splineX.maxIndex());

        final int fromj = Math.max(0, splineY.minIndex());
        final int toj = Math.min(sizeY(), splineY.maxIndex());

        // Do the spline convolution...
        double sum = 0.0, sumw = 0.0;
        for(int i=toi; --i >= fromi; ) {
            final double wx = splineX.coefficientAt(i);
            for(int j=toj; --j >= fromj; ) if(isValid(i, j)) {
                final double w = wx * splineY.coefficientAt(j);
                sum += w * get(i, j).doubleValue();
                sumw += w;
            }
        }

        return sum / sumw;

        // ~200 ops...
    }

 
    @Override
    protected int getInterpolationOps(Interpolator.Type type) {
        switch(type) {
        case NEAREST : return 10;
        case LINEAR : return 45;
        case PIECEWISE_QUADRATIC : return 60;
        case CUBIC_SPLINE : return 200;
        }
        return 1;
    }
    
    
    @Override
    public int getPointSmoothOps(int beamPoints, Interpolator.Type interpolationType) {
        return 25 + beamPoints * (16 + getInterpolationOps(interpolationType));
    }

    @Override
    public void getSmoothedValueAtIndex(final Index2D index, final RegularData<Index2D, Vector2D> beam, final Index2D refIndex, 
            final IndexedValues<Index2D, ?> weight, final WeightedPoint result) {   
        // More efficient than generic implementation...
        
        final int iR = index.i() - refIndex.i();
        final int jR = index.j() - refIndex.j();

        final int fromi = Math.max(0, iR);
        final int fromj = Math.max(0, jR);
        
        final int toi = Math.min(sizeX(), iR + beam.getSize(0));
        final int toj = Math.min(sizeY(), jR + beam.getSize(1));
        
        double sum = 0.0, sumw = 0.0;
        
        for(int i=fromi; i<toi; i++) for(int j=fromj; j<toj; j++) if(isValid(i, j)) {
            final double w;
            
            if(weight == null) w = 1.0;
            else {
                w = weight.get(i, j).doubleValue();
                if(w == 0.0) continue;
            }
            
            final double wB = w * beam.get(i - iR, j - jR).doubleValue();
            if(wB == 0.0) return;
            
            sum += wB * get(i, j).doubleValue();
            sumw += Math.abs(wB);    
        }

        result.setValue(sum / sumw);
        result.setWeight(sumw); 
    }  


    @Override
    public Object getTableEntry(String name) {
        if(name.equals("sizeX")) return sizeX();
        else if(name.equals("sizeY")) return sizeY();  
        else return super.getTableEntry(name);
    }

    @Override
    public String getInfo() {
        return "Image Size: " + getSizeString() + " pixels.";
    }

    @Override
    public DataCrawler<Number> iterator() {
        return Values2D.super.iterator();
    }
}
