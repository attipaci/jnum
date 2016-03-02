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
package jnum.data;

import java.text.ParseException;

import jnum.ExtraMath;
import jnum.Unit;
import jnum.Util;
import jnum.math.Coordinate2D;
import jnum.math.Range;
import jnum.math.Vector2D;
import jnum.util.DataTable;


// TODO: Auto-generated Javadoc
/**
 * The Class EllipticalSource.
 *
 * @param <CoordinateType> the generic type
 */
public class EllipticalSource<CoordinateType extends Coordinate2D> extends GaussianSource<CoordinateType> {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -7816545856774075826L;

	/** The elongation. */
	private DataPoint elongation = new DataPoint();
	
	/** The angle. */
	private DataPoint angle = new DataPoint();
	
	/**
	 * Instantiates a new elliptical source.
	 */
	public EllipticalSource() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Instantiates a new elliptical source.
	 *
	 * @param map the map
	 * @param offset the offset
	 * @param a the a
	 * @param b the b
	 * @param angle the angle
	 */
	public EllipticalSource(GridImage2D<CoordinateType> map, Vector2D offset, double a, double b, double angle) {
		super(map, offset, 0.5*(a+b));
		elongation.setValue((a-b) / (a+b));
		this.angle.setValue(angle);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Instantiates a new elliptical source.
	 *
	 * @param coords the coords
	 * @param a the a
	 * @param b the b
	 * @param angle the angle
	 */
	public EllipticalSource(CoordinateType coords, double a, double b, double angle) {
		super(coords, 0.5*(a+b));
		elongation.setValue((a-b) / (a+b));
		this.angle.setValue(angle);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Instantiates a new elliptical source.
	 *
	 * @param line the line
	 * @param format the format
	 * @param forImage the for image
	 * @throws ParseException the parse exception
	 */
	public EllipticalSource(String line, int format, GridImage2D<CoordinateType> forImage) throws ParseException {
		super(line, format, forImage);
		// TODO Auto-generated constructor stub
	}
	
	/* (non-Javadoc)
	 * @see jnum.data.GaussianSource#hashCode()
	 */
	@Override
	public int hashCode() { 
		int hash = super.hashCode();
		if(angle != null) hash ^= angle.hashCode();
		if(elongation != null) hash ^= elongation.hashCode();
		return hash;
	}
	
	/* (non-Javadoc)
	 * @see jnum.data.GaussianSource#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if(o == this) return true;
		if(!(o instanceof EllipticalSource)) return false;
		if(!super.equals(o)) return false;
		EllipticalSource<?> e = (EllipticalSource<?>) o;
		if(!Util.equals(angle, e.angle)) return false;
		if(!Util.equals(elongation, e.elongation)) return false;
		return true;
	}
	
	
	/**
	 * Gets the elongation.
	 *
	 * @return the elongation
	 */
	public DataPoint getElongation() { return elongation; }
	
	/**
	 * Gets the angle.
	 *
	 * @return the angle
	 */
	public DataPoint getAngle() { return angle; }
	
	/* (non-Javadoc)
	 * @see kovacs.util.data.GaussianSource#measureShape(kovacs.util.data.GridImage)
	 */
	@Override
	public void measureShape(GridImage2D<CoordinateType> map) {	
		super.measureShape(map);
		
		// TODO elliptical image beam...
		double maxr = 1.5 * map.getImageBeam().getCircularEquivalentFWHM();
		
		Grid2D<CoordinateType> grid = map.getGrid();
		Vector2D center = getIndex(grid);
		Vector2D delta = map.getResolution();
		IndexBounds2D bounds = getBounds(map, maxr);
		
		
		double m0 = 0.0, m2c = 0.0, m2s = 0.0, sumw = 0.0;
		
		for(int i=bounds.fromi; i<=bounds.toi; i++) for(int j=bounds.fromj; j<=bounds.toj; j++) if(map.isUnflagged(i, j)) {
			double w = map.getWeight(i, j);
			double wp = w * map.getValue(i,j);
			double dx = (i - center.x()) / delta.x();
			double dy = (j - center.y()) / delta.y();
			double theta = 2.0 * Math.atan2(dy, dx);
			
			double r = ExtraMath.hypot(dx * delta.x(), dy * delta.y());
			if(r < maxr) continue;
			
			double c = Math.cos(theta);
			double s = Math.sin(theta);
			
			m2c += wp * c;
			m2s += wp * s;
			m0 += Math.abs(wp);
			
			sumw += w;
		}
		if(m0 > 0.0) {
			m2c *= 1.0 / m0;
			m2s *= 1.0 / m0;
			
			elongation.setValue(2.0 * ExtraMath.hypot(m2s, m2c));
			elongation.setRMS(Math.sqrt(sumw) / m0);
			
			angle.setValue(0.5 * Math.atan2(m2s, m2c));
			angle.setRMS(elongation.rms() / elongation.value());
		}
		else {
			angle.noData();
			elongation.noData();
		}
	}
	
	/**
	 * Gets the axes.
	 *
	 * @return the axes
	 */
	public Range getAxes() {
		Range axes = new Range();
		getAxes(axes);
		return axes;
	}
	
	/**
	 * Gets the axes.
	 *
	 * @param axes the axes
	 * @return the axes
	 */
	public void getAxes(Range axes) {
		axes.setMin(getRadius().value() * (1.0 - elongation.value()));
		axes.setMax(getRadius().value() * (1.0 + elongation.value()));
		// Renormalize to keep area unchanged...
		axes.scale(1.0 / (1.0 - elongation.value() * elongation.value()));	
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.data.GaussianSource#getData(kovacs.util.data.GridImage, kovacs.util.Unit)
	 */
	@Override
	public DataTable getData(GridImage2D<CoordinateType> map, Unit sizeUnit) {
		DataTable data = super.getData(map, sizeUnit);
		Range axes = getAxes();
		
		double da = getRadius().weight() > 0.0 ? ExtraMath.hypot(getRadius().rms(), axes.max() * elongation.rms()) : Double.NaN;
		double db = getRadius().weight() > 0.0 ? ExtraMath.hypot(getRadius().rms(), axes.min() * elongation.rms()) : Double.NaN;
		
		data.new Entry("a", axes.max() / sizeUnit.value(), sizeUnit.name());
		data.new Entry("b", axes.min() / sizeUnit.value(), sizeUnit.name());
		data.new Entry("angle", angle.value() / sizeUnit.value(), sizeUnit.name());
		data.new Entry("dangle", angle.rms() / sizeUnit.value(), sizeUnit.name());
		
		data.new Entry("da", da / sizeUnit.value(), sizeUnit.name());
		data.new Entry("db", db / sizeUnit.value(), sizeUnit.name());
		
		return data;
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.data.GaussianSource#pointingInfo(kovacs.util.data.GridImage, kovacs.util.Unit)
	 */
	@Override
	public String pointingInfo(GridImage2D<CoordinateType> map, Unit sizeUnit) {
		String info = super.pointingInfo(map, sizeUnit);
		Range axes = getAxes();
		
		double da = getRadius().weight() > 0.0 ? ExtraMath.hypot(getRadius().rms(), axes.max() * elongation.rms()) : Double.NaN;
		double db = getRadius().weight() > 0.0 ? ExtraMath.hypot(getRadius().rms(), axes.min() * elongation.rms()) : Double.NaN;
		
			
		info += " (a="
				+ Util.f1.format(axes.max() / sizeUnit.value()) + "+-" + Util.f1.format(da / sizeUnit.value()) 
				+ ", b=" 
				+ Util.f1.format(axes.min() / sizeUnit.value()) + "+-" + Util.f1.format(db / sizeUnit.value()) 
				+ ", angle="
				+ Util.d1.format(angle.value() / Unit.deg) + "+-" + Util.d1.format(angle.rms() / Unit.deg)
				+ " deg)";

		return info;
	}
	
	
	
	// TODO Override add...
	
}
