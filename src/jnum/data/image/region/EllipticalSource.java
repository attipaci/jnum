/* *****************************************************************************
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
 *     Attila Kovacs  - initial API and implementation
 ******************************************************************************/

package jnum.data.image.region;

import jnum.Constant;
import jnum.ExtraMath;
import jnum.IncompatibleTypesException;
import jnum.PointOp;
import jnum.Unit;
import jnum.Util;
import jnum.data.DataPoint;
import jnum.data.image.Gaussian2D;
import jnum.data.image.Grid2D;
import jnum.data.image.IndexBounds2D;
import jnum.data.image.Map2D;
import jnum.data.image.Values2D;
import jnum.data.image.overlay.Viewport2D;
import jnum.data.index.Index2D;
import jnum.fits.FitsToolkit;
import jnum.math.Coordinate2D;
import jnum.math.Vector2D;
import jnum.util.DataTable;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;
import nom.tam.util.Cursor;



public class EllipticalSource extends GaussianSource {


    private static final long serialVersionUID = -7816545856774075826L;

    private DataPoint elongation = new DataPoint();

    private DataPoint angle = new DataPoint();


    public EllipticalSource(Class<? extends Coordinate2D> coordinateClass) {
        super(coordinateClass);
    }

    
    public EllipticalSource(Coordinate2D coords, double fwhm) {
        this(coords, fwhm, fwhm, 0.0);
    }

    public EllipticalSource(Coordinate2D coords, double a, double b, double angle) {
        super(coords, Math.sqrt(a * b));
        
        if(a > b) {
            setElongation(a, b);
            getPositionAngle().setValue(angle);
        }
        else {
            setElongation(b, a);
            getPositionAngle().setValue(angle + Constant.rightAngle);
        }
    }

    public EllipticalSource(Class<? extends Coordinate2D> coordinateClass, String line, int format) throws Exception {
        super(coordinateClass, line, format);
    }


    @Override
    public int hashCode() { 
        int hash = super.hashCode();
        if(angle != null) hash ^= angle.hashCode();
        if(elongation != null) hash ^= elongation.hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(!(o instanceof EllipticalSource)) return false;
        if(!super.equals(o)) return false;
        EllipticalSource e = (EllipticalSource) o;
        if(!Util.equals(angle, e.angle)) return false;
        if(!Util.equals(elongation, e.elongation)) return false;
        return true;
    }

    public DataPoint getElongation() { return elongation; }
    
    public void setElongation(double major, double minor) {
        elongation.setValue((major - minor) / (major + minor));
        elongation.exact();
    }

    public DataPoint getPositionAngle() { return angle; }


    public DataPoint getMajorFWHM() { 
        DataPoint major = new DataPoint(getFWHM());
        major.multiplyBy(elongation);
        major.add(getFWHM());
        // Renormalize to keep area unchanged...
        major.scale(1.0 / (1.0 - elongation.value() * elongation.value()));  
        return major;
    }

    public DataPoint getMinorFWHM() { 
        DataPoint minor = new DataPoint(getFWHM());
        minor.multiplyBy(elongation);
        minor.scale(-1.0);
        minor.add(getFWHM());
        // Renormalize to keep area unchanged...
        minor.scale(1.0 / (1.0 - elongation.value() * elongation.value()));  
        return minor;
    }
    
    
    @Override
    public void editHeader(Header header, Unit sizeUnit) throws HeaderCardException {
        super.editHeader(header, sizeUnit);

        DataPoint major = getMajorFWHM();
        DataPoint minor = getMinorFWHM();
        DataPoint angle = getPositionAngle();

        boolean hasError = getRadius().weight() > 0.0;

        Cursor<String, HeaderCard> c = FitsToolkit.endOf(header);
        
        if(!major.isNaN()) {
            c.add(new HeaderCard("SRCMAJ", major.value() / sizeUnit.value(), "(" + sizeUnit.name() + ") source major axis."));
            if(hasError) c.add(new HeaderCard("SRCMAJER", major.rms() / sizeUnit.value(), "(" + sizeUnit.name() + ") major axis error."));
        }

        if(!minor.isNaN()) {
            c.add(new HeaderCard("SRCMIN", minor.value() / sizeUnit.value(), "(" + sizeUnit.name() + ") source minor axis."));
            if(hasError) c.add(new HeaderCard("SRCMINER", minor.rms() / sizeUnit.value(), "(" + sizeUnit.name() + ") minor axis error."));
        }

        if(!angle.isNaN()) {
            c.add(new HeaderCard("SRCPA", angle.value() / Unit.deg, "(deg) source position angle."));
            c.add(new HeaderCard("SRCPAERR", angle.rms() / Unit.deg, "(deg) source angle error."));
        }
    }


