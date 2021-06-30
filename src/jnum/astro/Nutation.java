/*******************************************************************************
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
 *     Attila Kovacs <attila[AT]sigmyne.com> - initial API and implementation
 ******************************************************************************/

package jnum.astro;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;

import jnum.Constant;
import jnum.Unit;
import jnum.Util;
import jnum.math.Vector2D;

/**
 * A class for calculating nutation corrections for a given time of observation.
 * 
 * @author Attila Kovacs
 *
 */
public abstract class Nutation {
    private static Nutation iau2000aR06 = new IAU2000AR06();
    private static Nutation truncated100uas = new IAU2000ATruncated(0.1 * Unit.mas);
    private static Nutation truncated1mas = new IAU2000ATruncated(Unit.mas);
    private static Nutation truncated10mas = new IAU2000ATruncated(10.0 * Unit.mas);
    
    public int cacheSize = 100;
    
    private Hashtable<Integer, Vector2D> cache = new Hashtable<>();
    
    /**
     * Returns the full precision IAU2000A nutation parameters, with IAU2006 corrections applied. The calculation takes into
     * account over 1300 periodic terms of the full IAU2000A model.
     * 
     * @param mjd   (day) Modified Julian Date of observation.
     * @return      (rad) The (dpsi, deps) nutation vector.
     */
    public static Vector2D getPrecise(double mjd) { return iau2000aR06.getCorrection(mjd); }
    
    /**
     * Returns approximate nutation parameters for, based on the IAU2000A nutation model, but with terms with
     * amplitude smaller than 100 microasrcsecond ommitted. The omission reduces the number of terms
     * used to ~100 (from ~1400), resulting in significant gain in speed of calculation at the price of
     * a modest loss in accuracy for 1950 to 2050.
     * 
     * @param mjd   (day) Modified Julian Date of observation.
     * @return      (rad) The (dpsi, deps) nutation vector.
     */
    public static Vector2D getTruncated100uas(double mjd) { return truncated100uas.getCorrection(mjd); }
    
    /**
     * Returns approximate nutation parameters, based on the IAU2000A nutation model, but with terms with
     * amplitude smaller than 1 milliarcsecond ommitted. The omission reduces the number of terms
     * used to ~40 (from ~1400), resulting in significant gain in speed of calculation at the price of
     * a moderate loss in accuracy for 1950 to 2050.
     * 
     * @param mjd   (day) Modified Julian Date of observation.
     * @return      (rad) The (dpsi, deps) nutation vector.
     */
    public static Vector2D getTruncated1mas(double mjd) { return truncated1mas.getCorrection(mjd); }
    
    
    /**
     * Returns approximate nutation parameters, based on the IAU2000A nutation model, but with terms with
     * amplitude smaller than 1 milliarcsecond ommitted. The omission reduces the number of terms
     * used to around a dozen (from ~1400), resulting in significant gain in speed of calculation at the price of
     * loss in sub-arcsecond accuracy for 1950 to 2050.
     * 
     * @param mjd   (day) Modified Julian Date of observation.
     * @return      (rad) The (dpsi, deps) nutation vector.
     */
    public static Vector2D getTruncated10mas(double mjd) { return truncated10mas.getCorrection(mjd); }
      
    private static int getKey(double mjd) { return (int) (100.0 * mjd); }
    
    /**
     * Returns the dpsi, deps nutation parameters, see e.g. Eq. 12 of Capitaine & Wallace, A&A,. 450, 855 (2006)
     * It takes into account both Luni-solar and planetary precession terms.
     * 
     * The function is implemweted with a cache for recent calls, returning cached values fast when available
     * to improve the speed of repeated
     * 
     * @param mjd   Modified Julian Date for which precession is calculated.
     * @return      (dpsi, deps) vector in radians.
     */
    private final Vector2D getCorrection(double mjd) {
        Vector2D v = cache.get(getKey(mjd));
        if(v != null) return v.copy();
        
        // Interval between fundamental epoch J2000.0 and given date.
        double t = (mjd - AstroTime.MJDJ2000) / AstroTime.JulianCenturyDays;
        if(t == 0) return new Vector2D();
      
        try { v = calcCorrection(t, new DelaunayArguments(mjd)); }
        catch(IOException e) {
            Util.warning(this, "Could not read IAU2000A precession resource.\n");
            v = new Vector2D();     
        }
        
        cache(mjd, v);
        
        return v;
    }
    
