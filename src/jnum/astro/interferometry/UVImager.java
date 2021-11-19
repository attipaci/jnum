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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import jnum.Constant;
import jnum.ExtraMath;
import jnum.Unit;
import jnum.Util;
import jnum.astro.EquatorialCoordinates;
import jnum.data.DataPoint;
import jnum.data.FlagCompanion;
import jnum.data.WeightedComplex;
import jnum.data.WeightedPoint;
import jnum.data.image.Gaussian2D;
import jnum.data.image.Grid2D;
import jnum.data.image.Image2D;
import jnum.data.image.Observation2D;
import jnum.data.image.SkyGrid;
import jnum.data.image.overlay.Flagged2D;
import jnum.data.image.region.EllipticalSource;
import jnum.fft.MultiFFT;
import jnum.fits.FitsProperties;
import jnum.math.Complex;
import jnum.math.Range;
import jnum.math.Range2D;
import jnum.math.SphericalCoordinates;
import jnum.math.Vector2D;
import jnum.math.specialfunctions.CumulativeNormalDistribution;
import jnum.projection.Gnomonic;
import jnum.util.BufferedRandom;
import nom.tam.fits.BasicHDU;
import nom.tam.fits.FitsException;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;
import nom.tam.util.Cursor;


/**
 * Class for imaging interferometric data from measured visibilities in the uv plane.
 * 
 * 
 * @author Attila Kovacs
 *
 */
public class UVImager {
    /**
     * Set the field size by the relative primary beam response at the field edges (assuming a Gaussian promary beam)
     */
    public double minPrimaryResponse = 0.3;

    /** The equatorial coordinates of the phase center */
    public EquatorialCoordinates equatorial = new EquatorialCoordinates();

    private WeightedComplex[][] vis;
    private Vector2D delta;

    private FitsProperties fitsProperties;
    private WeightedPoint primaryFWHM = new WeightedPoint();
    private WeightedPoint frequency = new WeightedPoint();
    private Range frequencyRange = new Range();

    private double weighting = 0.0; // Natural weights;


    // sigmax * sigmaw = 1
    // sigmax * sigmaf = 1 / 2pi
    // fwhmX * sigmaF  = 2.35 / 2pi
    // fwhmX * df * Sf = 2.35 / 2pi
    // Sf = (2.35 / 2pi) / (fwhmX * df)

    /**
     * The product of standard deviation in one domain and FWHM in the other (Fourier) domain for Gaussian beams. It comes
     * handy for converting between these quantities when moving for one domain to the other.
     */
    public static final double beamC = Constant.sigmasInFWHM / Constant.twoPi;

    /**
     * Constructor with a given image size. For high-fidelity imaging the u.v sizes should be at least a factor of
     * 2 larger than the occupied highest uv bins. Also, sizes must be a power of 2 for the imaging via FFT. So, 
     * for example, if your data populates up to bin 100 in u, then you probably want to set the size in u to 256
     * (or even 512). Larger images are slower to transform, of course. But that is generally not an issue, unless
     * you are expecting to produce thousands of images in one go.
     * 
     * 
     * @param sizeU   Size in the u-direction. Must be a power of 2.  
     * @param sizeV   Size in the v-direction. Must be a power of 2.
     */
    public UVImager(int sizeU, int sizeV) {
        if(sizeU != 1<<ExtraMath.log2round(sizeU)) throw new IllegalArgumentException("sizeU is not a power of 2.");
        if(sizeV != 1<<ExtraMath.log2round(sizeV)) throw new IllegalArgumentException("sizeV is not a power of 2.");
        
        vis = new WeightedComplex[sizeU][sizeV];
        Arrays.stream(vis).parallel().forEach(row -> vIndices().forEach(j -> row[j] = new WeightedComplex()));
        fitsProperties = new FitsProperties();
        fitsProperties.setCreatorName(getClass().getSimpleName());
    }

    /**
     * Gets the standard FITS header properties associated to this uv imager.
     * 
     * @return      Associated FITS properties.
     * 
     * @see FitsProperties#setTelescopeName(String)
     * @see FitsProperties#setInstrumentName(String)
     * @see FitsProperties#setOrganization(String)
     * @see FitsProperties#setObjectName(String)
     */
    public final FitsProperties getFitsProperties() {
        return fitsProperties;
    }

    /**
     * Set a square uv grid size.
     * 
     * @param d     (1/rad) Common grid cell size for u and v. 
     * 
     * @see #setUVResolution(Vector2D)
     * @see #getUVResolution()
     */
    public void setUVResolution(double d) {
        setUVResolution(new Vector2D(d, d));
    }

    /**
     * Set a rectangular uv grid size.
     * 
     * @param d     (1/rad) independent u and v grid cell sized. 
     * 
     * @see #setUVResolution(double)
     * @see #getUVResolution()
     */
    public void setUVResolution(Vector2D d) {
        delta = d.copy();
    }
    
