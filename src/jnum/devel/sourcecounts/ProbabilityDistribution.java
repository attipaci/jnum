package jnum.devel.sourcecounts;


import java.io.PrintStream;
import java.io.Serializable;

import jnum.Copiable;
import jnum.CopiableContent;
import jnum.ExtraMath;
import jnum.Util;
import jnum.data.Histogram;
import jnum.data.sequence.Grid1D;
import jnum.math.Range;
import jnum.util.HashCode;


public class ProbabilityDistribution implements Cloneable, Copiable<ProbabilityDistribution>, 
    CopiableContent<ProbabilityDistribution>, Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 3557219014359958416L;
    
    private double resolution;
    private double from;
    private double[] data;
   
    public ProbabilityDistribution(Range targetRange, double resolution) {
        setRange(targetRange, resolution);
    }
    
    public ProbabilityDistribution(Histogram histogram, Grid1D grid, int paddingFactor) {
        Range r = new Range(grid.valueAt(histogram.getMinBinValue()), grid.valueAt(histogram.getMaxBinValue()));
        if(paddingFactor > 1.0) r.grow(paddingFactor);
       
        double[] data = getData();
       
        for(int i=data.length; --i >= 0; ) data[i] = histogram.countsFor(grid.valueAt((double) i));
        renormalize();
    }
    
    @Override
    public int hashCode() {
        return super.hashCode() ^ HashCode.from(resolution) ^ HashCode.from(from) ^ HashCode.sampleFrom(data);
    }
    
    @Override
    public boolean equals(Object o) {
        if(!super.equals(o)) return false;
        if(!(o instanceof ProbabilityDistribution)) return false;
        ProbabilityDistribution p = (ProbabilityDistribution) o;
        if(p.resolution != resolution) return false;
        if(p.from != from) return false;
        if(p.data.length != data.length) return false;
        if(!Util.equals(p.data, data)) return false;
        return true;        
    }
    
    @Override
    public Object clone() {
        try { return super.clone(); }
        catch(CloneNotSupportedException e) { return null; }
    }
    
    @Override
    public ProbabilityDistribution copy() {
        return copy(true);
    }
    
    @Override
    public ProbabilityDistribution copy(boolean withContent) {
        ProbabilityDistribution copy = (ProbabilityDistribution) clone();
        if(data != null) {
            if(withContent) copy.data = Util.copyOf(data);
            else copy.data = new double[data.length];
        }
        return copy;
    }
    
    public double getResolution() { return resolution; }
    
    public double minOrdinate() { return from; }
    
    public double maxOrdinate() { return from + size() * getResolution(); }
    
    private void setRange(Range targetRange, double resolution) {
        int n = 1 + (int) Math.floor(targetRange.span() / resolution);
        data = new double[ExtraMath.pow2ceil(n)];
        int padding = (data.length - n) >>> 1;
        
        this.from = targetRange.min() - padding * resolution;
        this.resolution = resolution;
    }
  
    public Range getRange() {
        return new Range(from, from + resolution * size());
    }
    
    protected double[] getData() { return data; }
       
    public int size() { return data.length; }
    
    public double getBinProbability(int i) {
        return data[i];
    }
    
    public double getOrdinate(int bin) {
        return from + bin * getResolution();
    }
    
    public int indexOf(double ordinate) {
        return (int) Math.floor((ordinate - from) / resolution);
    }
    
    public void renormalize() {
        double norm = 0.0;
        for(int i=size(); --i >= 0; ) norm += data[i];
        
        final double inorm = 1.0 / norm;
        for(int i=size(); --i >= 0; ) data[i] *= inorm;
    }
    
    public CharSpectrum getSpectrum() {
        return new CharSpectrum(this);
    }
    
    
    public double getChiSquaredFrom(ProbabilityDistribution other, int nObs) {
        if(other.resolution != resolution) throw new IllegalArgumentException("Mismatched probability resolution");
        if(other.from != from || other.size() != size()) throw new IllegalArgumentException("Mismatched probability range");
         
        double chi2 = 0.0;
        for(int i=size(); --i >= 0; ) {
            // p = n / N
            // sigma(p) = sigma(n) / N = sqrt(n) / N = sqrt(pN) / N = sqrt(p / N)
            double dev = (data[i] - other.data[i]) / Math.sqrt(data[i] / nObs);
            chi2 += dev * dev;
        }
        return chi2;
    }
 
    protected void printHeader(PrintStream out) {
        out.println("# Probability Distribution");
        out.println();
    }
    
    public void print(PrintStream out) {
        printHeader(out);
        out.println("# bin\tprob.");
        out.println("# --------------");
        for(int i=0; i<data.length; i++) out.println(i + "\t" + Util.s3.format(data[i])); 
    }
    
}
