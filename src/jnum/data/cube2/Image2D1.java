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

import java.io.Serializable;

import jnum.data.image.Image2D;
import jnum.data.image.Index2D;

public abstract class Image2D1 extends Resizable2D1<Image2D> implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 7816827408682434510L;

    public static Image2D1 create(Class<? extends Number> type) {
        if(type.equals(Double.class)) return new Double2D1();
        if(type.equals(Float.class)) return new Float2D1();
        if(type.equals(Long.class)) return new Long2D1();
        if(type.equals(Integer.class)) return new Integer2D1();
        if(type.equals(Short.class)) return new Short2D1();
        if(type.equals(Byte.class)) return new Byte2D1();
        throw new IllegalArgumentException("unsupported type: " + type);
    }
    
    public static Image2D1 create(Class<? extends Number> type, int sizeX, int sizeY, int sizeZ) {
        Image2D1 image = create(type);
        image.setSize(sizeX, sizeY, sizeZ);
        return image;
    }
    
    
    @Override
    public void cropXY(Index2D from, Index2D to) {
        for(Image2D plane : getPlanes()) plane.crop(from, to);
    }
    
    
    public static class Double2D1 extends Image2D1 {
        /**
         * 
         */
        private static final long serialVersionUID = 113550949968714499L;

        @Override
        public Image2D newPlaneInstance() { return new Image2D.Double2D(); }

        
    }
    
    public static class Float2D1 extends Image2D1 {
        /**
         * 
         */
        private static final long serialVersionUID = 1243630861744367750L;

        @Override
        public Image2D newPlaneInstance() { return new Image2D.Float2D(); }
    }
    
    public static class Long2D1 extends Image2D1 {
        /**
         * 
         */
        private static final long serialVersionUID = -1107032476154933891L;

        @Override
        public Image2D newPlaneInstance() { return new Image2D.Long2D(); }
    }
    
    public static class Integer2D1 extends Image2D1 {
        /**
         * 
         */
        private static final long serialVersionUID = -4354855370129531989L;

        @Override
        public Image2D newPlaneInstance() { return new Image2D.Integer2D(); }
    }
    
    public static class Short2D1 extends Image2D1 {
        /**
         * 
         */
        private static final long serialVersionUID = -5534545653389046267L;

        @Override
        public Image2D newPlaneInstance() { return new Image2D.Short2D(); }
    }
    
    public static class Byte2D1 extends Image2D1 {
        /**
         * 
         */
        private static final long serialVersionUID = 5091245705693686730L;

        @Override
        public Image2D newPlaneInstance() { return new Image2D.Byte2D(); }
    }
    
    
}