    /**
     * Gets the cell size for the uv grid.
     * 
     * @return  (1/rad) u and v cell sizes for grid.
     * 
     * @see #setUVResolution(Vector2D)
     * @see #setUVResolution(double)
     */
    public Vector2D getUVResolution() {
        return delta;
    }


    /**
     * Sets the telescope name for the standard FITS header.
     * 
     * @param name      Name of the telescope, e.g. "SMA"
     * 
     * @see #getFitsProperties()
     */
    public void setTelescope(String name) {
        fitsProperties.setTelescopeName(name);
    }
    
    /**
     * Sets the name of organization where this image originated (for the standard FITS header).
     * 
     * @param name      Name of organization, e.g. "Center for Astrophysics | Harvard and Smithsonian".
     * 
     * @see #getFitsProperties()
     */
    public void setOrganization(String name) {
        fitsProperties.setOrganization(name);
    }

    /**
     * Sets the name of instrument(s) that produced this data (for the standard FITS header).
     * 
     * @param name      Instrument name or list, e.g. "Rx230, Rx345".
     * 
     * @see #getFitsProperties()
     */
    public void setInstrument(String name) {
        fitsProperties.setInstrumentName(name);
    }

    
    /**
     * Sets the name of the observed source (for the standard FITS header).
     * 
     * @param value      Source name, e.g. "3c84".
     * 
     * @see #getFitsProperties()
     */
    public void setSourceName(String value) {
        fitsProperties.setObjectName(value);
    }

    /**
     * Sets the equatorial coordinates of the observed source position.
     * 
     * @param eq    Equatorial coordinates in the current or standard (e.g. J2000) epoch.
     */
    public void setEquatorial(EquatorialCoordinates eq) {
        this.equatorial = eq;
    }

    /**
     * Sets the primary field of view to image. The field is the circular are within which the Gaussian
     * beam response exceeds the specified value.
     * 
     * @param value     (0.0 -- 1.0) Threshold value for the relative beam response.
     */
    public void setMinPrimaryResponse(double value) {
        minPrimaryResponse = value;
    }
    
    /**
     * Gets the mean primary beam size for the visibilities contained in this image set.
     * 
     * @return  (rad) Primary beam FWHM.
     */
    public final double getPrimaryFWHM() {
        return primaryFWHM.value();
    }

    /**
     * Returns the solid angle of the mean primary beam for this <i>uv</i> image.
     * 
     * @return  (sr) The solid angle of the primary beam, i.e. 2 &pi; &sigma;<sup>2</sup> &cong; 1.14 FWHM<sup>2</sup>.
     */
    public double getPrimaryBeamArea() {
        double r = getPrimaryFWHM();
        return Gaussian2D.areaFactor * r * r;
    }
    
    /**
     * Returns the size of the imaged area, given the relative primary beam response cutoff currently in use.
     * 
     * @return  (sr) The solid angle of the imaged area, given the current edge response cutoff.
     */
    public double getImageArea() {
        double r = getPrimaryFWHM() * Math.sqrt(-2.0 * Math.log(minPrimaryResponse));
        return Math.PI * r * r;
    }
    
    /**
     * Returns the total area of the image that is used for the Fourier transform.
     * 
     * @return  (sr) The total solid angle that is Fourier transformed when imaging the <i>uv</i> plane.
     */
    public double getTotalArea() {
        return 1.0 / (delta.x() * delta.y());
    }
    
    /**
     * Returns the total degrees of freedom (DOF) in this <i>uv</i> plane, properly
     * accounting for the fact that not all visibilities carry the same weight. This is a very useful 
     * qunatity for determining the size of the search space that in turn determines confidence levels.
     * 
     * @return      The degrees of freedom in the image.
     */
    public double countDOF() {
        double maxw = parallelStreamValid().mapToDouble(WeightedComplex::weight).max().orElse(0.0);
        if(maxw == 0.0) return 0.0;
        
        // x2 because of re/im parts are 2 degrees of freedom per uv point.
        return 2.0 * parallelStreamValid().mapToDouble(WeightedComplex::weight).sum() / maxw;
    }
    
    /**
     * Returns the number of populated (with weight &gt;0) cells in the <i>uv</i>-plane.
     * 
     * @return  the number of grid cells with valid data in the <i>uv</i>-plane.
     */
    public int countPoints() {
        return parallelStreamValid().mapToInt(z -> 1).sum();
    }
    
    
    /**
     * Gets the weighted mean frequency for the set of visibilities included in this image set.
     * 
     * @return  (Hz) Weighted mean frequency to be imaged.
     *
     */
    public final double getMeanFrequency() {
        return frequency.value();
    }

    /**
     * Adds a uv frame to the image set.
     * 
     * @param uv    Frame to include in image set.
     * 
     * @see #add(UVFrame, double)
     */
    public void add(UVFrame uv) {
        add(uv, 1.0);
    }


