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
package jnum.data;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.concurrent.ExecutorService;

import jnum.Constant;
import jnum.CopiableContent;
import jnum.Parallel;
import jnum.Unit;
import jnum.Util;
import jnum.io.fits.FitsExtras;
import jnum.math.Range;
import jnum.math.Scalable;
import jnum.math.Vector2D;
import jnum.text.TableFormatter;
import jnum.util.CompoundUnit;
import jnum.util.HashCode;
import nom.tam.fits.BasicHDU;
import nom.tam.fits.Fits;
import nom.tam.fits.FitsDate;
import nom.tam.fits.FitsException;
import nom.tam.fits.FitsFactory;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;
import nom.tam.fits.ImageHDU;
import nom.tam.util.Cursor;


// TODO: Auto-generated Javadoc
/**
 * The Class Data2D.
 */
public class Data2D implements Serializable, Cloneable, TableFormatter.Entries, Scalable, CopiableContent<Data2D> {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -251823028156537784L;
	
	/** The data. */
	private double[][] data;
	
	/** The flag. */
	private int[][] flag;
	
	/** The parallelism. */
	private int parallelism = Runtime.getRuntime().availableProcessors();
	
	/** The executor. */
	private ExecutorService executor;
	
	/** The base units. */
	private Hashtable<String, Unit> baseUnits;
	
	/** The unit. */
	private Unit unit = Unit.unity;
	
	/** The content type. */
	private String contentType = UNDEFINED;
	
	/** The interpolation type. */
	private int interpolationType = BICUBIC_SPLINE;
	
	/** The verbose. */
	private boolean verbose = false;
	
	/** The header. */
	public Header header;
	
	/** The history. */
	public Vector<String> history = new Vector<String>();
	
	/** The name. */
	private String name = UNDEFINED;
	
	/** The file name. */
	public String fileName;
	
	private Class<? extends Number> dataType;
	
	/** The creator. */
	public String creator = "jnum " + Util.getFullVersion();
		
	/**
	 * Instantiates a new data2 d.
	 */
	public Data2D() {
		defaults();
	}
	
	/**
	 * Instantiates a new data2 d.
	 *
	 * @param fileName the file name
	 * @throws Exception the exception
	 */
	public Data2D(String fileName) throws Exception {
		this();
		read(fileName);		
	}

	
	/**
	 * Instantiates a new data2 d.
	 *
	 * @param sizeX the size x
	 * @param sizeY the size y
	 */
	public Data2D(int sizeX, int sizeY) {
		this();
		setSize(sizeX, sizeY);
		fillFlag(1);
	}
	
	/**
	 * Instantiates a new data2 d.
	 *
	 * @param data the data
	 */
	public Data2D(double[][] data) {
		this();
		this.data = data;
		this.flag = new int[sizeX()][sizeY()];
	}
	
	/**
	 * Instantiates a new data2 d.
	 *
	 * @param data the data
	 * @param flag the flag
	 */
	public Data2D(double[][] data, int[][] flag) {
		this();
		this.data = data;
		this.flag = flag;
	}
	
	/**
	 * Defaults.
	 */
	public void defaults() {
		Locale.setDefault(Locale.US);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int hashcode = sizeX() ^ sizeY() ^ interpolationType;
		if(name != null) hashcode ^= name.hashCode();
		if(contentType != null) hashcode ^= contentType.hashCode();
		if(unit != null) hashcode ^= unit.hashCode();
		if(data != null) if(data.length > 0) hashcode ^= HashCode.sampleFrom(data);
		if(flag != null) if(flag.length > 0) hashcode ^= HashCode.sampleFrom(flag);
		return hashcode;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if(o == this) return true;
		if(!(o instanceof Data2D)) return false;
		if(!super.equals(o)) return false;
		
		Data2D other = (Data2D) o;
		
		if(sizeX() != other.sizeX()) return false;
		if(sizeY() != other.sizeY()) return false;
		if(interpolationType != other.interpolationType) return false;
		if(contentType != other.contentType) return false;
		if(!Util.equals(name, other.name)) return false;
		if(!Util.equals(unit, other.unit)) return false;
		if(!Arrays.equals(data, other.data)) return false;
		if(!Arrays.equals(flag, other.flag)) return false;
		
		return true;
	}
	
	/**
	 * Sets the executor.
	 *
	 * @param executor the new executor
	 */
	public void setExecutor(ExecutorService executor) {
		this.executor = executor;
	}
	
	/**
	 * Gets the executor.
	 *
	 * @return the executor
	 */
	public ExecutorService getExecutor() { return executor; }
	
