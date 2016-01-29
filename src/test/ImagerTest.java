/*******************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
 * All rights reserved. 
 * 
 * This file is part of crush.
 * 
 *     crush is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     crush is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with crush.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Attila Kovacs <attila_kovacs[AT]post.harvard.edu> - initial API and implementation
 ******************************************************************************/
package test;

import java.awt.*;
import java.awt.event.*;


import javax.swing.*;

import jnum.data.GridMap2D;
import jnum.math.Coordinate2D;
import jnum.math.Vector2D;
import jnum.plot.BufferedImageLayer;
import jnum.plot.Data2DLayer;
import jnum.plot.GridImageLayer;
import jnum.plot.ImageArea;
import jnum.plot.SimpleLabel;
import jnum.plot.colorscheme.*;
import jnum.util.Unit;


// TODO: Auto-generated Javadoc
/**
 * The Class ImagerTest.
 */
public class ImagerTest {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		try {
			GridMap2D<?> map = new GridMap2D<Coordinate2D>("/home/pumukli/data/sharc2/images/VESTA.8293.fits");
			
			float[][] data = new float[10][10];
			for(int i=data.length; --i >= 0; ) for(int j=data[0].length; --j >= 0; )
				data[i][j] = (float) Math.random();	
			
			//GridImageLayer image = new GridImageLayer(map);
			//final ImageArea<GridImageLayer> imager = new ImageArea<GridImageLayer>();
			
			//ImageLayer image = new ImageLayer.Float(data);
			//image.defaults();
			
			
			
			final Data2DLayer image = new Data2DLayer(map);
			final ImageArea<Data2DLayer> imager = new ImageArea<Data2DLayer>();
			
			
			image.setColorScheme(new Colorful());
			imager.setContentLayer(image);
			//imager.invertAxes(true, true);
			
			//ColorBar colorbar = new ColorBar(imager, ColorBar.VERTICAL, 20);
			
			
			final JComponent cross = new JComponent() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 6303247317754045021L;

				@Override
				public void paintComponent(Graphics g) {
					g.setColor(Color.RED);
					
					int width = getWidth();
					int height = getHeight();
					
					g.drawLine(0, height/2, width, height/2);
					g.drawLine(width/2, 0, width/2, height);
				}
			};
		
			imager.setOpaque(false);
			
			final SimpleLabel simpleLabel = new SimpleLabel(imager, "Test Label") {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public Vector2D getPosition() {
					return new Vector2D(0.5 * imager.getWidth(), 0.5 * imager.getHeight());
				}
			};
			
			simpleLabel.setHorizontalTextAlign(SimpleLabel.ALIGN_CENTER);
			simpleLabel.setVerticalTextAlign(SimpleLabel.ALIGN_MIDRISE);
			simpleLabel.setRotation(45.0 * Unit.deg);
			
			
			
			JComponent root = new JComponent() {
				/**
				 * 
				 */
				private static final long serialVersionUID = -3036536847348729404L;

				@Override
				public void paintComponent(Graphics g) {
					// Set sizes of all subcomponents to make sure they are the same...
					imager.setSize(getSize());
					cross.setSize(getSize());
					
					// Set before rendering, otherwise not guaranteed
					//setComponentZOrder(cross, 0);
					//setComponentZOrder(imager, 1);
					
					//Turn on/off subcomponent visibility...
					//imager.setVisible(true);
					
					super.paintComponent(g);
					
					//simpleLabel.paint();
				}
			};
			
			
			
			
			root.add(imager, 0);
			root.add(cross, 0);
			
			
			//root.setComponentZOrder(label, 0);
			//root.setComponentZOrder(cross, 0);
			//root.setComponentZOrder(imager, 1);
		
			
			JFrame frame = new JFrame();
			frame.setSize(600, 600);
			

			frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					System.err.println();
					System.err.println();
					System.exit(0);
				}
			});	
			
			//Box box = Box.createHorizontalBox();
			//box.add(colorbar);
			//box.add(label);	
			
			frame.add(root, "Center");
			//frame.add(box, "East");
		
			frame.setVisible(true);
		
		}
		catch(Exception e) {
			e.printStackTrace();			
		}
	}
	
}
