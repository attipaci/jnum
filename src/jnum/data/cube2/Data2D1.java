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

package jnum.data.cube2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jnum.NonConformingException;
import jnum.PointOp;
import jnum.Unit;
import jnum.data.cube.Index3D;
import jnum.data.CubicSpline;
import jnum.data.Referenced;
import jnum.data.SplineSet;
import jnum.data.RegularData;
import jnum.data.Statistics;
import jnum.data.WeightedPoint;
import jnum.data.cube.Data3D;
import jnum.data.image.Data2D;
import jnum.data.image.Image2D;
import jnum.data.samples.Index1D;
import jnum.data.samples.Offset1D;
import jnum.data.samples.Samples1D;
import jnum.math.IntRange;
import jnum.math.Vector3D;

public abstract class Data2D1<ImageType extends Data2D> extends Data3D {

    // TODO 2D+1D split methods...
    /*
     * - smoothXY(), filterXY()
     * 
     */

    private ImageType template;
    private ArrayList<ImageType> stack;
    
      
    public Data2D1() {
        stack = new ArrayList<>();
    }

    public Data2D1(int initialPlanesCapacity) {
        this();
        stack.ensureCapacity(initialPlanesCapacity);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Data2D1<ImageType> clone() {
        Data2D1<ImageType> clone = (Data2D1<ImageType>) super.clone();
        if(stack != null) clone.stack = (ArrayList<ImageType>) stack.clone();
        return clone;
    }
    
    @Override
    public void addLocalUnit(Unit u, String altNames) {
        super.addLocalUnit(u, altNames);
        if(template != null) template.addLocalUnit(u, altNames);
    }
    
    @Override
    public void addLocalUnit(Unit u) {
        super.addLocalUnit(u);
        if(template != null) template.addLocalUnit(u);
    }
    
    @Override
    public void setUnit(String name) {
        if(template == null) super.setUnit(name);
        else {
            template.setUnit(name);
            super.setUnit(template.getUnit());
        }
    }
    
    @Override
    public void setUnit(Unit u) {
        super.setUnit(u);
        if(template != null) template.setUnit(u);
    }
    
    public abstract ImageType newPlaneInstance();
    
    public final ImageType createPlane() {
        ImageType plane = newPlaneInstance();
        applyTemplateTo(plane);
        return plane;        
    }
    
    public ImageType getPlaneTemplate() { 
        if(template == null) {
            template = newPlaneInstance();
            Map<String, Unit> localUnits = getLocalUnits();
            if(localUnits != null) for(Unit u : localUnits.values()) template.addLocalUnit(u);
            template.setUnit(getUnit());
        }
        return template;    
    }
    
    
    protected void applyTemplateTo(ImageType image) {
        Map<String, Unit> templateLocalUnits = getPlaneTemplate().getLocalUnits();
        if(templateLocalUnits != null) for(Unit u : templateLocalUnits.values()) image.addLocalUnit(u);
        image.setUnit(template.getUnit().name());
    }

    public void makeConsistent() {
        for(ImageType image : getPlanes()) if(image != template) applyTemplateTo(image);
    }


    
    public ArrayList<ImageType> getPlanes() { return stack; }


    public final ImageType getPlane(int index) {
        return stack.get(index);
    }


    public final void setPlane(int index, ImageType image) {
        stack.set(index, image);
    }

    public void addPlane(ImageType image) throws NonConformingException {
        if(!stack.isEmpty()) if(image.sizeX() != sizeX() || image.sizeY() != sizeY()) 
            throw new NonConformingException("irregular stack addition.");

        if(stack.isEmpty()) template = image;
        else applyTemplateTo(image);
        
        stack.add(image);
    }

    public void trim(int count) {
        for(int i=count; --i >= 0 && !stack.isEmpty(); ) stack.remove(stack.size() - 1);
    }

    public void trimToSize() {
        stack.trimToSize();
    }

    @Override
    public Class<? extends Number> getElementType() { return getPlaneTemplate().getElementType(); }

    @Override
    public int compare(Number a, Number b) {
        return getPlaneTemplate().compare(a, b);
    }


    @Override
    public int sizeX() { return getPlaneTemplate().sizeX(); }

    @Override
    public int sizeY() { return getPlaneTemplate().sizeY(); }

    @Override
    public int sizeZ() { return stack.size(); }
    
    
    public void setSizeZ(int sizeZ) {
        getPlanes().clear();
        if(sizeZ > 0) {       
            getPlanes().ensureCapacity(sizeZ);
            for(int k=sizeZ; --k >= 0; ) addPlane(createPlane());
            addHistory("Z-size: " + sizeZ);
        }
        else addHistory("Set null size.");
    }
    
    public final void destroy() { setSizeZ(0); }
    
    public void setPlanes(ImageType[] planes) {
        stack.clear();
        for(int i=0; i<planes.length; i++) addPlane(planes[i]);
        addHistory("Set " + planes.length + "planes.");
    }

    public void setPlanes(List<ImageType> planes) {
        stack.clear();
        for(int i=0; i<planes.size(); i++) addPlane(planes.get(i));
        addHistory("Set " + planes.size() + "planes.");
    }


    @Override
    public boolean isValid(int i, int j, int k) { return getPlane(k).isValid(i, j); }


    @Override
    public void discard(int i, int j, int k) { getPlane(k).discard(i, j); }


    @Override
    public Number get(int i, int j, int k) {
        return getPlane(k).get(i, j);
    }


    @Override
    public void set(int i, int j, int k, Number value) {
        getPlane(k).set(i, j, value);
    }


    @Override
    public void clear(int i, int j, int k) { 
        getPlane(k).clear(i, j);
    }


    @Override
    public void add(int i, int j, int k, Number value) {
        getPlane(k).add(i, j, value);
    }

    @Override
    public void scale(int i, int j, int k, double factor) { 
        getPlane(k).scale(i, j, factor);
    }



    @Override
    public Number nearestValueAtIndex(double i, double j, double k) {
        return getPlane((int) Math.round(k)).get((int) Math.round(i), (int) Math.round(j));   
    }


    @Override
    public Data2D1<Image2D> getCore() {
        Data2D1<Image2D> data = new Data2D1<Image2D>(sizeZ()) {
            @Override
            public Image2D newPlaneInstance() { return null; }
        };

        for(int i=0; i<sizeZ(); i++) data.addPlane(getPlane(i).getImage());

        return data;
    }


    public void cropZ(int fromk, int tok) {
        fromk = Math.max(0, fromk);
        tok = Math.min(sizeZ() -1, tok);

        ArrayList<ImageType> planes = new ArrayList<>(tok - fromk + 1);
        for(int k = fromk; k <= tok; k++) planes.add(getPlane(k));

        setPlanes(planes);
    }

    public void autoCropZ() {
        IntRange z = getZIndexRange();
        if(z == null) return; 
        cropZ((int)z.min(), (int) z.max());
    }

    public final synchronized void smoothZ(Referenced<Index1D, Offset1D> beam) {
        smoothZ(beam.getData(), beam.getReferenceIndex().value());
    }

    public final synchronized void smoothZ(RegularData<Index1D, Offset1D> beam, Offset1D refIndex) {
        smoothZ(beam, refIndex.value());
    }

    public synchronized void smoothZ(RegularData<Index1D, Offset1D> beam, double refIndex) {
        // TODO
        addHistory("z-smoothed");
    }

    public final synchronized void fastSmoothZ(Referenced<Index1D, Offset1D> beam, int step) {
        fastSmoothZ(beam.getData(), beam.getReferenceIndex().value(), step);        
    }

    public final synchronized void fastSmoothZ(RegularData<Index1D, Offset1D> beam, Offset1D refIndex, int step) {
        fastSmoothZ(beam, refIndex.value(), step);
    }

    public synchronized void fastSmoothZ(RegularData<Index1D, Offset1D> beam, double refIndex, int step) {
        // TODO
        addHistory("z-smoothed (fast method)");
    }



    public Data2D getSumZ() { return getSumZ(0, sizeZ()); }

    public Data2D getSumZ(int fromZ, int toZ) {
        final int fromk = Math.max(0, fromZ);
        final int tok = Math.min(sizeZ(), toZ);

        if(tok < fromk) return null;

        final ImageType sum = newPlaneInstance();

        sum.new Fork<Void>() {
            @Override
            protected void process(int i, int j) {
                for(int k=fromk; k < tok; k++) {
                    final Data2D plane = getPlane(k);
                    if(plane.isValid(i, j)) sum.add(i, j, plane.get(i, j));
                }
            }
        }.process();

        return sum;
    }


    public Data2D getAverageZ() {
        return getAverageZ(0, sizeZ());
    }

    public Data2D getAverageZ(int fromZ, int toZ) {
        final int fromk = Math.max(0, fromZ);
        final int tok = Math.min(sizeZ(), toZ);

        if(tok < fromk) return null;

        final ImageType sum = newPlaneInstance();

        sum.new Fork<Void>() {
            @Override
            protected void process(int i, int j) {
                int n = 0;
                for(int k=tok; --k >= fromk; ) {
                    final Data2D plane = getPlane(k);
                    if(!plane.isValid(i, j)) return;
                    sum.add(i, j, plane.get(i, j));
                    n++;
                }
                sum.scale(i, j, 1.0 / n);
            }
        }.process();

        return sum;   
    }

    public Data2D getMedianZ() {
        return getMedianZ(0, sizeZ());
    }


    public Data2D getMedianZ(int fromZ, int toZ) {
        final int fromk = Math.max(0, fromZ);
        final int tok = Math.min(sizeZ(), toZ);

        if(tok < fromk) return null;

        final ImageType sum = newPlaneInstance();

        sum.new Fork<Void>() {
            private double[] sorter;

            @Override
            public void init() {
                super.init();
                sorter = new double[tok - fromk];
            }

            @Override
            protected void process(int i, int j) {
                int n = 0;
                for(int k=tok; --k >= fromk; ) {
                    final Data2D plane = getPlane(k);
                    if(plane.isValid(i, j)) sorter[n++] = plane.get(i, j).doubleValue();
                }
                if(n > 0) sum.set(i,  j, Statistics.Inplace.median(sorter, 0, n));
            }
        }.process();

        return sum;   
    }

    

    public Samples1D getZSumSamples() {
        Samples1D z = Samples1D.createType(getElementType(), sizeZ());
        z.setUnit(getUnit());
        for(int k=sizeZ(); --k >= 0; ) z.set(k, getPlane(k).getSum());
        return z;
    }
    
    public Samples1D[] getZMeanSamples() {
        Samples1D mean = Samples1D.createType(getElementType(), sizeZ());
        Samples1D weight = Samples1D.createType(getElementType(), sizeZ());
        mean.setUnit(getUnit());
        weight.setUnit(getUnit());
        for(int k=sizeZ(); --k >= 0; ) {
            WeightedPoint p = getPlane(k).getMean();
            mean.set(k, p.value());
            weight.set(k, p.weight());
        }
        return new Samples1D[] { mean, weight };
    }
    

    @Override
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
        for(int k=tok; --k >= fromk; ) {
            final double wz = splineZ.coefficientAt(k);
            final ImageType slice = getPlane(k);

            for(int i=toi; --i >= fromi; ) {
                final double wzx = wz * splineX.coefficientAt(i);
                for(int j=toj; --j >= fromj; ) if(slice.isValid(i, j)) {
                    final double w = wzx * splineY.coefficientAt(j);         
                    sum += w * slice.get(i, j).doubleValue();
                    sumw += w;
                }
            }
        }

        return sum / sumw;

        // ~800 ops...
    }



