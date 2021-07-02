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

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

import jnum.math.Vector2D;


// TODO
// 	*	Right click --> make editable (Directly or through dialog)
//  *	"%u" to symbolize unit...
//  *	left click cycle through units/labels, or --> UnitLabel to change unit
//  *   multiline labels (manually break lines and layout...)


public abstract class SimpleLabel {

	private JComponent canvas;

	private Font font;

	private String label = "";

	private int alignX = ALIGN_CENTER;

	private int alignY = ALIGN_CENTER;
	
	private double rotation = 0.0; // clockwise rotation
	

	public SimpleLabel(JComponent canvas) {
		this.canvas = canvas;
		setFont(defaultFont);
	}


	public SimpleLabel(JComponent canvas, String text) {
		this(canvas);
		this.label = text;
	}


	public SimpleLabel(JComponent canvas, String text, int alignX, int alignY) {
		this(canvas, text);
		this.alignX = alignX;
		this.alignY = alignY;
	}	
	

	public abstract Vector2D getPosition();

	
	public void setFont(Font f) { this.font = f; }
	

	public Font getFont() { return font == null ? canvas.getFont() : font; }

	
	public Rectangle2D getBounds() {
		Graphics g = canvas.getGraphics();
		Graphics2D g2 = (Graphics2D) g;
		if(font != null) g.setFont(font);
		TextLayout layout = new TextLayout(label, getFont(), g2.getFontRenderContext());
		return layout.getBounds();
	}
	

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


	public int getHorizontalTextAlign() { return alignX; }
	

	public int getVerticalTextAlign() { return alignY; }
	

	public void setHorizontalTextAlign(int value) { alignX = value; }
	

	public void setVerticalTextAlign(int value) { alignY = value; }
	

	public String getText() { return label; }
	

	public void setText(String text) { this.label = text; }
	

	public double getRotation() { return rotation; }
	

	public void setRotation(double angle) { this.rotation = angle; }


	public float getFontSize() { return getFont().getSize2D(); }
	

	public void setFontSize(float size) {
		setFont(getFont().deriveFont(size));
	}
	

	public void setFontBold(boolean value) {
		int style = getFont().getStyle();
		if(value) style |= Font.BOLD;
		else style &= ~Font.BOLD;
		setFont(getFont().deriveFont(style));
	}
	

	public void setFontItalic(boolean value) {
		int style = getFont().getStyle();
		if(value) style |= Font.ITALIC;
		else style &= ~Font.ITALIC;
		setFont(getFont().deriveFont(style));
	}
	

	public boolean isFontItalic() {
		return (getFont().getStyle() & Font.ITALIC) != 0;
	}
	

	public boolean isFontBold() {
		return (getFont().getStyle() & Font.BOLD) != 0;
	}
	

	public static final int ALIGN_CENTER = 0;

	public static final int ALIGN_LEFT = 1;

	public static final int ALIGN_RIGHT = 2;

	public static final int ALIGN_BOTTOM = 3;

	public static final int ALIGN_TOP = 4;

	public static final int ALIGN_BASELINE = 5;

	public static final int ALIGN_MIDRISE = 6;


	public static Font defaultFont = new Font("SansSerif", Font.BOLD, 15);

}


