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



import jnum.Unit;
import jnum.Util;
import jnum.fits.FitsProperties;
import jnum.fits.FitsToolkit;
import jnum.math.Coordinate2D;
import jnum.math.Vector2D;
import jnum.projection.Projection2D;
import jnum.util.HashCode;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;
import nom.tam.util.Cursor;


public class MapProperties extends FitsProperties {
    
    /**
     * 
     */
    private static final long serialVersionUID = 6844342367048765949L;
    
    
    private Grid2D<?> grid = new FlatGrid2D();  
    
    private Unit displayGridUnit;
    
   
    private Gaussian2D underlyingBeam;
  
    private Gaussian2D smoothingBeam;
    
     
    private double filterFWHM = Double.NaN;
  
    private double correctingFWHM = Double.NaN; 
    
    
    private double filterBlanking = Double.POSITIVE_INFINITY;
    
    
    
    @Override
    public int hashCode() {
        int hash = super.hashCode() ^ HashCode.from(filterFWHM) ^ HashCode.from(correctingFWHM) ^ HashCode.from(filterBlanking);
        if(grid != null) hash ^= grid.hashCode();
        if(displayGridUnit != null) hash ^= displayGridUnit.hashCode();
        if(underlyingBeam != null) hash ^= underlyingBeam.hashCode();
        if(smoothingBeam != null) hash ^= smoothingBeam.hashCode();
        return hash;
    }
    
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof MapProperties)) return false;
       
        MapProperties p = (MapProperties) o;
        if(!Util.equals(filterFWHM, p.filterFWHM)) return false;
        if(!Util.equals(correctingFWHM, p.correctingFWHM)) return false;
        if(!Util.equals(filterBlanking, p.filterBlanking)) return false;
        
        if(!Util.equals(grid, p.grid)) return false;
        if(!Util.equals(displayGridUnit, p.displayGridUnit)) return false;
        if(!Util.equals(underlyingBeam, p.underlyingBeam)) return false;
        if(!Util.equals(smoothingBeam, p.smoothingBeam)) return false;
        
        return super.equals(o);
    }
    
    @Override
    public void copy(FitsProperties template) {
        super.copy(template);
        if(!(template instanceof MapProperties)) return;
        
        MapProperties p = (MapProperties) template;
        
        copyProcessingFrom(p);
        
        filterFWHM = p.filterFWHM;
        correctingFWHM = p.correctingFWHM;
        filterBlanking = p.filterBlanking;
        
        if(p.grid != null) grid = p.grid.copy();
        if(p.displayGridUnit != null) displayGridUnit = p.displayGridUnit.copy();
       
        if(p.underlyingBeam != null) underlyingBeam = p.underlyingBeam.copy();
        if(p.smoothingBeam != null) smoothingBeam = p.smoothingBeam.copy();
    }
    
    @Override
    public MapProperties copy() {
        MapProperties copy = (MapProperties) super.copy();
       
        if(underlyingBeam != null) copy.underlyingBeam = underlyingBeam.copy();
        if(smoothingBeam != null) copy.smoothingBeam = smoothingBeam.copy();   
       
        if(grid != null) copy.grid = grid.copy();
        if(displayGridUnit != null) copy.displayGridUnit = displayGridUnit.copy();
        
        return copy;
    }
    
    
    public void copyProcessingFrom(MapProperties other) {
        underlyingBeam = other.underlyingBeam == null ? null : other.underlyingBeam.copy();
        smoothingBeam = other.smoothingBeam == null ? null : other.smoothingBeam.copy();
          
        filterFWHM = other.filterFWHM;
        filterBlanking = other.filterBlanking;
        
        correctingFWHM = other.correctingFWHM;
    }
    
    @Override
    public void resetProcessing() {
        super.resetProcessing();
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
    
    public final Grid2D<?> getGrid() { return grid; }
    
    public void setGrid(Grid2D<?> grid) { 
        // Undo the prior pixel smoothing, if any...
        if(smoothingBeam != null) if(this.grid != null) smoothingBeam.deconvolveWith(getPixelSmoothing());     
        
        this.grid = grid; 
        
        // Apply new pixel smoothing...
        if(smoothingBeam == null) smoothingBeam = getPixelSmoothing();
        else smoothingBeam.encompass(getPixelSmoothing());      
    }

    public void setResolution(double dx, double dy) { 
        // Undo the prior pixel smoothing, if any...
        if(smoothingBeam != null) if(this.grid != null) smoothingBeam.deconvolveWith(getPixelSmoothing());      
        
        getGrid().setResolution(dx, dy);
        
        // Apply new pixel smoothing...
        if(smoothingBeam == null) smoothingBeam = getPixelSmoothing();
        else smoothingBeam.encompass(getPixelSmoothing());
    }
    
    public Gaussian2D getPixelSmoothing() {
        Vector2D resolution = grid.getResolution();      
        return new Gaussian2D(resolution.x() / Gaussian2D.fwhm2size, resolution.y() / Gaussian2D.fwhm2size, 0.0);
    }
    
    
    public void setPixelSmoothing() {
        smoothingBeam.copy(getPixelSmoothing());
    }

    
 
    public final Projection2D<?> getProjection() { return getGrid().getProjection(); }
    
    public final Coordinate2D getReference() { return getGrid().getReference(); }
    
    public final double getPixelArea() { return getGrid().getPixelArea(); } 


 
    public final Gaussian2D getUnderlyingBeam() { return underlyingBeam; }
    

    public void setUnderlyingBeam(Gaussian2D psf) { 
        underlyingBeam = psf; 
    }
    

    public void setUnderlyingBeam(double fwhm) { 
        underlyingBeam = new Gaussian2D(fwhm);     
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
    
  
    public void seDisplayGridUnit(Unit u) {
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
     

    public void merge(MapProperties other) {
        if(smoothingBeam != null) {
            if(other.smoothingBeam != null) smoothingBeam.encompass(other.smoothingBeam);
        }
        else {
            if(other.smoothingBeam != null) smoothingBeam = other.smoothingBeam.copy();       
        }
        filterFWHM = Math.min(filterFWHM, other.filterFWHM);
    }
    

    @Override
    public Object getTableEntry(String name) {
        return super.getTableEntry(name);
    }
    
    @Override
    public String brief(String header) {
        Unit sizeUnit = getDisplayGridUnit();
         
        String info = 
                super.brief(header) + "\n" +
                grid.toString(sizeUnit) +
                "Instrument PSF: " + getUnderlyingBeam().toString(sizeUnit) + " FWHM.\n" +
                "Applied Smoothing: " + smoothingBeam.toString(sizeUnit) + " FWHM (includes pixelization).\n" +
                "Image Resolution: " + getImageBeam().toString(sizeUnit) + " FWHM (includes smoothing).\n";
                
        return info;
    }
    
    private final static String underlyingBeamFitsID = "I";
    private final static String smoothingBeamFitsID = "S";
    private final static String correctedBeamFitsID = "C";
    private final static String filterBeamFitsID = "X";
   
}
