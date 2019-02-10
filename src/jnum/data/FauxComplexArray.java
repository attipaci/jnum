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

package jnum.data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.stream.IntStream;

import jnum.CopiableContent;
import jnum.ExtraMath;
import jnum.fft.DoubleFFT;
import jnum.fft.FFT;
import jnum.fft.FloatFFT;
import jnum.math.Complex;
import jnum.parallel.Parallelizable;
import jnum.util.HashCode;


public abstract class FauxComplexArray<Type> implements Serializable, Cloneable, CopiableContent<FauxComplexArray<Type>> {

	private static final long serialVersionUID = -7774331934035418157L;

	private int size;
	

	private FauxComplexArray(int size) { this.size = size; }
	
	
	public FauxComplexArray(Complex[] data) {
		this(data.length);
		IntStream.range(0, data.length).parallel().forEach(i -> set(i, data[i]));
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@SuppressWarnings("unchecked")
    @Override
	public FauxComplexArray<Type> clone() {
		try { return (FauxComplexArray<Type>) super.clone(); }
		catch(CloneNotSupportedException e) { return null; }
	}

	/* (non-Javadoc)
	 * @see jnum.Copiable#copy()
	 */
	@Override
	public final FauxComplexArray<Type> copy() { return copy(true); }
	
	/* (non-Javadoc)
	 * @see jnum.CopiableContent#copy(boolean)
	 */
	@Override
	public abstract FauxComplexArray<Type> copy(boolean fill);
		

	public abstract Type getData();
	

	protected abstract double get(int i);
	

	protected abstract void set(int i, double value);
	

	public final void get(int i, final Complex element) {
		element.set(get(i), get(i+1));		
	}
	

	public final void set(int i, final Complex value) {
		i <<= 1;
		set(i, value.x());
		set(i+1, value.y());
	}
	

	public final void conjugate(int i) {
		i = i<<1 + 1;
		set(i, -get(i));
	}
	

	public final double norm(int i) {
		i <<= 1;
		return ExtraMath.sumSquares(get(i), get(i+1));
	}
	

	public final double length(int i) {
		i <<= 1;
		return ExtraMath.hypot(get(i), get(i+1));
	}
	

	public final double angle(int i) {
		i <<= 1;
		return Math.atan2(get(i+1), get(i));
	}
	

	public final double cosAngle(int i) {
		i <<= 1;
		return ExtraMath.cos(get(i), get(i+1));
	}
	

	public final double sinAngle(int i) {
		i <<= 1;
		return ExtraMath.sin(get(i), get(i+1));
	}
	

	public final double tanAngle(int i) {
		i <<= 1;
		return ExtraMath.tan(get(i), get(i+1));
	}
	

	public final void increment(int i, final Complex value) {
		i <<= 1;
		set(i, get(i) + value.x());
		set(++i, get(i) + value.y());
	}
	

	public final void decrement(int i, final Complex value) {
		i <<= 1;
		set(i, get(i) - value.x());
		set(++i, get(i) - value.y());
	}
	

	public final void scale(int i, final double value) {
		i <<= 1;
		set(i, get(i) * value);
		set(++i, get(i) * value);
	}
	

	public final void divideBy(int i, final double value) {
		i <<= 1;
		set(i, get(i) / value);
		set(++i, get(i) / value);
	}
	

	public final void multiply(int i, Complex value) {
		i <<= 1;
		final double x = get(i);
		final double y = get(i+1);
		 
		set(i, x * value.x() - y * value.y());
		set(i+1, x * value.y() + y * value.x());
	}
	

	public final void divideBy(int i, Complex value) {
		final double A = 1.0 / value.absSquared();
		final double x = get(i);
		final double y = get(i+1);
		
		set(i, A * (x * value.x() - y * value.y()));
		set(i+1, A * (y * value.x() - x * value.y()));
	}
	

	public final int size() { return size; }
	

	public final Complex[] asComplex() {
		final Complex[] c = new Complex[size()];
		for(int i=c.length; --i >= 0; ) get(i, c[i] = new Complex());	
		return c;
	}
	

	public abstract FFT<Type> getFFT(ExecutorService executor);
	
	public abstract FFT<Type> getFFT(Parallelizable processing);
	
	

	public static Float from(float[] data) { return new Float(data); }
	

	public static Double from(double[] data) { return new Double(data); }
	

	public static Float floats(int size) { return new Float(size); }
	

	public static Double doubles(int size) { return new Double(size); }
	
	


	public static class Float extends FauxComplexArray<float[]> {

		private static final long serialVersionUID = -5639060583266518343L;

		private float[] data;
		

		public Float(int size) { 
			super(size);
			data = new float[size<<1]; 
		}
		

		public Float(float[] data) {
			super(data.length >> 1);
			if((data.length & 1) != 0) throw new IllegalArgumentException("Cannot create complex array from odd-sized float[].");
			this.data = data;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			return super.hashCode() ^ HashCode.sampleFrom(data);
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object o) {
			if(o == this) return true;
			if(!(o instanceof FauxComplexArray.Float)) return false;
			
			FauxComplexArray.Float array = (FauxComplexArray.Float) o;
			return Arrays.equals(data, array.data);
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
		 * @see jnum.data.FauxComplexArray#getData()
		 */
		@Override
		public float[] getData() {
			return data;
		}

		/* (non-Javadoc)
		 * @see jnum.data.FauxComplexArray#get(int)
		 */
		@Override
		protected final double get(int i) {
			return data[i];
		}

		/* (non-Javadoc)
		 * @see jnum.data.FauxComplexArray#set(int, double)
		 */
		@Override
		protected final void set(int i, double value) {
			data[i] = (float) value;
		}

		
		@Override
		public FloatFFT getFFT(ExecutorService executor) {
			return new FloatFFT(executor);
		}	
		
		@Override
        public FloatFFT getFFT(Parallelizable processing) {
            return new FloatFFT(processing);
        }   
	}
	
	
	

	public static class Double extends FauxComplexArray<double[]> {

		private static final long serialVersionUID = 4234523710592006106L;

		private double[] data;
		

		public Double(int size) { 
			super(size);
			data = new double[size<<1]; 
		}
		

		public Double(double[] data) {
			super(data.length >> 1);
			if((data.length & 1) != 0) throw new IllegalArgumentException("Cannot create complex array from odd-sized double[].");
			this.data = data;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			return super.hashCode() ^ HashCode.sampleFrom(data);
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object o) {
			if(o == this) return true;
			if(!(o instanceof FauxComplexArray.Double)) return false;
			
			FauxComplexArray.Double array = (FauxComplexArray.Double) o;
			return Arrays.equals(data, array.data);
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
		 * @see jnum.data.FauxComplexArray#getData()
		 */
		@Override
		public double[] getData() {
			return data;
		}

		/* (non-Javadoc)
		 * @see jnum.data.FauxComplexArray#get(int)
		 */
		@Override
		protected final double get(int i) {
			return data[i];
		}

		/* (non-Javadoc)
		 * @see jnum.data.FauxComplexArray#set(int, double)
		 */
		@Override
		protected final void set(int i, double value) {
			data[i] = value;
		}
		
		@Override
        public DoubleFFT getFFT(ExecutorService executor) {
            return new DoubleFFT(executor);
        }   
        
        @Override
        public DoubleFFT getFFT(Parallelizable processing) {
            return new DoubleFFT(processing);
        }   

	}
	
}