    /**
     * Adds a scaled (or gain corrected) uv frame to this image set
     * 
     * @param uv        Frame to include in image set.
     * @param gain      Gain factor, i.e. divide amplitudes by this gain when adding.
     * 
     * @see #add(UVFrame)
     */
    public void add(UVFrame uv, double gain) {
        double w = uv.getWeightSum();
        
        primaryFWHM.average(uv.getPrimaryFWHM(), w);
        frequency.average(uv.getFrequency(), w);
        frequencyRange.include(uv.getFrequency());
        uv.values().stream().forEach(x -> add(x, gain));
    }

    /**
     * Adds a single gain-corrected visibility measurement to the imaging set.
     * 
     * @param vis       Visibility measurement datum       
     * @param gain      Gain factor, i.e. divide amplitudes by this gain when adding.
     */
    private void add(UVFrame.Visibility vis, double gain) {
        if(vis.w <= 0.0) return;

        add(vis, gain, false);
        add(vis, gain, true);           // symmetrize (it should be symmetric already...)
    }

    /**
     * Adds a single gain-corrected visibility measurement to the imaging set.
     * 
     * @param vis       Visibility measurement datum       
     * @param gain      Gain factor, i.e. divide amplitudes by this gain when adding.
     * @param isMirror  true if to add as a symmetric conjugate pair of the specified visibility. Otherwise false.
     */
    private void add(UVFrame.Visibility vis, double gain, boolean isMirror) {
        if(weighting != 0.0) throw new IllegalStateException("Cannot add new visibilities after re-weighting.");

        int i = getI(isMirror ? -vis.u() : vis.u());
        int j = getJ(isMirror ? -vis.v() : vis.v());

        WeightedComplex z = this.vis[i][j];

        z.add(gain * vis.wre, gain * (isMirror ? -vis.wim : vis.wim));
        z.addWeight(gain * gain * vis.w);
    }

    /**
     * UV image size in the u coordinate direction.
     * 
     * @return      Grid size in the u coordinate direction. 
     * 
     * @see #sizeV()
     */
    public final int sizeU() { return vis.length; }

    /**
     * UV image size in the v coordinate direction.
     * 
     * @return      Grid size in the v coordinate direction. 
     * 
     * @see #sizeU()
     */
    public final int sizeV() { return vis[0].length; }

    /**
     * Stream of integer u coordinate indices (0 to {@link #sizeU()}.
     * 
     * @return  u coordinate index stream.
     */
    public final IntStream uIndices() {
        return IntStream.range(0,  sizeU());
    }
    
    /**
     * Stream of integer v coordinate indices (0 to {@link #sizeV()}.
     * 
     * @return  v coordinate index stream.
     */
    public final IntStream vIndices() {
        return IntStream.range(0,  sizeV());
    }
    
    /**
     * Stream of all the weighted complex visibilities, populated or not.
     * 
     * @return  A stream of all visibilities in this image set.
     * 
     * @see #streamValid()
     * @see #parallelStream()
     * 
     */
    public final Stream<WeightedComplex> stream() {
        return Arrays.stream(vis).flatMap(Arrays::stream);
    }
    
    /**
     * Stream of only the valid (populated) weighted complex visibilities in this image set.
     * 
     * @return  A stream of the populated visibilities in this image set.
     * 
     * @see #stream()
     * @see #parallelStreamValid()
     */
    public final Stream<WeightedComplex> streamValid() {
        return stream().filter(vis -> isValid(vis));
    }
    
    
    /**
     * Parallel stream of all the weighted complex visibilities, populated or not.
     * 
     * @return  A parallel stream of all visibilities in this image set.
     * 
     * @see #parallelStreamValid()
     * @see #stream()
     * 
     */
    public final Stream<WeightedComplex> parallelStream() {
        return Arrays.stream(vis).parallel().flatMap(Arrays::stream);
    }
    

    /**
     * Parallel stream of only the valid (populated) weighted complex visibilities in this image set.
     * 
     * @return  A parallel stream of the populated visibilities in this image set.
     * 
     * @see #streamValid()
     * @see #parallelStream()
     */
    public final Stream<WeightedComplex> parallelStreamValid() {
        return parallelStream().filter(vis -> isValid(vis));
    }
    
    /**
     * Checks if the visibility is valid (i.e. populated).
     * 
     * @param vis   Visibility to check
     * @return      true if valid (populated), otherwise false.
     * 
     * @see #isValid(int, int)
     */
    public boolean isValid(WeightedComplex vis) {
        return vis.weight() > 0.0;
    }
    
    /**
     * Checks if the visibility at the given index is valid (i.e. populated).
     * 
     * @param i     Index in the u coordinate direction.
     * @param j     Index in the v coordinate direction.
     * @return      true if valid (populated), otherwise false.
     * 
     * @see #isValid(WeightedComplex)
     */
    public final boolean isValid(int i, int j) {
        return isValid(vis[i][j]);
    }
    
    /**
     * Gets the grid index in the u-direction for the given u coordinate.
     * 
     * @param u     Coordinate value in the u-direction
     * @return      First grid index corresponding to the u coordinate.
     * 
     * @see #getJ(double)
     * @see #getX(int)
     */
    public final int getI(double u) {
        int iu = (int) Math.round(u / delta.x());
        if(iu >= 0) return iu;
        return sizeU() + iu;
    }

