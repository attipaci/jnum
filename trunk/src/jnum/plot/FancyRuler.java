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
package jnum.plot;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.JComponent;

import jnum.Constant;
import jnum.Util;
import jnum.math.Scale;


// TODO: Auto-generated Javadoc
/**
 * The Class FancyRuler.
 */
public abstract class FancyRuler extends BasicRuler implements Arrangeable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 132926720323714435L;
	
	/** The labels. */
	private TickLabels tickLabels;
	
	/** The title. */
	private AxisLabel axisLabel;
	
	/**
	 * Instantiates a new fancy ruler.
	 *
	 * @param edge the edge
	 */
	public FancyRuler(int edge) {
		super(edge);
		setLayout(new BorderLayout());
		tickLabels = new TickLabels();	
		axisLabel = new AxisLabel(isVertical() ? "y" : "x");
	}
	
	/* (non-Javadoc)
	 * @see java.awt.Component#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
		super.setName(name);
		axisLabel.setName(name);
	}
	
	/**
	 * Gets the labels.
	 *
	 * @return the labels
	 */
	public TickLabels getTickLabels() { return tickLabels; }
	
	/**
	 * Show tick labels.
	 *
	 * @param value the value
	 */
	public void showTickLabels(boolean value) { tickLabels.setVisible(value); }
	
	/**
	 * Checks if is showing tick labels.
	 *
	 * @return true, if is showing tick labels
	 */
	public boolean isShowingTickLabels() { return tickLabels.isVisible(); }
	
	
	/**
	 * Gets the axis label.
	 *
	 * @return the axis label
	 */
	public AxisLabel getAxisLabel() { return axisLabel; }

	/**
	 * Show axis label.
	 *
	 * @param value the value
	 */
	public void showAxisLabel(boolean value) { axisLabel.setVisible(value); }
	
	/**
	 * Checks if is showing axis labels.
	 *
	 * @return true, if is showing axis labels
	 */
	public boolean isShowingAxisLabels() { return axisLabel.isVisible(); }
	
	
	/**
	 * Show labels.
	 *
	 * @param value the value
	 */
	public void showLabels(boolean value) {
		showAxisLabel(value);
		showTickLabels(value);
	}
	
	
	
	/* (non-Javadoc)
	 * @see kovacs.util.plot.BasicRuler#setSide(int)
	 */
	@Override
	public void setSide(int edge) {
		super.setSide(edge);
		
		if(tickLabels != null) tickLabels.setSide(edge);
		if(axisLabel != null) axisLabel.setSide(edge);
		
		arrange();
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.plot.Arrangeable#arrange()
	 */
	@Override
	public void arrange() {
		removeAll();
		
		int edge = getSide();
		
		if(tickLabels != null) add(tickLabels, BorderLayout.CENTER);
		
		switch(edge) {
		case Plot.LEFT_SIDE :
			add(getTickMarks(), BorderLayout.EAST);	
			if(axisLabel != null) add(axisLabel, BorderLayout.WEST);
			break;
		case Plot.RIGHT_SIDE :
			add(getTickMarks(), BorderLayout.WEST);
			if(axisLabel != null) add(axisLabel, BorderLayout.EAST);
			break;
		case Plot.TOP_SIDE :
			add(getTickMarks(), BorderLayout.SOUTH);
			if(axisLabel != null) add(axisLabel, BorderLayout.NORTH);
			break;
		case Plot.BOTTOM_SIDE :
			add(getTickMarks(), BorderLayout.NORTH);
			if(axisLabel != null) add(axisLabel, BorderLayout.SOUTH);
			break;
		}		

		revalidate();
	}
	
	/* (non-Javadoc)
	 * @see kovacs.plot.BasicRuler#setRange(double, double)
	 */
	@Override
	public void setRange(double min, double max) {
		super.setRange(min, max);
			
		tickLabels.update();
		axisLabel.update();
		
		revalidate();		
	}
	

	/* (non-Javadoc)
	 * @see java.awt.Container#validate()
	 */
	@Override
	public void validate() {
		arrange();
		super.validate();
	}
	
	/**
	 * The Class Labels.
	 */
	public class TickLabels extends JComponent {
		
		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 6716865385705990104L;
			
		/** The rotation. */
		private double rotation = 0.0;
		
		/** The delta. */
		private double delta = Double.NaN;
		
		/** The nf. */
		private NumberFormat nf = null;
		
		/** The v align. */
		private int hAlign, vAlign;
		
		/** The spacing. */
		private int spacing = 2;
		
		/** The entries. */
		private ArrayList<Entry> entries = new ArrayList<Entry>();
		
		
		/**
		 * Instantiates a new tick labels.
		 */
		public TickLabels() { 
			setFont(defaultDivisionFont); 
		}
		
		/**
		 * Update.
		 */
		public void update() {
			ArrayList<Scale.Division> divs = getMainDivisions();
			
			Font font = getFont();
			
			entries.clear();
			entries.ensureCapacity(divs.size());
			
			NumberFormat nf1 = nf == null ? getAutoNumberFormat() : nf;
			
			for(int i=0; i<divs.size(); i++) {
				Scale.Division div = divs.get(i);
				div.setNumberFormat(nf1);
				entries.add(new Entry(div.getValue(), div.toString(), font));	
			}
			
			final Point2D posA = new Point2D.Double();
			final Point2D posB = new Point2D.Double();
			
			
			// Order the labels in plotting sequence (left to right, bottom to top)
			Collections.sort(entries, new Comparator<Entry>() {
				@Override
				public int compare(Entry a, Entry b) {
					getPosition(a.value, posA);
					getPosition(b.value, posB);	
					return isHorizontal() ? Double.compare(posA.getX(), posB.getX()) : Double.compare(posB.getY(), posA.getY());
				}
			});
			
			setPreferredSize();
		}
	
	
		/* (non-Javadoc)
		 * @see javax.swing.JComponent#setFont(java.awt.Font)
		 */
		@Override
		public void setFont(Font font) {
			super.setFont(font);
			for(Entry entry : entries) entry.getBounds(font);
			setPreferredSize();
		}
		
		/**
		 * Sets the side.
		 *
		 * @param side the new side
		 */
		public void setSide(int side) {
			setPreferredSize();
			
			if(isVertical()) {
				hAlign = side == Plot.LEFT_SIDE ? Plot.HALIGN_RIGHT : Plot.HALIGN_LEFT;
				vAlign = Plot.VALIGN_CENTER;
			}
			else {
				hAlign = Plot.HALIGN_CENTER;
				vAlign = side == Plot.BOTTOM_SIDE ? Plot.VALIGN_TOP : Plot.VALIGN_BOTTOM;
			}
			
		}
		
		/* (non-Javadoc)
		 * @see java.awt.Container#validate()
		 */
		@Override
		public void validate() {
			setPreferredSize();
			super.validate();
		}

		
		/**
		 * Sets the preferred size.
		 */
		private void setPreferredSize() {			
			
			int minsize = 10 + spacing;
			
			double maxw = minsize;
			double maxh = minsize;
			
			for(Entry entry : entries) {
				maxw = Math.max(maxw, entry.bounds.getWidth());
				maxh = Math.max(maxh, entry.bounds.getHeight());
			}
			
			double ac = Math.abs(Math.cos(rotation));
			double as = Math.abs(Math.sin(rotation));
			
			int w = (int) Math.ceil(ac * maxw + as * maxh);
			int h = (int) Math.ceil(as * maxw + ac * maxh);
			
			Dimension dim = new Dimension(w, h);
			
			setMinimumSize(dim);
			setPreferredSize(dim);
		}
		
		
		/**
		 * Sets the rotation.
		 *
		 * @param theta the new rotation
		 */
		public void setRotation(double theta) { 
			this.rotation = theta; 
			setPreferredSize();
		}
		
		/**
		 * Gets the rotation.
		 *
		 * @return the rotation
		 */
		public double getRotation() { return rotation; }
		
		
		/**
		 * Calc delta.
		 */
		protected void calcDelta() {
			delta = Double.POSITIVE_INFINITY;
			final ArrayList<Scale.Division> divs = getMainDivisions();
			double last = divs.get(divs.size() - 1).getValue();
			for(int i=divs.size()-1; --i >= 0; ) {
				final double current = divs.get(i).getValue();
				final double d = Math.abs(last - current);
				if(d > 0.0) if(d < delta) delta = d;
				last = current;	
			}
		}
		
		
		/* (non-Javadoc)
		 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
		 */
		@Override
		public void paintComponent(Graphics g) {	
			super.paintComponent(g);
			draw(g, getMainDivisions(), 1);
		}
		
		/**
		 * Draw.
		 *
		 * @param g the g
		 * @param divs the divs
		 * @param step the step
		 */
		private void draw(Graphics g, ArrayList<Scale.Division> divs, int step) {
			final Point2D pos = new Point2D.Double();
			final Graphics2D g2 = (Graphics2D) g;
			
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			FontMetrics fm = g.getFontMetrics();	

			float dy = 0;
			
			if(vAlign == Plot.VALIGN_CENTER) dy = 0.5F * (fm.getAscent() - fm.getDescent()) - fm.getLeading();
			else if(vAlign == Plot.VALIGN_TOP) dy = fm.getHeight() - spacing;
			
			int h = fm.getHeight();
			int height = getHeight();
			int width = getWidth();
			double c = Math.cos(rotation);
			double s = Math.sin(rotation);
			
			int next = isHorizontal() ? 0 : h;	
			
			for(int i=0; i<entries.size(); i += step) {
				Entry entry = entries.get(i);
				getPosition(entry.value, pos);				
				toPlotSide(pos, width, height);
			
				double w = entry.bounds.getWidth();
				
				float x = (float) pos.getX();	
				float y = (float) pos.getY();
				
				if(hAlign == Plot.HALIGN_CENTER) x -= 0.5F * w;
				else if(hAlign == Plot.HALIGN_RIGHT) x -= w;
					
				if(isHorizontal()) { 
					if(x < next) continue; 
					if(x + w > width) continue;
				}
				else {
					if(height - y < next) continue;
					if(y < h) continue;
				}
				
				if(rotation != 0.0) g2.rotate(-rotation, x, y);
				g2.drawString(entry.text, x, y + dy);
				if(rotation != 0.0) g2.rotate(rotation, x, y);
				
				if(isHorizontal()) next = spacing + (int) Math.ceil(x + Math.min(h / s, c * w));
				else next = spacing + h + (int) Math.ceil(height - y);
			}	
		}
		
		/**
		 * The Class Entry.
		 */
		private class Entry implements Comparable<FancyRuler.TickLabels.Entry> {	
			
			/** The value. */
			private double value;
			
			/** The text. */
			private String text;
			
			/** The bounds. */
			private Rectangle2D bounds;
			
			/**
			 * Instantiates a new entry.
			 *
			 * @param value the value
			 * @param text the text
			 * @param font the font
			 */
			private Entry(double value, String text, Font font) { 
				this.value = value * getUnit().value(); 
				this.text = text; 
				if(font != null) getBounds(font);
			}
			
			/**
			 * Gets the bounds.
			 *
			 * @param font the font
			 * @return the bounds
			 */
			private void getBounds(Font font) {		
				bounds = font.getStringBounds(text, getFontMetrics(font).getFontRenderContext());
			}
			
			/* (non-Javadoc)
			 * @see java.lang.Object#toString()
			 */
			@Override
			public String toString() { return text; }

			/* (non-Javadoc)
			 * @see java.lang.Comparable#compareTo(java.lang.Object)
			 */
			@Override
			public int compareTo(Entry o) {
				return Double.compare(value, o.value);
			}
		}
		
	}
	
	/**
	 * The Class Title.
	 */
	public class AxisLabel extends JComponent {
		
		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = -2340066081374973607L;
		
		/** The text. */
		private String text;
		
		/** The show unit. */
		private boolean showUnit = false;
		
		/** The bounds. */
		private Rectangle2D bounds;
		
		
		/** The spacing. */
		int spacing = 10;
		
		/**
		 * Instantiates a new title.
		 *
		 * @param name the name
		 */
		public AxisLabel(String name) {
			setFont(defaultAxisLabelFont);
			setName(name);			
		}
		
		/**
		 * Update.
		 */
		public void update() {	
			text = getName();
			
			if(text == null) text = "";
			
			if(text.length() == 0) text = unit.name();
			else text += (showUnit ? " [" + unit.name() + "]" : "");
			
			Font font = getFont();	
			bounds = font.getStringBounds(text, getFontMetrics(font).getFontRenderContext());
			setPreferredSize();
		}

		/**
		 * Sets the side.
		 *
		 * @param side the new side
		 */
		public void setSide(int side) {
			setPreferredSize();			
		}
		
		
		/* (non-Javadoc)
		 * @see java.awt.Container#validate()
		 */
		@Override
		public void validate() {
			setPreferredSize();
			super.validate();
		}
		
		/**
		 * Sets the preferred size.
		 */
		private void setPreferredSize() {
			int h = getFontMetrics(getFont()).getHeight() + 2 * spacing;
		
			if(isVertical()) setPreferredSize(new Dimension(h, 0));
			else if(isHorizontal()) setPreferredSize(new Dimension(0, h));
			else setPreferredSize(new Dimension(0, 0));
		}
		
		/* (non-Javadoc)
		 * @see java.awt.Component#setName(java.lang.String)
		 */
		@Override
		public void setName(String value) { 
			super.setName(value);
			update();
		}
		
		/* (non-Javadoc)
		 * @see javax.swing.JComponent#setFont(java.awt.Font)
		 */
		@Override
		public void setFont(Font font) {
			super.setFont(font);
			setPreferredSize();
		}
		
		/**
		 * Show unit.
		 *
		 * @param value the value
		 */
		public void showUnit(boolean value) { 
			showUnit = value; 
			update();
		}
		
		/**
		 * Checks if is showing unit.
		 *
		 * @return true, if is showing unit
		 */
		public boolean isShowingUnit() { return showUnit; }
		
		/* (non-Javadoc)
		 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
		 */
		@Override
		public void paintComponent(Graphics g) {	
			super.paintComponent(g);
			
			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			draw(g, getMainDivisions(), 1);
		}
		
		/**
		 * Draw.
		 *
		 * @param g the g
		 * @param divs the divs
		 * @param step the step
		 */
		private void draw(Graphics g, ArrayList<Scale.Division> divs, int step) {
			final Graphics2D g2 = (Graphics2D) g;
			
			float x0 = 0.5F * getWidth();
			float y0 = 0.5F * getHeight();
			
			if(isVertical()) g2.rotate(-Constant.rightAngle, x0, y0);
			g2.drawString(text, (float) (x0 - 0.5 * bounds.getWidth()), (float) (y0 + 0.5 * bounds.getHeight()));
			
		}
		
		
	}
	
	/** The Constant defaultDivisionFont. */
	public final static Font defaultDivisionFont = new Font("Monospaced", Font.PLAIN, 10);
	
	/** The Constant defaultTitleFont. */
	public final static Font defaultAxisLabelFont = new Font("Serif", Font.BOLD, 14);

	/** The Constant defaultNumberFormat. */
	public final static NumberFormat defaultNumberFormat = Util.f2;
	
	
}
