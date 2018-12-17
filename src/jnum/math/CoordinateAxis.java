/*******************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila[AT]sigmyne.com>.
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

package jnum.math;

import java.io.Serializable;
import java.text.NumberFormat;
import java.text.ParseException;

import jnum.Unit;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;
import nom.tam.util.Cursor;


public class CoordinateAxis implements Serializable, Cloneable {

	private static final long serialVersionUID = 7273239690459736139L;

    private String label;

	private String shortLabel;

	private String fancyLabel;

	public NumberFormat format;

	public boolean reverse = false;

	public double reverseFrom = 0.0;

	public double[] multiples; // The multiples of the fundamental tickunits that can be used.

	public boolean magnitudeScaling = true; // If the ticks can be scaled by powers of 10 also.

	public double majorTick, minorTick; // The actual ticks.
	
	public Unit unit = Unit.arbitrary;
	

	public CoordinateAxis() { this("unspecified axis"); }


	public CoordinateAxis(String longLabel) {
	    this(longLabel, null, null);
	}
	

	public CoordinateAxis(String longLabel, String shortLabel, String fancyLabel) { 
		defaults();
		setShortLabel(shortLabel);
		setFancyLabel(fancyLabel); 
		setLabel(longLabel); 
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public CoordinateAxis clone() { 
		try { return (CoordinateAxis) super.clone(); }
		catch(CloneNotSupportedException e) { return null; }
	}
	
	/**
	 * Defaults.
	 */
	public void defaults() {
		reverse = false;
		multiples = new double[] { 1.0, 2.0, 5.0 }; // The multiples of the fundamental tickunits that can be used.
		magnitudeScaling = true;
	}


	public void setLabel(String text) { label = text; }
	

	public void setShortLabel(String text) { shortLabel = text; }


	public void setFancyLabel(String text) { fancyLabel = text; }
	

	public String getLabel() { return label; }
	

	public String getShortLabel() { return shortLabel == null ? getLabel() : shortLabel; }
	

	public String getFancyLabel() { return fancyLabel == null ? getLabel() : fancyLabel; }


	public void setFormat(NumberFormat nf) { format = nf; }
	

	public NumberFormat getFormat() { return format; }
	

	public void setReverse(boolean value) { setReverse(value, 0.0); }
	

	public void setReverse(boolean value, double from) { reverse = value; reverseFrom = from; }
	

	public boolean isReverse() { return reverse; }
	
	public Unit getUnit() { return unit; }
	
	public void setUnit(Unit u) { this.unit = u; }
	
	

	public String format(double value) { return format.format(reverse ? reverseFrom - value : value); }
	

	public double parse(String text) throws ParseException {
		double value = format.parse(text).doubleValue();
		return reverse ? reverseFrom - value : value;
	}
	

	public void editHeader(Cursor<String, HeaderCard> cursor, String id) throws HeaderCardException {
		if(shortLabel != null) cursor.add(new HeaderCard("CNAME" + id, shortLabel, "Coordinate axis name."));
	}
	
	// TODO
	// does not read label and format information...
	// should use getDefaults("wcsName")?
	public void parse(Header header, String id) {
		if(header.containsKey("CNAME" + id)) shortLabel = header.getStringValue("CNAME" + id);
	}
	
}

