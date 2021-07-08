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
package jnum.plot;

import java.awt.Graphics;

import jnum.Util;
import jnum.math.CartesianSystem;
import jnum.math.Range;
import jnum.colorscheme.ColorScheme;
import jnum.colorscheme.GreyScale;



public abstract class ImageLayer extends ContentLayer {

	private static final long serialVersionUID = 1451020511179557736L;

	private ColorScheme colorScheme = new GreyScale();

	private Range range, logRange, sqrtRange;

	private int scaling = SCALE_LINEAR;
	
	protected boolean verbose = false;	
	
	
	public ImageLayer(){
	    range = new Range();
	    logRange = new Range();
	    sqrtRange = new Range();
	}

	@Override
	public void setContentArea(ContentArea<?> area) {
		super.setContentArea(area);
		area.coordinateSystem = new CartesianSystem(2);
	}
	
	
	public void setScaling(int value) { scaling = value; }
	
	public int getScaling() { return scaling; }
	
	protected double getScaled(double value) {
			
		switch(scaling) {
		case SCALE_LOG : {
			final double logValue = Math.log(value);
			return logValue > logRange.min() ? (Math.log(value) - logRange.min()) / logRange.span() : 0.0;
		}
		case SCALE_SQRT : return (Math.signum(value) * Math.sqrt(Math.abs(value)) - sqrtRange.min()) / sqrtRange.span();
		}
		return (value - range.min()) / range.span();	
	}
	

	public int getRGB(double value) {
		return Double.isNaN(value) ? colorScheme.noData : colorScheme.getRGB(getScaled(value));
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		//System.err.println("### layer " + getSize());
		drawImage(g);
	}

	
	protected abstract void drawImage(Graphics g);
	

	public abstract Range getDataRange();
	

	public void autoscale() {
		setRange(getDataRange());
		if(verbose) Util.info(this, "Setting scale to " + range);
	}
	
	@Override
	public void defaults() {
		autoscale();
	}
		
	
	public ColorScheme getColorScheme() { return colorScheme; }
	

	public void setColorScheme(ColorScheme scheme) { this.colorScheme = scheme; }
	

	public Range getRange() { return range; }
	

	public void setRange(Range r) {
		this.range.setRange(r.min(), r.max()); 
		logRange.setRange(0.1 * Math.log(Math.abs(r.min())), Math.log(Math.abs(r.max())));
		sqrtRange.setRange(Math.signum(r.min()) * Math.sqrt(Math.abs(r.min())), Math.signum(r.max()) * Math.log(Math.abs(r.max())));
	}
	

	public boolean isVerbose() { return verbose; }
	

	public void setVerbose(boolean value) { verbose = value; }
	
	

	public static final int SCALE_LINEAR = 0;

	public static final int SCALE_LOG = 1;

	public static final int SCALE_SQRT = 2;
	
}
