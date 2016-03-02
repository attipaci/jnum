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
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;

import javax.swing.JComponent;
import javax.swing.JPanel;

// TODO: Auto-generated Javadoc
// TODO dragging boundaries to adjust component sizes?

/**
 * The Class Plot.
 *
 * @param <ContentType> the generic type
 */
public class Plot<ContentType extends ContentLayer> extends JPanel {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1434685464605442072L;
	
	/** The content area. */
	private ContentArea<? extends ContentType> contentArea;

	/** The bottom. */
	public PlotSidePane left, right, top, bottom;
	
	/** The bottom right. */
	public PlotPane center,topLeft, topRight, bottomLeft, bottomRight;
	
	/** The stroke. */
	private Stroke stroke;
	
	// containers for each sub-panel...
	// getComponentAt/findComponentAt --> the top-most visible component at the position.
	// validate()? (does layout, but how is it different from paintComponent?...)
	
	// TODO
	// constructors:
	//   Plot(float[][])
	//   Plot(double[][])
	//   Plot(Data2D)
	//   Plot(GridImage<?>)
	//   ...
	
	// Top:
	//  * Title
	//  * AxisLabel
	//  * Ruler (adjustable)
	
	// Bottom:
	//	* Ruler (adjustable)
	//	* AxisLabel
	//  * (ColorBar.Vertical, ScaleBar, AxisLabel)
	//  * (...)
	
	// Left:
	//	* AxisLabel
	//  * Ruler (adjustable)
	
	// Right:
	//	* Ruler (adjustable)
	//  * AxisLabel
	//  * (ColorBar.Horizontal, ...)
	//  * (...)
	
	/** The layout. */
	GridBagLayout layout = new GridBagLayout();
	
