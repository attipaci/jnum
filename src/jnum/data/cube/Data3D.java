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

package jnum.data.cube;


import java.util.List;

import jnum.data.CubicSpline;
import jnum.data.DataCrawler;
import jnum.data.Interpolator;
import jnum.data.RegularData;
import jnum.data.SplineSet;
import jnum.data.WeightedPoint;
import jnum.data.index.Index3D;
import jnum.data.index.IndexedValues;
import jnum.math.Vector3D;



public abstract class Data3D extends RegularData<Index3D, Vector3D> implements Values3D {



    // TODO 3D methods
    /*
     * - restrictRange(Range);
     * - discardRange(Range);
     * 
     * 
     * - level()
     */

    @Override
    public Cube3D newImage(Index3D size, Class<? extends Number> elementType) {
        Cube3D c = Cube3D.createType(getElementType(), size.i(), size.j(), size.k());
        c.copyPoliciesFrom(this);
        return c;
    }

    public Cube3D getCube() {
        return getCube(getElementType(), getInvalidValue());
    }

    public final Cube3D getCube(Number blankingValue) {
        return getCube(getElementType(), blankingValue);
    }

    public final Cube3D getCube(Class<? extends Number> elementType) {
        return getCube(elementType, getInvalidValue());
    }

    public Cube3D getCube(Class<? extends Number> elementType, Number invalidValue) {
        Cube3D cube = newImage(getSize(), elementType);

        cube.setInvalidValue(invalidValue);
        cube.setData(this);

        List<String> imageHistory = cube.getHistory();
        if(getHistory() != null) imageHistory.addAll(getHistory());

        return cube;
    }

    @Override
    public final Vector3D getVectorInstance() { return new Vector3D(); }

    public final Number getValid(final int i, final int j, final int k, final Number defaultValue) {
        if(!isValid(i, j, k)) return defaultValue;
        return get(i, j, k);
    }

    @Override
    public final Number getValid(final Index3D index, final Number defaultValue) { 
        return getValid(index.i(), index.j(), index.k(), defaultValue); 
    }

    @Override
    public final boolean isValid(Index3D index) { return isValid(index.i(), index.j(), index.k()); }

    @Override
    public final void discard(Index3D index) { discard(index.i(), index.j(), index.k()); }

    @Override
    public boolean isValid(int i, int j, int k) {
        return isValid(get(i, j, k));
    }

    @Override
    public void discard(int i, int j, int k) {
        set(i, j, k, getInvalidValue());
    }

    @Override
    public double valueAtIndex(double ic, double jc, double kc) {
        return valueAtIndex(ic, jc, kc, null);
    }


    @Override
    public final double valueAtIndex(Vector3D index, SplineSet<Vector3D> splines) {
        return valueAtIndex(index.x(), index.y(), index.z(), splines);
    }

    @Override
    public final double valueAtIndex(Index3D numerator, Index3D denominator, SplineSet<Vector3D> splines) {
        return valueAtIndex((double) numerator.i() / denominator.i(), (double) numerator.j() / denominator.j(), (double) numerator.k() / denominator.k(), splines);
    }

    public double valueAtIndex(double ic, double jc, double kc, SplineSet<Vector3D> splines) {
        // The nearest data point (i,j)
        final int i = (int) Math.round(ic);
        if(i < 0) return Double.NaN;
        else if(i >= sizeX()) return Double.NaN;

        final int j = (int) Math.round(jc);
        if(j < 0) return Double.NaN;
        else if(j >= sizeY()) return Double.NaN;

        final int k = (int) Math.round(kc);
        if(k < 0) return Double.NaN;
        else if(k >= sizeZ()) return Double.NaN;

        if(!isValid(i, j, k)) return Double.NaN;

        if(i == ic) if(j == jc) if(k == kc) return get(i, j, k).doubleValue();

        switch(getInterpolationType()) {
        case NEAREST : return get(i, j, k).doubleValue();
        case LINEAR : return linearAtIndex(ic, jc, kc);
        case PIECEWISE_QUADRATIC : return quadraticAtIndex(ic, jc, kc);
        case CUBIC_SPLINE : return splines == null ? splineAtIndex(ic, jc, kc) : splineAtIndex(ic, jc, kc, splines);
        }

        return Double.NaN;

    }