    @Override
    public <ReturnType> ReturnType loop(final PointOp<Index3D, ReturnType> op, Index3D from, Index3D to) {
        final Index3D index = new Index3D();
        for(int k=to.k(); --k >= from.k(); ) {
            for(int i=to.i(); --i >= from.i(); ) for(int j=to.j(); --j >= from.j(); ) {
                index.set(i, j, k);
                op.process(index);
                if(op.exception != null) return null;
            }
        }
        return op.getResult();
    }


    @Override
    public <ReturnType> ReturnType loopValid(final PointOp<Number, ReturnType> op, Index3D from, Index3D to) {
        for(int k=to.k(); --k >= from.k(); ) {
            for(int i=to.i(); --i >= from.i(); ) for(int j=to.j(); --j >= from.j(); )
                if(isValid(i, j, k)) op.process(get(i, j, k));
        }
        return op.getResult();
    }


    public abstract class ForkZ<ReturnType> extends Task<ReturnType> {  
        private int fromZ, toZ;

        public ForkZ() { this(0, sizeZ()); }

        public ForkZ(int fromZ, int toZ) {
            this.fromZ = fromZ;
            this.toZ = toZ;
        }

        @Override
        protected void processChunk(int index, int threadCount) {
            for(int i=fromZ + index; i<toZ; i += threadCount) {
                processPlane(i);
            }
        }

        protected abstract void processPlane(int k);

        @Override
        protected int getRevisedChunks(int chunks, int minBlockSize) {
            return super.getRevisedChunks(getPointOps(), minBlockSize);
        }

        @Override
        protected int getTotalOps() {
            return 3 + capacity() * getPointOps();
        }

        protected int getPointOps() {
            return 10;
        }  

    } 
    
 
}
