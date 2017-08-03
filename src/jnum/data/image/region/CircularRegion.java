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


import jnum.ExtraMath;
import jnum.IncompatibleTypesException;
import jnum.Unit;
import jnum.Util;
import jnum.data.DataPoint;
import jnum.data.image.Data2D;
import jnum.data.image.Asymmetry2D;
import jnum.data.image.Grid2D;
import jnum.data.image.Index2D;
import jnum.data.image.IndexBounds2D;
import jnum.data.image.Map2D;
import jnum.data.image.Observation2D;
import jnum.data.image.Values2D;
import jnum.data.image.overlay.Overlay2D;
import jnum.data.image.overlay.Viewport2D;
import jnum.math.Coordinate2D;
import jnum.math.CoordinateSystem;
import jnum.math.Range;
import jnum.math.SphericalCoordinates;
import jnum.math.Vector2D;
import jnum.text.AngleFormat;
import jnum.text.StringParser;
import jnum.text.TableFormatter;


public class CircularRegion extends Region2D implements TableFormatter.Entries {

    private static final long serialVersionUID = -7133550327744995670L;

    private Coordinate2D coords;

    private DataPoint radius;
    
    private int positioningMethod = POSITION_PEAK;

    public CircularRegion(Class<? extends Coordinate2D> coordinateClass) { 
        super(coordinateClass); 
        radius = new DataPoint();
    }


    public CircularRegion(Coordinate2D coords, double r) {
        this(coords.getClass());
        setCoordinates(coords);
        setRadius(r);
    }

    public CircularRegion(Class<? extends Coordinate2D> coordinateClass, String line, int format) throws Exception { 
        this(coordinateClass); 
        parse(line, format);
    }

    /* (non-Javadoc)
     * @see jnum.data.Region#hashCode()
     */
    @Override
    public int hashCode() {
        int hash = super.hashCode();
        if(coords != null) hash ^= coords.hashCode();
        if(radius != null) hash ^= radius.hashCode();
        return hash;
    }

    /* (non-Javadoc)
     * @see jnum.data.Region#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(!(o instanceof CircularRegion)) return false;
        if(!super.equals(o)) return false;
        CircularRegion r = (CircularRegion) o;
        if(!Util.equals(coords, r.coords)) return false;
        if(!Util.equals(radius, r.radius)) return false;
        return true;
    }


    /* (non-Javadoc)
     * @see jnum.data.Region#clone()
     */ 
    @Override
    public Object clone() {
        CircularRegion clone = (CircularRegion) super.clone();
        return clone;
    }

    public void convertTo(Class<? extends Coordinate2D> coordinateClass) throws IncompatibleTypesException, InstantiationException, IllegalAccessException { 
        Coordinate2D converted;
        converted = coordinateClass.newInstance();
        coords.convertTo(converted);
        coords = converted;
    }



    /**
     * Gets the coordinates.
     *
     * @return the coordinates
     */
    public Coordinate2D getCoordinates() {
        return coords;
    }

    /**
     * Sets the coordinates.
     *
     * @param coords the new coordinates
     */
    public void setCoordinates(Coordinate2D coords) {
        setCoordinateClass(coords.getClass());
        this.coords = coords;
    }

    /**
     * Gets the radius.
     *
     * @return the radius
     */
    public DataPoint getRadius() { return radius; }

    /**
     * Sets the radius.
     *
     * @param r the new radius
     */
    public void setRadius(DataPoint r) { this.radius = r; }

    /**
     * Sets the radius.
     *
     * @param r the new radius
     */
    public void setRadius(double r) { 
        if(radius == null) radius = new DataPoint();
        else radius.setWeight(0.0);
        radius.setValue(r);
    }
    
    
    public void setPositioningMethod(int method) {
        positioningMethod = method;
    }

    public int getPositioningMethod() { return positioningMethod; }
    
    public void setPeakPositioning() { setPositioningMethod(POSITION_PEAK); }
    
    public void setCentroidPositioning() { setPositioningMethod(POSITION_PEAK); }
    

    @Override
    public CircularRegion.Representation getRepresentation(Grid2D<?> grid) throws IncompatibleTypesException {
        return new Representation(grid);
    }


