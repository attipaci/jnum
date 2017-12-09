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

package jnum.data.image;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;

import jnum.Constant;
import jnum.CopiableContent;
import jnum.ExtraMath;
import jnum.Unit;
import jnum.Util;
import jnum.data.DataPoint;
import jnum.data.Transforming;
import jnum.data.WeightedPoint;
import jnum.data.image.overlay.Flagged2D;
import jnum.data.image.overlay.RangeRestricted2D;
import jnum.data.image.overlay.Referenced2D;
import jnum.data.image.transform.CartesianGridTransform2D;
import jnum.data.image.transform.ProjectedIndexTransform2D;
import jnum.fft.MultiFFT;
import jnum.math.Coordinate2D;
import jnum.math.Range;
import jnum.math.Vector2D;
import jnum.projection.DefaultProjection2D;
import jnum.projection.Projection2D;
import nom.tam.fits.BasicHDU;
import nom.tam.fits.Fits;
import nom.tam.fits.FitsException;
import nom.tam.fits.FitsFactory;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCardException;
import nom.tam.fits.ImageHDU;



/**
 * 2D image base class with FITS I/O, simple units, and 2D grid...
 * 
 * 
 * @author pumukli
 *
 * @param <ImageType>
 * @param <ElementType>
 */
public class Map2D extends Flagged2D implements Resizable2D, Serializable, CopiableContent<Map2D> {
    /**
     * 
     */
    private static final long serialVersionUID = -2684430958862706671L;

    private MapProperties properties;

    private Vector2D reuseIndex = new Vector2D();



    private Map2D() { 
        init();    
    }

    public Map2D(Image2D data, int flagType) {
        this();
        setImage(data);
        createFlags(flagType);
    }

    public Map2D(Class<? extends Number> dataType, int flagType) {
        this(Image2D.createType(dataType), flagType);
    }

    protected void init() {
        reuseIndex = new Vector2D();
        properties = getPropertiesInstance();
        setGrid(new FlatGrid2D());
        addProprietaryUnits();
    }



    @Override
    public void setUnit(String spec) {
        setUnit(spec, getLocalUnits());
    }

    @Override
    public void setUnit(Unit u) {
        super.setUnit(u);
        if(getImage() != null) getImage().setUnit(u);
    }


