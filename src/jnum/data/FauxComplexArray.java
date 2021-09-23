/* *****************************************************************************
 * Copyright (c) 2021 Attila Kovacs <attila[AT]sigmyne.com>.
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

package jnum.data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;

import jnum.CopiableContent;
import jnum.ExtraMath;
import jnum.fft.DoubleFFT;
import jnum.fft.FFT;
import jnum.fft.FloatFFT;
import jnum.math.Complex;
import jnum.math.ComplexConjugate;
import jnum.math.Scalable;
import jnum.parallel.Parallelizable;
import jnum.util.HashCode;

/**
 * <p>
 * A complex array view of a array of pairwise real values, such as numerical recipes-style packing of complex values.
 * I.e., <i>n</i> complex values are represented as a an array of <i>2n</i> real values:
 * </p>
 * 
 * <pre>
 *    [ Re(z[0]), Im(z[0]), Re(z[1]), Im(z[1]), ... , Re(z[n]), Im(z[n]) ]
 * </pre>
 * 
 * <p>
 * This class provides convenient access to the stored complex values, and allows for complex number operation
 * on the complex values represented in a real array form in this way. This is useful, since some Numerical 
 * Recipes algorithms, such as Fast Fourier Transforms (FFTs) of real data pack complex amplitudes in this way.
 * </p>
 * 
 * 
 * @author Attila Kovacs
 *
 * @param <Type>
 */
