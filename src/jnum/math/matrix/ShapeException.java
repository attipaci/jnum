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

package jnum.math.matrix;

import jnum.NonConformingException;

/**
 * An expection that indicates that there is something about the shape of the argument (e.g. a matrix)
 * or objects (e.g. a matrix and a vector) that makes the operation operation impossible to
 * perform as requested.
 * 
 * @author Attila Kovacs
 *
 */
public class ShapeException extends NonConformingException {

    /**
     * 
     */
    private static final long serialVersionUID = -4097408545266016198L;

    /**
     * Instantiates a new exception when an object has an unexpected or incompatible size or shape.
     * 
     */
    public ShapeException() {
        super();
    }

    /**
     * Instantiates a new exception when an object has an unexpected or incompatible size or shape.
     * 
     * @param message       the message string
     * @param cause         the original cause that was thrown to trigger this exception. 
     * 
     */
    public ShapeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new exception when an object has an unexpected or incompatible size or shape.
     * 
     * @param s             the message string 
     * 
     */
    public ShapeException(String s) {
        super(s);
    }

    /**
     * Instantiates a new exception when an object has an unexpected or incompatible size or shape.
     * 
     * @param cause         the original cause that was thrown to trigger this exception. 
     * 
     */
    public ShapeException(Throwable cause) {
        super(cause);
    }

    
}