    /**
     * Gets the grid index in the v-direction for the given coordinate.
     * 
     * @param v     Coordinate value in the v-direction
     * @return      Second grid index corresponding to the v coordinate.
     * 
     * @see #getI(double)
     * @see #getY(int)
     */
    public final int getJ(double v) {
        int iv = (int) Math.round(v / delta.y());
        if(iv >= 0) return iv;
        return sizeV() + iv;
    }

    /**
     * Gets the signed u-bin for the given first grid index. The actual u coordinate is the returned value multiplied with
     * the grid resolution in the u direction.
     * 
     * @param i     First grid index.
     * @return      Signed coordinate bin for u.
     * 
     * @see #getI(double)
     * @see #getY(int)
     * @see #getUVResolution()
     */
    public final int getX(int i) {
        if((i<<1) <= sizeU()) return i;
        return i - sizeU();
    }

    /**
     * Gets the signed v-bin for the given second grid index. The actual v coordinate is the returned value multiplied with
     * the grid resolution in the v direction.
     * 
     * @param j     Second grid index.
     * @return      Signed coordinate bin for v.
     * 
     * @see #getJ(double)
     * @see #getX(int)
     * @see #getUVResolution()
     */
    public final int getY(int j) {
        if((j<<1) <= sizeV()) return j;
        return j - sizeV();
    }

    /**
     * Clear all visibilities from the image set, and reset the frequency and primary beam averages. 
     * 
     */
    public void clear() {
        parallelStream().forEach(WeightedComplex::noData);
        frequencyRange.empty();
        frequency.noData();
        primaryFWHM.noData();
    }

    /**
     * Gets the sum of all visibility wwights in this images set.
     * 
     * @return      visibility weight sum.
     */
    public double getWeightSum() {
        return parallelStreamValid().mapToDouble(WeightedComplex::weight).sum();
    }


    /**
     * Gets the uv amplitude, phase, and weight images. (The phases replace the exposure plane in the
     * standard {@link Observation2D} object.)
     * 
     * @return      The multiplane image set fully representing the uv-plane of this interferometric imaging set. 
     */
    public UVImage2D getUVImage() {
        UVImage2D uv = new UVImage2D(Float.class, FlagCompanion.Type.BYTE);
        uv.setSize(sizeU(), sizeV());
        uv.setUnderlyingBeam(1.0);
        uv.setUnit(new Unit("abu", 1.0));

        Grid2D<SphericalCoordinates> grid = new SkyGrid();
        grid.setProjection(new Gnomonic());
        grid.setReferenceIndex(new Vector2D(sizeU() >>> 1, sizeV() >>> 1));
        grid.setResolution(delta);
        grid.setReference(new SphericalCoordinates());
        uv.setGrid(grid);

        double x0 = sizeU() >>> 1;
        double y0 = sizeV() >>> 1;

        uv.getExposures().setUnit("deg");
        
        uIndices().parallel().forEach(x -> {
            int i = getI((x-x0) * delta.x());
            
            vIndices().forEach(y -> {
                int j = getJ((y-y0) * delta.y());
                WeightedComplex z = vis[i][j];

                if(isValid(z)) {
                    uv.set(x, y, z.abs() / z.weight());
                    uv.setExposureAt(x, y, z.angle() / Unit.degree);
                    uv.setWeightAt(x, y, z.weight());
                }
                else uv.flag(x, y);
            });
        });

        return uv;
    }

    
    /**
     * Despikes the uv image plane, flagging any significant outliers. Any visibility that is expected to occur 
     * at a probability &lt;10% given the size of the <i>uv</i> image plane, under Gaussian noise, is flagged (weighted zero).
     * 
     * @return  the number of visibilities flagged.
     * 
     * @see #despike(double)
     */
    public int despike() {
        return despike(CumulativeNormalDistribution.inverseComplementAt(0.05 / countPoints()));
    }
      
    /**
     * Despikes the visibility data, flagging any outliers above the specified significance level.
     * 
     * @param level     The signal-to-noise ratio above which to flag spikes.
     * @return          the number of visibilities flagged.
     * 
     * @see #despike()
     */
    public int despike(double level) {
        final double L2 = level * level;
        return parallelStreamValid().filter(z -> z.weight() * z.squareNorm() > L2).mapToInt(z -> {
            z.setWeight(0.0);
            return 1;
        }).sum();
       
    }
    
    /**
     * Randomly invert the visibilities in this image set.
     * 
     * @see #jackknife(Random)
     * @see #randomize()
     * 
     */
    public final void jackknife() {
        jackknife(new BufferedRandom());
    }

    /**
     * Randomly invert the visibilities in this image set.
     * 
     * @param random    random generator to use.
     * 
     * @see #jackknife()
     * @see #randomize(Random)
     * 
     */
    public void jackknife(Random random) {
        fitsProperties.setObjectName(fitsProperties.getObjectName() + "-JK");
        streamValid().forEach(z -> z.rotate(Constant.twoPi * random.nextDouble()));
    }

