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
// Copyright (c) 2007 Attila Kovacs 

package jnum.plot;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

import jnum.math.Vector2D;


// TODO: Auto-generated Javadoc
// TODO
// 	*	Right click --> make editable (Directly or through dialog)
//  *	"%u" to symbolize unit...
//  *	left click cycle through units/labels, or --> UnitLabel to change unit
//  *   multiline labels (manually break lines and layout...)


/**
 * The Class SimpleLabel.
 */
public abstract class SimpleLabel {
	
	/** The canvas. */
	private JComponent canvas;
	
	/** The font. */
	private Font font;
	
	/** The label. */
	private String label = "";
		
	/** The align x. */
	private int alignX = ALIGN_CENTER;
	
	/** The align y. */
	private int alignY = ALIGN_CENTER;
	
	/** The rotation. */
	private double rotation = 0.0; // clockwise rotation
	
	/**
	 * Instantiates a new simple label.
	 *
	 * @param canvas the canvas
	 */
	public SimpleLabel(JComponent canvas) {
		this.canvas = canvas;
		setFont(defaultFont);
	}

	/**
	 * Instantiates a new simple label.
	 *
	 * @param canvas the canvas
	 * @param text the text
	 */
	public SimpleLabel(JComponent canvas, String text) {
		this(canvas);
		this.label = text;
	}

	/**
	 * Instantiates a new simple label.
	 *
	 * @param canvas the canvas
	 * @param text the text
	 * @param alignX the align x
	 * @param alignY the align y
	 */
	public SimpleLabel(JComponent canvas, String text, int alignX, int alignY) {
		this(canvas, text);
		this.alignX = alignX;
		this.alignY = alignY;
	}	
	
	/**
	 * Gets the position.
	 *
	 * @return the position
	 */
	public abstract Vector2D getPosition();

	/**
	 * Sets the font.
	 *
	 * @param f the new font
	 */
	public void setFont(Font f) { this.font = f; }
	
	/**
	 * Gets the font.
	 *
	 * @return the font
	 */
	public Font getFont() { return font == null ? canvas.getFont() : font; }
	
	/**
	 * Gets the bounds.
	 *
	 * @return the bounds
	 */
	public Rectangle2D getBounds() {
		Graphics g = canvas.getGraphics();
		Graphics2D g2 = (Graphics2D) g;
		if(font != null) g.setFont(font);
		TextLayout layout = new TextLayout(label, getFont(), g2.getFontRenderContext());
		return layout.getBounds();
	}
	
	/**
	 * Paint.
	 */
	public void paint() {
		Graphics g = canvas.getGraphics();
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		if(font != null) g.setFont(font);
		TextLayout layout = new TextLayout(label, getFont(), g2.getFontRenderContext());
		Rectangle2D bounds = layout.getBounds();	
		//FontMetrics fm = g.getFontMetrics(getFont());
		
		double refX = 0.0;
		
		switch(alignX) {
		case ALIGN_LEFT: refX = bounds.getMinX(); break;
		case ALIGN_RIGHT: refX = bounds.getMaxX(); break;
		case ALIGN_CENTER: refX = bounds.getCenterX(); break;
		default: refX = bounds.getCenterX();
		}
		
		double refY = 0.0;
		

		// ---------- top       \                     \
		// ascent               |                     |
		// ---------- baseline   >  TextLayout bounds |  
		// descent              |                      > Font.getStringBounds
		// ----------           /                     |
		// leading                                    |
		// ---------- next line                       /
				
		switch(alignY) {
		case ALIGN_TOP: refY = bounds.getMinY(); break;
		case ALIGN_BOTTOM: refY = bounds.getMaxY(); break;
		case ALIGN_CENTER: refY = bounds.getCenterY(); break;
		case ALIGN_BASELINE: refY = bounds.getMaxY() - layout.getDescent(); break;
		case ALIGN_MIDRISE: refY = bounds.getMinY() + 0.5 * layout.getAscent(); break;
		default: refY = bounds.getCenterY();
		}		
		
		Vector2D position = getPosition();
		
		int x0 = (int)Math.round(position.x() - refX);
		int y0 = (int)Math.round(position.y() - refY);
		
		// Rotate around the anchored position....
		g2.rotate(rotation, position.x(), position.y());
		
		// Render the label...
		g.drawString(label, x0, y0);
	}

	
	/**
	 * Gets the horizontal text align.
	 *
	 * @return the horizontal text align
	 */
	public int getHorizontalTextAlign() { return alignX; }
	
	/**
	 * Gets the vertical text align.
	 *
	 * @return the vertical text align
	 */
	public int getVerticalTextAlign() { return alignY; }
	
	/**
	 * Sets the horizontal text align.
	 *
	 * @param value the new horizontal text align
	 */
	public void setHorizontalTextAlign(int value) { alignX = value; }
	
	/**
	 * Sets the vertical text align.
	 *
	 * @param value the new vertical text align
	 */
	public void setVerticalTextAlign(int value) { alignY = value; }
	
	/**
	 * Gets the text.
	 *
	 * @return the text
	 */
	public String getText() { return label; }
	
	/**
	 * Sets the text.
	 *
	 * @param text the new text
	 */
	public void setText(String text) { this.label = text; }
	
	/**
	 * Gets the rotation.
	 *
	 * @return the rotation
	 */
	public double getRotation() { return rotation; }
	
	/**
	 * Sets the rotation.
	 *
	 * @param angle the new rotation
	 */
	public void setRotation(double angle) { this.rotation = angle; }

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
	
	/** The Constant ALIGN_CENTER. */
	public final static int ALIGN_CENTER = 0;
	
	/** The Constant ALIGN_LEFT. */
	public final static int ALIGN_LEFT = 1;
	
	/** The Constant ALIGN_RIGHT. */
	public final static int ALIGN_RIGHT = 2;
	
	/** The Constant ALIGN_BOTTOM. */
	public final static int ALIGN_BOTTOM = 3;
	
	/** The Constant ALIGN_TOP. */
	public final static int ALIGN_TOP = 4;
	
	/** The Constant ALIGN_BASELINE. */
	public final static int ALIGN_BASELINE = 5;
	
	/** The Constant ALIGN_MIDRISE. */
	public final static int ALIGN_MIDRISE = 6;

	
	/** The default font. */
	public static Font defaultFont = new Font("SansSerif", Font.BOLD, 15);

}


