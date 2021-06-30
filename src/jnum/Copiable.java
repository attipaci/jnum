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
 * The interface for implementing standard deep copies of objects. Deep copies are useful for spawning a new, entirely
 * independent object from an object, while carrying over all of the original object properties.
 * 
 * 
 * <b>The main principle of deep copies is to ensure that any changes to the copy will leave the original object uneffected, 
 * and vice-versa.</b> As such, deep copies must follow the following rules:
 * 
 * 
 *  <ul>
 *  <li>The copy's class should match the original's class exactly.
 *  <li><i>Recursivity</i>: for all mutable object fields, the copy must have a deep copy of the field itself. E.g. for 
 *  a field that is collection, the copied object must have an entirely separate collection containing deep copies
 *  of all the elements in the original collection. Same for arrays, etc.
 *  <li>primitive fields are exempt, provided they were cloned (since these aren't references).
 *  <li>Inmutable fields are also exempt, since they cannot be used to change the state of the original object from the copy.
 *  object in the copy.
 *  <li>Copies are most easily generated starting via {@link Object#clone()}. However, cloning is not required.
 *  </ul>
 *
 * @author Attila Kovacs
 *
 * @param <Type> the generic type of the copies created.
 * 
 * @see Object#clone()
 */
public interface Copiable<Type> {

	/**
	 * Gets a deep copy of this object. The copy carries over the state/data from this object but is completely independent
	 * from the original, s.t. any change to the copy will leave the original object unaffected and vice-versa.
	 *
	 * @return the independent deep copy of the object that implements the <code>copy()</code> call.
	 */
	public Type copy();
	
}
