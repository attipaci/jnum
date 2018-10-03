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
// Copyright (c) 2010 Attila Kovacs 

package jnum.math;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;

// TODO: Auto-generated Javadoc
// TODO Better handling of custom divisions (all vs within range)

/**
 * The Class ScaleDivisions.
 */
public class Scale extends ArrayList<Scale.Division> implements Cloneable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 5612144764133920964L;

	/** The range. */
	private Range range = new Range();
	
	/** The type. */
	private int type = LINEAR;
	
	/** The over sampling. */
	private int overSampling = 1;	
	
	/**
	 * Instantiates a new scale divisions.
	 */
	public Scale () {}
	
	/**
	 * Instantiates a new scale divisions.
	 *
	 * @param oversampling the oversampling
	 */
	public Scale (int oversampling) { this.overSampling = oversampling; }
	
		
	/**
	 * Linear.
	 */
	public void linear() { type = LINEAR; update(); }
	
	/**
	 * Log.
	 */
	public void log() { type = LOGARITHMIC; update(); }
	
	/**
	 * Sqrt.
	 */
	public void sqrt() { type = SQRT; update(); }
	
	/**
	 * Power.
	 */
	public void power() { type = POWER; update(); }
	
	
	/**
	 * Checks if is custom.
	 *
	 * @return true, if is custom
	 */
	public boolean isCustom() { return type == CUSTOM; }
	
	/**
	 * Checks if is linear.
	 *
	 * @return true, if is linear
	 */
	public boolean isLinear() { return type == LINEAR; }
	
	/**
	 * Checks if is logarithmic.
	 *
	 * @return true, if is logarithmic
	 */
	public boolean isLogarithmic() { return type == LOGARITHMIC; }
	
	/**
	 * Checks if is sqrt.
	 *
	 * @return true, if is sqrt
	 */
	public boolean isSqrt() { return type == SQRT; }

	/**
	 * Checks if is power.
	 *
	 * @return true, if is power
	 */
	public boolean isPower() { return type == POWER; }
	
	/**
	 * Over sample.
	 *
	 * @param n the n
	 * @return the int
	 */
	public int overSample(int n) { 
		if(overSampling != n) {
			overSampling = Math.min(1, n);
			update();
		}
		return overSampling;
	}
	
	/**
	 * Gets the over sampling.
	 *
	 * @return the over sampling
	 */
	public int getOverSampling() { return overSampling; }
		
	
	/**
	 * Adds the divisions.
	 *
	 * @param divs the divs
	 */
	public void addDivisions(Collection<Double> divs) {
		type = CUSTOM;
		for(double level : divs) add(new Division(level));
	}

	/**
	 * Update.
	 */
	public void update() {
		if(range == null) return;
		update(range);
	}
	
	/**
	 * Update.
	 *
	 * @param range the range
	 */
	public void update(Range range) {
		update(range.min(), range.max());
	}
	
	
	/**
	 * Update.
	 *
	 * @param setmin the setmin
	 * @param setmax the setmax
	 */
	public void update(double setmin, double setmax) {
		range.setRange(setmin, setmax);
		
		if(isCustom()) return;
		
		clear();
		
		if(isLogarithmic()) {
			final int from = (int) Math.floor(Math.log10(Math.abs(range.min())));
			final int to = (int) Math.ceil(Math.log10(Math.abs(range.max())));

			final double increment = Math.pow(10.0, 1.0 / overSampling);
			double level = Math.pow(10.0, from);
			
			for(int order = from; order <= to; order++) {
				add(new Division(level));
				level *= increment;
			}	
		}
		else {
			final int order = (int)Math.floor(Math.log10(range.span() / 3.0));	
			
			// Snap to the nearest big division (no oversampling)
			double div = Math.pow(10.0, order);
			int fromi = (int) Math.floor(range.min() / div);
			int toi = (int) Math.ceil(range.max() / div);
			double level = fromi * div;
			
			// Then oversample...
			fromi *= overSampling;
			toi *= overSampling;
			div /= overSampling;
			
			for(int i=fromi; i<=toi; i++) {
				if(Math.abs(level) < 0.001 * div) level = 0.0;
				add(new Division(level));
				level += div;
			}
			
		}
	}
	
	
	/**
	 * The Class Division.
	 */
	public class Division implements Comparable<Scale.Division> {
		
		/** The value. */
		private double value;
		
		/** The label. */
		private String label;
		
		/** The nf. */
		private NumberFormat nf;
		
		/**
		 * Instantiates a new division.
		 *
		 * @param value the value
		 */
		public Division(double value) {
			this.value = value;
		}
		
		/**
		 * Instantiates a new division.
		 *
		 * @param value the value
		 * @param nf the nf
		 */
		public Division(double value, NumberFormat nf) {
			this(value);
			setNumberFormat(nf);
		}
		
		/**
		 * Instantiates a new division.
		 *
		 * @param value the value
		 * @param label the label
		 */
		public Division(double value, String label) {
			this(value);
			setLabel(label);
		}
		
		/**
		 * Sets the label.
		 *
		 * @param text the new label
		 */
		public void setLabel(String text) { label = text; nf = null; }
		
		/**
		 * Sets the number format.
		 *
		 * @param nf the new number format
		 */
		public void setNumberFormat(NumberFormat nf) { this.nf = nf; label = null; } 
		
		/**
		 * Gets the value.
		 *
		 * @return the value
		 */
		public double getValue() { return value; }
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			if(label != null) return label;
			if(value == 0.0) return "0";
			if(nf != null) return nf.format(value);
			return Double.toString(value);
		}

		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(Division div) {
			return Double.compare(getValue(), div.getValue());
		}
	
	}
	
	/** The Constant CUSTOM. */
	private final static int CUSTOM = -1;
	
	/** The Constant LINEAR. */
	private final static int LINEAR = 0;
	
	/** The Constant LOGARITHMIC. */
	private final static int LOGARITHMIC = 1;
	
	/** The Constant SQRT. */
	private final static int SQRT = 2;
	
	/** The Constant POWER. */
	private final static int POWER = 3;
}