	/**
	 * Instantiates a new plot.
	 */
	public Plot() {
		setOpaque(false);
		setLayout(layout);

		// The central plot area
		center = new PlotPane(this);
		add(center, 1, 1, GridBagConstraints.BOTH, 1.0, 1.0);
	
		// The sides...
		left = new PlotSidePane(this, Plot.LEFT_SIDE);
		add(left, 0, 1, GridBagConstraints.VERTICAL, 0.0, 0.0);
					
		right = new PlotSidePane(this, Plot.RIGHT_SIDE);
		add(right, 2, 1, GridBagConstraints.VERTICAL, 0.0, 0.0);
		
		top = new PlotSidePane(this, Plot.TOP_SIDE);
		add(top, 1, 0, GridBagConstraints.HORIZONTAL, 0.0, 0.0);
		
		bottom = new PlotSidePane(this, Plot.BOTTOM_SIDE);
		add(bottom, 1, 2, GridBagConstraints.HORIZONTAL, 0.0, 0.0);
	
		// The corners...
		topLeft = new PlotPane(this);
		add(topLeft, 0, 0, GridBagConstraints.BOTH, 0.0, 0.0);	
		
		topRight = new PlotPane(this);
		add(topRight, 2, 0, GridBagConstraints.BOTH, 0.0, 0.0);
		
		bottomLeft = new PlotPane(this);
		add(bottomLeft, 0, 2, GridBagConstraints.BOTH, 0.0, 0.0);
	
		bottomRight = new PlotPane(this);
		add(bottomRight, 2, 2, GridBagConstraints.BOTH, 0.0, 0.0);
		
		setDefaults();
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#setBackground(java.awt.Color)
	 */
	@Override
	public void setBackground(Color color) {
		super.setBackground(color);
		for(Component c : getComponents()) c.setBackground(color);
	}
	
	/**
	 * Gets the center pane.
	 *
	 * @return the center pane
	 */
	public PlotPane getCenterPane() { return center; }
	
	/**
	 * Gets the leftane.
	 *
	 * @return the leftane
	 */
	public PlotPane getLeftane() { return left; }
	
	/**
	 * Gets the right pane.
	 *
	 * @return the right pane
	 */
	public PlotPane getRightPane() { return right; }
	
	/**
	 * Gets the top pane.
	 *
	 * @return the top pane
	 */
	public PlotPane getTopPane() { return top; }
	
	/**
	 * Gets the bottom pane.
	 *
	 * @return the bottom pane
	 */
	public PlotPane getBottomPane() { return bottom; }
	
	/**
	 * Gets the top left pane.
	 *
	 * @return the top left pane
	 */
	public PlotPane getTopLeftPane() { return topLeft; }
	
	/**
	 * Gets the top right pane.
	 *
	 * @return the top right pane
	 */
	public PlotPane getTopRightPane() { return topRight; }
	
	/**
	 * Gets the bottom left pane.
	 *
	 * @return the bottom left pane
	 */
	public PlotPane getBottomLeftPane() { return bottomLeft; }
	
	/**
	 * Gets the bottom right pane.
	 *
	 * @return the bottom right pane
	 */
	public PlotPane getBottomRightPane() { return bottomRight; }
	
	/**
	 * Set defaults.
	 */
	public void setDefaults() {
		setFont(defaultFont);
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	@Override
	public void paint(Graphics g) {
		contentArea.setSize(center.getSize());		
		super.paint(g);
	}
	
	/**
	 * Gets the coordinate bounds.
	 *
	 * @param side the side
	 * @return the coordinate bounds
	 */
	public Rectangle2D getCoordinateBounds(int side) {
		
		if(contentArea.toCoordinates() == null) return new Rectangle2D.Double(0.0, 0.0, 1.0, 1.0);
		
		Point2D c1 = null, c2 = null; 
		
		switch(side) {
		case(Plot.LEFT_SIDE):
			c1 = new Point2D.Double(0.0, getHeight());
			c2 = new Point2D.Double(0.0, 0.0);
			break;
		case(Plot.RIGHT_SIDE):
			c1 = new Point2D.Double(getWidth(), getHeight());
			c2 = new Point2D.Double(getWidth(), 0.0);
			break;
		case(Plot.TOP_SIDE):
			c1 = new Point2D.Double(0, 0.0);
			c2 = new Point2D.Double(getWidth(), 0.0);
			break;
		case(Plot.BOTTOM_SIDE):
			c1 = new Point2D.Double(0, 0.0);
			c2 = new Point2D.Double(getWidth(), 0.0);
			break;
		default: return null;
		}
		
		contentArea.toCoordinates(c1);
		contentArea.toCoordinates(c2);
		
		return new Rectangle2D.Double(
			Math.min(c1.getX(), c2.getX()),
			Math.min(c1.getY(), c2.getY()),
			Math.abs(c1.getX() - c2.getX()),
			Math.abs(c1.getY() - c2.getY())
		);
	}
	
	
	/**
	 * Gets the content.
	 *
	 * @return the content
	 */
	public ContentArea<? extends ContentType> getContent() { return contentArea; }
	
	/**
	 * Sets the content.
	 *
	 * @param area the new content
	 */
	public void setContent(ContentArea<? extends ContentType> area) { 
		if(this.contentArea != null) center.remove(this.contentArea);
		this.contentArea = area; 
		center.add(area);
	}
	
	/**
	 * Sets the rulers.
	 *
	 * @param value the new rulers
	 */
	public void setRulers(boolean value) {
		top.setRuler(value);
		bottom.setRuler(value);
		left.setRuler(value);
		right.setRuler(value);
		revalidate();
	}
	
	/**
	 * Adds the.
	 *
	 * @param component the component
	 * @param x the x
	 * @param y the y
	 * @param fill the fill
	 * @param weightx the weightx
	 * @param weighty the weighty
	 */
	private void add(JComponent component, int x, int y, int fill, double weightx, double weighty) {	
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = weightx;
        c.weighty = weighty;
        c.gridx = x;
        c.gridy = y;
        c.fill = fill;
        layout.setConstraints(component, c);
        add(component);
	}
	
	/**
	 * Gets the stroke.
	 *
	 * @return the stroke
	 */
	public Stroke getStroke() { return stroke; }

	/**
	 * Sets the stroke.
	 *
	 * @param s the new stroke
	 */
	public void setStroke(Stroke s) { this.stroke = s; }
	
	/**
	 * Gets the font size.
	 *
	 * @return the font size
	 */
	public float getFontSize() { return getFont().getSize2D(); }
	
	
	/**
	 * Sets the font size.
	 *
	 * @param size the new font size
	 */
	public void setFontSize(float size) {
		setFont(getFont().deriveFont(size));
	}
	
	/**
	 * Sets the font bold.
	 *
	 * @param value the new font bold
	 */
	public void setFontBold(boolean value) {
		int style = getFont().getStyle();
		if(value) style |= Font.BOLD;
		else style &= ~Font.BOLD;
		setFont(getFont().deriveFont(style));
	}
	
	/**
	 * Sets the font italic.
	 *
	 * @param value the new font italic
	 */
	public void setFontItalic(boolean value) {
		int style = getFont().getStyle();
		if(value) style |= Font.ITALIC;
		else style &= ~Font.ITALIC;
		setFont(getFont().deriveFont(style));
	}
	
	/**
	 * Checks if is font italic.
	 *
	 * @return true, if is font italic
	 */
	public boolean isFontItalic() {
		return (getFont().getStyle() & Font.ITALIC) != 0;
	}
	
	/**
	 * Checks if is font bold.
	 *
	 * @return true, if is font bold
	 */
	public boolean isFontBold() {
		return (getFont().getStyle() & Font.BOLD) != 0;
	}
	
	// Returns a generated image.
	/**
	 * Gets the rendered image.
	 *
	 * @param width the width
	 * @param height the height
	 * @return the rendered image
	 */
	public RenderedImage getRenderedImage(int width, int height) {
		setSize(width, height);

		// Create a buffered image in which to draw
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		// Create a graphics contents on the buffered image
		Graphics2D g2 = bufferedImage.createGraphics();  

		// Draw graphics
		paint(g2);

		// Graphics context no longer needed so dispose it
		g2.dispose();

		return bufferedImage;
	}
	
	
	/** The default font. */
	public static Font defaultFont = new Font("SansSerif", Font.BOLD, 15);
	
	/** The Constant SIDE_UNDEFINED. */
	public final static int SIDE_UNDEFINED = -1;
	
	/** The Constant TOP_SIDE. */
	public final static int TOP_SIDE = 0;
	
	/** The Constant BOTTOM_SIDE. */
	public final static int BOTTOM_SIDE = 1;
	
	/** The Constant LEFT_SIDE. */
	public final static int LEFT_SIDE = 2;
	
	/** The Constant RIGHT_SIDE. */
	public final static int RIGHT_SIDE = 3;
	
	
	/** The Constant VALIGN_TOP. */
	public final static int VALIGN_TOP = 1;
	
	/** The Constant VALIGN_CENTER. */
	public final static int VALIGN_CENTER = 0;
	
	/** The Constant VALIGN_BOTTOM. */
	public final static int VALIGN_BOTTOM = -1;
	
	/** The Constant HALIGN_LEFT. */
	public final static int HALIGN_LEFT = 1;
	
	/** The Constant HALIGN_CENTER. */
	public final static int HALIGN_CENTER = 0;
	
	/** The Constant HALIGN_RIGHT. */
	public final static int HALIGN_RIGHT = -1;
	
}
