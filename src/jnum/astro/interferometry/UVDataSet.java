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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Random;
import java.util.stream.Collectors;

import jnum.Constant;
import jnum.ExtraMath;
import jnum.Unit;
import jnum.Util;
import jnum.astro.EquatorialCoordinates;
import jnum.astro.EquatorialSystem;
import jnum.fits.FitsProperties;
import jnum.math.Range;
import jnum.math.Range2D;
import jnum.math.Vector2D;
import jnum.math.specialfunctions.CumulativeNormalDistribution;
import jnum.util.BufferedRandom;
import nom.tam.fits.BinaryTable;
import nom.tam.fits.BinaryTableHDU;
import nom.tam.fits.Fits;
import nom.tam.fits.FitsException;
import nom.tam.fits.Header;
import nom.tam.util.ArrayDataInput;


/**
 * A reduced, calibrated interferometric data set, as a list of <i>uv</i> frames, each containing a set of visibility
 * measurements for a specific frequency bin. The data may be loaded in from a standard FITS calibrated UV Table.
 * 
 * 
 * @author Attila Kovacs
 *
 */
public class UVDataSet extends ArrayList<UVFrame> {
    /**
     * 
     */
    private static final long serialVersionUID = 6184860828104201918L;
    private Vector2D delta;
    private double aperture;
    private EquatorialCoordinates equatorial;
        
    private FitsProperties fitsProperties = new FitsProperties();
    
    /**
     * Constructor with a square <i>uv</i> binning grid.
     * 
     * @param aperture  (m) Dish diameter, or equivalent
     * @param uvres     (1/rad) Common bin size for <i>u</i> and <i>v</i> coordinates.
     */
    public UVDataSet(double aperture, double uvres) {
        this(aperture, new Vector2D(uvres, uvres));
    }


    /**
     * Constructor with a rectangular <i>uv</i> binning grid.
     * 
     * @param aperture  (m) Dish diameter, or equivalent
     * @param uvres     (1/rad) Independent <i>u</i> and <i>v</i> coordinate binning sizes.
     */
    public UVDataSet(double aperture, Vector2D uvres) {
        this.aperture = aperture;
        delta = uvres.copy();
    }

    /**
     * Constructor, which creates the <i>uv</i> dataset from a standard FITS calibrated uv table file. It simply calls
     * {@link #read(String)}.
     * 
     * @param fileName          Path of the FITS calibrated uv table file.
     * @throws IOException      If there was an error accessing the file.
     * @throws FitsException    If the FITS file is corrupted or malformed.
     */
    public UVDataSet(String fileName) throws IOException, FitsException {
        read(fileName);
    }
    
    /**
     * Gets the <i>u,v</i> resolutions of the <i>uv</i> binnind grid.
     * 
     * @return      (1/rad) <i>u</i> and <i>v</i> bin resolutions.
     */
    public final Vector2D getResolution() { return delta; }
    
    /**
     * Gets the standard FITS properties associated to this <i>uv</i> dataset, such as telescope and instrument
     * names, names of observers, etc. 
     * 
     * @return  Associated standard FITS header properties. 
     */
    public final FitsProperties getFitsProperties() { return fitsProperties; }
    
    /**
     * Returns the equatorial coordinates for the center of the observed field.
     * 
     * @return  the equatorial coordinates of the observed field.
     */
    public final EquatorialCoordinates getEquatorial() {
        return equatorial;
    }
    
