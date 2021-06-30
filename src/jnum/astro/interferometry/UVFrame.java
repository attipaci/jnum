/*******************************************************************************
 * Copyright (c) 2020 Attila Kovacs <attila[AT]sigmyne.com>.
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


package jnum.astro.interferometry;

import java.util.Hashtable;
import java.util.Random;

import jnum.ExtraMath;
import jnum.data.Accumulating;
import jnum.math.ComplexConjugate;
import jnum.math.Inversion;
import jnum.math.Range;
import jnum.math.Range2D;
import jnum.math.Vector2D;
import jnum.math.ZeroValue;


/**
 * Class representing a set of binned interferometric measurements at a single binned frequency.
 * 
 * 
 * @author Attila Kovacs
 *
 */
public class UVFrame extends Hashtable<Integer, UVFrame.Visibility> implements Comparable<UVFrame> {
    /**
     * 
     */
    private static final long serialVersionUID = -1486904569610339354L;
    private double frequency;       // TODO add bandwidth...
    double primaryFWHM;
    private Vector2D delta;         // (1/rad)

    /**
     * Constructor.
     * 
     * @param frequency     (Hz) Frequency for this uv frame.
     * @param delta         (1/rad) u,v bin sizes.
     * @param primaryFWHM   (rad) FWHM of the primary telescope beam at the frequency of this frame. 
     */
    public UVFrame(double frequency, Vector2D delta, double primaryFWHM) {
        this.frequency = frequency;
        this.delta = delta;
        this.primaryFWHM = primaryFWHM;
    }
   
    /**
     * Gets the u,v resolution (bin size) in 1/rad units.
     * 
     * @return  u,v resolution (bin size) in 1/rad.
     */
    public final Vector2D getResolution() { return delta; }
    
    /**
     * Gets the representative observed frequency for this uv frame.
     * 
     * @return  (Hz) observed frequency.
     */
    public final double getFrequency() { return frequency; }

    /**
     * Gets the representative primary telescope beam size for this uv frame.
     * 
     * @return  (rad) Primary beam size (FWHM).
     */
    public final double getPrimaryFWHM() {
        return primaryFWHM;
    }

    /**
     * Jackknife the visibility data in this frame, by randomly inverting individual visibility bins.
     * 
     */
    public void jackknife() {
        final Random random = new Random();
        random.setSeed(202009110301L);
        values().parallelStream().filter(e -> random.nextGaussian() < 0.5).forEach(Visibility::flip);
    }

    /**
     * Returns the sum of the weights over all visibilities in this frame
     * 
     * @return  (1/Jy^2) Sum of visibility weights in this frame.
     */
    public double getWeightSum() {
        return values().parallelStream().mapToDouble(e -> e.w).sum();
    }

    /**
     * Adds a visibility measurement to this frame.
     * 
     * @param u     (1/rad) u coordinate.
     * @param v     (1/rad) v coordinate.
     * @param wre   (1/Jy) Real part of weighted visibility, i.e. w * Re(vis), where vis has units of Jy and w has units of 1/jy^2.
     * @param wim   (1/Jy) Imaginary part of weighted visibility, i.e. w * Re(vis), where vis has units of Jy and w has units of 1/jy^2
     * @param w     (1/Jy**2) Natural noise weight for this visibility.
     */
    public void add(double u, double v, double wre, double wim, double w) {
        if(w <= 0.0) return;
        
        short iu = (short) Math.round(u / delta.x());
        short iv = (short) Math.round(v / delta.y());

        int i = getVirtualIndex(iu, iv);
        Visibility e = get(i);
        if(e == null) {
            e = new Visibility(iu, iv);
            put(i, e);
        }

        e.wre = (float) wre;
        e.wim = (float) wim;
        e.w += (float) w;
    }

    /**
     * Gets an aggregated linearized integer index for a pair of u,v indices. This is useful for fast has-table
     * storage and lookup. 
     * 
     * @param u     u coordinate index
     * @param v     v coordinate index
     * @return      A unique virtual index (for hashing) for the u,v index pair 
     */
    private int getVirtualIndex(int u, int v) {
        return (u & 0xff) << 16 | (v & 0xff);
    }
    
    /**
     * Gets the range of radii in the uv plane spanned by this frame.
     * 
     * @return      (1/rad) Range of uv radii measured in this frame.
     */
    public Range getUVRange() {
        final Range range = new Range();
        values().stream().forEach(vis -> range.include(vis.uvDistance()));
        return range;
    }

