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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.OverlayLayout;

import jnum.math.Coordinate2D;
import jnum.math.CoordinateSystem;
import jnum.math.Vector2D;
import jnum.util.Unit;




// TODO: Auto-generated Javadoc
/**
 * The Class ContentArea.
 *
 * @param <ContentType> the generic type
 */
public class ContentArea<ContentType extends ContentLayer> extends JPanel implements Arrangeable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 5175315778375641551L;

	/** The content layer. */
	private ContentType contentLayer;
	
	/** The coordinate layer. */
	private JComponent coordinateLayer;
	
	/** The reference point. */
	private Vector2D referencePoint = new Vector2D(0.0, 1.0); // lower left corner...
	
	/** The scale. */
	private Vector2D scale = new Vector2D();
	
	/** The rotation. */
	private double rotation = 0.0;
	
	/** The flip y. */
	private boolean flipX = false, flipY = false;
	
	/** The zoom mode. */
	private int zoomMode = ZOOM_STRETCH;
	
	/** The to coordinates. */
	private AffineTransform toDisplay, toCoordinates = null;
	
	/** The initialized. */
	private boolean initialized = false;
	
	/** The verbose. */
	private boolean verbose = false;
	
	/** The y unit. */
	private Unit xUnit, yUnit;
	
	/** The coordinate system. */
	public CoordinateSystem coordinateSystem;
	
	/** The is auto angle y. */
	public boolean isAutoAngleX = false, isAutoAngleY = false;
	
	/**
	 * Instantiates a new content area.
	 */
	public ContentArea() {
		setOpaque(false);
		setLayout(new OverlayLayout(this));
		coordinateLayer = new JComponent() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 7993031801765435627L;

		};
		//add(coordinateLayer, COORDINATE_LAYER);
	}
	
	/**
	 * Instantiates a new content area.
	 *
	 * @param content the content
	 */
	public ContentArea(ContentType content) {
		this();
		setContentLayer(content);
	}
	
	/**
	 * Called before the first rendering of the image. Otherwise, it can be called before rendering, allowing
	 * to override the rendering parameters (e.g. rendering size, data scaling, subarray selection etc.)
	 */
	public void initialize() {
		if(isInitialized()) return;
		if(contentLayer != null) contentLayer.initialize();
		initialized = true;
	}

	/**
	 * To display.
	 *
	 * @return the affine transform
	 */
	public final AffineTransform toDisplay() { return toDisplay; }
	
	/**
	 * To coordinates.
	 *
	 * @return the affine transform
	 */
	public final AffineTransform toCoordinates() { return toCoordinates; }
	
	/**
	 * Gets the reference point.
	 *
	 * @return the reference point
	 */
	public Vector2D getReferencePoint() { return referencePoint; }	
	
	/**
	 * Sets the reference point.
	 *
	 * @param v the new reference point
	 */
	public void setReferencePoint(Vector2D v) { referencePoint = v; }
	
	/**
	 * Gets the reference pixel.
	 *
	 * @return the reference pixel
	 */
	public Vector2D getReferencePixel() {
		Vector2D ref = getReferencePoint(); 
		ref.scaleX(getWidth());
		ref.scaleY(getHeight());
		return ref;
	}
	
	/**
	 * Sets the reference pixel.
	 *
	 * @param v the new reference pixel
	 */
	public void setReferencePixel(Vector2D v) {
		setReferencePoint(new Vector2D(v.x() / getWidth(), v.y() / getHeight()));
	}
	
	/**
	 * Gets the content layer.
	 *
	 * @return the content layer
	 */
	public ContentType getContentLayer() {
		return contentLayer;		
	}
	
	/**
	 * Gets the coordinate layer.
	 *
	 * @return the coordinate layer
	 */
	public JComponent getCoordinateLayer() { return coordinateLayer; }
	
	/**
	 * Sets the content layer.
	 *
	 * @param layer the new content layer
	 */
	public void setContentLayer(ContentType layer) {
		if(contentLayer != null) {
			if(layer == contentLayer) return; // Nothing to do...
			remove(contentLayer);
		}
		contentLayer = layer;
		contentLayer.setContentArea(this);
		add(contentLayer, IMAGE_LAYER);
	}
	
	/**
	 * Gets the layers.
	 *
	 * @return the layers
	 */
	public Component[] getLayers() {
		Component[] layers = getComponents();
		Arrays.sort(layers, new Comparator<Component>() {
			@Override
			public int compare(Component c1, Component c2) {
				int z1 = getComponentZOrder(c1);
				int z2 = getComponentZOrder(c2);
				if(z1 == z2) return 0;
				return z1 < z2 ? -1 : 1;
			}
		});
		return layers;
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.plot.Arrangeable#arrange()
	 */
	@Override
	public void arrange() {
	}
	
	/**
	 * Gets the scale.
	 *
	 * @return the scale
	 */
	public Vector2D getScale() { return scale; }
	
	/**
	 * Sets the scale.
	 *
	 * @param s the new scale
	 */
	public void setScale(Coordinate2D s) { setScale(s.x(), s.y()); }
	
	/**
	 * Sets the scale.
	 *
	 * @param x the x
	 * @param y the y
	 */
	public void setScale(double x, double y) { this.scale.set(x, y); }
	
	/**
	 * Gets the x unit.
	 *
	 * @return the x unit
	 */
	public Unit getXUnit() { return xUnit; }
	
	/**
	 * Gets the y unit.
	 *
	 * @return the y unit
	 */
	public Unit getYUnit() { return yUnit; }
	
	/**
	 * Sets the x unit.
	 *
	 * @param u the new x unit
	 */
	public void setXUnit(Unit u) { xUnit = u; }
	
	/**
	 * Sets the y unit.
	 *
	 * @param u the new y unit
	 */
	public void setYUnit(Unit u) { yUnit = u; }
 	
	/**
	 * Sets the render size.
	 *
	 * @param width the width
	 * @param height the height
	 */
	public void setRenderSize(int width, int height) {
		if(verbose) System.err.println("Setting render size to " + width + "x" + height);
		Rectangle2D bounds = contentLayer.getCoordinateBounds();
		scale.set(width / bounds.getWidth(), height / bounds.getHeight());
	}
	
	/**
	 * Move reference.
	 *
	 * @param dx the dx
	 * @param dy the dy
	 */
	public void moveReference(double dx, double dy) {
		referencePoint.subtractX(dx / getWidth());
		referencePoint.subtractY(dy / getHeight());
	}
	
	

	/**
	 * Gets the zoom mode.
	 *
	 * @return the zoom mode
	 */
	public int getZoomMode() { return zoomMode; }
	
	/**
	 * Sets the zoom mode.
	 *
	 * @param value the new zoom mode
	 */
	public void setZoomMode(int value) { zoomMode = value; }
	
	/**
	 * Sets the zoom.
	 *
	 * @param value the new zoom
	 */
	public void setZoom(double value) {
		if(verbose) System.err.println("Setting zoom to " + value);
		scale.set(value, value);
	}

	/**
	 * Zoom.
	 *
	 * @param relative the relative
	 */
	public void zoom(double relative) {
		if(verbose) System.err.println("Zooming by " + relative);
		scale.scale(relative);
	}
	
	
	/**
	 * Update zoom.
	 */
	protected void updateZoom() {
		
		Rectangle2D bounds = null;
		switch(zoomMode) {
		case ZOOM_FIT : 
			bounds = contentLayer.getCoordinateBounds();
			setZoom(Math.min(getWidth() / bounds.getWidth(), getHeight() / bounds.getHeight()));
			contentLayer.center();
			break;
		case ZOOM_FILL : 
			bounds = contentLayer.getCoordinateBounds();
			setZoom(Math.min(getWidth() / bounds.getWidth(), getHeight() / bounds.getHeight()));		
			contentLayer.center();
			break; 
		case ZOOM_STRETCH : setRenderSize(getWidth(), getHeight()); break;
		case ZOOM_FIXED : break;	// Nothing to do, it stays where it was set by setZoom or setRenderSize
		default : setRenderSize(300, 300);
		}		
	}
	
	/**
	 * Fit.
	 */
	public void fit() { 	
		setZoomMode(ZOOM_FIT);
		updateZoom();
	}
	

	/**
	 * Fill.
	 */
	public void fill() { 
		setZoomMode(ZOOM_FILL);
		updateZoom();
	}
	
	/**
	 * Sets the rotation.
	 *
	 * @param angle the new rotation
	 */
	public void setRotation(double angle) {
		rotation = angle;
	}
	
	/**
	 * Invert axes.
	 *
	 * @param x the x
	 * @param y the y
	 */
	public void invertAxes(boolean x, boolean y) {
		flipX = x;
		flipY = y;
	}
	
	/**
	 * Checks if is inverted x.
	 *
	 * @return true, if is inverted x
	 */
	public boolean isInvertedX() { return flipX; }
	
	/**
	 * Checks if is inverted y.
	 *
	 * @return true, if is inverted y
	 */
	public boolean isInvertedY() { return flipY; }
	
	/**
	 * Gets the center x.
	 *
	 * @return the center x
	 */
	public int getCenterX() {
		return (int) Math.round(0.5 * getWidth());
	}
	
	/**
	 * Gets the center y.
	 *
	 * @return the center y
	 */
	public int getCenterY() {
		return (int) Math.round(0.5 * getHeight());
	}

	/* (non-Javadoc)
	 * @see java.awt.Component#setSize(int, int)
	 */
	@Override
	public void setSize(int w, int h) {
		super.setSize(w, h);
		for(Component c : getComponents()) c.setSize(w, h);
		updateTransforms();
	}
	
	/**
	 * Update transforms.
	 */
	protected void updateTransforms() {
		toDisplay = new AffineTransform();
			
		updateZoom();
				
		toDisplay.translate(
				referencePoint.x() * getWidth(), 
				referencePoint.y() * getHeight()
		);	// Move image to the referencepoint of the panel.
			
			
		if(flipX ^ coordinateSystem.get(0).reverse) toDisplay.scale(-1.0, 1.0);	// invert axes as desired
		if(!(flipY ^ coordinateSystem.get(1).reverse)) toDisplay.scale(1.0, -1.0);	// invert axes as desired
		
		toDisplay.scale(scale.x(), scale.y());		// Rescale to image size
		toDisplay.rotate(rotation);				// Rotate by the desired amount
		
		Coordinate2D userOffset = contentLayer.getReferenceCoordinate();
		if(contentLayer != null) toDisplay.translate(-userOffset.x(), -userOffset.y()); // Move by the desired offset in user coordinates...
		
		/*
		Point2D coordRef = contentLayer.getCoordinateReference();
		toDisplay.translate(-coordRef.getX(), -coordRef.getY());
		*/
		
		try { toCoordinates = toDisplay.createInverse(); }
		catch(NoninvertibleTransformException e) { toCoordinates = null; }
	}
	
	/**
	 * To display.
	 *
	 * @param point the point
	 * @return the point2 d
	 */
	public Point2D toDisplay(Point2D point) {
		return toDisplay.transform(point, point);
	}

	/**
	 * To coordinates.
	 *
	 * @param point the point
	 * @return the point2 d
	 */
	public Point2D toCoordinates(Point2D point) {
		return toCoordinates.transform(point, point);
	}
	
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	@Override
	public void paint(Graphics g) {	
		if(!initialized) initialize();
			
		// Make sure all children are the same size...
		Dimension d = getSize();
		for(Component c : getComponents()) c.setSize(d);
		
		super.paint(g);
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

	/**
	 * Save as.
	 *
	 * @param fileName the file name
	 * @param width the width
	 * @param height the height
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void saveAs(String fileName, int width, int height) throws IOException {
		File file = new File(fileName);
		int iExt = fileName.lastIndexOf(".");
		String type = iExt > 0 && iExt < fileName.length() - 1 ? fileName.substring(iExt + 1) : "gif";    
		ImageIO.write(getRenderedImage(width, height), type, file);
		System.err.println(" Written " + fileName);
	}

	
	/**
	 * Checks if is verbose.
	 *
	 * @return true, if is verbose
	 */
	public boolean isVerbose() { return verbose; }
	
	/**
	 * Sets the verbose.
	 *
	 * @param value the new verbose
	 */
	public void setVerbose(boolean value) { verbose = value; }
	
	/**
	 * Checks if is initialized.
	 *
	 * @return true, if is initialized
	 */
	protected boolean isInitialized() { return initialized; }
	
	/**
	 * Sets the initialized.
	 */
	protected void setInitialized() { initialized = true; }

	// Assume that children overlap...
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#isOptimizedDrawingEnabled()
	 */
	@Override
	public boolean isOptimizedDrawingEnabled() { return false; }
	
	/** The Constant ZOOM_FIXED. */
	public static final int ZOOM_FIXED = 0;
	
	/** The Constant ZOOM_FIT. */
	public static final int ZOOM_FIT = 1;
	
	/** The Constant ZOOM_FILL. */
	public static final int ZOOM_FILL = 2;
	
	/** The Constant ZOOM_STRETCH. */
	public static final int ZOOM_STRETCH = 3;

	
	/** The image layer. */
	public static int IMAGE_LAYER = 0;
	
	/** The coordinate layer. */
	public static int COORDINATE_LAYER = 1;
	
}
