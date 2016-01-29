/*******************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
 * All rights reserved. 
 * 
 * This file is part of jnum.
 * 
 *     kovacs.util is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     kovacs.util is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with kovacs.util.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Attila Kovacs <attila_kovacs[AT]post.harvard.edu> - initial API and implementation
 ******************************************************************************/
// Copyright (c) 2009 Attila Kovacs 

package jnum.data;


import java.text.NumberFormat;
import java.text.ParseException;
import java.util.StringTokenizer;

import jnum.math.Coordinate2D;
import jnum.math.CoordinateAxis;
import jnum.math.CoordinateSystem;
import jnum.math.Metric;
import jnum.math.SphericalCoordinates;
import jnum.math.Vector2D;
import jnum.text.AngleFormat;
import jnum.text.TableFormatter;
import jnum.util.Constant;
import jnum.util.ExtraMath;
import jnum.util.Unit;
import jnum.util.Util;

// TODO: Auto-generated Javadoc
/**
 * The Class CircularRegion.
 *
 * @param <CoordinateType> the generic type
 */
public class CircularRegion<CoordinateType extends Coordinate2D> extends Region<CoordinateType> implements TableFormatter.Entries {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -7133550327744995670L;

	/** The coords. */
	private CoordinateType coords;
	
	/** The radius. */
	private DataPoint radius;

	/**
	 * Instantiates a new circular region.
	 */
	public CircularRegion() {}

	/**
	 * Instantiates a new circular region.
	 *
	 * @param image the image
	 * @param offset the offset
	 * @param r the r
	 */
	@SuppressWarnings("unchecked")
	public CircularRegion(GridImage2D<CoordinateType> image, Vector2D offset, double r) {
		coords = (CoordinateType) image.getReference().clone();
		image.getProjection().deproject(offset, coords);
		setRadius(r);
		radius.setRMS(Math.sqrt(image.getPixelArea()) / (GridImage2D.fwhm2size * Constant.sigmasInFWHM));
	}
	
	/**
	 * Instantiates a new circular region.
	 *
	 * @param coords the coords
	 * @param r the r
	 */
	public CircularRegion(CoordinateType coords, double r) {
		setCoordinates(coords);
		radius = new DataPoint();
		setRadius(r);
	}
	
	/**
	 * Instantiates a new circular region.
	 *
	 * @param line the line
	 * @param format the format
	 * @param forImage the for image
	 * @throws ParseException the parse exception
	 */
	public CircularRegion(String line, int format, GridImage2D<CoordinateType> forImage) throws ParseException { super(line, format, forImage); }
	
