/*******************************************************************************
 * Copyright (c) 2018 Attila Kovacs <attila[AT]sigmyne.com>.
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

package jnum.data.samples;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.StringTokenizer;

import jnum.Configurator;
import jnum.PointOp;
import jnum.Util;
import jnum.data.CubicSpline;
import jnum.data.DataCrawler;
import jnum.data.RegularData;
import jnum.data.SplineSet;
import jnum.data.WeightedPoint;
import jnum.data.samples.overlay.Overlay1D;
import jnum.math.CoordinateAxis;
import jnum.math.IntRange;
import jnum.math.Range;
import jnum.parallel.ParallelPointOp;
import jnum.parallel.ParallelTask;
import jnum.text.TableFormatter;
import jnum.util.HashCode;

public abstract class Data1D extends RegularData<Index1D, Offset1D> implements Values1D, TableFormatter.Entries {

    private CubicSpline reuseSpline;
    
    @Override
    public Data1D clone() {
        Data1D clone = (Data1D) super.clone();
        clone.reuseSpline = new CubicSpline();
        return clone;
    }
    
    @Override
    public int hashCode() {
        int hash = super.hashCode() ^ size(); 
        hash ^= HashCode.sampleFrom(this);
        return hash;
    }

    @Override
    public Index1D getIndexInstance() { return new Index1D(); }
    
    @Override
    public Offset1D getVectorInstance() { return new Offset1D(); }
     
    @Override
    public final Index1D copyOfIndex(Index1D index) { return index.copy(); }

    
    @Override
    public Samples1D newImage() {
        return Samples1D.createType(getElementType(), size());
    }
    
    @Override
    public Samples1D newImage(Index1D size, Class<? extends Number> elementType) {
        return Samples1D.createType(getElementType(), size.i());
    }

    public Samples1D getSamples() {
        return getSamples(getElementType(), getBlankingValue());
    }

    public final Samples1D getSamples(Number blankingValue) {
        return getSamples(getElementType(), blankingValue);
    }

    public final Samples1D getImage(Class<? extends Number> elementType) {
        return getSamples(elementType, getBlankingValue());
    }

    public Samples1D getSamples(Class<? extends Number> elementType, Number blankingValue) {
        Samples1D samples = Samples1D.createFrom(this, blankingValue, elementType);

        samples.copyParallel(this);
        samples.setInterpolationType(getInterpolationType());
        samples.setVerbose(isVerbose());
        samples.setUnit(getUnit());

        List<String> imageHistory = samples.getHistory();
        if(getHistory() != null) imageHistory.addAll(getHistory());

        return samples;
    }

    
    @Override
    public final int dimension() { return 1; }
    
    @Override
    public final Index1D getSize() { return new Index1D(size()); }
    
    @Override
    public final int capacity() { return size(); }

    
    @Override
    public final String toString(Index1D index) {
        return toString(index.i());
    }
    
    public String toString(int i) {
        return "[" + i + "]=" + Util.S3.format(get(i));
    }
  
    
    public final boolean conformsTo(int size) {
        return size() == size;
    }
    
    @Override
    public final boolean containsIndex(Index1D index) {
        return containsIndex(index.i());
    }
    
    @Override
    public final boolean containsIndex(Offset1D index) {
        return containsIndex((int) Math.round(index.x()));
    }
    
    public boolean containsIndex(int i) {
        if(i < 0) return false;
        if(i >= size()) return false;
        return true;
    }
    
    @Override
    public final Number getValid(final Index1D i, final Number defaultValue) {
        return getValid(i.i(), defaultValue);
    }
    
    public final Number getValid(final int i, final Number defaultValue) {
        if(!isValid(i)) return defaultValue;
        return get(i);
    }

    @Override
    public final void set(Index1D index, Number value) { set(index.i(), value); }
    
    @Override
    public final void add(Index1D index, Number value) { add(index.i(), value); }
    
    @Override
    public final Number get(Index1D index) { return get(index.i()); }
    
    @Override
    public final void clear(Index1D index) { clear(index.i()); }
    
    public void clear(int i) { set(i, 0); }

    @Override
    public final void discard(Index1D index) { discard(index.i()); }
    
    @Override
    public void discard(int i) {
        set(i, getBlankingValue());
    }
    
    @Override
    public final boolean isValid(Index1D index) { return isValid(index.i()); }
        
    @Override
    public boolean isValid(int i) {
        return isValid(get(i));
    }
    
    @Override
    public void scale(Index1D index, double factor) { scale(index.i(), factor); }
    
    public void scale(int i, double factor) {
        set(i, get(i).doubleValue() * factor);
    }
    
    public final void paste(final Values1D source, boolean report) {
        paste(new Overlay1D(source), report);
    }

    public void paste(final Data1D source, boolean report) {
        if(source == this) return;

        source.new Fork<Void>() {
            @Override
            protected void processElementAt(int i) {
                if(source.isValid(i)) set(i, source.get(i));
                else discard(i);
            }
        }.process();

        if(report) addHistory("pasted new content: " + source.getSizeString());
    }
    

    
    @Override
    public double valueAtIndex(Offset1D ic, SplineSet<Offset1D> splines) { return valueAtIndex(ic.x(), splines.getSpline(0)); }
    
    @Override
    public double valueAtIndex(Index1D numerator, Index1D denominator, SplineSet<Offset1D> splines) { 
        return valueAtIndex((double) numerator.i() / denominator.i(), splines.getSpline(0)); 
    }
    
    @Override
    public double valueAtIndex(double ic) { return valueAtIndex(ic, null); }
    
    
    public double valueAtIndex(double ic, CubicSpline spline) {
        // The nearest data point (i,j)
        final int i = (int) Math.round(ic);
        if(i < 0) return Double.NaN;
        else if(i >= size()) return Double.NaN;

        
        if(!isValid(i)) return Double.NaN;

        if(i == ic) return get(i).doubleValue();

        switch(getInterpolationType()) {
        case NEAREST : return get(i).doubleValue();
        case LINEAR : return linearAtIndex(ic);
        case QUADRATIC : return quadraticAtIndex(ic);
        case SPLINE : return spline == null ? splineAtIndex(ic) : splineAtIndex(ic, spline);
        }

        return Double.NaN;
        
    }
    
    @Override
    public final Number nearestValueAtIndex(Offset1D ic) { return nearestValueAtIndex(ic.x()); }

    
    public Number nearestValueAtIndex(double ic) {
        int i = (int) Math.round(ic);
        if(!containsIndex(i)) return Double.NaN;
        return get(i);
    }
    
    // Bilinear interpolation
    @Override
    public final double linearAtIndex(Offset1D ic) {
        return linearAtIndex(ic.x());
    }
    
    public double linearAtIndex(double ic) {        
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
    

    @Override
    public final double quadraticAtIndex(Offset1D ic) {
        return quadraticAtIndex(ic.x());
    }
    
    public double quadraticAtIndex(double ic) {
        // Find the nearest data point (i)
        final int i = (int)Math.round(ic);
     
        final double y0 = get(i).doubleValue();
        double a=0.0, b=0.0;

        if(isValid(i+1)) {
                a = 0.5 * (get(i+1).doubleValue() + get(i-1).doubleValue()) - y0;
                b = 0.5 * (get(i+1).doubleValue() - get(i-1).doubleValue());
        }
        else if(isValid(i-1)) b = y0 - get(i-1).doubleValue();

       
        ic -= i;
       
        return (a * ic + b) * ic + y0;

        // ~30 ops...
    }

    
    @Override
    public final double splineAtIndex(final Offset1D ic, SplineSet<Offset1D> splines) {
        return splineAtIndex(ic.x(), splines.getSpline(0));
    }
    
    public final double splineAtIndex(final double ic) {
        synchronized(reuseSpline) { return splineAtIndex(ic, reuseSpline); }
    }


    // Performs a bicubic spline interpolation...
    public double splineAtIndex(final double ic, CubicSpline spline) {   
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
    protected int getInterpolationOps(int type) {
        switch(type) {
        case NEAREST : return 5;
        case LINEAR : return 25;
        case QUADRATIC : return 30;
        case SPLINE : return 50;
        }
        return 1;
    }

    
    public IntRange getIndexRange() {
        int min = size(), max = -1;
        for(int i=size(); --i >= 0; ) if(isValid(i)) {
            if(i < min) min = i;
            if(i > max) max = i;
            break;
        }
        return max > min ? new IntRange(min, max) : null;
    }

    
    @SuppressWarnings("cast")
    @Override
    public final Samples1D getCropped(Index1D imin, Index1D imax) {
        return (Samples1D) getCropped(imin.i(), imax.i());
    }
    
    public Samples1D getCropped(int imin, int imax) {
        return getCropped(new Index1D(imin), new Index1D(imax));
    }   
    
  
  
    @Override
    public int getPointSmoothOps(int beamPoints, int interpolationType) {
        return 16 + beamPoints * (16 + getInterpolationOps(interpolationType));
    }
    
  
    @Override
    public String getInfo() {
        return "Sample size: " + getSize() + " bins.";
    }

    public void writeASCIITable(String corePath, Grid1D grid, String yName) throws FileNotFoundException {
        String fileName = corePath + ".dat";

        PrintWriter out = new PrintWriter(new FileOutputStream(fileName));
        StringTokenizer header = new StringTokenizer(getInfo(), "\n");

        while(header.hasMoreTokens()) out.println("# " + header.nextToken());
           
        out.println("#");
        out.println("# " + getASCIITableHeader(grid, yName));
  
        for(int i=0; i<size(); i++) out.println(getASCIITableEntry(i, grid));
        
        out.flush();
        out.close();
        
        Util.notify(this, "Written " + fileName);
    }
    
    protected String getASCIITableHeader(Grid1D grid, String yName) {
        return getXLabel(grid) + "\t" + getYLabel(yName);
    }
        
    protected String getASCIITableEntry(int index, Grid1D grid) {
        if(grid == null) return (index+1) + "";
        
        
        
        return Util.S6.format(grid.coordAt(index) / grid.getAxis().unit.value()) + "\t" +
            Util.S6.format(get(index).doubleValue() / getUnit().value());
    }

    public String getXLabel(Grid1D grid) {
        if(grid == null) return "Sample #";

        CoordinateAxis axis = grid.getAxis();
        String xLabel = axis.getLabel();
        if(axis.getUnit() != null) xLabel += " (" + axis.getUnit().name() + ")";
        
        return xLabel;
    }
    
    public String getYLabel(String name) {
        String yLabel = name;
        if(getUnit() != null) yLabel += " (" + getUnit().name() + ")";
        return yLabel;
    }
     
    public void gnuplot(String coreName, Grid1D grid, String yName, String gnuplotCommand, Configurator options) throws IOException {
        String plotName = coreName + ".plt";
        PrintWriter plot = new PrintWriter(new FileOutputStream(plotName));
                      
        // Save & disable the default plot terminal while setting up the plot command...
        plot.println("set term push");
        plot.println("set term dumb");
        
        createGnuplot(plot, coreName, grid, yName, gnuplotCommand, options);
        plot.println();
           
        if(options.isConfigured("eps")) gnuplotEPS(plot, coreName);
        if(options.isConfigured("png")) gnuplotPNG(plot, coreName, options.get("png"));
            
        // Re-enable the default plot terminal
        plot.println("set out");
        plot.println("set term pop");
        
        // Plot onto default terminal if requested.
        plot.println((options.isConfigured("show") ? "" : "#")  + "replot");
        plot.close();
        
        Util.notify(this, "Written " + plotName);

        if(gnuplotCommand == null) gnuplotCommand = "gnuplot";
        else gnuplotCommand = Util.getSystemPath(gnuplotCommand);

        Runtime runtime = Runtime.getRuntime();
        runtime.exec(gnuplotCommand + " -p " + plotName);
    }
    
    protected void createGnuplot(PrintWriter plot, String coreName, Grid1D grid, String yName, String gnuplotCommand, Configurator options) throws IOException {       
        Range xRange = new Range(0, size()-1);  
        if(grid != null) {
            xRange = new Range(grid.coordAt(0), grid.coordAt(size()-1));
            xRange.scale(1.0 / grid.getAxis().unit.value());
        }
       
        if(yName == null) yName = "Value";
        
        plot.println("set xla '" + getXLabel(grid) + "'");
        plot.println("set yla '" + getYLabel(yName) + "'");

        if(grid != null) {
            plot.println("set xtics nomirror");
            plot.println("set x2tics nomirror");
        }
        
        Range yRange = getRange();
        yRange.scale(1.0 / getUnit().value());
        
        yRange.grow(1.05);   // some y-padding...    

        plot.println("set xra [" + xRange.min() + ":" + xRange.max() + "]");
        plot.println("set x2ra [1:" + size() + "]");
        plot.println("set yra [" + yRange.min() + ":" + yRange.max() + "]");   
        
        String style = options.isConfigured("style") ? options.get("style").getValue() : "histep";
        
        if(options.isConfigured("lt")) plot.println("set style line 1 lt " + options.get("lt").getInt());
        if(options.isConfigured("lw")) plot.println("set style line 1 lw " + options.get("lw").getDouble());
        if(options.isConfigured("pt")) plot.println("set style line 1 pt " + options.get("pt").getInt());
        if(options.isConfigured("ps")) plot.println("set style line 1 ps " + options.get("ps").getDouble());
        
        plot.println("plot \\");
        plot.print("'" + coreName + ".dat' using 1:2 notitle with " + style);
    }
    

    private void gnuplotEPS(PrintWriter plot, String coreName) {
        plot.println("set term post eps enh col sol 18");
        plot.println("set out '" + coreName + ".eps'");
        plot.println("replot");

        plot.println("print 'Written " + coreName + ".eps'");
        Util.notify(this, "Written " + coreName + ".eps"); 
    }

    private void gnuplotPNG(PrintWriter plot, String coreName, Configurator pngOptions) {
        boolean isTransparent = false;
        int bgColor = Color.WHITE.getRGB();
        if(pngOptions.isConfigured("bg")) {
            String spec = pngOptions.get("bg").getValue().toLowerCase();
            if(spec.equals("transparent")) isTransparent = true;
            else bgColor = Color.getColor(spec).getRGB(); 
        }

        int sizeX = 640;
        int sizeY = 480;
        if(pngOptions.isConfigured("size")) {
            String spec = pngOptions.get("size").getValue();
            StringTokenizer tokens = new StringTokenizer(spec, "xX*:, ");
            sizeX = sizeY = Integer.parseInt(tokens.nextToken());
            if(tokens.hasMoreTokens()) sizeY = Integer.parseInt(tokens.nextToken());                
        }

        plot.println("set term pngcairo enhanced color " + (isTransparent ? "" : "no") + "transparent" +
                " background '#" + Integer.toHexString(bgColor).substring(2) + "' fontscale 1.0" + 
                " butt size " + sizeX + "," + sizeY);
        plot.println("set out '" + coreName + ".png'");
        plot.println("replot");
        plot.println("print 'Written " + coreName + ".png'");   
        Util.notify(this, "Written " + coreName + ".png");
    }
    
   
    
    @Override
    public DataCrawler<Number> iterator() {
        return new DataCrawler<Number>() {
            int i = 0;
            
            @Override
            public final boolean hasNext() {
                return i < (size() - 1);
            }

            @Override
            public final Number next() {
                if(i >= size()) return null;
                i++;
                return i < size() ? get(i) : null;
            }

            @Override
            public final void remove() {
                discard(i);
            }

            @Override
            public final Object getData() {
                return Data1D.this;
            }

            @Override
            public final void setCurrent(Number value) {
                set(i, value);
            }
            
            @Override
            public final boolean isValid() {
                return Data1D.this.isValid(i);
            }

            @Override
            public final void reset() {
                i = 0;
            }
            
        };
        
    }
    
    
    
    @Override
    public <ReturnType> ReturnType loopValid(final PointOp<Number, ReturnType> op, Index1D from, Index1D to) {
        for(int i=to.i(); --i >= from.i(); ) if(isValid(i)) op.process(get(i));
        return op.getResult();
    }
    
    @Override  
    public <ReturnType> ReturnType loop(final PointOp<Index1D, ReturnType> op, Index1D from, Index1D to) {
        final Index1D index = new Index1D();
        for(int i=to.i(); --i >= from.i(); ) {
            index.set(i);
            op.process(index);
            if(op.exception != null) return null;
        }
        return op.getResult();
    }
    
    @Override
    public <ReturnType> ReturnType forkValid(final ParallelPointOp<Number, ReturnType> op, Index1D from, Index1D to) {
      
        Fork<ReturnType> fork = new Fork<ReturnType>(from, to) {
            private ParallelPointOp<Number, ReturnType> localOp;
            
            @Override
            public void init() {
                super.init();
                localOp = op.newInstance();
            }
            
            @Override
            protected void processElementAt(int i) {
                if(isValid(i)) localOp.process(get(i));
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
    public <ReturnType> ReturnType fork(final ParallelPointOp<Index1D, ReturnType> op, Index1D from, Index1D to) {
      
        Fork<ReturnType> fork = new Fork<ReturnType>(from, to) {
            private ParallelPointOp<Index1D, ReturnType> localOp;
            private Index1D index;
            
            @Override
            public void init() {
                super.init();
                index = new Index1D();
                localOp = op.newInstance();
            }
            
            @Override
            protected void processElementAt(int i) {
                index.set(i); 
                localOp.process(index);
            }
          
            @Override
            public ReturnType getLocalResult() { return localOp.getResult(); }
            

            @Override
            public ReturnType getResult() { 
                ParallelPointOp<Index1D, ReturnType> globalOp = op.newInstance();
                
                for(ParallelTask<ReturnType> worker : getWorkers()) {
                    globalOp.mergeResult(worker.getLocalResult());
                }
                return globalOp.getResult();
            }
            
        };
        
        fork.process();
        return fork.getResult();
    }

    
    
    
    
    
    
    

    public abstract class Loop<ReturnType> extends AbstractLoop<ReturnType> {
        public Loop() {}
        
        public Loop(Index1D from, Index1D to) { super(from, to); }
        
        @Override
        public ReturnType process() {
            for(int i=to.i(); --i >= from.i(); ) process(i);
            return getResult();
        }

        protected abstract void process(int i);

        @Override
        protected ReturnType getResult() { return null; }
    }

    

    public abstract class Fork<ReturnType> extends AbstractFork<ReturnType> {           
        
        public Fork() { }
        
        public Fork(Index1D from, Index1D to) { super(from, to); }
        
        public Fork(int from, int to) { super(new Index1D(from), new Index1D(to)); }
        
        @Override
        protected void processChunk(int index, int threadCount) {
            for(int i=from.i() + index; i<to.i(); i += threadCount) processElementAt(i);
        }


        @Override
        protected int getRevisedChunks(int chunks, int minBlockSize) {
            return super.getRevisedChunks(getPointOps(), minBlockSize);
        }

        @Override
        protected int getTotalOps() {
            return 3 + size() * getPointOps();
        }

        protected int getPointOps() {
            return 10;
        }  

        protected abstract void processElementAt(int i);
    } 





    public abstract class AveragingFork extends Fork<WeightedPoint> {
        public AveragingFork() {}
        
        public AveragingFork(int from, int to) { super(from, to); }
        
        @Override
        public WeightedPoint getResult() {
            WeightedPoint ave = new WeightedPoint();      
            for(ParallelTask<WeightedPoint> task : getWorkers()) ave.accumulate(task.getLocalResult(), 1.0);
            if(ave.weight() > 0.0) ave.endAccumulation();
            return ave;
        }
    }


   


    
}
