/*******************************************************************************
 * Copyright (c) 2016 Attila Kovacs <attila[AT]sigmyne.com>.
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

package jnum.devel.symbolic;

import jnum.Util;

public class ScriptVariable implements Variable {
    private String id;
    private String literal;
    
    private ScriptVariable(String id) {
        this.id = id;
    }
    
    public ScriptVariable(String id, boolean value) {
        this(id);
        setValue(value);
    }
    
    public ScriptVariable(String id, long value) {
        this(id);
        setValue(value);
    }
    
    public ScriptVariable(String id, double value) {
        this(id);
        setValue(value);
    }
    
    
    public String getID() { return id; }
    
    @Override
    public void setValue(boolean value) { literal = Boolean.toString(value); }
    
    @Override
    public void setValue(long value) { literal = (value == (int)value) ? Integer.toString((int)value) : Long.toString(value); }
    
    @Override
    public void setValue(double value) { literal = Double.toString(value); }
    
    @Override
    public boolean asBoolean() { return Util.parseBoolean(literal); }
    
    @Override
    public long asLong() { return Long.decode(literal); }
    
    @Override
    public double asDouble() { return Double.parseDouble(literal); }
}
