package jnum.devel.sourcecounts;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.Arrays;

import jnum.Copiable;
import jnum.CopiableContent;
import jnum.Util;
import jnum.fft.DoubleFFT;
import jnum.math.Complex;
import jnum.math.Multiplicative;
import jnum.util.HashCode;

public class CharSpectrum implements Cloneable, Copiable<CharSpectrum>, CopiableContent<CharSpectrum>, 
Multiplicative<CharSpectrum>, Serializable {  
    /**
     * 
     */
    private static final long serialVersionUID = 7718469976559913795L;
    
    private Complex[] d; // A(x) = 1 + d(x); the zero index stores the Nyquist component...
    private DoubleFFT fft = new DoubleFFT();
    
    public CharSpectrum(int size) {
        d = new Complex[size];
        for(int i=size; --i >= 0; ) d[i] = new Complex();
    }
    
    public CharSpectrum(ProbabilityDistribution p) {
        double[] p0 = p.getData();
        double[] p1 = new double[p0.length];
        
        final int nf = p0.length >>> 1;
    
        for(int i=p0.length; --i >= 0; ) p1[i] = p0[i];
        p1[0] -= 1.0; 
        
        fft.realTransform(p1, true);
        
        d = new Complex[nf];
        d[0] = new Complex(p1[1], 0.0);
        
        for(int i=nf; --i > 0; ) {
            int j = i<<1;
            d[i] = new Complex(p1[j], p1[j|1]);
        }
    }
    
    @Override
    public int hashCode() {
        return super.hashCode() ^ HashCode.sampleFrom(d);
    }
    
    @Override
    public boolean equals(Object o) {
        if(!super.equals(o)) return false;
        if(!(o instanceof CharSpectrum)) return false;
        CharSpectrum s = (CharSpectrum) o;
        if(!Util.equals(d, s.d)) return false;
        return true;
    }
    
    public Complex getValueMinus1(int bin) {
        return d[bin];
    }
    
    public void getValue(int bin, Complex z) {
        z.set(1.0 + d[bin].x(), d[bin].y());
    }
    
    @Override
    public CharSpectrum clone() {
        try { return (CharSpectrum) super.clone(); }
        catch(CloneNotSupportedException e) { return null; }        
    }
    
    @Override
    public final CharSpectrum copy() {
        return copy(true);
    }
    
    
    
    @Override
    public CharSpectrum copy(boolean withContent) {
        CharSpectrum copy = clone();
        copy.fft = new DoubleFFT();
        copy.d = new Complex[d.length];
        if(withContent) copy.copyContent(this);
        return copy;
    }
    

    public void copyContent(CharSpectrum f) {
        for(int i=d.length; --i >= 0; ) d[i].copy(f.d[i]);        
    }  
    
    public void clear() {
        for(int i=size(); --i >= 0; ) d[i].zero();
    }
    
    public final int size() { return d.length; }
   
    public double[] toProbabilities() {
        double[] counts = new double[size()<<1];
        toProbabilities(counts);
        return counts;
    }
    
    public void toProbabilities(double[] p) { 
        p[0] = 0.0;   
        p[1] = d[0].x();
        
        for(int i=size(); --i > 0; ) {
            int j = i<<1;
            p[j] = d[i].x();
            p[j | 1] = d[i].y();
        }
        
        // Pad with zeroes as necessary;
        Arrays.fill(p, size()<<1, p.length, 0.0);
        
        fft.realTransform(p, false);
     
        for(int i=p.length; --i >= 0; ) p[i] /= size();
        p[0] += 1.0;
    }
    
    public void toProbabilities(ProbabilityDistribution p) {
        toProbabilities(p.getData());
    }
   
    
    public void pow(double n) {
        
        // (1+1/n)^(n) = e  when n >> 1
        // (1+a/n)^(n/a) = e
        // (1+a/n)^n = e^a
        // (1+x)^n = e^(nx) when nx << 1
        // x' = e^(nx) - 1 ~ nx when nx << 1;
         
        for(int i=size(); --i >= 0; ) {
            final Complex z = d[i];
            
            if(n > 100.0) {
                if(n * getValueMinus1(i).length() < 1e-5) z.scale(n);
                else {
                    z.scale(n);
                    z.expm1();
                }
            }
            else if(n < 1e-5) z.scale(n);
            else {      
                z.addX(1.0);
                z.pow(n);
                z.subtractX(1.0);
            }
        }
    }
    
    protected void printHeader(PrintStream out) {
        out.println("# Characteristic Probability Spectrum");
        out.println();
    }
    
    public void print(PrintStream out) {
        printHeader(out);
        out.println("# bin\tmag\tphase");
        out.println("# ----------------------");
        Complex z = new Complex();
        
        for(int i=0; i<d.length; i++) {
            getValue(i, z);
            out.println(i + "\t" + Util.e3.format(z.length() + "\t" + z.angle()));
        }
    }

    @Override
    public void setProduct(CharSpectrum a, CharSpectrum b) {
        copyContent(a);
        multiplyBy(b);
    }

    
    @Override
    public void multiplyBy(CharSpectrum other) {
        // (1+x) * (1+y) = 1 + x + y + xy;
        // d = x + y + xy
        final Complex xy = new Complex();
               
        for(int i=size(); --i >= 0; ) {
            xy.setProduct(d[i], other.d[i]);
            d[i].add(xy);
            d[i].add(other.d[i]);
        }   
    }
  
    
}