    /**
     * Loads visibility data from a standard FITS calibrated uv table file, discarding prior data if any.
     * The reading will override the preset resolution and aperture size with the values stored in the
     * FITS, if these are recorded. The loaded data will be guaranteed to be gapless (no null frames)
     * (see {@link #compact()} and will be ordered in increasing observing frequencies (see {@link #sort()}).
     * 
     * @param fileName          Path of the FITS calibrated uv table file.
     * @throws IOException      If there was an error accessing the file.
     * @throws FitsException    If the FITS file is corrupted or malformed.
     * 
     * @see #compact()
     * @see #sort()
     */
    public void read(String fileName) throws IOException, FitsException {
        clear();
           
        try (Fits fits = new Fits(new File(fileName))) {
            BinaryTableHDU tab = (BinaryTableHDU) fits.getHDU(1);
            BinaryTableHDU freqs = (BinaryTableHDU) fits.getHDU(2);
            Header h = tab.getHeader();
            fitsProperties.parseHeader(h);
            
            equatorial = new EquatorialCoordinates(
                    h.getDoubleValue("RA", 0.0) * Unit.hourAngle, 
                    h.getDoubleValue("DEC", 0.0) * Unit.deg,
                    EquatorialSystem.fromHeader(h)
            );
            
            delta = new Vector2D(h.getDoubleValue("U_RES", Double.NaN), h.getDoubleValue("V_RES", Double.NaN)); 

            double df = h.getDoubleValue("FREQRES", 0.0);
            double[] f = (double[]) freqs.getColumn(0);
                        
            BinaryTable table = tab.getData();

            int[] bin = new int[1];
            short[] u = new short[1];
            short[] v = new short[1];
            float[] wre = new float[1];
            float[] wim = new float[1];
            float[] w = new float[1];
            Object[] rowData = new Object[] { bin, u, v, wre, wim, w };

            @SuppressWarnings("resource")
            ArrayDataInput in = fits.getStream();

            Util.info(this, "Reading " + fileName);
            
            int nRows = table.getNRows();
            int nVis = 0, nMirror = 0;

            Util.detail(this, "Parsing " + nRows + " visibilities.");

            table.reset();

            for(int i=0; i<nRows; i++) {            
                long bytes = in.readLArray(rowData);
                if(bytes == 0) {
                    Util.warning(this, "Premature table end.");
                    break;
                }
                
                // 1-based indices to 0-based indices...
                //bin[0]--;
                
                // ------------------------------------------------------------------------------------------
                // v3 workarounds...
                
               
                //v[0] -= 513;    // make it work with imperfect v3 UV data
                
                //if(u[0] == (short) 0) continue;            // Discard quashed mirror uv plane...
                //else 
                // -------------------------------------------------------------------------------------------
                

                // Flagging should not be needed if SWARM channels N * 4096 are flagged by COMPASS...
                if(Math.abs(Math.IEEEremainder(f[bin[0]], Unit.GHz)) < df) continue;
                
                if(v[0] < 0) nMirror++;    
                else if(u[0] < 0 && v[0] == 0) nMirror++; 
                else { 
                    addVisibility(bin[0], f[bin[0]], df, u[0] * delta.x(), v[0] * delta.y(), wre[0], wim[0], w[0]);
                    nVis++;
                }
            }    

            Util.detail(this, "Parsed " + nVis + " visibilities, (" + nMirror + " mirrored).");

            in.close();
            fits.close();
        }
        
        compact();
        sort();
        
        Util.detail(this, "Stored as " + size() + " slices.");  
        Util.detail(this, "Frequency: " + Util.f1.format(first().getFrequency() / Unit.GHz) + " - " + Util.f1.format(last().getFrequency() / Unit.GHz) + " GHz");
    }


    /**
     * Compacts the dataset by disacarding null uv frames.
     * 
     * 
     */
    public void compact() { 
        // Check if there are any null elements
        if(parallelStream().allMatch(x -> x != null)) return;
        
        ArrayList<UVFrame> compacted = new ArrayList<>(stream().filter(x -> x != null).collect(Collectors.toList()));      
        if(compacted.isEmpty()) {
            Util.warning(this, "No valid slices.");
            System.exit(1);
        }
        clear();
        addAll(compacted);
    }
    
    /**
     * Sorts the uv frames in increasing frequnecy order.
     * 
     */
    public void sort() {
        sort(new Comparator<UVFrame>() {
            @Override
            public int compare(UVFrame a, UVFrame b) { return Double.compare(a.getFrequency(), b.getFrequency()); }
        });
    }
    
    
    /**
     * Despikes the visibility data, flagging any significant outliers from the full dataset. Any
     * visibility that is expected to occur at a probability &lt;10% given the size of the full dataset,
     * and assuming Gaussian noise, is flagged (weighted zero).
     * 
     * @return  the number of visibilities flagged.
     * 
     * @see #despike(double)
     * @see UVFrame#despike(double)
     */
    public int despike() {
        // Each visibility has 2 degrees of freedom...
        return despike(CumulativeNormalDistribution.inverseComplementAt(0.05 / countVisibilities()));
    }
    
    
    /**
     * Despikes the visibility data, flagging any outliers above the specified significance level.
     * 
     * @param level     The signal-to-noise ratio above which to flag spikes.
     * @return          the number of visibilities flagged.
     * 
     * @see #despike()
     * @see UVFrame#despike(double)
     */
    public int despike(double level) {
        return parallelStream().mapToInt(frame -> frame.despike(level)).sum();
    }
    
    
    /**
     * Gets a hastable for looking up uv frames by frequency.
     * 
     * @return      frequency lookup table.
     */
    public Hashtable<Double, UVFrame> getFrequencyLookup() {
        Hashtable<Double, UVFrame> table = new Hashtable<>(size());
        stream().forEach(frame -> table.put(frame.getFrequency(), frame));
        return table;
    }
    
