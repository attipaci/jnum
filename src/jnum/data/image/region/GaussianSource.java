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

package jnum.data.image.region;



import jnum.Constant;
import jnum.IncompatibleTypesException;
import jnum.Symbol;
import jnum.Unit;
import jnum.Util;
import jnum.data.DataPoint;
import jnum.data.image.Gaussian2D;
import jnum.data.image.Grid2D;
import jnum.data.image.Map2D;
import jnum.data.image.Observation2D;
import jnum.data.image.Values2D;
import jnum.data.image.overlay.Viewport2D;
import jnum.fits.FitsToolkit;
import jnum.math.Coordinate2D;
import jnum.math.Vector2D;
import jnum.text.StringParser;
import jnum.util.DataTable;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;
import nom.tam.util.Cursor;




// TODO: Auto-generated Javadoc
/**
 * The Class GaussianSource.
 *
 * @param <CoordinateType> the generic type
 */
public class GaussianSource extends CircularRegion {

    private static final long serialVersionUID = 786127030179333921L;

    private DataPoint peak = new DataPoint(); 

    private boolean isCorrected = false;
    
    private String unitName = "";
    
  
    public GaussianSource(Class<? extends Coordinate2D> coordinateClass) { super(coordinateClass); }

    public GaussianSource(Class<? extends Coordinate2D> coordinateClass, String line, int format) throws Exception {
        super(coordinateClass, line, format);
    }

    public GaussianSource(Coordinate2D coords, double r) {
        super(coords, r);
    }


