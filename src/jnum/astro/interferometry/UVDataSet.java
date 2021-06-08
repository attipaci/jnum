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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.stream.Collectors;

import jnum.Constant;
import jnum.ExtraMath;
import jnum.Unit;
import jnum.Util;
import jnum.fits.FitsProperties;
import jnum.math.Range;
import jnum.math.Range2D;
import jnum.math.Vector2D;
import nom.tam.fits.BinaryTable;
import nom.tam.fits.BinaryTableHDU;
import nom.tam.fits.Fits;
import nom.tam.fits.FitsException;
import nom.tam.util.ArrayDataInput;


/**
 * A reduced, calibrated interferometric data set, as a list of uv frames, each containing a set of visibility
 * measurements for a specific frequency bin. The data may be loaded in from a standard FITS calibrated UV table.
 * 
 * 
 * @author Attila Kovacs <attila@sigmyne.com>
 *
 */
public class UVDataSet extends ArrayList<UVFrame> {
    /**
     * 
     */
    private static final long serialVersionUID = 6184860828104201918L;
    private Vector2D delta;
    private double aperture;
        
    private FitsProperties fitsProperties = new FitsProperties();
    
    /**
     * Constructor with a square uv binning grid.
     * 
     * @param aperture  (m) Dish diameter, or equivalent
     * @param uvres     (1/rad) Common bin size for u and v coordinates.
     */
    public UVDataSet(double aperture, double uvres) {
        this(aperture, new Vector2D(uvres, uvres));
    }


    /**
     * Constructor with a rectangular uv binning grid.
     * 
     * @param aperture  (m) Dish diameter, or equivalent
     * @param uvres     (1/rad) Independent u and v coordinate binning sizes.
     */
    public UVDataSet(double aperture, Vector2D uvres) {
        this.aperture = aperture;
        delta = uvres.copy();
    }

    /**
     * Constructor, which creates the uv dataset from a standard FITS calibrated uv table file. It simply calls
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
     * Gets the u,v resolutions of the uv binnind grid.
     * 
     * @return      (1/rad) u and v bin resolutions.
     */
    public final Vector2D getResolution() { return delta; }
    
    /**
     * Gets the standard FITS properties associated to this uv dataset, such as telescope and instrument
     * names, names of observers, etc. 
     * 
     * @return  Associated standard FITS header properties. 
     */
    public final FitsProperties getFitsProperties() { return fitsProperties; }
    
    
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
     * @see compact()
     * @see sort()
     */
    public void read(String fileName) throws IOException, FitsException {
        clear();
        
        try (Fits fits = new Fits(new File(fileName))) {
            BinaryTableHDU hdu = (BinaryTableHDU) fits.getHDU(1);
            fitsProperties.parseHeader(hdu.getHeader());
            
            BinaryTable table = hdu.getData();

            short[] bin = new short[1];
            double[] f = new double[1];
            double[] u = new double[1];
            double[] v = new double[1];
            double[] wre = new double[1];
            double[] wim = new double[1];
            double[] w = new double[1];
            Object[] rowData = new Object[] { bin, f, u, v, wre, wim, w };

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

                if(v[0] < 0.0) nMirror++;
                else if(v[0] == 0.0 && u[0] < 0.0) nMirror++; 
                else {
                    addVisibility(bin[0]-1, f[0], u[0], v[0], wre[0], wim[0], w[0]);
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
     * Adds visibilities from another uv dataset. If the added frames match existing frames in frequency
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
                localFrame = new UVFrame(f.getFrequency(), delta, f.primaryFWHM);
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
     * @param u         (1/rad) u coordinate
     * @param v         (1/rad) v coordinate
     * @param re        (Jy) Real part of visibility amplitude
     * @param im        (Jy) Imaginary part of visibility amplitude 
     * @param w         (1/Jy^2) Visibility noise weight (w = 1/sigma^2)
     */
    private void addVisibility(int fBin, double f, double u, double v, double re, double im, double w) {

        if(fBin < 0 || fBin >= 16384) {
            Util.warning(this, "Invalid fBin = " + fBin);
            System.exit(1);
        }

        // Pad slices with null to ensure there is an fBin'th slice.
        if(fBin >= size()) for(int i=size(); i<=fBin; i++) add(null);


        // Create slice as necessary...
        UVFrame slice = get(fBin);
        if(slice == null) {
            slice = new UVFrame(f, delta, Constant.c / (f * aperture));
            set(fBin, slice);
        }

        slice.add(u, v, re, im, w);
    }

    /**
     * Returns the first uv frame in the dataset (which may be null). If the dataset is non-empty, compacted, and sorted
     * it will be the lowest frequency frame.
     * 
     * @return      The first frame in the dataset (or null).
     * 
     * @see compact()
     * @see sort()
     * @see last()
     */
    public final UVFrame first() { return get(0); }
    
    /**
     * Returns the last uv frame in the dataset (which may be null). If the dataset is non-empty, compacted, and sorted
     * it will be the highest frequency frame.
     * 
     * @return      The first frame in the dataset (or null).
     * 
     * @see compact()
     * @see sort()
     * @see first()
     */
    public final UVFrame last() { return get(size() - 1); }
    
    /**
     * Gets the range of uv radii represented by this dataset.
     * 
     * 
     * @return (1/rad) The range of uv radii in this dataset.
     */
    public Range getUVRange() {
        final Range range = new Range();
        stream().forEach(slice -> range.include(slice.getUVRange()));
        return range;
    }
    
    /**
     * Gets the u and v ranges spanned by this dataset.
     * 
     * 
     * @return (1/rad) The range of u and v coordinates spanned by this dataset this dataset.
     */
    public Range2D getUVRange2D() {
        final Range2D range = new Range2D();
        stream().forEach(slice -> range.include(slice.getUVRange2D()));
        return range;
    }
    
    /**
     * Gets the total interferometric flux in this data, i.e. sum{ w * |vis| } / sum{ w} 
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
     * @see UVFrame.jackknife()
     * 
     */
    public void jackknife() {
        parallelStream().forEach(slice -> slice.jackknife());      
    }

    /**
     * Gets the coverage weighted mean frequency in this dataset, over all frames contained within. 
     * 
     * @return  (Hz) The noise weighted mean frequency of this dataset.
     * 
     * @see getMeanFrequency(int, int)
     * 
     */
    public double getMeanFrequency() {
        return getMeanFrequency(0, size());
    }

    /**
     * Gets the coverage weighted mean frequency for a range of consecutive frames in this dataset
     * 
     * @return  (Hz) The noise weighted mean frequency over the selected frames.
     * 
     * @see getMeanFrequency()
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
     * Counts the number of visibility points in this dataset.
     * 
     * @return      Total number of independent visibilities in this dataset.
     */
    public int countVisibilities() {
        return parallelStream().mapToInt(UVFrame::size).sum();
    }

}
