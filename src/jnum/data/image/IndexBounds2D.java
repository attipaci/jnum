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

package jnum.data.image;

import java.io.Serializable;


public class IndexBounds2D implements Serializable {

	private static final long serialVersionUID = -602716444281795130L;
	
	public int fromi, toi, fromj, toj;
	
	public IndexBounds2D() {}
	
	public IndexBounds2D(int fromi, int fromj, int toi, int toj) {
	    this();
	    set(fromi, fromj, toi, toj);
	}
	

    public IndexBounds2D(double fromi, double fromj, double toi, double toj) {
        this();
        set(fromi, fromj, toi, toj);
    }
	
	@Override
	public int hashCode() {
		return super.hashCode() ^ fromi ^ toi ^ fromj ^ toj;
	}

	@Override
	public boolean equals(Object o) {
		if(o == this) return true;
		if(!(o instanceof IndexBounds2D)) return false;
		
		final IndexBounds2D b = (IndexBounds2D) o;
		if(b.fromi != fromi) return false;
		if(b.fromj != fromj) return false;
		if(b.toi != toi) return false;
		if(b.toj != toj) return false;
		return true;
	}
	
	public void set(double fromi, double fromj, double toi, double toj) {
	    set((int)Math.floor(fromi), (int)Math.floor(fromj), (int)Math.ceil(toi), (int)Math.ceil(toj));
	}
	
	public void set(int fromi, int fromj, int toi, int toj) {
	    this.fromi = fromi;
	    this.fromj = fromj;
	    this.toi = toi;
	    this.toj = toj;
	}
	
	@Override
    public String toString() { return fromi + "," + fromj + " -> " + toi + "," + toj; }

}
