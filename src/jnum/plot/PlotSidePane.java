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
package jnum.plot;


import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class PlotSidePane extends PlotPane implements PlotSide, Arrangeable {

	private static final long serialVersionUID = 940220609694011545L;
	
	private int side = Plot.SIDE_UNDEFINED;

	private PlotSideRuler ruler;

	private JComponent center, far;
	
	public PlotSidePane(Plot<?> plot, int side) {
		super(plot);
		ruler = new PlotSideRuler(plot, side);
		setSide(side);
	}

	@Override
	public int getSide() { return side; }

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

	public void setRuler(boolean value) {
		if(value == hasRuler()) return;
		if(value) ruler = new PlotSideRuler(getPlot(), side);
		else ruler = null;
		arrange();
	}

	public boolean hasRuler() { return ruler != null; }

	public PlotSideRuler getRuler() { return ruler; }

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

	public JComponent getCenter() { return center; }

	public JComponent setCenter(JComponent c) {
		JComponent old = this.center;
		this.center = c;
		c.setBackground(getBackground());
		if(c instanceof PlotSide) ((PlotSide) c).setSide(getSide());
		arrange();
		return old;		
	}

	public JComponent getFar() { return far; }

	public JComponent setFar(JComponent c) {
		JComponent old = this.far;
		this.far = c;
		c.setBackground(getBackground());
		if(c instanceof PlotSide) ((PlotSide) c).setSide(getSide());
		arrange();
		return old;		
	}

	@Override
	public boolean isHorizontal() { return side == Plot.TOP_SIDE || side == Plot.BOTTOM_SIDE; }

	@Override
	public boolean isVertical() { return side == Plot.LEFT_SIDE || side == Plot.RIGHT_SIDE; }

	@Override
	public void setBackground(Color color) {
		super.setBackground(color);
		for(Component c : getComponents()) c.setBackground(color);
	}

	public class Spacer extends JPanel {

		private static final long serialVersionUID = -1112347113472103728L;

		private int size = 0;

		public Spacer(int width) { setSpacing(width); }

		public int getSpacing() { return size; }

		public void setSpacing(int width) { this.size = width; }

		@Override
		public Dimension getPreferredSize() {
			if(isHorizontal()) return new Dimension(0, size);
			return new Dimension(size, 0);
		}
	}
	
}
