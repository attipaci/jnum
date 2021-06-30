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
// Copyright (c) 2007 Attila Kovacs 

package jnum.plot;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.text.NumberFormat;
import java.util.ArrayList;

import javax.swing.JComponent;

import jnum.Unit;
import jnum.Util;
import jnum.math.Range;
import jnum.math.Vector2D;
import jnum.util.PrefixedUnit;


// TODO
//   * 4 orientations (H & V, with alignment...)
//   * update also content.getCoordinateLayer() if necessary for inward ticks
//   * Mouse over to draw grey ticks for dragging when outer ticks not drawn
//				-- inner ticks not redrawn until finished dragging)
//				-- move linked rulers (moveScale(double value))
//   * Mouse drag to rescale --> apply scale to content (content.getScale());
//							--> apply scale to linked rulers...
//
//	 * Right click to bring up scale dialog (also change units here...)
//	 * Click to change scale type (log or sqrt might be disabled)
//
//	 * Turn on/off numbering
//   * NumberFormatting
//   * Numbering orientation
//   * Numbering width...


public abstract class BasicRuler extends JComponent implements PlotSide {

	private static final long serialVersionUID = 228962325800810360L;

	private double min = -1.0, max = 1.0;

	private Scale mainDivisions, subDivisions;

	private int side = Plot.SIDE_UNDEFINED;

	protected PrefixedUnit unit;

	private TickMarks tickMarks;

	boolean isAutoMultiplier = true;
	
	
	public BasicRuler(int edge) {
		unit = new PrefixedUnit(Unit.unity);
		mainDivisions = new Scale(1);
		subDivisions = new Scale(10);
		tickMarks = new TickMarks();
		setSide(edge);
	}

	@Override
	public void setSide(int edge) {
		this.side = edge;
		tickMarks.setSide(edge);
	}
	
	@Override
	public int getSide() { return side; }

	public final Unit getUnit() { return unit; }

	public final void setUnit(Unit u) { 
		if(u instanceof PrefixedUnit) this.unit = (PrefixedUnit) u;
		else this.unit = new PrefixedUnit(u);
		setRange(min, max);
	}

	@Override
	public boolean isHorizontal() { return side == Plot.TOP_SIDE || side == Plot.BOTTOM_SIDE; }

	@Override
	public boolean isVertical() { return side == Plot.LEFT_SIDE || side == Plot.RIGHT_SIDE; }
	

	public TickMarks getTickMarks() { return tickMarks; }
	

	public Scale getMainDivisions() { return mainDivisions; }

	
	public Scale getSubdivisions() { return subDivisions; }
	

	public void setMainDivisions(Scale divs) { this.mainDivisions = divs; }
	

	public void setSubdivisions(Scale divs) { this.subDivisions = divs; }
	
	// Get the component position for the given value
	// Note, that the position does not need to be aligned to the border. This will
	public abstract void getPosition(double value, Point2D pos);
		
	// Get the value corresponding to the component position
	public abstract double getValue(Point2D pos);
	

	public void setRange(Range range) {
		setRange(range.min(), range.max());
	}
	

	protected NumberFormat getAutoNumberFormat() {
		double res = Math.abs(max - min) / mainDivisions.size();
		int order = (int) Math.floor(-Math.log10(res / unit.value()));
		
		if(order < 1) order = 1;
	
		return Util.s[order];
		
	}

	public void setRange(double min, double max) {
		this.min = min;
		this.max = max;
		
		if(isAutoMultiplier) unit.setMultiplierFor(Math.max(Math.abs(max / unit.value()), Math.abs(min / unit.value())));	 
			
		min /= unit.value();
		max /= unit.value();
		
		if(mainDivisions != null) mainDivisions.update(min, max);
		if(subDivisions != null) subDivisions.update(min, max);
	}

	@Override
	public void setBackground(Color color) {
		super.setBackground(color);
		if(tickMarks != null) tickMarks.setBackground(color);
	}
	

	public class TickMarks extends JComponent {

