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

import java.util.ArrayList;
import java.util.List;

import jnum.PointOp;
import jnum.Util;
import jnum.data.index.Index;
import jnum.data.index.IndexedValues;
import jnum.fft.DoubleFFT;
import jnum.fft.FloatFFT;
import jnum.fft.MultiFFT;
import jnum.math.MathVector;
import jnum.math.Stretch;
import jnum.math.Complex;
import jnum.math.CoordinateTransform;
import jnum.parallel.ParallelPointOp;
import jnum.parallel.ParallelTask;
import nom.tam.fits.BasicHDU;
import nom.tam.fits.Fits;
import nom.tam.fits.FitsException;
import nom.tam.fits.ImageHDU;

/**
 * A base class for data sampled at regular intervals.
 * 
 * @author Attila Kovacs
 *
 * @param <IndexType>   the generic type of index for locating values in this data.
 * @param <VectorType>  the generic type of vectors used for representing arbitrary positions in the space
 *                      of the data.
 */
public abstract class RegularData<IndexType extends Index<IndexType>, VectorType extends MathVector<Double>> extends Data<IndexType> {
    protected SplineSet<VectorType> reuseIpolData;

    /** The interpolation type to use to interpolate data to inbetween stored indices */
    private Interpolator.Type interpolationType;    

    protected RegularData() {
        reuseIpolData = new SplineSet<>(dimension());
        setInterpolationType(Interpolator.Type.CUBIC_SPLINE);
    }
    
    @Override
    public int hashCode() {
        return super.hashCode() ^ interpolationType.hashCode(); 
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null) return false;
        if(!getClass().isAssignableFrom(o.getClass())) return false;

        @SuppressWarnings("unchecked")
        RegularData<IndexType, VectorType> data = (RegularData<IndexType, VectorType>) o;

        if(getInterpolationType() != data.getInterpolationType()) return false;

