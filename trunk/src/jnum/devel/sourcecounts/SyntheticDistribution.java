package jnum.devel.sourcecounts;

import java.util.List;
import java.util.Vector;

import jnum.math.Range;

public class SyntheticDistribution extends ProbabilityDistribution {
    /**
     * 
     */
    private static final long serialVersionUID = 7231487167532940294L;
    
    private Vector<ComponentSpectrum> components;
    private CharSpectrum composite;
    
    public SyntheticDistribution(Range range, double resolution) {
        super(range, resolution);
        components = new Vector<ComponentSpectrum>();
        composite = new CharSpectrum(size() >>> 1);
    }
  
    public void add(ComponentSpectrum spec) { 
        if(spec.size() != composite.size()) throw new IllegalArgumentException("Component size mismatch: found " 
                + spec.size() + ", expected " + composite.size() + ".");
        components.add(spec);    
    }
   
    public void clear() { components.clear(); }
    
    public List<ComponentSpectrum> getComponents() { return components; }
    
    public int components() { return components.size(); }
    
    public void recalc() {
        composite.clear();
        for(ComponentSpectrum c : components) composite.multiplyBy(c);
        composite.toProbabilities(this);
    }
    
}