    /**
     * Replace actual data with random Gaussian variates for the same noise weights.
     * 
     * @see #randomize(Random)
     * @see #jackknife()
     */
    public final void randomize() {
        randomize(new BufferedRandom());
    }
    
    /**
     * Replace actual data with random Gaussian variates for the same noise weights.
     * 
     * @param random    random generator to use.
     * 
     * @see #randomize()
     * @see #jackknife(Random)
     * 
     */
    public void randomize(Random random) {
        streamValid().forEach(z -> z.set(random.nextGaussian() / Constant.sqrt2, random.nextGaussian() / Constant.sqrt2));
    }
    

    /**
     * Apply a uv taper, which is equivalent to smoothing the reconstructed image with a
     * Gaussian beam of the specified fwhm
     * 
     * @param fwhm  (rad) FWHM of the equivalent Gaussian smoothing beam in configuration space. 
     */
    public void taper(double fwhm) {
        double sigma = beamC / fwhm;

        uIndices().parallel().forEach(i -> {
            double x = getX(i) * delta.x();
            
            vIndices().filter(j -> isValid(i, j)).forEach(j -> {
                double d = ExtraMath.hypot(x, getY(j) * delta.y()) / sigma;
                double A = Math.exp(-0.5 * d * d);
                vis[i][j].scaleValue(A);
                vis[i][j].scaleWeight(A);            
            });
        });

    }

    /**
     * Gets the range of <i>uv</i> radii populated in this image set.
     * 
     * @return  Range of <i>uv</i> radii containing valid (populated) visibilities.
     */
    public Range getUVRange() {
        Range r = new Range();
        uIndices().forEach(i -> {
            double x = getX(i) * delta.x();
            vIndices().filter(j -> isValid(i, j)).mapToDouble(j -> getY(j) * delta.y()).forEach(y -> r.include(ExtraMath.hypot(x, y)));
        });
        return r;
    }
    
    /**
     * Returns the 2D range of <i>u</i> anbd <i>v</i> coordinates contained in the <i>uv</i> plane of this image.
     * 
     * @return      (1/rad<sup>2</sup>) The 2D range of populated <i>u</i> anbd <i>v</i> coordinates
     */
    public Range2D getUVRange2D() {
        Range2D r = new Range2D();
        uIndices().forEach(i -> {
            double x = getX(i) * delta.x();
            vIndices().filter(j -> isValid(i, j)).mapToDouble(j -> getY(j) * delta.y()).forEach(y -> r.include(x, y));
        });
        return r;
    }
    
    /**
     * Returns the <i>uv</i> radial profile with the default resolution of a diagonal <i>uv</i> pixel element.
     * 
     * @return      The measured <i>uv</i> radial profile, including weights/uncertainties.
     */
    public DataPoint[] getUVProfile() {
        return getUVProfile(Math.min(delta.x(), delta.y()));
    }
    

    /** 
     * Return the radial uv flux profile over the noise.
     * 
     * @param res   (1/rad) Radial resolution
     * @return      Average radial flux profile above noise. 
     */
    public DataPoint[] getUVProfile(final double res) { 
        
        int N = 1 + (int) Math.ceil(getUVRange().max() / res);
        DataPoint[] profile = DataPoint.createArray(N); 
        
        uIndices().forEach(i -> {
           int toj = (i == 0) ? sizeV()>>>1 : sizeV();
           
           for(int j=toj; --j >= 0; ) if(isValid(i, j)) {
                WeightedComplex wA = vis[i][j];
                int bin = (int) Math.round(ExtraMath.hypot(getX(i) * delta.x(), getY(j) * delta.y()) / res);
                
                // Rayleigh distribution...
                // mu_r = sqrt(pi/2) sigma_r, mu_r^2 = pi/(2 w_x)
                // var(r^2) = <|r^2|> - mu_r^2 = (2 - pi/2) sigma_x^2 -> w_r = w_x / (2 - pi/2)
                profile[bin].add(wA.abs() - Math.sqrt(Constant.halfPi * 0.5 * wA.weight()));    // TODO Why the extra 1/2 factor here?
                profile[bin].addWeight(wA.weight()); 
            }
        });

        Arrays.stream(profile).parallel().forEach(x -> {
            x.endAccumulation();
            x.scaleWeight(1.0 / (2.0 - Constant.halfPi));
            //if(x.value() < 0.0) x.setValue(0.0);
        });

        return profile;
    }