	/* (non-Javadoc)
	 * @see kovacs.util.data.Region#clone()
	 */
	@Override
	public Object clone() {
		CircularRegion<?> clone = (CircularRegion<?>) super.clone();
		return clone;
	}

	
	/**
	 * Gets the index.
	 *
	 * @param grid the grid
	 * @return the index
	 */
	public Vector2D getIndex(Grid2D<CoordinateType> grid) {
		Vector2D index = new Vector2D();
		grid.getIndex(coords, index);
		return index;
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.data.Region#getBounds(kovacs.util.data.GridImage)
	 */
	@Override
	public IndexBounds2D getBounds(GridImage2D<CoordinateType> image) {
		Vector2D centerIndex = getIndex(image.getGrid());
		IndexBounds2D bounds = new IndexBounds2D();
		Vector2D resolution = image.getResolution();
		double deltaX = radius.value() / resolution.x();
		double deltaY = radius.value() / resolution.y();
		
		bounds.fromi = Math.max(0, (int)Math.floor(centerIndex.x() - deltaX));
		bounds.toi = Math.min(image.sizeX()-1, (int)Math.ceil(centerIndex.x() + deltaX));
		bounds.fromj = Math.max(0, (int)Math.floor(centerIndex.y() - deltaY));
		bounds.toj = Math.min(image.sizeY()-1, (int)Math.ceil(centerIndex.y() + deltaY));
		
		return bounds;
	}
	
	/**
	 * Gets the bounds.
	 *
	 * @param image the image
	 * @param r the r
	 * @return the bounds
	 */
	public IndexBounds2D getBounds(GridImage2D<CoordinateType> image, double r) {
		DataPoint origRadius = getRadius();
		setRadius(new DataPoint(r, 0.0));
		IndexBounds2D bounds = getBounds(image);
		setRadius(origRadius);
		return bounds;
	}

	/**
	 * Distance to.
	 *
	 * @param pos the pos
	 * @return the double
	 */
	public double distanceTo(Metric<CoordinateType> pos) {
		return pos.distanceTo(coords);
	}

	/* (non-Javadoc)
	 * @see kovacs.util.data.Region#isInside(kovacs.util.data.Grid2D, double, double)
	 */
	@Override
	public boolean isInside(Grid2D<CoordinateType> grid, double i, double j) {
		Vector2D centerIndex = getIndex(grid);
		return ExtraMath.hypot(centerIndex.x() - i, centerIndex.y() - j) <= radius.value();
	}
	
	/**
	 * Move to peak.
	 *
	 * @param map the map
	 * @throws IllegalStateException the illegal state exception
	 */
	public void moveToPeak(GridImage2D<CoordinateType> map) throws IllegalStateException {
		IndexBounds2D bounds = getBounds(map);
		Vector2D centerIndex = getIndex(map.getGrid());
		
		if(!map.containsIndex(centerIndex.x(), centerIndex.y())) throw new IllegalStateException("Region falls outside of map.");
		Index2D index = new Index2D(centerIndex);
		
		double significance = map.getS2N(index.i(), index.j());
		
		for(int i=bounds.fromi; i<=bounds.toi; i++) for(int j=bounds.fromj; j<=bounds.toj; j++) 
			if(map.isUnflagged(i, j)) if(map.getS2N(i,j) > significance) if(isInside(map.getGrid(), i, j)) {
				significance = map.getS2N(i,j);
				index.set(i, j);			
			}
		
		if(map.isFlagged(index.i(), index.j())) throw new IllegalStateException("No valid peak in search area. ");
		
		centerIndex.setX(index.i());
		centerIndex.setY(index.j());
		
		if(isInside(map.getGrid(), index.i()+1, index.j())) if(isInside(map.getGrid(), index.i()-1, index.j())) 
			if(isInside(map.getGrid(), index.i(), index.j()+1)) if(isInside(map.getGrid(), index.i(), index.j()-1)) finetunePeak(map);
	}
	
	/**
	 * Finetune peak.
	 *
	 * @param map the map
	 * @return the data point
	 */
	public DataPoint finetunePeak(GridImage2D<CoordinateType> map) {
		Vector2D centerIndex = getIndex(map.getGrid());
		Data2D.InterpolatorData ipolData = new Data2D.InterpolatorData();
		
		int i = (int) Math.round(centerIndex.x());
		int j = (int) Math.round(centerIndex.y());
		
		double a=0.0,b=0.0,c=0.0,d=0.0;
			
		double y0 = map.getS2N(i,j);
	
		if(i>0 && i<map.sizeX()-1) if((map.getFlag(i+1, j) | map.getFlag(i-1, j)) == 0) {
			a = 0.5 * (map.getS2N(i+1,j) + map.getS2N(i-1,j)) - y0;
			c = 0.5 * (map.getS2N(i+1,j) - map.getS2N(i-1,j));
		}
		
		if(j>0 && j<map.sizeY()-1) if((map.getFlag(i, j+1) | map.getFlag(i, j-1)) == 0) {
			b = 0.5 * (map.getS2N(i,j+1) + map.getS2N(i,j-1)) - y0;
			d = 0.5 * (map.getS2N(i,j+1) - map.getS2N(i,j-1));
		}
		
		double di = (a == 0.0) ? 0.0 : -0.5*c/a;
		double dj = (b == 0.0) ? 0.0 : -0.5*d/b;	
		
		if(Math.abs(di) > 0.5) di = 0.0;
		if(Math.abs(dj) > 0.5) dj = 0.0;
		
		final double significance = y0 + (a*di + c)*di + (b*dj + d)*dj;			
		
		if(Math.abs(di) > 0.5 || Math.abs(dj) > 0.5) 
			throw new IllegalStateException("Position is not an S/N peak.");
		
		centerIndex.setX(i + di);
		centerIndex.setY(j + dj);
		
		moveTo(map, centerIndex);
		
		double peak = map.valueAtIndex(i+di, j+dj, ipolData);
	
		return new DataPoint(peak, peak / significance);
	}

	/**
	 * Move to.
	 *
	 * @param image the image
	 * @param index the index
	 */
	protected void moveTo(GridImage2D<CoordinateType> image, Vector2D index) {
		image.getGrid().getCoords(index, coords);
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.data.Region#toString(kovacs.util.data.GridImage)
	 */
	@Override
	public String toString(GridImage2D<CoordinateType> image) {
		return toString(image, FORMAT_CRUSH);
	}
	
	/**
	 * To string.
	 *
	 * @param image the image
	 * @param format the format
	 * @return the string
	 */
	public String toString(GridImage2D<CoordinateType> image, int format) {
		String line = null;
		
		switch(format) {
		case FORMAT_CRUSH : line = toCrushString(image); break;
		case FORMAT_OFFSET : line = toOffsetString(image); break;
		case FORMAT_GREG : line = toGregString(image); break;
		case FORMAT_DS9 : line = toDS9String(image); break;
		}
		
		String comment = getComment();
		if(comment.length() > 0) line += "\t#" + comment; 
		
		return line;
	}
	
	/**
	 * Gets the coordinates.
	 *
	 * @return the coordinates
	 */
	public CoordinateType getCoordinates() {
		return coords;
	}
	
	/**
	 * Sets the coordinates.
	 *
	 * @param coords the new coordinates
	 */
	public void setCoordinates(CoordinateType coords) {
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
	
	/**
	 * To crush string.
	 *
	 * @param image the image
	 * @return the string
	 */
	public String toCrushString(GridImage2D<CoordinateType> image) {
		CoordinateType coords = getCoordinates();	
		
		if(coords instanceof SphericalCoordinates) {
			SphericalCoordinates spherical = (SphericalCoordinates) coords;
			CoordinateSystem axes = spherical.getCoordinateSystem();
			((AngleFormat) axes.get(0).format).colons();
			((AngleFormat) axes.get(1).format).colons();
			return getID() + "\t" + coords.toString() + "  " + Util.f1.format(radius.value()/Unit.arcsec) + " # " + getComment();
		}
		else return getID() + "\t" + coords.x() + "\t" + coords.y() + "\t" + radius.value() + "\t# " + getComment();
	}

	/**
	 * To greg string.
	 *
	 * @param image the image
	 * @return the string
	 */
	public String toGregString(GridImage2D<CoordinateType> image) {
		Coordinate2D offset = new Coordinate2D();
		image.getProjection().project(coords, offset);
		
		return "ellipse " + Util.f1.format(radius.value()/Unit.arcsec) + " /user " +
		Util.f1.format(offset.x() / Unit.arcsec) + " " + Util.f1.format(offset.y() / Unit.arcsec);
	}
	
	/**
	 * To d s9 string.
	 *
	 * @param image the image
	 * @return the string
	 */
	public String toDS9String(GridImage2D<CoordinateType> image) {
		CoordinateType coords = getCoordinates();
	
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
	
	
	/**
	 * To offset string.
	 *
	 * @param image the image
	 * @return the string
	 */
	public String toOffsetString(GridImage2D<CoordinateType> image) {
		Vector2D offset = new Vector2D();
		image.getProjection().project(coords, offset);
	
		if(coords instanceof SphericalCoordinates) {
			SphericalCoordinates reference = (SphericalCoordinates) image.getReference();
			CoordinateSystem axes = reference.getLocalCoordinateSystem();
			CoordinateAxis x = axes.get(0);
			CoordinateAxis y = axes.get(1);
		
			return x.label + " = " + x.format(offset.x()) + "\t" + y.label + " = " + y.format(offset.y());
		}
		else return "dx = " + offset.x() + "\tdy = " + offset.y();
	}

	
	/* (non-Javadoc)
	 * @see kovacs.util.data.Region#parse(java.lang.String, int, kovacs.util.data.GridImage)
	 */
	@Override
	public void parse(String line, int format, GridImage2D<CoordinateType> forImage) throws ParseException {	
		switch(format) {
		case FORMAT_CRUSH : parseCrush(line, forImage); break;
		//case FORMAT_OFFSET : parseOffset(line); break;
		case FORMAT_GREG : parseGreg(line, forImage); break;
		case FORMAT_DS9 : parseDS9(line, forImage); break;
		}
	}
	
	/**
	 * Parses the crush.
	 *
	 * @param line the line
	 * @param forImage the for image
	 * @return the string tokenizer
	 */
	public StringTokenizer parseCrush(String line, GridImage2D<CoordinateType> forImage) {
		CoordinateType coords = getCoordinates();
		
		StringTokenizer tokens = new StringTokenizer(line);
		setID(tokens.nextToken());
		
		if(coords instanceof SphericalCoordinates) {
			SphericalCoordinates spherical = (SphericalCoordinates) coords;
			CoordinateSystem axes = spherical.getCoordinateSystem();
			((AngleFormat) axes.get(0).format).colons();
			((AngleFormat) axes.get(1).format).colons();
		
			coords.parse(tokens.nextToken() + " " + tokens.nextToken() + " " + tokens.nextToken());
			setCoordinates(coords);
			setRadius(Double.parseDouble(tokens.nextToken()) * Unit.arcsec);
		}
		else {
			coords.setX(Double.parseDouble(tokens.nextToken()));
			coords.setY(Double.parseDouble(tokens.nextToken()));			
		}
		
		radius.setWeight(0.0);	
		
		if(line.contains("#")) setComment(line.substring(line.indexOf('#') + 2));
	
		return tokens;
	}

	/**
	 * Parses the greg.
	 *
	 * @param line the line
	 * @param forImage the for image
	 */
	public void parseGreg(String line, GridImage2D<CoordinateType> forImage) {
		StringTokenizer tokens = new StringTokenizer(line);
		if(!tokens.nextToken().equalsIgnoreCase("ellipse"))
			throw new IllegalArgumentException("WARNING! " + getClass().getSimpleName() + " can parse 'ellipse' only.");
		
		setRadius(Double.parseDouble(tokens.nextToken()) * Unit.arcsec);

		// TODO What if not '/user' coordinates?
		tokens.nextToken(); // Assumed to be '/user';
		
		Vector2D centerIndex = new Vector2D();
		centerIndex.setX(-Double.parseDouble(tokens.nextToken()) * Unit.arcsec);
		centerIndex.setY(Double.parseDouble(tokens.nextToken()) * Unit.arcsec);
		
		forImage.getGrid().getCoords(centerIndex, coords);
	}
	
	/**
	 * Parses the d s9.
	 *
	 * @param line the line
	 * @param forImage the for image
	 */
	public void parseDS9(String line, GridImage2D<CoordinateType> forImage) {	
		CoordinateType coords = getCoordinates();
	
		StringTokenizer tokens = new StringTokenizer(line, "(), \t");
		boolean isCircle = tokens.nextToken().equalsIgnoreCase("circle");
		
		if(coords instanceof SphericalCoordinates) {
			SphericalCoordinates spherical = (SphericalCoordinates) coords;
			CoordinateSystem axes = spherical.getCoordinateSystem();
			((AngleFormat) axes.get(0).format).colons();
			((AngleFormat) axes.get(1).format).colons();
	
			coords.parse(tokens.nextToken() + " " + tokens.nextToken() + " (J2000)");
		}
		else {
			coords.setX(Double.parseDouble(tokens.nextToken()));
			coords.setY(Double.parseDouble(tokens.nextToken()));
		}
				
		if(isCircle) {
			String R = tokens.nextToken();
			char unit = R.charAt(R.length() - 1);
			setRadius(Double.parseDouble(R.substring(0, R.length()-1)));
			if(unit == '\'') radius.scale(Unit.arcmin);
			else if(unit == '"') radius.scale(Unit.arcsec);
		}
		else setRadius(Double.NaN);
	}
	
	/**
	 * Gets the asymmetry.
	 *
	 * @param map the map
	 * @param angle the angle
	 * @param minr the minr
	 * @param maxr the maxr
	 * @return the asymmetry
	 */
	public DataPoint getAsymmetry(GridImage2D<CoordinateType> map, double angle, double minr, double maxr) {	
		Vector2D center = getIndex(map.getGrid());
		IndexBounds2D bounds = getBounds(map, maxr);
		Vector2D delta = map.getResolution();
		
		double m0 = 0.0, mc = 0.0, c2 = 0.0;
		
		for(int i=bounds.fromi; i<=bounds.toi; i++) for(int j=bounds.fromj; j<=bounds.toj; j++) if(map.isUnflagged(i, j)) {
			double dx = (i - center.x()) * delta.x();
			double dy = (j - center.y()) * delta.y();
			double r = ExtraMath.hypot(dx, dy);
			
			if(r > maxr) continue;
			
			double w = map.getWeight(i, j);
			double wp = w * map.getValue(i,j);
			
			m0 += Math.abs(wp);
			
			if(r < minr) continue;
			
			double theta = Math.atan2(dy, dx);
			
			// cos term is gain-like
			double c = Math.cos(theta - angle);
			
			mc += wp * c;
			c2 += w * c * c;
			
		}
		if(m0 > 0.0) return new DataPoint(mc / m0, Math.sqrt(c2) / m0);
		return new DataPoint();	
	}
	

	// Increase the aperture until it captures >98% of the flux
	// Then estimate the spread by comparing the peak flux to the integrated flux
	// with an equivalent Gaussian source profile...
	/**
	 * Gets the adaptive integral.
	 *
	 * @param map the map
	 * @return the adaptive integral
	 */
	public WeightedPoint getAdaptiveIntegral(GridImage2D<CoordinateType> map) {
		double origRadius = getRadius().value();
		WeightedPoint I = getIntegral(map);

		// 20 iterations on 20% increases covers ~40-fold increase in radius
		// Should be plenty even for a very large pointing source...
		for(int i=0; i<20; i++) {
			// A 20% increase in radius is ~40% increase in area.
			// Look for less than 5% change in amplitude --> 0.05*0.4 == 0.02 --> 2% change in integral
			getRadius().scaleValue(1.2);
			WeightedPoint I1 = getIntegral(map);
			if(I1.value() > 1.005 * I.value()) I = I1;
			else break;
		}
		
		getRadius().setValue(origRadius);
		return I;
	}
	

	/* (non-Javadoc)
	 * @see kovacs.util.text.TableFormatter.Entries#getFormattedEntry(java.lang.String, java.lang.String)
	 */
	@Override
	public String getFormattedEntry(String name, String formatSpec) {
		NumberFormat nf = TableFormatter.getNumberFormat(formatSpec);
		
		if(name.equals("r")) return nf.format(radius.value());
		else if(name.equals("dr")) return nf.format(radius.rms());
		if(name.equals("dr")) return nf.format(radius.rms());
		else return TableFormatter.NO_SUCH_DATA;
		
	}
}

