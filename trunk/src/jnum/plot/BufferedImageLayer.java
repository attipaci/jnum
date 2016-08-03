/*******************************************************************************
 * Copyright (c) 2014 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
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
import jnum.data.Index2D;
import jnum.math.Range;

// TODO: Auto-generated Javadoc
/**
 * The Class BufferedImageLayer.
 */
public abstract class BufferedImageLayer extends ImageLayer {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5659356933524758138L;

	/** The buffer. */
	private BufferedImage buffer;
	
	/** The interpolation type. */
	private int interpolationType = AffineTransformOp.TYPE_NEAREST_NEIGHBOR;

	/** The start index. */
	private Index2D startIndex = new Index2D();	// The subarray offset to image
	
	/** The index to coords. */
	private AffineTransform indexToCoords = new AffineTransform();
	
	/** The coords to index. */
	private AffineTransform coordsToIndex = new AffineTransform();


	/* (non-Javadoc)
	 * @see kovacs.util.plot.PlotLayer#defaults()
	 */
	@Override
	public void defaults() {
		setFullArray();
		super.defaults();
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
	public abstract Dimension getDataSize();
	

	
	/**
	 * Gets the value.
	 *
	 * @param i the i
	 * @param j the j
	 * @return the value
	 */
	public abstract double getValue(int i, int j);

	
	
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
	 * Sets the subarray.
	 *
	 * @param fromi the fromi
	 * @param fromj the fromj
	 * @param toi the toi
	 * @param toj the toj
	 */
	public void setSubarray(int fromi, int fromj, int toi, int toj) {
		startIndex.set(fromi, fromj);
		createBuffer(toi - fromi, toj - fromj);
		if(verbose) Util.info(this, "Selecting " + fromi + "," + fromj + " -- " + toi + "," + toj);	
	}
	
	/**
	 * Sets the full array.
	 */
	public void setFullArray() {
		Dimension size = getDataSize();
		setSubarray(0, 0, size.width, size.height);
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
	 * @see kovacs.util.plot.ContentLayer#getCoordinateBounds()
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
	
	
	/**
	 * Gets the subarray offset.
	 *
	 * @return the subarray offset
	 */
	public Index2D getSubarrayOffset() { return startIndex; }
	
	/**
	 * Sets the subarray offset.
	 *
	 * @param index the new subarray offset
	 */
	public void setSubarrayOffset(Index2D index) { this.startIndex = index; }
	
	/**
	 * Sets the subarray offset.
	 *
	 * @param i the i
	 * @param j the j
	 */
	public void setSubarrayOffset(int i, int j) { startIndex.set(i,  j); }
	
	
	/**
	 * The Class Double.
	 */
	public static class Double extends BufferedImageLayer {
		
		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 6276800154911203125L;
		
		/** The data. */
		private double[][] data;
		
		/**
		 * Instantiates a new double.
		 */
		public Double() {}
		
		/**
		 * Instantiates a new double.
		 *
		 * @param data the data
		 */
		public Double(double[][] data) {
			setData(data);
		}
		
		/**
		 * Gets the data.
		 *
		 * @return the data
		 */
		public double[][] getData() { return data; }
		
		/**
		 * Sets the data.
		 *
		 * @param data the new data
		 */
		public void setData(double[][] data) {
			this.data = data;
			defaults();
		}
		
		/* (non-Javadoc)
		 * @see kovacs.util.plot.ImageLayer#getDataSize()
		 */
		@Override
		public Dimension getDataSize() {
			return new Dimension(data.length, data[0].length);
		}

		/* (non-Javadoc)
		 * @see kovacs.util.plot.ImageLayer#getValue(int, int)
		 */
		@Override
		public double getValue(int i, int j) {
			return data[i + getSubarrayOffset().i()][j + getSubarrayOffset().j()];
		}

		/* (non-Javadoc)
		 * @see kovacs.util.plot.ContentLayer#initialize()
		 */
		@Override
		public void initialize() {
			updateBuffer();
		}
	}
	
	
	/**
	 * The Class Float.
	 */
	public static class Float extends BufferedImageLayer {
		
		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = -341608880761068245L;
		
		/** The data. */
		private float[][] data;
		
		/**
		 * Instantiates a new float.
		 */
		public Float() {}
		
		/**
		 * Instantiates a new float.
		 *
		 * @param data the data
		 */
		public Float(float[][] data) {
			setData(data);
		}
		
		/**
		 * Gets the data.
		 *
		 * @return the data
		 */
		public float[][] getData() { return data; }
		
		/**
		 * Sets the data.
		 *
		 * @param data the new data
		 */
		public void setData(float[][] data) {
			this.data = data;
			defaults();
		}
		
		/* (non-Javadoc)
		 * @see kovacs.util.plot.ImageLayer#getDataSize()
		 */
		@Override
		public Dimension getDataSize() {
			return new Dimension(data.length, data[0].length);
		}

		/* (non-Javadoc)
		 * @see kovacs.util.plot.ImageLayer#getValue(int, int)
		 */
		@Override
		public double getValue(int i, int j) {
			return data[i + getSubarrayOffset().i()][j + getSubarrayOffset().j()];
		}

		/* (non-Javadoc)
		 * @see kovacs.util.plot.ContentLayer#initialize()
		 */
		@Override
		public void initialize() {
			updateBuffer();
		}

	}
}