    /**
     * Gets the range of u and v values contained in this frame.
     * 
     * 
     * @return  (1/rad) u and v ranges packed into a Range2D object.
     */
    public Range2D getUVRange2D() {
        final Range2D range = new Range2D();
        values().stream().forEach(vis -> range.include(vis.u(), vis.v()));
        return range;
    }
    
    /**
     * Returns the visibilities contained in this frame, resampled into a new uv grid of a new frame. 
     * 
     * @param uvres     (1/rad) New square uv grid size. 
     * @return          New frame with the resampled uv visibilities. 
     * 
     * @see #getResampled(Vector2D)
     */
    public final UVFrame getResampled(double uvres) {
        return getResampled(new Vector2D(uvres, uvres));
    }

    /**
     * Returns the visibilities contained in this frame, resampled into a new uv grid of a new frame. 
     * 
     * @param uvres     (1/rad) New square uv grid size. 
     * @return          New frame with the resampled uv visibilities. 
     * 
     * @see #getResampled(double)
     */
    public UVFrame getResampled(Vector2D uvres) {
        UVFrame resampled = (UVFrame) clone();
        resampled.clear();
        values().stream().forEach(v -> add(v.u(), v.v(), v.wre, v.wim, v.w));
        return resampled;
    }
    

    @Override
    public int compareTo(UVFrame f) {
        return Double.compare(getFrequency(), f.getFrequency());
    } 

    
    /**
     * Binned, weighted visibility entries in this uv frame.
     * 
     * 
     * @author Attila Kovacs
     *
     */
    public class Visibility implements Accumulating<Visibility>, Inversion, ComplexConjugate, ZeroValue {
        short iu, iv;
        float wre, wim, w;

        private Visibility() {}

        private Visibility(short u, short v) {
            this();
            this.iu = u; this.iv = v;
        }
        
        /**
         * Returns the radius (distance from origin) of this visibility bin in the uv plane.
         * 
         * @return
         */
        public double uvDistance() {
            return ExtraMath.hypot(iu * delta.x(), iv * delta.y());
        }
         
        @Override
        public void flip() {
            wre *= -1.0;
            wim *= -1.0;
        }

        @Override
        public void conjugate() {
            wim *= -1.0;
        }

        /**
         * Gets the frequency for this frame.
         * 
         * @return  (Hz) Frequency for this frame.
         */
        public final double getFrequency() {
            return frequency;
        }

        /**
         * Returns the u coordinate for this visibility bin.
         * 
         * @return  (1/rad) u coordinate value.
         */
        public double u() {
            return iu * delta.x();
        }

        
        /**
         * Returns the v coordinate for this visibility bin.
         * 
         * @return  (1/rad) v coordinate value.
         */
        public double v() {
            return iv * delta.y();
        }

        /**
         * Returns the real part of the visibility amplitude.
         * 
         * @return  (Jy) Real value of visibility. 
         */
        public float re() {
            return w > 0.0F ? wre / w : 0.0F; 
        }

        /**
         * Returns the imaginary part of the visibility amplitude.
         * 
         * @return  (Jy) Imaginary value of visibility. 
         */
        public float im() {
            return w > 0.0F ? wim / w : 0.0F; 
        }
        
        @Override
        public int hashCode() {
            return getVirtualIndex(iu, iv);
        }

        @Override
        public void zero() {
            wre = wim = w = 0.0F;
        }

        @Override
        public boolean isNull() {
            return wre == 0.0 && wim == 0.0;
        }
        
        @Override
        public final void noData() {
            zero();
        }

        @Override
        public void accumulate(final Visibility x) {
            wre += x.wre;
            wim += x.wim;
            w += x.w;
        }
        
        @Override
        public void accumulate(final Visibility x, double weight) {
            wre += weight * x.wre;
            wim += weight * x.wim;
            w += weight * x.w;
        }

        @Override
        public void accumulate(final Visibility x, double weight, double G) {
            weight *= G;
            wre += weight * x.wre;
            wim += weight * x.wim;
            w += weight * G * x.w;
        }

        @Override
        public void startAccumulation() {
            noData();
        }

        @Override
        public void endAccumulation() {
            
        }
    }
}

