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


import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

// TODO: Auto-generated Javadoc
/**
 * The Class PlotSidePane.
 */
public class PlotSidePane extends PlotPane implements PlotSide, Arrangeable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 940220609694011545L;
	
	/** The side. */
	private int side = Plot.SIDE_UNDEFINED;

	/** The ruler. */
	private PlotSideRuler ruler;
	
	/** The far. */
	private JComponent center, far;
	
	/**
	 * Instantiates a new plot side pane.
	 *
	 * @param plot the plot
	 * @param side the side
	 */
	public PlotSidePane(Plot<?> plot, int side) {
		super(plot);
		ruler = new PlotSideRuler(plot, side);
		setSide(side);
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.plot.PlotSide#getSide()
	 */
	@Override
	public int getSide() { return side; }
	
	/* (non-Javadoc)
	 * @see kovacs.util.plot.PlotSide#setSide(int)
	 */
	@Override
	public void setSide(int side) {
		if(this.side == side) return;
		
		this.side = side;
		
		if(ruler != null) ruler.setSide(side);
		if(center != null) if(center instanceof PlotSide) ((PlotSide) center).setSide(side);
		if(far != null) if(far instanceof PlotSide) ((PlotSide) far).setSide(side);
		
		if(isHorizontal()) setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		else if(isVertical()) setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		arrange();
	}

	
	/**
	 * Sets the ruler.
	 *
	 * @param value the new ruler
	 */
	public void setRuler(boolean value) {
		if(value == hasRuler()) return;
		if(value) ruler = new PlotSideRuler(getPlot(), side);
		else ruler = null;
		arrange();
	}
	
	/**
	 * Checks for ruler.
	 *
	 * @return true, if successful
	 */
	public boolean hasRuler() { return ruler != null; }
	
	/**
	 * Gets the ruler.
	 *
	 * @return the ruler
	 */
	public PlotSideRuler getRuler() { return ruler; }
	
	/* (non-Javadoc)
	 * @see kovacs.util.plot.Arrangeable#arrange()
	 */
	@Override
	public void arrange() {
		removeAll();
		
		if(side == Plot.TOP_SIDE || side == Plot.LEFT_SIDE) {
			if(far != null) add(far);
			if(center != null) add(center);
			if(ruler != null) add(ruler);
		}
		else if(side == Plot.BOTTOM_SIDE || side == Plot.RIGHT_SIDE) {
			if(ruler != null) add(ruler);
			if(center != null) add(center);
			if(far != null) add(far);
		}
		
		revalidate();
	}
	
	/*
	@Override
	public void setSize(int w, int h) {
		super.setSize(w, h);
		
		if(isHorizontal()) for(Component c : getComponents()) 
			c.setSize(c.getPreferredSize().width, h);
		else if(isVertical()) for(Component c : getComponents()) 
			c.setSize(w, c.getPreferredSize().height);
	}
	*/
	
	/**
	 * Gets the center.
	 *
	 * @return the center
	 */
	public JComponent getCenter() { return center; }
	
	/**
	 * Sets the center.
	 *
	 * @param c the c
	 * @return the j component
	 */
	public JComponent setCenter(JComponent c) {
		JComponent old = this.center;
		this.center = c;
		c.setBackground(getBackground());
		if(c instanceof PlotSide) ((PlotSide) c).setSide(getSide());
		arrange();
		return old;		
	}
	
	/**
	 * Gets the far.
	 *
	 * @return the far
	 */
	public JComponent getFar() { return far; }
	
	/**
	 * Sets the far.
	 *
	 * @param c the c
	 * @return the j component
	 */
	public JComponent setFar(JComponent c) {
		JComponent old = this.far;
		this.far = c;
		c.setBackground(getBackground());
		if(c instanceof PlotSide) ((PlotSide) c).setSide(getSide());
		arrange();
		return old;		
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.plot.PlotSide#isHorizontal()
	 */
	@Override
	public boolean isHorizontal() { return side == Plot.TOP_SIDE || side == Plot.BOTTOM_SIDE; }
	
	/* (non-Javadoc)
	 * @see kovacs.util.plot.PlotSide#isVertical()
	 */
	@Override
	public boolean isVertical() { return side == Plot.LEFT_SIDE || side == Plot.RIGHT_SIDE; }

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#setBackground(java.awt.Color)
	 */
	@Override
	public void setBackground(Color color) {
		super.setBackground(color);
		for(Component c : getComponents()) c.setBackground(color);
	}
	
	/**
	 * The Class Spacer.
	 */
	public class Spacer extends JPanel {
		
		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = -1112347113472103728L;
		
		/** The size. */
		private int size = 0;
		
		/**
		 * Instantiates a new spacer.
		 *
		 * @param width the width
		 */
		public Spacer(int width) { setSpacing(width); }
		
		/**
		 * Gets the spacing.
		 *
		 * @return the spacing
		 */
		public int getSpacing() { return size; }
		
		/**
		 * Sets the spacing.
		 *
		 * @param width the new spacing
		 */
		public void setSpacing(int width) { this.size = width; }
		
		/* (non-Javadoc)
		 * @see javax.swing.JComponent#getPreferredSize()
		 */
		@Override
		public Dimension getPreferredSize() {
			if(isHorizontal()) return new Dimension(0, size);
			else return new Dimension(size, 0);
		}
	}
	
}