    abstract Vector2D calcCorrection(double mjd, DelaunayArguments m) throws IOException;
    
    /**
     * Adds the nutation parameters to the cache with 1/100 day time resolution.
     * 
     * @param mjd   Modified Julian Date
     * @param v     (dspi, deps) Nutation parameters.
     */
    private void cache(double mjd, Vector2D v) {
        int halfFull = cacheSize >>> 1;
        if(cache.size() >= cacheSize) for(Integer key : cache.keySet()) {
            cache.remove(key);
            if(cache.size() <= halfFull) break;
        }
            
        cache.put(getKey(mjd), v.copy());
    }
    
    /**
     * Planeraty longitudes (Mercury to Neptune) at a given time.
     * See Eq. 5.44, parameter F_14 in IERS Technical Note 36 (2010)
     * 
     * @param t     Julian centuries since J2000.0
     * @return      (rad) An array of planet logintudes [-Pi:Pi].
     */
    private static double[] getPlanetLongitudes(double t) {
        double[] l = new double[8];

        l[0] = 4.402608842 + 2608.7903141574 * t;
        l[1] = 3.176146697 + 1021.3285546211 * t;
        l[2] = 1.753470314 +  628.3075849991 * t;
        l[3] = 6.203480913 +  334.0612426700 * t;
        l[4] = 0.599546497 +   52.9690962641 * t;
        l[5] = 0.874016757 +   21.3299104960 * t;
        l[6] = 5.481293872 +    7.4781598567 * t;
        l[7] = 5.311886287 +    3.8133035638 * t;
        
        for(int i=l.length; --i >= 0; ) l[i] = Math.IEEEremainder(l[i] * Unit.arcsec, Constant.twoPi);
        
        return l;
    }
    
    /**
     * See Eq. 5.44, parameter F_14 in IERS Technical Note 36 (2010)
     * 
     * @param t
     * @return
     */
    private static double getPrecessionLongitude(double t) {
        return Math.IEEEremainder((0.02438175 + 0.00000538691 * t) * t * Unit.arcsec, Constant.twoPi);
    }
   
    private static final int N_DELAUNAY = 5;
    private static final int N_PLANETS = 8;
    
   
    /**
     * The full IAU2000A precession model, as descrived in IERS Technical Note 36 (2010), Eq 5.35
     * using tables tab5.3a.txt and tab5.3b.txt available from the IERS Conventions Centre site.
     * 
     * @author Attila Kovacs
     *
     */
    private static class IAU2000A extends Nutation {
        ArrayList<Term> psi, eps;
        
        private final static String subdir = "nutation" + File.separator + "IAU2000A";
        
        @Override
        Vector2D calcCorrection(double t, DelaunayArguments m) throws IOException {
            if(psi == null) psi = readResource(subdir + File.separator + "tab5.3a.txt");
            if(eps == null) eps = readResource(subdir + File.separator + "tab5.3b.txt");
            
            final double[] pl = getPlanetLongitudes(t);
            final double L = getPrecessionLongitude(t);
            
            double dpsi = 0.0, deps = 0.0;
            
            for(Term T : psi) dpsi += T.getProduct(t, m, pl, L);
            for(Term T : eps) deps += T.getProduct(t, m, pl, L);
            
            return new Vector2D(dpsi, deps);
        }
        
        protected ArrayList<Term> readResource(String fileName) throws IOException {
            ArrayList<Term> terms = new ArrayList<>();
            
            InputStream is = getClass().getResourceAsStream(File.separator + "data" + File.separator + fileName);
            if(is == null) {
                Util.warning(this, "Could not find IAU2000A nutation coefficients.");
                return new ArrayList<>(1);
            }
            
            try(BufferedReader in = new BufferedReader(new InputStreamReader(is))) {
                readJTerms(in, terms);
                readJTerms(in, terms);
            } 
            catch(IOException e) { throw e; }
            
            terms.trimToSize();
            return terms;
        }
        
