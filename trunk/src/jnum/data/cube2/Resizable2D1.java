/*******************************************************************************
 * Copyright (c) 2017 Attila Kovacs <attila[AT]sigmyne.com>.
 * All rights reserved. 
 * 
 * This file is part of crush.
 * 
 *     crush is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     crush is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with crush.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Attila Kovacs <attila[AT]sigmyne.com> - initial API and implementation
 ******************************************************************************/

package jnum.data.cube2;

import jnum.data.image.Data2D;
import jnum.data.image.Resizable2D;

public abstract class Resizable2D1<ImageType extends Data2D & Resizable2D> extends Data2D1<ImageType> {

    @Override
    public final ImageType getImage2DInstance(int sizeX, int sizeY) {
        ImageType image = getImage2DInstance();
        image.setSize(sizeX, sizeY);
        return image;
    }

    public abstract ImageType getImage2DInstance();
    
}
