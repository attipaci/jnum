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

package jnum.reporting;

public abstract class Reporter {
    private String id;
    
    public Reporter(String id) {
        this.id = id;
    }

    public String getID() { return id; }
    
    
    public abstract void info(Object owner, String message);
   
    public abstract void notify(Object owner, String message);
    
    public abstract void debug(Object owner, String message);
    
    public abstract void warning(Object owner, String message);
    
    public abstract void error(Object owner, String message);
    
    public abstract void trace(Throwable e);
    
    
    public void error(Object owner, Throwable e, boolean debug) {
        error(owner, e.getMessage());
        if(debug) trace(e);
    }
    
    public void error(Object owner, Throwable e) {
        error(owner, e, true);
    }
    
    public void warning(Object owner, Exception e, boolean debug) {
        warning(owner, e.getMessage());
        if(debug) trace(e);
    }
    
    public void warning(Object owner, Exception e) {
        warning(owner, e, false);
    }
    
    public abstract void status(Object owner, String message);
    
    public abstract void result(Object owner, String message);
    
    
    public abstract void detail(Object owner, String message);
    
    public abstract void values(Object owner, String message); 
    
    public abstract void suggest(Object owner, String message);
    
    
   
    
    
    
    
}
