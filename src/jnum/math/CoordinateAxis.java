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

package jnum.math;

import java.io.Serializable;
import java.text.NumberFormat;
import java.text.ParseException;

import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;
import nom.tam.util.Cursor;

// TODO: Auto-generated Javadoc

/**
 * The Class CoordinateAxis.
 */
public class CoordinateAxis implements Serializable, Cloneable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7273239690459736139L;

	/** The label. */
	public String label;
		
	/** The format. */
	public NumberFormat format;
	
	/** The reverse. */
	public boolean reverse = false;
	
	/** The reverse from. */
	public double reverseFrom = 0.0;
	
	/** The multiples. */
	public double[] multiples; // The multiples of the fundamental tickunits that can be used.
	
	/** The magnitude scaling. */
	public boolean magnitudeScaling = true; // If the ticks can be scaled by powers of 10 also.
	
	/** The minor tick. */
	public double majorTick, minorTick; // The actual ticks.
	
	/**
	 * Instantiates a new coordinate axis.
	 */
	public CoordinateAxis() { this("unspecified axis"); }

	/**
	 * Instantiates a new coordinate axis.
	 *
	 * @param text the text
	 */
	public CoordinateAxis(String text) { 
		defaults();
		setLabel(text); 
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() { 
		try { return super.clone(); }
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

	/**
	 * Sets the label.
	 *
	 * @param text the new label
	 */
	public void setLabel(String text) { label = text; }

	/**
	 * Gets the label.
	 *
	 * @return the label
	 */
	public String getLabel() { return label; }

	/**
	 * Sets the format.
	 *
	 * @param nf the new format
	 */
	public void setFormat(NumberFormat nf) { format = nf; }
	
	/**
	 * Gets the format.
	 *
	 * @return the format
	 */
	public NumberFormat getFormat() { return format; }
	
	/**
	 * Sets the reverse.
	 *
	 * @param value the new reverse
	 */
	public void setReverse(boolean value) { setReverse(value, 0.0); }
	
	/**
	 * Sets the reverse.
	 *
	 * @param value the value
	 * @param from the from
	 */
	public void setReverse(boolean value, double from) { reverse = value; reverseFrom = from; }
	
	/**
	 * Checks if is reverse.
	 *
	 * @return true, if is reverse
	 */
	public boolean isReverse() { return reverse; }
	
	/**
	 * Format.
	 *
	 * @param value the value
	 * @return the string
	 */
	public String format(double value) { return format.format(reverse ? reverseFrom - value : value); }
	
	public double parse(String text) throws ParseException {
		double value = format.parse(text).doubleValue();
		return reverse ? reverseFrom - value : value;
	}
	
	/**
	 * Edits the.
	 *
	 * @param cursor the cursor
	 * @param id the id
	 * @throws HeaderCardException the header card exception
	 */
	public void editHeader(Cursor<String, HeaderCard> cursor, String id) throws HeaderCardException {
		if(label != null) cursor.add(new HeaderCard("CNAME" + id, label, "Coordinate axis name."));
	}
	
	// TODO
	// does not read label and format information...
	// should use getDefaults("wcsName")?
	/**
	 * Parses the.
	 *
	 * @param header the header
	 * @param id the id
	 */
	public void parse(Header header, String id) {
		if(header.containsKey("CNAME" + id)) label = header.getStringValue("CNAME" + id);
	}
	
}