    @Override
    public String pointingInfo(Map2D map) {
        String info = super.pointingInfo(map);

        Unit sizeUnit = map.getDisplayGridUnit();

        DataPoint major = getMajorFWHM();
        DataPoint minor = getMinorFWHM();

        major.scale(1.0 / sizeUnit.value());
        minor.scale(1.0 / sizeUnit.value());

        DataPoint angle = getPositionAngle();
        angle.scale(1.0 / Unit.deg);

        info += " (a=" + major.toString(Util.f1) + ", b=" + minor.toString(Util.f1) 
        + ", angle=" + angle.toString(Util.d1) +  " deg)";

        return info;
    }



    
    
    @Override
    public Gaussian2D getGaussian2D() {
        return new Gaussian2D(getMajorFWHM().value(), getMinorFWHM().value(), getPositionAngle().value());
    }
    
  
    
    @Override
    public EllipticalSource.Representation getRepresentation(Grid2D<?> grid) throws IncompatibleTypesException {
        return new Representation(grid);
    }



    public class Representation extends GaussianSource.Representation {


        protected Representation(Grid2D<?> grid) throws IncompatibleTypesException {
            super(grid);
        }


        @Override
        public double adaptTo(final Values2D image) { 
            double I = super.adaptTo(image);
            measureShape(image, 0.0, 1.5);
            return I;
        }
        
        public void measureShape(final Values2D image, double minRScale, double maxRScale) {      
            final double minr = minRScale * getFWHM().value();
            final Vector2D center = getCenterOffset();    
            
            getFWHM().scale(maxRScale);
            
            final Viewport2D view = getViewer(image);
   
            view.loop(new PointOp<Index2D, Void>() {

                private double m2c, m2s, sumw;
                private Vector2D v;

                @Override
                protected void init() {
                    m2c = m2s = sumw = 0.0;
                    v = new Vector2D();
                }
                
                @Override
                public void process(Index2D index) {     
                    if(!view.isValid(index)) return;
                       
                    double w = view.get(index).doubleValue();
                     
                    v.set(index.i() + view.fromi(), index.j() + view.fromj());
                    getGrid().toOffset(v);
                    v.subtract(center);
      
                    if(v.length() < minr) return;

                    //double theta = 2.0 * v.angle();
                    //double c = Math.cos(theta);
                    //double s = Math.sin(theta);

                    // More computationally efficient way to get sin/cos 2*angle:
                    double C = v.cosAngle();
                    double S = v.sinAngle();
                    
                    double c = C * C - S * S;
                    double s = 2.0 * S * C;
                    
                    m2c += w * c;
                    m2s += w * s;
                    sumw += Math.abs(w);
                }      
                
                @Override
                public Void getResult() {  
                    if(sumw > 0.0) {              
                        m2c *= 1.0 / sumw;
                        m2s *= 1.0 / sumw;

                        elongation.setValue(2.0 * ExtraMath.hypot(m2s, m2c));
                        elongation.setWeight(sumw);

                        angle.setValue(0.5 * Math.atan2(m2s, m2c));
                        angle.setRMS(elongation.rms() / elongation.value());
                    }
                    else {
                        angle.noData();
                        elongation.noData();
                    }
                    return null;
                }

                
                
            });
            
            getFWHM().scale(1.0 / maxRScale);
            
        }


        @Override
        public void updateBounds(IndexBounds2D bounds, double radialScale) {       
            super.updateBounds(bounds, (1.0 + elongation.value()) * radialScale);
        }


        @Override
        public DataTable getData(Map2D map, Unit sizeUnit) {
            DataTable data = super.getData(map, sizeUnit);

            DataPoint major = getMajorFWHM();
            DataPoint minor = getMinorFWHM();
            DataPoint angle = getPositionAngle();
            
            Unit deg = Unit.get("deg");
            
            
            data.new Entry("a", major.value(), sizeUnit);
            data.new Entry("b", minor.value(), sizeUnit);
            data.new Entry("angle", angle.value(), deg);
            data.new Entry("dangle", angle.rms(), deg);

            data.new Entry("da", major.rms(), sizeUnit);
            data.new Entry("db", minor.rms(), sizeUnit);

            return data;
        }

      

    }

}
