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

package jnum.data;

import java.io.Serializable;

import jnum.data.index.Index;
import nom.tam.fits.FitsException;
import nom.tam.fits.ImageHDU;

/**
 * An image with resizable content.
 * 
 * @author Attila Kovacs
 *
 * @param <IndexType>
 */
public interface Image<IndexType extends Index<IndexType>> extends Resizable<IndexType>, Serializable {

    /**
     * Returns a new image HDU for a FITS representation of this image's data. FITS, the Flexible
     * Image Transport System, is a commonly used data exchange format used in astronomy and related fields.
     * 
     * @param dataType      The number class in which to represent data in thew HDU.
     * @return              a new image HDU that may be added to a Fits object.
     * @throws FitsException    if the HDU could not be created from the data or with the specified nymber class.
     */
    public ImageHDU createHDU(Class<? extends Number> dataType) throws FitsException;
}
