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

import java.util.Iterator;

/**
 * An interface that enables crawling data objects. Unlike {@link Iterator}, it allows updating the underlying data
 * elements, not only querying them. Also, crawlers can be re-used: a call to {@link #reset()} will start the
 * next crawl from the beginning all over again...
 *
 * @param <T> the generic type
 */
public interface DataCrawler<T> extends Iterator<T> {

	/**
	 * Returns the underlying data object that this crawler operates on.
	 *
	 * @return the underlying data object.
	 */
	public Object getData();
	
	/**
	 * Sets the element at the current crawler position to the specified value. Use only after {@link Iterator#next()} 
	 * was called to point to a valid element after initialization or {@link #reset()}.
	 *
	 * @param value the new element at the current crawl position.
	 * @see Iterator#next();
	 */
	public void setCurrent(T value);
	
	/**
	 * Check whether the entry at the current position is valid.
	 * 
	 * @return true if the current element is valid, false otherwise.
	 */
	public boolean isValid();
	
	/**
	 * Resets the crawler, so that the underlying data object will be be crawled from the beginning again.
	 * I.e., a successive call to {@link Iterator#next()} will return the first element to be crawled.
	 * 
	 * @see Iterator#next()
	 */
	public void reset();
	
}