    @Override
    public final Number nearestValueAtIndex(Vector3D index) {
        return nearestValueAtIndex(index.x(), index.y(), index.z());
    }

    public Number nearestValueAtIndex(double ic, double jc, double kc) {
        return get((int) Math.round(ic), (int) Math.round(jc), (int) Math.round(kc));
    }


    @Override
    public final double linearAtIndex(Vector3D index) {
        return linearAtIndex(index.x(), index.y(), index.z());
    }

    public double linearAtIndex(double ic, double jc, double kc) {
        int i0 = (int)Math.floor(ic);
        int j0 = (int)Math.floor(jc);
        int k0 = (int)Math.floor(kc);

        int toi = Math.min(sizeX()-1, i0+1);
        int toj = Math.min(sizeY()-1, j0+1);   
        int tok = Math.min(sizeZ()-1, k0+1);

        final double di = ic - i0;
        final double dj = jc - j0;
        final double dk = kc - k0;

        double sum = 0.0, sumw = 0.0;


        for(int k=tok; --k >= k0; ) for(int j=toj; --j >= j0; ) for(int i=toi; --i >= i0; ) if(isValid(i, j, k)) {
            double w = (i == i0 ? (1.0 - di) : di) * (j == j0 ? (1.0 - dj) : dj) * (k == k0 ? (1.0 - dk) : dk);
            sum += w * get(i, j, k).doubleValue();
            sumw += w;          
        }

        return sum / sumw;

        // ~90 ops
    }

    @Override
    public final double quadraticAtIndex(Vector3D index) {
        return quadraticAtIndex(index.x(), index.y(), index.z());
    }


    public double quadraticAtIndex(double ic, double jc, double kc) {
        // TODO
        throw new UnsupportedOperationException("Not implemented.");
    }


    public double splineAtIndex(double ic, double jc, double kc) {
        synchronized(reuseIpolData) { return splineAtIndex(ic, jc, kc, reuseIpolData); }
    }

    @Override
    public final double splineAtIndex(Vector3D index, SplineSet<Vector3D> splines) {
        return splineAtIndex(index.x(), index.y(), index.z(), splines);
    }

    public double splineAtIndex(double ic, double jc, double kc, SplineSet<Vector3D> splines) {
        splines.centerOn(ic, jc, kc);

        final CubicSpline splineX = splines.getSpline(0);
        final CubicSpline splineY = splines.getSpline(1);
        final CubicSpline splineZ = splines.getSpline(2);

        final int fromi = Math.max(0, splineX.minIndex());
        final int toi = Math.min(sizeX(), splineX.maxIndex());

        final int fromj = Math.max(0, splineY.minIndex());
        final int toj = Math.min(sizeY(), splineY.maxIndex());

        final int fromk = Math.max(0, splineZ.minIndex());
        final int tok = Math.min(sizeZ(), splineZ.maxIndex());

        // Do the spline convolution...
        double sum = 0.0, sumw = 0.0;
        for(int i=toi; --i >= fromi; ) {
            final double wx = splineX.coefficientAt(i);
            for(int j=toj; --j >= fromj; ) {
                final double wxy = wx * splineY.coefficientAt(j);
                for(int k=tok; --k >= fromk; ) if(isValid(i, j, k)) {
                    final double w = wxy * splineZ.coefficientAt(k);         
                    sum += w * get(i, j, k).doubleValue();
                    sumw += w;
                }
            }
        }

        return sum / sumw;

        // ~800 ops...
    }

    public final double splineAt(Index3D index, SplineSet<Vector3D> splines) {
        return splineAtIndex(index.i(), index.j(), index.k(), splines);
    }


    @Override
    public final Number get(int ... idx) {
        return get(idx[0], idx[1], idx[2]);
    }

    @Override
    public final void set(Number value, int ... idx) {
        set(idx[0], idx[1], idx[2], value);
    }
    
    @Override
    public final double valueAtIndex(double ... idx) {
        return valueAtIndex(idx[0], idx[1], idx[2]);
    }
    
    @Override
    public double splineAtIndex(SplineSet<Vector3D> splines, double ... idx) {
        return splineAtIndex(idx[0], idx[1], idx[2], splines);
    }

