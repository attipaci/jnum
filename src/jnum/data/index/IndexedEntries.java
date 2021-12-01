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

package jnum.data.index;

import jnum.PointOp;
import jnum.parallel.ParallelPointOp;
import jnum.parallel.ParallelTask;

/**
 * Access to data entries using an index.
 * 
 * @author Attila Kovacs
 *
 * @param <IndexType>
 * @param <DataType>
 */
public interface IndexedEntries<IndexType extends Index<IndexType>, DataType> {
    
    /**
     * Gets the number of data elements being held in this object (populated or not).
     * 
     * @return      the total number of data elements in this objects.
     */
    public int capacity();
    
    /**
     * Gets the dimensionality of this data, i.e. the number of independent 
     * components in its data indices. For example a image plane with i and j
     * data indices would have a dimension of 2.
     * 
     * @return  the dimension of this data.
     */
    public int dimension();
    
    /**
     * Gets the size of this data, returning the number of elements contained
     * along each dimension. The retuned value should be treated as read-only, such that
     * implementation may return a static object. If you do need to operate on the
     * returned index, you should make a copy using {@link Index#copy()}.
     * 
     * @return  the number of data elements along each dimension.
     * 
     * @see #getSize(int)
     * @see Index#copy()
     */
    public IndexType getSize();
    
    /**
     * REturns the size along the <i>i</i><sup>th</sup> dimension.
     * 
     * @param i     the index of the data dimension along which to query the size.
     * @return      the size along the specified dimension
     * @throws IllegalArgumentException     if the index is not within the dimensionality of the data.
     */
    public int getSize(int i) throws IllegalArgumentException;
    
    /**
     * Checks if the datum at the specified index is valid.
     * 
     * @param index     the data index
     * @return          <code>true</code> if this data object contains valid number value at the specified index,
     *                  otherwise <code>false</code>.
     *                  
     * @throws IndexOutOfBoundsException   if the index it outside the range of this data.
     */
    public boolean isValid(IndexType index) throws IndexOutOfBoundsException;
    
    /**
     * Gets the data stored at the specified index.
     * 
     * @param index     the index of the data
     * @return          the data element at that index. It is a reference to an object, or a
     *                  primitive value.
     * @throws IndexOutOfBoundsException   if the index it outside the range of this data.
     */
    public DataType get(IndexType index) throws IndexOutOfBoundsException;
    
    /**
     * Sets a new data element at the specified data index.
     * 
     * @param index     the index of the data
     * @param value     the new elements to set. The data object will reference that value
     *                  or set a primitive type equal to the supplieed value.
     * @throws IndexOutOfBoundsException   if the index it outside the range of this data.
     */
    public void set(IndexType index, DataType value) throws IndexOutOfBoundsException;
    
    /**
     * Gets a new generic type index instance for this data object.
     * 
     * @return      A new index object for this data.
     */
    public IndexType getIndexInstance();
    
    /**
     * Checks if this data has a matching size, along all dimensions, as the size
     * specified.
     * 
     * @param size      the expected size along each dimention
     * @return          <code>true</code> if the object has the expected size. Otherwise <code>false</code>.
     */
    public boolean conformsTo(IndexType size);
         
    /**
     * Checks if this data object has a matching size, in all dimensions, as another data object
     * with the same generic type of index.
     * 
     * @param data      The other indexed data object.
     * @return          <code>true</code> if this object has the same size as the argument. Otherwise <code>false</code>.
     */
    public default boolean conformsTo(IndexedValues<IndexType, ?> data) {
        return conformsTo(data.getSize());
    }
    
    /**
     * Gets a string representation of the object's size in all dimensions, usually in square brackets.
     * 
     * @return  A string representation of this object's size.
     */
    public default String getSizeString() {
        return getSize().toString("x");
    }
    
    /**
     * Checks if the supplied index is contained within the bounds of this object.
     * 
     * @param index The index of an element we may try to access.
     * @return      <code>true</code> if the index is within the supported bounds of this object. Otherwise <code>false</code>.
     */
    public boolean containsIndex(IndexType index);
    