        return super.equals(data);
    }

    
    @SuppressWarnings("unchecked")
    @Override
    public RegularData<IndexType, VectorType> clone() {
        RegularData<IndexType, VectorType> clone = (RegularData<IndexType, VectorType>) super.clone();
        clone.reuseIpolData = new SplineSet<>(dimension());  
        return clone;
    }

    /**
     * <p>
     * Returns the element contained at the specified index. The index argument must have at least
     * as many components as the dimension of this data instance. If it has more elements, the ones
     * beyond those necessary will be ignored. For example, if you pass an array of 5 integers for
     * a 2D data object, only the first two index components will be used.
     * </p>
     * <p>
     * It is generally safe to access data via the {@link IndexedValues} interface with the {@link #get(Object)}
     * method. However, at times it is more convenient to pass an array or a comma-separated list of values.
     * It is for these special situations that this method is designed for.
     * </p>
     * 
     * @param idx   the array or list of index values
     * @return      the value contained at the specified index
     * 
     * @see #get(Object)
     */
    public abstract Number get(int ... idx);

    /**
     * <p>
     * Sets a new value for the the element contained at the specified index. The index argument must have at least
     * as many components as the dimension of this data instance. If it has more elements, the ones
     * beyond those necessary will be ignored. For example, if you pass an array of 5 integers for
     * a 2D data object, only the first two index components will be used.
     * </p>
     * <p>
     * It is generally safe to access data via the {@link IndexedValues} interface with the {@link #set(Object, Number)}
     * method. However, at times it is more convenient to pass an array or a comma-separated list of values.
     * It is for these special situations that this method is designed for.
     * </p>
     * 
     * @param value the new point value to set.
     * @param idx   the array or list of index values
     * 
     * @see #set(Object, Number)
     */
    public abstract void set(Number value, int ... idx);
    
    /**
     * <p>
     * Returns the interpolated value at the specified index. The index argument must have at least
     * as many components as the dimension of this data instance. If it has more elements, the ones
     * beyond those necessary will be ignored. For example, if you pass an array of 5 integers for
     * a 2D data object, only the first two index components will be used.
     * </p>
     * <p>
     * It is generally safe to access data via the {@link IndexedValues} interface with the {@link #valueAtIndex(MathVector)}
     * method. However, at times it is more convenient to pass an array or a comma-separated list of values.
     * It is for these special situations that this method is designed for.
     * </p>
     * 
     * @param idx   the array or list of index values
     * @return      the value (possibly interpolated) at the specified index
     * 
     * @see #splineAtIndex(SplineSet, double...)
     * @see #valueAtIndex(MathVector)
     */
    public abstract double valueAtIndex(double ... idx);
    
    /**
     * <p>
     * Returns the spline interpolated value at the specified index. The index argument must have at least
     * as many components as the dimension of this data instance. If it has more elements, the ones
     * beyond those necessary will be ignored. For example, if you pass an array of 5 integers for
     * a 2D data object, only the first two index components will be used.
     * </p>
     * <p>
     * It is generally safe to access data via the {@link IndexedValues} interface with the {@link #valueAtIndex(MathVector)}
     * method. However, at times it is more convenient to pass an array or a comma-separated list of values.
     * It is for these special situations that this method is designed for.
     * </p>
     * 
     * @param splines   a set of spline to use for the interpolation
     * @param idx       the array or list of index values
     * @return          the value (possibly interpolated) at the specified index
     * 
     * @see #valueAtIndex(double...)
     * @see #valueAtIndex(MathVector, SplineSet)
     */
    public abstract double splineAtIndex(SplineSet<VectorType> splines, double ... idx);
    
    @Override
    public String getInfo() {
        return "Data Size: " + getSizeString() + " points.";
    }
    
    @Override
    public final String getSizeString() {
        return getSize().toString("x");
    }
    
    /**
     * Loops over a block of data elements in the specified index range, 
     * performing the specified point operation on each.
     * 
     * @param <ReturnType>  the generic return value type for the operation.
     * @param op            the point operation to perform on each data element
     * @param from          the starting index for the loop.
     * @param to            the ecxlusive ending index for the loop.
     * @return              the return value of the operation (if any).
     * 
     * @see #loop(PointOp)
     * @see #loopValid(PointOp, Index, Index)
     * @see #fork(ParallelPointOp, Index, Index)
     */
    public abstract <ReturnType> ReturnType loop(PointOp<IndexType, ReturnType> op, IndexType from, IndexType to);

    /**
     * Loops over a block of valid data elements in the specified index range, 
     * performing the specified point operation on each.
     * 
     * @param <ReturnType>  the generic return value type for the operation.
     * @param op            the point operation to perform on each data element
     * @param from          the starting index for the loop.
     * @param to            the ecxlusive ending index for the loop.
     * @return              the return value of the operation (if any).
     * 
     * @see #loopValid(PointOp)
     * @see #loop(PointOp, Index, Index)
     * @see #forkValid(ParallelPointOp, Index, Index)
     */
    public abstract <ReturnType> ReturnType loopValid(PointOp<Number, ReturnType> op, IndexType from, IndexType to);

    /**
     * Process a block of elements (points), in the specified range of indices, in this data object in parallel 
     * with the specified point operation.
     * 
     * @param <ReturnType>  the generic return value type for the operation.
     * @param op            the (parallel) point operation to perform on each data element
     * @param from          the starting index for the block of data to process.
     * @param to            the ecxlusive ending index for the block of data to process.
     * @return              the return value of the operation (if any).
     * 
     * @see #smartFork(ParallelPointOp, Index, Index)
     * @see #forkValid(ParallelPointOp, Index, Index)
     * @see #fork(ParallelPointOp)
     * @see #loop(PointOp, Index, Index)
     */
    public abstract <ReturnType> ReturnType fork(final ParallelPointOp<IndexType, ReturnType> op, IndexType from, IndexType to);

    /**
     * Process a block of valid elements (points) only, in the specified range of indices, in this data object in parallel 
     * with the specified point operation.
     * 
     * @param <ReturnType>  the generic return value type for the operation.
     * @param op            the (parallel) point operation to perform on each data element
     * @param from          the starting index for the block of data to process.
     * @param to            the ecxlusive ending index for the block of data to process.
     * @return              the return value of the operation (if any).
     * 
     * @see #smartForkValid(ParallelPointOp, Index, Index)
     * @see #fork(ParallelPointOp, Index, Index)
     * @see #forkValid(ParallelPointOp)
     * @see #loopValid(PointOp, Index, Index)
     */
    public abstract <ReturnType> ReturnType forkValid(final ParallelPointOp<Number, ReturnType> op, IndexType from, IndexType to);

    /**
     * Process a block of elements (points), in the specified range of indices, in this data object efficiently, 
     * using parallel or sequential processing, whichever is deemed fastest for the data size and the specified point operation.
     * 
     * @param <ReturnType>  the generic return value type for the operation.
     * @param op            the (parallel) point operation to perform on each data element
     * @param from          the starting index for the block of data to process.
     * @param to            the ecxlusive ending index for the block of data to process.
     * @return              the return value of the operation (if any).
     * 
     * @see #loop(PointOp, Index, Index)
     * @see #fork(ParallelPointOp, Index, Index)
     * @see #smartForkValid(ParallelPointOp, Index, Index)
     * @see #smartFork(ParallelPointOp)
     */
    public final <ReturnType> ReturnType smartFork(final ParallelPointOp<IndexType, ReturnType> op, IndexType from, IndexType to) {
        if(getParallel() < 2) return loop(op, from, to);
        IndexType span = getIndexInstance();
        span.setDifference(to, from);
        if(span.getVolume() * (2 + op.numberOfOperations()) < 2 * ParallelTask.minExecutorBlockSize) return loop(op, from, to);
        return fork(op, from, to);
    }
    
    /**
     * Process a block of valid elements (points) only, in the specified range of indices, in this data object efficiently, 
     * using parallel or sequential processing, whichever is deemed fastest for the data size and the specified point operation.
     * 
     * @param <ReturnType>  the generic return value type for the operation.
     * @param op            the (parallel) point operation to perform on each data element
     * @param from          the starting index for the block of data to process.
     * @param to            the ecxlusive ending index for the block of data to process.
     * @return              the return value of the operation (if any).
     * 
     * @see #loopValid(PointOp, Index, Index)
     * @see #forkValid(ParallelPointOp, Index, Index)
     * @see #smartFork(ParallelPointOp, Index, Index)
     * @see #smartForkValid(ParallelPointOp)
     */
    public final <ReturnType> ReturnType smartForkValid(final ParallelPointOp<Number, ReturnType> op, IndexType from, IndexType to) {
        if(getParallel() < 2) return loopValid(op, from, to);
        IndexType span = getIndexInstance();
        span.setDifference(to, from);
        if(span.getVolume() * (2 + op.numberOfOperations()) < 2 * ParallelTask.minExecutorBlockSize) return loopValid(op, from, to);
        return forkValid(op, from, to);  
    }
    

    @Override
    public final <ReturnType> ReturnType loop(PointOp<IndexType, ReturnType> op) {
        return loop(op, getIndexInstance(), getSize());
    }

    @Override
    public final <ReturnType> ReturnType loopValid(PointOp<Number, ReturnType> op) {
        return loopValid(op, getIndexInstance(), getSize());
    }

    @Override
    public final <ReturnType> ReturnType fork(final ParallelPointOp<IndexType, ReturnType> op) {
        return fork(op, getIndexInstance(), getSize());
    }
    
    @Override
    public final <ReturnType> ReturnType forkValid(final ParallelPointOp<Number, ReturnType> op) {
        return forkValid(op, getIndexInstance(), getSize());
    }

    @Override
    public final <ReturnType> ReturnType smartFork(final ParallelPointOp<IndexType, ReturnType> op) {
        return smartFork(op, getIndexInstance(), getSize());
    }

    @Override
    public final <ReturnType> ReturnType smartForkValid(final ParallelPointOp<Number, ReturnType> op) {
        return smartForkValid(op, getIndexInstance(), getSize());
    }

    
    
    /**
     * Returns a new instance of the type of vector that can represent position in-between grid points.
     * The returned vector is initialized to zero (origin).
     * 
     * @return  a new zero vector in in the space of this data.
     */
    public abstract VectorType getVectorInstance();

    /**
     * Checks if the specified grid position is within the bounds of this data object.
     * 
     * @param index     a position on the data grid
     * @return          <code>true</code> if the position is within the range of grid indices
     *                  of this data, otherwise <code>false</code>
     */
    public abstract boolean containsIndex(VectorType index);

    /**
     * Returns the value at the nearest grid location for a given position.
     * 
     * @param index     a position on the data grid
     * @return          the nearest value on the data grid
     * 
     * @see #linearAtIndex(MathVector)
     * @see #quadraticAtIndex(MathVector)
     * @see #splineAtIndex(MathVector)
     */
    public abstract Number nearestValueAtIndex(VectorType index);

    /**
     * Returns an interpolated value for the specified grid position, using piecewise
     * linear segments between the neighbouring grid locations along each dimension.
     * 
     * @param index     a position on the data grid
     * @return          the piecewise linear interpolated value at the specified position.
     * 
     * @see #nearestValueAtIndex(MathVector)
     * @see #quadraticAtIndex(MathVector)
     * @see #splineAtIndex(MathVector)
     */
    public abstract double linearAtIndex(VectorType index);

    /**
     * Returns an interpolated value for the specified grid position, using piecewise
     * quadratic segment between the neighbouring grid locations along each dimension.
     * Quadratic interpolation is not a smooth interpolation, in the sense that it will
     * result in abrupt discontinuities in the derivatives. For a completely smooth
     * interpolation you should be using cubic splines instead, which are free from such
     * artifacts.
     * 
     * @param index     a position on the data grid
     * @return          the piecewise quadratic interpolated value at the specified position.
     * 
     * @see #nearestValueAtIndex(MathVector)
     * @see #quadraticAtIndex(MathVector)
     * @see #splineAtIndex(MathVector)
     */
    public abstract double quadraticAtIndex(VectorType index);

    /**
     * <p>
     * Returns an interpolated value for the specified grid position, using cubic
     * splines fitted to nearby grid locations along each dimension.
     * Splines yield completely smooth interpolation, with continuout derivatives,
     * but they are relatively expensive computationally when compared to less perfect
     * alternatives. 
     * </p>
     * 
     * <p>
     * This call uses hidden spline coefficients to perform the interpolation.
     * While it is thread safe, it is not thread efficient. As such it should not
     * be used for multi-threaded interpolation. If you want to interpolate on
     * this dataset in multiple concurrent threads, you will want to use 
     * {@link #splineAtIndex(MathVector, SplineSet)} instead, and separate thread-local 
     * spline sets for each thread.
     * </p>
     * 
     * @param index     a position on the data grid
     * @return          the cubic spline interpolated value at the specified position.
     *
     * @see #splineAtIndex(MathVector, SplineSet)
     * @see #nearestValueAtIndex(MathVector)
     * @see #quadraticAtIndex(MathVector)
     * @see #linearAtIndex(MathVector)
     */
    public final double splineAtIndex(VectorType index) {
        synchronized(reuseIpolData) { return valueAtIndex(index, reuseIpolData); }
    }

    /**
     * <p>
     * Returns an interpolated value for the specified grid position, using cubic
     * splines fitted to nearby grid locations along each dimension.
     * Splines yield completely smooth interpolation, with continuout derivatives,
     * but they are relatively expensive computationally when compared to less perfect
     * alternatives. 
     * </p>
     * 
     * <p>
     * This call uses the caller-supplied spline coefficients to perform the interpolation.
     * As such it is the method of choice when multiple concurrent threads to 
     * perform interpolation in this dataset.
     * </p>
     * 
     * @param index     a position on the data grid
     * @param splines   the set cubic splines to use (containing a spline for each data dimension).
     * @return          the cubic spline interpolated value at the specified position.
     *
     * @see #splineAtIndex(MathVector)
     * @see #nearestValueAtIndex(MathVector)
     * @see #quadraticAtIndex(MathVector)
     * @see #linearAtIndex(MathVector)
     */    
    public abstract double splineAtIndex(VectorType index, SplineSet<VectorType> splines);

    /**
     * Returns the interpolated value using the default interpolation method of this
     * dataset. When using cubic spline interpolation, this call uses hidden spline 
     * coefficients to perform the interpolation. While it is thread safe, it is not 
     * thread efficient. As such it should not be used for multi-threaded interpolation.
     * If you want to interpolate using splines on this dataset in multiple concurrent threads, 
     * you will want to use {@link #valueAtIndex(MathVector, SplineSet)} instead, and separate 
     * thread-local spline sets for each thread.
     * 
     * @param index     a position on the data grid
     * @return          the cubic interpolated value at the specified position, using the
     *                  current default interpolation type if this dataset.
     * 
     * @see #valueAtIndex(MathVector, SplineSet)
     * @see #getInterpolationType()
     * @see #setInterpolationType(Interpolator.Type)
     * @see #nearestValueAtIndex(MathVector)
     * @see #quadraticAtIndex(MathVector)
     * @see #splineAtIndex(MathVector)
     */
    public final double valueAtIndex(VectorType index) {
        synchronized(reuseIpolData) { return valueAtIndex(index, reuseIpolData); }
    }

    /**
     * Returns the interpolated value using the default interpolation method of this
     * dataset. When using cubic spline interpolation, this call uses the caller-supplied 
     * spline coefficients to perform the interpolation. As such it is the method of choice 
     * when multiple concurrent threads to perform interpolation in this dataset.
     *
     * @param index     a position on the data grid
     * @param splines   the set cubic splines to use (containing a spline for each data dimension).
     * @return          the cubic interpolated value at the specified position, using the
     *                  current default interpolation type if this dataset.
     * 
     * @see #valueAtIndex(Index, Index, SplineSet)
     * @see #splineAtIndex(MathVector, SplineSet)
     * @see #valueAtIndex(MathVector)
     * @see #getInterpolationType()
     * @see #setInterpolationType(Interpolator.Type)
     * @see #nearestValueAtIndex(MathVector)
     * @see #quadraticAtIndex(MathVector)
     */
    public abstract double valueAtIndex(VectorType index, SplineSet<VectorType> splines);

    /**
     * Returns the interpolated value using the default interpolation method of this
     * dataset, at a fractional grid positiob. The fractional grid position in this
     * variant of the method defines the position as the fraction of two integers
     * indices <i>n</i><sub>i</sub> and <i>d</i><sub>i</sub>, such that the position
     * of interpolation is <i>x</i><sub>i</sub> = <i>n</i><sub>i</sub> / <i>d</i><sub>i</sub>.
     * 
     * 
     * When using cubic spline interpolation, this call uses the caller-supplied 
     * spline coefficients to perform the interpolation. As such it is the method of choice 
     * when multiple concurrent threads to perform interpolation in this dataset.
     *
     * @param numerator     <i>n</i><sub>i</sub>.
     * @param denominator   <i>d</i><sub>i</sub>
     * @return          the cubic interpolated value at the specified position, 
     *                  <i>x</i><sub>i</sub> = <i>n</i><sub>i</sub> / <i>d</i><sub>i</sub>,
     *                  using the current default interpolation type if this dataset.
     * 
     * @see #splineAtIndex(MathVector, SplineSet)
     * @see #valueAtIndex(MathVector)
     * @see #getInterpolationType()
     * @see #setInterpolationType(Interpolator.Type)
     * @see #nearestValueAtIndex(MathVector)
     * @see #quadraticAtIndex(MathVector)
     */
    protected abstract double valueAtIndex(IndexType numerator, IndexType denominator, SplineSet<VectorType> splines);

    /**
     * Returns the number of elementary operations (e.g. addition, multiplication, assignment) that is 
     * expected per point with the current default interpolation method. This is useful for optimizing
     * multi-threaded performance of operations that use interpolation, such as smoothing and regridding.
     * 
     * @return      the (approximate) number of elementary operations per point interpolatiion.
     * 
     * @see #getInterpolationOps(Interpolator.Type)
     * @see #setInterpolationType(Interpolator.Type)
     */
    protected final int getInterpolationOps() { return getInterpolationOps(getInterpolationType()); }

    /**
     * Returns the number of elementary operations (e.g. addition, multiplication, assignment) that is 
     * expected per point with the specified interpolation method. This is useful for optimizing
     * multi-threaded performance of operations that use interpolation, such as smoothing and regridding.
     * 
     * @return      the (approximate) number of elementary operations per point interpolatiion.
     * 
     * @see #getInterpolationOps()
     * @see #getInterpolationType()
     */
    protected abstract int getInterpolationOps(Interpolator.Type type);

    /**
     * Returns a new empty (zeroed) regularly sampled data object of the same type and size as this object.
     * 
     * @return      a new data object of the same type and size as this one.
     * 
     * @see #newImage(Index, Class)
     */
    public RegularData<IndexType, VectorType> newImage() { return newImage(getSize(), getElementType()); }

    /**
     * Returns a new empty (zeroed) regularly sampled data object of the same class as this one byt with
     * the specified size and element type.
     * 
     * @param size          the size of the new data.
     * @param elementType   the type of elements in the new data, such as <code>Float.class</code>.
     * @return              a new data object of the same class as this one, but with the specified size and element type.
     * 
     * @see #newImage()
     */
    public abstract RegularData<IndexType, VectorType> newImage(IndexType size, Class<? extends Number> elementType);

    /**
     * Smoothes (convolves) this data with the specified image. The convolution is performed meticulously and precisely,
     * calculating the smoothed value at every grid position, which can be quite slow for large images and/or smoothing
     * kernels.
     * 
     * @param beam      the smoothing (convolving) kernel image, with a reference (origin) position.
     * 
     * @see #smooth(RegularData, MathVector)
     * @see #fastSmooth(Referenced, Index)
     * @see #getSmoothed(Referenced, IndexedValues, IndexedValues)
     * @see #getSmoothedValueAtIndex(MathVector, RegularData, MathVector, IndexedValues, SplineSet, WeightedPoint)
     */
    public final synchronized void smooth(Referenced<IndexType, VectorType> beam) {
        smooth(beam.getData(), beam.getReferenceIndex());
    }

    /**
     * Smoothes (convolves) this data with the specified image, provided the reference (origin) coordinate
     * position in that image. The convolution is performed meticulously and precisely,
     * calculating the smoothed value at every grid position, which can be quite slow for large images and/or smoothing
     * kernels.
     * 
     * @param beam      the smoothing (convolving) kernel image, with a reference (origin) coordinate position.
     * @param refIndex  the place on the smoothing kernel that is its nominal reference (origin).
     * 
     * @see #smooth(Referenced)
     * @see #fastSmooth(RegularData, MathVector, Index)
     * @see #getSmoothed(RegularData, MathVector, IndexedValues, IndexedValues)
     * @see #getSmoothedValueAtIndex(MathVector, RegularData, MathVector, IndexedValues, SplineSet, WeightedPoint)
     */
    public synchronized void smooth(RegularData<IndexType, VectorType> beam, VectorType refIndex) {
        paste(getSmoothed(beam, refIndex, null, null), false);
        addHistory("smoothed");
    }

    /**
     * Smoothes (convolves) this data with the specified image using an approximate (and faster!) method. 
     * The convolution is performed on a select subset of the grid positions and is interpolated in-between. Depending
     * on the step size chosen, the user may choose the desired trade-off between accuracy and speed for their
     * application.
     * 
     * @param beam      the smoothing (convolving) kernel image, with a reference (origin) position.
     * @param step      the index distance on the data grid between points for which the convolution is
     *                  performed properly. Points in-between will be interpolated using the default
     *                  method of interpolation for this data object.
     * 
     * @see #fastSmooth(RegularData, MathVector, Index)
     * @see #smooth(Referenced)
     * @see #getFastSmoothed(Referenced, Index, IndexedValues, IndexedValues)
     * @see #setInterpolationType(Interpolator.Type)
     * @see #getSmoothedValueAtIndex(MathVector, RegularData, MathVector, IndexedValues, SplineSet, WeightedPoint)
     */
    public final synchronized void fastSmooth(Referenced<IndexType, VectorType> beam, IndexType step) {
        fastSmooth(beam.getData(), beam.getReferenceIndex(), step);
    }


    /**
     * Smoothes (convolves) this data with the specified image, provided the reference (origin) coordinate
     * position in that image, using an approximate (and faster!) method. 
     * The convolution is performed on a select subset of the grid positions and is interpolated in-between. Depending
     * on the step size chosen, the user may choose the desired trade-off between accuracy and speed for their
     * application.
     * 
     * @param beam      the smoothing (convolving) kernel image, with a reference (origin) coordinate position.
     * @param refIndex  the place on the smoothing kernel that is its nominal reference (origin).
     * @param step      the index distance on the data grid between points for which the convolution is
     *                  performed properly. Points in-between will be interpolated using the default
     *                  method of interpolation for this data object.
     * 
     * @see #fastSmooth(Referenced, Index)
     * @see #smooth(RegularData, MathVector)
     * @see #getFastSmoothed(RegularData, MathVector, Index, IndexedValues, IndexedValues)
     * @see #setInterpolationType(Interpolator.Type)
     * @see #getSmoothedValueAtIndex(MathVector, RegularData, MathVector, IndexedValues, SplineSet, WeightedPoint)
     * 
     */
    public synchronized void fastSmooth(RegularData<IndexType, VectorType> beam, VectorType refIndex, IndexType step) {
        paste(getFastSmoothed(beam, refIndex, step, null, null), false);
        addHistory("smoothed (fast method)");
    }


    /**
     * <p>
     * Calculates a single point smoothed (convolved) value for a specific grid position of this data, and
     * a smoothing (convolving) kernel, its reference position (origin), and optionally externally defined
     * weights. This implementation uses interpolation and is meant for applications where the <code>index</code>
     * and <code>reIndex</code> areguments are fractional indices. When they are integers,
     * {@link #getSmoothedValueAtIndex(Index, RegularData, Index, IndexedValues, WeightedPoint)} offers
     * a much more efficient implementation without requiring costly interpolation, and should be your
     * go to choice for most applications.
     * </p>
     * 
     * @param index         the position on this data grid at which we want to calculate the convolution product.
     * @param beam          the smoothing (convolving) kernel image, with a reference (origin) coordinate position.
     * @param refIndex      the place on the smoothing kernel that is its nominal reference (origin).
     * @param weight        (optional) weights to use for this data, such as 1/&sigma;<sup>2</sup> noise weights,
     *                      or <code>null</code> if to use uniform weights for all data points. If specified,
     *                      the weights should conform to this data in size. For every index position in this
     *                      data, the corresponding data weight is expected at the same index position in the
     *                      supplied weights.
     * @param splines       A set of splines to use when interpolating the smoothing/convolution kernel to
     *                      the gridding of this data (if and when cubic spline interpolation is default for
     *                      this data). When calling this routine in multiple concurrent threads, each
     *                      thread should supply its own set of spline coefficients to avoid race conditions.
     * @param result        the object in which to deposit the result, both the calculated convolution
     *                      product and the correspoinding weight, propagated from the weights supplied
     *                      or assumed.
     * 
     * @see #getSmoothedValueAtIndex(Index, RegularData, Index, IndexedValues, WeightedPoint)
     * @see #setInterpolationType(Interpolator.Type)
     * @see #resampleFrom(RegularData)
     * @see #resampleFrom(RegularData)
     */
    public void getSmoothedValueAtIndex(final VectorType index, final RegularData<IndexType, VectorType> beam, final VectorType refIndex, 
            final IndexedValues<IndexType, ?> weight, final SplineSet<VectorType> splines, final WeightedPoint result) {   

        // Beam fitting: I' = C * sum(wBI) / sum(wB2)
        // I(x) = I -> I' = I -> C = sum(wB2) / sum(wB)
        // I' = sum(wBI) / sum(wB)
        // rms = Math.sqrt(1 / sum(wB))
        
        final VectorType iR = getVectorInstance();
        iR.setDifference(index, refIndex);
        
        PointOp.Simple<IndexType> op = new PointOp.Simple<IndexType>() {
            private VectorType iB = getVectorInstance();

            @Override
            public void process(IndexType i1) { 
                if(!isValid(i1)) return;
                
                final double w = (weight == null ? 1.0 : weight.get(i1).doubleValue());
                if(w == 0.0) return;
                if(Double.isNaN(w)) return;
                
                for(int i=dimension(); --i >= 0; ) iB.setComponent(i, i1.getValue(i) - iR.getComponent(i));

                final double B = beam.valueAtIndex(iB, splines);
                if (!Double.isNaN(B)) {
                    final double wB = w * B;
                    result.add(wB * get(i1).doubleValue());
                    result.addWeight(Math.abs(wB));
                }
            }
        };
       
        final IndexType from = getIndexInstance();
        final IndexType to = getIndexInstance();

        for(int i=dimension(); --i >= 0; ) {
            double imin = iR.getComponent(i);
            // To interpolate on the beam image, we must be inside it...
            from.setValue(i, Math.max(0, (int) Math.ceil(imin)));
            to.setValue(i, Math.min(getSize(i), (int) Math.floor(imin) + beam.getSize(i)));
        }

        result.noData();
        
        loop(op, from, to);

        result.scaleValue(1.0 / result.weight()); 
    }  
    
    /**
     * <p>
     * Calculates a single point smoothed (convolved) value for a specific grid position of this data, and
     * a smoothing (convolving) kernel, its reference position (origin), and optionally externally defined
     * weights. This method is at the base of all smoothing/convolution (and some other) operations.
     * </p>
     * 
     * <p>
     * Given the data <b>X</b> a smoothing kernel (beam) <b>B</b>, data weights <b>w</b>, the smoothed
     * value<i>S</i><sub>i</sub>, at index <i>i</i>, is defined as: 
     * </p>
     * <i>S</i><sub>i</sub> =  (&sum;<sub>k</sub> <i>w</i><sub>k</sub> <i>B</i><sub>i-k</sub> <i>X</i><sub>k</sub>) /
     * (&sum;<sub>k</sub> <i>w</i><sub>k</sub> <i>B</i><sub>i-k</sub><sup>2</sup>)
     * </p>
     * <p>
     * with the corresponding smoothed weight of:
     * </p>
     * <p>
     * <i>w</i>(<i>S</i><sub>i</sub>) = &sum;<sub>k</sub> <i>w</i><sub>k</sub> <i>B</i><sub>i-k</sub><sup>2</sup>.
     * </p>
     * 
     * @param index         the position on this data grid at which we want to calculate the convolution product.
     * @param beam          the smoothing (convolving) kernel image, with a reference (origin) coordinate position.
     * @param refIndex      the place on the smoothing kernel that is its nominal reference (origin).
     * @param weight        (optional) weights to use for this data, such as 1/&sigma;<sup>2</sup> noise weights,
     *                      or <code>null</code> if to use uniform weights for all data points. If specified,
     *                      the weights should conform to this data in size. For every index position in this
     *                      data, the corresponding data weight is expected at the same index position in the
     *                      supplied weights.
     * @param result        the object in which to deposit the result, both the calculated convolution
     *                      product and the correspoinding weight, propagated from the weights supplied
     *                      or assumed.
     *                      
     * @see #getSmoothedValueAtIndex(MathVector, RegularData, MathVector, IndexedValues, SplineSet, WeightedPoint)
     * @see #setInterpolationType(Interpolator.Type)
     * @see #smooth(Referenced)
     * @see #smooth(RegularData, MathVector)
     * @see #getSmoothed(Referenced, IndexedValues, IndexedValues)
     * @see #getSmoothed(RegularData, MathVector, IndexedValues, IndexedValues)
     */
    public void getSmoothedValueAtIndex(final IndexType index, final RegularData<IndexType, VectorType> beam, final IndexType refIndex, 
            final IndexedValues<IndexType, ?> weight, final WeightedPoint result) {   

        // Beam fitting: I' = C * sum(wBI) / sum(wB2)
        // I(x) = I -> I' = I -> C = sum(wB2) / sum(wB)
        // I' = sum(wBI) / sum(wB)
        // rms = Math.sqrt(1 / sum(wB))
        
        final IndexType iR = getIndexInstance();
        iR.setDifference(index, refIndex);
        
        PointOp.Simple<IndexType> op = new PointOp.Simple<IndexType>() {
            private IndexType iB = getIndexInstance();

            @Override
            public void process(IndexType i1) { 
                if(!isValid(i1)) return;
                
                final double w = (weight == null ? 1.0 : weight.get(i1).doubleValue());
                if(w == 0.0) return;
                if(Double.isNaN(w)) return;      
                
                iB.setDifference(i1, iR);

                final double wB = w * beam.get(iB).doubleValue();
                result.add(wB * get(i1).doubleValue());
                result.addWeight(Math.abs(wB));
            }
        };
       
        final IndexType from = getIndexInstance();
        final IndexType to = getIndexInstance();

        for(int i=dimension(); --i >= 0; ) {
            int imin = iR.getValue(i);
            // To interpolate on the beam image, we must be inside it...
            from.setValue(i, Math.max(0, imin));
            to.setValue(i, Math.min(getSize(i), imin + beam.getSize(i)));
        }

        result.noData();
        
        loop(op, from, to);

        result.scaleValue(1.0 / result.weight()); 
    }  
    
    
    /**
     * Returns the (approximate) number of elementary operations (e.g. addition, multiplication, assignment) that are
     * typically necessary to calculate a smoothed/convolved value at a specific positon on this data's grid, 
     * given the 'volume' of the smoothing kernel. This is useful for eatimating the cost of the calculation and
     * to optimize parallelization accordingly (when that is desired).
     * 
     * @param beamPoints            The smoothing/convolving kernel's volume as a number of (non-zero) data points
     *                              in that kernel.
     * @param interpolationType     the type of interpolation for which to estimate the cost of calculation.
     * @return  the (approximate) number of elementary operations for smoothing/convolving this data at a single
     *          grid position.
     *          
     * @see #setInterpolationType(Interpolator.Type)
     */
    public abstract int getPointSmoothOps(int beamPoints, Interpolator.Type interpolationType);

    /**
     * Returns a smoothes (convolved) version of this data with the specified kernel, provided the reference (origin) coordinate
     * position in that image. The convolution is performed meticulously and precisely,
     * calculating the smoothed value at every grid position, which can be quite slow for large images and/or smoothing
     * kernels.
     * 
     * @param beam      the smoothing (convolving) kernel image, with a reference (origin) coordinate position.
     * @param weight    the weight image to associate with the data, or <code>null</code> to assume uniform weights.
     * @param smoothedWeights   The image in which to populate the smoothed weights, or <code>null</code> if not required.
     * 
     * @return          a new regularly gridded data derived from this one, but containing the smoothed data values.
     * 
     * @see #smooth(Referenced)
     * @see #fastSmooth(RegularData, MathVector, Index)
     * @see #getSmoothed(RegularData, MathVector, IndexedValues, IndexedValues)
     * @see #getSmoothedValueAtIndex(MathVector, RegularData, MathVector, IndexedValues, SplineSet, WeightedPoint)
     */
    public final RegularData<IndexType, VectorType> getSmoothed(final Referenced<IndexType, VectorType> beam, 
            final IndexedValues<IndexType, ?> weight, final IndexedValues<IndexType, ?> smoothedWeights) {
        return getSmoothed(beam.getData(), beam.getReferenceIndex(), weight, smoothedWeights);
    }

    /**
     * Returns a smoothes (convolved) version of this data with the specified kernel, provided the reference (origin) coordinate
     * position in that image. The convolution is performed meticulously and precisely,
     * calculating the smoothed value at every grid position, which can be quite slow for large images and/or smoothing
     * kernels.
     * 
     * @param beam      the smoothing (convolving) kernel image.
     * @param refIndex  the fractional index location of the beam center, in the beam image.
     * @param weight    the weight image to associate with the data, or <code>null</code> to assume uniform weights.
     * @param smoothedWeights   The image in which to populate the smoothed weights, or <code>null</code> if not required.
     * 
     * @return          a new regularly gridded data derived from this one, but containing the smoothed data values.
     * 
     * @see #getSmoothed(RegularData, Index, IndexedValues, IndexedValues)
     * @see #smooth(Referenced)
     * @see #fastSmooth(RegularData, MathVector, Index)
     * @see #getSmoothed(Referenced, IndexedValues, IndexedValues)
     * @see #getSmoothedValueAtIndex(MathVector, RegularData, MathVector, IndexedValues, SplineSet, WeightedPoint)
     */    
    public final RegularData<IndexType, VectorType> getSmoothed(RegularData<IndexType, VectorType> beam, final VectorType refIndex, 
            final IndexedValues<IndexType, ?> weight, final IndexedValues<IndexType, ?> smoothedWeights) {
        
        IndexType iRefIndex = getIndexInstance();
        VectorType shift = getVectorInstance();
        
        // Shift the beam as necessary s.t. its reference is on an index position.
        for(int i=dimension(); --i >= 0; ) {
            iRefIndex.setValue(i, (int) Math.round(refIndex.getComponent(i)));
            shift.setComponent(i, iRefIndex.getValue(i) - refIndex.getComponent(i));
        }
        if (!shift.isNull()) beam = beam.getShifted(shift);
        
        return getSmoothed(beam, iRefIndex, weight, smoothedWeights);        
    }

    boolean useFFT = true;
    
    /**
     * Returns a smoothes (convolved) version of this data with the specified kernel, provided the reference (origin) coordinate
     * position in that image. The convolution is performed meticulously and precisely,
     * calculating the smoothed value at every grid position, which can be quite slow for large images and/or smoothing
     * kernels.
     * 
     * @param beam      the smoothing (convolving) kernel image.
     * @param refIndex  the fractional index location of the beam center, in the beam image.
     * @param weight    the weight image to associate with the data, or <code>null</code> to assume uniform weights.
     * @param smoothedWeights   The image in which to populate the smoothed weights, or <code>null</code> if not required.
     * 
     * @return          a new regularly gridded data derived from this one, but containing the smoothed data values.
     * 
     * @see #getSmoothed(RegularData, MathVector, IndexedValues, IndexedValues)
     * @see #smooth(Referenced)
     * @see #fastSmooth(RegularData, MathVector, Index)
     * @see #getSmoothed(Referenced, IndexedValues, IndexedValues)
     * @see #getSmoothedValueAtIndex(MathVector, RegularData, MathVector, IndexedValues, SplineSet, WeightedPoint)
     */
    public final RegularData<IndexType, VectorType> getSmoothed(RegularData<IndexType, VectorType> beam, final IndexType refIndex, 
            final IndexedValues<IndexType, ?> weight, final IndexedValues<IndexType, ?> smoothedWeights) {
      
        final RegularData<IndexType, VectorType> convolved = newImage();

        ParallelPointOp.Simple<IndexType> op = new ParallelPointOp.Simple<IndexType>() {
            private WeightedPoint result;
            @Override
            protected void init() {
                super.init();
                result = new WeightedPoint();
            }
            @Override
            public void process(IndexType index) { 
                if(!isValid(index)) return;
                getSmoothedValueAtIndex(index, beam, refIndex, weight, result);  
                convolved.set(index, result.value());
                if(smoothedWeights != null) smoothedWeights.set(index, result.weight());
            }
            @Override
            public int numberOfOperations() {
                return 5 + getPointSmoothOps(beam.capacity(), Interpolator.Type.NEAREST);
            }
        };

        smartFork(op);
        
        convolved.addHistory("smoothed copy");

        return convolved;
    }
    
    public final RegularData<IndexType, VectorType> getFFTSmoothed(RegularData<IndexType, VectorType> beam, final IndexType refIndex, 
            final IndexedValues<IndexType, ?> weight, final IndexedValues<IndexType, ?> smoothedWeights) {
    
        IndexType size = getIndexInstance();
        size.setSum(getSize(), beam.getSize());
        
        RegularData<IndexType, VectorType> B = beam.getFFT(size, true, refIndex, null);
        RegularData<IndexType, VectorType> S = getFFT(size, true, null, weight);
        RegularData<IndexType, VectorType> W = smoothedWeights == null ? null : getFFT(size, true, null, null);
        
        ParallelPointOp.Simple<IndexType> complexMultiply = new ParallelPointOp.Simple<IndexType>() {
            Complex b, s, w;
            
            @Override
            protected void init() {
                super.init();
                b = new Complex();
                s = new Complex();
                w = new Complex();
            }

            @Override
            public void process(IndexType index) {
                // If starting index is not pointing to the real part, then skip.
                // (we process pairwise below)
                if((index.getValue(dimension() + 1) & 1) != 0) return;
                
                // Get the real parts
                b.setRealPart(B.get(index).doubleValue());
                s.setRealPart(S.get(index).doubleValue());
                if(W != null) w.setRealPart(W.get(index).doubleValue());
                
                // Change index to the imaginaty part
                index.increment(dimension() - 1);
                
                // Get the imaginary parts
                b.setImaginaryPart(B.get(index).doubleValue());
                s.setImaginaryPart(S.get(index).doubleValue());
                if(W != null) w.setImaginaryPart(W.get(index).doubleValue());
             
                // Perform the multiplication, and set the imaginary part(s)
                s.multiplyBy(b);
                S.set(index, s.im());
                
                if(W != null) {
                    w.multiplyBy(b);
                    W.set(index, w.im());
                }
                // Change index back to the real part
                index.decrement(dimension() - 1);
                
                // Set the real part(s)
                S.set(index, s.re());
                if(W != null) W.set(index, w.re());
            }
            
        };
        
        smartFork(complexMultiply);
        
        RegularData<IndexType, VectorType> smoothed = newImage();
        S.getFFT(S.getSize(), false, null, weight).copyTo(smoothed);
        
        if(W != null) W.getFFT(S.getSize(), false, null, null).copyTo(smoothedWeights);
        
        return smoothed;
    }
    
    
    /**
     * Returns a smoothes (convolved) version of this data with the specified kernel, provided the reference (origin) coordinate
     * position in that image. The convolution is performed approximately for performance,
     * calculating the smoothed value at every few grid positions, and interpolation in-between.
     * This can be significantly faster for large images and/or smoothing kernels.
     * 
     * @param beam      the smoothing (convolving) kernel image, with a reference (origin) coordinate position.
     * @param step      the step size in each index direction between points for which the smoothed value is calculated
     *                  precisely. For indices in-between these steps cubic spline interpolation is used to obtain a fast
     *                  approximate smoothed value. The coarser the step, the faster the method, and the less precise
     *                  the result.
     * @param weight    the weight image to associate with the data, or <code>null</code> to assume uniform weights.
     * @param smoothedWeights   The image in which to populate the smoothed weights, or <code>null</code> if not required.
     * 
     * @return          a new regularly gridded data derived from this one, but containing the smoothed data values.
     * 
     * @see #smooth(Referenced)
     * @see #fastSmooth(RegularData, MathVector, Index)
     * @see #getSmoothed(RegularData, MathVector, IndexedValues, IndexedValues)
     * @see #getFastSmoothed(RegularData, MathVector, Index, IndexedValues, IndexedValues)
     * @see #getSmoothedValueAtIndex(MathVector, RegularData, MathVector, IndexedValues, SplineSet, WeightedPoint)
     */
    public final RegularData<IndexType, VectorType> getFastSmoothed(final Referenced<IndexType, VectorType> beam,
            final IndexType step, final IndexedValues<IndexType, ?> weight, final IndexedValues<IndexType, ?> smoothedWeights) {
        return getFastSmoothed(beam.getData(), beam.getReferenceIndex(), step, weight, smoothedWeights);
    }
    
    /**
     * Returns a smoothes (convolved) version of this data with the specified kernel, provided the reference (origin) coordinate
     * position in that image. The convolution is performed approximately for performance,
     * calculating a coarsely smoothed value at every few grid positions, and interpolating in-between.
     * This can be significantly faster for large images and/or smoothing kernels.
     * 
     * @param beam      the smoothing (convolving) kernel image.
     * @param refIndex  the fractional index location of the beam center, in the beam image.
     * @param step      the step size in each index direction between points for which the smoothed value is calculated
     *                  precisely. For indices in-between these steps cubic spline interpolation is used to obtain a fast
     *                  approximate smoothed value. The coarser the step, the faster the method, and the less precise
     *                  the result.
     * @param weight    the weight image to associate with the data, or <code>null</code> to assume uniform weights.
     * @param smoothedWeights   The image in which to populate the smoothed weights, or <code>null</code> if not required.
     * 
     * @return          a new regularly gridded data derived from this one, but containing the smoothed data values.
     * 
     * @see #smooth(Referenced)
     * @see #fastSmooth(RegularData, MathVector, Index)
     * @see #getSmoothed(RegularData, MathVector, IndexedValues, IndexedValues)
     * @see #getFastSmoothed(Referenced, Index, IndexedValues, IndexedValues)
     * @see #getSmoothedValueAtIndex(MathVector, RegularData, MathVector, IndexedValues, SplineSet, WeightedPoint)
     */
    public RegularData<IndexType, VectorType> getFastSmoothed(final RegularData<IndexType, VectorType> beam, final VectorType refIndex,
            final IndexType step, final IndexedValues<IndexType, ?> weight, final IndexedValues<IndexType, ?> smoothedWeights) {
        
        int vol = step.getVolume();
        if(vol == 1) return getSmoothed(beam, refIndex, weight, smoothedWeights);
        if(vol == 0) throw new IllegalArgumentException("coarse smoothing step with 0 volume.");
        
        
        final WeightedSet<RegularData<IndexType, VectorType>> coarse = createDownAveraged(weight, step);
        RegularData<IndexType, VectorType> coarseBeam = createDownSampledFrom(beam, step);
        final RegularData<IndexType, VectorType> coarseSmoothedWeight = smoothedWeights == null ? null : coarse.weight().newImage();
        
        // Re-scale the beam reference point to coarse indices.
        VectorType shift = getVectorInstance();
        IndexType iRefIndex = getIndexInstance();

        // Shift beam as needed s.t. reference is at an index position.
        for(int i=dimension(); --i >= 0; ) {
            double ci = refIndex.getComponent(i) / step.getValue(i);
            iRefIndex.setValue(i, (int) Math.round(ci));
            shift.setComponent(i, iRefIndex.getValue(i) - ci);
        }       
        if(!shift.isNull()) coarseBeam = coarseBeam.getShifted(shift);

        // We calculate coarsely smoothed data...
        final RegularData<IndexType, VectorType> coarseSmoothed = coarse.value().getSmoothed(coarseBeam, iRefIndex, coarse.weight(), coarseSmoothedWeight);
        final RegularData<IndexType, VectorType> convolved = newImage();
        
        // Then we use the coarse smoothed values to interpolate back to the original grid...
        Interpolation interpolation = new Interpolation() {
            @SuppressWarnings("null")
            @Override
            public void process(IndexType index) {
                if(!isValid(index)) {
                    convolved.discard(index);
                    return;
                }

                final double value = coarseSmoothed.valueAtIndex(index, step, getSplines());
                
                if(!Double.isNaN(value)) {      
                    convolved.set(index, value);
                    if(smoothedWeights != null) {
                        smoothedWeights.set(index, coarseSmoothedWeight.valueAtIndex(index, step, getSplines()));
                    }
                }
                else {
                    convolved.discard(index);
                    if(smoothedWeights != null) smoothedWeights.set(index, 0);
                } 
            }
            @Override
            public int numberOfOperations() {
                return 9 + (smoothedWeights == null ? 1 : 2) * getInterpolationOps();
            }
        };

        convolved.smartFork(interpolation);     
        convolved.addHistory("smoothed copy (fast method)");
        
        return convolved; 
    }


    public synchronized void resampleFrom(final RegularData<IndexType, VectorType> image, final CoordinateTransform<VectorType> toSourceIndex, 
            final Referenced<IndexType, VectorType> beam, final IndexedValues<IndexType, ?> weight) {
        
        if(beam != null) resampleFrom(image, toSourceIndex, beam.getData(), beam.getReferenceIndex(), weight);
        else resampleFrom(image, toSourceIndex, null, null, weight);
    }


    private synchronized void resampleFrom(final RegularData<IndexType, VectorType> image, final CoordinateTransform<VectorType> toSourceIndex, 
            final RegularData<IndexType, VectorType> beam, final VectorType refIndex, final IndexedValues<IndexType, ?> weight) {
    
        RegularData<IndexType, VectorType> smoothed = beam == null ? image : image.getSmoothed(beam, refIndex, weight, null);
        
        Interpolation interpolation = new Interpolation() {
            private VectorType v;

            @Override
            protected void init() { 
                super.init();
                v = getVectorInstance();
            }

            @Override
            public void process(IndexType index) {
                index.toVector(v);
                toSourceIndex.transform(v);

                double value = smoothed.valueAtIndex(v, getSplines());
                if(Double.isNaN(value)) discard(index);
                else set(index, value);
            }
            @Override
            public int numberOfOperations() {
                return 5 + getInterpolationOps();
            }
        };

        smartFork(interpolation);
        
        clearHistory();
        addHistory("resampled " + getSizeString() + " from " + image.getSizeString());
    }
    
    public RegularData<IndexType, VectorType> getFFT(final boolean isForward) {
        return getFFT(getSize(), isForward, null, null);
    }
        
    public RegularData<IndexType, VectorType> getFFT(final boolean isForward, final IndexType refIndex) {
        return getFFT(getSize(), isForward, refIndex, null);
    }

    public RegularData<IndexType, VectorType> getFFT(final IndexType size, final boolean isForward, final IndexType refIndex, final IndexedValues<IndexType, ?> weight) {
        size.toPaddedFFTSize();
        
        IndexType unrolledSize = size.copy();
        unrolledSize.setValue(dimension() - 1, unrolledSize.getValue(dimension() - 1) + 2);
        
        Class<? extends Number> type = Double.class.isAssignableFrom(getElementType()) || Long.class.isAssignableFrom(getElementType()) 
                ? Double.class : Float.class;
        
        RegularData<IndexType, VectorType> spectrum = newImage(unrolledSize, type);

        ParallelPointOp.Simple<IndexType> op = new ParallelPointOp.Simple<IndexType>() {
            IndexType packed;
            
            @Override
            protected void init() {
                if(refIndex != null) packed = getIndexInstance();
            }

            @Override
            public void process(IndexType index) {
                if(refIndex != null) {
                    packed.setDifference(index, refIndex);
                    packed.wrap(size);
                }
                else packed = index;
                
                if (isForward) {
                    if(!isValid(index)) spectrum.discard(packed);
                    else if(weight == null) spectrum.set(packed, get(index));
                    else spectrum.set(packed, get(index).doubleValue() * weight.get(index).doubleValue());
                }
                else {
                    if(!isValid(packed)) spectrum.discard(index);
                    else if(weight == null) spectrum.set(index, get(packed));
                    else spectrum.set(index, get(packed).doubleValue() / weight.get(packed).doubleValue());   
                }
            }
            
            @Override
            public int numberOfOperations() {
                return (3 + (refIndex == null ? 0 : 4) + (weight == null ? 0 : 2)) * dimension();
            }
        };
        
        size.limit(getSize());
        smartFork(op, getIndexInstance(), size);    

        Object core = spectrum.getCore();

        if (core instanceof float[]) new FloatFFT.NyquistUnrolled(getExecutor()).realTransform((float[]) core, isForward);
        else if (core instanceof double[]) new DoubleFFT.NyquistUnrolled(getExecutor()).realTransform((double[]) core, isForward);
        else new MultiFFT(getExecutor()).realTransform((Object[]) core, isForward);
        
        return spectrum;
    }

    public <V extends MathVector<Double>> void addPatchAt(final VectorType index, final RegularData<IndexType, V> patch, final double scaling) {
        addPatchAt(index, patch, scaling, false);
    }


    public <V extends MathVector<Double>> void addPatchAt(final VectorType index, final RegularData<IndexType, V> patch, final double scaling, boolean parallel) {
        IndexType min = getIndexInstance();
        IndexType max = getIndexInstance();   

        for(int i=dimension(); --i >= 0; ) {
            min.setValue(i, Math.max(0, (int) Math.floor(index.getComponent(i))));
            max.setValue(i, Math.min(getSize(i), (int) Math.ceil(index.getComponent(i)) + patch.getSize(i)));
        }

        ParallelPointOp.Simple<IndexType> op = new ParallelPointOp.Simple<IndexType>() {
            private V d;
            private SplineSet<V> iPolData;

            @Override
            public void init() {
                super.init();
                d = patch.getVectorInstance();
                iPolData = new SplineSet<>(dimension());
            }

            @Override
            public void process(IndexType i0) {
                for(int i=dimension(); --i >= 0; ) d.setComponent(i, i0.getValue(i) - index.getComponent(i));
                double patchValue = patch.valueAtIndex(d, iPolData);
                if(!Double.isNaN(patchValue)) add(i0, scaling * patchValue);
            }
        };

        smartFork(op, min, max);
    }


    public RegularData<IndexType, VectorType> clean(final RegularData<IndexType, VectorType> beam, final VectorType beamCenterIndex, final double gain, final double threshold) { 
        RegularData<IndexType, VectorType> clean = newImage();

        final int maxComponents = (int) Math.ceil(countPoints() / gain);         
        int components = 0;

        IndexType peakIndex = indexOfMaxDev();
        double peakValue = get(peakIndex).doubleValue();

        VectorType offset = getVectorInstance();

        while(Math.abs(peakValue) > threshold && components < maxComponents) { 
            final double componentValue = gain * peakValue;

            for(int i=dimension(); --i >= 0; ) offset.setComponent(i, peakIndex.getValue(i) - beamCenterIndex.getComponent(i));

            addPatchAt(offset, beam, -componentValue);
            clean.add(peakIndex, componentValue);

            components++;

            peakIndex = indexOfMaxDev();
            peakValue = get(peakIndex).doubleValue();
        }

        // Scale cleaned components by the beam area...
        clean.scale(beam.getAbsSum());

        addHistory("cleaned away " + components + " components.");
        clean.addHistory("created with " + components + " clean components.");

        return clean;
    }

    /**
     * Discards outliers above the specified deviation level (from zero), by calling {@link #discard(Index)} on
     * any point that is deemed to be an outlier at the specified level. The default implementation is to 
     * call {@link #despikeNeighbors(double, IndexedValues)} with <code>null</code> for the <code>noiseWeight</code>
     * argument.
     * 
     * @param threshold     the despike threshold level. All data elements with absolute values larger than this
     *                      threshold will be discarded.
     *                      
     * @see #despikeAbsolute(double, IndexedValues)
     */
    @Override
    public void despike(double threshold) {
        despikeNeighbors(threshold, null);
    }

    /**
     * <p>
     * Discards outliers above the specified deviation level (from zero), by calling {@link #discard(Index)} on
     * any point that is deemed to be an outlier at the specified level. The second optional argument can
     * be used to specify noise weights data (<i>w</i> = 1/%sigma;<sup>2</sup>), in which case the threshold is
     * interpreted as a signal-to-noise threshold, s.t. data <i>x</i><sub>i</sub> for which |<i>x</i><sub>i</sub>| 
     * &gt; <code>significance</code> * %sigma;<sub>i</sub> is removed. Leaving the second argument <code>null</code>
     * is equivalent to all data weight being 1. 
     * </p>
     * <p>
     * Unlike its superclass implementation, int this method <i>x</i>
     * is NOT the stored data value, but rather the deviation of that value from (the average of) its immediate 
     * neighbours. This is a more robust despiking method, since it allows for large amplitude smooth fluctuations,
     * and rejecting only single-point deviations.
     * </p>
     * 
     * @param significance   the despike threshold level. All data elements with absolute values or standardized
     *                       deviations (if noise weights are given) larger than this threshold will be discarded.
     * @param noiseWeight    (optional) Noise weights (<i>w</i> = 1/%sigma;<sup>2</sup>) to use with the data,
     *                       or <code>null</code> to use uniform weights of 1.
     *                      
     * @see #despikeAbsolute(double, IndexedValues)
     */
    public synchronized void despikeNeighbors(final double significance, final IndexedValues<IndexType, ?> noiseWeight) {
        final Referenced<IndexType, VectorType> neighbours = getNeighbors();

        final IndexType iRef = getIndexInstance();
        iRef.fill(1);
        
        Interpolation op = new Interpolation() {  
            private WeightedPoint point, surrounding;
            
            @Override
            protected void init() {
                point = new WeightedPoint();
                surrounding = new WeightedPoint();
            }
            @Override
            public void process(IndexType index) {
                if(!isValid(index)) return;

                point.setValue(get(index).doubleValue());
                point.setWeight(noiseWeight == null ? 1.0 : noiseWeight.get(index).doubleValue());
                
                getSmoothedValueAtIndex(index, neighbours.getData(), iRef, noiseWeight, surrounding);

                point.subtract(surrounding);

                if(DataPoint.significanceOf(point) > significance) discard(index);             
            }
        };

        smartFork(op);

        addHistory("despiked (neighbors) at " + Util.S3.format(significance));
    }

    /**
     * Returns a list of peaks from the data, above some specified threshold, and an exclusion radius (or radii) that
     * sets the minimum separation between peaks returned. Each peak returned will have a data index that identifies
     * the location of the peak, and the data value at that index. The peaks will also be masked out (discarded) from the 
     * data object with the specified flagging radius/radii. If you wish to keep your data intact, you should call this
     * method on an independent copy of the data, e.g. as <code>data.copy().findPeaks(threshold, r);</code>.
     * 
     * @param threshold     The threshold level above which to extract peaks from this data.
     * @param r             the exclusion radius, or radii (if different along the various data dimensions).
     * @return              a list of peak locations and corresponding peak values, each separated by the
     *                      specified exclusion radius/radii, or more.
     *                      
     * @see #discardRadius(Index, double...)
     * @see #copy()
     */
    public List<Point> findPeaks(double threshold, double... r) {
        ArrayList<Point> peaks = new ArrayList<>();

        if(isEmpty()) return peaks;

        while(true) {
            IndexType idx = indexOfMax();
            double S = get(idx).doubleValue();
            if(S < threshold) break;

            peaks.add(new Point(idx, S));
            discardRadius(idx, r);
        }

        return peaks;
    }

    /**
     * Discards data in a circle/ellipsoid around some data index, by calling {@link #discard(Index)} on points
     * within the specified radius/radii of the specified center index.
     * 
     * 
     * @param centerIndex   the data index around which to discard data
     * @param rPix          (ct) the radius or radii (if different along the various data dimensions) of indices
     *                      that specifies the exclusion circle or ellipsoid.
     *                      
     * @see #discard(Index)
     */
    @SuppressWarnings("cast")
    public void discardRadius(final IndexType centerIndex, double... rPix) {
        IndexType from = (IndexType) centerIndex.copy();
        IndexType to = (IndexType) centerIndex.copy();

        for(int i=centerIndex.dimension(); --i >= 0; ) {
            int d =  (int) Math.ceil(rPix[rPix.length > i ? i : rPix.length - 1]);
            from.setValue(i, Math.max(0, from.getValue(i) - d));
            to.setValue(i, Math.min(getSize(i), to.getValue(i) + d + 1));
        }

        PointOp<IndexType, Void> flagger = new PointOp.Simple<IndexType>() {
            @Override
            public void process(IndexType idx) {
                if(!isValid(idx)) return;

                double d2 = 0.0;

                for(int i=centerIndex.dimension(); --i >= 0; ) {
                    double d = (idx.getValue(i) - centerIndex.getValue(i)) / rPix[rPix.length > i ? i : rPix.length - 1];
                    d2 += d * d;
                    if(d2 > 1.0) return;
                }

                discard(idx);
            } 
        };

        loop(flagger, from, to);
    }

    
    /**
     * Discards isolated data points, which are not connected to a contiguous region. This method will keep
     * right-angle corners, but not will descard angled vertices, effectively chiselling them away for a more
     * rounded corner...
     * 
     * @see #discardIsolated(int)
     */
    public final void discardIsolated() {
        int max = 0;
        for(int i=dimension(); --i >= 0; ) max = 3 * max + 2;
        discardIsolated(max / (1 << dimension()));
    }

    /**
     * Discards isolated data points, which are not connected to a contiguous region. This method offers
     * fine control over what exactly is considered connected.
     * 
     * @param minNeighbors  the minimum number of valid adjacent neighbors a point must have in order to be not
     *                      considered isolated. Neighbors are cells that share at least a common vertex with
     *                      the pixel (voxel) considered (2 in 1D, 8 in 2D, 26 in 3D...). 
     *                      
     * @see #discardIsolated()
     */
    public final void discardIsolated(final int minNeighbors) { 
        if(minNeighbors < 1) return;   // Nothing to do...
        validate(getNeighborValidator(minNeighbors));
    }


    public final Validating<IndexType> getNeighborValidator(final int minNeighbors) {
        return new Validating<IndexType>() {
            private PointOp.Sum<IndexType> op = new PointOp.Sum<IndexType>() {
                @Override
                protected double getValue(IndexType index) {
                    return RegularData.this.isValid(index) ? 1.0 : 0.0; 
                }
            };

            @Override
            public boolean isValid(IndexType index) {
                int self = RegularData.this.isValid(index) ? 1 : 0;

                final IndexType from = getIndexInstance();
                final IndexType to = getIndexInstance();
                
                for(int i=dimension(); --i >= 0; ) {
                    from.setValue(i, Math.max(0, index.getValue(i)-1));
                    to.setValue(i, Math.min(getSize(i), index.getValue(i) + 1));
                }
                
                return loop(op, from, to) >= minNeighbors + self;         
            }

            @Override
            public void discard(IndexType index) {
                RegularData.this.discard(index);
            }
        };
    }

    

    public final Referenced<IndexType, VectorType> getNeighbors() {
        final IndexType size = getIndexInstance();
        final IndexType center = getIndexInstance();

        size.fill(3);
        center.fill(1);

        final RegularData<IndexType, VectorType> neighbors = newImage(size, Double.class);

        ParallelPointOp.Simple<IndexType> op = new ParallelPointOp.Simple<IndexType>() {
            @Override
            public void process(IndexType index) {
                if(index.equals(center)) return;
                double d = index.distanceTo(center);
                neighbors.set(index, 1.0 / (d*d));
            }
        };

        neighbors.loop(op);

        VectorType refIndex = getVectorInstance();
        center.toVector(refIndex);

        return new ReferencedData<>(neighbors, refIndex);
    }
    
    public final void resampleFrom(final RegularData<IndexType, VectorType> image) {     
        VectorType scale = getVectorInstance();

        boolean antiAlias = false;
        for(int i=dimension(); --i >= 0; ) {
            double scalar = (double) image.getSize(i) / getSize(i);
            if(scalar > 1.0) antiAlias = true;
            scale.setComponent(i, scalar);
        }

        Referenced<IndexType, VectorType> beam = antiAlias ? getGaussianBeam(scale) : null;        
        resampleFrom(image, new Stretch<>(scale), beam, null);
    }


    public Referenced<IndexType, VectorType> getGaussianBeam(final VectorType pixelFWHMs) {
        final IndexType size = getIndexInstance();
        final VectorType center = getVectorInstance();

        for(int i=dimension(); --i >= 0; ) {
            size.setValue(i, 1 + 2 * (int) Math.ceil(3.0 * Math.abs(pixelFWHMs.getComponent(i))));
            center.setComponent(i, 0.5 * (size.getValue(i) - 1));
        }
        
        final RegularData<IndexType, VectorType> beam = newImage(size, Double.class);

        ParallelPointOp.Simple<IndexType> op = new ParallelPointOp.Simple<IndexType>() {
            private VectorType v;
            
            @Override
            public void init() {
                super.init();
                v = getVectorInstance();
            }
            
            @Override
            public void process(IndexType index) {
                index.toVector(v);
                v.subtract(center);
                for(int i=dimension(); --i >= 0; ) v.setComponent(i, v.getComponent(i) / pixelFWHMs.getComponent(i));
                beam.set(index, Math.exp(-0.5 * v.squareNorm()));
            }
        };

        beam.smartFork(op);

        return new ReferencedData<>(beam, center);
    }



    public RegularData<IndexType, VectorType> getCropped(final IndexType from, final IndexType to) {
        final IndexType size = getIndexInstance();
        size.setDifference(to, from);

        final RegularData<IndexType, VectorType> cropped = newImage(size, getElementType());

        ParallelPointOp.Simple<IndexType> op = new ParallelPointOp.Simple<IndexType>() {
            @Override
            public void process(IndexType index) {
                final boolean isValid = isValid(index);

                if(isValid) {
                    Number value = get(index);
                    index.subtract(from);
                    cropped.set(index, value);
                }
                else {
                    index.subtract(from);
                    cropped.discard(index);
                }     
            }        
        };

        smartFork(op, from, to);

        return cropped;
    }   




    public VectorType getCentroidIndex() {

        class Result {
            VectorType centroid = getVectorInstance();
            double sumw = 0.0;
        }

        ParallelPointOp<IndexType, Result> op = new ParallelPointOp<IndexType, Result>() {
            private Result localResult;

            @Override
            protected void init() {
                localResult = new Result();
            }

            @Override
            public void process(IndexType index) {
                if(!isValid(index)) return; 
                VectorType c = localResult.centroid;
                final double w = Math.abs(get(index).doubleValue());
                for(int i=dimension(); --i >= 0; ) c.setComponent(i, c.getComponent(i) + w * index.getValue(i));
                localResult.sumw += w;
            }

            @Override
            public Result getResult() {
                Result result = new Result();
                result.centroid = localResult.centroid;
                result.sumw = localResult.sumw;
                result.centroid.scale(1.0 / result.sumw);
                return result;
            }

            @Override
            public void mergeResult(Result localResult) {
                this.localResult.centroid.add(localResult.centroid);
                this.localResult.sumw += localResult.sumw;
            }
        };

        smartFork(op);

        return op.getResult().centroid;
    }
    
    @Override
    public ArrayList<BasicHDU<?>> getHDUs(Class<? extends Number> dataType) throws FitsException {
        ArrayList<BasicHDU<?>> hdus = new ArrayList<>();
        hdus.add(createPrimaryHDU(dataType));
        return hdus;
    }
 
    public final ImageHDU createPrimaryHDU(Class<? extends Number> dataType) throws FitsException {  
        ImageHDU hdu = (ImageHDU) Fits.makeHDU(getPrimaryFitsImage(dataType));
        editHeader(hdu.getHeader());
        return hdu;
    }

    public Object getPrimaryFitsImage(Class<? extends Number> dataType) {  
        IndexType tSize = getIndexInstance();
        tSize.setReverseOrderOf(getSize());

        final Data<IndexType> transpose = newImage(tSize, dataType);

        ParallelPointOp.Simple<IndexType> op = new ParallelPointOp.Simple<IndexType>() {
            private IndexType reversed;

            @Override
            public void init() {
                super.init();
                reversed = getIndexInstance();
            }

            @Override
            public void process(IndexType index) {
                reversed.setReverseOrderOf(index);
                transpose.set(reversed, isValid(index) ? get(index) : getInvalidValue());
            }     
        };

        smartFork(op);

        if(getUnit().value() != 1.0) transpose.scale(1.0 / getUnit().value());

        return transpose.getCore();
    }

    /**
     * Returns the default interpolation type to use on this data in-between grid positions.
     * 
     * @return       the currently set interpolation constant, to use by default.
     * 
     * @see #setInterpolationType(Interpolator.Type)
     */
    public Interpolator.Type getInterpolationType() { return interpolationType; }

    
    /**
     * Sets the default interpolation type to use on this data in-between grid positions.
     * 
     * @param value     the new interpolation constant to use, unless specified
     *                  otherwise in the call itself.
     * 
     * @see #getInterpolationType()
     */
    public void setInterpolationType(Interpolator.Type value) { this.interpolationType = value; }


    public void addTo(final IndexedValues<IndexType, ?> v) {
        ParallelPointOp.Simple<IndexType> op = new ParallelPointOp.Simple<IndexType>() {
            @Override
            public void process(IndexType index) {
                v.add(index, get(index));
            } 
            @Override
            public int numberOfOperations() {
                return 3 * dimension();
            }
        };
        smartFork(op);
    }
    
    public void copyTo(final IndexedValues<IndexType, ?> v) {
        ParallelPointOp.Simple<IndexType> op = new ParallelPointOp.Simple<IndexType>() {
            @Override
            public void process(IndexType index) {
                v.set(index, get(index));
            } 
            @Override
            public int numberOfOperations() {
                return 2 * dimension();
            }
        };
        smartFork(op);
    }
    
    /**
     * Returns a coarse version of this data and corresponding weights, in which every point corresponds to 
     * a block averaged value and weight of the original. This is  equivalent to smoothing by a boxcar of the 
     * size of the downsampling factors first, before sampling at the intervals of matching size.
     * 
     * @param weight    optional weights to use or <code>null</code> to use uniform weights
     * @param factor    the downsampling (integer) factors along each index components
     * @return          a new weighted pair of the down-aevraged image, and the corresponding down-averaged
     *                  weights.
     *                  
     * @see #createDownSampledFrom(IndexedValues, Index)
     */
    public WeightedSet<RegularData<IndexType, VectorType>> createDownAveraged(final IndexedValues<IndexType, ?> weight, final IndexType factor) {   
        IndexType n = getIndexInstance();
        n.setRoundedRatio(getSize(), factor);
    
        final RegularData<IndexType, VectorType> coarse = newImage(n, Double.class);
        final RegularData<IndexType, VectorType> coarseWeight = newImage(n, Double.class);

        // We calculate coarse versions of the data (and weights) by averaging in cubes of step size
        // This is a fast coarse smoothing step, that is approximate, and relies on the assumption
        // that the real smoothing beam has little or no structure below the step scale.
        ParallelPointOp.Simple<IndexType> op = new ParallelPointOp.Simple<IndexType>() {
            IndexType scaled;

            @Override
            public void init() {
                super.init();
                scaled = getIndexInstance();
            }

            @Override
            public void process(IndexType index) {
                if(!isValid(index)) return;
                
                double w = (weight == null) ? 1.0 : weight.get(index).doubleValue();
                scaled.setRatio(index, factor);
                coarse.add(scaled, w * get(index).doubleValue());
                coarseWeight.add(scaled, w);
            }  

            @Override
            public int numberOfOperations() {
                return 8 * dimension();
            }
        };
        
        smartFork(op); 
        
        // Renormalize...
        op = new ParallelPointOp.Simple<IndexType>() {
            @Override
            public void process(IndexType index) {
                double w = coarseWeight.get(index).doubleValue();
                if(w != 0.0) coarse.scale(index, 1.0 / w);
            }
            @Override
            public int numberOfOperations() {
                return 3 + 4 * dimension();
            }
        };
        
        coarse.smartFork(op);
        
        return new WeightedSet<>(coarse, coarseWeight);
    }
    
    /**
     * Returns a new image of the same type as this one, created as a downsampled version of the supplied 
     * values of the same dimensionality as this data. This method really just samples the original data
     * at intervals of the specified <code>factor</code> argument to create the new image.
     * 
     * @param v         the values to downsample
     * @param factor    the downsampling (integer) factors along each index components
     * @return          a new image of the same type as this one, containing the downsampled values of the
     *                  argument.
     *                  
     * @see #createDownAveraged(IndexedValues, Index)
     */
    public RegularData<IndexType, VectorType> createDownSampledFrom(final IndexedValues<IndexType, ?> v, final IndexType factor) {
        
        IndexType n = v.getIndexInstance();
        n.setRatio(v.getSize(), factor);

        final RegularData<IndexType, VectorType> coarse = newImage(n, v.getElementType());
        
        // We calculate a downsampled smoothing beam by sampling the original beam at the stepped locations
        // only. This relies on the assumption that the smoothing beam has little or no structure below the
        // stepping scales
        ParallelPointOp.Simple<IndexType> op = new ParallelPointOp.Simple<IndexType>() {
            IndexType scaled;

            @Override
            public void init() {
                super.init();
                scaled = getIndexInstance();
            }

            @Override
            public void process(IndexType index) {
                scaled.setProduct(index, factor);
                coarse.set(index, v.get(scaled));
            }   

            @Override
            public int numberOfOperations() {
                return 6 * dimension();
            }
        };
        
        coarse.smartFork(op);
        return coarse;
    }
    
   
    public RegularData<IndexType, VectorType> getShifted(final VectorType shift) {

        final RegularData<IndexType, VectorType> shifted = newImage();
        
        // We calculate a downsampled smoothing beam by sampling the original beam at the stepped locations
        // only. This relies on the assumption that the smoothing beam has little or no structure below the
        // stepping scales
        Interpolation op = new Interpolation() {
            VectorType from;
            
            @Override
            public void init() {
                super.init();
                from = getVectorInstance();
            }

            @Override
            public void process(IndexType index) {
                for(int i=dimension(); --i >= 0; ) from.setComponent(i, index.getValue(i) - shift.getComponent(i));
                shifted.set(index, valueAtIndex(from, getSplines()));
            }   

            @Override
            public int numberOfOperations() {
                return dimension() + getInterpolationOps();
            }
        };
        
        shifted.smartFork(op);
        return shifted;
    }
    

    
    /**
     * An abstract base class for operations that implement interpolation on this regularly sampled data,
     * possibly in a parallel processing environment.
     * 
     * @author Attila Kovacs
     *
     */
    public abstract class Interpolation extends ParallelPointOp.Simple<IndexType> {
        private SplineSet<VectorType> splines = new SplineSet<>(dimension());

        @Override
        protected Interpolation clone() {
            Interpolation clone = (Interpolation) super.clone();
            clone.splines = new SplineSet<>(dimension());
            return clone;
        }

        /**
         * Returns a dedicated set of splines to use specifically for this instance of the parallel operation.
         * When the operation is executed in parallel, each thread will have its own clone of this
         * operation, with its own dedicated set of splines, to avoid race conditions and/or blocking while
         * interpolating in parallel.
         * 
         * @return  a dedicate set of splines to use with this instance of the operation only.
         */
        public final SplineSet<VectorType> getSplines() { return splines; }
    }  
    
}