    /**
     * Produces a multiplane image from the UV dataset, with the specified name.
     * 
     * @param data      uv data with power-of-2 dimensions in u and v.    
     * 
     * @return      A multiplane 2D image set, consisting of the synthesized image (main image), 
     *              synthesized beam image, and primary beam weight coverage planes.
     */
    private SynthesizedImage2D image(Complex[][] data) { 
        MultiFFT fft = new MultiFFT();
        fft.setParallel(2 * Runtime.getRuntime().availableProcessors());
        fft.setTwiddleErrorBits(8);
        fft.complexBack(data);

        SynthesizedImage2D im = new SynthesizedImage2D(Double.class, FlagCompanion.Type.BYTE);
        im.setSize(sizeU(), sizeV());

        Grid2D<SphericalCoordinates> grid = new SkyGrid();
        grid.setProjection(new Gnomonic());
        grid.setReferenceIndex(new Vector2D(sizeU() / 2, sizeV() / 2));
        grid.setResolution(new Vector2D(1.0 / (sizeU() * delta.x()), 1.0 / (sizeV() * delta.y())));
        grid.setReference(equatorial);
        im.setGrid(grid);

        final int x0 = sizeU() / 2;
        final int y0 = sizeV() / 2;

        final Vector2D resolution = grid.getResolution();
        final Gaussian2D response = new Gaussian2D(primaryFWHM.value());

        uIndices().parallel().forEach(i -> {
            final int x = getX(i) + x0;
            final double dx = (x - x0) * resolution.x();

            vIndices().forEach(j -> {
                final int y = getY(j) + y0;
                final double dy = (y - y0) * resolution.y();
                final double G = response.valueAt(dx, dy);

                if(G > minPrimaryResponse) {
                    im.set(x, y, data[i][j].re() / G);
                    im.setWeightAt(x, y, G * G);
                    im.setExposureAt(x, y, 1.0);
                }
            });
        });

        im.autoCrop();

        double sumw = getWeightSum();
        im.getImage().scale(1.0 / sumw);
        im.getWeightImage().scale(sumw);

        return im;
    }


    /**
     * Estimates the size of a (circular) synthesized beam in this image, based on the radial distribution of
     * the <i>uv</i> data alone.
     * 
     * @return  (rad) The estimated FWHM size of a synthesized beam under the assumption that it is curcular (not elongated). 
     */
    public double estimateSynthesticFWHM() {
        double sumwd2 = 0.0, sumw = 0.0;

        for(int i=sizeU(); --i >= 0; ) {
            double x = getX(i) * delta.x();

            for(int j=sizeV(); --j >= 0; ) {
                double y = getY(j) * delta.y();
                double w = vis[i][j].weight();    
                double r = Math.sqrt(x * x + y * y);

                // Account for the fact that the number of point at r increases as r...
                // To effectively reduce to a 1D profile...
                if(r > 0.0) {
                    sumwd2 += w * r;
                    sumw += w / r;
                }
            }
        }

        return beamC / Math.sqrt(sumwd2 / sumw);
    }

    /**
     * Returns the synthesized beam image for the <i>uv</i> plane to be imaged.
     * 
     * @return  the synthesized beam image.
     */
    public Observation2D getSynthesizedBeam() {
        Complex[][] W = new Complex[sizeU()][sizeV()];  
        uIndices().parallel().forEach(i -> vIndices().forEach(j -> W[i][j] = new Complex(vis[i][j].weight(), 0.0)));

        Observation2D im = image(W);
        im.setUnit(new Unit("response", 1.0));

        return im;
    }

    /**
     * Returns the dirty beam image from the <i>uv</i> image data. 
     * 
     * @return  the dirty beam image, with the specified edge cutoff based on a threshold to the primary beam response.
     */
    public Observation2D image() {
        final WeightedComplex[][] uv = new WeightedComplex[sizeU()][sizeV()];
        uIndices().parallel().forEach(i -> {
            vIndices().forEach(j -> uv[i][j] = vis[i][j].copy());
        });


        Observation2D beam = getSynthesizedBeam();
        double iFWHM = estimateSynthesticFWHM();
        Util.info(this, "Estimated resolution = " + Util.f2.format(iFWHM / Unit.arcsec) + " arcsec");
        EllipticalSource g = new EllipticalSource(beam.getReference(), iFWHM);
        g.adaptTo(beam);
        Util.info(this, "Actual resolution = " + g.getGaussian2D().toString(Unit.get("arcsec"), Util.f2));

        Observation2D im = image(uv);
        im.setUnderlyingBeam(g.getGaussian2D());
        im.setExposureImage(beam.getImage());
        im.setUnit(new Unit("Jy/beam", 1.0));        

        return im;
    }

    /**
     * Returns the typical weight of the <i>uv</i> image, such a the geometric mean weight across all
     * populated <i>uv</i> grid pixels.
     * 
     * @return  (~1/Jy<sup>2</sup>) The typical (representative) weight of <i>uv</i> data in this image.
     */
    private double getTypicalWeight() {
        return Math.exp(parallelStreamValid().mapToDouble(z -> Math.log(z.weight())).average().orElse(0.0));
    }


    /**
     * Checks if the image is naturally weighted to provide maximum sensitivity, at the cost of
     * some loss in spatial resolution.
     * 
     * @return      true if the image is naturally (noise) weighted, otherwise false.
     * 
     * @see #isUniformWeighted()
     * @see #isRobustWeighted()
     */
    public final boolean isNaturalWeighted() { return weighting == 0.0; }

