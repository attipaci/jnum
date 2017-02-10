/*******************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila[AT]sigmyne.com>.
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
 *     Attila Kovacs <attila[AT]sigmyne.com> - initial API and implementation
 ******************************************************************************/
package test;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

import jnum.data.GridMap2D;
import jnum.math.Coordinate2D;
import jnum.plot.ColorBar;
import jnum.plot.Data2DLayer;
import jnum.plot.GridImageLayer;
import jnum.plot.ImageArea;
import jnum.plot.ImageLayer;
import jnum.plot.Plot;
import jnum.plot.PlotSideRuler;
import jnum.plot.colorscheme.*;


// TODO: Auto-generated Javadoc
/**
 * The Class PlotTest.
 */
public class PlotTest {
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		try {
			new PlotTest().test();
		}
		catch(Exception e) { e.printStackTrace(); }
	}
	
	/**
	 * Test.
	 *
	 * @throws Exception the exception
	 */
	public void test() throws Exception {
		
		GridMap2D<?> map = new GridMap2D<Coordinate2D>("/home/pumukli/data/sharc2/images/VESTA.8293.fits");
		map.autoCrop();
		
		GridImageLayer image = new GridImageLayer(map.getS2NImage());
		image.setColorScheme(new Temperature());
		
		final ImageArea<GridImageLayer> imager = new ImageArea<GridImageLayer>();
		imager.setContentLayer(image);
		imager.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		Plot<ImageLayer> plot = new Plot<ImageLayer>();
		//plot.setRulers(false);
		plot.setContent(imager);
		plot.setBackground(Color.WHITE);
		plot.setOpaque(true);
	
		
		ColorBar c = new ColorBar(image);
		c.setName("S/N");
		plot.right.setCenter(c);
		//c.setRotation(1*Unit.deg);
		
		JFrame frame = new JFrame();
		frame.setSize(600, 600);
		frame.setBackground(Color.WHITE);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
		frame.add(plot, "Center");
	
		frame.setVisible(true);
	
	}
}
