package jnum.util;

import java.util.Map;

import jnum.Symbol;
import jnum.Unit;

public class PrefixedUnit extends Unit {
    /**
     * 
     */
    private static final long serialVersionUID = 7570445362533632115L;

    private Unit baseUnit;

    private Multiplier multiplier;
    
    
    public PrefixedUnit(Unit baseUnit) {
        this(Multiplier.unity, baseUnit);
    }
   
    
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
   

    public void setMultiplier(Multiplier m) { this.multiplier = m; }


    public void multiplyBy(Multiplier m) {
        setMultiplier(Multiplier.getMultiplier(getMultiplier().value * m.value));   
    }


    public void setMultiplierFor(double value) {
        multiplyBy(Multiplier.getMultiplier(value));        
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
    

    @SuppressWarnings("hiding")
    public static enum Multiplier {

        unity ("", 1.0, ""),

        deci ("d", 0.1, "deci"),

        centi ("c", 1.0e-2, "centi"),

        milli ("m", 1.0e-3, "milli"),

        micro ("u", 1.0e-6, "micro"), 

        nano ("n", 1.0e-9, "nano"),

        pico ("p", 1.0e-12, "pico"),

        femto ("f", 1.0e-15, "femto"), 

        atto ("a", 1.0e-18, "atto"), 

        zepto ("z", 1.0e-21, "zepto"), 

        yocto ("y", 1.0e-24, "yocto"), 

        deka ("dk", 10.0, "deka"),

        hecto ("h", 100.0, "hecto"),

        kilo ("k", 1.0e3, "kilo"),

        mega ("M", 1.0e6, "Mega"), 

        giga ("G", 1.0e9, "Giga"),

        tera ("T", 1.0e12, "Tera"), 

        peta ("P", 1.0e15, "Peta"), 

        exa ("E", 1.0e18, "Exa"),

        zetta ("Z", 1.0e21, "Zetta"),

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


        public static Multiplier getMultiplier(double value) {
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
