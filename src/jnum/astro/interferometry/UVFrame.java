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


package jnum.astro.interferometry;

import java.util.Hashtable;
import java.util.Map;
import java.util.Random;

import jnum.Constant;
import jnum.ExtraMath;
import jnum.data.Accumulating;
import jnum.math.ComplexConjugate;
import jnum.math.Inversion;
import jnum.math.Range;
import jnum.math.Range2D;
import jnum.math.Vector2D;
import jnum.math.ZeroValue;
import jnum.util.BufferedRandom;


/**
 * A set of binned visibilities at a single binned frequency.
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
    private double frequency;
    private double bandwidth;
    double primaryFWHM;
    private Vector2D delta;         // (1/rad)

    /**
     * Constructor.
     * 
     * @param frequency     (Hz) Frequency for this uv frame.
     * @param bandwidth     (Hz) Total bandwidth in this frame (not necessarily contiguous)
     * @param delta         (1/rad) <i>u</i>,<i>v</i> bin sizes.
     * @param primaryFWHM   (rad) FWHM of the primary telescope beam at the frequency of this frame. 
     */
    public UVFrame(double frequency, double bandwidth, Vector2D delta, double primaryFWHM) {
        this.frequency = frequency;
        this.bandwidth = bandwidth;
        this.delta = delta;
        this.primaryFWHM = primaryFWHM;
    }
   
    @Override
    public UVFrame clone() {
        return (UVFrame) super.clone();
    }
    
    /**
     * Gets the <i>u,v</i> (1/rad) resolution (bin size).
     * 
     * @return  <i>u,v</i> (1/rad) resolution (bin size).
     */
    public final Vector2D getResolution() { return delta; }
    
    /**
     * Gets the representative observed frequency for this <i>uv</i> frame.
     * 
     * @return  (Hz) observed frequency.
     */
    public final double getFrequency() { return frequency; }

    /**
     * Gets the representative observed bandwidth for this <i>uv</i> frame.
     * 
     * @return  (Hz) observed bandwidth.
     */
    public final double getBandwidth() { return bandwidth; }    
    
    /**
     * Gets the representative primary telescope beam size for this <i>uv</i> frame.
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
    public final void jackknife() {
        jackknife(new BufferedRandom());
    }

    /**
     * Jackknife the visibility data in this frame, by randomly inverting individual visibility bins.
     * 
     * @param   random generator to use
     * 
     */
    public void jackknife(Random random) {
        values().stream().forEach(e -> e.rotate(Constant.twoPi * random.nextDouble()));
    }
    

    /**
     * Returns the sum of the weights over all visibilities in this frame
     * 
     * @return  (1/Jy<sup>2</sup>) Sum of visibility weights in this frame.
     */
    public double getWeightSum() {
        return values().parallelStream().mapToDouble(e -> e.w).sum();
    }

    /**
     * Adds a visibility measurement to this frame.
     * 
     * @param u     (1/rad) <i>u</i> coordinate.
     * @param v     (1/rad) <i>v</i> coordinate.
     * @param wre   (1/Jy) Real part of weighted visibility, i.e. <i>w</i> Re(<i>vis</i>), 
     *              where <i>vis</i> has units of Jy and <i>w</i> has units of 1/Jy<sup>2</sup>.
     * @param wim   (1/Jy) Imaginary part of weighted visibility, i.e. <i>w</i> Re(<i>vis</i>), 
     *              where <i>vis</i> has units of Jy and <i>w</i> has units of 1/Jy<sup>2</sup>
     * @param w     (1/Jy<sup>2</sup>) Natural noise weight for this visibility.
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

        e.wre += wre;
        e.wim += wim;
        e.w += w;
    }

    /**
     * Adds a visibility to this uv-frame.
     * 
     * @param A     the visibility to add to this frame.
     * 
     * @see #add(Visibility, double)
     */
    public void add(Visibility A) {
        add(A, 1.0);
    }
    
    /**
     * Adds a gain adjusted visibility to this uv-frame.
     * 
     * 
     * @param A     the visibility to add to this frame.
     * @param G     The relative response, with which to divide the visibility amplitude before adding.
     * 
     * @see #add(Visibility)
     * @see #add(double, double, double, double, double)
     */
    public void add(Visibility A, double G) {
        if(G == 0.0) return;
        add(A.u(), A.v(), G * A.wre, G * A.wim, G * G * A.w);
    }

    public int despike(double level) {
        return entrySet().stream().map(Map.Entry::getValue).mapToInt(v -> v.despike(level)).sum();
    }
    
    /**
     * Returns the visitibility the specified <i>uv</i> grid index.
     * 
     * @param iu    Integer grid index in the <i>u</i> direction.
     * @param iv    Integer grid index in the <i>v</i> direction.
     * @return      The visibility at the specified index, or <code>null</code> if there is no visibility currently
     *              at the specified <i>uv</i> grid location.
     *              
     * @see #getVisibility(double, double)
     */
    public Visibility getVisibilityAtIndex(short iu, short iv) {
        return get(getVirtualIndex(iu, iv));
    }
    
    /**
     * Returns the visitibility the specified <i>uv</i> coordinates.
     * 
     * @param u     (1/rad) <i>u</i> coordinate
     * @param v     (1/rad) <i>v</i> coordinate
     * @return      The visibility at the specified coordinate on the grid, or <code>null</code> if there is no visibility currently
     *              at the specified <i>uv</i> grid location.
     */
    public Visibility getVisibility(double u, double v) {
        return getVisibilityAtIndex((short) Math.round(u / delta.x()), (short) Math.round(v / delta.y()));
    }
    
    /**
     * Gets an aggregated linearized integer index for a pair of <i>u,v</i> indices. This is useful for fast has-table
     * storage and lookup. 
     * 
     * @param u     <i>u</i> coordinate index
     * @param v     <i>v</i> coordinate index
     * @return      A unique virtual index (for hashing) for the <i>u,v</i> index pair 
     */
    private int getVirtualIndex(int u, int v) {
        return (u & 0xff) << 16 | (v & 0xff);
    }
    
    /**
     * Gets the range of radii in the <i>uv</i> plane spanned by this frame.
     * 
     * @return      (1/rad) Range of <i>uv</i> radii measured in this frame.
     */
    public Range getUVRange() {
        final Range range = new Range();
        values().stream().forEach(vis -> range.include(vis.uvDistance()));
        return range;
    }

    /**
     * Gets the range of <i>u</i> and <i>v</i> values contained in this frame.
     * 
     * 
     * @return  (1/rad) <i>u</i> and <i>v</i> ranges packed into a {@link Range2D} object.
     */
    public Range2D getUVRange2D() {
        final Range2D range = new Range2D();
        values().stream().forEach(vis -> range.include(vis.u(), vis.v()));
        return range;
    }
    
    /**
     * Returns the visibilities contained in this frame, resampled into a new <i>uv</i> grid of a new frame. 
     * 
     * @param uvres     (1/rad) New square <i>uv</i> grid size. 
     * @return          New frame with the resampled <i>uv</i> visibilities. 
     * 
     * @see #getResampled(Vector2D)
     */
    public final UVFrame getResampled(double uvres) {
        return getResampled(new Vector2D(uvres, uvres));
    }

    /**
     * Returns the visibilities contained in this frame, resampled into a new <i>uv</i> grid of a new frame. 
     * 
     * @param uvres     (1/rad) New square uv grid size. 
     * @return          New frame with the resampled <i>uv</i> visibilities. 
     * 
     * @see #getResampled(double)
     */
    public UVFrame getResampled(Vector2D uvres) {
        UVFrame resampled = clone();
        resampled.clear();
        values().stream().forEach(v -> add(v.u(), v.v(), v.wre, v.wim, v.w));
        return resampled;
    }
    

    @Override
    public int compareTo(UVFrame f) {
        return Double.compare(getFrequency(), f.getFrequency());
    } 

    
    /**
     * Binned, weighted visibility entries in this <i>uv</i> frame.
     * 
     * 
     * @author Attila Kovacs
     *
     */
    public class Visibility implements Accumulating<Visibility>, Inversion, ComplexConjugate, ZeroValue {
        short iu, iv;
        float wre, wim, w;

        /**
         * Constructs a new visibility
         * 
         */
        private Visibility() {}

        /**
         * Constructs a new visibility at the specified <i>uv</i> grid location
         * 
         * @param u     Integer grid index in the <i>u</i> direction
         * @param v     Integer grid index in the <i>v</i> direction
         */
        private Visibility(short u, short v) {
            this();
            this.iu = u; this.iv = v;
        }
        
        /**
         * Returns the radius (distance from origin) of this visibility bin in the <i>uv</i> plane.
         * 
         * @return  the magintude of the <i>u,v</i> vector.
         */
        public double uvDistance() {
            return ExtraMath.hypot(iu * delta.x(), iv * delta.y());
        }
         
        /**
         * Rotates the complex visibility amplitude by the specified amount.
         * 
         * @param angle     (rad) Counter clockwise rotation angle on the complex plane.
         */
        public void rotate(double angle) {
            float s = (float) Math.sin(angle);
            float c = (float) Math.cos(angle);
            
            float x = wre;
            wre = x * c - wim * s;
            wim = x * s + wim * c;
        }
        
        /**
         * Flags visibilities in which the signal-to-noise ratio exceeds the specified level, by setting
         * their weight to zero.
         * 
         * @param level     Threshold signal-to-noise ration above which to flag the visibility.
         * @return          the number of visibilities flagged.
         */
        public int despike(double level) {
            if((wre * wre + wim * wim) / w > level * level) {
                wre = wim = w = 0.0F;
                return 1;
            }
            return 0;
        }
        
        @Override
        public void flip() {
            wre = -wre;
            wim = -wim;
        }

        @Override
        public void conjugate() {
            wim = -wim;
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
         * Returns the <i>u</i> coordinate for this visibility bin.
         * 
         * @return  (1/rad) <i>u</i> coordinate value.
         */
        public double u() {
            return iu * delta.x();
        }

        
        /**
         * Returns the <i>v</i> coordinate for this visibility bin.
         * 
         * @return  (1/rad) <i>v</i> coordinate value.
         */
        public double v() {
            return iv * delta.y();
        }
        
        /**
         * Returns the weighted real part of the visibility amplitude.
         * 
         * @return      (~1/Jy) The weighted real part of the visibility amplitude  
         */
        public float wre() {
            return wre;
        }

        /**
         * Returns the weighted imaginary part of the visibility amplitude.
         * 
         * @return      (~1/Jy) The weighted imaginary part of the visibility amplitude  
         */
        public float wim() {
            return wim;
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
        
        /**
         * Returns the weight (e.g. 1/&sigma;<sup>2</sup> noise weight) of this visibility.
         * 
         * @return  (~1/Jy<sup>2</sup>) The (noise) weight of this visibility.
         */
        public float weight() {
            return w;
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
        public void endAccumulation() {}
    }
}

