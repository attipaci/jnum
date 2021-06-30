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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.geom.Point2D;

import javax.swing.BorderFactory;
import javax.swing.JComponent;

import jnum.math.Range;




public class ColorBar extends JComponent implements PlotSide {

	private static final long serialVersionUID = 460851913543807978L;

	private ImageLayer image;

	private Stripe stripe;

	private Ruler ruler;

	private int side = Plot.SIDE_UNDEFINED;
	

	public String name = null;
	

	public ColorBar(ImageLayer image) { 
		this.image = image;
		stripe = new Stripe(defaultWidth);
		ruler = new Ruler();
		ruler.setName(null);
		setLayout(new BorderLayout());
		//TODO set the unit on the ruler to that of the image...
	}

	@Override
	public void setName(String name) {
		super.setName(name);
		ruler.setName(name);
	}

	@Override
	public boolean isHorizontal() { return side == Plot.TOP_SIDE || side == Plot.BOTTOM_SIDE; }

	@Override
	public boolean isVertical() { return side == Plot.LEFT_SIDE || side == Plot.RIGHT_SIDE; }

	@Override
	public int getSide() { return side; }

	@Override
	public void setSide(int side) {
		if(side == this.side) return;
		this.side = side;	
		stripe.setPreferredSize();
		
		if(ruler != null) ruler.setSide(side);
		
		arrange();
	}
	

	private void arrange() {
		removeAll();
		
		add(stripe, BorderLayout.CENTER);
		
		if(ruler != null) {
			switch(side) {
			case Plot.LEFT_SIDE: add(ruler, BorderLayout.WEST); break;
			case Plot.RIGHT_SIDE: add(ruler, BorderLayout.EAST); break;
			case Plot.TOP_SIDE: add(ruler, BorderLayout.NORTH); break;
			case Plot.BOTTOM_SIDE: add(ruler, BorderLayout.SOUTH); break;
			}
		}
		
		revalidate();
	}

	@Override
	public void validate() {
		arrange();
		super.validate();
	}
	

	public class Stripe extends JComponent {	

		private static final long serialVersionUID = 5950901962993328368L;

		private int width;

		private boolean inverted = false;

		private Stripe(int width) {	
			this.width = width;
			setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		}

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponents(g);
			
			if(isHorizontal()) drawHorizontal(g);
			else drawVertical(g);
		}
		

		private void drawVertical(Graphics g) {
			ColorScheme colors = image.getColorScheme();	
			double scale = 1.0 / getHeight();
			
			for(int y = getHeight() - 1; --y >= 0; ) {
				double value = inverted ? scale * y : 1.0 - scale * y;
				g.setColor(new Color(colors.getRGB(value)));
				g.drawRect(0, y, width, 1);
			}	
		}
		

		private void drawHorizontal(Graphics g) {
			ColorScheme colors = image.getColorScheme();	
			double scale = 1.0 / getWidth();
			
			for(int x = getWidth() - 1; --x >= 0; ) {
				double value = inverted ? 1.0 - scale * x : scale * x;
				g.setColor(new Color(colors.getRGB(value)));
				g.drawRect(x, 0, 1, width);
			}	
		}
		

		public void setPreferredSize() {
			if(isVertical()) setPreferredSize(new Dimension(width, 2));
			else if(isHorizontal()) setPreferredSize( new Dimension(2, width));
		}
		
		@Override
		public void validate() {
			setPreferredSize();
			super.validate();
		}
		

		public void setWidth(int pixels) { this.width = pixels; }
		
		public void invert() {
			inverted = !inverted;
		}		
		
	}
	

	
	
	public class Ruler extends FancyRuler {

		private static final long serialVersionUID = -7906137098891819994L;

		private Range range;
		
		private Ruler() {
			super(ColorBar.this.getSide());
		}

		@Override
		public void getPosition(double value, Point2D pos) {
			double frac = (value - range.min()) / range.span();
			if(isHorizontal()) pos.setLocation(frac * getWidth(), 0);
			else if(isVertical()) pos.setLocation(0, getHeight() * (1.0 - frac));
		}

		@Override
		public void paintComponent(Graphics g) {	
			range = image.getRange();
			setRange(range.min(), range.max());
			
			super.paintComponent(g);
			
		}

		@Override
		public double getValue(Point2D pos) {
			Range range = image.getRange();
			
			double frac = isHorizontal() ?
					pos.getX() / getWidth() :
					1.0 - pos.getY() / getHeight();
					
			return range.min() + frac * range.span();
		}	
		
	}
	
	
	private static int defaultWidth = 20;
	
}

