/*******************************************************************************
 * Copyright (c) 2017 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
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
 *     Attila Kovacs <attila_kovacs[AT]post.harvard.edu> - initial API and implementation
 ******************************************************************************/

package jnum.data.mesh;

public class IntMesh extends NumberMesh<Integer> {

    /**
     * 
     */
    private static final long serialVersionUID = 3405647344446844891L;
    
    /**
     * Instantiates a new double mesh.
     *
     * @param dimensions the dimensions
     */
    public IntMesh(int[] dimensions) {
        super(int.class, dimensions);
    }

    /**
     * Instantiates a new double mesh.
     */
    public IntMesh() {
        super(int.class);
    }

    /**
     * Instantiates a new double mesh.
     *
     * @param data the data
     */
    public IntMesh(Object data) {
        this();
        setData(data);
    }   
     
    @Override
    public final Integer convert(Number x) { return x.intValue(); }

    /* (non-Javadoc)
     * @see jnum.data.mesh.PrimitiveMesh#zeroValue()
     */
    @Override
    protected final Integer zeroValue() {
        return 0;
    }

    /* (non-Javadoc)
     * @see jnum.data.mesh.PrimitiveMesh#baseLineElementAt(java.lang.Object, int)
     */
    @Override
    protected final Integer linearElementAt(Object simpleArray, int index) {
        return ((int[]) simpleArray)[index];
    }

    /* (non-Javadoc)
     * @see jnum.data.mesh.PrimitiveMesh#setBaseLineElementAt(java.lang.Object, int, java.lang.Number)
     */
    @Override
    protected final void setLinearElementAt(Object simpleArray, int index, Integer value) {
        ((int[]) simpleArray)[index] = value;
    }

    /* (non-Javadoc)
     * @see jnum.data.mesh.Mesh#parseElement(java.lang.String)
     */
    @Override
    public Integer parseElement(String text) throws Exception {
        return Integer.decode(text);
    }

    /* (non-Javadoc)
     * @see jnum.data.mesh.Mesh#newInstance()
     */
    @Override
    public Mesh<Integer> newInstance() {
        return new IntMesh();
    }

    @Override
    public final Integer getSumOf(Number a, Number b) {
        return a.intValue() + b.intValue();
    }
    
    @Override
    public final Integer getDifferenceOf(Number a, Number b) {
        return a.intValue() - b.intValue();
    }
    
    @Override
    public final Integer getProductOf(Number a, Number b) {
        return a.intValue() * b.intValue();
    }
    
    @Override
    public final Integer getRatioOf(Number a, Number b) {
        return a.intValue() / b.intValue();
    }
    

}
