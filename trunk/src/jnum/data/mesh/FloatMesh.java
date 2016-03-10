/*******************************************************************************
 * Copyright (c) 2016 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
 * All rights reserved. 
 * 
 * This file is part of jnum.
 * 
 *     kovacs.util is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     kovacs.util is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with kovacs.util.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Attila Kovacs <attila_kovacs[AT]post.harvard.edu> - initial API and implementation
 ******************************************************************************/

package jnum.data.mesh;

import java.lang.Float;

public class FloatMesh extends PrimitiveMesh<Float> {
    /**
     * 
     */
    private static final long serialVersionUID = -4081004880332314702L;

    public FloatMesh(int[] dimensions) {
        super(Float.class, dimensions);
    }

    public FloatMesh() {
        super(Float.class);
    }

    public FloatMesh(Object data) {
        this();
        setData(data);
    }

    
    @Override
    protected Float getScaled(Float value, double factor) {
        return (float) factor * value;
    }

  
    @Override
    protected Float getSum(Number a, Number b) {
        return a.floatValue() + b.floatValue();
    }

    @Override
    protected Float getDifference(Number a, Number b) {
        return a.floatValue() - b.floatValue();
    }

    @Override
    protected Float zeroValue() {
        return 0.0F;
    }

    @Override
    protected Float baseLineElementAt(Object simpleArray, int index) {
        return ((float[]) simpleArray)[index];
    }

    @Override
    protected void setBaseLineElementAt(Object simpleArray, int index, Float value) {
        ((float[]) simpleArray)[index] = value;
    }

    @Override
    public Float parseElement(String text) throws Exception {
        return Float.parseFloat(text);
    }

    @Override
    public Mesh<Float> subArrayAt(int[] index) {
        return new FloatMesh(subarrayDataAt(index));
    }

}
