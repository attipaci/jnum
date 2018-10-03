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


package jnum;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;

import jnum.util.HashCode;
import jnum.util.PrefixedUnit;

// TODO: Auto-generated Javadoc
// TODO Convert to Enum?
/**
 * The Class Unit.
 * 
 * FITS units definitions: https://astropy.readthedocs.org/en/v0.1/wcs/units.html
 */
public class Unit extends Number implements Serializable, Cloneable, Copiable<Unit> {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1573657596441176858L;

    /** The Constant standardUnits. */
    protected final static Hashtable<String, Unit> standardUnits = new Hashtable<String, Unit>();

    // Usage Example:
    //    - To convert degrees to hourAngle : hourAngle = (degrees * Unit.deg) / Unit.hourAngle

    /** The name. */
    private String name;

    /** The value. */
    private double value;

    /**
     * Instantiates a new unit.
     */
    public Unit() { this(null, Double.NaN); }

    public Unit(String name) {
        this(name, Double.NaN);
    }

    /**
     * Instantiates a new unit.
     *
     * @param name the name
     * @param value the value
     */
    public Unit(String name, double value) { 
        this.value = value; 
        this.name = name; 
    }
   

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    public Unit clone() {
        try { return (Unit) super.clone(); }
        catch(CloneNotSupportedException e) { return null; }        
    }

    /* (non-Javadoc)
     * @see jnum.Copiable#copy()
     */
    @Override
    public Unit copy() {
        return clone();
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(!o.getClass().equals(getClass())) return false;
        
        Unit u = (Unit) o;
        if(!u.name().equals(name())) return false;
        if(u.value() != value()) return false;
       
        
        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return super.hashCode() ^ name().hashCode() ^ HashCode.from(value);
    }

    /**
     * Name.
     *
     * @return the string
     */
    public String name() { return name; }

    
    public double value() { return value; }


    @Override
    public final double doubleValue() {
        return value();
    }

    @Override
    public final float floatValue() {
        return (float) value();
    }

    @Override
    public int intValue() {
        return (int) Math.round(value());
    }

    @Override
    public long longValue() {
       return Math.round(value);
    }
 
  

    /**
     * Register.
     */
    public void register() throws IllegalArgumentException {
        registerTo(standardUnits);
    }
    
    public void registerTo(Hashtable<String, Unit> lookup) throws IllegalArgumentException {
        if(name() == null) throw new IllegalArgumentException("Unnamed unit.");
        
        // Register only base unit names, not compound names or exponential ones... 
        if(isBaseType(name())) standardUnits.put(name(), this);
        else throw new IllegalArgumentException("Not a base unit: " + name());
    }
    

    public void registerTo(Hashtable<String, Unit> units, String names) {
        StringTokenizer values = new StringTokenizer(names, " \t,;");
        while(values.hasMoreTokens()) {
            String spec = values.nextToken();
            if(isBaseType(spec)) units.put(spec, this);
        }
    }
    
    
    static boolean isBaseType(String spec) {
        // Register only base unit names, not compound names or exponential ones... 
        StringTokenizer parts = new StringTokenizer(spec, " \t*/^(){}[]=");
        if(parts.countTokens() == 1) {
            String part = parts.nextToken();
            if(part.length() == spec.length()) return true;
        }
        return false;
    }

    /**
     * Gets the.
     *
     * @param id the id
     * @return the unit
     */
    public static Unit get(String id) {
        return get(id, standardUnits);
    }