    /**
     * Adds visibilities from another <i>uv</i> dataset. If the added frames match existing frames in frequency
     * exactly, then they are accumulated into the existing uv frames, or otherwise as independent frames.
     * The set is sorted again after the addition.
     * 
     * @param set       Visibility data set to add. It may include null frames, which are skipped.
     */
    public void absorb(UVDataSet set) {
        final Hashtable<Double, UVFrame> fLookup = getFrequencyLookup();
        
        set.stream().filter(f -> f != null).forEach(f -> {
            UVFrame localFrame = fLookup.get(f.getFrequency());
            if(localFrame == null) {
                localFrame = new UVFrame(f.getFrequency(), f.getBandwidth(), delta, f.primaryFWHM);
                add(localFrame);
            }
            else if(f.getPrimaryFWHM() < localFrame.getPrimaryFWHM()) localFrame.primaryFWHM = f.primaryFWHM;
            
            final UVFrame to = localFrame;  
            f.values().stream().forEach(vis -> to.add(vis.u(), vis.v(), vis.wre, vis.wim, vis.w));
        });
        
        sort();
    }
    
    /**
     * Adds a visibility measurement to this dataset.
     * 
     * @param fBin      Frequency bin index (>= 0).
     * @param f         (Hz) frequency
     * @param df        (Hz) bandwidth
     * @param u         (1/rad) u coordinate
     * @param v         (1/rad) v coordinate
     * @param re        (Jy) Real part of visibility amplitude
     * @param im        (Jy) Imaginary part of visibility amplitude 
     * @param w         (1/Jy^2) Visibility noise weight (w = 1/sigma^2)
     */
    private void addVisibility(int fBin, double f, double df, double u, double v, double re, double im, double w) {

        if(fBin < 0 || fBin >= maxFreqBins) {
            Util.warning(this, "Invalid fBin = " + fBin);
            System.exit(1);
        }

        // Pad slices with null to ensure there is an fBin'th slice.
        if(fBin >= size()) for(int i=size(); i<=fBin; i++) add(null);


        // Create slice as necessary...
        UVFrame slice = get(fBin);
        if(slice == null) {
            slice = new UVFrame(f, df, delta, Constant.c / (f * aperture));
            set(fBin, slice);
        }

        slice.add(u, v, re, im, w);
    }

    /**
     * Returns the first <i>uv</i> frame in the dataset (which may be <code>null</code>). If the dataset is non-empty, compacted, and sorted
     * it will be the lowest frequency frame.
     * 
     * @return      The first frame in the dataset (or <code>null</code>).
     * 
     * @see #compact()
     * @see #sort()
     * @see #last()
     */
    public final UVFrame first() { return get(0); }
    
    /**
     * Returns the last <i>uv</i> frame in the dataset (which may be <code>null</code>). If the dataset is non-empty, compacted, and sorted
     * it will be the highest frequency frame.
     * 
     * @return      The first frame in the dataset (or <code>null</code>).
     * 
     * @see #compact()
     * @see #sort()
     * @see #first()
     */
    public final UVFrame last() { return get(size() - 1); }
    
    /**
     * Gets the range of <i>uv</i> radii represented by this dataset.
     * 
     * 
     * @return (1/rad) The range of <i>uv</i> radii in this dataset.
     */
    public Range getUVRange() {
        final Range range = new Range();
        stream().forEach(slice -> range.include(slice.getUVRange()));
        return range;
    }
    
    /**
     * Gets the <i>u</i> and <i>v</i> ranges spanned by this dataset.
     * 
     * 
     * @return (1/rad) The range of <i>u</i> and <i>v</i> coordinates spanned by this dataset this dataset.
     */
    public Range2D getUVRange2D() {
        final Range2D range = new Range2D();
        stream().forEach(slice -> range.include(slice.getUVRange2D()));
        return range;
    }
    