		private static final long serialVersionUID = 1221288634179934990L;

		private int divLength;

		private int subdivLength;

		private Stroke divStroke = new BasicStroke();

		private Stroke smallStroke = new BasicStroke();

		private int spacing = 3;

		private Vector2D direction = new Vector2D();

		public TickMarks() {
			mainDivisions = new Scale();
			subDivisions = new Scale(10);
			setSide(side);
			setDivLength(6);
			setSubdivLength(2);
		}
	

		public void setSide(int side) {
			switch(side) {
			case Plot.TOP_SIDE: direction.set(0.0, -1.0); break;
			case Plot.BOTTOM_SIDE: direction.set(0.0, 1.0); break;
			case Plot.LEFT_SIDE: direction.set(-1.0, 0.0); break;
			case Plot.RIGHT_SIDE: direction.set(1.0, 0.0); break;
			default: direction.set(0.0, 0.0);
			}
			setPreferredSize();
		}
		

		protected void getAlignment(Vector2D v) {
			v.setX(0.5 * (1.0 - direction.x()));
			v.setY(0.5 * (1.0 - direction.y()));
		}
			

		public void setDivLength(int pixels) {
			divLength = pixels; 
			setPreferredSize();
		}
		

		public void setSubdivLength(int pixels) { 
			subdivLength = pixels; 
			setPreferredSize();
		}
		

		public int getDivLength() { return divLength; }
		

		public int getSubdivLength() { return subdivLength; }
		

		private void setPreferredSize() {		
			if(isVertical()) setPreferredSize(new Dimension(divLength + spacing, 0));
			else if(isHorizontal()) setPreferredSize(new Dimension(0, divLength + spacing));
			else setPreferredSize(new Dimension(0, 0));
		}
		
		// TODO separate handling of CUSTOM subdivisions....

		@Override
		public void paintComponent(Graphics g) {	
			super.paintComponent(g);
			
			int length = isHorizontal() ? getWidth() : getHeight();
			final double npix = (double) length / subDivisions.size();
			
			// Draw subdivisions only if there is enough room for them...
			if(npix > 0.4) {
				// Figure out how many of the subdivisions to actually draw...
				int subStep = 1;
				if(npix < 5 && npix > 2) subStep = 2; 
				else if(npix <= 2 ) subStep = 5;
				
				// Set the stroke rendering
				((Graphics2D) g).setStroke(smallStroke);
				
				draw(g, subDivisions, subStep, subdivLength);
			}
			
			// Now draw the main divisions...
			// Set the stroke rendering
			((Graphics2D) g).setStroke(divStroke);
			
			draw(g, mainDivisions, 1, divLength);
		}
		

		private void draw(Graphics g, ArrayList<Scale.Division> divs, int step, int length) {
			final Point2D pos = new Point2D.Double();
			final Graphics2D g2 = (Graphics2D) g;
			
			final double dx = length * direction.x();
			final double dy = length * direction.y();
			
			for(int i=0; i<divs.size(); i += step) {
				getPosition(divs.get(i).getValue() * unit.value(), pos);
				toPlotSide(pos, getWidth(), getHeight());
				
				//System.err.println("### " + i + ": " + divs.get(i) + ",\t" + pos);
				
				g2.draw(new Line2D.Double(pos.getX(), pos.getY(), pos.getX() + dx, pos.getY() + dy));
			}	
		}
		
		
	}
	

	protected void toPlotSide(Point2D pos, int width, int height) {
		// snap to the appropriate display edge...
		switch(getSide()) {
		case Plot.TOP_SIDE: pos.setLocation(pos.getX(), height-1); break;
		case Plot.BOTTOM_SIDE: pos.setLocation(pos.getX(), 0); break;
		case Plot.LEFT_SIDE: pos.setLocation(width-1, pos.getY()); break;
		case Plot.RIGHT_SIDE: pos.setLocation(0, pos.getY()); break;
		default: pos.setLocation(Double.NaN, Double.NaN);
		}
	}
	
}
