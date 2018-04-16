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

import jnum.data.Resizable;
import jnum.data.cube.Index3D;
import jnum.data.image.Data2D;
import jnum.data.image.Index2D;

public abstract class Resizable2D1<ImageType extends Data2D & Resizable<Index2D>> extends Data2D1<ImageType> {

    @Override
    public final ImageType getImage2DInstance(int sizeX, int sizeY) {
        ImageType image = getPlaneInstance();
        image.setSize(new Index2D(sizeX, sizeY));
        return image;
    }

    public abstract ImageType getPlaneInstance();
    
    public abstract void cropXY(int fromi, int fromj, int toi, int toj);
    
    
    
    public final void crop(Index3D from, Index3D to) {
        crop(from.i(), from.j(), from.k(), to.i(), to.j(), to.k());
    }
    
    public final void crop(int fromi, int fromj, int fromk, int toi, int toj, int tok) {
        cropZ(fromk, tok);
        cropXY(fromi, fromj, toi, toj);
    }
    
    public final void cropXY(Index2D from, Index2D to) {
        cropXY(from.i(), from.j(), to.i(), to.j());
    }
    
    
    
}