        private void readJTerms(BufferedReader in, ArrayList<Term> terms) throws IOException {
            String line = null;
            int j = 0;
            
            while((line = in.readLine()) != null) if(line.startsWith("j = ")) {
                StringTokenizer tokens = new StringTokenizer(line, " \t=");
                tokens.nextToken();
                j = Integer.parseInt(tokens.nextToken());
                for(int i=3; --i >= 0; ) in.readLine();
                break;
            }
                
            while((line = in.readLine()) != null) {
                StringTokenizer tokens = new StringTokenizer(line);
                if(tokens.countTokens() <= 1) break;
                if(tokens.countTokens() < 4 + N_DELAUNAY + N_PLANETS) continue;
    
                Term t = new Term();
                t.readLine(line, (byte) j);
                terms.add(t);
            }
        }
           
        class Term {
            private short idx;
            private byte j;
            private byte[] nLS;
            private byte[] nPL;
            private byte nL;
            private double A, A1;
            
            public void readLine(String line, byte j) {
                StringTokenizer tokens = new StringTokenizer(line);
                
                this.j = j;
                idx = Short.parseShort(tokens.nextToken());
                A = Double.parseDouble(tokens.nextToken()) * Unit.uas;
                A1 = Double.parseDouble(tokens.nextToken()) * Unit.uas;
                
                nLS = new byte[N_DELAUNAY];
                int n = 0;
                for(int i=0; i<N_DELAUNAY; i++) {
                    nLS[i] = Byte.parseByte(tokens.nextToken());
                    if(nLS[i] != 0) n++;
                }
                if(n == 0) nLS = null;
               
                nPL = new byte[N_PLANETS];
                n = 0;
                for(int i=0; i<N_PLANETS; i++) {
                    nPL[i] = Byte.parseByte(tokens.nextToken());
                    if(nPL[i] != 0) n++;
                }
                if(n == 0) nPL = null;
                
                nL = Byte.parseByte(tokens.nextToken());
            }
            
            public double getProduct(double t, DelaunayArguments m, double[] planetLong, double precessionLong) {
                double arg = 0.0;
                
                if(nLS != null) for(int i=nLS.length; --i >= 0; ) if(nLS[i] != 0) arg += nLS[i] * m.f[i];
                if(nPL != null) for(int i=nPL.length; --i >= 0; ) if(nPL[i] != 0) arg += nPL[i] * planetLong[i];
                if(nL != 0) arg += nL * precessionLong;
                
                final double x =  A * Math.sin(arg) + A1 * Math.cos(arg);
                return j == 0 ? x : x * t;
            }
            
            @Override
            public String toString() { return "IAU2000A(n=" + idx + ")"; }
        }
        
    }
    
    /**
     * The IAU2006 revision of the IAU2000A nutation model. It contains the IAU2006 adjustments to the IAI2000A nutation model. 
     * See Eqs. 5.36, 5.37 in IERS Techincal Note 26 (2010)
     * 
     */
    private static class IAU2000AR06 extends IAU2000A {
        
        @Override
        Vector2D calcCorrection(double t, DelaunayArguments m) throws IOException {
            Vector2D v = super.calcCorrection(t, m);
            
            double FDmO2 = 2.0 * (m.F() - m.D() + m.Omega());
            double s2FDmO = Math.sin(FDmO2);
            double sO = Math.sin(m.Omega());
            
            
            double dpsi1 = 47.8 * sO + 3.7 * s2FDmO 
                + 0.6 * Math.sin(2.0 * (m.F() + m.Omega()))  - 0.6 * Math.sin(2.0 * m.Omega());
            
            double deps1 = -25.6 * Math.cos(m.Omega()) - 1.6 * Math.cos(FDmO2);
            
            double dpsi0 = -8.1 * sO - 0.6 * s2FDmO;
            
            v.addX((dpsi0 + dpsi1 * t) * Unit.uas);
            v.addY(deps1 * t * Unit.uas);
            
            return v;
        }
        
    }
        
    /**
     * Like the IAU2000A, but with terms with amplitude below a threshold (for 1950-2050) ignored.
     * 
     * @author Attila Kovacs
     *
     */
    private static class IAU2000ATruncated extends IAU2000A {
        private double threshold;
        
        IAU2000ATruncated(double threshold) {
            this.threshold = threshold;
        }
        
        @Override
        protected ArrayList<Term> readResource(String fileName) throws IOException {
            ArrayList<Term> terms = super.readResource(fileName);
            
            ArrayList<Term> truncated = new ArrayList<>();
            for(Term T : terms) {
                if(T.A > threshold) truncated.add(T);
                else if(T.A1 > 2.0 * threshold) truncated.add(T);
            }
            
            truncated.trimToSize();
           
            return truncated;
        }   
    }
    
}
