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
    protected void applyTemplateTo(ImageType image) {
        image.setSize(new Index2D(sizeX(), sizeY()));
        super.applyTemplateTo(image);
    }
    

    public final void crop(Index3D from, Index3D to) {
        cropZ(from.k(), to.k());
        cropXY(new Index2D(from.i(), from.j()), new Index2D(to.i(), to.j()));
    }
   
    public void setSize(int sizeX, int sizeY, int sizeZ) {
        destroy();
        setSizeZ(sizeZ);
        setSizeXY(sizeX, sizeY);
    }
    
    public void setSizeXY(int sizeX, int sizeY) {
        setSizeXY(new Index2D(sizeX, sizeY));
    }
        
    public void setSizeXY(Index2D sizeXY) { 
        getPlaneTemplate().setSize(sizeXY);
        for(int k=sizeZ(); --k >= 0; ) getPlane(k).setSize(sizeXY);
        addHistory("XY-size: " + getPlaneTemplate().getSizeString());
    }

    public abstract void cropXY(Index2D from, Index2D to);
    
}
