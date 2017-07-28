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

package jnum.data.image;

import java.util.List;

import jnum.Constant;
import jnum.ExtraMath;
import jnum.Util;
import jnum.data.DataPoint;
import jnum.data.Validating;
import jnum.data.Data;
import jnum.data.DataCrawler;
import jnum.data.CubicSpline;
import jnum.data.WeightedPoint;
import jnum.data.image.overlay.Referenced2D;
import jnum.data.image.overlay.Viewport2D;
import jnum.data.image.transform.Stretch2D;
import jnum.math.Coordinate2D;
import jnum.math.Range;
import jnum.math.Vector2D;
import jnum.parallel.ParallelPointOp;
import jnum.parallel.ParallelTask;
import jnum.parallel.PointOp;
import jnum.util.HashCode;


/**
 * An abstract 2D image class that operates solely via element access, but does not allow changing size
 * or access the backup storage object directly.
 * 
 * @author pumukli
 *
 */
public abstract class Data2D extends Data<Index2D> implements Value2D {


    private InterpolatorData reuseIpolData;


    protected Data2D() {
        reuseIpolData = new InterpolatorData();
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode() ^ sizeX() ^ sizeY(); 
        hash ^= HashCode.sampleFrom(this);
        return hash;
    }

      
    @Override
    public Data2D clone() {
        Data2D clone = (Data2D) super.clone();
        clone.reuseIpolData = new InterpolatorData();
        return clone;
    }

    @Override
    public final Index2D copyOfIndex(Index2D index) { return new Index2D(index.i(), index.j()); }

    
    public Image2D getEmptyImage() {
        return Image2D.createType(getElementType(), sizeX(), sizeY());
    }

    public Image2D getImage() {
        return getImage(getElementType(), getBlankingValue());
    }

    public final Image2D getImage(Number blankingValue) {
        return getImage(getElementType(), blankingValue);
    }

    public final Image2D getImage(Class<? extends Number> elementType) {
        return getImage(elementType, getBlankingValue());
    }

    public Image2D getImage(Class<? extends Number> elementType, Number blankingValue) {
        Image2D image = Image2D.createFrom(this, blankingValue, elementType);

        image.copyParallel(this);
        image.setInterpolationType(getInterpolationType());
        image.setVerbose(isVerbose());
        image.setUnit(getUnit());

        List<String> imageHistory = image.getHistory();
        if(getHistory() != null) imageHistory.addAll(getHistory());

        return image;
    }


  