    @Override
    protected int getInterpolationOps(Interpolator.Type type) {
        switch(type) {
        case NEAREST : return 15;
        case LINEAR : return 90;
        case PIECEWISE_QUADRATIC : return 100;
        case CUBIC_SPLINE : return 800;
        }
        return 1;
    }

    
    @Override
    public int getPointSmoothOps(int beamPoints, Interpolator.Type interpolationType) {
        return 36 + beamPoints * (16 + getInterpolationOps(interpolationType));
    }

    @SuppressWarnings("null")
    @Override
    public void getSmoothedValueAtIndex(final Index3D index, final RegularData<Index3D, Vector3D> beam, final Index3D refIndex, 
            final IndexedValues<Index3D, ?> weight, final WeightedPoint result) {   
        // More efficient than generic implementation

        final int iR = index.i() - refIndex.i();
        final int jR = index.j() - refIndex.j();
        final int kR = index.k() - refIndex.k();

        final int fromi = Math.max(0, iR);
        final int fromj = Math.max(0, jR);
        final int fromk = Math.max(0, kR);
        
        final int toi = Math.min(sizeX(), iR + beam.getSize(0));
        final int toj = Math.min(sizeY(), jR + beam.getSize(1));
        final int tok = Math.min(sizeZ(), kR + beam.getSize(1));
        
        Index3D idx = (weight == null) ? null : new Index3D();
        
        double sum = 0.0, sumw = 0.0;
        
        for(int i=fromi; i<toi; i++) for(int j=fromj; j<toj; j++) for(int k=fromk; k<tok; k++) if(isValid(i, j, k)) {
            final double w;
            
            if(weight == null) w = 1.0;
            else {
                idx.set(i, j, k);
                w = weight.get(idx).doubleValue();
                if(w == 0.0) continue;
            }
            
            final double wB = w * beam.get(i - iR, j - jR, k - kR).doubleValue();
            if(wB == 0.0) return;
            
            sum += wB * get(i, j, k).doubleValue();
            sumw += Math.abs(wB);    
        }

        result.setValue(sum / sumw);
        result.setWeight(sumw); 
    }  
    
    @Override
    public final boolean containsIndex(Vector3D index) {
        return containsIndex(index.x(), index.y(), index.z());        
    }

    public boolean containsIndex(final double i, final double j, final double k) {
        if(i < 0) return false;
        if(j < 0) return false;
        if(k < 0) return false;
        if(i >= sizeX()-0.5) return false;
        if(j >= sizeY()-0.5) return false;
        if(k >= sizeZ()-0.5) return false;
        return true;
    }


    @SuppressWarnings("cast")
    @Override
    public final Cube3D getCropped(Index3D from, Index3D to) {
        return (Cube3D) getCropped(from.i(), from.j(), from.k(), to.i(), to.j(), to.k());
    }

    public Cube3D getCropped(int imin, int jmin, int kmin, int imax, int jmax, int kmax) {
        return getCropped(new Index3D(imin, jmin, kmin), new Index3D(imax, jmax, kmax));
    }   



    @Override
    public String getInfo() {
        return "Datacube Size: " + getSizeString() + " voxels.";
    }


    @Override
    public Object getTableEntry(String name) {
        if(name.equals("sizeX")) return sizeX();
        else if(name.equals("sizeY")) return sizeY();
        else if(name.equals("sizeZ")) return sizeZ();
        else return super.getTableEntry(name);
    }

    @Override
    public DataCrawler<Number> iterator() {
        return new DataCrawler<Number>() {
            int i = 0, j = 0, k = 0;

            @Override
            public final boolean hasNext() {
                if(i < sizeX()) return true;
                return k < (sizeZ()-1);
            }

            @Override
            public final Number next() {
                if(i >= sizeX()) return null;

                k++;
                if(k == sizeZ()) {
                    k = 0; j++; 
                    if(j == sizeY()) { j = 0; i++; }
                }

                return i < sizeX() ? get(i, j, k) : null;
            }

            @Override
            public final void remove() {
                discard(i, j, k);
            }

            @Override
            public final Object getData() {
                return Data3D.this;
            }

            @Override
            public final void setCurrent(Number value) {
                set(i, j, k, value);
            }

            @Override
            public final boolean isValid() {
                return Data3D.this.isValid(i, j, k);
            }

            @Override
            public final void reset() {
                i = j = k = 0;
            }

        };

    }

}
