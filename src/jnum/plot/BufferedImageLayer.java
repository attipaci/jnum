/* *****************************************************************************
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
 *     Attila Kovacs  - initial API and implementation
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
import jnum.data.image.Values2D;
import jnum.math.Range;


public class BufferedImageLayer extends ImageLayer {

	private static final long serialVersionUID = -5659356933524758138L;

	private Values2D data;

	private BufferedImage buffer;
	
	private int interpolationType = AffineTransformOp.TYPE_NEAREST_NEIGHBOR;

	private AffineTransform indexToCoords = new AffineTransform();

	private AffineTransform coordsToIndex = new AffineTransform();


	public BufferedImageLayer(Values2D data) {
	    setData(data);
	    defaults();
	}
	
	public Values2D getData() { return data; }
	
	public void setData(Values2D data) { 
	    this.data = data; 
	    createBuffer(data.sizeX(), data.sizeY());
	}
	

	public final Dimension getBufferSize() { 
		return new Dimension(getBufferedImage().getWidth(), getBufferedImage().getHeight());
	}

	public final Dimension getDataSize() {
	    return new Dimension(data.sizeX(), data.sizeY());
	}
	

	public final double getValue(int i, int j) { return data.isValid(i, j) ? data.get(i, j).doubleValue() : Double.NaN; }


	public AffineTransform indexToCoords() {
		return indexToCoords;
	}
	

	public AffineTransform coordsToIndex() {
		return coordsToIndex;
	}
	

	public void createBuffer(int width, int height) {
		buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	}


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


    @Override
    public void initialize() {
        updateBuffer();
    }


	public void updateBuffer() {	
		for(int i=buffer.getWidth(); --i >= 0; ) for(int j=buffer.getHeight(); --j >= 0; ) 
			buffer.setRGB(i, j, getRGB(getValue(i, j)));
	}

	@Override
	public Range getDataRange() {
		Range range = new Range();
		for(int i=buffer.getWidth(); --i >=0; ) for(int j=buffer.getHeight(); --j >=0; ) {
			final double value = getValue(i, j);
			if(!java.lang.Double.isNaN(value)) range.include(value);
		}
		return range;	
	}


	public void setCoordinateTransform(AffineTransform transform) throws NoninvertibleTransformException  {
		indexToCoords = transform;
		coordsToIndex = transform.createInverse();
	}
	

	public Point2D indexToCoordinates(Point2D point) {
		return indexToCoords.transform(point, point);
	}


	public Point2D coordinatesToIndex(Point2D point) {
		return coordsToIndex.transform(point, point);
	}	

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


	public BufferedImage getBufferedImage() { return buffer; }
	

	public void setBufferedImage(BufferedImage im) { this.buffer = im; }
	

	public int getInterpolationType() { return interpolationType; }
	

	public void setInterpolationType(int value) { interpolationType = value; }

	public void setPixelized() { setInterpolationType(AffineTransformOp.TYPE_NEAREST_NEIGHBOR); }

	public void setSpline() { setInterpolationType(AffineTransformOp.TYPE_BICUBIC); }
	
	
	
	
	
}
