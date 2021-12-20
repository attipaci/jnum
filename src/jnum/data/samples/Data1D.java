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

package jnum.data.samples;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.StringTokenizer;

import jnum.Configurator;
import jnum.Util;
import jnum.data.CubicSpline;
import jnum.data.DataCrawler;
import jnum.data.Interpolator;
import jnum.data.RegularData;
import jnum.data.SplineSet;
import jnum.data.WeightedPoint;
import jnum.data.index.Index1D;
import jnum.data.index.IndexedValues;
import jnum.data.samples.overlay.Overlay1D;
import jnum.math.CoordinateAxis;
import jnum.math.Position;
import jnum.math.Range;
import jnum.text.TableFormatter;
import jnum.util.HashCode;

public abstract class Data1D extends RegularData<Index1D, Position> implements Values1D, TableFormatter.Entries {

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
    public Position getVectorInstance() { return new Position(); }
    
    @Override
    public Samples1D newImage() {
        return (Samples1D) super.newImage();
    }
    
    @Override
    public Samples1D newImage(Index1D size, Class<? extends Number> elementType) {
        Samples1D s = Samples1D.createType(getElementType(), size.i());
        s.copyPoliciesFrom(this);
        return s;
    }

    @Override
    public Data1D newInstance() {
        return (Data1D) super.newInstance();
    }
    
    @Override
    public abstract Data1D newInstance(Index1D size);
    
    public Samples1D getSamples() {
        return getSamples(getElementType(), getInvalidValue());
    }

    public final Samples1D getSamples(Number blankingValue) {
        return getSamples(getElementType(), blankingValue);
    }

    public Samples1D getSamples(Class<? extends Number> elementType, Number invalidValue) {
        Samples1D samples = newImage(getSize(), elementType);
        
        samples.setInvalidValue(invalidValue);
        samples.setData(this);
        
        List<String> imageHistory = samples.getHistory();
        if(getHistory() != null) imageHistory.addAll(getHistory());

        return samples;
    }

 
    @Override
    public final boolean containsIndex(Position index) {
        return containsIndex((int) Math.round(index.x()));
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
    public final void discard(Index1D index) { discard(index.i()); }
    
    @Override
    public void discard(int i) {
        set(i, getInvalidValue());
    }
    
    @Override
    public final boolean isValid(Index1D index) { return isValid(index.i()); }
        
    @Override
    public boolean isValid(int i) {
        return isValid(get(i));
    }

    
    public final void paste(final Values1D source, boolean report) {
        paste(new Overlay1D(source), report);
    }

        
    @Override
    public double valueAtIndex(Position ic, SplineSet<Position> splines) { return valueAtIndex(ic.x(), splines.getSpline(0)); }
    
    @Override
    public double valueAtIndex(Index1D numerator, Index1D denominator, SplineSet<Position> splines) { 
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
        case PIECEWISE_QUADRATIC : return quadraticAtIndex(ic);
        case CUBIC_SPLINE : return spline == null ? splineAtIndex(ic) : splineAtIndex(ic, spline);
        }

        return Double.NaN;
        
    }
    
    @Override
    public final Number nearestValueAtIndex(Position ic) { return nearestValueAtIndex(ic.x()); }

    
    public Number nearestValueAtIndex(double ic) {
        int i = (int) Math.round(ic);
        if(!containsIndex(i)) return Double.NaN;
        return get(i);
    }
    
