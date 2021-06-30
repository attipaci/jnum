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
 * An exception thrown when a singularity (degeneracy) in the matrix prevents
 * performing an operation, such as a matrix inversion.. 
 * 
 * @author Attila Kovacs
 *
 */
public class SingularMatrixException extends ArithmeticException {

    /**
     * 
     */
    private static final long serialVersionUID = 5112498399596176163L;

    public SingularMatrixException() {
        super(defaultMessage);
    }

    public SingularMatrixException(String s) {
        super(s);
    }

    private static String defaultMessage = "Cannot perform operation: singular input matrix.";
}
