package jnum.util;

import java.util.Map;

import jnum.Symbol;
import jnum.Unit;


/**
 * A physical unit composed of a base unit and a standard multiplier prefix. For example <b>km</b>, which is composed of 
 * <b>k</b> (kilo) meaning a thousand, and <b>m</b> (meter) the unit for distance. Or <b>GeV</b>, composed of 
 * the prefix <b>G</b> (giga) for 10<sup>9</sup> and <b>eV</b> (electronVolt) a unit of energy commonly used in particle
 * physics. 
 * 
 * @author Attila Kovacs
 *
 */
public class PrefixedUnit extends Unit {
    /**
     * 
     */
    private static final long serialVersionUID = 7570445362533632115L;
    
    /** the base unit component */
    private Unit baseUnit;
    /** the multiplier prefix */
    private Multiplier multiplier;
    
    /** 
     * Creates a new prefixed unit, using the specified base unit and the default unity multiplier.
     * 
     * @param baseUnit      the base unit for this prefixed unit.
     */
    public PrefixedUnit(Unit baseUnit) {
        this(Multiplier.unity, baseUnit);
    }
   
    /**
     * creates a new prefixed unit from combining the specified multiplier prefix and based unit.
     * 
     * @param m
     * @param baseUnit
     */
    public PrefixedUnit(Multiplier m, Unit baseUnit) {
        setMultiplier(m);
        this.baseUnit = baseUnit;
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ multiplier.hashCode();
    }

    
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null) return false;
        if(!o.getClass().equals(getClass())) return false;
        
        PrefixedUnit u = (PrefixedUnit) o;
        if(u.multiplier != multiplier) return false;
        return true;
    }
    

    @Override
    public String name() { return multiplier.letterCode() + baseUnit.name(); }
    
    @Override
    public double value() { return multiplier.value() * baseUnit.value(); }
   
    /**
     * Sets a new standard multiplier prefix for int this unit.
     * 
     * @param m     the new multiplier to use with the base unit.
     */
    public void setMultiplier(Multiplier m) { this.multiplier = m; }

    public void setMultiplier(double value) throws IllegalArgumentException {
        this.multiplier = Multiplier.from(value);        
    }


    public Multiplier getMultiplier() { return multiplier; }


   
    public static Multiplier getMultiplier(char c) {    
        switch(c) {
        case ' ': return Multiplier.unity;
        case 'd': return Multiplier.deci; // could also be deka
        case 'c': return Multiplier.centi;
        case 'm': return Multiplier.milli;
        case 'u': return Multiplier.micro;
        case Symbol.mu: return Multiplier.micro;
        case 'n': return Multiplier.nano;
        case 'p': return Multiplier.pico;
        case 'f': return Multiplier.femto;
        case 'a': return Multiplier.atto;
        case 'z': return Multiplier.zepto;
        case 'y': return Multiplier.yocto;
        case 'h': return Multiplier.hecto;
        case 'k': return Multiplier.kilo;
        case 'M': return Multiplier.mega;
        case 'G': return Multiplier.giga;
        case 'T': return Multiplier.tera;
        case 'P': return Multiplier.peta;
        case 'E': return Multiplier.exa;
        case 'Z': return Multiplier.zetta;
        case 'Y': return Multiplier.yotta;
        // and some commonly used prefixes with the wrong case...
        case 'K': return Multiplier.kilo;
        
        default: return null;
        }
    }

    
    public static Unit createFrom(String spec, Map<String, Unit> lookup) throws IllegalArgumentException {
        Unit u = lookup.get(spec);
        if(u != null) return u;
        
        // Try with multiplier;
        Multiplier m = getMultiplier(spec.charAt(0));
        if(m == null) throw new IllegalArgumentException("No matching unit: " + spec);
        
        u = lookup.get(spec.substring(1));
        if(u == null) throw new IllegalArgumentException("No matching unit: " + spec);
        
        return new PrefixedUnit(m, u); 
    }
    

    /**
     * Standard unit multiplier prefixes.
     * 
     * @author Attila Kovacs
     *
     */
    @SuppressWarnings("hiding")
    public static enum Multiplier {
        
        /** 1.0 */
        unity ("", 1.0, ""),

        /** 10<sup>-1</sup> */
        deci ("d", 0.1, "deci"),

        /** 10<sup>-2</sup> */
        centi ("c", 1.0e-2, "centi"),

        /** 10<sup>-3</sup> */
        milli ("m", 1.0e-3, "milli"),

        /** 10<sup>-6</sup> */
        micro ("u", 1.0e-6, "micro"), 

        /** 10<sup>-9</sup> */
        nano ("n", 1.0e-9, "nano"),

        /** 10<sup>-12</sup> */
        pico ("p", 1.0e-12, "pico"),

        /** 10<sup>-15</sup> */
        femto ("f", 1.0e-15, "femto"), 

        /** 10<sup>-18</sup> */
        atto ("a", 1.0e-18, "atto"), 

        /** 10<sup>-21</sup> */
        zepto ("z", 1.0e-21, "zepto"), 

        /** 10<sup>-24</sup> */
        yocto ("y", 1.0e-24, "yocto"), 

        /** 10 */
        deka ("dk", 10.0, "deka"),

        /** 100 */
        hecto ("h", 100.0, "hecto"),

        /** 10<sup>3</sup> */
        kilo ("k", 1.0e3, "kilo"),

        /** 10<sup>6</sup> */
        mega ("M", 1.0e6, "Mega"), 

        /** 10<sup>9</sup> */
        giga ("G", 1.0e9, "Giga"),

        /** 10<sup>12</sup> */
        tera ("T", 1.0e12, "Tera"), 

        /** 10<sup>15</sup> */
        peta ("P", 1.0e15, "Peta"), 

        /** 10<sup>18</sup> */
        exa ("E", 1.0e18, "Exa"),

        /** 10<sup>21</sup> */
        zetta ("Z", 1.0e21, "Zetta"),

        /** 10<sup>24</sup> */
        yotta ("Y", 1.0e24, "Yotta");

        final double value;

        final String letterCode;

        final String name;


        private Multiplier(String c, double multiplier, String name) {
            this.letterCode = c;
            this.value = multiplier;
            this.name = name;
        }


        public double value() { return value; }


        public String letterCode() { return letterCode; }


        public String standardName() { return name; }


        public static Multiplier from(double value) {
            if(value < 1e-21) return yocto;
            if(value < 1e-18) return zepto;
            if(value < 1e-15) return atto;
            if(value < 1e-12) return femto;
            if(value < 1e-9) return pico;
            if(value < 1e-6) return nano;
            if(value < 1e-3) return micro;
            if(value < 1.0) return milli;
            if(value < 1e3) return unity;
            if(value < 1e6) return kilo;
            if(value < 1e9) return mega;
            if(value < 1e12) return giga;
            if(value < 1e15) return tera;
            if(value < 1e18) return peta;
            if(value < 1e21) return exa;
            if(value < 1e24) return zetta;
            return yotta;
        }

    }
    
}