    protected void addProprietaryUnits() {
        addLocalUnit(new Unit("beam", Double.NaN) {            
            /**
             * 
             */
            private static final long serialVersionUID = 7593700995697181741L;

            @Override
            public double value() { return getProperties().getImageBeamArea(); }
        }, "beam, BEAM, Beam, bm, BM, Bm");

        addLocalUnit(new Unit("pixel", Double.NaN) {            
            /**
             * 
             */
            private static final long serialVersionUID = -647302245323576100L;

            @Override
            public double value() { return getGrid().getPixelArea(); }
        }, "pixel, PIXEL, Pixel, pixels, PIXELS, Pixels, pxl, PXL, Pxl");   
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ properties.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof Map2D)) return false;

        Map2D map = (Map2D) o;
        if(!Util.equals(properties, map.properties)) return false;

        return super.equals(o);
    }

    protected MapProperties getPropertiesInstance() { return new MapProperties(); }



    @Override
    public Map2D clone() {
        Map2D clone = (Map2D) super.clone();
        clone.reuseIndex = new Vector2D();  
        // Replace the clone's list of proprietary units with it own...
        clone.addProprietaryUnits();
        return clone;
    }

    @Override
    public Map2D copy() { return copy(true); }

    @Override
    public Map2D copy(boolean withContent) {
        Map2D copy = clone();

        if(properties != null) copy.properties = properties.copy();   

        // Units might have proprietary components, which aren't easily copied over.
        // Hence, the safest is to re-construct the units of the copy from scratch
        // based on the specification.
        copy.setUnit(getUnit().name());

        if(getImage() != null) copy.setImage(getImage().copy(withContent));
        if(getFlags() != null) copy.setFlags(getFlags().copy(withContent));
        
        return copy;
    }


    public void resetProcessing() {
        getProperties().resetProcessing();
    }

    public final void renew() {
        resetProcessing();
        clear();
    }

    public boolean isConsistent() {
        if(getImage() == null) return false;
        if(getFlags() == null) return false;
        if(properties == null) return false;
        // TODO size check...
        return true;
    }

    public String diagnoseInconsistency() {
        if(getImage() == null) return "null image";
        if(getFlags() == null) return "null flags";
        if(properties == null) return "null properties";
        // TODO size check...
        return null;        
    }


    @Override
    public final Image2D getImage() { return (Image2D) getBasis(); } 

    public void setImage(Image2D image) {
        if(image == null) image = Image2D.createType(getElementType());
        setBasis(image); 
        claim(image);
    } 

    protected void claim(Data2D image) {
        image.setUnit(getUnit());
        image.setParallel(getParallel());
        image.setExecutor(getExecutor());
        image.validate();
    }

    @Override
    public void setFlags(Flag2D flags) { 
        super.setFlags(flags);
        flags.setParallel(getParallel());
        flags.setExecutor(getExecutor());
    }


    public MapProperties getProperties() { return properties; }

    public final Grid2D<?> getGrid() { return properties.getGrid(); }

    public void setGrid(Grid2D<?> grid) { properties.setGrid(grid); }


    public final Projection2D<?> getProjection() { return getGrid().getProjection(); }

    public final void setProjection(Projection2D<?> projection) { ((Grid2D) getGrid()).setProjection(projection); }


    public final Coordinate2D getReference() { return getGrid().getReference(); }

    public final void setReference(Coordinate2D coords) { ((Grid2D) getGrid()).setReference(coords); } 


    public final Vector2D getResolution() { return getGrid().getResolution(); }

    public final void setResolution(Vector2D delta) { getGrid().setResolution(delta); }

    public final void setResolution(double dx, double dy) { getGrid().setResolution(dx, dy); }


    public final Vector2D getReferenceIndex() { return getGrid().getReferenceIndex(); }

    public final void setReferenceIndex(Vector2D v) { getGrid().setReferenceIndex(v); }



    public final Gaussian2D getSmoothing() { return properties.getSmoothing(); }

    public final double getBeamArea() { return properties.getImageBeamArea(); }


    public Class<? extends Coordinate2D> getCoordinateClass() { return getGrid().getReference().getClass(); }


    @Override
    public String toString(int i, int j) {
        return super.toString(i,j) + " flag=0x" + Long.toHexString(getFlags().get(i,j));
    }

    @Override
    public void setSize(int sizeX, int sizeY) { 
        getImage().setSize(sizeX, sizeY); 
        getFlags().setSize(sizeX, sizeY);
        initFlags();
    }

    @Override
    public void destroy() {
        super.destroy();
        getImage().destroy();     
    }


    public void noData() {
        new Fork<Void>() {
            @Override
            protected void process(int i, int j) { discard(i, j); }
        }.process();    
    }


    public double countBeams() { return getArea() / properties.getImageBeamArea(); }


    public double getArea() { return countPoints() * getGrid().getPixelArea(); }

    // In 1-D at least 3 points per beam are needed to separate a positioned point
    // source from an extended source...
    // Similarly 9 points per beam are necessary for 2-D...
    public double countIndependentPoints(double area) {
        double smoothArea = getSmoothing().getArea();
        double filterFWHM = properties.getFilterFWHM();
        double filterArea = Gaussian2D.areaFactor * filterFWHM * filterFWHM;
        double beamArea = properties.getImageBeamArea();

        // Account for the filtering correction.
        double eta = 1.0;
        if(Double.isNaN(filterFWHM) && filterFWHM > 0.0) eta -= smoothArea / filterArea;
        double iPointsPerBeam = eta * Math.min(9.0, smoothArea / getGrid().getPixelArea());

        return Math.ceil((1.0 + area/beamArea) * iPointsPerBeam);
    }



    public Number nearestToOffset(double dx, double dy) {
        synchronized(reuseIndex) {
            reuseIndex.set(dx, dy);
            return nearestToOffset(reuseIndex);
        }
    }

    public Number nearestToOffset(Vector2D offset) {
        synchronized(reuseIndex) {
            getGrid().offsetToIndex(offset, reuseIndex);
            return get((int)Math.round(reuseIndex.x()), (int)Math.round(reuseIndex.y()));
        }
    }


    public double getPointsPerSmoothingBeam() {
        return Math.max(1.0, properties.getSmoothing().getArea() / getGrid().getPixelArea());
    }



    public void crop(int imin, int jmin, int imax, int jmax) {
        getImage().crop(imin, jmin, imax, jmax);
        getFlags().crop(imin, jmin, imax, jmax);

        Vector2D refIndex = getGrid().getReferenceIndex();

        refIndex.subtractX(imin);
        refIndex.subtractY(jmin);
    }

    public final void crop(double dXmin, double dYmin, double dXmax, double dYmax) {
        if(dXmin > dXmax) { double temp = dXmin; dXmin = dXmax; dXmax=temp; }
        if(dYmin > dYmax) { double temp = dYmin; dYmin = dYmax; dYmax=temp; }

        Unit sizeUnit = properties.getDisplayGridUnit();

        if(isVerbose()) Util.info(this, "Will crop to " + ((dXmax - dXmin)/sizeUnit.value()) + "x" + ((dYmax - dYmin)/sizeUnit.value()) + " " + sizeUnit.name() + ".");

        Index2D c1 = getIndexOfOffset(new Vector2D(dXmin, dYmin));
        Index2D c2 = getIndexOfOffset(new Vector2D(dXmax, dYmax));

        crop(c1.i(), c1.j(), c2.i(), c2.j());
    }


    public final void autoCrop() {
        int[] hRange = getXIndexRange();
        if(hRange == null) return; 

        int[] vRange = getYIndexRange();
        if(vRange == null) return;

        if(isVerbose()) Util.info(this, "Auto-cropping: " + (hRange[1] - hRange[0] + 1) + "x" + (vRange[1] - vRange[0] + 1));
        this.crop(hRange[0], vRange[0], hRange[1], vRange[1]);
    }




    public final Index2D getIndexOfOffset(final Vector2D offset) {
        Index2D index = new Index2D();
        getGrid().getIndex(offset, index);
        return index;
    }


    public final void smoothTo(double FWHM) {
        smoothTo(new Gaussian2D(FWHM));
    }

    public final void smoothTo(Gaussian2D psf) {
        if(getSmoothing().isEncompassing(psf)) return;
        psf.deconvolveWith(getSmoothing());
        smooth(psf);
    }

    public final void smooth(double FWHM) {
        smooth(new Gaussian2D(FWHM));
    }

    public final void smooth(Gaussian2D psf) { 
        int stepX = (int)Math.ceil(psf.extentInX()/(5.0 * getGrid().pixelSizeX()));
        int stepY = (int)Math.ceil(psf.extentInY()/(5.0 * getGrid().pixelSizeY()));

        fastSmooth(psf.getBeam(getGrid()), stepX, stepY);
    }


    @Override
    public void smooth(Referenced2D beam) {
        super.smooth(beam);
        properties.addSmoothing(Gaussian2D.getEquivalent(beam, getGrid().getResolution()));
    }

    @Override
    public void fastSmooth(Referenced2D beam, int stepX, int stepY) {
        super.fastSmooth(beam, stepX, stepY);
        properties.addSmoothing(Gaussian2D.getEquivalent(beam, getGrid().getResolution()));
    }



    public void filterAbove(double FWHM) {
        filterAbove(FWHM, null);
    }

    public void filterAbove(double FWHM, final Validating2D validator) {
        final Map2D extended = copy(true);

        // Null out the points that are to be skipped over by the validator...
        if(validator != null) extended.new Fork<Void>() {
            @Override
            protected void process(int i, int j) {
                if(!validator.isValid(i, j)) extended.clear(i,  j);
            }
        }.process();

        extended.smoothTo(FWHM);

        new Fork<Void>() {
            @Override
            protected void process(int i, int j) {
                double X = extended.get(i, j).doubleValue();
                if(!Double.isNaN(X)) add(i, j, -X);
            }
        }.process();

        properties.updateFiltering(FWHM);
    }

    public void fftFilterAbove(double FWHM) {
        fftFilterAbove(FWHM, null);
    }

    public void fftFilterAbove(double FWHM, final Values2D weight) {
        fftFilterAbove(FWHM, null, weight);
    }

    public void fftFilterAbove(double FWHM, final Validating2D validator) {
        fftFilterAbove(FWHM, validator, null);
    }

    public final void fftFilterAbove(double FWHM, final Validating2D validator, final Values2D weight) {
        // Oversized transformer to reduce wrapping effects by copious padding...
        final int nx = ExtraMath.pow2ceil(sizeX()<<1);
        final int ny = ExtraMath.pow2ceil(sizeY()<<1);
 
        final double[][] transformer = new double[nx][ny+2];

        AveragingFork weightedCalc = new AveragingFork() {
            private double sumw = 0.0;
            private int n = 0;
            @Override
            protected void process(final int i, final int j) {
                if(!isValid(i, j)) return;  
                if(validator != null) if(!validator.isValid(i, j)) return;

                final double w = (weight == null) ? 1.0 : weight.get(i,  j).doubleValue();
          
                transformer[i][j] = w * get(i, j).doubleValue();
                sumw += w*w;
                n++;
            }
            @Override
            public WeightedPoint getLocalResult() { return new WeightedPoint(sumw, n); }
        };
        weightedCalc.process();

        final double avew = Math.sqrt(weightedCalc.getResult().value());
        if(avew <= 0.0) return;

        final MultiFFT fft = new MultiFFT(this);
        fft.setParallel(getParallel());
        fft.real2Amplitude(transformer);

        // sigma_x sigma_w = 1
        // FWHM_x sigma_w = 2.35
        // FWHM_x * 2Pi sigma_f = 2.35
        // sigma_f = 2.35/2Pi * 1.0/FWHM_x
        // delta_f = 1.0/(Nx * delta_x);
        // sigma_nf = sigma_f / delta_x = 2.35 * Nx * delta_x / (2Pi * FWHM_x)

        final double sigmax = Constant.sigmasInFWHM * nx * getGrid().pixelSizeX() / (Constant.twoPi * FWHM);
        final double sigmay = Constant.sigmasInFWHM * ny * getGrid().pixelSizeY() / (Constant.twoPi * FWHM);

        final double ax = -0.5/(sigmax*sigmax);
        final double ay = -0.5/(sigmay*sigmay);
   
        for(int fx=nx; --fx >= 0; ) {
            final double axfx2 = ax*fx*fx;
            final double[] r = transformer[fx];            // The positive frequencies
        
            // The unrolled real spectrum...
            for(int fy=0; fy <= ny; fy += 2) {
                // The transfer function (Gaussian taper)
                final double A = Math.exp(axfx2 + ay*fy*fy);

                r[fy] *= A;
                r[fy+1] *= A;
            }
        }

        fft.amplitude2Real(transformer);
        
        final double norm = -1.0 / avew;
        new Fork<Void>() {
            @Override
            public void process(int i, int j) {
                add(i, j, norm * transformer[i][j]);
            }
        }.process();

        properties.updateFiltering(FWHM);
    }



    public Transforming<Vector2D> getIndexTransformTo(Map2D map) {
        // See if we can use a faster/simpler Cartesian transform for re-mapping...
        if(map.getProjection().equals(getProjection())) {
            // If both a Cartesian projections, then yes...
            if(getProjection() instanceof DefaultProjection2D)
                return new CartesianGridTransform2D(getGrid(), map.getGrid());

            // Or, if not re-projecting (i.e. using the same projections and
            // referenced to the same position...
            if(map.getGrid().getReference().equals(getGrid().getReference())) 
                return new CartesianGridTransform2D(getGrid(), map.getGrid());
        }

        // Otherwise, go with the full-blown reprojection...
        return new ProjectedIndexTransform2D(getGrid(), map.getGrid());
    }

    public final Gaussian2D getAntialiasingBeamFor(Map2D map) {
        // TODO incorporate rotation at reference...

        Gaussian2D mapSmoothing = map.getSmoothing();
        Gaussian2D pixelization = properties.getPixelSmoothing();
        if(mapSmoothing == null) return pixelization;
        else if(mapSmoothing.isEncompassing(pixelization)) return null;

        Gaussian2D antialias = pixelization.copy();
        antialias.deconvolveWith(mapSmoothing);
        return antialias;       
    }

    public final Referenced2D getAntialiasingBeamImageFor(Map2D map) {
        Gaussian2D antialias = getAntialiasingBeamFor(map);
        return antialias == null ? null : antialias.getBeam(getGrid()); 
    }


    public void resampleFrom(Map2D map) {
        resampleFrom(map, null);
    }

    public void resampleFrom(Map2D map, Values2D weight) {
        Referenced2D beam = getAntialiasingBeamImageFor(map);
        resampleFrom(map.getImage(), map.getIndexTransformTo(this), beam, weight);       
        properties.copyProcessingFrom(map.getProperties());
    }

    public void resample(double newres) {
        resample(new Vector2D(newres, newres));
    }

    public void resample(Vector2D newres) {
        Map2D clone = clone();

        Vector2D resolution = getResolution();
        setSize((int) Math.ceil(sizeX() * resolution.x() / newres.x()), (int) Math.ceil(sizeY() * resolution.y() / newres.y()));

        setGrid(getGrid().forResolution(resolution));

        resampleFrom(clone);        
    }

    public DataPoint getAsymmetry(final Vector2D centerIndex, final double angle, final Range radialRange) {    
        return super.getAsymmetry(getGrid(), centerIndex, angle, radialRange);
    }

    public Asymmetry2D getAsymmetry2D(Vector2D centerIndex, double angle, Range radialRange) {
        return super.getAsymmetry2D(getGrid(), centerIndex, angle, radialRange);
    }


    @Override
    public Object getTableEntry(String name) {
        if(name.equals("beams")) return countBeams();
        else if(name.equals("min")) return getMin().doubleValue() / getUnit().value();
        else if(name.equals("max")) return getMax().doubleValue() / getUnit().value();  
        else if(name.equals("unit")) return getUnit().name();
        else if(name.equals("mean")) return getMean().value() / getUnit().value();
        else if(name.equals("median")) return getMedian().value() / getUnit().value();
        else if(name.equals("rms")) return getRMS(true) / getUnit().value();
        else return properties.getTableEntry(name);
    }


    @Override
    public Fits createFits(Class<? extends Number> dataType) throws FitsException {
        FitsFactory.setLongStringsEnabled(true);
        FitsFactory.setUseHierarch(true);

        Fits fits = new Fits(); 
        ArrayList<BasicHDU<?>> hdus = getHDUs(dataType);

        for(int i=0; i<hdus.size(); i++) fits.addHDU(hdus.get(i));
        return fits;
    }


    public boolean read(BasicHDU<?>[] HDUs) throws Exception {

        for(int i=0; i<HDUs.length; i++) if(HDUs[i] instanceof ImageHDU) {
            readData((ImageHDU) HDUs[i]);
            return true;          
        }

        return false;
    }

    protected void readData(ImageHDU hdu) throws Exception {
        // Read the properties first as they may provide new dynamic units...
        // like 'pixel', 'beam'...
        properties.resetProcessing();
        properties.parseHeader(hdu.getHeader());    

        getImage().read(hdu, getLocalUnits());

        if(!conformsTo(getFlags().sizeX(), getFlags().sizeY())) 
            getFlags().setSize(sizeX(), sizeY());
        else getFlags().clear();

        validate();
    }

    public ArrayList<BasicHDU<?>> getHDUs(Class<? extends Number> dataType) throws FitsException {
        ArrayList<BasicHDU<?>> hdus = new ArrayList<BasicHDU<?>>();

        ImageHDU hdu = createHDU(dataType);
        editHeader(hdu.getHeader());
        hdus.add(hdu);

        return hdus;
    }


    @Override
    protected void parseHeader(Header header) {
        properties.parseHeader(header);
        super.parseHeader(header);
    }


    @Override
    protected void editHeader(Header header) throws HeaderCardException {  
        properties.editHeader(header);   
        super.editHeader(header);
    }


    @Override
    public String getInfo() {      
        Grid2D<?> grid = getGrid();

        Unit sizeUnit = properties.getDisplayGridUnit();

        String sizeInfo = super.getInfo() + " (" +
                Util.f1.format(sizeX() * grid.pixelSizeX() / sizeUnit.value()) + " x " + 
                Util.f1.format(sizeY() * grid.pixelSizeY() / sizeUnit.value()) + " " + sizeUnit.name() + ").";

        String info = properties.brief(sizeInfo);

        return info;
    }

    public void filterBeamCorrect() {
        filterCorrect(getProperties().getUnderlyingBeam().getCircularEquivalentFWHM());
    }

    public void filterCorrect(double underlyingFWHM) {
        filterCorrect(underlyingFWHM, this);
    }

    public void filterCorrect(double underlyingFWHM, Values2D reference) {
        double blankingValue = getProperties().getFilterBlanking();
        filterCorrectValidated(underlyingFWHM, new RangeRestricted2D(reference, new Range(-blankingValue, blankingValue)));
    }

    public void filterCorrectValidated(double underlyingFWHM, final Validating2D validator) {  
        // Undo prior corrections if necessary
        if(!properties.isCorrected()) { 
            if(underlyingFWHM == properties.getCorrectingFWHM()) return;
            undoFilterCorrectBy(validator);
        }
        final double filterC = properties.getFilterCorrectionFactor(underlyingFWHM);

        new Fork<Void>() {
            @Override
            protected void process(int i, int j) {
                if(validator.isValid(i, j)) scale(i, j, filterC);
            }
        }.process();

        properties.setCorrectingFWHM(underlyingFWHM);
    }


    public void undoFilterCorrect() {
        undoFilterCorrect(this);
    }

    public void undoFilterCorrect(Data2D reference) {
        double blankingValue = getProperties().getFilterBlanking();
        undoFilterCorrectBy(new RangeRestricted2D(reference, new Range(-blankingValue, blankingValue)));
    }


    public void undoFilterCorrectBy(final Validating2D validator) {
        if(!properties.isCorrected()) return;

        final double iFilterC = 1.0 / properties.getFilterCorrectionFactor(properties.getCorrectingFWHM());

        new Fork<Void>() {
            @Override
            protected void process(int i, int j) {
                if(validator.isValid(i,  j)) scale(i, j, iFilterC);
            }
        }.process();

        properties.setCorrectingFWHM(Double.NaN);
    }

    public static Map2D read(Fits fits, int hduIndex) throws Exception {
        return read(fits, hduIndex, Flag2D.TYPE_INT);
    }

    public static Map2D read(Fits fits, int hduIndex, int flagType) throws Exception {
        return read(fits, hduIndex, null, flagType);
    }

    public static Map2D read(Fits fits, int hduIndex, Hashtable<String, Unit> extraUnits, int flagType) throws Exception {
        Image2D image = Image2D.read(fits, hduIndex, extraUnits);
        Map2D map = new Map2D(image, flagType);
        map.parseHeader(fits.getHDU(hduIndex).getHeader());
        return map;
    }


}
