/* *****************************************************************************
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
 *     Attila Kovacs  - initial API and implementation
 ******************************************************************************/

package jnum;

/**
 * A generic exception indicating an operational failure because of a mismatch in types, such as between two arguments or an argument 
 * and an internal variable.
 * 
 * 
 * @author Attila Kovacs
 *
 */
public class IncompatibleTypesException extends IllegalArgumentException {

    /**
     * 
     */
    private static final long serialVersionUID = -7538968509151055033L;

    /**
     * Constructs and IncompatibleTypesException with a corresponding message identifying the conflicting classes.
     * 
     * @param a     One of the conflicting objects
     * @param b     The other conflicting object
     */
    public IncompatibleTypesException(Object a, Object b) {
        super(a.getClass().getSimpleName() + " is not compatible with " + b.getClass().getSimpleName());
    }
    
}