    public int getParallel();
    
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
    public <ReturnType> ReturnType loop(PointOp<IndexType, ReturnType> op, IndexType from, IndexType to);

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
    public default <ReturnType> ReturnType loopValid(PointOp<DataType, ReturnType> op, IndexType from, IndexType to) {
        return loop(new PointOp<IndexType, ReturnType>() {

            @Override
            protected void init() {}
        
            @Override
            public void process(IndexType index) {
                if (isValid(index)) op.process(get(index));
            }

            @Override
            public ReturnType getResult() {
                return op.getResult();
            }
        }, from, to);
    }

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
    public default <ReturnType> ReturnType fork(final ParallelPointOp<IndexType, ReturnType> op, IndexType from, IndexType to) {
        return from.fork(op, to);
    }

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
    public default <ReturnType> ReturnType forkValid(final ParallelPointOp<DataType, ReturnType> op, IndexType from, IndexType to) {
        return from.fork(new ParallelPointOp<IndexType, ReturnType>() {
            ParallelPointOp<DataType, ReturnType> localOp;

            @Override
            public void mergeResult(ReturnType localResult) {
                localOp.mergeResult(localResult);
            }

            @Override
            protected void init() {
                localOp = op.newInstance();
                localOp.reset();
            }

            @Override
            public void process(IndexType index) {
                if(isValid(index)) localOp.process(get(index));
            }
            
            @Override
            public ReturnType getResult() {
                return localOp.getResult();
            }
            
        }, to);
    }
    
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
    public default <ReturnType> ReturnType smartFork(final ParallelPointOp<IndexType, ReturnType> op, IndexType from, IndexType to) {
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
    public default <ReturnType> ReturnType smartForkValid(final ParallelPointOp<DataType, ReturnType> op, IndexType from, IndexType to) {
        if(getParallel() < 2) return loopValid(op, from, to);
        IndexType span = getIndexInstance();
        span.setDifference(to, from);
        if(span.getVolume() * (2 + op.numberOfOperations()) < 2 * ParallelTask.minExecutorBlockSize) return loopValid(op, from, to);
        return forkValid(op, from, to);  
    }
    
    /**
     * Loops over all data elements, performing the specified point operation on each.
     * 
     * @param <ReturnType>  the generic return value type for the operation.
     * @param op            the point operation to perform on each data element
     * @return              the return value of the operation (if any).
     * 
     * @see #loopValid(PointOp)
     * @see #fork(ParallelPointOp)
     */
    public default <ReturnType> ReturnType loop(PointOp<IndexType, ReturnType> op) {
        return loop(op, getIndexInstance(), getSize());
    }

    /**
     * Loops over all valid data elements, performing the specified point operation on each.
     * 
     * @param <ReturnType>  the generic return value type for the operation.
     * @param op            the point operation to perform on each data element
     * @return              the return value of the operation (if any).
     * 
     * @see #loop(PointOp)
     * @see #forkValid(ParallelPointOp)
     */
    public default <ReturnType> ReturnType loopValid(PointOp<DataType, ReturnType> op) {
        return loopValid(op, getIndexInstance(), getSize());
    }

    /**
     * Process elements (points) in this data object in parallel with the specified point operation.
     * 
     * @param <ReturnType>  the generic return value type for the operation.
     * @param op            the (parallel) point operation to perform on each data element
     * @return              the return value of the operation (if any).
     * 
     * @see #smartFork(ParallelPointOp)
     * @see #forkValid(ParallelPointOp)
     * @see #loop(PointOp)
     */
    public default <ReturnType> ReturnType fork(final ParallelPointOp<IndexType, ReturnType> op) {
        return fork(op, getIndexInstance(), getSize());
    }
    
    /**
     * Process only valid elements (points) in this data object in parallel with the specified point operation.
     * 
     * @param <ReturnType>  the generic return value type for the operation.
     * @param op            the (parallel) point operation to perform on each data element
     * @return              the return value of the operation (if any).
     * 
     * @see #smartForkValid(ParallelPointOp)
     * @see #fork(ParallelPointOp)
     * @see #loopValid(PointOp)
     */
    public default <ReturnType> ReturnType forkValid(final ParallelPointOp<DataType, ReturnType> op) {
        return forkValid(op, getIndexInstance(), getSize());
    }

    /**
     * Process elements (points) in this data object efficiently, using parallel or sequential processing, whichever
     * is deemed fastest for the data size and the specified point operation.
     * 
     * @param <ReturnType>  the generic return value type for the operation.
     * @param op            the (parallel) point operation to perform on each data element
     * @return              the return value of the operation (if any).
     * 
     * @see #loop(PointOp)
     * @see #fork(ParallelPointOp)
     * @see #smartForkValid(ParallelPointOp)
     */
    public default <ReturnType> ReturnType smartFork(final ParallelPointOp<IndexType, ReturnType> op) {
        return smartFork(op, getIndexInstance(), getSize());
    }

    /**
     * Process valid elements (points) only in this data object efficiently, using parallel or sequential processing, whichever
     * is deemed fastest for the data size and the specified point operation.
     * 
     * @param <ReturnType>  the generic return value type for the operation.
     * @param op            the (parallel) point operation to perform on each data element
     * @return              the return value of the operation (if any).
     * 
     * @see #loopValid(PointOp)
     * @see #forkValid(ParallelPointOp)
     * @see #smartFork(ParallelPointOp)
     */
    public default <ReturnType> ReturnType smartForkValid(final ParallelPointOp<DataType, ReturnType> op) {
        return smartForkValid(op, getIndexInstance(), getSize());
    }
    


    
}