public abstract class FauxComplexArray<Type> implements Serializable, Cloneable, CopiableContent<FauxComplexArray<Type>>, 
Scalable, ComplexConjugate {

    /** */
	private static final long serialVersionUID = -7774331934035418157L;

	/** private constructor, because we do not want to instantiate the abstract base class */
	private FauxComplexArray() { }


	@SuppressWarnings("unchecked")
    @Override
	public FauxComplexArray<Type> clone() {
		try { return (FauxComplexArray<Type>) super.clone(); }
		catch(CloneNotSupportedException e) { return null; }
	}

	@Override
	public final FauxComplexArray<Type> copy() { return copy(true); }
	
	@Override
	public abstract FauxComplexArray<Type> copy(boolean fill);
		
	/** 
	 * Returns the underlying real-valued array containing the pairwise values that constitute the
	 * complex values in this array.
	 * 
	 * @return     the underlying Java array containing the pairwise real values.
	 * 
	 * @see #set(int, Complex)
	 * @see #set(int, double)
	 */
	public abstract Type getData();
	
	/**
	 * Returns the number of complex values in this array. It is the size of the underlying real
	 * array divided by 2.
	 * 
	 * @return     the number of complex values contained in this array
	 */
	public abstract int size();

	/**
	 * Returns the <i>i</i><sup>th</sup> real value from the underlying real array.
	 * 
	 * @param i    the element index in the underlying real array
	 * @return     the value at that index in the underlying real array.
	 * 
	 * @see #set(int, double)
	 */
	protected abstract double get(int i);
	
	/**
     * Sets a new value for the <i>i</i><sup>th</sup> element in the underlying real array.
     * 
     * @param i         the element index in the underlying real array
     * @param value     the new value at that index in the underlying real array.
     * 
     * @see #get(int)
     */
	protected abstract void set(int i, double value);
	
	/**
	 * Returns the <i>i</i><sup>th</sup> complex value in this array into the supplied complex
	 * number. Because the underlying storage is based on {@link Complex} objects, returning
	 * the result in the usual way would require the creation of a new Java object for every call.
	 * It is far more efficient, to let the caller pass on (a reusable) object in which
	 * to return the result.
	 * 
	 * @param i            the element index in the represented complex array.
	 * @param element      the complex number in which to return the result.
	 * 
	 * @see #set(int, Complex)
	 */
	public final void get(int i, final Complex element) {
	    i <<= 1;
		element.set(get(i), get(i+1));		
	}
	
	/**
     * Sets a new complex value for the <i>i</i><sup>th</sup> element in this array.
     * 
     * @param i            the element index in the represented complex array.
     * @param value        the new complex value for the specified array element.
     * 
     * @see #set(int, Complex)
     */
	public final void set(int i, final Complex value) {
		i <<= 1;
		set(i, value.x());
		set(i+1, value.y());
	}
	
	/**
	 * Conjugates the <i>i</i><sup>th</sup> complex element in this array.
	 * 
	 * @param i        the element index in the represented complex array.
	 * 
	 * @see Complex#conjugate()
	 */
	public final void conjugate(int i) {
		i = i<<1 + 1;
		set(i, -get(i));
	}
	

	@Override
    public final void conjugate() {
	    int n = size() << 1;
	    for(int i=1; i < n; i += 2) set(i, -get(i));
	}
	
	/**
     * Returns the square norm of the <i>i</i><sup>th</sup> complex element in this array.
     * 
     * @param i        the element index in the represented complex array.
     * @return         |<i>z</i><sub>i</sub>|<sup>2</sup>.
     * 
     * @see Complex#squareNorm()
     * @see #abs(int)
     */
	public final double squareNorm(int i) {
		i <<= 1;
		return ExtraMath.sumSquares(get(i), get(i+1));
	}
	
	/**
	 * Returns the absolute value of the <i>i</i><sup>th</sup> complex element in this array.
	 * 
	 * @param i        the element index in the represented complex array.
	 * @return         |<i>z</i><sub>i</sub>|
	 * 
	 * @see #squareNorm(int)
	 */
	public final double abs(int i) {
		i <<= 1;
		return ExtraMath.hypot(get(i), get(i+1));
	}

	/**
     * Returns the angle (or argument) of the <i>i</i><sup>th</sup> complex element in this array.
     * If you need it only to calculate its sine and/or cosine, you should probably use
     * {@link #sinAngle(int)} and/or {@link #cosAngle(int)} instead for better performance.
     * 
     * @param i        the element index in the represented complex array.
     * @return         (rad) arg(<i>z</i><sub>i</sub>).
     * 
     * @see #cosAngle(int)
     * @see #sinAngle(int)
     * @see #tanAngle(int)
     */
	public final double angle(int i) {
		i <<= 1;
		return Math.atan2(get(i+1), get(i));
	}
	
	/**
	 * Returns the cosine of the angle (or argument) of the <i>i</i><sup>th</sup> complex element in this array.
	 * The calculation is purely algebraic, and therefore a lot more computationally efficient than
	 * calling {@link Math#cos(double)} on {@link #angle(int)}.
	 * 
	 * @param i       the element index in the represented complex array.
	 * @return        cos(arg(<i>z</i><sub>i</sub>)) = Re(<i>z</i><sub>i</sub>) / |<i>z</i><sub>i</sub>|
	 * 
	 * @see #sinAngle(int)
	 * @see #tanAngle(int)
	 * @see #angle(int)
	 */
	public final double cosAngle(int i) {
		i <<= 1;
		return ExtraMath.cos(get(i), get(i+1));
	}
	
	/**
     * Returns the sine of the angle (or argument) of the <i>i</i><sup>th</sup> complex element in this array.
     * The calculation is purely algebraic, and therefore a lot more computationally efficient than
     * calling {@link Math#sin(double)} on {@link #angle(int)}.
     * 
     * @param i       the element index in the represented complex array.
     * @return        sin(arg(<i>z</i><sub>i</sub>)) = Im(<i>z</i><sub>i</sub>) / |<i>z</i><sub>i</sub>|
     * 
     * @see #cosAngle(int)
     * @see #tanAngle(int)
     * @see #angle(int)
     */
	public final double sinAngle(int i) {
		i <<= 1;
		return ExtraMath.sin(get(i), get(i+1));
	}
	
	/**
     * Returns the tangent of the angle (or argument) of the <i>i</i><sup>th</sup> complex element in this array.
     * The calculation is purely algebraic, and therefore a lot more computationally efficient than
     * calling {@link Math#tan(double)} on {@link #angle(int)}.
     * 
     * @param i       the element index in the represented complex array.
     * @return        tan(arg(<i>z</i><sub>i</sub>)) = Im(<i>z</i><sub>i</sub>) / Re(<i>z</i><sub>i</sub>)
     * 
     * @see #sinAngle(int)
     * @see #cosAngle(int)
     * @see #angle(int)
     */
	public final double tanAngle(int i) {
		i <<= 1;
		return ExtraMath.tan(get(i), get(i+1));
	}
	
	/**
     * Increments the <i>i</i><sup>th</sup> complex element in this array by the specified value.
     * 
     * @param i         the element index in the represented complex array.
     * @param value     the complex value to add.
     * 
     * @see #decrement(int, Complex)
     * @see Complex#add(Complex)
     */
	public final void increment(int i, final Complex value) {
		i <<= 1;
		set(i, get(i) + value.x());
		set(++i, get(i) + value.y());
	}
	
	/**
     * Decrements the <i>i</i><sup>th</sup> complex element in this array by the specified value.
     * 
     * @param i         the element index in the represented complex array.
     * @param value     the complex value to subtract.
     * 
     * @see #increment(int, Complex)
     * @see Complex#subtract(Complex)
     */
	public final void decrement(int i, final Complex value) {
		i <<= 1;
		set(i, get(i) - value.x());
		set(++i, get(i) - value.y());
	}
	

	/**
     * Scales the <i>i</i><sup>th</sup> complex element in this array by the specified factor.
     * 
     * @param i         the element index in the represented complex array.
     * @param value     the scaling factor to apply to the element.
     * 
     * @see Complex#scale(double)
     * @see #divideBy(int, double)
     */
	public final void scale(int i, final double value) {
		i <<= 1;
		set(i, get(i) * value);
		set(++i, get(i) * value);
	}
	
    @Override
    public final void scale(double value) {
        int n = size() << 1;
        for(int i=1; i < n; i += 2) set(i, -get(i));
    }
	
	/**
     * Divides the <i>i</i><sup>th</sup> complex element in this array by the specified value.
     * 
     * @param i         the element index in the represented complex array.
     * @param value     the divisor to apply to the element.
     * 
     * @see Complex#scale(double)
     * @see #scale(int, double)
     */
	public final void divideBy(int i, final double value) {
	    scale(i, 1.0 / value);
	}
	
	/**
     * Multiplies the <i>i</i><sup>th</sup> complex element in this array by the specified 
     * complex value.
     * 
     * @param i         the element index in the represented complex array.
     * @param value     the complex value to multiply the element with.
     * 
     * @see Complex#multiplyBy(Complex)
     * @see #divideBy(int, Complex)
     */
	public final void multiplyBy(int i, Complex value) {
		i <<= 1;
		final double x = get(i);
		final double y = get(i+1);
		 
		set(i, x * value.x() - y * value.y());
		set(i+1, x * value.y() + y * value.x());
	}
	
	/**
     * Divides the <i>i</i><sup>th</sup> complex element in this array by the specified 
     * complex value.
     * 
     * @param i         the element index in the represented complex array.
     * @param value     the complex divisor.
     * 
     * @see Complex#divideBy(Complex)
     * @see #multiplyBy(int, Complex)
     */
	public final void divideBy(int i, Complex value) {
		final double A = 1.0 / value.squareNorm();
		final double x = get(i);
		final double y = get(i+1);
		
		set(i, A * (x * value.x() - y * value.y()));
		set(i+1, A * (y * value.x() - x * value.y()));
	}
	
	/**
	 * Returns a snapshot of the data from this array as a new complex array.
	 * 
	 * @return     a new complex array with the current complex values in this array.
	 */
	public final Complex[] asComplex() {
		final Complex[] c = new Complex[size()];
		for(int i=c.length; --i >= 0; ) get(i, c[i] = new Complex());	
		return c;
	}
	
	/**
	 * Returns an approriate Fast-Fourier Transformer object for this array, using 
	 * the specified Java executor service.
	 * 
	 * @param executor     the executor service to use by the FFT.
	 * @return             the FFT for this array, using the specified executor service.
	 */
	public abstract FFT<Type> getFFT(ExecutorService executor);
	
	/**
     * Returns an approriate Fast-Fourier Transformer object for this array, using 
     * the specified JNUM parallel processing environment
     * 
     * @param processing    the JNUM parallel processing environment to use.
     * @return              the FFT for this array, in the specified JNUM parallel processinf environment.
     */
	public abstract FFT<Type> getFFT(Parallelizable processing);
	
	/**
	 * Returns a new complex array view of the specified array of pairwise values.
	 * 
	 * @param data     the array containing containing the complex values as pairwise real values. 
	 * @return         the new complex view of the specified underlying data.
	 * 
	 * @see #from(double[])
	 * @see #floats(int)
	 */
	public static Float from(float[] data) { return new Float(data); }
	
	/**
     * Returns a new complex array view of the specified array of pairwise values.
     * 
     * @param data     the array containing containing the complex values as pairwise real values. 
     * @return         the new complex view of the specified underlying data.
     * 
     * @see #from(float[])
     * @see #doubles(int)
     */
	public static Double from(double[] data) { return new Double(data); }
	
	/**
	 * Returns a new complex array view of pairwise real-values, with storage for
	 * the specified number of complex values, and initialized to zeroes.
	 * 
	 * @param size     the number of complex values in the new array.
	 * @return         the new complex view of the underlying zeroed <code>float[]</code> array.
	 * 
	 * @see #doubles(int)
	 * @see #from(float[])
	 */
	public static Float floats(int size) { return new Float(size); }
	
	/**
     * Returns a new complex array view of pairwise real-values, with storage for
     * the specified number of complex values, and initialized to zeroes.
     * 
     * @param size     the number of complex values in the new array.
     * @return         the new complex view of the underlying zeroed <code>double[]</code> array.
     * 
     * @see #floats(int)
     * @see #from(double[])
     */
	public static Double doubles(int size) { return new Double(size); }
	
	

	/**
	 * A complex array view of pairwise <code>float</code> values.
	 * 
	 * @author Attila Kovacs
	 * 
	 * @see FauxComplexArray.Double
	 *
	 */
	public static class Float extends FauxComplexArray<float[]> {

		private static final long serialVersionUID = -5639060583266518343L;

		private float[] data;
		
		/**
		 * Instantiates a new complex array view of a array of pairwise <code>float</code> values, all initialized to zero.
		 * 
		 * @param size    the number of complex values in this view.
		 */
		public Float(int size) {
			this(new float[size<<1]); 
		}
		
		/**
		 * Instantiates a new complex array view of the specified array of pairwise <code>float</code> values.
		 * 
		 * @param data    the underlying data containing complex numbers as pairwise real values.
		 * 
		 * @throws IllegalArgumentException   if the supplied array has an odd number of elements.
		 */
		public Float(float[] data) {
			if((data.length & 1) != 0) throw new IllegalArgumentException("Cannot create complex array from odd-sized float[].");
			this.data = data;
		}

		@Override
        public final int size() {
		    return data.length >>> 1;
		}
		
		@Override
		public int hashCode() {
			return super.hashCode() ^ HashCode.sampleFrom(data);
		}

		@Override
		public boolean equals(Object o) {
			if(o == this) return true;
			if(!(o instanceof FauxComplexArray.Float)) return false;
			
			FauxComplexArray.Float array = (FauxComplexArray.Float) o;
			return Arrays.equals(data, array.data);
		}

		@Override
		public FauxComplexArray<float[]> copy(boolean fill) {
			Float copy = (Float) clone();
			copy.data = new float[data.length];
			if(fill) System.arraycopy(data, 0, copy.data, 0, data.length);
			return copy;
		}

		@Override
		public float[] getData() {
			return data;
		}

		@Override
		protected final double get(int i) {
			return data[i];
		}

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
	
	
	
	/**
     * A complex array view of pairwise <code>double</code> values.
     * 
     * @author Attila Kovacs
     * 
     * @see FauxComplexArray.Float
     *
     */
	public static class Double extends FauxComplexArray<double[]> {

		private static final long serialVersionUID = 4234523710592006106L;

		private double[] data;
		

        /**
         * Instantiates a new complex array view of a array of pairwise <code>double</code> values, all initialized to zero.
         * 
         * @param size    the number of complex values in this view.
         */
		public Double(int size) { 
		    this(new double[size<<1]); 
		}
		
		/**
         * Instantiates a new complex array view of the specified array of pairwise <code>double</code> values.
         * 
         * @param data    the underlying data containing complex numbers as pairwise real values.
         * 
         * @throws IllegalArgumentException   if the supplied array has an odd number of elements.
         */
		public Double(double[] data) {
			if((data.length & 1) != 0) throw new IllegalArgumentException("Cannot create complex array from odd-sized double[].");
			this.data = data;
		}
		
		@Override
        public final int size() {
            return data.length >>> 1;
        }

		@Override
		public int hashCode() {
			return super.hashCode() ^ HashCode.sampleFrom(data);
		}

		@Override
		public boolean equals(Object o) {
			if(o == this) return true;
			if(!(o instanceof FauxComplexArray.Double)) return false;
			
			FauxComplexArray.Double array = (FauxComplexArray.Double) o;
			return Arrays.equals(data, array.data);
		}

		@Override
		public FauxComplexArray<double[]> copy(boolean fill) {
			Double copy = (Double) clone();
			copy.data = new double[data.length];
			if(fill) System.arraycopy(data, 0, copy.data, 0, data.length);
			return copy;
		}

		@Override
		public double[] getData() {
			return data;
		}

		@Override
		protected final double get(int i) {
			return data[i];
		}

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
