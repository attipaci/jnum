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

package jnum.math.matrix;

public class DiagonalMatrixException extends SquareMatrixException {
    /**
     * 
     */
    private static final long serialVersionUID = -8024324298675264092L;
    
    
    public DiagonalMatrixException() {
        super(defaultMessage);
    }

    public DiagonalMatrixException(String message, Throwable cause) {
        super(message, cause);
    }

    public DiagonalMatrixException(String s) {
        super(s);
    }

    public DiagonalMatrixException(Throwable cause) {
        super(defaultMessage, cause);
    }

    public static String defaultMessage = "diagonal matrix operation on a non-diagonal matrix.";
        
}