    /**
     * Checks if the image is uniformly weighted to provide maximum spatial resolution at the cost
     * of reduced sensitivity.
     * 
     * @return      true if the image is uniformly weighted, otherwise false.
     * 
     * @see #isNaturalWeighted()
     * @see #isRobustWeighted()
     * @see #uniformWeight()
     * 
     */
    public final boolean isUniformWeighted() { return weighting < 0.0; }

    /**
     * Checks if the image is robust weighted to providing an intermediate trade-off between 
     * spatial resolution and sensitivity.
     * 
     * @return      true if the image is robust weighted, otherwise false.
     * 
     * @see #isNaturalWeighted()
     * @see #isUniformWeighted()
     * @see #robustWeight(double)
     * 
     */
    public final boolean isRobustWeighted() { return weighting > 0.0; }

    /**
     * Change the weights from natural noise weights to robust weights, by suppressing weights that
     * exceeed the 'typical' weights in the uv-plane by a factor larger than the supplied threshold.
     * Threshold values much greater than 1 will approach the natural weight limit, while threshold
     * values less than 1 will approachuniform weigts. A zero threshold is essentially the same as
     * applying uniform weights. In this way the threshold can be tuned to provide any desired
     * trade-off between natural weights (maximum sensitivity) and uniform weights (maximum resolution).
     * Typical in-between values for the threshold may be in the range of 10 to 30.
     * 
     * @param threshold     Relative to typical weight at which to level of downweighting. E.g. 10.0.
     * 
     * @see #isRobustWeighted()
     * @see #uniformWeight()
     */
    public void robustWeight(double threshold) {   
        if(weighting == threshold) return;
        if(weighting != 0.0) uniformWeight();
        double maxw = threshold * getTypicalWeight();
        parallelStreamValid().forEach(z -> z.setWeight(1.0 / (1.0 / z.weight() + 1.0 / maxw)));
    }


    /**
     * Change the weights from natural to uniform, to provide maximum spatial resolution at the cost
     * of significantly reduced sensitivity.
     * 
     * @see #isUniformWeighted()
     * @see #robustWeight(double)
     * 
     */
    public void uniformWeight() {
        if(weighting < 0.0) return; // Already done!
        if(weighting > 0.0) throw new IllegalStateException("UV image was already re-weighted otherwise.");
        parallelStreamValid().forEach(z -> z.setWeight(1.0));
    }

    
    /**
     * Scales the visibilities in this image by the specified scale factor.
     * 
     * @param factor    Scale factor, e.g. for secondary gain correction or change of units.
     */
    public void scale(double factor) {
        parallelStreamValid().forEach(z -> z.scale(factor));
    }

    /**
     * Writes the image, obtained as a transform of the UV plane into a new FITS file.
     * 
     * @param name              the name or path of the image. ".fits" will be added as necessary
     * @throws FitsException    if the FITS object could not be constructed
     * @throws IOException      if there was an IO error
     * 
     * @see #writeUV(String)
     */
    public void writeImage(String name) throws FitsException, IOException {
        write(image(), name);
    }
    
    /**
     * Writes the uv image plane into a new FITS file.
     * 
     * @param name              the name or path of the image. ".fits" will be added as necessary
     * @throws FitsException    if the FITS object could not be constructed
     * @throws IOException      if there was an IO error
     * 
     * @see #writeUVProfile(String)
     * @see #writeImage(String)
     */
    public void writeUV(String name) throws FitsException, IOException {
        write(getUVImage(), name);
    }
    
    private void write(Observation2D im, String name) throws FitsException, IOException {
        String lName = name.toLowerCase();
        if(!lName.endsWith(".fits") && !lName.endsWith(".fit")) name = name + ".fits";
        im.writeFits(name, Float.class);
    }


    /**
     * Writes out the products of this imager: the uv-plane image, the uv profile, and the reconstructed
     * dirty image.
     * 
     * @param path              the directory in which to write. The files will be named after the
     *                          the object name of the observed source.
     * @throws FitsException    if the FITS object could not be constructed
     * @throws IOException      if there was an IO error
     * 
     * @see #writeProducts(String, String)
     */
    public void writeProducts(String path) throws FitsException, IOException {
        writeProducts(path, fitsProperties.getObjectName());
    }

    /**
     * Writes out the products of this imager: the uv-plane image, the uv profile, and the reconstructed
     * dirty image.
     * 
     * @param path              the directory in which to write. The files will be named after the
     *                          the object name of the observed source.
     * @param stem              the name stem of the image, without file extension. The various data products
     *                          will append a descriptiove postfix, and a file extension as appropriate to it.
     * @throws FitsException    if the FITS object could not be constructed
     * @throws IOException      if there was an IO error
     * 
     * @see #writeProducts(String)
     * @see #writeUV(String)
     * @see #writeUVProfile(String)
     * @see #writeImage(String)
     */
    public void writeProducts(String path, String stem) throws FitsException, IOException {
        stem = path + File.separator + stem;

        Util.info(this, "\nProcessing " + stem);

        writeUV(stem + "-uv.fits");
        writeUVProfile(stem + "uv-profile.dat");
        writeImage(stem + ".fits");
    }  

