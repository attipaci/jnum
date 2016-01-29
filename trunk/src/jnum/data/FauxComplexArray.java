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

import java.io.Serializable;

import jnum.CopiableContent;
import jnum.ExtraMath;
import jnum.fft.DoubleFFT;
import jnum.fft.FFT;
import jnum.fft.FloatFFT;
import jnum.math.Complex;

// TODO: Auto-generated Javadoc
/**
 * The Class FauxComplexArray.
 *
 * @param <Type> the generic type
 */
public abstract class FauxComplexArray<Type> implements Serializable, Cloneable, CopiableContent<FauxComplexArray<Type>> {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -7774331934035418157L;
	
	/** The size. */
	private int size;
	
	/**
	 * Instantiates a new faux complex array.
	 *
	 * @param size the size
	 */
	private FauxComplexArray(int size) { this.size = size; }
	
	
	/**
	 * Instantiates a new double.
	 *
	 * @param data the data
	 */
	public FauxComplexArray(Complex[] data) {
		this(data.length);
		for(int i=data.length; --i >= 0; ) set(i, data[i]);
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() {
		try { return super.clone(); }
		catch(CloneNotSupportedException e) { return null; }
	}

	/* (non-Javadoc)
	 * @see kovacs.util.Copiable#copy()
	 */
	@Override
	public final FauxComplexArray<Type> copy() { return copy(true); }
	
	/* (non-Javadoc)
	 * @see kovacs.util.CopiableContent#copy(boolean)
	 */
	@Override
	public abstract FauxComplexArray<Type> copy(boolean fill);
		
	
	/**
	 * Gets the data.
	 *
	 * @return the data
	 */
	public abstract Type getData();
	
	/**
	 * Gets the.
	 *
	 * @param i the i
	 * @return the double
	 */
	protected abstract double get(int i);
	
	/**
	 * Sets the.
	 *
	 * @param i the i
	 * @param value the value
	 */
	protected abstract void set(int i, double value);
	
	
	/**
	 * Gets the.
	 *
	 * @param i the i
	 * @param element the element
	 */
	public final void get(int i, final Complex element) {
		element.set(get(i), get(i+1));		
	}
	
	/**
	 * Sets the.
	 *
	 * @param i the i
	 * @param value the value
	 */
	public final void set(int i, final Complex value) {
		i <<= 1;
		set(i, value.x());
		set(i+1, value.y());
	}
	
	/**
	 * Conjugate.
	 *
	 * @param i the i
	 */
	public final void conjugate(int i) {
		i = i<<1 + 1;
		set(i, -get(i));
	}
	
	/**
	 * Norm.
	 *
	 * @param i the i
	 * @return the double
	 */
	public final double norm(int i) {
		i <<= 1;
		return ExtraMath.sumSquares(get(i), get(i+1));
	}
	
	/**
	 * Length.
	 *
	 * @param i the i
	 * @return the double
	 */
	public final double length(int i) {
		i <<= 1;
		return ExtraMath.hypot(get(i), get(i+1));
	}
	
	/**
	 * Angle.
	 *
	 * @param i the i
	 * @return the double
	 */
	public final double angle(int i) {
		i <<= 1;
		return Math.atan2(get(i+1), get(i));
	}
	
	/**
	 * Cos angle.
	 *
	 * @param i the i
	 * @return the double
	 */
	public final double cosAngle(int i) {
		i <<= 1;
		return ExtraMath.cos(get(i), get(i+1));
	}
	
	/**
	 * Sin angle.
	 *
	 * @param i the i
	 * @return the double
	 */
	public final double sinAngle(int i) {
		i <<= 1;
		return ExtraMath.sin(get(i), get(i+1));
	}
	
	/**
	 * Tan angle.
	 *
	 * @param i the i
	 * @return the double
	 */
	public final double tanAngle(int i) {
		i <<= 1;
		return ExtraMath.tan(get(i), get(i+1));
	}
	
	/**
	 * Increment.
	 *
	 * @param i the i
	 * @param value the value
	 */
	public final void increment(int i, final Complex value) {
		i <<= 1;
		set(i, get(i) + value.x());
		set(++i, get(i) + value.y());
	}
	
	/**
	 * Decrement.
	 *
	 * @param i the i
	 * @param value the value
	 */
	public final void decrement(int i, final Complex value) {
		i <<= 1;
		set(i, get(i) - value.x());
		set(++i, get(i) - value.y());
	}
	
	/**
	 * Scale.
	 *
	 * @param i the i
	 * @param value the value
	 */
	public final void scale(int i, final double value) {
		i <<= 1;
		set(i, get(i) * value);
		set(++i, get(i) * value);
	}
	
	/**
	 * Divide by.
	 *
	 * @param i the i
	 * @param value the value
	 */
	public final void divideBy(int i, final double value) {
		i <<= 1;
		set(i, get(i) / value);
		set(++i, get(i) / value);
	}
	
	/**
	 * Multiply.
	 *
	 * @param i the i
	 * @param value the value
	 */
	public final void multiply(int i, Complex value) {
		i <<= 1;
		final double x = get(i);
		final double y = get(i+1);
		 
		set(i, x * value.x() - y * value.y());
		set(i+1, x * value.y() + y * value.x());
	}
	
	/**
	 * Divide by.
	 *
	 * @param i the i
	 * @param value the value
	 */
	public final void divideBy(int i, Complex value) {
		final double A = 1.0 / value.norm();
		final double x = get(i);
		final double y = get(i+1);
		
		set(i, A * (x * value.x() - y * value.y()));
		set(i+1, A * (y * value.x() - x * value.y()));
	}
	
	/**
	 * Size.
	 *
	 * @return the int
	 */
	public final int size() { return size; }
	
	/**
	 * As complex.
	 *
	 * @return the complex[]
	 */
	public final Complex[] asComplex() {
		final Complex[] c = new Complex[size()];
		for(int i=c.length; --i >= 0; ) get(i, c[i] = new Complex());	
		return c;
	}
	
	/**
	 * Gets the fft.
	 *
	 * @return the fft
	 */
	public abstract FFT<Type> getFFT();
	
	
	/**
	 * From.
	 *
	 * @param data the data
	 * @return the float
	 */
	public static Float from(float[] data) { return new Float(data); }
	
	/**
	 * From.
	 *
	 * @param data the data
	 * @return the double
	 */
	public static Double from(double[] data) { return new Double(data); }
	
	
	/**
	 * Floats.
	 *
	 * @param size the size
	 * @return the float
	 */
	public static Float floats(int size) { return new Float(size); }
	
	/**
	 * Doubles.
	 *
	 * @param size the size
	 * @return the double
	 */
	public static Double doubles(int size) { return new Double(size); }
	
	

	/**
	 * The Class Float.
	 */
	public static class Float extends FauxComplexArray<float[]> {
		
		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = -5639060583266518343L;
		
		/** The data. */
		private float[] data;
		
		/**
		 * Instantiates a new float.
		 *
		 * @param size the size
		 */
		public Float(int size) { 
			super(size);
			data = new float[size<<1]; 
		}
		
		/**
		 * Instantiates a new float.
		 *
		 * @param data the data
		 */
		public Float(float[] data) {
			super(data.length >> 1);
			if((data.length & 1) != 0) throw new IllegalArgumentException("Cannot create complex array from odd-sized float[].");
			this.data = data;
		}
		
		/* (non-Javadoc)
		 * @see kovacs.data.FauxComplexArray#copy(boolean)
		 */
		@Override
		public FauxComplexArray<float[]> copy(boolean fill) {
			Float copy = (Float) clone();
			copy.data = new float[data.length];
			if(fill) System.arraycopy(data, 0, copy.data, 0, data.length);
			return copy;
		}
		
		/* (non-Javadoc)
		 * @see kovacs.util.data.FauxComplexArray#getData()
		 */
		@Override
		public float[] getData() {
			return data;
		}

		/* (non-Javadoc)
		 * @see kovacs.util.data.FauxComplexArray#get(int)
		 */
		@Override
		protected final double get(int i) {
			return data[i];
		}

		/* (non-Javadoc)
		 * @see kovacs.util.data.FauxComplexArray#set(int, double)
		 */
		@Override
		protected final void set(int i, double value) {
			data[i] = (float) value;
		}

		/* (non-Javadoc)
		 * @see kovacs.util.data.FauxComplexArray#getFFT()
		 */
		@Override
		public FFT<float[]> getFFT() {
			return new FloatFFT();
		}	
	}
	
	
	
	/**
	 * The Class Double.
	 */
	public static class Double extends FauxComplexArray<double[]> {
		
		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 4234523710592006106L;
		
		/** The data. */
		private double[] data;
		
		/**
		 * Instantiates a new double.
		 *
		 * @param size the size
		 */
		public Double(int size) { 
			super(size);
			data = new double[size<<1]; 
		}
		
		/**
		 * Instantiates a new double.
		 *
		 * @param data the data
		 */
		public Double(double[] data) {
			super(data.length >> 1);
			if((data.length & 1) != 0) throw new IllegalArgumentException("Cannot create complex array from odd-sized double[].");
			this.data = data;
		}
		
		/* (non-Javadoc)
		 * @see kovacs.data.FauxComplexArray#copy(boolean)
		 */
		@Override
		public FauxComplexArray<double[]> copy(boolean fill) {
			Double copy = (Double) clone();
			copy.data = new double[data.length];
			if(fill) System.arraycopy(data, 0, copy.data, 0, data.length);
			return copy;
		}
		
		/* (non-Javadoc)
		 * @see kovacs.util.data.FauxComplexArray#getData()
		 */
		@Override
		public double[] getData() {
			return data;
		}

		/* (non-Javadoc)
		 * @see kovacs.util.data.FauxComplexArray#get(int)
		 */
		@Override
		protected final double get(int i) {
			return data[i];
		}

		/* (non-Javadoc)
		 * @see kovacs.util.data.FauxComplexArray#set(int, double)
		 */
		@Override
		protected final void set(int i, double value) {
			data[i] = value;
		}

		/* (non-Javadoc)
		 * @see kovacs.util.data.FauxComplexArray#getFFT()
		 */
		@Override
		public FFT<double[]> getFFT() {
			return new DoubleFFT();
		}
	}
	
}