	/**
	 * Adds the processing history.
	 *
	 * @param entry the entry
	 */
	public void addProcessingHistory(String entry) {
		history.add(" " + entry);
	}
	
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() { return name; }
	
	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) { this.name = name; }
	
	/**
	 * Gets the unit.
	 *
	 * @return the unit
	 */
	public final Unit getUnit() { return unit; }
	
	/**
	 * Sets the unit.
	 *
	 * @param u the new unit
	 */
	public void setUnit(Unit u) { this.unit = u; }
	
	/**
	 * Sets the unit.
	 *
	 * @param spec the new unit
	 */
	public void setUnit(String spec) {
		addBaseUnits();
		CompoundUnit u = new CompoundUnit();
		u.parse(spec, baseUnits);
		setUnit(u); 
	}
	
	/**
	 * Adds the base units.
	 */
	protected void addBaseUnits() {}
	
	/**
	 * Adds the base unit.
	 *
	 * @param u the u
	 * @param names the names
	 */
	public void addBaseUnit(Unit u, String names) {
		if(baseUnits == null) baseUnits = new Hashtable<String, Unit>();
		StringTokenizer values = new StringTokenizer(names, " \t,");
		while(values.hasMoreTokens()) baseUnits.put(values.nextToken(), u);
	}
	
	/**
	 * Gets the content type.
	 *
	 * @return the content type
	 */
	public String getContentType() { return contentType; }
	
	/**
	 * Sets the content type.
	 *
	 * @param value the new content type
	 */
	public void setContentType(String value) { contentType = value; }
	
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
	public void setInterpolationType(int value) { this.interpolationType = value; }
	
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
	 * Gets the data.
	 *
	 * @return the data
	 */
	public final double[][] getData() { return data; }
	
	/**
	 * Gets the flag.
	 *
	 * @return the flag
	 */
	public final int[][] getFlag() { return flag; }
	
	/**
	 * Sets the data.
	 *
	 * @param image the new data
	 */
	public void setData(double[][] image) {
		data = image;
	}
	
	/**
	 * Creates the default flag.
	 */
	public void createDefaultFlag() {
		// If the existing flag array is of different size, then destroy it...
		if(data == null) {
			flag = null;
			return;
		}
		
		if(flag != null) {
			if(flag.length != data.length) flag = null;
			else if(flag[0].length != data[0].length) flag = null;
		}
		
		if(flag == null) flag = new int[sizeX()][sizeY()];
		
		
		// Create default flag...
		new Task<Void>() {
			@Override
			protected void process(int i, int j) { flag[i][j] = Double.isNaN(data[i][j]) ? 1 : 0; }
		}.process();
	}
	
	/**
	 * Creates the level flag.
	 *
	 * @param flagValue the flag value
	 */
	public void createLevelFlag(final double flagValue) {
		// If the existing flag array is of different size, then destroy it...
		if(data == null) {
			flag = null;
			return;
		}
		
		if(flag != null) {
			if(flag.length != data.length) flag = null;
			else if(flag[0].length != data[0].length) flag = null;
		}
		
		if(flag == null) flag = new int[sizeX()][sizeY()];
		
		
		// Create default flag...
		new Task<Void>() {
			@Override
			protected void process(int i, int j) { flag[i][j] = data[i][j] == flagValue ? 1 : 0; }
		}.process();
	}
	
	
	/**
	 * Sets the flag.
	 *
	 * @param image the new flag
	 */
	public final void setFlag(int[][] image) { flag = image; }
	
	
	/**
	 * Gets the value.
	 *
	 * @param index the index
	 * @return the value
	 */
	public final double getValue(Index2D index) { return data[index.i()][index.j()]; }
	
	/**
	 * Gets the value.
	 *
	 * @param i the i
	 * @param j the j
	 * @return the value
	 */
	public final double getValue(int i, int j) { return data[i][j]; }
	
	/**
	 * Sets the value.
	 *
	 * @param i the i
	 * @param j the j
	 * @param value the value
	 */
	public final void setValue(int i, int j, double value) { data[i][j] = value; }
	
	/**
	 * Increment.
	 *
	 * @param i the i
	 * @param j the j
	 * @param d the d
	 */
	public final void increment(int i, int j, double d) { data[i][j] += d; }
	
	/**
	 * Decrement.
	 *
	 * @param i the i
	 * @param j the j
	 * @param d the d
	 */
	public final void decrement(int i, int j, double d) { data[i][j] -= d; }
	
	/**
	 * Scale value.
	 *
	 * @param i the i
	 * @param j the j
	 * @param factor the factor
	 */
	public final void scaleValue(int i, int j, double factor) { data[i][j] *= factor; }
	
	/**
	 * Checks if is na n.
	 *
	 * @param i the i
	 * @param j the j
	 * @return true, if is na n
	 */
	public final boolean isNaN(int i, int j) { return Double.isNaN(data[i][j]); }
	
	/**
	 * Checks if is infinite.
	 *
	 * @param i the i
	 * @param j the j
	 * @return true, if is infinite
	 */
	public final boolean isInfinite(int i, int j) { return Double.isInfinite(data[i][j]); }
	
	/**
	 * Checks if is indefinite.
	 *
	 * @param i the i
	 * @param j the j
	 * @return true, if is indefinite
	 */
	public final boolean isIndefinite(int i, int j) { return Double.isNaN(data[i][j]) || Double.isInfinite(data[i][j]); }
	
	/**
	 * Gets the flag.
	 *
	 * @param i the i
	 * @param j the j
	 * @return the flag
	 */
	public final int getFlag(int i, int j) { return flag[i][j]; }
	

	/**
	 * Sets the flag.
	 *
	 * @param i the i
	 * @param j the j
	 * @param value the value
	 */
	public final void setFlag(int i, int j, int value) { flag[i][j] = value; }
	

	/**
	 * Scale.
	 *
	 * @param value the value
	 */
	public final void setFlag(final int value) {	
		new Task<Void>() {
			@Override
			protected void process(int i, int j) { setFlag(i, j, value); }
		}.process();
	}
	
	/**
	 * Scale.
	 */
	public final void flag() {	
		new Task<Void>() {
			@Override
			protected void process(int i, int j) { flag(i, j); }
		}.process();
	}
	
	/**
	 * Flag.
	 *
	 * @param i the i
	 * @param j the j
	 */
	public final void flag(int i, int j) { flag[i][j] |= 1; }
	
	
	/**
	 * Scale.
	 */
	public final void unflag() {	
		new Task<Void>() {
			@Override
			public void process(int i, int j) { unflag(i, j); }
		}.process();
	}
	
	
	/**
	 * Unflag.
	 *
	 * @param i the i
	 * @param j the j
	 */
	public final void unflag(int i, int j) { flag[i][j] = 0; }
	
	/**
	 * Flag.
	 *
	 * @param i the i
	 * @param j the j
	 * @param pattern the pattern
	 */
	public final void flag(int i, int j, int pattern) { flag[i][j] |= pattern; }
	
	/**
	 * Unflag.
	 *
	 * @param i the i
	 * @param j the j
	 * @param pattern the pattern
	 */
	public final void unflag(int i, int j, int pattern) { flag[i][j] &= ~pattern; }
	
	/**
	 * Checks if is flagged.
	 *
	 * @param i the i
	 * @param j the j
	 * @return true, if is flagged
	 */
	public final boolean isFlagged(int i, int j) { return flag[i][j] != 0; }
	
	/**
	 * Checks if is unflagged.
	 *
	 * @param i the i
	 * @param j the j
	 * @return true, if is unflagged
	 */
	public final boolean isUnflagged(int i, int j) { return flag[i][j] == 0; }
	
	/**
	 * Checks if is flagged.
	 *
	 * @param i the i
	 * @param j the j
	 * @param pattern the pattern
	 * @return true, if is flagged
	 */
	public final boolean isFlagged(int i, int j, int pattern) { return (flag[i][j] & pattern) != 0; }
	
	/**
	 * Checks if is unflagged.
	 *
	 * @param i the i
	 * @param j the j
	 * @param pattern the pattern
	 * @return true, if is unflagged
	 */
	public final boolean isUnflagged(int i, int j, int pattern) { return (flag[i][j] & pattern) == 0; }
	
	/**
	 * Sets the parallel.
	 *
	 * @param n the new parallel
	 */
	public void setParallel(int n) { parallelism = Math.max(1, n); }
	
	/**
	 * No parallel.
	 */
	public void noParallel() { parallelism = 1; }
	
	/**
	 * Gets the parallel.
	 *
	 * @return the parallel
	 */
	public int getParallel() { return parallelism; }
	
	
	// TODO 
	// It may be practical to have these methods so more sophisticated algorithms can work on these
	// images. However, the case for it may not be so strong after all, and they create confusion...
	/**
	 * Gets the weight.
	 *
	 * @param i the i
	 * @param j the j
	 * @return the weight
	 */
	public double getWeight(int i, int j) { return 1.0; }
	
	/**
	 * Gets the rms.
	 *
	 * @param i the i
	 * @param j the j
	 * @return the rms
	 */
	public double getRMS(int i, int j) { return 1.0; }
	
	/**
	 * Gets the s2 n.
	 *
	 * @param i the i
	 * @param j the j
	 * @return the s2 n
	 */
	public double getS2N(int i, int j) { return valueAtIndex(i, j); }
	
	/**
	 * Gets the weight.
	 *
	 * @param index the index
	 * @return the weight
	 */
	public final double getWeight(final Index2D index) { return getWeight(index.i(), index.j()); }
	
	/**
	 * Gets the rms.
	 *
	 * @param index the index
	 * @return the rms
	 */
	public final double getRMS(final Index2D index) { return getRMS(index.i(), index.j()); }
	
	/**
	 * Gets the s2 n.
	 *
	 * @param index the index
	 * @return the s2 n
	 */
	public final double getS2N(final Index2D index) { return getS2N(index.i(), index.j()); }
	
	
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() {
		try { 
			Data2D clone = (Data2D) super.clone(); 
			clone.reuseIpolData = new InterpolatorData();
			return clone;
		}
		catch(CloneNotSupportedException e) { return null; }
	}
		
	/* (non-Javadoc)
	 * @see kovacs.util.Copiable#copy()
	 */
	@Override
	public final Data2D copy() { return copy(true); }
	
	/**
	 * Copy.
	 *
	 * @param copyContent the copy content
	 * @return the data2 d
	 */
	@Override
	public Data2D copy(boolean copyContent) {
		Data2D copy = (Data2D) clone();
		copy.destroy();
		copy.setSize(sizeX(), sizeY());
		if(copyContent) copy.copyImageOf(this);
		return copy;
	}
	
	/**
	 * Annihilate.
	 */
	public void destroy() {
		data = null;
		flag = null;
	}

	/**
	 * Conforms to.
	 *
	 * @param image the image
	 * @return true, if successful
	 */
	public boolean conformsTo(Data2D image) {
		if(data == null) return false;
		else if(sizeX() != image.sizeX()) return false;
		else if(sizeY() != image.sizeY()) return false;
		return true;
	}
	
	/**
	 * Copy image of.
	 *
	 * @param image the image
	 */
	public void copyImageOf(final Data2D image) {
		if(!conformsTo(image)) setSize(image.sizeX(), image.sizeY());
		
		new Task<Void>() {
			@Override
			protected void processX(int i) { copy(image, i); }
			@Override
			protected void process(int i, int j) {}
		}.process();	
	}
	
	/**
	 * Copy.
	 *
	 * @param image the image
	 * @param i the i
	 */
	protected void copy(Data2D image, int i) {
		System.arraycopy(image.data[i], 0, data[i], 0, sizeY()); 
		System.arraycopy(image.flag[i], 0, flag[i], 0, sizeY()); 
	}
	
	/**
	 * Copy to.
	 *
	 * @param dst the dst
	 */
	public void copyTo(final double[][] dst) {
		new Task<Void>() {
			final int sizeY = sizeY();
			@Override
			protected void processX(int i) { System.arraycopy(data[i], 0, dst[i], 0, sizeY); }
			@Override
			protected void process(int i, int j) {}
		}.process();	
	}
	

	
	/**
	 * Size x.
	 *
	 * @return the int
	 */
	public final int sizeX() { return data.length; }
	
	/**
	 * Size y.
	 *
	 * @return the int
	 */
	public final int sizeY() { return data[0].length; }
	
	/**
	 * Sets the size.
	 *
	 * @param x the x
	 * @param y the y
	 */
	public void setSize(int x, int y) {
		data = new double[x][y];
		flag = new int[x][y];
		
		new Task<Void>() {
			@Override
			protected void processX(int i) { Arrays.fill(flag[i], 1); }
			@Override
			protected void process(int i, int j) {}
		}.process();
		
	}
	
	/**
	 * No flag.
	 */
	public void noFlag() { fillFlag(0); }
	
	/**
	 * Fill flag.
	 *
	 * @param value the value
	 */
	public void fillFlag(final int value) {
		if(flag == null) flag = new int[sizeX()][sizeY()];
		else {
			new Task<Void>() {
				@Override
				protected void processX(int i) { Arrays.fill(flag[i], value); }
				@Override
				protected void process(int i, int j) {}
			}.process();	
		}
	}

	/**
	 * Fill.
	 *
	 * @param value the value
	 */
	public void fill(final double value) {
		if(flag == null) flag = new int[sizeX()][sizeY()];
		else {
			new Task<Void>() {
				@Override
				protected void processX(int i) { Arrays.fill(data[i], value); }
				@Override
				protected void process(int i, int j) {}
			}.process();	
		}
	}

	
	/**
	 * Sets the image.
	 *
	 * @param image the new image
	 */
	public void setImage(Data2D image) {
		data = image.data;
		flag = image.flag;
	}
	
	/**
	 * Adds the image.
	 *
	 * @param image the image
	 * @param scale the scale
	 */
	public void addImage(final double[][] image, final double scale) {
		new Task<Void>() {
			@Override
			protected void process(int i, int j) {
				data[i][j] += scale * image[i][j];
			}
		}.process();
	}

	/**
	 * Adds the image.
	 *
	 * @param image the image
	 */
	public void addImage(double[][] image) { addImage(image, 1.0); }

	/**
	 * Adds the value.
	 *
	 * @param x the x
	 */
	public void addValue(final double x) {
		new Task<Void>() {
			@Override
			protected void process(int i, int j) {
				data[i][j] += x;
			}
		}.process();
	}
	
	/**
	 * Clear.
	 */
	public void clear() {
		new Task<Void>() {
			@Override
			protected void process(int i, int j) { clear(i, j); }
		}.process();	
	}

	/**
	 * Clear history.
	 */
	public void clearHistory() { history.clear(); }
	
	/**
	 * Clear.
	 *
	 * @param i the i
	 * @param j the j
	 */
	public void clear(int i, int j) {
		zero(i, j);
		flag[i][j] = 1;
	}
	
	/**
	 * Zero.
	 */
	public void zero() {
		new Task<Void>() {
			@Override
			protected void process(int i, int j) { zero(i, j); }
		}.process();	
	}

	/**
	 * Zero.
	 *
	 * @param i the i
	 * @param j the j
	 */
	public void zero(int i, int j) {
		data[i][j] = 0.0;		
	}
	
	/**
	 * Value at.
	 *
	 * @param index the index
	 * @return the double
	 */
	public final double valueAt(final Index2D index) {
		return valueAtIndex(index.i(), index.j());
	}
	
	/**
	 * Value at index.
	 *
	 * @param i the i
	 * @param j the j
	 * @return the double
	 */
	public double valueAtIndex(int i, int j) { return flag[i][j] == 0 ? data[i][j] : Double.NaN; }
	
	/**
	 * Value at index.
	 *
	 * @param index the index
	 * @return the double
	 */
	public double valueAtIndex(Vector2D index) {
		return valueAtIndex(index.x(), index.y(), null);
	}
	
	/**
	 * Value at index.
	 *
	 * @param index the index
	 * @param ipolData the ipol data
	 * @return the double
	 */
	public double valueAtIndex(Vector2D index, InterpolatorData ipolData) {
		return valueAtIndex(index.x(), index.y(), ipolData);
	}
		
	/**
	 * Value at index.
	 *
	 * @param ic the ic
	 * @param jc the jc
	 * @return the double
	 */
	public double valueAtIndex(double ic, double jc) {
		return valueAtIndex(ic, jc, null);
	}
	
	/**
	 * Value at index.
	 *
	 * @param ic the ic
	 * @param jc the jc
	 * @param ipolData the ipol data
	 * @return the double
	 */
	public double valueAtIndex(double ic, double jc, InterpolatorData ipolData) {
		
		// The nearest data point (i,j)
		final int i = (int) Math.round(ic);
		final int j = (int) Math.round(jc);
		
		if(!isValid(i, j)) return Double.NaN;
		
		switch(interpolationType) {
		case NEAREST_NEIGHBOR : return data[i][j];
		case BILINEAR : return bilinearAt(ic, jc);
		case PIECEWISE_QUADRATIC : return piecewiseQuadraticAt(ic, jc);
		case BICUBIC_SPLINE : return ipolData == null ? splineAt(ic, jc) : splineAt(ic, jc, ipolData);
		}
		
		return Double.NaN;
	}
	
	// Bilinear interpolation
	/**
	 * Bilinear at.
	 *
	 * @param ic the ic
	 * @param jc the jc
	 * @return the double
	 */
	public double bilinearAt(double ic, double jc) {		
		final int i = (int)Math.floor(ic);
		final int j = (int)Math.floor(jc);
		
		final double di = ic - i;
		final double dj = jc - j;
		
		double sum = 0.0, sumw = 0.0;
		
		if(isValid(i, j)) {
			double w = (1.0 - di) * (1.0 - dj);
			sum += w * data[i][j];
			sumw += w;			
		}
		if(isValid(i+1, j)) {
			double w = di * (1.0 - dj);
			sum += w * data[i+1][j];
			sumw += w;	
		}
		if(isValid(i, j+1)) {
			double w = (1.0 - di) * dj;
			sum += w * data[i][j+1];
			sumw += w;	
		}
		if(isValid(i+1, j+1)) {
			double w = di * dj;
			sum += w * data[i+1][j+1];
			sumw += w;	
		}

		return sum / sumw;
	}
	
	
	// Interpolate (linear at edges, quadratic otherwise)	
	// Piecewise quadratic...
	/**
	 * Piecewise quadratic at.
	 *
	 * @param ic the ic
	 * @param jc the jc
	 * @return the double
	 */
	public double piecewiseQuadraticAt(double ic, double jc) {
		// Find the nearest data point (i,j)
		final int i = (int)Math.round(ic);
		final int j = (int)Math.round(jc);
		
		final double y0 = data[i][j];
		double ax=0.0, ay=0.0, bx=0.0, by=0.0;

		if(isValid(i+1,j)) {
			if(isValid(i-1, j)) {
				ax = 0.5 * (data[i+1][j] + data[i-1][j]) - y0;
				bx = 0.5 * (data[i+1][j] - data[i-1][j]);
			}
			else bx = data[i+1][j] - y0; // Fall back to linear...
		}
		else if(isValid(i-1, j)) bx = y0 - data[i-1][j];
	
		if(isValid(i,j+1)) {
			if(isValid(i,j-1)) {
				ay = 0.5 * (data[i][j+1] + data[i][j-1]) - y0;
				by = 0.5 * (data[i][j+1] - data[i][j-1]);
			}
			else by = data[i][j+1] - y0; // Fall back to linear...
		}
		else if(isValid(i,j-1)) by = y0 - data[i][j-1];
		
		ic -= i;
		jc -= j;
		
		return (ax*ic+bx)*ic + (ay*jc+by)*jc + y0;
	}
	
	
	/** The reuse ipol data. */
	private InterpolatorData reuseIpolData = new InterpolatorData();
	
	/**
	 * Spline at.
	 *
	 * @param ic the ic
	 * @param jc the jc
	 * @return the double
	 */
	public synchronized double splineAt(final double ic, final double jc) {	
		return splineAt(ic, jc, reuseIpolData);
	}
		
	// Performs a bicubic spline interpolation...
	/**
	 * Spline at.
	 *
	 * @param ic the ic
	 * @param jc the jc
	 * @param ipolData the ipol data
	 * @return the double
	 */
	public double splineAt(final double ic, final double jc, InterpolatorData ipolData) {	
		
		ipolData.centerOn(ic, jc);
		
		final SplineCoeffs splineX = ipolData.splineX;
		final SplineCoeffs splineY = ipolData.splineY;
			
		final int fromi = Math.max(0, splineX.minIndex());
		final int toi = Math.min(sizeX(), splineX.maxIndex());
		
		final int fromj = Math.max(0, splineY.minIndex());
		final int toj = Math.min(sizeY(), splineY.maxIndex());
		
		// Do the spline convolution...
		double sum = 0.0, sumw = 0.0;
		for(int i=toi; --i >= fromi; ) {
			final double ax = splineX.valueAt(i);
			for(int j=toj; --j >= fromj; ) if(flag[i][j] == 0) {
				final double w = ax * splineY.valueAt(j);
				sum += w * data[i][j];
				sumw += w;
			}
		}
		
		return sum / sumw;
	}
	
	/**
	 * Resample.
	 *
	 * @param from the from
	 */
	public void resample(Data2D from) {
		final Vector2D stretch = new Vector2D(sizeX() / from.sizeX(), sizeY() / from.sizeY());
	
		// Antialias filter
		if(stretch.x() > 1.0 || stretch.y() > 1.0) {
			from = from.copy(true);
			double a = Math.sqrt(stretch.x() * stretch.x() - 1.0);
			double b = Math.sqrt(stretch.y() * stretch.y() - 1.0);
			from.smooth(getGaussian(a, b));
		}
		
		final Data2D antialiased = from;

		// Interpolate to new array...
		new InterpolatingTask() {
			@Override
			protected void process(int i, int j) {
				data[i][j] = antialiased.valueAtIndex(i*stretch.x(), j*stretch.y(), getInterpolatorData());
				flag[i][j] = Double.isNaN(data[i][j]) ?  1 : 0;
			}
		}.process();
		
	}
	
	/**
	 * Gets the gaussian.
	 *
	 * @param sigma the sigma
	 * @return the gaussian
	 */
	public static double[][] getGaussian(double sigma) {
		return getGaussian(sigma, sigma);		
	}
	
	/**
	 * Gets the gaussian.
	 *
	 * @param sigmaX the a
	 * @param sigmaY the b
	 * @return the gaussian
	 */
	public static double[][] getGaussian(double sigmaX, double sigmaY) {
		sigmaX = Math.abs(sigmaX);
		sigmaY = Math.abs(sigmaY);

		int nx = 1 + 2 * (int)(Math.ceil(3.0 * sigmaX));
		int ny = 1 + 2 * (int)(Math.ceil(3.0 * sigmaY));
		
		double[][] beam = new double[nx][ny];
		int ic = nx/2;
		int jc = ny/2;
		
		for(int di=ic; --di >= 0; ) {
			final double devx = di / sigmaX;
			for(int dj=jc; --dj >= 0;) {
				final double devy = dj / sigmaY;	
				beam[ic-di][jc-dj] = beam[ic-di][jc+dj] = beam[ic+di][jc-dj] = beam[ic+di][jc+dj] = 
					Math.exp(-0.5 * (devx*devx + devy*devy));
			}
		}
		
		return beam;
	}
	
	/**
	 * Gets the gaussian.
	 *
	 * @param a the a
	 * @param b the b
	 * @param angle the angle
	 * @param sigmas the sigmas
	 * @return the gaussian
	 */
	public static double[][] getGaussian(double a, double b, double angle, double sigmas) {
		a = Math.abs(a);
		b = Math.abs(b);
		
		double c = Math.cos(angle);
		double s = Math.sin(angle);
		
		double ac = Math.abs(c);
		double as = Math.abs(s);
		
		int nx = 1 + 2 * (int)(Math.ceil(sigmas * (ac * a + as * b)));
		int ny = 1 + 2 * (int)(Math.ceil(sigmas * (ac * b + as * a)));
		
		double sigmas2 = sigmas * sigmas;
		
		double[][] beam = new double[nx][ny];
		int ic = nx/2;
		int jc = ny/2;
		
		for(int i=nx; --i >= 0; ) {
			for(int dj=jc; --dj >= 0; ) {
				final double devx = (c * (i-ic) - s * dj) / a;
				final double devy = (s * (i-ic) + c * dj) / b;
				final double d2 = devx*devx + devy*devy;
				if(d2 > sigmas2) continue;
				beam[i][jc-dj] = beam[i][jc+dj] = Math.exp(-0.5 * d2);
			}
		}
		
		return beam;
	}
	
	/**
	 * Contains index.
	 *
	 * @param i the i
	 * @param j the j
	 * @return true, if successful
	 */
	public boolean containsIndex(final int i, final int j) {
		if(i < 0) return false;
		if(j < 0) return false;
		if(i >= sizeX()) return false;
		if(j >= sizeY()) return false;
		return true;
	}
	
	/**
	 * Contains index.
	 *
	 * @param i the i
	 * @param j the j
	 * @return true, if successful
	 */
	public boolean containsIndex(final double i, final double j) {
		if(i < 0) return false;
		if(j < 0) return false;
		if(i >= sizeX()-0.5) return false;
		if(j >= sizeY()-0.5) return false;
		return true;
	}
	
	/**
	 * Checks if is valid.
	 *
	 * @param i the i
	 * @param j the j
	 * @return true, if is valid
	 */
	public boolean isValid(final int i, final int j) {
		if(!containsIndex(i, j)) return false;
		if(flag[i][j] != 0) return false;
		return true;
	}
	
	/**
	 * Checks if is valid.
	 *
	 * @param i the i
	 * @param j the j
	 * @return true, if is valid
	 */
	public boolean isValid(final double i, final double j) {
		if(!containsIndex(i, j)) return false;
		if(flag[(int)Math.round(i)][(int)Math.round(j)] != 0) return false;
		return true;
	}
	 
	
	/**
	 * Gets the pixel info.
	 *
	 * @param i the i
	 * @param j the j
	 * @return the pixel info
	 */
	public String getPixelInfo(final int i, final int j) {
		if(!isValid(i, j)) return "";
		String type = "";
		if(contentType != null) if(contentType.length() != 0) type = contentType + "> ";
		return type + Util.getDecimalFormat(1e3).format(data[i][j]) + " " + unit.name();
	}
	
	/**
	 * Scale.
	 *
	 * @param value the value
	 */
	@Override
	public final void scale(final double value) {
		if(value == 1.0) return;
		
		new Task<Void>() {
			@Override
			protected void process(int i, int j) {
				data[i][j] *= value;
			}
		}.process();
	}
	
	/**
	 * Scale.
	 *
	 * @param i the i
	 * @param j the j
	 * @param factor the factor
	 */
	public void scale(int i, int j, double factor) {
		data[i][j] *= factor;
	}

	/**
	 * Gets the min.
	 *
	 * @return the min
	 */
	public double getMin() { 
		Task<Double> search = new Task<Double>() {
			private double min = Double.POSITIVE_INFINITY;
			@Override
			protected void process(int i, int j) {
				if(flag[i][j]==0) if(data[i][j] < min) min = data[i][j];
			}
			@Override 
			public Double getLocalResult() { return min; }
			@Override
			public Double getResult() {
				double globalMin = Double.POSITIVE_INFINITY;
				for(Parallel<Double> task : getWorkers()) if(task.getLocalResult() < globalMin) globalMin = task.getLocalResult();
				return globalMin;
			}
		};
		search.process();
		return search.getResult();
	}

	/**
	 * Gets the max.
	 *
	 * @return the max
	 */
	public double getMax() {
		Task<Double> search = new Task<Double>() {
			private double max = Double.NEGATIVE_INFINITY;
			@Override
			protected void process(int i, int j) {
				if(flag[i][j] == 0) if(data[i][j] > max) max = data[i][j];
			}
			@Override 
			public Double getLocalResult() { return max; }
			@Override
			public Double getResult() {
				double globalMax = Double.NEGATIVE_INFINITY;
				for(Parallel<Double> task : getWorkers()) if(task.getLocalResult() > globalMax) globalMax = task.getLocalResult();
				return globalMax;
			}
		};
		search.process();
		return search.getResult();
	}

	/**
	 * Gets the range.
	 *
	 * @return the range
	 */
	public Range getRange() {
		Task<Range> search = new Task<Range>() {
			private Range range;
			@Override
			protected void init() {
				super.init();
				range = new Range();
			}
			@Override
			protected void process(int i, int j) {
				if(flag[i][j]==0) range.include(data[i][j]);
			}
			@Override 
			public Range getLocalResult() { return range; }
			@Override
			public Range getResult() {
				Range globalRange = new Range();
				for(Parallel<Range> task : getWorkers()) globalRange.include(task.getLocalResult());
				return globalRange;
			}
		};
		search.process();
		return search.getResult();
	}
	
	
	/**
	 * Index of max.
	 *
	 * @return the index2 d
	 */
	public Index2D indexOfMax() {	
		Task<Index2D> search = new Task<Index2D>() {
			private Index2D index;
			private double peak = Double.NEGATIVE_INFINITY;
			@Override
			protected void init() {
				super.init();
				index = new Index2D();
			}
			@Override
			protected void process(int i, int j) {
				if(flag[i][j] == 0) if(data[i][j] > peak) {
					peak = data[i][j];
					index.set(i, j);
				}
			}
			@Override 
			public Index2D getLocalResult() { return index; }
			@Override
			public Index2D getResult() {
				double globalPeak = Double.NEGATIVE_INFINITY;
				Index2D globalIndex = null;
				for(Parallel<Index2D> task : getWorkers()) {
					Index2D partial = task.getLocalResult();
					if(partial != null) if(data[partial.i()][partial.j()] > globalPeak) {
						globalIndex = partial;
						globalPeak = data[partial.i()][partial.j()];
					}
				}
				return globalIndex;
			}
		};
		
		search.process();
		return search.getResult();
	}
	
	/**
	 * Index of max dev.
	 *
	 * @return the index2 d
	 */
	public Index2D indexOfMaxDev() {
		Task<Index2D> search = new Task<Index2D>() {
			private Index2D index;
			private double dev = 0.0;
			@Override
			protected void init() {
				super.init();
				index = new Index2D();
			}
			@Override
			protected void process(int i, int j) {
				final double value = Math.abs(data[i][j]);
				if(flag[i][j] == 0) if(value > dev) {
					dev = value;
					index.set(i, j);
				}
			}
			@Override 
			public Index2D getLocalResult() { return index; }
			@Override
			public Index2D getResult() {
				double globalDev = 0.0;
				Index2D globalIndex = null;
				for(Parallel<Index2D> task : getWorkers()) {
					Index2D partial = task.getLocalResult();
					if(partial == null) continue;
					final double value = Math.abs(data[partial.i()][partial.j()]);
					if(value > globalDev) {
						globalIndex = partial;
						globalDev = value;
					}
				}
				return globalIndex;
			}
		};
		
		search.process();
		return search.getResult();
	}

	
	/**
	 * Mean.
	 *
	 * @return the double
	 */
	public double mean() {
		Task<WeightedPoint> average = new AveragingTask() {
			private double sum = 0.0, sumw = 0.0;
			@Override
			protected void process(int i, int j) { 
				if(flag[i][j] == 0) {
					sum  += data[i][j];
					sumw += getWeight(i, j);
				}
			}
			@Override
			public WeightedPoint getLocalResult() { return new WeightedPoint(sum, sumw); }
		};
		
		average.process();
		return average.getResult().value();	
	}
	
	/**
	 * Median.
	 *
	 * @return the double
	 */
	public double median() {
		float[] temp = new float[countPoints()];
		if(temp.length == 0) return 0.0;
		int n=0;
		for(int i=sizeX(); --i >= 0; ) for(int j=sizeY(); --j >= 0; ) if(flag[i][j]==0) temp[n++] = (float) data[i][j];
		return Statistics.median(temp, 0, n);
	}
	
	/**
	 * Select.
	 *
	 * @param fraction the fraction
	 * @return the double
	 */
	public double select(double fraction) {
		float[] temp = new float[sizeX() * sizeY()];
		int n=0;
		for(int i=sizeX(); --i >= 0; ) for(int j=sizeY(); --j >= 0; ) if(flag[i][j]==0) temp[n++] = (float) data[i][j];
		return Statistics.select(temp, fraction, 0, n);
	}

	
	/**
	 * Gets the rMS scatter.
	 *
	 * @return the rMS scatter
	 */
	public double getRMSScatter() {
		Task<WeightedPoint> rms = new AveragingTask() {
			private double sum = 0.0;
			private int n = 0;
			@Override
			protected void process(int i, int j) { 
				if(flag[i][j] == 0) {
					sum += data[i][j] * data[i][j];
					n++;
				}
			}
			@Override
			public WeightedPoint getLocalResult() { return new WeightedPoint(sum, n); }
		};
		
		rms.process();
		return rms.getResult().value();	
	}
	
	/**
	 * Gets the robust rms.
	 *
	 * @return the robust rms
	 */
	public double getRobustRMS() {
		float[] chi2 = new float[countPoints()];
		if(chi2.length == 0) return 0.0;

		for(int i=sizeX(), k=0; --i >= 0; ) for(int j=sizeY(); --j >= 0; ) if(flag[i][j] == 0) {
			final float value = (float) data[i][j];
			chi2[k++] = value * value;
		}
		
		return Math.sqrt(Statistics.median(chi2) / Statistics.medianNormalizedVariance);	
	}
	

	/**
	 * Clip below.
	 *
	 * @param level the level
	 */
	public void clipBelow(final double level) {
		new Task<Void>() {
			@Override
			protected void process(int i, int j) {
				if(flag[i][j] == 0) if(data[i][j] < level) flag[i][j] = 1;
			}
		}.process();
	}
	
	/**
	 * Clip above.
	 *
	 * @param level the level
	 */
	public void clipAbove(final double level) {
		new Task<Void>() {
			@Override
			protected void process(int i, int j) {
				if(flag[i][j] == 0) if(data[i][j] > level) flag[i][j] = 1;
			}
		}.process();
	}
	
	/**
	 * Count points.
	 *
	 * @return the int
	 */
	public int countPoints() {
		Task<Integer> counter = new Task<Integer>() {
			private int counter = 0;
			@Override
			protected void process(int i, int j) {
				if(flag[i][j] == 0) counter++;
			}
			@Override
			public Integer getLocalResult() {
				return counter;
			}
			@Override
			public Integer getResult() {
				int globalCount = 0;
				for(Parallel<Integer> task : getWorkers()) globalCount += task.getLocalResult();
				return globalCount;
			}
		};
		
		counter.process();
		return counter.getResult();
	}
	
	/**
	 * Checks if is empty.
	 *
	 * @return true, if is empty
	 */
	public boolean isEmpty() { return countPoints() == 0; }
	
	/**
	 * Crop.
	 *
	 * @param imin the imin
	 * @param jmin the jmin
	 * @param imax the imax
	 * @param jmax the jmax
	 */
	protected void crop(int imin, int jmin, int imax, int jmax) {
		if(verbose) System.err.println("Cropping to " + (imax - imin + 1) + "x" + (jmax - jmin + 1));
		
		double[][] olddata = data;
		int[][] oldflag = flag;
		
		final int fromi = Math.max(0, imin);
		final int fromj = Math.max(0, jmin);
		final int toi = Math.min(imax, sizeX()-1);
		final int toj = Math.min(jmax, sizeY()-1);		
		
		setSize(imax-imin+1, jmax-jmin+1);
		//fillFlag(1); // setSize takes care of initialization...
		
		for(int i=fromi, i1=fromi-imin; i<=toi; i++, i1++) for(int j=fromj, j1=fromj-jmin; j<=toj; j++, j1++) {
			data[i1][j1] = olddata[i][j];
			flag[i1][j1] = oldflag[i][j];
		}
		
	}	
	

	/**
	 * Gets the horizontal index range.
	 *
	 * @return the horizontal index range
	 */
	public int[] getHorizontalIndexRange() {
		int min = sizeX(), max = -1;
		for(int i=sizeX(); --i >= 0; ) for(int j=sizeY(); --j >= 0; ) if(flag[i][j] == 0) {
			if(i < min) min = i;
			if(i > max) max = i;
			break;
		}
		return max > min ? new int[] { min, max } : null;
	}
	
	/**
	 * Gets the vertical index range.
	 *
	 * @return the vertical index range
	 */
	public int[] getVerticalIndexRange() {
		int min = sizeY(), max = -1;
		for(int j=sizeY(); --j >= 0; ) for(int i=sizeX(); --i >= 0; ) if(flag[i][j] == 0) {
			if(j < min) min = j;
			if(j > max) max = j;
			break;
		}
		return max > min ? new int[] { min, max } : null;
	}
	
	/**
	 * Auto crop.
	 */
	public void autoCrop() {
		if(verbose) System.err.print("Auto-cropping. ");
		int[] hRange = getHorizontalIndexRange();
		int[] vRange = getVerticalIndexRange();
		if(verbose) System.err.println((hRange[1] - hRange[0] + 1) + "x" + (vRange[1] - vRange[0] + 1));
		this.crop(hRange[0], vRange[0], hRange[1], vRange[1]);
	}
	
	/**
	 * Gets the boolean flag.
	 *
	 * @return the boolean flag
	 */
	public boolean[][] getBooleanFlag() {
		final boolean[][] mask = new boolean[sizeX()][sizeY()];
		new Task<Void>() {
			@Override
			protected void process(int i, int j) {
				mask[i][j] = flag[i][j] > 0;
			}
		}.process();
		return mask;
	}
	
	/**
	 * Flag.
	 *
	 * @param mask the mask
	 * @param pattern the pattern
	 */
	public void flag(final boolean[][] mask, final int pattern) {
		new Task<Void>() {
			@Override
			protected void process(int i, int j) {
				if(mask[i][j]) flag[i][j] |= pattern;	
			}
		}.process(); 
	}
	
	/**
	 * Unflag.
	 *
	 * @param mask the mask
	 * @param pattern the pattern
	 */
	public void unflag(final boolean[][] mask, int pattern) {
		final int ipattern = ~pattern;
		new Task<Void>() {
			@Override
			protected void process(int i, int j) {
				if(mask[i][j]) flag[i][j] &= ipattern;	
			}
		}.process();
	}
	
	/**
	 * Level.
	 *
	 * @param robust the robust
	 * @return the double
	 */
	public double level(boolean robust) {
		double level = robust ? median() : mean();
		addValue(-level);
		return level;
	}
	
	/**
	 * Adds the weighted direct.
	 *
	 * @param image the image
	 * @param w the w
	 */
	public synchronized void addWeightedDirect(final Data2D image, final double w) {
		new Task<Void>() {		
			@Override
			protected void process(int i, int j) {
				if(image.isUnflagged(i, j)) addWeightedDirect(i, j, image, w * image.getWeight(i, j));
			}
		}.process();
		
		mergePropertiesWith(image);
	}
	
	/**
	 * Adds the weighted direct.
	 *
	 * @param i the i
	 * @param j the j
	 * @param src the src
	 * @param w the w
	 */
	protected void addWeightedDirect(int i, int j, Data2D src, double w) {
		data[i][j] += w * src.data[i][j];
		unflag(i, j);
	}
	
	/**
	 * Merge unflagged.
	 *
	 * @param image the image
	 */
	public synchronized void mergeUnflagged(final Data2D image) {
		new Task<Void>() {		
			@Override
			protected void process(int i, int j) {
				if(image.isUnflagged(i, j)) merge(i, j, image, image.getWeight(i, j));
			}
		}.process();
		
		mergePropertiesWith(image);
	}
	
	/**
	 * Merge non zero.
	 *
	 * @param image the image
	 */
	public synchronized void mergeNonZero(final Data2D image) {
		new Task<Void>() {		
			@Override
			protected void process(int i, int j) {
				if(image.data[i][j] != 0.0) merge(i, j, image, image.getWeight(i, j));
			}
		}.process();
		
		mergePropertiesWith(image);
	}
	
	/**
	 * Merge properties with.
	 *
	 * @param data the data
	 */
	protected synchronized void mergePropertiesWith(final Data2D data) {}

	
	/**
	 * Merge.
	 *
	 * @param i the i
	 * @param j the j
	 * @param src the src
	 * @param w the w
	 */
	protected void merge(int i, int j, Data2D src, double w) {
		data[i][j] += src.data[i][j];
		unflag(i, j);
	}
	
	
	/**
	 * Smooth.
	 *
	 * @param beam the beam
	 */
	public void smooth(double[][] beam) {
		double[][] beamw = new double[sizeX()][sizeY()];
		data = getSmoothed(beam, beamw);
	}
	
	/**
	 * Fast smooth.
	 *
	 * @param beam the beam
	 * @param stepX the step x
	 * @param stepY the step y
	 */
	public void fastSmooth(double[][] beam, int stepX, int stepY) {
		double[][] beamw = new double[sizeX()][sizeY()];
		data = getFastSmoothed(beam, beamw, stepX, stepY);
	}
	
	/**
	 * Gets the smoothed.
	 *
	 * @param beam the beam
	 * @param beamw the beamw
	 * @return the smoothed
	 */
	public double[][] getSmoothed(final double[][] beam, final double[][] beamw) {
		final double[][] convolved = new double[sizeX()][sizeY()];
		final int ic = (beam.length-1) / 2;
		final int jc = (beam[0].length-1) / 2;
	
		new Task<Void>() {
			private WeightedPoint result;
			@Override
			protected void init() {
				super.init();
				result = new WeightedPoint();
			}
			@Override
			protected void process(int i, int j) {
				if(flag[i][j] == 0) {
					getSmoothedValueAt(i, j, beam, ic, jc, result);
					convolved[i][j] = result.value();
					if(beamw != null) beamw[i][j] = result.weight();
				}
			}
		}.process();
		
		return convolved;
	}
	
	
	// Do the convolution proper at the specified intervals (step) only, and interpolate (quadratic) inbetween
	/**
	 * Gets the fast smoothed.
	 *
	 * @param beam the beam
	 * @param beamw the beamw
	 * @param stepX the step x
	 * @param stepY the step y
	 * @return the fast smoothed
	 */
	public double[][] getFastSmoothed(double[][] beam, final double[][] beamw, int stepX, int stepY) {
		if(stepX < 2 && stepY < 2) return getSmoothed(beam, beamw);
		
		final int ic = (beam.length-1) / 2;
		final int jc = (beam[0].length-1) / 2;
		
		final WeightedPoint result = new WeightedPoint();
		
		final int nx = sizeX()/stepX + 1;
		final int ny = sizeY()/stepY + 1;
 
		final Data2D signalImage = new Data2D(nx, ny);
		signalImage.interpolationType = interpolationType;
		
		Data2D weightImage = null;
		
		if(beamw != null) {
			weightImage = (Data2D) signalImage.clone();
			weightImage.data = new double[nx][ny];
		}
			
		for(int i=0, i1=0; i<sizeX(); i+=stepX, i1++) for(int j=0, j1=0; j<sizeY(); j+=stepY, j1++) {
			getSmoothedValueAt(i, j, beam, ic, jc, result);
			signalImage.data[i1][j1] = result.value();
			if(beamw != null) weightImage.data[i1][j1] = result.weight();
			signalImage.flag[i1][j1] = result.weight() > 0.0 ? 0 : 1;
		}
		
		final double[][] convolved = new double[sizeX()][sizeY()];
		final double istepX = 1.0 / stepX;
		final double istepY = 1.0 / stepY;
	
		final Data2D wI = weightImage;
		
		new InterpolatingTask() {
			@Override
			protected void process(int i, int j) {
				if(flag[i][j] == 0) {
					final double i1 = i * istepX;
					final double j1 = j * istepY;
					final double value = signalImage.valueAtIndex(i1, j1, getInterpolatorData());
					if(!Double.isNaN(value)) {		
						convolved[i][j] = value;
						if(beamw != null) beamw[i][j] = wI.valueAtIndex(i1, j1, getInterpolatorData());
					}
					else flag[i][j] = 1;
				}
			}
		}.process();
		
		return convolved;
	}
		
	
	// Beam fitting: I' = C * sum(wBI) / sum(wB2)
	// I(x) = I -> I' = I -> C = sum(wB2) / sum(wB)
	// I' = sum(wBI)/sum(wB)
	// rms = Math.sqrt(1 / sum(wB))
	/**
	 * Gets the smoothed value at.
	 *
	 * @param i the i
	 * @param j the j
	 * @param beam the beam
	 * @param ic the ic
	 * @param jc the jc
	 * @param result the result
	 * @return the smoothed value at
	 */
	public void getSmoothedValueAt(final int i, final int j, final double[][] beam, int ic, int jc, WeightedPoint result) {
		final int i0 = i - ic;
		final int fromi = Math.max(0, i0);
		final int toi = Math.min(sizeX(), i0 + beam.length);
		
		final int j0 = j - jc;
		final int fromj = Math.max(0, j0);
		final int toj = Math.min(sizeY(), j0 + beam[0].length);

		double sum = 0.0, sumw = 0.0;
		for(int i1=toi; --i1 >= fromi; ) for(int j1=toj; --j1 >= fromj; ) if(flag[i1][j1] == 0) {
			final double wB = getWeight(i1, j1) * beam[i1-i0][j1-j0];
			sum += wB * data[i1][j1];
			sumw += Math.abs(wB);		    
		}

		result.setValue(sum / sumw);
		result.setWeight(sumw);
	}
	
	
	/**
	 * Gets the skip.
	 *
	 * @param blankingValue the blanking value
	 * @return the skip
	 */
	public int[][] getSkip(final double blankingValue) {
		final int[][] skip = (int[][]) copyOf(getFlag());
		
		new Task<Void>() {
			@Override
			protected void process(int i, int j) {
				if(data[i][j] > blankingValue) skip[i][j] = 1;
			}
		}.process();
		
		return skip;
	}
	

	
	/**
	 * Sanitize.
	 */
	public void sanitize() {
		new Task<Void>() {
			@Override
			protected void process(int i, int j) {
				if(flag[i][j] != 0) sanitize(i, j);
				else if(Double.isNaN(data[i][j])) sanitize(i, j);
			}
		}.process();
	}
	
	/**
	 * Sanitize.
	 *
	 * @param i the i
	 * @param j the j
	 */
	protected void sanitize(final int i, final int j) { 
		flag[i][j] |= 1;
		data[i][j] = 0.0;		
	}
	

	/**
	 * Gets the histogram.
	 *
	 * @param image the image
	 * @param binSize the bin size
	 * @return the histogram
	 */
	public Vector2D[] getHistogram(final double[][] image, final double binSize) {
		Range range = getRange();
		
		int bins = 1 + (int)Math.round(range.max() / binSize) - (int)Math.round(range.min() / binSize);
		final Vector2D[] bin = new Vector2D[bins];
		for(int i=0; i<bins; i++) bin[i] = new Vector2D(i*binSize, 0.0);
		
		new Task<Void>() {
			@Override
			protected void process(int i, int j) {
				if(flag[i][j] == 0) bin[(int)Math.round(image[i][j] / binSize)].addY(1.0);
			}
		}.process();
		
		return bin;
	}
	
	
	
	/**
	 * Read.
	 *
	 * @param fileName the file name
	 * @throws Exception the exception
	 */
	public final void read(String fileName) throws Exception {	
		read(findFits(fileName));
	}
		
	/**
	 * Read.
	 *
	 * @param fileName the file name
	 * @param hduIndex the hdu index
	 * @throws Exception the exception
	 */
	public final void read(String fileName, int hduIndex) throws Exception {	
		read(findFits(fileName), hduIndex);
	}
	
	/**
	 * Read.
	 *
	 * @param fits the fits
	 * @throws Exception the exception
	 */
	public void read(Fits fits) throws Exception {
		read(fits, 0);
	}
	
	/**
	 * Read.
	 *
	 * @param fits the fits
	 * @param hduIndex the hdu index
	 * @throws Exception the exception
	 */
	public final void read(Fits fits, int hduIndex) throws Exception {
		read(fits.getHDU(hduIndex));
	}
	
	/**
	 * Read.
	 *
	 * @param hdu the hdu
	 * @throws Exception the exception
	 */
	public void read(BasicHDU<?> hdu) throws Exception {
		parseHeader(hdu.getHeader());
		readData(hdu);
	}
	
	/**
	 * Read data.
	 *
	 * @param hdu the hdu
	 * @throws FitsException the fits exception
	 */
	public void readData(BasicHDU<?> hdu) throws FitsException {
		int sizeX = header.getIntValue("NAXIS1");
		int sizeY = header.getIntValue("NAXIS2");
		setSize(sizeX, sizeY);
		setImage(hdu);
	}
	
	/**
	 * Parses the header.
	 *
	 * @param header the header
	 * @throws Exception the exception
	 */
	protected void parseHeader(Header header) throws Exception {
		this.header = header;
		
		creator = header.getStringValue("CREATOR");
		if(creator == null) creator = UNDEFINED;
	
		name = header.getStringValue("OBJECT");
		if(name == null) name = UNDEFINED;
		
		contentType = header.getStringValue("EXTNAME");
		if(contentType == null) contentType = UNDEFINED;
		
		String unitName = header.getStringValue("BUNIT");
		if(unitName != null) {
			try { unit = Unit.get(unitName); }
			catch(Exception e) { unit = new Unit(unitName, 1.0, false); }
		}
		
		parseHistory(header);
	}
		
	/**
	 * Adds the history.
	 *
	 * @param header the header
	 * @throws HeaderCardException the header card exception
	 */
	public void addHistory(Header header) throws HeaderCardException {
		for(int i=0; i<history.size(); i++) header.addLine(new HeaderCard("HISTORY", history.get(i), false));
	}
	
	/**
	 * Parses the history.
	 *
	 * @param header the header
	 */
	public void parseHistory(Header header) {
		history.clear();
		
		Cursor<String, HeaderCard> cursor = header.iterator();
		
		while(cursor.hasNext()) {
			HeaderCard card = (HeaderCard) cursor.next();
			if(card.getKey().equalsIgnoreCase("HISTORY")) {
				String comment = card.getComment();
				if(comment != null) history.add(comment);
			}
		}

		if(!history.isEmpty()) {
			System.err.println(" Processing History: " + history.size() + " entries found.");
			System.err.println("   --> Last: " + history.get(history.size() - 1));
		}
			
		//for(int i=0; i<history.size(); i++) System.err.println("#  " + history.get(i));
	}
	
	
	
	
	/**
	 * Creates the fits.
	 *
	 * @return the fits
	 * @throws HeaderCardException the header card exception
	 * @throws FitsException the fits exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public Fits createFits(Class<? extends Number> dataType) throws HeaderCardException, FitsException, IOException {
		FitsFactory.setUseHierarch(true);
		Fits fits = new Fits();	
		fits.addHDU(createHDU(dataType));
		return fits;
	}

	/**
	 * Find fits.
	 *
	 * @param name the name
	 * @return the fits
	 * @throws FitsException the fits exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public Fits findFits(String name) throws FitsException, IOException {
		FitsFactory.setUseHierarch(true);
		Fits fits;
		
		fits = new Fits(new File(name)); 
		fileName = name;
		
		return fits;
	}
	

	/**
	 * Sets the image.
	 *
	 * @param HDU the new image
	 * @throws FitsException the fits exception
	 */
	public void setImage(BasicHDU<?> HDU) throws FitsException {		
		Object image = HDU.getData().getData();
		final double u = unit.value();
		
		// TODO if the image is higher dimensional select first 2D sub-plane...
		
		try {
		    dataType = Float.class;
			final float[][] fdata = (float[][]) image;
			new Task<Void>() {
				@Override
				protected void process(int i, int j) {
					if(!Float.isNaN(fdata[j][i])) {
						setValue(i, j, fdata[j][i] * u);	    
						unflag(i, j);
					}
				}
			}.process();
		}
		
		catch(ClassCastException e) {
		    dataType = Double.class;
			final double[][] ddata = (double[][]) image;
			
			new Task<Void>() {
				@Override
				protected void process(int i, int j) {
					if(!Double.isNaN(ddata[j][i])) {
						setValue(i, j, ddata[j][i] * u);	    
						unflag(i, j);
					}
				}
			}.process();
		}
		
	}
	

	public final void write(String fileName) throws HeaderCardException, FitsException, IOException {
        write(fileName, dataType == null ? Float.class : dataType);
    }
	
	/**
	 * Write.
	 *
	 * @param name the name
	 * @throws HeaderCardException the header card exception
	 * @throws FitsException the fits exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void write(String name, Class<? extends Number> dataType) throws HeaderCardException, FitsException, IOException {
		FitsExtras.write(createFits(dataType), name);
		this.fileName = name;
		this.dataType = dataType;
		System.err.println(" Written " + name);
	}
	
	/**
	 * Edits the header.
	 *
	 * @param hdu the hdu
	 * @throws HeaderCardException the header card exception
	 * @throws FitsException the fits exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public final void editHeader(BasicHDU<?> hdu) throws HeaderCardException, FitsException, IOException {
		Header header = hdu.getHeader();
		Cursor<String, HeaderCard> cursor = header.iterator();
		
		// Go to the end of the header cards...
		while(cursor.hasNext()) cursor.next();
		editHeader(header, cursor);
		
		// Add the processing history...
		for(int i=0; i<history.size(); i++) cursor.add(new HeaderCard("HISTORY", history.get(i), null));
	}
	
	
		
	// TODO what about duplicate keywords (that's a cursor issue...)
	// ... Maybe check for duplicates...
	// TODO copy over existing header keys (non-conflicting...) 
	/**
	 * Edits the header.
	 *
	 * @param header the header
	 * @param cursor the cursor
	 * @throws HeaderCardException the header card exception
	 * @throws FitsException the fits exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void editHeader(Header header, Cursor<String, HeaderCard> cursor) throws HeaderCardException, FitsException, IOException {
		cursor.add(new HeaderCard("OBJECT", name, "The source name."));
		cursor.add(new HeaderCard("EXTNAME", contentType, "The type of data contained in this HDU"));
		cursor.add(new HeaderCard("DATE", FitsDate.getFitsDateString(), "Time-stamp of creation."));
		cursor.add(new HeaderCard("CREATOR", creator, "The software that created the image."));	
			
		Range range = getRange();

		cursor.add(new HeaderCard("DATAMIN", range.min() / unit.value(), "The lowest value in the image"));
		cursor.add(new HeaderCard("DATAMAX", range.max() / unit.value(), "The highest value in the image"));

		cursor.add(new HeaderCard("BZERO", 0.0, "Zeroing level of the image data"));
		cursor.add(new HeaderCard("BSCALE", 1.0, "Scaling of the image data"));
		cursor.add(new HeaderCard("BUNIT", unit.name(), "The image data unit."));
		
		//cursor.add(new HeaderCard("ORIGIN", "Caltech", "California Institute of Technology"));
	}
	
	private final double getStoreValue(final int i, final int j) {
	    return isUnflagged(i, j) ? getValue(i, j) / unit.value() : Double.NaN;
	}

	/**
	 * Creates the hdu.
	 *
	 * @return the image hdu
	 * @throws HeaderCardException the header card exception
	 * @throws FitsException the fits exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public ImageHDU createHDU(Class<? extends Number> dataType) throws HeaderCardException, FitsException, IOException {
	    Object fitsImage = null;
	  	   
		if(dataType.equals(Float.class)) {
		    final float[][] fImage = new float[sizeY()][sizeX()];
		    new Task<Void>() {
                @Override
                protected void process(int i, int j) { fImage[j][i] = (float) getStoreValue(i, j); }
            }.process();
            fitsImage = fImage;
		}
		else if(dataType.equals(Double.class)) {
		    final double[][] dImage = new double[sizeY()][sizeX()];
		    new Task<Void>() {
		        @Override
		        protected void process(int i, int j) { dImage[j][i] = getStoreValue(i, j); }
		    }.process();
		    fitsImage = dImage;
		}
		else throw new IllegalArgumentException("Data type must be Float.class or Double.class");
		
		ImageHDU hdu = (ImageHDU)Fits.makeHDU(fitsImage);
		editHeader(hdu);	
		addHistory(hdu.getHeader());
		
		return hdu;
	}
	
	/**
	 * Adds the long hierarch key.
	 *
	 * @param cursor the cursor
	 * @param key the key
	 * @param value the value
	 * @throws FitsException the fits exception
	 * @throws HeaderCardException the header card exception
	 */
	public void addLongHierarchKey(Cursor<String, HeaderCard> cursor, String key, String value) throws FitsException, HeaderCardException {
		addLongHierarchKey(cursor, key, 0, value);
	}

	/**
	 * Adds the long hierarch key.
	 *
	 * @param cursor the cursor
	 * @param key the key
	 * @param part the part
	 * @param value the value
	 * @throws FitsException the fits exception
	 * @throws HeaderCardException the header card exception
	 */
	public void addLongHierarchKey(Cursor<String, HeaderCard> cursor, String key, int part, String value) throws FitsException, HeaderCardException {
		if(value.length() == 0) value = "true";

		String alt = part > 0 ? "." + part : "";

		int available = 69 - (key.length() + alt.length() + 3);

		if(value.length() < available) cursor.add(new HeaderCard("HIERARCH." + key + alt, value, null));
		else { 
			if(alt.length() == 0) {
				part = 1;
				alt = "." + part;
				available -= 2;
			}

			cursor.add(new HeaderCard("HIERARCH." + key + alt, value.substring(0, available), null));
			addLongHierarchKey(cursor, key, (char)(part+1), value.substring(available)); 
		}
	}
	
	

	/**
	 * Sets the key.
	 *
	 * @param key the key
	 * @param value the value
	 * @throws HeaderCardException the header card exception
	 */
	public void setKey(String key, String value) throws HeaderCardException {
		String comment = header.containsKey(key) ? header.findCard(key).getComment() : "Set bu user.";

		// Try add as boolean, int or double -- fall back to String...
		try{ header.addValue(key, Util.parseBoolean(value), comment); }
		catch(NumberFormatException e1) { 
			try{ header.addValue(key, Integer.parseInt(value), comment); }
			catch(NumberFormatException e2) {
				try{ header.addValue(key, Double.parseDouble(value), comment); }
				catch(NumberFormatException e3) { header.addValue(key, value, comment); }
			}
		}
	}

	/**
	 * Prints the header.
	 */
	public void printHeader() {
		header.dumpHeader(System.out);
	}  
	
	
	
	/**
	 * The Class Task.
	 *
	 * @param <ReturnType> the generic type
	 */
	public abstract class Task<ReturnType> extends Parallel<ReturnType> {			
		
		/* (non-Javadoc)
		 * @see kovacs.util.Parallel#process(int)
		 */
		@Override
		public void process(int threadCount) {
			try { 
				if(executor != null) super.process(threadCount, executor);
				else {
					//System.err.println("# threads: " + threadCount);
					super.process(threadCount); 
				}
			}
			catch(Exception e) { e.printStackTrace(); }
		}
		
		/* (non-Javadoc)
		 * @see jnum.Parallel#process(int, java.util.concurrent.ExecutorService)
		 */
		@Override
		public void process(int chunks, ExecutorService executor) {
			//System.err.println("# pool: " + pool.getCorePoolSize());
			try { super.process(chunks, executor); }
			catch(Exception e) { e.printStackTrace(); }
		}
		
		
		/**
		 * Process.
		 */
		public void process() {
			if(executor != null) process(parallelism, executor);
			else process(parallelism);
		}
		
		/* (non-Javadoc)
		 * @see kovacs.util.Parallel#processIndex(int, int)
		 */
		@Override
		protected void processIndexOf(int index, int threadCount) {
			final int sizeX = sizeX();
			for(int i=index; i<sizeX; i += threadCount) {
				processX(i);
				Thread.yield();
			}
		}
	
		/**
		 * Process x.
		 *
		 * @param i the i
		 */
		protected void processX(int i) {
			for(int j=sizeY(); --j >= 0; ) process(i, j);
		}
		
		/**
		 * Process.
		 *
		 * @param i the i
		 * @param j the j
		 */
		protected abstract void process(int i, int j);
		
	}
	
	
	/**
	 * The Class AveragingTask.
	 */
	public abstract class AveragingTask extends Task<WeightedPoint> {
		
		/* (non-Javadoc)
		 * @see kovacs.util.Parallel#getResult()
		 */
		@Override
		public WeightedPoint getResult() {
			WeightedPoint ave = new WeightedPoint();
			for(Parallel<WeightedPoint> task : getWorkers()) {
				WeightedPoint partial = task.getLocalResult();
				ave.add(partial.value());
				ave.addWeight(partial.weight());
			}
			if(ave.weight() > 0.0) ave.scaleValue(1.0 / ave.weight());
			return ave;
		}
	}
	
	/**
	 * The Class InterpolatingTask.
	 */
	public abstract class InterpolatingTask extends Task<Void> {
		
		/** The ipol data. */
		private InterpolatorData ipolData;
		
		/* (non-Javadoc)
		 * @see kovacs.util.Parallel#init()
		 */
		@Override
		protected void init() { ipolData = new InterpolatorData(); }
		
		/**
		 * Gets the interpolator data.
		 *
		 * @return the interpolator data
		 */
		public final InterpolatorData getInterpolatorData() { return ipolData; }
	}	
	
	
	/**
	 * The Class InterpolatorData.
	 */
	public static class InterpolatorData {
		
		/** The spline y. */
		SplineCoeffs splineX, splineY;
		
		/**
		 * Instantiates a new interpolator data.
		 */
		public InterpolatorData() {
			splineX = new SplineCoeffs();
			splineY = new SplineCoeffs();
		}
		
		/**
		 * Center on.
		 *
		 * @param deltax the deltax
		 * @param deltay the deltay
		 */
		public void centerOn(double deltax, double deltay) {
			splineX.centerOn(deltax);
			splineY.centerOn(deltay);
		}

	}

	
	/**
	 * Copy of.
	 *
	 * @param image the image
	 * @return the object
	 */
	public static Object copyOf(final Object image) {
		if(image == null) return null;

		if(image instanceof double[][]) {
			final double[][] orig = (double[][]) image;
			final double[][] copy = new double[orig.length][orig[0].length];
			for(int i=orig.length; --i >= 0; ) System.arraycopy(orig[i], 0, copy[i], 0, orig[0].length);	
			return copy;
		}	
		else if(image instanceof int[][]) {
			final int[][] orig = (int[][]) image;
			final int[][] copy = new int[orig.length][orig[0].length];
			for(int i=orig.length; --i >= 0; ) System.arraycopy(orig[i], 0, copy[i], 0, orig[0].length);	
			return copy;
		}

		return null;
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.text.TableFormatter.Entries#getFormattedEntry(java.lang.String, java.lang.String)
	 */
	@Override
	public String getFormattedEntry(String name, String formatSpec) {
		if(name.equals("name")) return getName();
		else if(name.equals("contentType")) return contentType;
		else if(name.equals("creator")) return creator;
		else if(name.equals("unit")) return getUnit().name();
		else if(name.equals("filename")) return fileName;
		else if(name.equals("size")) return sizeX() + "x" + sizeY();
		else if(name.equals("sizeX")) return Integer.toString(sizeX());
		else if(name.equals("sizeY")) return Integer.toString(sizeY());
		else if(name.equals("points")) return Integer.toString(countPoints());
		else if(name.equals("min")) return TableFormatter.getNumberFormat(formatSpec).format(getMin() / getUnit().value());
		else if(name.equals("max")) return TableFormatter.getNumberFormat(formatSpec).format(getMax() / getUnit().value());
		else if(name.equals("rms")) return TableFormatter.getNumberFormat(formatSpec).format(getRobustRMS() / getUnit().value());
		else return "n/a";
	}
	
	
	// 2 pi sigma^2 = a^2
	// a = sqrt(2 pi) sigma
	//   = sqrt(2 pi) fwhm / 2.35
	/** The fwhm2size. */
	public static double fwhm2size = Math.sqrt(Constant.twoPi) / Constant.sigmasInFWHM;
	
	/** The undefined. */
	public static String UNDEFINED = "<undefined>";
	
	/** The Constant NEAREST_NEIGHBOR. */
	public final static int NEAREST_NEIGHBOR = 0;
	
	/** The Constant BILINEAR. */
	public final static int BILINEAR = 1;
	
	/** The Constant PIECEWISE_QUADRATIC. */
	public final static int PIECEWISE_QUADRATIC = 2;
	
	/** The Constant BICUBIC_SPLINE. */
	public final static int BICUBIC_SPLINE = 3;
	
}

