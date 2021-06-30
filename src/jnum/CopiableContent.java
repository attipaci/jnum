/* *****************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila[AT]sigmyne.com>.
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
package jnum;

/**
 * Generates a fully independent copy of the present object (see {@link Copiable}), with the option to omit carrying over 
 * all of the original objects content/data into the copy. For any data not carried over, the copy should be 
 * set to an appropriate initial state.
 * 
 * 
 * The possibility of exempting some part of the object's data from the copy can have performance advantages when
 * the affected data is going to be overwritten after the copying anyways.
 * 
 * @author Attila Kovacs
 *
 * @param <Type>    The generic type of the copies created.
 * 
 */
public interface CopiableContent<Type> extends Copiable<Type> {

    
    /**
     * Gets a deep copy of this object. The copy carries over the state/data from this object, fully or partially depending
     * on the argument, but is completely independent from the original, s.t. any change to the copy will leave the 
     * original object unaffected and vice-versa.
     *
     * @param   withContent     If <code>true</code> all the content/data of the orifinal is carried over into the copy
     *                          (same as {@link #copy()}. If <code>false</code> then the copy may omit some of its
     *                          content (for example the image values of an image object) and set these to their 
     *                          appropriate initial state (e.g. all zeroes). It is up to the implementing class to
     *                          define what content/data may be exempted from the full copy.
     * @return the independent deep copy of the object that implements the <code>copy()</code> call.
     */
	public Type copy(boolean withContent);
	
}
