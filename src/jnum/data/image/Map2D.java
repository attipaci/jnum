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
import jnum.ExtraMath;
import jnum.Unit;
import jnum.Util;
import jnum.data.DataPoint;
import jnum.data.Referenced;
import jnum.data.RegularData;
import jnum.data.Resizable;
import jnum.data.Transforming;
import jnum.data.WeightedPoint;
import jnum.data.image.overlay.Flagged2D;
import jnum.data.image.overlay.RangeRestricted2D;
import jnum.data.image.overlay.Referenced2D;
import jnum.data.image.transform.CartesianGridTransform2D;
import jnum.data.image.transform.ProjectedIndexTransform2D;
import jnum.fft.MultiFFT;
import jnum.fits.FitsProperties;
import jnum.fits.FitsToolkit;
import jnum.math.Coordinate2D;
import jnum.math.IntRange;
import jnum.math.Range;
import jnum.math.Vector2D;
import jnum.projection.DefaultProjection2D;
import jnum.projection.Projection2D;
import jnum.util.HashCode;
import nom.tam.fits.BasicHDU;
import nom.tam.fits.Fits;
import nom.tam.fits.FitsException;
import nom.tam.fits.FitsFactory;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;
import nom.tam.fits.ImageHDU;
import nom.tam.util.Cursor;



/**
 * 2D image base class with FITS I/O, simple units, and 2D grid...
 * 
 * 
 * @author pumukli
 *
 * @param <ImageType>
 * @param <ElementType>
 */
public class Map2D extends Flagged2D implements Resizable<Index2D>, Serializable, Referenced<Index2D, Vector2D> {
    /**
     * 
     */
    private static final long serialVersionUID = -2684430958862706671L;
    

    private transient Vector2D reuseIndex = new Vector2D();



    private FitsProperties fitsProperties;
    
    
    private Grid2D<?> grid = new FlatGrid2D();  
    
    private Unit displayGridUnit;
    
   
    private Gaussian2D underlyingBeam;
  
    private Gaussian2D smoothingBeam;
    
     
    private double filterFWHM = Double.NaN;
  
    private double correctingFWHM = Double.NaN; 
    
    
    private double filterBlanking = Double.POSITIVE_INFINITY;
    
    


    private Map2D() {
        reuseIndex = new Vector2D();
        fitsProperties = new FitsProperties();
        setGrid(new FlatGrid2D());        
    }
    
    @Override
    public void setDefaultUnit() {
        addProprietaryUnits();   
        super.setDefaultUnit();
    }

    public Map2D(Image2D data, int flagType) {
        this();
        setImage(data);
        createFlags(flagType);
    }

