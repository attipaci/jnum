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

public class ShortMesh extends NumberMesh.IntegerType<Short> {

    /**
     * 
     */
    private static final long serialVersionUID = -8830391634606131234L;


    public ShortMesh(int[] dimensions) {
        super(short.class, dimensions);
    }

    
    public ShortMesh() {
        super(short.class);
    }


    public ShortMesh(Object data) {
        this();
        setData(data);
    }   

    @Override
    public final Short cast(Number x) { return x.shortValue(); }
    
    /* (non-Javadoc)
     * @see jnum.data.mesh.PrimitiveMesh#zeroValue()
     */
    @Override
    protected final Short zeroValue() {
        return (short) 0;
    }

    /* (non-Javadoc)
     * @see jnum.data.mesh.PrimitiveMesh#baseLineElementAt(java.lang.Object, int)
     */
    @Override
    protected final Short linearElementAt(Object simpleArray, int index) {
        return ((short[]) simpleArray)[index];
    }

    /* (non-Javadoc)
     * @see jnum.data.mesh.PrimitiveMesh#setBaseLineElementAt(java.lang.Object, int, java.lang.Number)
     */
    @Override
    protected final void setLinearElementAt(Object simpleArray, int index, Short value) {
        ((int[]) simpleArray)[index] = value;
    }

    /* (non-Javadoc)
     * @see jnum.data.mesh.Mesh#parseElement(java.lang.String)
     */
    @Override
    public Short parseElement(String text) throws Exception {
        return Short.decode(text);
    }

    /* (non-Javadoc)
     * @see jnum.data.mesh.Mesh#newInstance()
     */
    @Override
    public Mesh<Short> newInstance() {
        return new ShortMesh();
    }
    
    @Override
    public final Short getSumOf(final Number a, final Number b) {
        return (short) (a.intValue() + b.intValue());
    }
    
    @Override
    public final Short getDifferenceOf(final Number a, final Number b) {
        return (short) (a.intValue() - b.intValue());
    }

}
