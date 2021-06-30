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

package jnum.data.cube;

import java.io.Serializable;


public class IndexBounds3D implements Serializable {
	/**
     * 
     */
    private static final long serialVersionUID = -6394727763241915314L;
    
    public int fromi, toi, fromj, toj, fromk, tok;
	
	public IndexBounds3D() {}
	
	public IndexBounds3D(int fromi, int fromj, int fromk, int toi, int toj, int tok) {
	    this();
	    set(fromi, fromj, fromk, toi, toj, tok);
	}
	

    public IndexBounds3D(double fromi, double fromj, double fromk, double toi, double toj, double tok) {
        this();
        set(fromi, fromj, fromk, toi, toj, tok);
    }
	
	@Override
	public int hashCode() {
		return super.hashCode() ^ fromi ^ toi ^ fromj ^ toj ^ fromk ^ tok;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == this) return true;
		if(!(o instanceof IndexBounds3D)) return false;
		
		final IndexBounds3D b = (IndexBounds3D) o;
		if(b.fromi != fromi) return false;
		if(b.fromj != fromj) return false;
		if(b.fromk != fromk) return false;
		if(b.toi != toi) return false;
		if(b.toj != toj) return false;
		if(b.tok != tok) return false;
		return true;
	}
	
	public void set(double fromi, double fromj, double fromk, double toi, double toj, double tok) {
	    set((int)Math.floor(fromi), (int)Math.floor(fromj), (int)Math.floor(fromk),
	            (int)Math.ceil(toi), (int)Math.ceil(toj), (int)Math.ceil(tok)
	    );
	}
	
	public void set(int fromi, int fromj, int fromk, int toi, int toj, int tok) {
	    this.fromi = fromi;
	    this.fromj = fromj;
	    this.fromk = fromk;
	    this.toi = toi;
	    this.toj = toj;
	    this.tok = tok;
	}
	
	@Override
    public String toString() { return fromi + "," + fromj + "," + fromk + " -> " + toi + "," + toj + "," + tok; }

}
