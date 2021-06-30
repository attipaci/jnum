/* *****************************************************************************
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
 *     Attila Kovacs  - initial API and implementation
 ******************************************************************************/

package jnum.data.mesh;


public class LongMesh extends NumberMesh.IntegerType<Long> {
    
    /**
     * 
     */
    private static final long serialVersionUID = 7707405135422248118L;


    public LongMesh(int[] dimensions) {
        super(Long.class, dimensions);
    }


    public LongMesh() {
        super(Long.class);
    }


    public LongMesh(Object data) {
        this();
        setData(data);
    }   
    
    @Override
    public final Long cast(Number x) { return x.longValue(); }

    @Override
    protected final Long zeroValue() {
        return 0L;
    }

    @Override
    protected final Long linearElementAt(Object simpleArray, int index) {
        return ((long[]) simpleArray)[index];
    }

    @Override
    protected final void setLinearElementAt(Object simpleArray, int index, Long value) {
        ((long[]) simpleArray)[index] = value;
    }

    @Override
    public Long parseElement(String text) throws Exception {
        return Long.decode(text);
    }

    @Override
    public Mesh<Long> newInstance() {
        return new LongMesh();
    }
    
    @Override
    public final Long getSumOf(final Number a, final Number b) {
        return a.longValue() + b.longValue();
    }
    
    @Override
    public final Long getDifferenceOf(final Number a, final Number b) {
        return a.longValue() - b.longValue();
    }
    

}
