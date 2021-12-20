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

/**
 * An exception thrown when trying to set or modify off-diagonal elements in
 * a diagonal matrix.
 * 
 * @author Attila Kovacs
 *
 */
public class DiagonalMatrixException extends SquareMatrixException {
    /**
     * 
     */
    private static final long serialVersionUID = -8024324298675264092L;
    
    /**
     * If an unsupported off-diagonal access was attempted on a diagonal matrix, using the
     * default message.
     * 
     * @see #DiagonalMatrixException(String)
     * @see DiagonalMatrixException#defaultMessage 
     */
    public DiagonalMatrixException() {
        super(defaultMessage);
    }

    /**
     * If an unsupported off-diagonal access was attempted on a diagonal matrix. 
     * 
     * @param s     The message that describes the exception.
     *  
     */
    public DiagonalMatrixException(String s) {
        super(s);
    }

    /**
     * The standard message to use for default diagonal matrix exceptions.
     * 
     */
    public static String defaultMessage = "off-diagonal matrix operation on a diagonal matrix.";
    
}
