/* *****************************************************************************
 * Copyright (c) 2021 Attila Kovacs <attila[AT]sigmyne.com>.
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


package jnum;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;

import jnum.util.HashCode;
import jnum.util.PrefixedUnit;


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
    protected static final Hashtable<String, Unit> standardUnits = new Hashtable<>();

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
   
    @Override
    public Unit clone() {
        try { return (Unit) super.clone(); }
        catch(CloneNotSupportedException e) { return null; }        
    }

    @Override
    public Unit copy() {
        return clone();
    }
    
    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(o == null) return false;
        
        if(!o.getClass().equals(getClass())) return false;
        
        Unit u = (Unit) o;
        if(!u.name().equals(name())) return false;
        if(u.value() != value()) return false;
       
        
        return true;
    }


    @Override
    public int hashCode() {
        return super.hashCode() ^ name().hashCode() ^ HashCode.from(value);
    }


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


    public static Unit get(String id) {
        return get(id, standardUnits);
    }


    public static Unit get(String id, Map<String, Unit> baseUnits) throws IllegalArgumentException {		
        if(baseUnits == null) return PrefixedUnit.createFrom(id, standardUnits);
        
        try { return PrefixedUnit.createFrom(id, baseUnits); }
        catch(IllegalArgumentException e) { return PrefixedUnit.createFrom(id, standardUnits); }
    }

    

    @Override
    public String toString() {
        return "[" + name() + "]";
    }


    // Unit Prefixes
    /** 0.1, d (deci), as in dL. (Non-standard, but used at some places) */
    public static final double deci = 0.1;

    /** 0.01, c (centi), as in cm. */
    public static final double centi = 1.0e-2;

    /** 10<sup>-3</sup>, m (milli), as in mm.*/
    public static final double milli = 1.0e-3;

    /** 10<sup>-6</sup>, &mu; (micro), as in &mu;m. */
    public static final double micro = 1.0e-6;

    /** 10<sup>-9</sup>, n (nano), as in nW. */
    public static final double nano = 1.0e-9;

    /** 10<sup>-12</sup>, prefix p (pico) as in pW. */
    public static final double pico = 1.0e-12;

    /** 10<sup>-15</sup>, f (femto) as in fm. */
    public static final double femto = 1.0e-15;

    /** 10<sup>-18</sup>, a (atto). */
    public static final double atto = 1.0e-18;

    /** 10<sup>-21</sup>, z (zepto). */
    public static final double zepto = 1.0e-21;

    /** 10<sup>-24</sup>, y (yocto). */
    public static final double yocto = 1.0e-24;

    /** 10, dk (deka), as in dkg. (Non-standard, but used in places like Hungary) */
    public static final double deka = 10.0;

    /** 100, h (hecto) as in hL. */
    public static final double hecto = 100.0;

    /** 10<sup>3</sup>, k (kilo) as in kg. */
    public static final double kilo = 1.0e3;

    /** 10<sup>6</sup> M (mega), as in Mpc. */
    public static final double mega = 1.0e6;

    /** 10<sup>9</sup> G (giga), as in Gyr. */
    public static final double giga = 1.0e9;

    /** 10<sup>12</sup>, T (tera), as in TB. */
    public static final double tera = 1.0e12;

    /** 10<sup>15</sup>, P (peta), as in PFLOPS. */
    public static final double peta = 1.0e15;

    /** 10<sup>18</sup>, E (exa). */
    public static final double exa = 1.0e18;

    /** 10<sup>21</sup>, Z (zetta). */
    public static final double zetta = 1.0e21;  // Z

    /** 10<sup>24</sup>, Y (yotta) */
    public static final double yotta = 1.0e24;  // Y



    // Generic Dimensionless units
    /** % (pre-cent) */
    public static final double percent = 0.01;

    /** ppm (part-per-million) */
    public static final double ppm = 1e-6;

    /** ppb (part-per-billion) */
    public static final double ppb = 1e-9;

    /** ppt (part-per-trillion) */
    public static final double ppt = 1e-12;

    /** ppw (part-per-quadrillion) */
    public static final double ppq = 1e-15;

    // SI Dimensionless unit Uno
    /** uno, the SI unit for dimensionless quantities. */
    public static final double uno = 1.0;



    // Basics (SI) and common scales

    /** m (meter). */
    public static final double meter = 1.0;

    /** m (meter). */
    public static final double m = meter;

    /** km (kilometer). */
    public static final double km = kilo * meter;

    /** dm (decimeter). */
    public static final double dm = deci * meter;

    /** cm (centimeter). */
    public static final double cm = centi * meter;

    /** mm (millimeter). */
    public static final double mm = milli * meter;

    /** &mu;m (micrometer or micron). */
    public static final double um = micro * meter;

    /** nm (nanometer). */
    public static final double nm = nano * meter;

    /** m<sup>2</sup> (meter squared). */
    public static final double m2 = m * m;

    /** m<sup>3</sup> (meter cubed). */
    public static final double m3 = m2 * m;

    /** m<sup>4</sup> (meter to the 4th power). */
    public static final double m4 = m3 * m;

    /** m<sup>5</sup> (meter to the 5th power). */
    public static final double m5 = m4 * m;

    /** m<sup>6</sup> (meter to the 6th power). */
    public static final double m6 = m5 * m;

    /** cm<sup>2</sup> (centimeter squared). */
    public static final double cm2 = cm * cm;

    /** cm<sup>3</sup> (centimeter cubed). */
    public static final double cm3 = cm2 * cm;

    /** cm<sup>4</sup> (centimeter to hte 4th power). */
    public static final double cm4 = cm3 * cm;

    /** cm<sup>5</sup> (centimeter to the 5th power). */
    public static final double cm5 = cm4 * cm;

    /** cm<sup>6</sup> (centimeter to the 6th power) */
    public static final double cm6 = cm5 * cm;


    /** kg (kilogram). */
    public static final double kilogram = 1.0;

    /** kg (kilogram). */
    public static final double kg = kilogram;

    /** kg<sup>2</sup> (kilogram squared). */
    public static final double kg2 = kg * kg;

    /** kg<sup>3</sup> (kilogram cubed). */
    public static final double kg3 = kg2 * kg;

    /** kg<sup>4</sup> (kilogram to the 4th power). */
    public static final double kg4 = kg3 * kg;

    /** kg<sup>5</sup> (kilogram to the 5th power). */
    public static final double kg5 = kg4 * kg;

    /** kg<sup>6</sup> (kilogram to the 6th power). */
    public static final double kg6 = kg5 * kg;

    /** g (gram). */
    public static final double gram = 0.001 * kg;

    /** g (gram). */
    public static final double g = gram;

    /** g<sup>2</sup> (gram squared). */
    public static final double g2 = g * g;

    /** g<sup>3</sup> (gram cubed). */
    public static final double g3 = g2 * g;

    /** g<sup>4</sup> (gram to the 4th power). */
    public static final double g4 = g3 * g;

    /** g<sup>5</sup> (gram to the 5th power). */
    public static final double g5 = g4 * g;

    /** g<sup>6</sup> (gram to the 6th power). */
    public static final double g6 = g5 * g;

    /** dkg (dekagram). */
    public static final double dkg = deka * gram;

    /** mg (milligram). */
    public static final double mg = milli * gram;
    
    /** &mu;g (microgram). */
    public static final double mug = micro * gram;

    /** s (second). */
    public static final double second = 1.0;

    /** s (second).. */
    public static final double s = second;

    /** s (second). */
    public static final double sec = second;

    /** ms (millisecond). */
    public static final double msec = milli * second;

    /** ms (millisecond). */
    public static final double ms = msec;

    /** &mu;s (microsecond). */
    public static final double usec = micro * second;

    /** &mu;s (microsecond) */
    public static final double us = usec;

    /** ns (nanosecond). */
    public static final double nsec = nano * second;

    /** ns (nanosecond). */
    public static final double ns = nsec;

    /** ps (picosecond). */
    public static final double psec = pico * second;

    /** ps (picosecond). */
    public static final double ps = psec;

    /** fs (femtosecond). */
    public static final double fsec = femto * second;

    /** fs (femtosecond). */
    public static final double fs = fsec;

    /** s<sup>2</sup> (second squared). */
    public static final double s2 = s * s;

    /** s<sup>2</sup> (second cubed). */
    public static final double s3 = s * s2;

    /** s<sup>4</sup> (second to the 4th power). */
    public static final double s4 = s * s3;

    /** s<sup>5</sup> (second to the 5th power). */
    public static final double s5 = s * s4;

    /** s<sup>6</sup> (second to the 6th power). */
    public static final double s6 = s * s5;

    /** A (ampere or amp). */
    public static final double ampere = 1.0;

    /** A (ampere or amp). */
    public static final double amp = ampere;

    /** A (ampere or amp). */
    public static final double A = ampere;

    /** kA (kiloampere or kiloamp). */
    public static final double kA = kilo * ampere;

    /** mA (milliampere or milliamp). */
    public static final double mA = milli * ampere;

    /** &mu;A (microampere or microamp). */
    public static final double uA = micro * ampere;

    /** nA (nanoampere or nanoamp). */
    public static final double nA = nano * ampere;

    /** pA (picoampere or picoamp). */
    public static final double pA = pico * ampere;

    /** A<sup>2</sup> (ampere squared). */
    public static final double A2 = A * A;

    /** A<sup>3</sup> (ampere cubed). */
    public static final double A3 = A2 * A;

    /** K (kelvin). */
    public static final double kelvin = 1.0;

    /** K (kelvin). */
    public static final double K = kelvin;

    /** mK (millikelvin). */
    public static final double mK = milli * kelvin;

    /** &mu;K (microkelvin). */
    public static final double uK = micro * kelvin;

    /** nK (nanokelvin). */
    public static final double nK = nano * kelvin;

    /** K<sup>2</sup> (kelvin squared). */
    public static final double K2 = K * K;

    /** K<sup>3</sup> (kelvin cubed). */
    public static final double K3 = K2 * K;

    /** K<sup>4</sup> (kelvin to the 4th power). */
    public static final double K4 = K3 * K;

    /** K<sup>5</sup> (kelvin to the 5th power). */
    public static final double K5 = K4 * K;

    /** K<sup>6</sup> (kelvin to the 6th power). */
    public static final double K6 = K5 * K;

    /** mol. */
    public static final double mol = 1.0;

    /** mmol (millimol). */
    public static final double mmol = milli * mol;

    /** &mu;mol (micromol). */
    public static final double umol = micro * 1.0;

    /** cd (candela). */
    public static final double candela = 1.0;

    /** cd (candela). */
    public static final double cd = candela;

    // Angles
    /** rad (radian). */
    public static final double radian = 1.0;

    /** rad (radian). */
    public static final double rad = radian;

    /** mrad (milliradian). */
    public static final double mrad = milli * rad;

    /** &mu;rad (microradian). */
    public static final double urad = micro * rad;

    /** sr (steradian). */
    public static final double steradian = 1.0;

    /** sr (steradian). */
    public static final double sr = steradian;

    /** rad<sup>2</sup> (radian squared). */
    public static final double rad2 = steradian;


    // other SI Units 
    /** Hz (hertz). */
    public static final double hertz = 1.0 / sec;

    /** Hz (herz). */
    public static final double Hz = hertz;
    
    /** mHz (millihertz). */
    public static final double mHz = milli * hertz;

    /** &mu;Hz (microhertz). */
    public static final double uHz = micro * hertz;

    /** kHz (kilohertz). */
    public static final double kHz = kilo * hertz;

    /** MHz (megahertz). */
    public static final double MHz = mega * hertz;

    /** GHz (gigahertz) */
    public static final double GHz = giga * hertz;

    /** THz (terahertz) */
    public static final double THz = tera * hertz;

    /** Hz<sup>2</sup> (hertz squared). */
    public static final double Hz2 = Hz * Hz;

    /** Hz<sup>3</sup> (hertz cubed). */
    public static final double Hz3 = Hz2 * Hz;

    /** Hz<sup>4</sup> (hertz to the 4th power). */
    public static final double Hz4 = Hz3 * Hz;

    /** Hz<sup>5</sup> (hertz to the 5th power). */
    public static final double Hz5 = Hz4 * Hz;

    /** Hz<sup>6</sup> (hertz to the 6th power). */
    public static final double Hz6 = Hz5 * Hz;


    // Samples per second
    /** kSPS (kilosamples-per-second). */
    public static final double kSPS = kilo / sec;

    /** MSPS (megasamples-per-second). */
    public static final double MSPS = mega / sec;

    /** GSPS (gigasamples-per-second). */
    public static final double GSPS = giga / sec;

    /** N (newton) = kg m / s<sup>2</sup>. */
    public static final double newton = kg * m / s2;

    /** N (newton) = kg m / s<sup>2</sup>. */
    public static final double N = newton;

    /** kN (kilonewton). */
    public static final double kN = kilo * newton;

    /** MN (meganewton). */
    public static final double MN = mega * newton;

    /** mN (millinewton). */
    public static final double mN = milli * newton;

    /** &mu;N (micronewton). */
    public static final double uN = micro * newton;

    /** nN (nanonewton). */
    public static final double nN = nano * newton;

    /** pN (piconewton). */
    public static final double pN = pico * newton;

    /** N<sup>2</sup> (newton squared). */
    public static final double N2 = N * N;

    /** N<sup>2</sup> (newton cubed). */
    public static final double N3 = N2 * N;

    /** N<sup>2</sup> (newton to the 4th power). */
    public static final double N4 = N3 * N;

    /** N<sup>2</sup> (newton to the 5th power). */
    public static final double N5 = N4 * N;

    /** N<sup>2</sup> (newton to the 6th power). */
    public static final double N6 = N5 * N;


    /** Pa (pascal) = N / m<sup>2</sup>. */
    public static final double pascal = N / m2;

    /** Pa (pascal). */
    public static final double Pa = pascal;

    /** hPa (hectopascal). */
    public static final double hPa = hecto * pascal;

    /** kPa (kilopascal). */
    public static final double kPa = kilo * pascal;

    /** MPa (megapascal). */
    public static final double MPa = mega * pascal;

    /** J (joule) = N m. */
    public static final double joule = N * m;

    /** J (joule) = N m. */
    public static final double J = joule;

    /** kJ (kilojoule). */
    public static final double kJ = kilo * joule;

    /** MJ (megajoule). */
    public static final double MJ = mega * joule;

    /** GJ (joule). */
    public static final double GJ = giga * joule;

    /** TJ (terajoule). */
    public static final double TJ = tera * joule;

    /** PJ (petajoule). */
    public static final double PJ = peta * joule;

    /** mJ (millijoule). */
    public static final double mJ = milli * joule;    

    /** &mu;J (microjoule). */
    public static final double uJ = micro * joule;

    /** nJ (nanojoule). */
    public static final double nJ = nano * joule;

    /** pJ (picojoule). */
    public static final double pJ = pico * joule;

    /** fJ (femtojoule). */
    public static final double fJ = femto * joule;


    /** W (watt) = J / s. */
    public static final double watt = J / s;

    /** W (watt) = J / s. */
    public static final double W = watt;

    /** kW (kilowatt). */
    public static final double kW = kilo * watt;

    /** MW (megawatt). */
    public static final double MW = mega * watt;

    /** GW (gigawatt). */
    public static final double GW = giga * watt;

    /** TW (terawatt). */
    public static final double TW = tera * watt;

    /** mW (milliwatt). */
    public static final double mW = milli * watt;

    /** uW (microwatt). */
    public static final double uW = micro * watt;

    /** nW (nanowatt). */
    public static final double nW = nano * watt;

    /** pW (picowatt). */
    public static final double pW = pico * watt;

    /** fW (femtowatt). */
    public static final double fW = femto * watt;

    /** C (coulomb) = A s. */
    public static final double coulomb = A * s;

    /** C (coulomb) = A s. */
    public static final double C = coulomb;

    /** V (volt) = W / A. */
    public static final double volt = W / A;

    /** V (volt) = W / A. */
    public static final double V = volt;

    /** mV (millivolt). */
    public static final double mV = milli * volt;

    /** &mu;V (microvolt). */
    public static final double uV = micro * volt;

    /** nV (nanovolt). */
    public static final double nV = nano * volt;

    /** pV (picovolt). */
    public static final double pV = pico * volt;

    /** kV (kilovolt). */
    public static final double kV = kilo * volt;

    /** F (farad) = C / V. */
    public static final double farad = C / V;

    /** F (farad) = C / V. */
    public static final double F = farad;

    /** mF (millifarad). */
    public static final double mF = milli * farad;

    /** &mu;F (microfarad). */
    public static final double uF = micro * farad;

    /** nF (nanofarad). */
    public static final double nF = nano * farad;

    /** pF (picofarad). */
    public static final double pF = pico * farad;

    /** fF (femtofarad). */
    public static final double fF = femto * farad;

    /** &Omega; (ohm) = V / A. */
    public static final double ohm = V / A;

    /** k&Omega; (kiloohm). */
    public static final double kohm = kilo * ohm;

    /** M&Omega; (megaohm). */
    public static final double Mohm = mega * ohm;

    /** m&Omega; (milliohm). */
    public static final double mohm = milli * ohm;

    /** &mu;&Omega; (microohm). */
    public static final double uohm = micro * ohm;

    /** S (siemens) = 1 / &Omega;. */
    public static final double siemens = 1.0 / ohm;

    /** S (siemens) = 1 / &Omega;. */
    public static final double S = siemens;    

    /** Wb (weber) = V s. */
    public static final double weber = V * s;

    /** Wb (weber) = V s. */
    public static final double Wb = weber;

    /** T (tesla) = Wb / m<sup>2</sup>. */
    public static final double tesla = Wb / m2;

    /** T (tesla) = Wb / m<sup>2</sup>. */
    public static final double T = tesla;

    /** mT (millitesla). */
    public static final double mT = milli * tesla;

    /** &mu;T (mictotesla). */
    public static final double uT = milli * tesla;


    /** H (henry) = Wb / A. */
    public static final double henry = Wb / A;

    /** H (henry) = Wb / A. */
    public static final double H = henry;

    /** mH (millihenry). */
    public static final double mH = milli * henry;

    /** &mu;H (microhenry). */
    public static final double uH = micro * henry;

    /** nH (nanohenry). */
    public static final double nH = nano * henry;

    /** pH (picohenry). */
    public static final double pH = pico * henry;

    /** lm (lumen) = cd sr. */
    public static final double lumen = cd * sr;

    /** lm (lumen) = cd sr. */
    public static final double lm = lumen;

    /** lx (lux) = lm / m<sup>2</sup>. */
    public static final double lux = lm / m2;

    /** lx (lux) = lm / m<sup>2</sup>. */
    public static final double lx = lux;

    /** Bq (becquerel) = 1 / s. */
    public static final double becquerel = 1.0 / sec;

    /** Bq (becquerel) = 1 / s. */
    public static final double Bq = becquerel;

    /** Gy (gray) = J / kg. */
    public static final double gray = J / kg;

    /** Gy (gray) = J / kg. */
    public static final double Gy = gray;

    /** mGy (milligray). */
    public static final double mGy = milli * gray;

    /** Sv (sievert) = J / kg. */
    public static final double sievert = J / kg;

    /** Sv (sievert) = J / kg. */
    public static final double Sv = sievert;


    // Angles (radians)
    /** &deg; (degree) = &pi; / 180. */
    public static final double degree = Math.PI/180.0;

    /** &deg; (degree) = &pi; / 180. */
    public static final double deg = degree;

    /** arcmin (minute of arc) = deg / 60. */
    public static final double arcMinute = degree/60.0;

    /** arcmin (minute of arc) = deg / 60. */
    public static final double arcmin = arcMinute;

    /** arcsec (second of arc) = arcmin / 60. */
    public static final double arcSecond = arcMinute/60.0;

    /** arcsec (second of arc) = arcmin / 60. */
    public static final double arcsec = arcSecond;

    /** mas (milliarcsec). */
    public static final double mas = milli * arcSecond;
   
    /** &mu;as (microarcsec). */
    public static final double uas = micro * arcSecond;

    /** hourangle. */
    public static final double hourAngle = Constant.twoPi / 24.0;

    /** minute-angle. */
    public static final double minuteAngle = hourAngle/60.0;

    /** second-angle. */
    public static final double secondAngle = minuteAngle/60.0;

    /** deg<sup>2</sup> (square degree). */
    public static final double squareDegree = degree * degree;

    /** deg<sup>2</sup> (square degree). */
    public static final double degree2 = squareDegree;

    /** deg<sup>2</sup> (square degree). */
    public static final double sqdeg = squareDegree;

    /** deg<sup>2</sup> (square degree). */
    public static final double deg2 = degree2;

    /** arcmin<sup>2</sup> (square arc-minute). */
    public static final double arcmin2 = arcmin * arcmin;

    /** arcsec<sup>2</sup> (square arc-second).. */
    public static final double arcsec2 = arcsec * arcsec;

    // non-SI derivatives
    // time:
    /** minute = 60 s. */
    public static final double minute = 60.0 * sec;

    /** minute = 60 s. */
    public static final double min = minute;

    /** hour = 60 minute. */
    public static final double hour = 60.0 * minute;

    /** day = 24 hour. */
    public static final double day = 24.0 * hour;
    
    /** sidereal day = 23.9344696 hour */
    public static final double siderealDay = 23.9344696 * hour;

    /** calendar year = 365.24219879 day. */
    public static final double year = 365.24219879 * day;

    /** calendar year = 365.24219879 day. */
    public static final double yr = year;

    /** Julian year = 365.25 day. */
    public static final double julianYear = 365.25 * day;
    
    /** calendar century = 100 calendar year. */
    public static final double century = 100.0 * year;

    /** Julian entury = 100 Julian year. */
    public static final double julianCentury = 100.0 * julianYear;

    /** angle of time. */
    public static final double timeAngle = hourAngle / hour;

    // distances:
    /** &Aring; (angstrom) = 10<sup>-10</sup> m. */
    public static final double angstrom = 1.0e-10 * m;

    /** Solar radius = 6.96e8 m. */
    public static final double Rsun = 696.0 * 1e6 * m;

    /** Solar radius = 6.96e8 m. */
    public static final double solarRadius = Rsun;

    /** Earth radius = 6378.14 km. */
    public static final double Rearth = 6378140.0 * m;

    /** Earth radius = 6378.14 km. */
    public static final double earthRadius = Rearth;

    /** AU (astronomucal unit) = 149597870 km. */
    public static final double AU = 149597870700.0 * Unit.m;

    /** ly (light-year) . */
    public static final double lightYear = Constant.c * year;

    /** The Constant ly. */
    public static final double ly = lightYear;

    /** pc (parsec). */
    public static final double parsec = AU / arcsec;

    /** pc (parsec). */
    public static final double pc = parsec; 

    /** kpc (kiloparsec). */
    public static final double kpc = kilo * parsec;

    /** Mpc (megaparsec). */
    public static final double Mpc = mega * parsec;

    /** Gpc (gigaparsec). */
    public static final double Gpc = giga * parsec;

    /** in (inch) = 2.54 cm. */
    public static final double inch = 2.54  * cm;

    /** in (inch) = 2.54 cm. */
    public static final double in = inch;

    /** mil = in / 1000. */
    public static final double mil = 1.0e-3 * inch;

    /** ft (foot/feet). */
    public static final double foot = 0.3048 * m;

    /** ft (foot/feet). */
    public static final double ft = foot;
    
    /** kft (kilofeet). */
    public static final double kft = kilo * ft;

    /** yd (yard). */
    public static final double yard = 0.9144 * m;

    /** yd (yard). */
    public static final double yd = yard;

    /** mi (mile). */
    public static final double mile = 1.60935 * km;

    /** mi (mile). */
    public static final double mi = mile;

    /** nmi (nautical mile). */
    public static final double nmi = 1852.0 * m;

    /** pt (point) size. */
    public static final double pt = 1/72.0 * in;

    // Areas
    /** barn = 10<sup>-28</sup> m<sup>2</sup> */
    public static final double barn = 1.0e-28 * m2;


    // Volumes
    /** L (litre/liter) = 10<sup>-3</sup> m<sup>3</sup>. */
    public static final double liter = 1.0e-3 * m3;

    /** L (litre/liter) = 10<sup>-3</sup> m<sup>3</sup>. */
    public static final double L = liter;

    /** dL (deciliter). */
    public static final double dL = deci * liter;

    /** cL (centiliter). */
    public static final double cL = centi * liter;

    /** mL (milliliter). */
    public static final double mL = milli * liter;

    /** gal (gallon). */
    public static final double gallon = 3.78543 * L;

    /** gal (gallon). */
    public static final double gal = gallon;

    /** qt (quart) volume. */
    public static final double quart = gal / 4.0;

    /** American pint (16 fl.oz). */
    public static final double pint = quart / 2.0;

    /** cup. */
    public static final double cup = pint / 2.0;

    /** fl.oz (fluid ounce). */
    public static final double fluidOunce = cup / 8.0;

    /**  fl.oz (fluid ounce). */
    public static final double fl_oz = fluidOunce;
    //public static final double tableSpoon = ?;
    //public static final double Tsp = tableSpoon;
    //public static final double teasSpoon = ?;
    //public static final double tsp = teaSpoon;

    /** English pint (20 fl.oz). */
    public static final double englishPint = 20.0 * fluidOunce;


    // weigths
    /** Solar mass. */
    public static final double Msun = 1.99e30 * kg;

    /** Solar mass. */
    public static final double solarMass = Msun;

    /** Earth mass. */
    public static final double Mearth = 5.9742e24 * kg;

    /** Earth mass. */
    public static final double earthMass = Mearth;

    /** u (atomic unit) = 1.66057e-27 kg. */
    public static final double atomicUnit = 1.66057e-27 * kg;

    /** u (atomic unit) = 1.66057e-27 kg. */
    public static final double u = atomicUnit;

    /** lb (pound). */
    public static final double pound = 0.4535924 * kg;

    /** lb (pound). */
    public static final double lb = pound;

    /** oz (ounce) weight. */
    public static final double ounce = pound / 16.0;

    /** oz (ounce) weight. */
    public static final double oz = ounce;    


    // Electric
    /** debye = 3.334e-10 * C * m. */
    public static final double debye = 3.334e-10 * C * m;

    /** Bi (biot) = 10 A. */
    public static final double biot = 10.0*A;

    /** Bi (biot) = 10 A. */
    public static final double Bi = biot;

    /** Gs (gauss) = 10<sup>-4</sup> T. */
    public static final double gauss = 1.0e-4*T;

    /** Gs (gauss) = 10<sup>-4</sup> T. */
    public static final double Gs = gauss;


    // Frequency
    /** rpm (rotations-per-minute). */
    public static final double rpm = Constant.twoPi / min;

    /** rad/s (radians-per-second). */
    public static final double radiansPerSecond = 1.0 / sec;

    /** rad/s (radians-per-second). */
    public static final double radpersec = radiansPerSecond;

    /** wave number. */
    public static final double waveNumber = Constant.h * Constant.c / meter;


    // Forces & Pressures
    /** dyn = 10<sup>-5</sup> Pa. */
    public static final double dyn = 1.0e-5 * N;

    /** psi (pounds-per-square-inch) pressure. */
    public static final double psi = 6.89476e3 * Pa;

    /** atm (atmosphere) pressure. */
    public static final double atm = 1.01325e5 * Pa;

    /** bar = 10<sup>5</sup> Pa. */
    public static final double bar = 1.0e5 * Pa;

    /** mbar (millibar). */
    public static final double mbar = milli * bar;

    /** &mu;bar (microbar). */
    public static final double ubar = micro * bar;

    /** mmHg (torr). */
    public static final double torr = 1.33322e2 * Pa;

    /** mmHg (torr). */
    public static final double mmHg = torr;

    /** mtorr (millitorr). */
    public static final double mTorr = milli * torr;


    // Energies (in Joules), Temperature, Power    
    /** erg = g cm<sup>2</sup> / s<sup>2</sup>. */
    public static final double erg = g * cm2 /s2;

    /** cal (calorie) = 4.1868 J. */
    public static final double calorie = 4.1868 * J;

    /** cal (calorie) = 4.1868 J. */
    public static final double cal = calorie;

    /** kcal (kilocalorie). */
    public static final double kcal = kilo * cal;

    /** therm = 1.05506e8 J. */
    public static final double therm = 1.05506e8 * J;

    /** BTU (British thermal unit) = term. */
    public static final double BTU = therm;

    /** C (celsius). */
    public static final double Celsius = K;

    /** F (farenheit). */
    public static final double Carenheit = 5.0/9.0 * K;

    /** eV (electronvolt) = 1.6022e-19 * V. */
    public static final double eV =  Constant.q_e * V;

    /** neV (nanoelectronvolt). */
    public static final double neV =  nano * eV;

    /** &mu;eV (microelectronvolt). */
    public static final double ueV =  micro * eV;

    /** meV (millielectronvolt). */
    public static final double meV =  milli * eV;

    /** keV (kiloelectornvolt). */
    public static final double keV = kilo * eV;

    /** MeV (megalelectronvolt). */
    public static final double MeV = mega * eV;

    /** GeV (gigaelevtronvolt). */
    public static final double GeV = giga * eV;

    /** TeV (teralelectronvolt). */
    public static final double TeV = tera * eV;

    /** Solar luminosity = 3.8e26 W. */
    public static final double Lsun = 3.8e26 * W;

    /** solar luminosity */
    public static final double solarLuminosity = Lsun;

    /** hp (horsepower). */
    public static final double horsePower = 7.457e2 * W;

    /** hp (horsepower). */
    public static final double hp = horsePower;

    // Spectral Density
    /** Jy (jansky) = 10<sup>-26</sup> W / m<sup>2</sup> Hz. */
    public static final double jansky = 1.0e-26 * W / (m2 * Hz);

    /** Jy (jansky) = 10<sup>-26</sup> W / m<sup>2</sup> Hz. */
    public static final double Jy = jansky;

    /** mJy (millijansky). */
    public static final double mJy = milli * jansky;

    /** &mu;Jy (microjansky). */
    public static final double uJy = micro * jansky;

    /** kJy (kilojansky). */
    public static final double kJy = kilo * jansky;

    // Speed
    
    /** mph (miles-per-hour). */
    public static final double mph = mile / hour;

    /** km/h (kilometers-per-hour). */
    public static final double kmh = kilo * meter / hour;

    /** kn (knot) = nmi / h. */
    public static final double kn = nmi / hour;


    // Various
    /** mpg (miles-per-gallon) */
    public static final double mpg = mile / gal;


    


    public static void register(double value, String names) {
        StringTokenizer tokens = new StringTokenizer(names, " \t,;");
        
        while(tokens.hasMoreTokens()) {
            try { new Unit(tokens.nextToken().trim(), value).register(); }
            catch(IllegalArgumentException e) {}
        }
    }

   
    /** The Constant unity. */
    public static final Unit arbitrary = new Unit("a.u.", 1.0);

    /** The Constant unity. */
    public static final Unit unity = new Unit("U", 1.0);

    /** The Constant counts. */
    public static final Unit counts = new Unit("counts", 1.0);


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
        register(L, "L, l, litre, liter");
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



