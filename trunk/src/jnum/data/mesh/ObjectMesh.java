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

import jnum.Copiable;
import jnum.text.Parser;


// TODO: Auto-generated Javadoc
/**
 * The Class ObjectMesh.
 *
 * @param <T> the generic type
 */
public class ObjectMesh<T> extends Mesh<T> {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 86938797450633242L;

	/**
	 * Instantiates a new generic array.
	 *
	 * @param type the type
	 */
	public ObjectMesh(Class<T> type) {
		super(type);
	}

	/**
	 * Instantiates a new generic array.
	 *
	 * @param type the type
	 * @param dimensions the dimensions
	 */
	public ObjectMesh(Class<T> type, int[] dimensions) {
		super(type, dimensions);
	}

	/**
	 * Instantiates a new generic array.
	 *
	 * @param data the data
	 */
	public ObjectMesh(Object data) {
		super(data);
	}
	
	  /* (non-Javadoc)
     * @see jnum.data.mesh.Mesh#newInstance()
     */
    @Override
    public Mesh<T> newInstance() {
        return new ObjectMesh<T>(elementClass);
    }
	
	/* (non-Javadoc)
	 * @see kovacs.data.AbstractArray#lineElementAt(java.lang.Object, int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected T linearElementAt(Object linearArray, int index) {
		return ((T[]) linearArray)[index];
	}

	/* (non-Javadoc)
	 * @see kovacs.data.AbstractArray#setLineElementAt(java.lang.Object, int, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void setLinearElementAt(Object linearArray, int index, T value) {
		((T[]) linearArray)[index] = value;
	}

	/* (non-Javadoc)
	 * @see kovacs.data.AbstractArray#parseElement(java.lang.String)
	 */
	@Override
	public T parseElement(String text) throws ClassCastException, InstantiationException, IllegalAccessException  {
		T value = elementClass.newInstance();
		((Parser) value).parse(text);
		return value;
	}

    @SuppressWarnings("unchecked")
    public void fill(final T x) {
        if(!(x instanceof Copiable)) throw new IllegalArgumentException("filler value must implement Copiable");
        final MeshCrawler<T> i = iterator(); 
        while(i.hasNext()) i.setNext((T) ((Copiable<T>) x).copy());
    }
    
    @SuppressWarnings("unchecked")
    public void fill(final int[] from, final int[] to, final T x) {
        if(!(x instanceof Copiable)) throw new IllegalArgumentException("filler value must implement Copiable");
        final MeshCrawler<T> i = iterator(from, to);
        while(i.hasNext()) i.setNext((T) ((Copiable<T>) x).copy());
       
    }
   
}
