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

package jnum;


/**
 * A class for indicating that one object does not conform to another object of the same of compatible type. For example,
 * two image objects of the same class, but of different dimensions, may throw such an exception if the operation that
 * works on those two images cannot proceed because of their size difference. 
 * 
 * @author Attila Kovacs
 *
 */
public class NonConformingException extends IllegalArgumentException {


    private static final long serialVersionUID = 6650987858665661469L;
    
    /**
     * Instantiates a new non conforming exception with no specific message detailing the failure.
     */
    public NonConformingException() {
        super();
    }


    public NonConformingException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }


    public NonConformingException(String arg0) {
        super(arg0);
    }


    public NonConformingException(Throwable arg0) {
        super(arg0);
    }


}