    @Override
    public String toString(int format) {
        String line = null;

        switch(format) {
        case FORMAT_CRUSH : line = toCrushString(); break;
        case FORMAT_DS9 : line = toDS9String(); break;
        }

        String comment = getComment();
        if(comment.length() > 0) line += "\t#" + comment; 

        return line;
    }

    
    public String toCrushString() {
        Coordinate2D coords = getCoordinates();	

        if(coords instanceof SphericalCoordinates) {
            SphericalCoordinates spherical = (SphericalCoordinates) coords;
            CoordinateSystem axes = spherical.getCoordinateSystem();
            ((AngleFormat) axes.get(0).format).colons();
            ((AngleFormat) axes.get(1).format).colons();
            return getID() + "\t" + coords.toString() + "  " + Util.f1.format(radius.value()/Unit.arcsec) + " # " + getComment();
        }
        else return getID() + "\t" + coords.x() + "\t" + coords.y() + "\t" + radius.value() + "\t# " + getComment();
    }

  
    public String toDS9String() {
        Coordinate2D coords = getCoordinates();

        if(coords instanceof SphericalCoordinates) {
            SphericalCoordinates spherical = (SphericalCoordinates) coords;
            CoordinateSystem axes = spherical.getCoordinateSystem();
            ((AngleFormat) axes.get(0).format).colons();
            ((AngleFormat) axes.get(1).format).colons();

            return "circle(" 
            + axes.get(0).format(coords.x()) + ","
            + axes.get(1).format(coords.y()) + ","
            + Util.f3.format(radius.value() / Unit.arcsec) + "\")";
        }
        else return "circle(" + coords.x() + "," + coords.y() + "," + radius.value() + ")";		
    }


    @Override
    public void parse(StringParser parser, int format) throws Exception {	
        switch(format) {
        case FORMAT_CRUSH : parseCrush(parser); break;
        case FORMAT_DS9 : parseDS9(parser); break;
        }
    }

    
    public void parseCrush(StringParser parser) throws Exception {
        setID(parser.nextToken());
        
        Coordinate2D coords = getCoordinateClass().newInstance();
        coords.parse(parser);
        setCoordinates(coords);
        
        setRadius(Double.parseDouble(parser.nextToken()) * Unit.arcsec);

        String line = parser.getString();
        if(line.contains("#")) setComment(line.substring(line.indexOf('#') + 2));
    }

   
    public void parseDS9(StringParser parser) throws Exception {	
        Coordinate2D coords = getCoordinateClass().newInstance();

        boolean isCircle = parser.nextToken().equalsIgnoreCase("circle");

        coords.parse(parser);
        setCoordinates(coords);
            
        if(isCircle) {
            String R = parser.nextToken();
            char unit = R.charAt(R.length() - 1);
            setRadius(Double.parseDouble(R.substring(0, R.length()-1)));
            
            if(unit == '\'') radius.scale(Unit.arcmin);
            else if(unit == '"') radius.scale(Unit.arcsec);
            else radius.scale(Unit.deg);
        }
        else setRadius(Double.NaN);
    }
    
    public final void adaptTo(Observation2D map) {
        getRepresentation(map.getGrid()).adaptTo(map);
    }
    
    public final void adaptTo(Map2D map) {
        adaptTo(map.getGrid(), map);
    }
     
    public final void adaptTo(Grid2D<?> grid, Values2D values) {
        getRepresentation(grid).adaptTo(values);
    }
          
    public Asymmetry2D getAsymmetry(Observation2D values, double angle, Range radialRange) {
        return getAsymmetry(values.getGrid(), values.getSignificance(), angle, radialRange);
    }
    
    public Asymmetry2D getAsymmetry(Map2D values, double angle, Range radialRange) {
        return getAsymmetry(values.getGrid(), values, angle, radialRange);
    }
    
    public Asymmetry2D getAsymmetry(Grid2D<?> grid, Values2D values, double angle, Range radialRange) {
        Representation r = (Representation) getRepresentation(grid);
        Data2D image = values instanceof Data2D ? (Data2D) values : new Overlay2D(values);
        return r.getAsymmetry2D(image, angle, radialRange);
    }
    
    /* (non-Javadoc)
     * @see jnum.text.TableFormatter.Entries#getFormattedEntry(java.lang.String, java.lang.String)
     */
    @Override
    public Object getTableEntry(String name) {
        if(name.equals("r")) return radius.value();
        else if(name.equals("dr")) return radius.rms();
        else return TableFormatter.NO_SUCH_DATA;
    }


    public class Representation extends Region2D.Representation {
        private Vector2D centerIndex;

        protected Representation(Grid2D<?> grid) throws IncompatibleTypesException {
            super(grid);
            if(coords != null) centerIndex = getIndex(coords);
        }

