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
package jnum.data;

import jnum.math.Function;
import jnum.text.Parser;


// TODO: Auto-generated Javadoc
/**
 * The Class GenericArray.
 *
 * @param <T> the generic type
 */
public class GenericArray<T> extends AbstractArray<T> {
	
	/**
	 * Instantiates a new generic array.
	 *
	 * @param type the type
	 */
	public GenericArray(Class<T> type) {
		super(type);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Instantiates a new generic array.
	 *
	 * @param type the type
	 * @param dimensions the dimensions
	 */
	public GenericArray(Class<T> type, int[] dimensions) {
		super(type, dimensions);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Instantiates a new generic array.
	 *
	 * @param data the data
	 */
	public GenericArray(Object data) {
		super(data);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see kovacs.data.AbstractArray#addPatchAt(java.lang.Object, double[], double[], kovacs.math.Function)
	 */
	@Override
	public void addPatchAt(T point, double[] exactpos, double[] patchSize,
			Function<double[], Double> shape) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see kovacs.data.AbstractArray#lineElementAt(java.lang.Object, int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected T lineElementAt(Object linearArray, int index) {
		return ((T[]) linearArray)[index];
	}

	/* (non-Javadoc)
	 * @see kovacs.data.AbstractArray#setLineElementAt(java.lang.Object, int, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void setLineElementAt(Object linearArray, int index, T value) {
		((T[]) linearArray)[index] = value;
		
	}

	/* (non-Javadoc)
	 * @see kovacs.data.AbstractArray#slim()
	 */
	@Override
	public void slim() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see kovacs.data.AbstractArray#subArrayAt(int[])
	 */
	@Override
	public AbstractArray<T> subArrayAt(int[] index) {
		return new GenericArray<T>(rawSubArrayAt(index));
	}

	/* (non-Javadoc)
	 * @see kovacs.data.AbstractArray#parseElement(java.lang.String)
	 */
	@Override
	public T parseElement(String text) throws ClassCastException, InstantiationException, IllegalAccessException  {
		T value = type.newInstance();
		((Parser) value).parse(text);
		return value;
	}

	
}
