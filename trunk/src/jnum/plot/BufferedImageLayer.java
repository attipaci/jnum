/*******************************************************************************
 * Copyright (c) 2017 Attila Kovacs <attila[AT]sigmyne.com>.
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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import jnum.Util;
import jnum.data.image.Value2D;
import jnum.math.Range;

// TODO: Auto-generated Javadoc
/**
 * The Class BufferedImageLayer.
 */
public class BufferedImageLayer extends ImageLayer {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5659356933524758138L;

	private Value2D data;
	
	/** The buffer. */
	private BufferedImage buffer;
	
	/** The interpolation type. */
	private int interpolationType = AffineTransformOp.TYPE_NEAREST_NEIGHBOR;
	
	/** The index to coords. */
	private AffineTransform indexToCoords = new AffineTransform();
	
	/** The coords to index. */
	private AffineTransform coordsToIndex = new AffineTransform();


	public BufferedImageLayer(Value2D data) {
	    setData(data);
	    defaults();
	}
	
	public Value2D getData() { return data; }
	
	public void setData(Value2D data) { 
	    this.data = data; 
	    createBuffer(data.sizeX(), data.sizeY());
	}
	
	/**
	 * Gets the buffer size.
	 *
	 * @return the buffer size
	 */
	public final Dimension getBufferSize() { 
		return new Dimension(getBufferedImage().getWidth(), getBufferedImage().getHeight());
	}
	
	/**
	 * Gets the data size.
	 *
	 * @return the data size
	 */
	public final Dimension getDataSize() {
	    return new Dimension(data.sizeX(), data.sizeY());
	}
	

	
	/**
	 * Gets the value.
	 *
	 * @param i the i
	 * @param j the j
	 * @return the value
	 */
	public final double getValue(int i, int j) { return data.isValid(i, j) ? data.get(i, j).doubleValue() : Double.NaN; }

	
	
	/**
	 * Index to coords.
	 *
	 * @return the affine transform
	 */
	public AffineTransform indexToCoords() {
		return indexToCoords;
	}
	
	/**
	 * Coords to index.
	 *
	 * @return the affine transform
	 */
	public AffineTransform coordsToIndex() {
		return coordsToIndex;
	}
	

	/**
	 * Creates the buffer.
	 *
	 * @param width the width
	 * @param height the height
	 */
	public void createBuffer(int width, int height) {
		buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	}

	
	/**
	 * Draw image.
	 *
	 * @param g the g
	 */
	@Override
	protected void drawImage(Graphics g) {	
		Graphics2D g2 = (Graphics2D) g;
		
		if(getContentArea() == null) Util.warning(this, "null content area"); 
		if(getContentArea().toDisplay() == null) Util.warning(this, "null transform"); 
		
		AffineTransform indexToDisplay = new AffineTransform(getContentArea().toDisplay());
		indexToDisplay.concatenate(indexToCoords());
		AffineTransformOp op = new AffineTransformOp(indexToDisplay, interpolationType);
		g2.drawImage(buffer, op, 0, 0);				
	}

	
	/* (non-Javadoc)
     * @see jnum.plot.ContentLayer#initialize()
     */
    @Override
    public void initialize() {
        updateBuffer();
    }


	/**
	 * Update buffer.
	 */
	public void updateBuffer() {	
		for(int i=buffer.getWidth(); --i >= 0; ) for(int j=buffer.getHeight(); --j >= 0; ) 
			buffer.setRGB(i, j, getRGB(getValue(i, j)));
	}
	
	/**
	 * Gets the data range.
	 *
	 * @return the data range
	 */
	@Override
	public Range getDataRange() {
		Range range = new Range();
		for(int i=buffer.getWidth(); --i >=0; ) for(int j=buffer.getHeight(); --j >=0; ) {
			final double value = getValue(i, j);
			if(!java.lang.Double.isNaN(value)) range.include(value);
		}
		return range;	
	}

	/**
	 * Sets the coordinate transform.
	 *
	 * @param transform the new coordinate transform
	 * @throws NoninvertibleTransformException the noninvertible transform exception
	 */
	public void setCoordinateTransform(AffineTransform transform) throws NoninvertibleTransformException  {
		indexToCoords = transform;
		coordsToIndex = transform.createInverse();
	}
	
	/**
	 * Index to coordinates.
	 *
	 * @param point the point
	 * @return the point2 d
	 */
	public Point2D indexToCoordinates(Point2D point) {
		return indexToCoords.transform(point, point);
	}

	/**
	 * Coordinates to index.
	 *
	 * @param point the point
	 * @return the point2 d
	 */
	public Point2D coordinatesToIndex(Point2D point) {
		return coordsToIndex.transform(point, point);
	}	
	
	/* (non-Javadoc)
	 * @see jnum.plot.ContentLayer#getCoordinateBounds()
	 */
	@Override
	public Rectangle2D getCoordinateBounds() {	
		Point2D lb = indexToCoordinates(new Point2D.Double(0, buffer.getHeight()));
		Point2D lt = indexToCoordinates(new Point2D.Double(0, 0));
		Point2D rb = indexToCoordinates(new Point2D.Double(buffer.getWidth(), buffer.getHeight()));
		Point2D rt = indexToCoordinates(new Point2D.Double(buffer.getWidth(), 0));
		
		double minX = Math.min(lt.getX(), lb.getX());
		double maxX = Math.max(rt.getX(), rb.getX());
		double minY = Math.min(lb.getY(), rb.getY());
		double maxY = Math.max(lt.getY(), rt.getY());
		
		if(minX > maxX) { double temp = minX; minX = maxX; maxX = temp; }	
		if(minY > maxY) { double temp = minY; minY = maxY; maxY = temp; }
		
		return new Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY);
	}

	
	/**
	 * Gets the buffered image.
	 *
	 * @return the buffered image
	 */
	public BufferedImage getBufferedImage() { return buffer; }
	
	/**
	 * Sets the buffered image.
	 *
	 * @param im the new buffered image
	 */
	public void setBufferedImage(BufferedImage im) { this.buffer = im; }
	
	/**
	 * Gets the interpolation type.
	 *
	 * @return the interpolation type
	 */
	public int getInterpolationType() { return interpolationType; }
	
	/**
	 * Sets the interpolation type.
	 *
	 * @param value the new interpolation type
	 */
	public void setInterpolationType(int value) { interpolationType = value; }
	
	/**
	 * Sets the pixelized.
	 */
	public void setPixelized() { setInterpolationType(AffineTransformOp.TYPE_NEAREST_NEIGHBOR); }

	/**
	 * Sets the spline.
	 */
	public void setSpline() { setInterpolationType(AffineTransformOp.TYPE_BICUBIC); }
	
	
	
	
	
}
