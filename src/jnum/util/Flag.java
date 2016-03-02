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

package jnum.util;

import java.io.Serializable;

public class Flag implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 6666953592077360485L;
    private FlagSpace space;
    private int value;
    private char letterCode;
    private String name;
    
    protected Flag(FlagSpace space, int value, char letterCode, String name) throws FlagConflictException {
        this.space = space;
        this.value = value;
        this.letterCode = letterCode;
        this.name = name;
        
        space.put(this);
    }
    
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof Flag)) return false;
        if(!super.equals(o)) return false;
        
        Flag f = (Flag) o;
        if(f.value != value) return false;
        if(f.letterCode != letterCode) return false;
        return true;
    }
    
    @Override
    public int hashCode() { return super.hashCode() ^ HashCode.get(value) ^ HashCode.get(letterCode); }
    
    public final FlagSpace getFlagSpace() { return space; }
    
    public final int value() { return value; }
    
    public final char letterCode() { return letterCode; }
    
    public final String name() { return name; }
    
    @Override
    public String toString() {
        return "'" + letterCode + "' - 0x" + Integer.toHexString(value) + " - " + name;
    }

   
    
}