    @Override
    public int hashCode() {
        int hash = super.hashCode() ^ (isCorrected ? 1 : 0);
        if(peak != null) hash ^= peak.hashCode();
        return hash;
    }


    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(!super.equals(o)) return false;
        GaussianSource s = (GaussianSource) o;
        if(isCorrected != s.isCorrected) return false;
        if(!Util.equals(peak, s.peak)) return false;
        return true;
    }

    public String getUnitName() { return unitName; }
    
    public void setUnitName(String name) { unitName = (name == null) ? "" : name; }
    
    public final DataPoint getFWHM() { return getRadius(); }

    public void setFWHM(double value) { setRadius(value); }

    public void setFWHM(DataPoint value) { setRadius(value); }

 

    public DataPoint getPeak() { return peak; }

    public void setPeak(DataPoint value) { peak = value; }

    public void setPeak(double value) { 
        if(peak == null) peak = new DataPoint();
        else peak.exact();
        peak.setValue(value);
    }


    public boolean isCorrected() { return isCorrected; }


    public void setCorrected(boolean value) { isCorrected = value; }


    public double getCorrectionFactor(Map2D map) {	
        double correction = 1.0;	

        // Correct for filtering.
        // Consider that only the tip of the source might escape the filter...
        if(!map.isFiltered()) return 1.0;

        double filterFraction = map.isFilterBlanked() ? Math.min(1.0, map.getFilterBlanking() / peak.significance()) : 1.0;
        double filtering = 1.0 - 1.0 / map.getFilterCorrectionFactor(getFWHM().value());
        correction *= 1.0 / (1.0 - filtering * filterFraction);

        return correction;
    }


    public void correct(Map2D map) {	
        if(isCorrected) throw new IllegalStateException("Source is already corrected.");
        peak.scale(getCorrectionFactor(map));
        isCorrected = true;
    }


    public void uncorrect(Map2D map) {
        if(!isCorrected) throw new IllegalStateException("Source is already uncorrected.");
        peak.scale(1.0 / getCorrectionFactor(map));
        isCorrected = false;
    }


    public void convolveWith(Gaussian2D psf) {
        double f = getFWHM().value();
        getFWHM().setValue(getConvolvedBeam(psf).getCircularEquivalentFWHM());
        f /= getFWHM().value();
        getFWHM().scaleWeight(1.0 / (f*f));
        // TODO propagation of angle errors (EllipticalSource)...
    }

    public void deconvolveWith(Gaussian2D psf) {   
        double f = getFWHM().value();
        getFWHM().setValue(getDeconvolvedBeam(psf).getCircularEquivalentFWHM());
        f /= getFWHM().value();
        getFWHM().scaleWeight(1.0 / (f*f));
        // TODO propagation of angle errors (EllipticalSource)...
    }

    public Gaussian2D getConvolvedBeam(Gaussian2D psf) {        
        Gaussian2D beam = getGaussian2D();
        beam.convolveWith(psf);
        return beam;
    }
    
    public Gaussian2D getDeconvolvedBeam(Gaussian2D psf) {        
        Gaussian2D beam = getGaussian2D();
        beam.deconvolveWith(psf);
        return beam;
    }
    
    
    /* (non-Javadoc)
     * @see jnum.data.CircularRegion#toCrushString(jnum.data.GridImage)
     */
    @Override
    public String toCrushString() {
        return getID() + "\t" + super.toCrushString() + "  " + peak + " " + unitName;
    }

    /* (non-Javadoc)
     * @see jnum.data.Region#getComment()
     */
    @Override
    public String getComment() {
        return "s/n=" + Util.f2.format(peak.significance()) + " " + super.getComment();
    }

    /* (non-Javadoc)
     * @see jnum.data.CircularRegion#parseCrush(java.lang.String, jnum.data.GridImage)
     */
    @Override
    public void parseCrush(StringParser parser) throws Exception {
        super.parseCrush(parser);

        peak.setValue(Double.parseDouble(parser.nextToken()));
        String next = parser.nextToken();
        if(next.equals("+-") || next.equals("+/-") || next.equals(Symbol.plusminus + "")) {
            peak.setRMS(Double.parseDouble(parser.nextToken()));
            next = parser.nextToken();
        }

        if(next != null) setUnitName(next); 

    }


    public void editHeader(Header header, Unit sizeUnit) throws HeaderCardException {
        Cursor<String, HeaderCard> c = FitsToolkit.endOf(header);    
       
        c.add(new HeaderCard("SRCPEAK", peak.value(), "(" + getUnitName() + ") source peak flux."));
        c.add(new HeaderCard("SRCPKERR", peak.rms(), "(" + getUnitName() + ") peak flux error."));

        // Fint = peak * 1.14 * FWHM^2
        // dFint^2 = (dpeak * 1.14 * FWHM^2)^2 + (1.14 * peak * 2 FWHM * dFWHM)^2
        
        //DataPoint F = getIntegral();     
        //c.add(new HeaderCard("SRCINT", F.value(), "Source integrated flux."));
        //c.add(new HeaderCard("SRCIERR", F.rms(), "Integrated flux error."));

        if(!getFWHM().isNaN()) {
            c.add(new HeaderCard("SRCFWHM", getFWHM().value() / sizeUnit.value(), "(" + sizeUnit.name() + ") source FWHM."));
            if(getFWHM().weight() > 0.0) c.add(new HeaderCard("SRCWERR", getFWHM().rms() / sizeUnit.value(), "(" + sizeUnit.name() + ") FWHM error."));
        }
    }
    
    public double getBeamArea() {
        return Gaussian2D.areaFactor * getFWHM().value() * getFWHM().value();
    }
    
    public DataPoint getIntegral(double psfArea) {
        DataPoint F = new DataPoint(peak);     
        F.scale(getBeamArea() / psfArea);
        return F;
    }
    
    
    
    
    public String pointingInfo(Map2D map) {
          
        StringBuffer info = new StringBuffer();
        //info.append("  [" + getID() + "]\n");
        info.append("  Peak: " + peak + " " + getUnitName() + " (S/N ~ " + Util.f1.format(peak.significance()) + ")\n");

        Unit sizeUnit = map.getDisplayGridUnit();
        DataPoint I = getIntegral(map.getUnderlyingBeam().getArea());
        
        info.append("  Int.: " + I + "\n");
   
        info.append("  FWHM: " + Util.f1.format(getFWHM().value() / sizeUnit.value()) 
        + (getFWHM().weight() > 0.0 ? " +- " + Util.f1.format(getFWHM().rms() / sizeUnit.value()) : "")
        + " " + sizeUnit.name());


        return new String(info);
    }
    
   
    
    
    public Gaussian2D getGaussian2D() {
        return new Gaussian2D(getFWHM().value());
    }
  
 
    
    @Override
    public GaussianSource.Representation getRepresentation(Grid2D<?> grid) throws IncompatibleTypesException {
        return new Representation(grid);
    }

    
    
    
    
  

    public class Representation extends CircularRegion.Representation {

        protected Representation(Grid2D<?> grid) throws IncompatibleTypesException {
            super(grid);
        }

        public final void add(final Values2D image) { addScaled(image, 1.0); }
        
        public final void subtract(final Values2D image) { addScaled(image, -1.0); }
        
        public void addScaled(final Values2D image, final double factor) {
            final Viewport2D view = getViewer(image);
            final Vector2D center = getCenterIndex();
            final Gaussian2D shape = getGaussian2D();
           
            getGrid().toOffset(center);
            
            view.new Loop<Void>() {
                private Vector2D v = new Vector2D();
                
                @Override
                protected void process(int i, int j) {
                    v.set(i + view.fromi(), j + view.fromj());
                    getGrid().toOffset(v);
                    v.subtract(center);
                    view.add(i, j, factor * shape.valueAt(v));
                }
                
            }.process();
        }
        
        
        public final void setPeakFrom(Values2D image) {        
            Vector2D centerIndex = getCenterIndex();
            peak.setValue(image.valueAtIndex(centerIndex.x(), centerIndex.y()));
              
            if(image instanceof Observation2D) peak.setWeight(((Observation2D) image).getWeights().valueAtIndex(centerIndex.x(), centerIndex.y()));
            else peak.exact();
                      
            if(image instanceof Map2D) {
                Unit unit = ((Map2D) image).getUnit();
                peak.scale(1.0 / unit.value());
                setUnitName(unit.name());
            }
        }
      

        
        @Override
        public double adaptTo(Values2D image) {         
            double I = super.adaptTo(image); 
                
            setFWHM(Math.sqrt(I * getGrid().getPixelArea() / peak.value()) / Gaussian2D.fwhm2size);  
            setUnitName(null);
            
            return I;
        }


        @Override
        public double adaptTo(Observation2D map) {
            double I = adaptTo(map.getSignificance());
             
            setPeakFrom(map);   
            getFWHM().setRMS(Math.sqrt(2.0) * getFWHM().value() / peak.significance());
                
            return I;
        }
        
     
        
        // Formula from Kovacs et al. (2006)
        public void setSearchRadius(Gaussian2D underlyingPSF, double pointingRMS) {
            double beamSigma = underlyingPSF.getCircularEquivalentFWHM() / Constant.sigmasInFWHM;
            setRadius(Math.sqrt(4.0 * pointingRMS * pointingRMS - 2.0 * beamSigma * beamSigma * Math.log(1.0 - 2.0 / peak.significance())));
        }

       
        @Override
        public void moveToLocalPeak(Values2D image, int sign) {
            super.moveToLocalPeak(image, sign);     
            setPeakFrom(image);
        }
        
 
        @Override
        public void moveToLocalCentroid(Values2D image) {
            super.moveToLocalCentroid(image);
            setPeakFrom(image);
        }
       
        

        public DataTable getData(Map2D properties, Unit sizeUnit) {
            DataTable data = new DataTable();

            data.new Entry("peak", peak.value(), unitName);
            data.new Entry("dpeak", peak.rms(), unitName);
            data.new Entry("peakS2N", peak.significance(), "");

            DataPoint F = GaussianSource.this.getIntegral(properties.getUnderlyingBeam().getArea());

            data.new Entry("int", F.value(), unitName);
            data.new Entry("dint", F.rms(), unitName);
            data.new Entry("intS2N", F.significance(), "");

            data.new Entry("FWHM", getRadius().value(), sizeUnit);
            data.new Entry("dFWHM", getRadius().rms(), sizeUnit);

            return data;
        }

        
    }

 
    
}
