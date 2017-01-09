/*******************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
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
 *     Attila Kovacs <attila_kovacs[AT]post.harvard.edu> - initial API and implementation
 ******************************************************************************/
package jnum.plot;

import java.awt.Graphics;

import jnum.Util;
import jnum.math.Cartesian;
import jnum.math.Range;
import jnum.plot.colorscheme.GreyScale;



// TODO: Auto-generated Javadoc
/**
 * The Class ImageLayer.
 */
public abstract class ImageLayer extends ContentLayer {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1451020511179557736L;
	
	/** The color scheme. */
	private ColorScheme colorScheme = new GreyScale();
	
	/** The range. */
	private Range range, logRange = new Range(), sqrtRange = new Range();
	
	/** The scaling. */
	private int scaling = SCALE_LINEAR;
	

	/** The verbose. */
	protected boolean verbose = false;	
	
	
	/* (non-Javadoc)
	 * @see kovacs.plot.PlotLayer#setContentArea(kovacs.plot.ContentArea)
	 */
	@Override
	public void setContentArea(ContentArea<?> area) {
		super.setContentArea(area);
		area.coordinateSystem = new Cartesian(2);
	}
	
	

	/**
	 * Sets the scaling.
	 *
	 * @param value the new scaling
	 */
	public void setScaling(int value) { scaling = value; }
	
	/**
	 * Gets the scaling.
	 *
	 * @return the scaling
	 */
	public int getScaling() { return scaling; }
	
	
	/**
	 * Gets the scaled.
	 *
	 * @param value the value
	 * @return the scaled
	 */
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
	
	/**
	 * Gets the rgb.
	 *
	 * @param value the value
	 * @return the rgb
	 */
	public int getRGB(double value) {
		return java.lang.Double.isNaN(value) ? colorScheme.noData : colorScheme.getRGB(getScaled(value));
	}
	
	
	/* (non-Javadoc)
	 * @see jnum.plot.PlotLayer#paintComponent(java.awt.Graphics)
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		//System.err.println("### layer " + getSize());
		drawImage(g);
	}
	
	/**
	 * Draw image.
	 *
	 * @param g the g
	 */
	protected abstract void drawImage(Graphics g);
	
	/**
	 * Gets the data range.
	 *
	 * @return the data range
	 */
	public abstract Range getDataRange();
	
	/**
	 * Autoscale.
	 */
	public void autoscale() {
		setRange(getDataRange());
		if(verbose) Util.info(this, "Setting scale to " + range);
	}
	

	/* (non-Javadoc)
	 * @see jnum.plot.PlotLayer#defaults()
	 */
	@Override
	public void defaults() {
		autoscale();
	}
		
	
	/**
	 * Gets the color scheme.
	 *
	 * @return the color scheme
	 */
	public ColorScheme getColorScheme() { return colorScheme; }
	
	/**
	 * Sets the color scheme.
	 *
	 * @param scheme the new color scheme
	 */
	public void setColorScheme(ColorScheme scheme) { this.colorScheme = scheme; }
	
	/**
	 * Gets the range.
	 *
	 * @return the range
	 */
	public Range getRange() { return range; }
	
	/**
	 * Sets the range.
	 *
	 * @param r the new range
	 */
	public void setRange(Range r) { 
		this.range = r; 
		logRange.setRange(0.1 * Math.log(Math.abs(r.min())), Math.log(Math.abs(r.max())));
		sqrtRange.setRange(Math.signum(r.min()) * Math.sqrt(Math.abs(r.min())), Math.signum(r.max()) * Math.log(Math.abs(r.max())));
	}
	
	/**
	 * Checks if is verbose.
	 *
	 * @return true, if is verbose
	 */
	public boolean isVerbose() { return verbose; }
	
	/**
	 * Sets the verbose.
	 *
	 * @param value the new verbose
	 */
	public void setVerbose(boolean value) { verbose = value; }
	
	




	/** The Constant SCALE_LINEAR. */
	public final static int SCALE_LINEAR = 0;
	
	/** The Constant SCALE_LOG. */
	public final static int SCALE_LOG = 1;
	
	/** The Constant SCALE_SQRT. */
	public final static int SCALE_SQRT = 2;
	
}