    /**
     * Gets the.
     *
     * @param id the id
     * @param baseUnits the base units
     * @return the unit
     * @throws IllegalArgumentException the illegal argument exception
     */
    public static Unit get(String id, Map<String, Unit> baseUnits) throws IllegalArgumentException {		
        if(baseUnits == null) return PrefixedUnit.createFrom(id, standardUnits);
        
        try { return PrefixedUnit.createFrom(id, baseUnits); }
        catch(IllegalArgumentException e) { return PrefixedUnit.createFrom(id, standardUnits); }
    }

    

  

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "[" + name() + "]";
    }


    // Unit Prefixes
    /** The Constant deci. */
    public final static double deci = 0.1;

    /** The Constant centi. */
    public final static double centi = 1.0e-2;

    /** The Constant milli. */
    public final static double milli = 1.0e-3;

    /** The Constant micro. */
    public final static double micro = 1.0e-6;

    /** The Constant nano. */
    public final static double nano = 1.0e-9;

    /** The Constant pico. */
    public final static double pico = 1.0e-12;

    /** The Constant femto. */
    public final static double femto = 1.0e-15;

    /** The Constant atto. */
    public final static double atto = 1.0e-18;

    /** The Constant zepto. */
    public final static double zepto = 1.0e-21;

    /** The Constant yocto. */
    public final static double yocto = 1.0e-24;

    /** The Constant deka. */
    public final static double deka = 10.0;

    /** The Constant hecto. */
    public final static double hecto = 100.0;

    /** The Constant kilo. */
    public final static double kilo = 1.0e3;

    /** The Constant mega. */
    public final static double mega = 1.0e6;

    /** The Constant giga. */
    public final static double giga = 1.0e9;

    /** The Constant tera. */
    public final static double tera = 1.0e12;

    /** The Constant peta. */
    public final static double peta = 1.0e15;

    /** The Constant exa. */
    public final static double exa = 1.0e18;

    /** The Constant zetta. */
    public final static double zetta = 1.0e21;  // Z

    /** The Constant yotta. */
    public final static double yotta = 1.0e24;  // Y



    // Generic Dimensionless units
    /** The Constant percent. */
    public final static double percent = 0.01;

    /** The Constant ppm. */
    public final static double ppm = 1e-6;

    /** The Constant ppb. */
    public final static double ppb = 1e-9;

    /** The Constant ppt. */
    public final static double ppt = 1e-12;

    /** The Constant ppq. */
    public final static double ppq = 1e-15;

    // SI Dimensionless unit Uno
    /** The Constant uno. */
    public final static double uno = 1.0;



    // Basics (SI) and common scales

    /** The Constant metre. */
    public final static double metre = 1.0;

    /** The Constant meter. */
    public final static double meter = metre;

    /** The Constant m. */
    public final static double m = metre;

    /** The Constant km. */
    public final static double km = kilo * metre;

    /** The Constant dm. */
    public final static double dm = deci * metre;

    /** The Constant cm. */
    public final static double cm = centi * metre;

    /** The Constant mm. */
    public final static double mm = milli * metre;

    /** The Constant um. */
    public final static double um = micro * metre;

    /** The Constant nm. */
    public final static double nm = nano * metre;

    /** The Constant m2. */
    public final static double m2 = m * m;

    /** The Constant m3. */
    public final static double m3 = m2 * m;

    /** The Constant m4. */
    public final static double m4 = m3 * m;

    /** The Constant m5. */
    public final static double m5 = m4 * m;

    /** The Constant m6. */
    public final static double m6 = m5 * m;

    /** The Constant cm2. */
    public final static double cm2 = cm * cm;

    /** The Constant cm3. */
    public final static double cm3 = cm2 * cm;

    /** The Constant cm4. */
    public final static double cm4 = cm3 * cm;

    /** The Constant cm5. */
    public final static double cm5 = cm4 * cm;

    /** The Constant cm6. */
    public final static double cm6 = cm5 * cm;


    /** The Constant kilogramm. */
    public final static double kilogramm = 1.0;

    /** The Constant kg. */
    public final static double kg = kilogramm;

    /** The Constant kg2. */
    public final static double kg2 = kg * kg;

    /** The Constant kg3. */
    public final static double kg3 = kg2 * kg;

    /** The Constant kg4. */
    public final static double kg4 = kg3 * kg;

    /** The Constant kg5. */
    public final static double kg5 = kg4 * kg;

    /** The Constant kg6. */
    public final static double kg6 = kg5 * kg;

    /** The Constant gramm. */
    public final static double gramm = 0.001 * kg;

    /** The Constant g. */
    public final static double g = gramm;

    /** The Constant g2. */
    public final static double g2 = g * g;

    /** The Constant g3. */
    public final static double g3 = g2 * g;

    /** The Constant g4. */
    public final static double g4 = g3 * g;

    /** The Constant g5. */
    public final static double g5 = g4 * g;

    /** The Constant g6. */
    public final static double g6 = g5 * g;

    /** The Constant dkg. */
    public final static double dkg = deka * gramm;

    /** The Constant mg. */
    public final static double mg = milli * gramm;

    /** The Constant second. */
    public final static double second = 1.0;

    /** The Constant s. */
    public final static double s = second;

    /** The Constant sec. */
    public final static double sec = second;

    /** The Constant msec. */
    public final static double msec = milli * second;

    /** The Constant ms. */
    public final static double ms = msec;

    /** The Constant usec. */
    public final static double usec = micro * second;

    /** The Constant us. */
    public final static double us = usec;

    /** The Constant nsec. */
    public final static double nsec = nano * second;

    /** The Constant ns. */
    public final static double ns = nsec;

    /** The Constant psec. */
    public final static double psec = pico * second;

    /** The Constant ps. */
    public final static double ps = psec;

    /** The Constant fsec. */
    public final static double fsec = femto * second;

    /** The Constant fs. */
    public final static double fs = fsec;

    /** The Constant sec2. */
    public final static double sec2 = sec * sec;

    /** The Constant sec3. */
    public final static double sec3 = sec2 * sec;

    /** The Constant sec4. */
    public final static double sec4 = sec3 * sec;

    /** The Constant sec5. */
    public final static double sec5 = sec4 * sec;

    /** The Constant sec6. */
    public final static double sec6 = sec5 * sec;

    /** The Constant s2. */
    public final static double s2 = sec2;

    /** The Constant s3. */
    public final static double s3 = sec3;

    /** The Constant s4. */
    public final static double s4 = sec4;

    /** The Constant s5. */
    public final static double s5 = sec5;

    /** The Constant s6. */
    public final static double s6 = sec6;

    /** The Constant ampere. */
    public final static double ampere = 1.0;

    /** The Constant amp. */
    public final static double amp = ampere;

    /** The Constant A. */
    public final static double A = ampere;

    /** The Constant kA. */
    public final static double kA = kilo * ampere;

    /** The Constant mA. */
    public final static double mA = milli * ampere;

    /** The Constant uA. */
    public final static double uA = micro * ampere;

    /** The Constant nA. */
    public final static double nA = nano * ampere;

    /** The Constant pA. */
    public final static double pA = pico * ampere;

    /** The Constant A2. */
    public final static double A2 = A * A;

    /** The Constant A3. */
    public final static double A3 = A2 * A;

    /** The Constant kelvin. */
    public final static double kelvin = 1.0;

    /** The Constant K. */
    public final static double K = kelvin;

    /** The Constant mK. */
    public final static double mK = milli * kelvin;

    /** The Constant uK. */
    public final static double uK = micro * kelvin;

    /** The Constant nK. */
    public final static double nK = nano * kelvin;

    /** The Constant K2. */
    public final static double K2 = K * K;

    /** The Constant K3. */
    public final static double K3 = K2 * K;

    /** The Constant K4. */
    public final static double K4 = K3 * K;

    /** The Constant K5. */
    public final static double K5 = K4 * K;

    /** The Constant K6. */
    public final static double K6 = K5 * K;

    /** The Constant mol. */
    public final static double mol = 1.0;

    /** The Constant mmol. */
    public final static double mmol = milli * mol;

    /** The Constant umol. */
    public final static double umol = micro * 1.0;

    /** The Constant candela. */
    public final static double candela = 1.0;

    /** The Constant cd. */
    public final static double cd = candela;

    // Angles
    /** The Constant radian. */
    public final static double radian = 1.0;

    /** The Constant rad. */
    public final static double rad = radian;

    /** The Constant mrad. */
    public final static double mrad = milli * rad;

    /** The Constant urad. */
    public final static double urad = micro * rad;

    /** The Constant steradian. */
    public final static double steradian = 1.0;

    /** The Constant sr. */
    public final static double sr = steradian;

    /** The Constant rad2. */
    public final static double rad2 = steradian;


    // other SI Units 
    /** The Constant hertz. */
    public final static double hertz = 1.0 / sec;

    /** The Constant Hz. */
    public final static double Hz = hertz;

    /** The Constant uHz. */
    public final static double uHz = micro * hertz;

    /** The Constant mHz. */
    public final static double mHz = milli * hertz;

    /** The Constant kHz. */
    public final static double kHz = kilo * hertz;

    /** The Constant MHz. */
    public final static double MHz = mega * hertz;

    /** The Constant GHz. */
    public final static double GHz = giga * hertz;

    /** The Constant THz. */
    public final static double THz = tera * hertz;

    /** The Constant Hz2. */
    public final static double Hz2 = Hz * Hz;

    /** The Constant Hz3. */
    public final static double Hz3 = Hz2 * Hz;

    /** The Constant Hz4. */
    public final static double Hz4 = Hz3 * Hz;

    /** The Constant Hz5. */
    public final static double Hz5 = Hz4 * Hz;

    /** The Constant Hz6. */
    public final static double Hz6 = Hz5 * Hz;


    // Samples per second
    /** The Constant kSPS. */
    public final static double kSPS = kilo / sec;

    /** The Constant MSPS. */
    public final static double MSPS = mega / sec;

    /** The Constant GSPS. */
    public final static double GSPS = giga / sec;

    /** The Constant newton. */
    public final static double newton = kg * m / s2;

    /** The Constant N. */
    public final static double N = newton;

    /** The Constant kN. */
    public final static double kN = kilo * newton;

    /** The Constant MN. */
    public final static double MN = mega * newton;

    /** The Constant mN. */
    public final static double mN = milli * newton;

    /** The Constant uN. */
    public final static double uN = micro * newton;

    /** The Constant nN. */
    public final static double nN = nano * newton;

    /** The Constant pN. */
    public final static double pN = pico * newton;

    /** The Constant N2. */
    public final static double N2 = N * N;

    /** The Constant N3. */
    public final static double N3 = N2 * N;

    /** The Constant N4. */
    public final static double N4 = N3 * N;

    /** The Constant N5. */
    public final static double N5 = N4 * N;

    /** The Constant N6. */
    public final static double N6 = N5 * N;


    /** The Constant pascal. */
    public final static double pascal = N / m2;

    /** The Constant Pa. */
    public final static double Pa = pascal;

    /** The Constant hPa. */
    public final static double hPa = hecto * pascal;

    /** The Constant kPa. */
    public final static double kPa = kilo * pascal;

    /** The Constant MPa. */
    public final static double MPa = mega * pascal;

    /** The Constant joule. */
    public final static double joule = N * m;

    /** The Constant J. */
    public final static double J = joule;

    /** The Constant kJ. */
    public final static double kJ = kilo * joule;

    /** The Constant MJ. */
    public final static double MJ = mega * joule;

    /** The Constant GJ. */
    public final static double GJ = giga * joule;

    /** The Constant TJ. */
    public final static double TJ = tera * joule;

    /** The Constant PJ. */
    public final static double PJ = peta * joule;

    /** The Constant mJ. */
    public final static double mJ = milli * joule;    

    /** The Constant uJ. */
    public final static double uJ = micro * joule;

    /** The Constant nJ. */
    public final static double nJ = nano * joule;

    /** The Constant pJ. */
    public final static double pJ = pico * joule;

    /** The Constant fJ. */
    public final static double fJ = femto * joule;


    /** The Constant watt. */
    public final static double watt = J / s;

    /** The Constant W. */
    public final static double W = watt;

    /** The Constant kW. */
    public final static double kW = kilo * watt;

    /** The Constant MW. */
    public final static double MW = mega * watt;

    /** The Constant GW. */
    public final static double GW = giga * watt;

    /** The Constant TW. */
    public final static double TW = tera * watt;

    /** The Constant mW. */
    public final static double mW = milli * watt;

    /** The Constant uW. */
    public final static double uW = micro * watt;

    /** The Constant nW. */
    public final static double nW = nano * watt;

    /** The Constant pW. */
    public final static double pW = pico * watt;

    /** The Constant fW. */
    public final static double fW = femto * watt;

    /** The Constant coulomb. */
    public final static double coulomb = A * s;

    /** The Constant C. */
    public final static double C = coulomb;

    /** The Constant volt. */
    public final static double volt = W / A;

    /** The Constant V. */
    public final static double V = volt;

    /** The Constant mV. */
    public final static double mV = milli * volt;

    /** The Constant uV. */
    public final static double uV = micro * volt;

    /** The Constant nV. */
    public final static double nV = nano * volt;

    /** The Constant pV. */
    public final static double pV = pico * volt;

    /** The Constant kV. */
    public final static double kV = kilo * volt;

    /** The Constant farad. */
    public final static double farad = C / V;

    /** The Constant F. */
    public final static double F = farad;

    /** The Constant mF. */
    public final static double mF = milli * farad;

    /** The Constant uF. */
    public final static double uF = micro * farad;

    /** The Constant nF. */
    public final static double nF = nano * farad;

    /** The Constant pF. */
    public final static double pF = pico * farad;

    /** The Constant fF. */
    public final static double fF = femto * farad;

    /** The Constant ohm. */
    public final static double ohm = V / A;

    /** The Constant kohm. */
    public final static double kohm = kilo * ohm;

    /** The Constant Mohm. */
    public final static double Mohm = mega * ohm;

    /** The Constant mohm. */
    public final static double mohm = milli * ohm;

    /** The Constant uohm. */
    public final static double uohm = micro * ohm;

    /** The Constant siemens. */
    public final static double siemens = 1.0 / ohm;

    /** The Constant S. */
    public final static double S = siemens;    

    /** The Constant weber. */
    public final static double weber = V * s;

    /** The Constant Wb. */
    public final static double Wb = weber;

    /** The Constant tesla. */
    public final static double tesla = Wb / m2;

    /** The Constant T. */
    public final static double T = tesla;

    /** The Constant mT. */
    public final static double mT = milli * tesla;

    /** The Constant uT. */
    public final static double uT = milli * tesla;


    /** The Constant henry. */
    public final static double henry = Wb / A;

    /** The Constant H. */
    public final static double H = henry;

    /** The Constant mH. */
    public final static double mH = milli * henry;

    /** The Constant uH. */
    public final static double uH = micro * henry;

    /** The Constant nH. */
    public final static double nH = nano * henry;

    /** The Constant pH. */
    public final static double pH = pico * henry;

    /** The Constant lumen. */
    public final static double lumen = cd * sr;

    /** The Constant lm. */
    public final static double lm = lumen;

    /** The Constant lux. */
    public final static double lux = lm / m2;

    /** The Constant lx. */
    public final static double lx = lux;

    /** The Constant becquerel. */
    public final static double becquerel = 1.0 / sec;

    /** The Constant Bq. */
    public final static double Bq = becquerel;

    /** The Constant gray. */
    public final static double gray = J / kg;

    /** The Constant Gy. */
    public final static double Gy = gray;

    /** The Constant mGy. */
    public final static double mGy = milli * gray;

    /** The Constant sievert. */
    public final static double sievert = J / kg;

    /** The Constant Sv. */
    public final static double Sv = sievert;


    // Angles (radians)
    /** The Constant degree. */
    public final static double degree = Math.PI/180.0;

    /** The Constant deg. */
    public final static double deg = degree;

    /** The Constant arcMinute. */
    public final static double arcMinute = degree/60.0;

    /** The Constant arcmin. */
    public final static double arcmin = arcMinute;

    /** The Constant arcSecond. */
    public final static double arcSecond = arcMinute/60.0;

    /** The Constant arcsec. */
    public final static double arcsec = arcSecond;

    /** The Constant mas. */
    public final static double mas = milli * arcSecond;

    /** The Constant hourAngle. */
    public final static double hourAngle = Constant.twoPi / 24.0;

    /** The Constant minuteAngle. */
    public final static double minuteAngle = hourAngle/60.0;

    /** The Constant secondAngle. */
    public final static double secondAngle = minuteAngle/60.0;

    /** The Constant squareDegree. */
    public final static double squareDegree = degree * degree;

    /** The Constant degree2. */
    public final static double degree2 = squareDegree;

    /** The Constant sqdeg. */
    public final static double sqdeg = squareDegree;

    /** The Constant deg2. */
    public final static double deg2 = degree2;

    /** The Constant arcmin2. */
    public final static double arcmin2 = arcmin * arcmin;

    /** The Constant arcsec2. */
    public final static double arcsec2 = arcsec * arcsec;

    // non-SI derivatives
    // time:
    /** The Constant minute. */
    public final static double minute = 60.0 * sec;

    /** The Constant min. */
    public final static double min = minute;

    /** The Constant hour. */
    public final static double hour = 60.0 * minute;

    /** The Constant day. */
    public final static double day = 24.0 * hour;

    /** The Constant year. */
    public final static double year = 365.24219879 * day;

    /** The Constant yr. */
    public final static double yr = year;

    /** The Constant century. */
    public final static double century = 100.0 * year;

    /** The Constant julianCentury. */
    public final static double julianCentury = 36525.0 * day;

    /** The Constant timeAngle. */
    public final static double timeAngle = hourAngle / hour;

    // distances:
    /** The Constant angstrom. */
    public final static double angstrom = 1.0e-10 * m;

    /** The Constant Rsun. */
    public final static double Rsun = 696.0 * 1e6 * m;

    /** The Constant solarRadius. */
    public final static double solarRadius = Rsun;

    /** The Constant Rearth. */
    public final static double Rearth = 6378140.0 * m;

    /** The Constant earthRadius. */
    public final static double earthRadius = Rearth;

    /** The Constant AU. */
    public final static double AU = 149597870700.0 * Unit.m;

    /** The Constant lightYear. */
    public final static double lightYear = 299792459.0 * year;

    /** The Constant lyr. */
    public final static double lyr = lightYear;

    /** The Constant ly. */
    public final static double ly = lightYear;

    /** The Constant parsec. */
    public final static double parsec = AU / arcsec;

    /** The Constant pc. */
    public final static double pc = parsec; 

    /** The Constant kpc. */
    public final static double kpc = kilo * parsec;

    /** The Constant Mpc. */
    public final static double Mpc = mega * parsec;

    /** The Constant Gpc. */
    public final static double Gpc = giga * parsec;

    /** The Constant inch. */
    public final static double inch = 2.54  * cm;

    /** The Constant in. */
    public final static double in = inch;

    /** The Constant mil. */
    public final static double mil = 1.0e-3 * inch;

    /** The Constant foot. */
    public final static double foot = 0.3048 * m;

    /** The Constant ft. */
    public final static double ft = foot;
    
    /** The Constant kft. */
    public final static double kft = kilo * ft;

    /** The Constant yard. */
    public final static double yard = 0.9144 * m;

    /** The Constant yd. */
    public final static double yd = yard;

    /** The Constant mile. */
    public final static double mile = 1.60935 * km;

    /** The Constant mi. */
    public final static double mi = mile;

    /** The Constant nmi. */
    public final static double nmi = 1852.0 * m;

    /** The Constant pt. */
    public final static double pt = 1/72.0 * in;

    // Areas
    /** The Constant barn. */
    public final static double barn = 1.0e-28 * m2;


    // Volumes
    /** The Constant litre. */
    public final static double litre = 1.0e-3 * m3;

    /** The Constant liter. */
    public final static double liter = litre;

    /** The Constant l. */
    public final static double l = litre;

    /** The Constant L. */
    public final static double L = litre;

    /** The Constant dl. */
    public final static double dl = deci * litre;

    /** The Constant dL. */
    public final static double dL = dl;

    /** The Constant cl. */
    public final static double cl = centi * litre;

    /** The Constant cL. */
    public final static double cL = cl;

    /** The Constant ml. */
    public final static double ml = milli * litre;

    /** The Constant mL. */
    public final static double mL = ml;

    /** The Constant gallon. */
    public final static double gallon = 3.78543 * l;

    /** The Constant gal. */
    public final static double gal = gallon;

    /** The Constant quart. */
    public final static double quart = gal / 4.0;

    /** The Constant pint. */
    public final static double pint = quart / 2.0;

    /** The Constant cup. */
    public final static double cup = pint / 2.0;

    /** The Constant fluidOunce. */
    public final static double fluidOunce = cup / 8.0;

    /** The Constant fl_oz. */
    public final static double fl_oz = fluidOunce;
    //public final static double tableSpoon = ?;
    //public final static double Tsp = tableSpoon;
    //public final static double teasSpoon = ?;
    //public final static double tsp = teaSpoon;

    /** The Constant englishPint. */
    public final static double englishPint = 20.0 * fluidOunce;


    // weigths
    /** The Constant Msun. */
    public final static double Msun = 1.99e30 * kg;

    /** The Constant solarMass. */
    public final static double solarMass = Msun;

    /** The Constant Mearth. */
    public final static double Mearth = 5.9742e24 * kg;

    /** The Constant earthMass. */
    public final static double earthMass = Mearth;

    /** The Constant atomicUnit. */
    public final static double atomicUnit = 1.66057e-27 * kg;

    /** The Constant u. */
    public final static double u = atomicUnit;

    /** The Constant pound. */
    public final static double pound = 0.4535924 * kg;

    /** The Constant lb. */
    public final static double lb = pound;

    /** The Constant ounce. */
    public final static double ounce = pound / 16.0;

    /** The Constant oz. */
    public final static double oz = ounce;    


    // Electric
    /** The Constant debye. */
    public final static double debye = 3.334e-10 * C * m;

    /** The Constant biot. */
    public final static double biot = 10.0*A;

    /** The Constant Bi. */
    public final static double Bi = biot;

    /** The Constant gauss. */
    public final static double gauss = 1.0e-4*T;

    /** The Constant Gs. */
    public final static double Gs = gauss;


    // Frequency
    /** The Constant rpm. */
    public final static double rpm = Constant.twoPi / min;

    /** The Constant radiansPerSecond. */
    public final static double radiansPerSecond = 1.0 / sec;

    /** The Constant radpersec. */
    public final static double radpersec = radiansPerSecond;

    /** The Constant waveNumber. */
    public final static double waveNumber = Constant.h * Constant.c / metre;


    // Forces & Pressures
    /** The Constant dyn. */
    public final static double dyn = 1.0e-5;

    /** The Constant psi. */
    public final static double psi = 6.89476e3 * Pa;

    /** The Constant atm. */
    public final static double atm = 1.01325e5 * Pa;

    /** The Constant bar. */
    public final static double bar = 1.0e5 * Pa;

    /** The Constant mbar. */
    public final static double mbar = milli * bar;

    /** The Constant ubar. */
    public final static double ubar = micro * bar;

    /** The Constant torr. */
    public final static double torr = 1.33322e2 * Pa;

    /** The Constant mmHg. */
    public final static double mmHg = torr;

    /** The Constant mTorr. */
    public final static double mTorr = milli * torr;


    // Energies (in Joules), Temperature, Power    
    /** The Constant erg. */
    public final static double erg = g * cm2 /s2;

    /** The Constant calorie. */
    public final static double calorie = 4.1868 * J;

    /** The Constant cal. */
    public final static double cal = calorie;

    /** The Constant kcal. */
    public final static double kcal = kilo * cal;

    /** The Constant therm. */
    public final static double therm = 1.05506e8 * J;

    /** The Constant BTU. */
    public final static double BTU = therm;

    /** The Constant Celsius. */
    public final static double Celsius = K;

    /** The Constant Carenheit. */
    public final static double Carenheit = 5.0/9.0 * K;

    /** The Constant eV. */
    public final static double eV =  1.6022e-19 * V;

    /** The Constant neV. */
    public final static double neV =  nano * eV;

    /** The Constant ueV. */
    public final static double ueV =  micro * eV;

    /** The Constant meV. */
    public final static double meV =  milli * eV;

    /** The Constant keV. */
    public final static double keV = kilo * eV;

    /** The Constant MeV. */
    public final static double MeV = mega * eV;

    /** The Constant GeV. */
    public final static double GeV = giga * eV;

    /** The Constant TeV. */
    public final static double TeV = tera * eV;

    /** The Constant Lsun. */
    public final static double Lsun = 3.8e26 * W;

    /** The Constant solarLuminosity. */
    public final static double solarLuminosity = Lsun;

    /** The Constant horsePower. */
    public final static double horsePower = 7.457e2 * W;

    /** The Constant hp. */
    public final static double hp = horsePower;

    /** The Constant HP. */
    public final static double HP = horsePower;

    // Spectral Density
    /** The Constant jansky. */
    public final static double jansky = 1.0e-26 * W / (m2 * Hz);

    /** The Constant Jy. */
    public final static double Jy = jansky;

    /** The Constant mJy. */
    public final static double mJy = milli * jansky;

    /** The Constant uJy. */
    public final static double uJy = micro * jansky;

    /** The Constant kJy. */
    public final static double kJy = kilo * jansky;

    /** The Constant mph. */
    // Speed
    public final static double mph = mile / hour;

    /** The Constant kmh. */
    public final static double kmh = kilo * meter / hour;

    /** The Constant kn. */
    public final static double kn = nmi / hour;


    // Various
    /** The Constant mpg. */
    public final static double mpg = mile / gal;


    

    /**
     * Register
     *
     * @param value the value
     * @param names the names
     */
    public static void register(double value, String names) {
        StringTokenizer tokens = new StringTokenizer(names, " \t,;");
        
        while(tokens.hasMoreTokens()) {
            try { new Unit(tokens.nextToken().trim(), value).register(); }
            catch(IllegalArgumentException e) {}
        }
    }

   
    /** The Constant unity. */
    public final static Unit arbitrary = new Unit("a.u.", 1.0);

    /** The Constant unity. */
    public final static Unit unity = new Unit("U", 1.0);

    /** The Constant counts. */
    public final static Unit counts = new Unit("counts", 1.0);


    static {
        register(1.0, "arb.u., a.u., Arb.u, Arb.U., ARB.U., A.U.");
        register(1.0, "U, uno, count, counts, ct, photon, photons, ph, piece, pcs, unit, adu, bin");
        register(1.0, "pixel, pixels, pxl");
        register(0.01, "%, percent");
        register(ppm, "ppm");
        register(ppb, "ppb");
        register(ppt, "ppt");
        register(ppq, "ppq");

        register(m, "m, meter, metre, meters, metres, M, METRE, METER, METRES, METERS");
        register(s, "s, sec, second, seconds, (S), SEC, SECOND, SECONDS");
        register(A, "A, amp, ampere");
        register(K, "K, " + Symbol.degree + "K, kelvin, kelvins, Kelvin, Kelvins, KELVIN, KELVINS");
        register(cd, "cd, candela");

        register(rad, "rad, radian, radians, RAD, RADIAN, RADIANS");
        register(sr, "sr, steradian, steradians");
        register(mol, "mol, mole");

        register(g, "g, gramm");
        register(Hz, "Hz, hertz, HZ, SPS");
        register(N, "N, newton");
        register(Pa, "Pa, pascal, pascals, PASCAL, PASCALS");
        register(J, "J, joule");
        register(W, "W, watt");
        register(C, "C, Coulomb");
        register(V, "V, volt, volts, Volt, Volts, VOLT, VOLTS");
        register(F, "F, farad");
        register(ohm, "ohm, Ohm");
        register(S, "S, siemens");
        register(Wb, "Wb, weber");
        register(T, "T, tesla");
        register(H, "H, henry");
        register(lm, "lm, lumen");
        register(lx, "lx, lux");
        register(Bq, "Bq, bequerel");
        register(Gy, "Gy, gray");
        register(Sv, "Sv, sievert");

        register(barn, "b, barn");
        register(angstrom, Symbol.Acircle + ", Angstrom, angstrom");
        register(um, "micron");

        register(deg, "deg, " + Symbol.degree + ", degree, degrees, DEG, DEGREE, DEGREES");
        register(arcmin, "arcmin, arcmins, ARCMIN, ARCMINS");
        register(arcsec, "arcsec, arcsecs, ARCSEC, ARCSECS");

        // ...
        register(min, "min, minute, minutes, MIN");
        register(hour, "h, hr, hour, H, HR");
        register(day, "d, day, days, (D), DAY, DAYS");
        register(year, "yr, year, years, YR, YEAR, YEARS, a");	// annum
        register(century, "century, cent");
        register(julianCentury, "juliancentury");


        register(Rsun, "solRad, Rsun, solarradius");
        register(Rearth, "earthRad, Rearth, earthradius");
        register(AU, "AU, astronomicalunit");
        register(ly, "lyr, ly, lightyear");
        register(pc, "pc, parsec");
        register(in, "in, inch");
        register(mil, "mil");
        register(ft, "ft, foot");
        register(yd, "yd, yard");
        register(mi, "mi, mile");
        register(nmi, "nmi, NM, M");
        register(pt, "pt");
        register(l, "l, L, litre, liter");
        register(gal, "gal, gallon");
        register(quart, "quart");
        register(pint, "pint");
        register(cup, "cup");
        register(fl_oz, "fl.oz, floz, fluidounce");

        register(englishPint, "englishpint");
        register(Msun, "solMass, Msun, solarmass");
        register(Mearth, "earthMass, Mearth, earthmass");
        register(u, "u, atomicunit");
        register(lb, "lb, lbs, pound");
        register(oz, "oz, ounce");

        register(debye, "debye");
        register(Bi, "Bi, biot");
        register(Gs, "Gs, gauss");
        register(rpm, "rpm");
        register(waveNumber, "wavenumber");
        register(dyn, "dyn, dyne");
        register(psi, "psi");
        register(atm, "atm, atmosphere");
        register(bar, "bar");
        register(torr, "mmHg, torr");

        register(erg, "erg");
        register(cal, "cal, calorie");
        register(BTU, "Btu, BTU, therm");

        register(eV, "eV, electronvolt");
        register(Lsun, "solLum, Lsun, solarluminosity");
        register(hp, "hp, HP, horsepower");

        register(Jy, "Jy, jansky, JY");
        register(mpg, "mpg, MPG");

        register(mph, "mph");
        register(kmh, "kmh");
        register(kn, "kn, kt, kts, knot, knots");

        // Some upper-case FITS units
        register(mas, "mas, MAS");
        register(km, "KM");
        register(kHz, "KHZ");
        register(MHz, "MHZ");
        register(GHz, "GHZ");

    }

   
}



