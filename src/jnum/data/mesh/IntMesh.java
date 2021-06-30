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

public class IntMesh extends NumberMesh.IntegerType<Integer> {

    /**
     * 
     */
    private static final long serialVersionUID = 3405647344446844891L;
    

    public IntMesh(int[] dimensions) {
        super(int.class, dimensions);
    }


    public IntMesh() {
        super(int.class);
    }

    public IntMesh(Object data) {
        this();
        setData(data);
    }   
     
    @Override
    public final Integer cast(Number x) { return x.intValue(); }

    @Override
    protected final Integer zeroValue() {
        return 0;
    }

    @Override
    protected final Integer linearElementAt(Object simpleArray, int index) {
        return ((int[]) simpleArray)[index];
    }

    @Override
    protected final void setLinearElementAt(Object simpleArray, int index, Integer value) {
        ((int[]) simpleArray)[index] = value;
    }

    @Override
    public Integer parseElement(String text) throws Exception {
        return Integer.decode(text);
    }

    @Override
    public Mesh<Integer> newInstance() {
        return new IntMesh();
    }

    @Override
    public final Integer getSumOf(final Number a, final Number b) {
        return a.intValue() + b.intValue();
    }
    
    @Override
    public final Integer getDifferenceOf(final Number a, final Number b) {
        return a.intValue() - b.intValue();
    }
    
  
}
