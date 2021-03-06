/*******************************************************************************
 * Copyright (c) 2017 Attila Kovacs <attila[AT]sigmyne.com>.
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

package jnum.data.mesh;

import java.lang.Float;



public class FloatMesh extends NumberMesh.FloatingType<Float> {

    private static final long serialVersionUID = -4081004880332314702L;


    public FloatMesh(int[] dimensions) {
        super(float.class, dimensions);
    }


    public FloatMesh() {
        super(float.class);
    }


    public FloatMesh(Object data) {
        this();
        setData(data);
    }
    
    @Override
    public final Float cast(Number x) { return x.floatValue(); }

    /* (non-Javadoc)
     * @see jnum.data.mesh.PrimitiveMesh#zeroValue()
     */
    @Override
    protected final Float zeroValue() {
        return 0.0F;
    }

    /* (non-Javadoc)
     * @see jnum.data.mesh.PrimitiveMesh#baseLineElementAt(java.lang.Object, int)
     */
    @Override
    protected Float linearElementAt(Object simpleArray, int index) {
        return ((float[]) simpleArray)[index];
    }

    /* (non-Javadoc)
     * @see jnum.data.mesh.PrimitiveMesh#setBaseLineElementAt(java.lang.Object, int, java.lang.Number)
     */
    @Override
    protected final void setLinearElementAt(Object simpleArray, int index, Float value) {
        ((float[]) simpleArray)[index] = value;
    }

    /* (non-Javadoc)
     * @see jnum.data.mesh.Mesh#parseElement(java.lang.String)
     */
    @Override
    public Float parseElement(String text) throws Exception {
        return Float.parseFloat(text);
    }

    /* (non-Javadoc)
     * @see jnum.data.mesh.Mesh#subarrayAt(int[])
     */
    @Override
    public Mesh<Float> subarrayAt(int[] index) {
        return new FloatMesh(subarrayDataAt(index));
    }

    /* (non-Javadoc)
     * @see jnum.data.mesh.Mesh#newInstance()
     */
    @Override
    public Mesh<Float> newInstance() {
        return new FloatMesh();
    }
    
    @Override
    public final Float getSumOf(final Number a, final Number b) {
        return a.floatValue() + b.floatValue();
    }
    
    @Override
    public final Float getDifferenceOf(final Number a, final Number b) {
        return a.floatValue() - b.floatValue();
    }
    
   
}
