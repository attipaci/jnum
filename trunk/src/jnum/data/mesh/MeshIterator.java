/*******************************************************************************
 * Copyright (c) 2014 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
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


package jnum.data.mesh;

import java.util.Arrays;
import java.util.NoSuchElementException;

import jnum.data.DataIterator;

// TODO: Auto-generated Javadoc
/**
 * The Class ArrayIterator.
 *
 * @param <T> the generic type
 */
public abstract class MeshIterator<T> implements DataIterator<T> {
	
	/** The parent. */
	ObjectMeshIterator<T> parent;
	
	/** The to index. */
	int currentIndex, fromIndex, toIndex;
	
	/** The index. */
	int[] index;
	
	/**
	 * Instantiates a new array iterator.
	 */
	protected MeshIterator() {}
	
	/**
	 * Instantiates a new array iterator.
	 *
	 * @param from the from
	 * @param to the to
	 */
	public MeshIterator(int[] from, int[] to) {
		fromIndex = from[0];
		toIndex = to[0];
		index = Arrays.copyOf(from, from.length);
	}
	
	/**
	 * Sets the parent.
	 *
	 * @param iterator the new parent
	 */
	public void setParent(ObjectMeshIterator<T> iterator) {
		parent = iterator;
	}
	
	/**
	 * Gets the parent.
	 *
	 * @return the parent
	 */
	public ObjectMeshIterator<T> getParent() {
		return parent;
	}
	
	/* (non-Javadoc)
	 * @see kovacs.data.DataManager#reset()
	 */
	@Override
	public abstract void reset();
	
	/**
	 * Sets the array.
	 *
	 * @param data the new array
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public abstract void setArray(Object data) throws IllegalArgumentException;	

	/* (non-Javadoc)
	 * @see kovacs.data.DataManager#setElement(java.lang.Object)
	 */
	@Override
	public abstract void setElement(T element);
	
	/**
	 * Sets the next element.
	 *
	 * @param element the new next element
	 * @throws NoSuchElementException the no such element exception
	 */
	public void setNextElement(T element) throws NoSuchElementException {
		next();
		setElement(element);
	}
	
	
	/**
	 * Sets the index.
	 *
	 * @param index the new index
	 */
	protected abstract void setIndex(int index);
	
	/**
	 * Position.
	 *
	 * @param index the index
	 */
	public void position(int[] index) { setIndex(index, 0); }
	
	/**
	 * Sets the index.
	 *
	 * @param index the index
	 * @param depth the depth
	 */
	protected abstract void setIndex(int[] index, int depth);
	
	// returned index should be read-only access, else it may be corrupted!!!!
	/**
	 * Gets the index.
	 *
	 * @return the index
	 */
	public int[] getIndex() { return index; }
	
	// void setRange(int[] from, int[] to);
	// int[][] getRange(); // [from|to][]
	
}	
