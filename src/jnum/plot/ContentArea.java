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

import jnum.Unit;
import jnum.Util;
import jnum.math.Coordinate2D;
import jnum.math.CoordinateSystem;
import jnum.math.Vector2D;




public class ContentArea<ContentType extends ContentLayer> extends JPanel implements Arrangeable {

	private static final long serialVersionUID = 5175315778375641551L;

	private ContentType contentLayer;

	private JComponent coordinateLayer;

	private Vector2D referencePoint = new Vector2D(0.0, 1.0); // lower left corner...

	private Vector2D scale = new Vector2D();

	private double rotation = 0.0;

	private boolean flipX = false, flipY = false;

	private int zoomMode = ZOOM_STRETCH;

	private AffineTransform toDisplay, toCoordinates = null;

	private boolean initialized = false;

	private boolean verbose = false;

	private Unit xUnit, yUnit;

	public CoordinateSystem coordinateSystem;

	public boolean isAutoAngleX = false, isAutoAngleY = false;
	

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


	public final AffineTransform toDisplay() { return toDisplay; }
	

	public final AffineTransform toCoordinates() { return toCoordinates; }
	

	public Vector2D getReferencePoint() { return referencePoint; }	
	

	public void setReferencePoint(Vector2D v) { referencePoint = v; }
	

	public Vector2D getReferencePixel() {
		Vector2D ref = getReferencePoint(); 
		ref.scaleX(getWidth());
		ref.scaleY(getHeight());
		return ref;
	}
	

	public void setReferencePixel(Vector2D v) {
		setReferencePoint(new Vector2D(v.x() / getWidth(), v.y() / getHeight()));
	}
	

	public ContentType getContentLayer() {
		return contentLayer;		
	}
	

	public JComponent getCoordinateLayer() { return coordinateLayer; }
	

	public void setContentLayer(ContentType layer) {
		if(contentLayer != null) {
			if(layer == contentLayer) return; // Nothing to do...
			remove(contentLayer);
		}
		contentLayer = layer;
		contentLayer.setContentArea(this);
		add(contentLayer, IMAGE_LAYER);
	}
	

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
	

	@Override
	public void arrange() {
	}
	

	public Vector2D getScale() { return scale; }
	

	public void setScale(Coordinate2D s) { setScale(s.x(), s.y()); }
	

	public void setScale(double x, double y) { this.scale.set(x, y); }
	

	public Unit getXUnit() { return xUnit; }
	

	public Unit getYUnit() { return yUnit; }
	

	public void setXUnit(Unit u) { xUnit = u; }
	

	public void setYUnit(Unit u) { yUnit = u; }
 	

	public void setRenderSize(int width, int height) {
		if(verbose) Util.info(this, "Setting render size to " + width + "x" + height);
		Rectangle2D bounds = contentLayer.getCoordinateBounds();
		scale.set(width / bounds.getWidth(), height / bounds.getHeight());
	}
	

	public void moveReference(double dx, double dy) {
		referencePoint.subtractX(dx / getWidth());
		referencePoint.subtractY(dy / getHeight());
	}
	

	public int getZoomMode() { return zoomMode; }
	

	public void setZoomMode(int value) { zoomMode = value; }
	

	public void setZoom(double value) {
		if(verbose) Util.info(this, "Setting zoom to " + value);
		scale.set(value, value);
	}


	public void zoom(double relative) {
		if(verbose) Util.info(this, "Zooming by " + relative);
		scale.scale(relative);
	}

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
	

	public void fit() { 	
		setZoomMode(ZOOM_FIT);
		updateZoom();
	}
	

	public void fill() { 
		setZoomMode(ZOOM_FILL);
		updateZoom();
	}

	public void setRotation(double angle) {
		rotation = angle;
	}
	

	public void invertAxes(boolean x, boolean y) {
		flipX = x;
		flipY = y;
	}

	public boolean isInvertedX() { return flipX; }
	

	public boolean isInvertedY() { return flipY; }

	
	public int getCenterX() {
		return (int) Math.round(0.5 * getWidth());
	}
	

	public int getCenterY() {
		return (int) Math.round(0.5 * getHeight());
	}


	@Override
	public void setSize(int w, int h) {
		super.setSize(w, h);
		for(Component c : getComponents()) c.setSize(w, h);
		updateTransforms();
	}
	

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
		
		if(contentLayer != null) {
		    Coordinate2D userOffset = contentLayer.getReferenceCoordinate();
		    toDisplay.translate(-userOffset.x(), -userOffset.y()); // Move by the desired offset in user coordinates...
		}
		    
		/*
		Point2D coordRef = contentLayer.getCoordinateReference();
		toDisplay.translate(-coordRef.getX(), -coordRef.getY());
		*/
		
		try { toCoordinates = toDisplay.createInverse(); }
		catch(NoninvertibleTransformException e) { toCoordinates = null; }
	}
	

	public Point2D toDisplay(Point2D point) {
		return toDisplay.transform(point, point);
	}


	public Point2D toCoordinates(Point2D point) {
		return toCoordinates.transform(point, point);
	}
	
	
	@Override
	public void paint(Graphics g) {	
		if(!initialized) initialize();
			
		// Make sure all children are the same size...
		Dimension d = getSize();
		for(Component c : getComponents()) c.setSize(d);
		
		super.paint(g);
	}
	
	// Returns a generated image.
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


	public void saveAs(String fileName, int width, int height) throws IOException {
		File file = new File(fileName);
		int iExt = fileName.lastIndexOf(".");
		String type = iExt > 0 && iExt < fileName.length() - 1 ? fileName.substring(iExt + 1) : "gif";    
		ImageIO.write(getRenderedImage(width, height), type, file);
		Util.notify(this, "Written " + fileName);
	}

	
	public boolean isVerbose() { return verbose; }
	
	public void setVerbose(boolean value) { verbose = value; }
	
	protected boolean isInitialized() { return initialized; }
	
	protected void setInitialized() { initialized = true; }

	// Assume that children overlap...
	@Override
	public boolean isOptimizedDrawingEnabled() { return false; }

	public static final int ZOOM_FIXED = 0;
	
	public static final int ZOOM_FIT = 1;

	public static final int ZOOM_FILL = 2;
	
	public static final int ZOOM_STRETCH = 3;

	public static int IMAGE_LAYER = 0;

	public static int COORDINATE_LAYER = 1;
	
}