    public Map2D(Class<? extends Number> dataType, int flagType) {
        this(Image2D.createType(dataType), flagType);
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
            public double value() { return getImageBeamArea(); }
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
        int hash = super.hashCode() ^ HashCode.from(filterFWHM) ^ HashCode.from(correctingFWHM) ^ HashCode.from(filterBlanking);
        if(fitsProperties != null) hash ^= fitsProperties.hashCode();
        if(grid != null) hash ^= grid.hashCode();
        if(displayGridUnit != null) hash ^= displayGridUnit.hashCode();
        if(underlyingBeam != null) hash ^= underlyingBeam.hashCode();
        if(smoothingBeam != null) hash ^= smoothingBeam.hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof Map2D)) return false;

        Map2D map = (Map2D) o;
        if(!Util.equals(fitsProperties, map.fitsProperties)) return false;

        if(!Util.equals(filterFWHM, map.filterFWHM)) return false;
        if(!Util.equals(correctingFWHM, map.correctingFWHM)) return false;
        if(!Util.equals(filterBlanking, map.filterBlanking)) return false;
        
        if(!Util.equals(grid, map.grid)) return false;
        if(!Util.equals(displayGridUnit, map.displayGridUnit)) return false;
        if(!Util.equals(underlyingBeam, map.underlyingBeam)) return false;
        if(!Util.equals(smoothingBeam, map.smoothingBeam)) return false;
        
        return super.equals(o);
    }

    @Override
    public Map2D clone() {
        Map2D clone = (Map2D) super.clone();
        clone.reuseIndex = new Vector2D();  
        return clone;
    }

    @Override
    public Map2D copy() { return copy(true); }

    @Override
    public Map2D copy(boolean withContent) {
        Map2D copy = (Map2D) super.copy(withContent);
        
        if(fitsProperties != null) copy.fitsProperties = fitsProperties.copy();   
    
        if(underlyingBeam != null) copy.underlyingBeam = underlyingBeam.copy();
        if(smoothingBeam != null) copy.smoothingBeam = smoothingBeam.copy();   
       
        if(grid != null) copy.grid = grid.copy();
        if(displayGridUnit != null) copy.displayGridUnit = displayGridUnit.copy();

        
        // Replace the clone's list of proprietary units with it own...
        copy.addProprietaryUnits();
        
        // Units might have proprietary components, which aren't easily copied over.
        // Hence, the safest is to re-construct the units of the copy from the specification.
        copy.setUnit(getUnit().name());
  
        return copy;
    }

    public void copyProcessingFrom(Map2D other) {
        underlyingBeam = other.underlyingBeam == null ? null : other.underlyingBeam.copy();
        smoothingBeam = other.smoothingBeam == null ? null : other.smoothingBeam.copy();
          
        filterFWHM = other.filterFWHM;
        filterBlanking = other.filterBlanking;
        
        correctingFWHM = other.correctingFWHM;
    }
    
    public void copyPropertiesFrom(Map2D template) {
        fitsProperties = template.fitsProperties == null ? null : template.fitsProperties.copy();
    
        copyProcessingFrom(template);
        
        filterFWHM = template.filterFWHM;
        correctingFWHM = template.correctingFWHM;
        filterBlanking = template.filterBlanking;
        
        if(template.grid != null) grid = template.grid.copy();
        if(template.displayGridUnit != null) displayGridUnit = template.displayGridUnit.copy();
       
        if(template.underlyingBeam != null) underlyingBeam = template.underlyingBeam.copy();
        if(template.smoothingBeam != null) smoothingBeam = template.smoothingBeam.copy();
    }
    
    public FitsProperties getFitsProperties() { return fitsProperties; }
    
    public void setFitsProperties(FitsProperties p) { this.fitsProperties = p; }

    public void resetProcessing() {
        if(fitsProperties != null) fitsProperties.resetProcessing();
        resetSmoothing();
        resetFiltering();
    }
    
    
    public void resetSmoothing() {
        setPixelSmoothing(); 
    }
    
    public void resetFiltering() {
        setFiltering(Double.NaN);
        setCorrectingFWHM(Double.NaN);
        setFilterBlanking(Double.NaN);
    }

    public final void renew() {
        resetProcessing();
        clear();
    }
    
    
    public final Grid2D<?> getGrid() { return grid; }
    
    public void setGrid(Grid2D<?> grid) { 
        // Undo the prior pixel smoothing, if any...
        if(smoothingBeam != null) if(this.grid != null) smoothingBeam.deconvolveWith(getPixelSmoothing());     
        
        this.grid = grid; 
        
        // Apply new pixel smoothing...
        if(smoothingBeam == null) smoothingBeam = getPixelSmoothing();
        else smoothingBeam.encompass(getPixelSmoothing());      
    }

    
    public final Vector2D getResolution() { return getGrid().getResolution(); }

    public final void setResolution(Vector2D delta) { getGrid().setResolution(delta); }
    
    public void setResolution(double dx, double dy) { 
        // Undo the prior pixel smoothing, if any...
        if(smoothingBeam != null) if(this.grid != null) smoothingBeam.deconvolveWith(getPixelSmoothing());      
        
        getGrid().setResolution(dx, dy);
        
        // Apply new pixel smoothing...
        if(smoothingBeam == null) smoothingBeam = getPixelSmoothing();
        else smoothingBeam.encompass(getPixelSmoothing());
    }
    
        
    public final double getPixelArea() { return getGrid().getPixelArea(); } 

    

    public Class<? extends Coordinate2D> getCoordinateClass() { return getGrid().getReference().getClass(); }

    
    public final Coordinate2D getReference() { return getGrid().getReference(); }
    
    
    public final void setReference(Coordinate2D coords) { ((Grid2D) getGrid()).setReference(coords); } 



    @Override
    public final Vector2D getReferenceIndex() { return getGrid().getReferenceIndex(); }

    @Override
    public final void setReferenceIndex(Vector2D v) { getGrid().setReferenceIndex(v); }

    

    public final Projection2D<?> getProjection() { return getGrid().getProjection(); }
    
    public final void setProjection(Projection2D<?> projection) { ((Grid2D) getGrid()).setProjection(projection); }

    
    
 
    public final Gaussian2D getUnderlyingBeam() { return underlyingBeam; }
    

    public void setUnderlyingBeam(Gaussian2D psf) { 
        underlyingBeam = psf; 
    }
    

    public void setUnderlyingBeam(double fwhm) { 
        underlyingBeam = new Gaussian2D(fwhm);     
    }
    
    

    public Gaussian2D getPixelSmoothing() {
        Vector2D resolution = grid.getResolution();      
        return new Gaussian2D(resolution.x() / Gaussian2D.fwhm2size, resolution.y() / Gaussian2D.fwhm2size, 0.0);
    }
    
    
    public void setPixelSmoothing() {
        smoothingBeam.copy(getPixelSmoothing());
    }
    
    
    public final Gaussian2D getSmoothingBeam() { return smoothingBeam; }
    

    public void setSmoothingBeam(Gaussian2D psf) { 
        smoothingBeam = psf; 
    }
    

    public void setSmoothing(double fwhm) { 
        smoothingBeam = new Gaussian2D(fwhm);
    }
    
    public final void addSmoothing(double fwhm) {
        addSmoothing(new Gaussian2D(fwhm));
    }
    
    public void addSmoothing(Gaussian2D psf) {
        if(smoothingBeam == null) smoothingBeam = psf;
        else smoothingBeam.convolveWith(psf);
    }

    
   
    

    public Gaussian2D getImageBeam() {
        if(underlyingBeam == null) return smoothingBeam;
        if(smoothingBeam == null) return underlyingBeam;
        
        Gaussian2D beam = new Gaussian2D();
        beam.copy(underlyingBeam);
        beam.convolveWith(smoothingBeam);
        
        return beam;
    }
    
    
    public double getImageBeamArea() {
        return underlyingBeam.getArea() + smoothingBeam.getArea();
    }
    

    public double getFilterCorrectionFactor(double underlyingFWHM) {
        if(Double.isNaN(filterFWHM)) return 1.0;
        return 1.0 / (1.0 - (underlyingBeam.getArea() + smoothingBeam.getArea()) / (underlyingBeam.getArea() + getFilterArea()));
    }
    
 
    public boolean isFiltered() { return Double.isNaN(filterFWHM); }
    
    public final double getFilterFWHM() { return filterFWHM; }
    
    public final double getFilterArea() { return Gaussian2D.areaFactor * filterFWHM * filterFWHM; }
    
    public void setFiltering(double FWHM) { this.filterFWHM = FWHM; }
 
    public void updateFiltering(double FWHM) {
        if(Double.isNaN(filterFWHM)) filterFWHM = FWHM;
        else if(!Double.isNaN(FWHM)) filterFWHM = Math.min(filterFWHM, FWHM);       
    }
    
    
    public boolean isCorrected() { return Double.isNaN(correctingFWHM); }
    
    public double getCorrectingFWHM() { return correctingFWHM; }
    
    public void setCorrectingFWHM(double value) { this.correctingFWHM = value; }
 
    
    public boolean isFilterBlanked() { return !Double.isInfinite(filterBlanking); }
    
    public double getFilterBlanking() { return filterBlanking; }
    
    public void setFilterBlanking(double value) { this.filterBlanking = value; } 
    
  
    public void setDisplayGridUnit(Unit u) {
        displayGridUnit = u;
    }
    

    public final Unit getDisplayGridUnit() {
        if(displayGridUnit != null) return displayGridUnit;
        return getDefaultGridUnit();
    }
    

    public Unit getDefaultGridUnit() {
        Grid2D<?> grid = getGrid();
        if(grid == null) return Unit.get("pixel");
        return grid.getDefaultUnit();
    }
    
    
    public void parseCoordinateInfo(Header header, String alt) throws InstantiationException, IllegalAccessException {
        setGrid(Grid2D.fromHeader(header, alt));
    }
    
    public void editCoordinateInfo(Header header) throws HeaderCardException {
        if(grid != null) grid.editHeader(header);
    }
    
    
    @Override
    public void parseHeader(Header header) {               
        try { parseCoordinateInfo(header, ""); }
        catch(Exception e) { Util.warning(this, e); }
          
        parseCorrectedBeam(header);
        parseSmoothingBeam(header);
        parseFilterBeam(header);
        
        // The underlying beam must be parsed after the smoothing because it may rely on the smoothing
        // value in some cases...
        parseUnderlyingBeam(header);
               
        // The image data unit must be parsed after the instrument beam (underlying + smoothing)
        // and the coordinate grid are established as it may contain 'beam' or 'pixel' type units.
        super.parseHeader(header);       
    }

    public void parseCorrectedBeam(Header header) {
        // Use old CORRETN or new CBMAJ/CBMIN
        if(header.containsKey(correctedBeamFitsID + "BMAJ")) {
            Gaussian2D correctingBeam = new Gaussian2D();
            correctingBeam.parseHeader(header, correctedBeamFitsID, getDefaultGridUnit().value());
            correctingFWHM = correctingBeam.getCircularEquivalentFWHM();        
        }
        else correctingFWHM = FitsToolkit.getCommentedUnitValue(header, "CORRECTN", Double.NaN, getDisplayGridUnit().value());
    
    }
    
    public void parseSmoothingBeam(Header header) {   
        // Use old SMOOTH or new SBMAJ/SBMIN
        if(header.containsKey(smoothingBeamFitsID + "BMAJ")) 
            smoothingBeam.parseHeader(header, smoothingBeamFitsID, getDefaultGridUnit().value());  
        else {
            double smoothFWHM = FitsToolkit.getCommentedUnitValue(header, "SMOOTH", Double.NaN, getDisplayGridUnit().value());
            smoothingBeam.set(smoothFWHM);
        }
        
        double pixelSmoothing = Math.sqrt(getGrid().getPixelArea() / Gaussian2D.areaFactor);
        smoothingBeam.encompass(new Gaussian2D(pixelSmoothing));        
    }
    
    public void parseFilterBeam(Header header) {
        // Use old EXTFILTR or new XBMAJ, XBMIN
        if(header.containsKey(filterBeamFitsID + "BMAJ")) {
            Gaussian2D extFilterBeam = new Gaussian2D();
            extFilterBeam.parseHeader(header, filterBeamFitsID, getDefaultGridUnit().value());
            filterFWHM = extFilterBeam.getCircularEquivalentFWHM();
        }
        filterFWHM = FitsToolkit.getCommentedUnitValue(header, "EXTFLTR", Double.NaN, getDisplayGridUnit().value());
    }
    
    public void parseUnderlyingBeam(Header header) {
        // Use new IBMAJ/IBMIN if available
        // Otherwise calculate it based on BMAJ, BMIN
        // Else, use old BEAM, or calculate based on old RESOLUTN
        if(header.containsKey(underlyingBeamFitsID + "BMAJ")) 
            underlyingBeam.parseHeader(header, underlyingBeamFitsID, getDefaultGridUnit().value());
        else if(header.containsKey("BEAM")) 
            underlyingBeam.set(FitsToolkit.getCommentedUnitValue(header, "BEAM", Double.NaN, getDisplayGridUnit().value()));
        else if(header.containsKey("BMAJ")) {
            underlyingBeam.parseHeader(header, "", getDefaultGridUnit().value());
            underlyingBeam.deconvolveWith(smoothingBeam);
        }
        else if(header.containsKey("RESOLUTN")) {
            double resolution = FitsToolkit.getCommentedUnitValue(header, "RESOLUTN", Double.NaN, getDisplayGridUnit().value());
            underlyingBeam.set(resolution > smoothingBeam.getMajorFWHM() ? Math.sqrt(resolution * resolution - smoothingBeam.getMajorFWHM() * smoothingBeam.getMinorFWHM()) : 0.0);
        }
        else underlyingBeam.set(0.0);
    
    }
        
    @Override
    public void editHeader(Header header) throws HeaderCardException {   
        editCoordinateInfo(header);
        
        Gaussian2D psf = getImageBeam();
        Unit fitsUnit = getDefaultGridUnit();
        Unit displayUnit = getDisplayGridUnit();
        
        Cursor<String, HeaderCard> c = FitsToolkit.endOf(header);
        
        if(psf != null) {
            psf.editHeader(header, "image", "", fitsUnit);
            if(psf.isCircular()) c.add(new HeaderCard("RESOLUTN", psf.getCircularEquivalentFWHM() / displayUnit.value(), "{Deprecated} Effective image FWHM (" + displayUnit.name() + ").")); 
        }
            
        if(underlyingBeam != null) underlyingBeam.editHeader(header, "instrument", underlyingBeamFitsID, fitsUnit);
        if(smoothingBeam != null) {
            smoothingBeam.editHeader(header, "smoothing", smoothingBeamFitsID, fitsUnit);
            if(smoothingBeam.isCircular()) c.add(new HeaderCard("SMOOTH", smoothingBeam.getCircularEquivalentFWHM() / displayUnit.value(), "{Deprecated} FWHM (" + displayUnit.name() + ") smoothing.")); 
        }
            
        // TODO convert extended filter and corrections to proper Gaussian beams...
        if(!Double.isNaN(filterFWHM)) {
            Gaussian2D filterBeam = new Gaussian2D(filterFWHM);
            filterBeam.editHeader(header, "Extended Structure Filter", "X", fitsUnit);
        }
        
        if(!Double.isNaN(correctingFWHM)) {
            Gaussian2D correctionBeam = new Gaussian2D(correctingFWHM);
            correctionBeam.editHeader(header, "Peak Corrected", "C", fitsUnit);
        }
            
        c.add(new HeaderCard("SMTHRMS", true, "Is the Noise (RMS) image smoothed?"));
        
        super.editHeader(header);
        
        
    }
     

    public void mergeProperties(Map2D other) {
        if(smoothingBeam != null) {
            if(other.smoothingBeam != null) smoothingBeam.encompass(other.smoothingBeam);
        }
        else {
            if(other.smoothingBeam != null) smoothingBeam = other.smoothingBeam.copy();       
        }
        filterFWHM = Math.min(filterFWHM, other.filterFWHM);
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
    }

    @Override
    public void setFlags(Flag2D flags) { 
        super.setFlags(flags);
        flags.setParallel(getParallel());
        flags.setExecutor(getExecutor());
    }




    @Override
    public final void setSize(Index2D size) {
        setSize(size.i(), size.j());
    }
  
    public void setSize(int sizeX, int sizeY) { 
        // Create brand new images, so that setSize on a clone does not change the original...
        setImage(Image2D.createType(getElementType(), sizeX, sizeY));
        createFlags(getFlags().getType());
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


    public double countBeams() { return getArea() / getImageBeamArea(); }


    public double getArea() { return countPoints() * getGrid().getPixelArea(); }

    // In 1-D at least 3 points per beam are needed to separate a positioned point
    // source from an extended source...
    // Similarly 9 points per beam are necessary for 2-D...
    public double countIndependentPoints(double area) {
        double smoothArea = getSmoothingBeam().getArea();
        double filterFWHM = getFilterFWHM();
        double filterArea = Gaussian2D.areaFactor * filterFWHM * filterFWHM;
        double beamArea = getImageBeamArea();

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
        return Math.max(1.0, getSmoothingBeam().getArea() / getGrid().getPixelArea());
    }



    public synchronized void crop(Index2D from, Index2D to) {
        getImage().crop(from, to);
        getFlags().crop(from, to);

        Vector2D refIndex = getGrid().getReferenceIndex();

        refIndex.subtractX(from.i());
        refIndex.subtractY(from.j());
    }

    public final void crop(Vector2D from, Vector2D to) {
        if(from.x() > to.x()) { double temp = from.x(); from.setX(to.x()); to.setX(temp); }
        if(from.y() > to.y()) { double temp = from.y(); from.setY(to.y()); to.setY(temp); }

        Unit sizeUnit = getDisplayGridUnit();
        
        Vector2D d = Vector2D.differenceOf(to, from);

        if(isVerbose()) Util.info(this, "Will crop to " + d.x()/sizeUnit.value() + "x" + d.y()/sizeUnit.value() + " " + sizeUnit.name() + ".");

        Index2D c1 = getIndexOfOffset(from);
        Index2D c2 = getIndexOfOffset(to);

        crop(new Index2D(c1.i(), c1.j()), new Index2D(c2.i(), c2.j()));
    }


    public final void autoCrop() {
        IntRange x = getXIndexRange();
        if(x == null) return; 

        IntRange y = getYIndexRange();
        if(y == null) return;

        if(isVerbose()) Util.info(this, "Auto-cropping: " + (x.span() + 1) + "x" + (y.span() + 1));
        this.crop(new Index2D((int) x.min(), (int) y.min()), new Index2D((int) x.max(), (int) y.max()));
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
        if(getSmoothingBeam().isEncompassing(psf)) return;
        psf.deconvolveWith(getSmoothingBeam());
        smooth(psf);
    }

    public final void smooth(double FWHM) {
        smooth(new Gaussian2D(FWHM));
    }

    public final void smooth(Gaussian2D psf) { 
        Index2D step = new Index2D(
                (int)Math.ceil(psf.extentInX()/(5.0 * getGrid().pixelSizeX())),
                (int)Math.ceil(psf.extentInY()/(5.0 * getGrid().pixelSizeY()))
        );

        fastSmooth(psf.getBeam(getGrid()), step);
    }


    @Override
    public void smooth(RegularData<Index2D, Vector2D> beam, Vector2D refIndex) {
        super.smooth(beam, refIndex);
        addSmoothing(Gaussian2D.getEquivalent(beam, getGrid().getResolution()));
    }

    @Override
    public void fastSmooth(RegularData<Index2D, Vector2D> beam, Vector2D refIndex, Index2D step) {
        super.fastSmooth(beam, refIndex, step);
        addSmoothing(Gaussian2D.getEquivalent(beam, getGrid().getResolution()));
    }



    public void filterAbove(double FWHM) {
        filterAbove(FWHM, null);
    }
    
  
    public void filterAbove(double FWHM, final Validating2D validator) {
        final Map2D extended = copy(true);
        
        
        if(extended instanceof Observation2D) {
            extended.validate();    // Make sure zero weights are flagged...
            ((Observation2D) extended).isZeroWeightValid = true;
        }
        
        // Null out the points that are to be skipped over by the validator...
        if(validator != null) extended.new Fork<Void>() {
            @Override
            protected void process(int i, int j) {
                if(!isValid(i, j)) return;
                if(!validator.isValid(i, j)) {
                    extended.clear(i, j);
                    extended.unflag(i, j);
                }
            }
        }.process();
           
        extended.smoothTo(FWHM);
      
        final Image2D image = getImage();
        
        new Fork<Void>() {
            @Override
            protected void process(int i, int j) {
                if(!extended.isValid(i, j)) return;
                image.add(i, j, -extended.get(i, j).doubleValue());   // Subtract from the image directly without affecting flagging...
            }
        }.process();

        updateFiltering(FWHM);
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
                sumw += w*w;    // Normalize like window functions, by square sum, in line with Parseval's theorem...
                n++;
            }
            @Override
            public WeightedPoint getLocalResult() { return new WeightedPoint(sumw, n); }
        };
        weightedCalc.process();

        final double rmsw = Math.sqrt(weightedCalc.getResult().value());
        if(rmsw <= 0.0) return;

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
        
        final double norm = -1.0 / rmsw;
        final Image2D image = getImage();
        
        new Fork<Void>() {
            @Override
            public void process(int i, int j) {
                // Subtract from the image directly without affecting flagging...
                image.add(i, j, norm * transformer[i][j]);
            }
        }.process();

        updateFiltering(FWHM);
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

        Gaussian2D mapSmoothing = map.getSmoothingBeam();
        Gaussian2D pixelization = getPixelSmoothing();
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
        resampleFrom(map, map.getIndexTransformTo(this), beam, weight);       
        copyProcessingFrom(map);
    }

    public void resample(double newres) {
        resample(new Vector2D(newres, newres));
    }

    public void resample(Vector2D newres) {
        Map2D orig = clone();
        orig.fitsProperties = fitsProperties.clone();
        
        Vector2D resolution = getResolution();
        
        setSize((int) Math.ceil(sizeX() * resolution.x() / newres.x()), (int) Math.ceil(sizeY() * resolution.y() / newres.y()));
        setGrid(orig.getGrid().forResolution(newres));
        
        resampleFrom(orig);        
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
        else return fitsProperties.getTableEntry(name);
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
        resetProcessing();
        parseHeader(hdu.getHeader());    

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
    public String getInfo() {      
        Grid2D<?> grid = getGrid();

        Unit sizeUnit = getDisplayGridUnit();

        return super.getInfo() + " (" +
                Util.f1.format(sizeX() * grid.pixelSizeX() / sizeUnit.value()) + " x " + 
                Util.f1.format(sizeY() * grid.pixelSizeY() / sizeUnit.value()) + " " + sizeUnit.name() + ")." +
                grid.toString(sizeUnit) +
                "Instrument PSF: " + getUnderlyingBeam().toString(sizeUnit) + " FWHM.\n" +
                "Applied Smoothing: " + smoothingBeam.toString(sizeUnit) + " FWHM (includes pixelization).\n" +
                "Image Resolution: " + getImageBeam().toString(sizeUnit) + " FWHM (includes smoothing).\n";

    }

    public void filterBeamCorrect() {
        filterCorrect(getUnderlyingBeam().getCircularEquivalentFWHM());
    }

    public void filterCorrect(double underlyingFWHM) {
        filterCorrect(underlyingFWHM, this);
    }

    public void filterCorrect(double underlyingFWHM, Values2D reference) {
        double blankingValue = getFilterBlanking();
        filterCorrectValidated(underlyingFWHM, new RangeRestricted2D(reference, new Range(-blankingValue, blankingValue)));
    }

    public void filterCorrectValidated(double underlyingFWHM, final Validating2D validator) {  
        // Undo prior corrections if necessary
        if(!isCorrected()) { 
            if(underlyingFWHM == getCorrectingFWHM()) return;
            undoFilterCorrectBy(validator);
        }
        final double filterC =getFilterCorrectionFactor(underlyingFWHM);
     
        new Fork<Void>() {
            @Override
            protected void process(int i, int j) {
                if(validator.isValid(i, j)) scale(i, j, filterC);
            }
        }.process();

        setCorrectingFWHM(underlyingFWHM);
    }


    public void undoFilterCorrect() {
        undoFilterCorrect(this);
    }

    public void undoFilterCorrect(Data2D reference) {
        double blankingValue = getFilterBlanking();
        undoFilterCorrectBy(new RangeRestricted2D(reference, new Range(-blankingValue, blankingValue)));
    }


    public void undoFilterCorrectBy(final Validating2D validator) {
        if(!isCorrected()) return;

        final double iFilterC = 1.0 / getFilterCorrectionFactor(getCorrectingFWHM());

        new Fork<Void>() {
            @Override
            protected void process(int i, int j) {
                if(validator.isValid(i,  j)) scale(i, j, iFilterC);
            }
        }.process();

        setCorrectingFWHM(Double.NaN);
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

    @Override
    public RegularData<Index2D, Vector2D> getData() {
        return this;
    }

    
    
    private final static String underlyingBeamFitsID = "I";
    private final static String smoothingBeamFitsID = "S";
    private final static String correctedBeamFitsID = "C";
    private final static String filterBeamFitsID = "X";


}
