/*******************************************************************************
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
 *     Attila Kovacs <attila[AT]sigmyne.com> - initial API and implementation
 ******************************************************************************/

package jnum;

/**
 * An expection that indicates that there is something about the shape of the object (e.g. a matrix)
 * or objects (e.g. a matrix and a vector) that makes the operation operation impossible to
 * perform as requested.
 * 
 * @author Attila Kovacs <attila@sigmyne.com>
 *
 */
public class ShapeException extends IllegalArgumentException {

    /**
     * 
     */
    private static final long serialVersionUID = -4097408545266016198L;

    
    public ShapeException() {
        super();
    }

    public ShapeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ShapeException(String s) {
        super(s);
    }

    public ShapeException(Throwable cause) {
        super(cause);
    }

    
}
