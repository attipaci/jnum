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

public class ByteMesh extends NumberMesh.IntegerType<Byte> {
    /**
     * 
     */
    private static final long serialVersionUID = 6417694497366820158L;


    public ByteMesh(int[] dimensions) {
        super(byte.class, dimensions);
    }


    public ByteMesh() {
        super(byte.class);
    }


    public ByteMesh(Object data) {
        this();
        setData(data);
    }   
    
    @Override
    public final Byte cast(Number x) { return x.byteValue(); }

    @Override
    protected final Byte zeroValue() {
        return (byte) 0;
    }

    @Override
    protected final Byte linearElementAt(Object simpleArray, int index) {
        return ((byte[]) simpleArray)[index];
    }

    @Override
    protected final void setLinearElementAt(Object simpleArray, int index, Byte value) {
        ((byte[]) simpleArray)[index] = value;
    }

    @Override
    public Byte parseElement(String text) throws Exception {
        return Byte.decode(text);
    }

    @Override
    public Mesh<Byte> newInstance() {
        return new ByteMesh();
    }
    
    @Override
    public final Byte getSumOf(final Number a, final Number b) {
        return (byte) (a.intValue() + b.intValue());
    }
    
    @Override
    public final Byte getDifferenceOf(final Number a, final Number b) {
        return (byte) (a.intValue() - b.intValue());
    }
    

}