    @Override
    public Object getFitsData(Class<? extends Number> dataType) {  
        final Image2D transpose = Image2D.createType(dataType, sizeY(), sizeX());
        
        new Fork<Void>() {
            @Override
            protected void process(int i, int j) {
                transpose.set(j, i, isValid(i, j) ? get(i, j) : getBlankingValue());
            }     
        }.process();

        if(getUnit().value() != 1.0) transpose.scale(1.0 / getUnit().value());

        return transpose.getData();
    }

  
    @Override
    public String getSizeString() { return sizeX() + "x" + sizeY(); }


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
        set(i, j, getBlankingValue());
    }

  


    @Override
    public final Number get(Index2D index) { return get(index.i(), index.j()); }

    public final Number getValid(final int i, final int j, final Number defaultValue) {
        if(!isValid(i, j)) return defaultValue;
        return get(i, j);
    }

    @Override
    public final Number getValid(final Index2D index, final Number defaultValue) { return getValid(index.i(), index.j(), defaultValue); }


    @Override
    public final void set(Index2D index, Number value) { set(index.i(), index.j(), value); }


    @Override
    public final void add(Index2D index, Number value) { add(index.i(), index.j(), value); }


    public void scale(int i, int j, double factor) {
        set(i, j, get(i, j).doubleValue() * factor);
    }

    @Override
    public final void scale(Index2D index, double factor) { scale(index.i(), index.j(), factor); }

    @Override
    public final void discard(Index2D index) { discard(index.i(), index.j()); }

    @Override
    public Index2D size() {
        return new Index2D(sizeX(), sizeY());
    }
    
    @Override
    public int capacity() {
        return sizeX() * sizeY();
    }

  
    @Override
    public boolean conformsTo(Index2D size) {
        return conformsTo(size.i(), size.j());
    }
  
    public boolean conformsTo(int sizeX, int sizeY) {
        if(sizeX() != sizeX) return false;
        if(sizeY() != sizeY) return false;
        return true;
    }



    public int[] getXIndexRange() {
        int min = sizeX(), max = -1;
        for(int i=sizeX(); --i >= 0; ) for(int j=sizeY(); --j >= 0; ) if(isValid(i, j)) {
            if(i < min) min = i;
            if(i > max) max = i;
            break;
        }
        return max > min ? new int[] { min, max } : null;
    }

    public int[] getYIndexRange() {
        int min = sizeY(), max = -1;
        for(int j=sizeY(); --j >= 0; ) for(int i=sizeX(); --i >= 0; ) if(isValid(i, j)) {
            if(j < min) min = j;
            if(j > max) max = j;
            break;
        }
        return max > min ? new int[] { min, max } : null;
    }

    
    @Override
    public final boolean containsIndex(Index2D index) {
        return containsIndex(index.i(), index.j());        
    }

    public boolean containsIndex(final int i, final int j) {
        if(i < 0) return false;
        if(j < 0) return false;
        if(i >= sizeX()) return false;
        if(j >= sizeY()) return false;
        return true;
    }

    public boolean containsIndex(final double i, final double j) {
        if(i < 0) return false;
        if(j < 0) return false;
        if(i >= sizeX()-0.5) return false;
        if(j >= sizeY()-0.5) return false;
        return true;
    }


    
    protected Image2D getCropped(int imin, int jmin, int imax, int jmax) {
        Image2D cropped = getEmptyImage();

        final int fromi = Math.max(0, imin);
        final int fromj = Math.max(0, jmin);
        final int toi = Math.min(imax, sizeX()-1);
        final int toj = Math.min(jmax, sizeY()-1);      

        cropped.setSize(imax-imin+1, jmax-jmin+1);

        for(int i=fromi, i1=fromi-imin; i<=toi; i++, i1++) for(int j=fromj, j1=fromj-jmin; j<=toj; j++, j1++) 
            cropped.set(i1, j1, get(i, j));

        return cropped;
    }   


   
    @Override
    public final void clear(Index2D index) { clear(index.i(), index.j()); }
    
    public void clear(int i, int j) { set(i, j, 0); }


    public DataPoint getAsymmetry(final Grid2D<?> grid, final Vector2D centerIndex, final double angle, final Range radialRange) {    

        final Vector2D centerOffset = centerIndex.copy();
        grid.toOffset(centerOffset);

        class Moments {
            double m0 = 0.0, mc = 0.0, c2 = 0.0;
        }

        Fork<Moments> moments = new Fork<Moments>() {
            private Moments values;
            private Vector2D v;

            @Override
            public void init() {
                super.init();
                values = new Moments();
                v = new Vector2D();
            }

            @Override
            protected void process(int i, int j) {
                if(!isValid(i, j)) return;

                v.set(i,  j);
                grid.toOffset(v);
                v.subtract(centerOffset);

                double r = v.length();

                if(r > radialRange.max()) return;

                double p = get(i,j).doubleValue();

                values.m0 += Math.abs(p);

                if(r < radialRange.min()) return;

                // cos term is gain-like
                double c = Math.cos(v.angle() - angle);

                values.mc += p * c;
                values.c2 += c * c;
            }

            @Override
            public Moments getLocalResult() { return values; }

            @Override
            public Moments getResult() {
                Moments global = new Moments();
                for(ParallelTask<Moments> worker : getWorkers()) {
                    Moments local = worker.getLocalResult();
                    global.m0 += local.m0;
                    global.mc += local.mc;
                    global.c2 += local.c2;
                }
                return global;
            }  
        };

        moments.process();
        Moments values = moments.getResult();

        if(values.m0 > 0.0) return new DataPoint(values.mc / values.m0, Math.sqrt(values.c2) / values.m0);
        return new DataPoint(); 
    }


    public Asymmetry2D getAsymmetry2D(Grid2D<?> grid, Vector2D centerIndex, double angle, Range radialRange) {
        Asymmetry2D asym = new Asymmetry2D();
        asym.setX(getAsymmetry(grid, centerIndex, angle, radialRange));
        asym.setY(getAsymmetry(grid, centerIndex, angle + Constant.rightAngle, radialRange));
        return asym;
    }



    @Override
    public void despike(double threshold) {
        despike(threshold, null);
    }

    public synchronized void despike(final double significance, final Value2D noiseWeight) {
        final double[][] neighbourData = {{ 0.5, 1, 0.5 }, { 1, 0, 1 }, { 0.5, 1, 0.5 }};

        final Image2D neighbourImage = Image2D.createType(getElementType());
        neighbourImage.setData(neighbourData);

        final Referenced2D neighbours = new Referenced2D(neighbourImage, new Coordinate2D(1.0, 1.0));

        new Fork<Void>() {  
            private WeightedPoint point, surrounding;
            @Override
            protected void init() {
                point = new WeightedPoint();
                surrounding = new WeightedPoint();
            }
            @Override
            protected void process(final int i, final int j) {
                if(!isValid(i, j)) return;

                point.setValue(get(i, j).doubleValue());
                point.setWeight(noiseWeight == null ? 1.0 : noiseWeight.get(i, j).doubleValue());

                getSmoothedValueAtIndex(i, j, neighbours, noiseWeight, surrounding);

                point.subtract(surrounding);

                if(DataPoint.significanceOf(point) > significance) discard(i, j);             
            }
        }.process();

        addHistory("despiked at " + Util.S3.format(significance));
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

    
    
    public Vector2D getCentroidIndex() {
        final Vector2D centroid = new Vector2D();

        new Loop<Void>() {
            private double sumw = 0.0;

            @Override
            protected void process(int i, int j) {
                if(!isValid(i, j)) return; 

                double w = Math.abs(get(i,j).doubleValue());
                centroid.addX(w * i);
                centroid.addY(w * j);
                sumw += w;
            }

            @Override
            public Void getResult() {
                centroid.scale(1.0 / sumw);
                return null;
            }        
        }.process();

        return centroid;
    }



    public double valueAtIndex(Vector2D index) {
        return valueAtIndex(index.x(), index.y(), null);
    }


    public double valueAtIndex(Vector2D index, InterpolatorData ipolData) {
        return valueAtIndex(index.x(), index.y(), ipolData);
    }


    @Override
    public double valueAtIndex(double ic, double jc) {
        return valueAtIndex(ic, jc, null);
    }


    public double valueAtIndex(double ic, double jc, InterpolatorData ipolData) {  
        // The nearest data point (i,j)
        final int i = (int) Math.round(ic);
        final int j = (int) Math.round(jc);
        
        if(!containsIndex(i, j)) return Double.NaN;
        if(!isValid(i, j)) return Double.NaN;

        if(i == ic) if(j == jc) return get(i, j).doubleValue();

        switch(getInterpolationType()) {
        case NEAREST : return get(i, j).doubleValue();
        case LINEAR : return bilinearAt(ic, jc);
        case QUADRATIC : return piecewiseQuadraticAt(ic, jc);
        case SPLINE : return ipolData == null ? splineAt(ic, jc) : splineAt(ic, jc, ipolData);
        }

        return Double.NaN;
    }

    public Number nearestValueAtIndex(Vector2D index) {
        return nearestValueTo(index.x(), index.y());
    }
    
    public Number nearestValueTo(double ic, double jc) {
        return get((int) Math.round(ic), (int) Math.round(jc));
    }

    // Bilinear interpolation
    public double bilinearAt(double ic, double jc) {        
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


    // Interpolate (linear at edges, quadratic otherwise)   
    // Piecewise quadratic...
    /**
     * Piecewise quadratic at.
     *
     * @param ic the ic
     * @param jc the jc
     * @return the double
     */
    public double piecewiseQuadraticAt(double ic, double jc) {
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

    public double splineAt(final double ic, final double jc) {
        synchronized(reuseIpolData) { return splineAt(ic, jc, reuseIpolData); }
    }


    // Performs a bicubic spline interpolation...
    public double splineAt(final double ic, final double jc, InterpolatorData ipolData) {   
        ipolData.centerOn(ic, jc);

        final CubicSpline splineX = ipolData.splineX;
        final CubicSpline splineY = ipolData.splineY;

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
    protected int getInterpolationOps(int type) {
        switch(type) {
        case NEAREST : return 10;
        case LINEAR : return 45;
        case QUADRATIC : return 60;
        case SPLINE : return 200;
        }
        return 1;
    }


    public synchronized void smooth(Referenced2D beam) {
        paste(getSmoothed(beam, null, null), false);
        addHistory("smoothed");
    }


    public synchronized void fastSmooth(Referenced2D beam, int stepX, int stepY) {
        paste(getFastSmoothed(beam, stepX, stepY, null, null), false);
        addHistory("smoothed (fast method)");
    }




    // Beam fitting: I' = C * sum(wBI) / sum(wB2)
    // I(x) = I -> I' = I -> C = sum(wB2) / sum(wB)
    // I' = sum(wBI)/sum(wB)
    // rms = Math.sqrt(1 / sum(wB))
    protected void getSmoothedValueAtIndex(final double i, final double j, final Referenced2D beam, Value2D weight, WeightedPoint result) {    
        final double i0 = i - beam.getReferenceIndex().x();
        final int fromi = Math.max(0, (int) Math.ceil(i0));
        final int toi = Math.min(sizeX(), (int)Math.floor(i0) + beam.sizeX());

        final double j0 = j - beam.getReferenceIndex().y();
        final int fromj = Math.max(0, (int) Math.ceil(j0));
        final int toj = Math.min(sizeY(), (int)Math.floor(j0) + beam.sizeY());

        double sum = 0.0, sumw = 0.0;

        for(int i1=toi; --i1 >= fromi; ) for(int j1=toj; --j1 >= fromj; ) if(isValid(i1, j1)) {
            final double w = (weight == null ? 1.0 : weight.get(i1, j1).doubleValue());
            final double wB = w * beam.valueAtIndex(i1-i0, j1-j0);
            sum += wB * get(i1, j1).doubleValue();
            sumw += Math.abs(wB);   
        }

        result.setValue(sum / sumw);
        result.setWeight(sumw);   

    }

    public int getPointSmoothOps(int beamPoints, int interpolationType) {
        return 25 + beamPoints * (16 + getInterpolationOps(interpolationType));
    }


    public final Image2D getSmoothed(final Referenced2D beam, final Value2D weight, final Value2D smoothedWeights) {
        final Image2D convolved = getEmptyImage();

        new Fork<Void>() {
            private WeightedPoint result;
            @Override
            protected void init() {
                super.init();
                result = new WeightedPoint();
            }
            @Override
            protected void process(int i, int j) {            
                if(!isValid(i, j)) return;
                getSmoothedValueAtIndex(i, j, beam, weight, result);    
                convolved.set(i, j, result.value());
                if(smoothedWeights != null) smoothedWeights.set(i, j, result.weight());
            }
            @Override
            protected int getPointOps() {
                return 5 + getPointSmoothOps(beam.sizeX() * beam.sizeY(), NEAREST);
            }
        }.process();

        return convolved;
    }



    // Do the convolution proper at the specified intervals (step) only, and interpolate (quadratic) inbetween
    public Image2D getFastSmoothed(final Referenced2D beam, final int stepX, final int stepY, final Value2D weight, final Value2D smoothedWeights) {
        if(stepX * stepY == 1) return getSmoothed(beam, weight, smoothedWeights);

        final int nx = ExtraMath.roundupRatio(sizeX(), stepX);
        final int ny = ExtraMath.roundupRatio(sizeY(), stepY);


        final Image2D coarseSignal = Image2D.createType(getElementType());
        final Image2D coarseWeight = (smoothedWeights == null) ? null : Image2D.createType(weight.getElementType());

        coarseSignal.setSize(nx, ny);
        if(coarseWeight != null) coarseWeight.setSize(nx, ny);

        coarseSignal.new Fork<Void>() {
            WeightedPoint result;

            @Override
            public void init() {
                super.init();
                result = new WeightedPoint();
            }

            @Override
            protected void process(int i, int j) {
                getSmoothedValueAtIndex(i * stepX, j * stepY, beam, weight, result);
                coarseSignal.set(i, j, result.value());
                if(coarseWeight != null) coarseWeight.set(i, j, result.weight());
                if(result.weight() <= 0.0) coarseSignal.discard(i, j);
            }   

            @Override
            protected int getPointOps() {
                return 5 + getPointSmoothOps(beam.sizeX() * beam.sizeY() / (stepX * stepY), NEAREST);
            }
        }.process();


        final Image2D convolved = getEmptyImage();

        final double istepX = 1.0 / stepX;
        final double istepY = 1.0 / stepY;

        new InterpolatingFork() {
            @Override
            protected void process(int i, int j) {
                if(!isValid(i, j)) return;

                final double i1 = i * istepX;
                final double j1 = j * istepY;
                final double value = coarseSignal.valueAtIndex(i1, j1, getInterpolatorData());

                if(!Double.isNaN(value)) {      
                    convolved.set(i, j, value);
                    if(smoothedWeights != null) smoothedWeights.set(i, j, coarseWeight.valueAtIndex(i1, j1, getInterpolatorData()));
                }
                else {
                    convolved.discard(i, j);
                    if(smoothedWeights != null) smoothedWeights.set(i, j, 0);
                } 
            }
            @Override
            protected int getPointOps() {
                return 9 + (smoothedWeights == null ? 1 : 2) * getInterpolationOps();
            }
        }.process();

        return convolved;
    }



    public Image2D clean(final Referenced2D beam, final double gain, final double threshold) { 
        Image2D clean = getEmptyImage();

        final int maxComponents = (int) Math.ceil(countPoints() / gain);         
        int components = 0;

        final Coordinate2D beamCenterIndex = beam.getReferenceIndex();

        Index2D peakIndex = indexOfMaxDev();
        double peakValue = get(peakIndex).doubleValue();

        Coordinate2D offset = new Coordinate2D();

        while(Math.abs(peakValue) > threshold && components < maxComponents) { 
            final double componentValue = gain * peakValue;
            offset.set(peakIndex.i() - beamCenterIndex.x(), peakIndex.j() - beamCenterIndex.y());

            addPatchAt(offset, beam, -componentValue);
            clean.add(peakIndex, componentValue);

            components++;

            peakIndex = indexOfMaxDev();
            peakValue = get(peakIndex).doubleValue();
        }

        // Scale cleaned components by the beam area...
        clean.scale(beam.getAbsSum());

        addHistory("cleaned away " + components + " components.");
        clean.addHistory("created with " + components + " clean components.");

        return clean;
    }




    public void resampleFrom(final Data2D image) {
        double scaleX = (double) image.sizeX() / sizeX();
        double scaleY = (double) image.sizeY() / sizeY();

        if(scaleX <= 1.0 && scaleY <= 1.0) {
            resampleFrom(image, new Stretch2D(scaleX, scaleY), null, null);
        }
        else {
            Gaussian2D antialias = new Gaussian2D(scaleX, scaleY, 0.0);
            resampleFrom(image, new Stretch2D(scaleX, scaleY), antialias.getBeam(new CartesianGrid2D()), null);
        }

    }

    public synchronized void resampleFrom(final Data2D image, final Transforming2D toSourceIndex, final Referenced2D beam, final Value2D weight) {

        new InterpolatingFork() {
            private Vector2D index;
            private WeightedPoint smoothedValue;

            @Override
            protected void init() { 
                super.init();
                index = new Vector2D();
                smoothedValue = new WeightedPoint();
            }

            @Override
            protected void process(int i, int j) {
                index.set(i, j);
                toSourceIndex.transform(index);

                if(!image.containsIndex((int) Math.round(index.x()), (int) Math.round(index.y()))) {
                    discard(i, j);
                }
                else if(beam == null) {
                    double value = image.valueAtIndex(index.x(), index.y(), getInterpolatorData());
                    if(java.lang.Double.isNaN(value)) discard(i, j);
                    else set(i, j, value);
                }
                else {
                    image.getSmoothedValueAtIndex(index.x(), index.y(), beam, weight, smoothedValue);          
                    if(smoothedValue.weight() > 0.0) set(i, j, smoothedValue.value());
                    else discard(i, j);
                }
            }
            @Override
            protected int getPointOps() {
                return 5 + (beam == null ? getInterpolationOps() : getPointSmoothOps(beam.sizeX() * beam.sizeY(), getInterpolationType()));
            }
        }.process();

        clearHistory();
        addHistory("resampled " + getSizeString() + " from " + image.getSizeString());
    }


    public void add(final Value2D image) {
        new Fork<Void>() {
            @Override
            protected void process(int i, int j) {
                if(image.isValid(i, j)) add(i, j, image.get(i, j));
            }
        }.process();
        addHistory("added image.");
    }

    public void addScaled(final Value2D image, final double scaling) {
        new Fork<Void>() {
            @Override
            protected void process(int i, int j) {
                if(image.isValid(i, j)) add(i, j, scaling * image.get(i, j).doubleValue());
            }
        }.process();
        addHistory("added scaled image (" + scaling + "x).");
    }

    public final void subtract(final Value2D image) {
        addScaled(image, -1.0);
    }


    public void addPatchAt(Coordinate2D index, Value2D patch, double scaling) {
        final int imin = Math.max(0, (int) Math.floor(index.x()));
        final int jmin = Math.max(0, (int) Math.floor(index.y()));
        final int imax = Math.min(sizeX(), (int) Math.ceil(index.x()) + patch.sizeX());
        final int jmax = Math.min(sizeY(), (int) Math.ceil(index.y()) + patch.sizeY());

        for(int i=imax; --i >= imin; ) for(int j=jmax; --j >= jmin; ) {
            double patchValue = patch.valueAtIndex(i - index.x(), j - index.y());
            if(!Double.isNaN(patchValue)) add(i, j, scaling * patchValue);
        }
    }


    public void addParallelPatchAt(final Coordinate2D index, final Value2D patch, final double scaling) {
        int imin = Math.max(0, (int) Math.floor(index.x()));
        int jmin = Math.max(0, (int) Math.floor(index.y()));
        int imax = Math.min(sizeX(), patch.sizeX() + (int) Math.floor(index.x()));
        int jmax = Math.min(sizeY(), patch.sizeY() + (int) Math.floor(index.y()));

        final Viewport2D view = new Viewport2D(this, imin, jmin, imax, jmax);
        view.new Fork<Void>() {

            @Override
            protected void process(int i, int j) {
                double patchValue = patch.valueAtIndex(i + view.fromi() - index.x(), j + view.fromj() - index.y());
                if(!Double.isNaN(patchValue)) add(i, j, scaling * patchValue);
            }

            @Override
            public int getPointOps() { return 11 + getInterpolationOps(); }

        }.process();

    }


    public void discardIsolated(final int minNeighbors) { 
        if(minNeighbors < 1) return;   // Nothing to do...
        validate(getNeighborValidator(minNeighbors));
    }



    public Validating<Index2D> getNeighborValidator(final int minNeighbors) {
        return new Validating<Index2D>() {

            @Override
            public boolean isValid(Index2D index) {
                int i = index.i();
                int j = index.j();
                
                if(!Data2D.this.isValid(i, j)) return false;

                int neighbours = -1;    // will iterate over the actual point too, hence the -1...

                final int fromi = Math.max(0, i-1);
                final int toi = Math.min(sizeX(), i+1);
                final int fromj = Math.max(0, j-1);
                final int toj = Math.min(sizeY(), j+1);

                for(int i1=toi; --i1 >= fromi; ) for(int j1=toj; --j1 >= fromj; ) 
                    if(Data2D.this.isValid(i1, j1)) neighbours++;

                return neighbours >= minNeighbors;         
            }

            @Override
            public void discard(Index2D index) {
                Data2D.this.discard(index);
            }

        };
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
        return new DataCrawler<Number>() {
            int i = 0, j = 0;
            
            @Override
            public final boolean hasNext() {
                if(i < sizeX()) return true;
                return j < (sizeY()-1);
            }

            @Override
            public final Number next() {
                if(i >= sizeX()) return null;
                j++;
                if(j == sizeY()) { j = 0; i++; }
                return i < sizeX() ? get(i, j) : null;
            }

            @Override
            public final void remove() {
                discard(i, j);
            }

            @Override
            public final Object getData() {
                return Data2D.this;
            }

            @Override
            public final void setCurrent(Number value) {
                set(i, j, value);
            }

            @Override
            public final boolean isValid() {
                return Data2D.this.isValid(i, j);
            }
            
            @Override
            public final void reset() {
                i = j = 0;
            }
            
        };
        
    }
    

    @Override
    public <ReturnType> ReturnType loopValid(final PointOp<Number, ReturnType> op) {
        for(int i=sizeX(); --i >= 0; ) for(int j=sizeY(); --j >= 0; ) if(isValid(i, j)) {
            op.process(get(i, j));
            if(op.exception != null) return null;
        }
        return op.getResult();
    }
    
    @Override
    public <ReturnType> ReturnType loop(final PointOp<Index2D, ReturnType> op) {
        final Index2D index = new Index2D();
        for(int i=sizeX(); --i >= 0; ) for(int j=sizeY(); --j >= 0; ) {
            index.set(i, j);
            op.process(index);
            if(op.exception != null) return null;
        }
        return op.getResult();
    }
    
    
    @Override
    public <ReturnType> ReturnType forkValid(final ParallelPointOp<Number, ReturnType> op) {
      
        Fork<ReturnType> fork = new Fork<ReturnType>() {
            private ParallelPointOp<Number, ReturnType> localOp;
            
            @Override
            public void init() {
                super.init();
                localOp = op.newInstance();
            }
            
            @Override
            protected void process(int i, int j) {
                if(isValid(i, j)) localOp.process(get(i, j));
            }
            
            @Override
            public ReturnType getLocalResult() { return localOp.getResult(); }
            

            @Override
            public ReturnType getResult() { 
                ParallelPointOp<Number, ReturnType> globalOp = op.newInstance();
                
                for(ParallelTask<ReturnType> worker : getWorkers()) {
                    globalOp.mergeResult(worker.getLocalResult());
                }
                return globalOp.getResult();
            }
            
        };
        
        fork.process();
        return fork.getResult();
    }
    

    @Override
    public <ReturnType> ReturnType fork(final ParallelPointOp<Index2D, ReturnType> op) {
      
        Fork<ReturnType> fork = new Fork<ReturnType>() {
            private ParallelPointOp<Index2D, ReturnType> localOp;
            private Index2D index;
            
            @Override
            public void init() {
                super.init();
                index = new Index2D();
                localOp = op.newInstance();
            }
            
            @Override
            protected void process(int i, int j) {
                index.set(i, j); 
                localOp.process(index);
            }
            
            @Override
            public ReturnType getLocalResult() { return localOp.getResult(); }
            

            @Override
            public ReturnType getResult() { 
                ParallelPointOp<Index2D, ReturnType> globalOp = op.newInstance();
                
                for(ParallelTask<ReturnType> worker : getWorkers()) {
                    globalOp.mergeResult(worker.getLocalResult());
                }
                return globalOp.getResult();
            }
            
        };
        
        fork.process();
        return fork.getResult();
    }

    



    public abstract class Fork<ReturnType> extends Task<ReturnType> {           

        @Override
        protected void processChunk(int index, int threadCount) {
            final int sizeX = sizeX();
            for(int i=index; i<sizeX; i += threadCount) {
                processX(i);
                Thread.yield();
            }
        }

        protected void processX(int i) {
            for(int j=sizeY(); --j >= 0; ) process(i, j);
        }

        @Override
        protected int getRevisedChunks(int chunks, int minBlockSize) {
            return super.getRevisedChunks(getPointOps(), minBlockSize);
        }

        @Override
        protected int getTotalOps() {
            return 3 + sizeX() * sizeY() * getPointOps();
        }

        protected int getPointOps() {
            return 10;
        }  

        protected abstract void process(int i, int j);
    } 




    public abstract class Loop<ReturnType> {

        public ReturnType process() {
            for(int i=sizeX(); --i >= 0; ) for(int j=sizeY(); --j >= 0; ) process(i, j);
            return getResult();
        }

        protected abstract void process(int i, int j);

        protected ReturnType getResult() { return null; }
    }


    public abstract class AveragingFork extends Fork<WeightedPoint> {
        @Override
        public WeightedPoint getResult() {
            WeightedPoint ave = new WeightedPoint();      
            for(ParallelTask<WeightedPoint> task : getWorkers()) ave.accumulate(task.getLocalResult(), 1.0);
            if(ave.weight() > 0.0) ave.endAccumulation();
            return ave;
        }
    }


    public abstract class InterpolatingFork extends Fork<Void> {
        private InterpolatorData ipolData;

        @Override
        protected void init() { ipolData = new InterpolatorData(); }


        public final InterpolatorData getInterpolatorData() { return ipolData; }
    }   



    public static class InterpolatorData {
        CubicSpline splineX, splineY;

        public InterpolatorData() {
            splineX = new CubicSpline();
            splineY = new CubicSpline();
        }

        public void centerOn(double deltax, double deltay) {
            splineX.centerOn(deltax);
            splineY.centerOn(deltay);
        }

    }



}
