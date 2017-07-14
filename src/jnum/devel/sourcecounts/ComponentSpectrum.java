package jnum.devel.sourcecounts;

import java.io.PrintStream;

import jnum.Unit;
import jnum.Util;
import jnum.util.HashCode;

public class ComponentSpectrum extends CharSpectrum implements Comparable<ComponentSpectrum> {
    /**
     * 
     */
    private static final long serialVersionUID = 336578784473353409L;
    
    private CharSpectrum template;
    private double templateSources;
    
    private double flux = Double.NaN;
    private double sources = Double.NaN;
    
    public ComponentSpectrum(ProbabilityDistribution p, double flux, double testSources) {
        super(p);
        
        this.flux = flux;
        this.templateSources = this.sources = testSources;
         
        template = new CharSpectrum(size());
        template.copyContent(this);
    }
    
    @Override
    public int hashCode() { return super.hashCode() ^ template.hashCode() ^ HashCode.from(flux) ^ HashCode.from(sources); }

    @Override
    public boolean equals(Object o) {
        if(!super.equals(o)) return false;
        if(!(o instanceof ComponentSpectrum)) return false;
        ComponentSpectrum s = (ComponentSpectrum) o;
        if(s.flux != flux) return false;
        if(s.sources != sources) return false;
        if(!Util.equals(s.template, template)) return false;
        return true;
    }
    
    public double getSources() { return sources; }
    
    public void setSources(double n) {
        if(n == sources) return;
        
        copyContent(template);
        if(n != templateSources) pow(n / templateSources);
        
        sources = n;
    }
    
    public final double getFlux() { return flux; }
    
    public final double getPopulationCount() { return sources; }
    
    @Override
    protected void printHeader(PrintStream out) {
        super.printHeader(out);
        out.println("# Flux = " + Util.e3.format(flux / Unit.Jy) + "Jy");
        out.println("# Test sources = " + Util.e3.format(sources));
    }

    @Override
    public int compareTo(ComponentSpectrum other) {
        return Double.compare(flux, other.flux);
    }
    
}
