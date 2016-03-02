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

import jnum.math.LinearAlgebra;
import jnum.math.Multiplicative;





// TODO: Auto-generated Javadoc
/**
 * The Class PrimitiveArray.
 *
 * @param <T> the generic type
 */
public abstract class PrimitiveArray<T extends Number> extends AbstractArray<T> implements LinearAlgebra<PrimitiveArray<?>>, Multiplicative<Number> {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8401121783922804093L;

	/**
	 * Instantiates a new primitive array.
	 *
	 * @param type the type
	 * @param dimensions the dimensions
	 */
	public PrimitiveArray(Class<T> type, int[] dimensions) {
		super(type, dimensions);
	}

	/**
	 * Instantiates a new primitive array.
	 *
	 * @param type the type
	 */
	public PrimitiveArray(Class<T> type) {
		super(type);
	}

	/**
	 * Instantiates a new primitive array.
	 *
	 * @param data the data
	 */
	public PrimitiveArray(Object data) {
		super(data);
	}
	
	/* (non-Javadoc)
	 * @see kovacs.data.AbstractArray#initialize()
	 */
	@Override
	public void initialize() {
		try { super.initialize(); }
		catch(Exception e) { e.printStackTrace(); }
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.LinearAlgebra#isNull()
	 */
	@Override
	public boolean isNull() {
		for(Number value : this) if(value.doubleValue() != 0.0) return false;
		return true;
	}
	


}