        public Vector2D getCenterIndex() { return centerIndex; }
        
        public final void setCenterIndex(Index2D index) throws IncompatibleTypesException {
            setCenterIndex(new Vector2D(index.i(), index.j()));
        }
            
        public void setCenterIndex(Vector2D index) throws IncompatibleTypesException {
            if(coords == null) coords = getGrid().getReference().copy();
            coords.convertFrom(getGridCoords(index));
            centerIndex = index;
        }
        
        public Vector2D getViewerCenterIndex() { 
            IndexBounds2D bounds = getBounds();
            return new Vector2D(centerIndex.x() - bounds.fromi, centerIndex.y() - bounds.fromj);
        }
        
        public final void getCenterOffset(Vector2D offset) {
            getGrid().indexToOffset(centerIndex, offset);
        }
        
        public final Vector2D getCenterOffset() {
            Vector2D offset = new Vector2D();
            getCenterOffset(offset);
            return offset;
        }
        
        @Override
        public boolean isInside(double i, double j) {
            Vector2D delta = getGrid().getResolution();
            double d = ExtraMath.hypot(delta.x() * (i - centerIndex.x()), delta.y() * (j - centerIndex.y()));
            return d <= radius.value();
        }

        @Override
        public IndexBounds2D getBounds() {
            IndexBounds2D bounds = new IndexBounds2D();
            updateBounds(bounds);
            return bounds;
        }
        
        public void updateBounds(IndexBounds2D bounds) {
            updateBounds(bounds, 1.0);
        }
     
        public void updateBounds(IndexBounds2D bounds, double radialScale) {
            Vector2D delta = getGrid().getResolution();
            double r = radialScale * radius.value();
            
            bounds.set(
                    centerIndex.x() - r/delta.x(), centerIndex.y() - r/delta.y(),
                    centerIndex.x() + r/delta.x(), centerIndex.y() + r/delta.y()
            );
        }

        
     
        // Increase the aperture until it captures >98% of the flux
        public double adaptTo(Values2D image) {      
            moveTo(image, 0);
            
            IndexBounds2D bounds = getBounds();
            Viewport2D view = new Viewport2D(image, bounds);
               
            double I = view.getSum();
                 
            // 20 iterations on 20% increases covers ~40-fold increase in radius
            // Should be plenty even for a very large pointing source...
            for(int i=0; i<20; i++) {
                // A 20% increase in radius is ~40% increase in area.
                getRadius().scaleValue(1.2);
                updateBounds(bounds);
                view.setBounds(bounds);
                
                double I1 = view.getSum();
                
                if(I1 <= 1.005 * I) break;
                
                I = I1;
            }
            
            return I;
        }
        
        public double adaptTo(Observation2D map) {
            return adaptTo(map.getSignificance());
        }
        
      
        
        public void moveToLocalPeak(Values2D image, int sign) {
            // TODO make sure the radius is at least a few pixels in size...
              
            Viewport2D view = getViewer(image);
            Index2D peakIndex = null;
            if(sign == 0) peakIndex = view.indexOfMaxDev();
            else peakIndex = sign < 0 ? view.indexOfMin() : view.indexOfMax();
            
            Vector2D fracIndex = view.getRefinedPeakIndex(peakIndex);
            fracIndex.addX(view.fromi());
            fracIndex.addY(view.fromj());
            
            setCenterIndex(fracIndex);
        }
        
        
        public void moveToLocalCentroid(Values2D image) { 
            final Viewport2D view = getViewer(image);
            
            final Vector2D centroid = view.getCentroidIndex();
            centroid.addX(view.fromi());
            centroid.addY(view.fromj());
          
            setCenterIndex(centroid);
        }
        
        
        public void moveTo(Values2D image, int sign) {
            switch(getPositioningMethod()) {
            case POSITION_PEAK: moveToLocalPeak(image, sign); break;
            case POSITION_CENTRIOD: moveToLocalCentroid(image); break;
            default: throw new IllegalStateException("Uknown positioning method " + getPositioningMethod());
            }
        }

        public Asymmetry2D getAsymmetry2D(Data2D image, double angle, Range radialRange) {
            return image.getAsymmetry2D(getGrid(), getCenterIndex(), angle, radialRange);
        }
      
       

    }
    
    
    public static final int POSITION_PEAK = 0;
    public static final int POSITION_CENTRIOD = 1;
}