    /**
     * Gets the total interferometric flux in this data, i.e. &sum; w |vis| / &sum; w
     * 
     * @return      (Jy) The total flux in this dataset
     */
    public double getFlux() {
        double sum = 0.0, sumw = 0.0;
        for(UVFrame slice : this) for(UVFrame.Visibility e : slice.values()) {
            sum += ExtraMath.hypot(e.wre, e.wim);
            sumw += e.w;
        }
        return sum / sumw;
    }
    
    /**
     * Gets the flux centered (symmetrically) in the field of this dataset.
     * 
     * 
     * @return  (Jy) The flux that is symmetrically centered in the field covered by this dataset. 
     */
    public double getCenteredFlux() {
        double sum = 0.0, sumw = 0.0;
        for(UVFrame slice : this) for(UVFrame.Visibility e : slice.values()) {
            sum += e.wre;
            sumw += e.w;
        }
        return sum / sumw;
    }

    /**
     * Randomly invert the sign of visibility amplitudes in this dataset.
     * 
     * @see #jackknife(Random)
     * @see UVFrame#jackknife()
     * 
     */
    public void jackknife() {
        jackknife(new BufferedRandom());
    }
    
    
    /**
     * Randomly invert the sign of visibility amplitudes in this dataset.
     * 
     * @param random        random generator to use.
     * 
     * @see #jackknife()
     * @see UVFrame#jackknife(Random)
     *  
     */
    public void jackknife(Random random) {
        parallelStream().forEach(slice -> slice.jackknife(random));      
    }

    /**
     * Gets the coverage weighted mean frequency in this dataset, over all frames contained within. 
     * 
     * @return  (Hz) The noise weighted mean frequency of this dataset.
     * 
     * @see #getMeanFrequency(int, int)
     * 
     */
    public double getMeanFrequency() {
        return getMeanFrequency(0, size());
    }

    /**
     * Gets the coverage weighted mean frequency for a range of consecutive frames in this dataset
     * 
     * @param from      the index of the starting frame (inclusive)
     * @param to        the index of the ending frame (exclusive)
     * @return  (Hz) The noise weighted mean frequency over the selected frames.
     * 
     * @see #getMeanFrequency()
     * 
     */
    public double getMeanFrequency(int from, int to) {
        double sum = 0.0, sumw = 0.0;
 
        for(; from < to; from++) {
            UVFrame slice = get(from);
            if(slice == null) continue;

            double w = slice.getWeightSum();
            sum += w * slice.getFrequency();
            sumw += w;
        }
        return sum / sumw;
    }

    /**
     * Gets the range of frequencies covered by this dataset
     * 
     * @return  (Hz) Range of frequencies in this dataset
     */
    public double getFrequencyRange() {
        Range r = new Range();
        for(UVFrame slice : this) r.include(slice.getFrequency());
        return r.span();
    }

    
    /**
     * Gets the total bandwidth covered by this dataset
     * 
     * @return  (Hz) Total bandwidth in this dataset
     */
    public double getTotalBandwidth() {
        return parallelStream().mapToDouble(UVFrame::getBandwidth).sum();
    }

    
    /**
     * Counts the number of visibility points in this dataset.
     * 
     * @return      Total number of independent visibilities in this dataset.
     */
    public int countVisibilities() {
        return parallelStream().mapToInt(UVFrame::size).sum();
    }

    /**
     * Returns an estimate of the size of the synthesized beam based purely on
     * the radial distribution of uv weights.
     * 
     * @return      (rad) The estimated circular FWHM of the syntheized beam of this interferometric dataset.
     */
    public double estimateSynthesticFWHM() {
        double sumwd2 = 0.0, sumw = 0.0;
        
        for(UVFrame slice : this) for(UVFrame.Visibility v : slice.values()) {
                double r = Math.sqrt(v.u() * v.u() + v.v() * v.v());

                // Account for the fact that the number of point at r increases as r...
                // To effectively reduce to a 1D profile...
                if(r > 0.0) {
                    sumwd2 += v.weight() * r;
                    sumw += v.weight() / r;
                }
            }
        

        return UVImager.beamC / Math.sqrt(sumwd2 / sumw);
    }

    
    /** The maximum number of frames that a dataset may hold */
    public static final int maxFreqBins = 0x100000;
}
