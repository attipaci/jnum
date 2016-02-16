/*******************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
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
package jnum.data;

import java.io.Serializable;

// TODO: Auto-generated Javadoc
/**
 * The Class Bounds.
 */
public class IndexBounds2D implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -602716444281795130L;
	
	/** The toj. */
	public int fromi, toi, fromj, toj;
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return super.hashCode() ^ fromi ^ toi ^ fromj ^ toj;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if(o == this) return true;
		if(!(o instanceof IndexBounds2D)) return false;
		if(!super.equals(o)) return false;
		final IndexBounds2D b = (IndexBounds2D) o;
		if(b.fromi != fromi) return false;
		if(b.fromj != fromj) return false;
		if(b.toi != toi) return false;
		if(b.toj != toj) return false;
		return true;
	}
}
