/*******************************************************************************
 * Copyright (c) 2016 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
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

package jnum.data.fitting;

// TODO: Auto-generated Javadoc
/**
 * A class for formulating minimization constraints. Constraints are enforced via a penalty function, which increases
 * as the parameters stray from the constraining condition. The penalty ought to be 0.0 when the constraint is satisfied
 * and yield increasingly positive values (e.g. quadratically) the farther one strays from the strict condition of the
 * constraint. 
 */
public abstract class Constraint implements Penalty {
    
    /** An human-readable identification of the constraint. */
    public String id;
    
    /**
     * Instantiates a new constraint with the specified identifier.
     *
     * @param id the human-readable identifier.
     */
    public Constraint(String id) { this.id = id; }
    
    /* (non-Javadoc)
     * @see jnum.data.fitting.Penalty#penalty()
     */
    @Override
    public abstract double penalty();
    
}