    /**
     * Writes a uv radial profile into an ASCII file, containing tab-separated columns.
     * 
     * @param fileName      the anme (path) of the file into which to write the uv radial profile. 
     * @throws IOException  if there was an IO error
     * 
     * @see #writeUV(String)
     * @see #writeProducts(String)
     */
    public void writeUVProfile(String fileName) throws IOException {
        DataPoint[] profile = getUVProfile(Math.min(delta.x(), delta.y()));
        try (PrintWriter out = new PrintWriter(new FileOutputStream(new File(fileName)))) {
            out.println("# bin\tdUV\tA\trms");

            for(int i=0; i<profile.length; i++) {
                out.print(i + "\t" + Util.s4.format(i * delta.x()) + "\t");
                out.println(profile[i]);
            }
            out.close();
        }
    }
    

    private void editImageHeader(Header header) throws HeaderCardException { 
        Cursor<String, HeaderCard> c = header.iterator();
        c.setKey("SMOOTH");
        
        c.add(new HeaderCard("FREQ", frequency.value() / Unit.GHz, "(GHz) Reference frequency"));
        c.add(new HeaderCard("FMIN", frequencyRange.min() / Unit.GHz, "(GHz) Lowest contributing frequency"));
        c.add(new HeaderCard("FMAX", frequencyRange.max() / Unit.GHz, "(GHz) Highest contributing frequency"));

        if(isNaturalWeighted()) c.add(new HeaderCard("WEIGHTS", "natural", "Visibility Weighting mode."));
        else if(isUniformWeighted()) c.add(new HeaderCard("WEIGHTS", "uniform", "Visibility Weighting mode."));
        else c.add(new HeaderCard("WEIGHTS", "robust:" + weighting, "Visibility Weighting mode."));
    }

    
    /**
     * A class for containing the uv-plane images of an interferometric dataset. It is a slightly
     * modified version of the {@link Observation2D} class, with the main image representing the visibility 
     * ampl;itudes, while the exposure plane of direct observations is replaced by the visibility angles.
     * It also inserting extra header information specific to interferometric imaging in FITS outputs.
     * 
     * 
     * @author Attila Kovacs
     *
     */
    public class UVImage2D extends Observation2D {
        
        /**
         * 
         */
        private static final long serialVersionUID = -4724586925113901519L;


        private UVImage2D(Class<? extends Number> dataType, FlagCompanion.Type flagType) {
            super(dataType, flagType);
            setFitsProperties(fitsProperties);
        }

        /**
         * Returns the uv angles image view/plane for this uv data
         * 
         * @return  the uv angles view/plane.
         * 
         * @see #getAngleImage()
         */
        public Flagged2D getAngles() { return super.getExposures(); }
        
        /**
         * Returns the uv angles image for this uv data
         * 
         * @return  the uv angles image data.
         * 
         * @see #getAngles()
         */
        public Image2D getAngleImage() { return super.getExposureImage(); }
        
        @Override
        public void editHeader(Header header) throws HeaderCardException { 
            super.editHeader(header);
            editImageHeader(header);
        }


        @Override
        public ArrayList<BasicHDU<?>> getHDUs(Class<? extends Number> dataType) throws FitsException { 
            ArrayList<BasicHDU<?>> hdu = super.getHDUs(dataType);
            hdu.get(1).addValue("EXTNAME", "Phase", "Visibility phases");
            return hdu;
        }
    }
    

    /**
     * A class for containing a synthesized image of an interferometric dataset. It is a slightly
     * modified version of the {@link Observation2D} class, with the synthesized beam image replacing the
     * exposures plane of direct observations, and inserting extra header information specific to
     * interferometric imaging in FITS outputs.
     * 
     * 
     * @author Attila Kovacs
     *
     */
    public class SynthesizedImage2D extends Observation2D {

        /**
         * 
         */
        private static final long serialVersionUID = -8797542965286863668L;


        private SynthesizedImage2D(Class<? extends Number> dataType, FlagCompanion.Type flagType) {
            super(dataType, flagType);
            setFitsProperties(fitsProperties);
        }

        /**
         * Returns the synthesized beam image view/plane for this uv data
         * 
         * @return  the synthesized beam data.
         * 
         * @see #getSynthesizedBeamImage()
         */
        public Flagged2D getSynthesizedBeam() { return super.getExposures(); }
        
        /**
         * Returns the synthesized beam image for this uv data
         * 
         * @return  the synthesized beam image data.
         * 
         * @see #getSynthesizedBeam()
         */
        public Image2D getSynthesizedBeamImage() { return super.getExposureImage(); }
       
        @Override
        public void editHeader(Header header) throws HeaderCardException { 
            super.editHeader(header);
            editImageHeader(header);
        }

        @Override
        public ArrayList<BasicHDU<?>> getHDUs(Class<? extends Number> dataType) throws FitsException { 
            ArrayList<BasicHDU<?>> hdu = super.getHDUs(dataType);
            hdu.get(1).addValue("EXTNAME", "Synthesized Beam", "Synthesized beam image");
            return hdu;
        }

    }
}
