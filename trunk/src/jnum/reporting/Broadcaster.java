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

import java.util.Hashtable;

public class Broadcaster extends Reporter {
    
    private Hashtable<String, Reporter> reporters = new Hashtable<String, Reporter>();

    public Broadcaster(String id) {
        super(id);
    }

    public Broadcaster(String id, Reporter r) {
        this(id);
        add(r);
    }
    
    public void add(Reporter r) {
        reporters.put(r.getID(), r);
    }
    
    public boolean contains(Reporter r) { return reporters.contains(r); }
    
    public boolean contains(String id) { return reporters.containsKey(id); }
    
    public Reporter remove(Reporter r) { return remove(r.getID()); }
    
    public Reporter remove(String id) { return reporters.remove(id); }
    
    @Override
    public void info(Object owner, String message) {
        for(Reporter r : reporters.values()) r.info(owner, message);
    }

    @Override
    public void notify(Object owner, String message) {
        for(Reporter r : reporters.values()) r.notify(owner, message);
    }
    
    @Override
    public void debug(Object owner, String message) {
        for(Reporter r : reporters.values()) r.debug(owner, message);
    }
    
    @Override
    public void warning(Object owner, String message) {
        for(Reporter r : reporters.values()) r.warning(owner, message);
        
    }

    @Override
    public void error(Object owner, String message) {
        for(Reporter r : reporters.values()) r.error(owner, message);
    }

    @Override
    public void trace(Throwable e) {
        for(Reporter r : reporters.values()) r.trace(e);
    }

    @Override
    public void status(Object owner, String message) {
        for(Reporter r : reporters.values()) r.status(owner, message);
    }

    @Override
    public void result(Object owner, String message) {
        for(Reporter r : reporters.values()) r.result(owner, message);
    }

    @Override
    public void detail(Object owner, String message) {
        for(Reporter r : reporters.values()) r.detail(owner, message);
    }

    @Override
    public void values(Object owner, String message) {
        for(Reporter r : reporters.values()) r.values(owner, message);
    }

    @Override
    public void suggest(Object owner, String message) {
        for(Reporter r : reporters.values()) r.suggest(owner, message);
    }
    
    
    
  
}
