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
 * Attempting to perform a square matrix operation on a non-square matrix. 
 * 
 * @author Attila Kovacs
 *
 */
public class SquareMatrixException extends ArithmeticException {

    /** 
     * Instatiates a new exception for when a square matrix operation is attempted on a non-square matrix.
     */
    public SquareMatrixException() {
        super(defaultMessage);
    }

    /** 
     * Instatiates a new exception for when a square matrix operation is attempted on a non-square matrix, using
     * the specific message.
     * 
     * @param s     the message string
     */
    public SquareMatrixException(String s) {
        super(s);
    }

    /**
     * The default message string, to use with the default constructor.
     * 
     */
    private static String defaultMessage = "square matrix operation on a non-square matrix.";
    
    /**
     * 
     */
    private static final long serialVersionUID = -612910914774238752L;

}
