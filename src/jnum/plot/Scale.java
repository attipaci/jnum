/* *****************************************************************************
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
 *     Attila Kovacs  - initial API and implementation
 ******************************************************************************/
// Copyright (c) 2010 Attila Kovacs 

package jnum.plot;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;

import jnum.math.Range;

// TODO Better handling of custom divisions (all vs within range)

public class Scale extends ArrayList<Scale.Division> implements Cloneable {

	private static final long serialVersionUID = 5612144764133920964L;

	private Range range = new Range();

	private int type = LINEAR;

	private int overSampling = 1;	
	

	public Scale () {}
	

	public Scale (int oversampling) { this.overSampling = oversampling; }
		

	public void linear() { type = LINEAR; update(); }
	

	public void log() { type = LOGARITHMIC; update(); }
	

	public void sqrt() { type = SQRT; update(); }
	

	public void power() { type = POWER; update(); }
	
	
	public boolean isCustom() { return type == CUSTOM; }
	

	public boolean isLinear() { return type == LINEAR; }
	

	public boolean isLogarithmic() { return type == LOGARITHMIC; }
	

	public boolean isSqrt() { return type == SQRT; }


	public boolean isPower() { return type == POWER; }
	

	public int overSample(int n) { 
		if(overSampling != n) {
			overSampling = Math.min(1, n);
			update();
		}
		return overSampling;
	}
	

	public int getOverSampling() { return overSampling; }
		
	
	public void addDivisions(Collection<Double> divs) {
		type = CUSTOM;
		for(double level : divs) add(new Division(level));
	}


	public void update() {
		if(range == null) return;
		update(range);
	}
	

	public void update(Range range) {
		update(range.min(), range.max());
	}
	

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
	

	public class Division implements Comparable<Scale.Division> {

		private double value;

		private String label;

		private NumberFormat nf;
		

		public Division(double value) {
			this.value = value;
		}
		

		public Division(double value, NumberFormat nf) {
			this(value);
			setNumberFormat(nf);
		}
		

		public Division(double value, String label) {
			this(value);
			setLabel(label);
		}
		

		public void setLabel(String text) { label = text; nf = null; }
		

		public void setNumberFormat(NumberFormat nf) { this.nf = nf; label = null; } 
		

		public double getValue() { return value; }

		@Override
		public String toString() {
			if(label != null) return label;
			if(value == 0.0) return "0";
			if(nf != null) return nf.format(value);
			return Double.toString(value);
		}

		@Override
		public int compareTo(Division div) {
			return Double.compare(getValue(), div.getValue());
		}
	
	}

	private final static int CUSTOM = -1;

	private final static int LINEAR = 0;

	private final static int LOGARITHMIC = 1;

	private final static int SQRT = 2;

	private final static int POWER = 3;
}