    // Bilinear interpolation
    @Override
    public final double linearAtIndex(Position ic) {
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
    public final double quadraticAtIndex(Position ic) {
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
    public final double splineAtIndex(final Position ic, SplineSet<Position> splines) {
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
    protected int getInterpolationOps(Interpolator.Type type) {
        switch(type) {
        case NEAREST : return 5;
        case LINEAR : return 25;
        case PIECEWISE_QUADRATIC : return 30;
        case CUBIC_SPLINE : return 50;
        }
        return 1;
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
    public final double valueAtIndex(double ... idx) {
        return valueAtIndex(idx[0]);
    }
    
    @Override
    public double splineAtIndex(SplineSet<Position> splines, double ... idx) {
        return splineAtIndex(idx[0], splines.getSpline(0));
    }
    
    @Override
    public int getPointSmoothOps(int beamPoints, Interpolator.Type interpolationType) {
        return 16 + beamPoints * (16 + getInterpolationOps(interpolationType));
    }
    
    @Override
    public void getSmoothedValueAtIndex(final Index1D index, final RegularData<Index1D, Position> beam, final Index1D refIndex, 
            final IndexedValues<Index1D, ?> weight, final WeightedPoint result) {   
        // More efficient than generic implementation...

        final int iR = index.i() - refIndex.i();
        final int fromi = Math.max(0, iR);
        final int toi = Math.min(size(), iR + beam.getSize(0));
        
        double sum = 0.0, sumw = 0.0;
        
        for(int i=fromi; i<toi; i++) if(isValid(i)) {
            final double w;
            
            if(weight == null) w = 1.0;
            else {
                w = weight.get(i).doubleValue();
                if(w == 0.0) continue;
            }
            
            final double wB = w * beam.get(i - iR).doubleValue();
            if(wB == 0.0) return;
            
            sum += wB * get(i).doubleValue();
            sumw += Math.abs(wB);    
        }

        result.setValue(sum / sumw);
        result.setWeight(sumw); 
    }  
    
  
    @Override
    public String getInfo() {
        return "Sample size: " + getSize() + " bins.";
    }

    public void writeASCIITable(String corePath, Grid1D grid, String yName, String nanValue) throws FileNotFoundException {
        String fileName = corePath + ".dat";

        try(PrintWriter out = new PrintWriter(new FileOutputStream(fileName))) {
            StringTokenizer header = new StringTokenizer(getInfo(), "\n");

            while(header.hasMoreTokens()) out.println("# " + header.nextToken());

            out.println("#");
            out.println("# " + getASCIITableHeader(grid, yName));

            for(int i=0; i<size(); i++)  out.println(getASCIITableEntry(i, grid, nanValue));

            out.flush();
            out.close();
        }
        
        Util.notify(this, "Written " + fileName);
    }
    
    protected String getASCIITableHeader(Grid1D grid, String yName) {
        return getXLabel(grid) + "\t" + getYLabel(yName) + "\tflag";
    }
        
    protected String getASCIITableEntry(int index, Grid1D grid, String nanValue) {
        if(grid == null) return (index+1) + "";
        
        double value = get(index).doubleValue();        
        String sValue = Double.isNaN(value) ? nanValue : Util.S6.format(value / getUnit().value());
         
        return Util.S6.format(grid.coordAt(index) / grid.getAxis().unit.value()) + "\t" +
            sValue + "\t" +
            (Double.isNaN(value) ? "1" : "0"
        );
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
        
        try(PrintWriter plot = new PrintWriter(new FileOutputStream(plotName))) {

            // Save & disable the default plot terminal while setting up the plot command...
            plot.println("set term push");
            plot.println("set term dumb");

            createGnuplot(plot, coreName, grid, yName, options);
            plot.println();

            if(options.hasOption("eps")) gnuplotEPS(plot, coreName);
            if(options.hasOption("png")) gnuplotPNG(plot, coreName, options.option("png"));

            // Re-enable the default plot terminal
            plot.println("set out");
            plot.println("set term pop");

            // Plot onto default terminal if requested.
            plot.println((options.hasOption("show") ? "" : "#")  + "replot");
            plot.close();
        }
        
        Util.notify(this, "Written " + plotName);

        if(gnuplotCommand == null) gnuplotCommand = "gnuplot";
        else gnuplotCommand = Util.getSystemPath(gnuplotCommand);

        Runtime runtime = Runtime.getRuntime();
        runtime.exec(gnuplotCommand + " -p " + plotName);
    }
    
    protected void createGnuplot(PrintWriter plot, String coreName, Grid1D grid, String yName, Configurator options) throws IOException {       
        configGnuplot(plot, coreName, grid, yName, options);     
        plot.println("plot\\\n" + getPlotCommand(coreName));
    }
        
    protected void configGnuplot(PrintWriter plot, String coreName, Grid1D grid, String yName, Configurator options) throws IOException {       
        Range xRange = new Range(0, size()-1);  
        if(grid != null) {
            xRange = new Range(grid.coordAt(0), grid.coordAt(size()-1));
            xRange.scale(1.0 / grid.getAxis().unit.value());
            plot.println("delta = " + (grid.getResolution().value() / grid.getAxis().unit.value()));
            plot.println("set xtics nomirror");
            plot.println("set x2tics nomirror");
        }
        else plot.println("delta = 1.0");
        
        if(yName == null) yName = "Value";
        
        plot.println("set xla '" + getXLabel(grid) + "'");
        plot.println("set yla '" + getYLabel(yName) + "'");        
        
        Range yRange = getRange();
        yRange.scale(1.0 / getUnit().value());
        
        yRange.grow(1.05);   // some y-padding...    

        plot.println("set xra [" + xRange.min() + ":" + xRange.max() + "]");
        plot.println("set x2ra [1:" + size() + "]");
        //plot.println("set yra [" + yRange.min() + ":" + yRange.max() + "]");   
        
        if(options.hasOption("lt")) plot.println("set style line 1 lt " + options.option("lt").getInt());
        if(options.hasOption("lw")) plot.println("set style line 1 lw " + options.option("lw").getDouble());
        if(options.hasOption("pt")) plot.println("set style line 1 pt " + options.option("pt").getInt());
        if(options.hasOption("ps")) plot.println("set style line 1 ps " + options.option("ps").getDouble());
        
        String style = options.hasOption("style") ? options.option("style").getValue() : "points";
        plot.println("set style data " + style);  
    }
    
    public String getPlotXValues() { return "1"; }
    
    public String getPlotYValues() { return "2"; }
    
    protected String getPlotCommand(String coreName) {
        return "'" + coreName + ".dat' using " + getPlotXValues() + ":" + getPlotYValues() + " notitle";        
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
        if(pngOptions.hasOption("bg")) {
            String spec = pngOptions.option("bg").getValue().toLowerCase();
            if(spec.equals("transparent")) isTransparent = true;
            else bgColor = Color.getColor(spec).getRGB(); 
        }

        int sizeX = 640;
        int sizeY = 480;
        if(pngOptions.hasOption("size")) {
            String spec = pngOptions.option("size").getValue();
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
        return Values1D.super.iterator();
    }
}
