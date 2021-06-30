/* *****************************************************************************
 * Copyright (c) 2016 Attila Kovacs <attila[AT]sigmyne.com>.
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

package jnum.data.fitting;


/**
 * An easily identifiable exception specifically for {@link Minimizer} implementations.
 */
public class ConvergenceException extends IllegalStateException {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -632245294706738192L;

    /**
     * Instantiates a new convergence exception.
     */
    public ConvergenceException() {
        super();
    }

    /**
     * Instantiates a new convergence exception with a specific message.
     *
     * @param s the message providing some explanation on why the exception occurred. 
     */
    public ConvergenceException(String s) {
        super(s);
    }


}
