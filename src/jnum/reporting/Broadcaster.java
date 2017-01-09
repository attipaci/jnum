/*******************************************************************************
 * Copyright (c) 2016 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
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

package jnum.reporting;

import java.util.Hashtable;

// TODO: Auto-generated Javadoc
/**
 * The Class Broadcaster.
 */
public class Broadcaster extends Reporter {
    
    /** The reporters. */
    private Hashtable<String, Reporter> reporters = new Hashtable<String, Reporter>();

    /**
     * Instantiates a new broadcaster.
     *
     * @param id the id
     */
    public Broadcaster(String id) {
        super(id);
    }

    /**
     * Instantiates a new broadcaster.
     *
     * @param id the id
     * @param r the r
     */
    public Broadcaster(String id, Reporter r) {
        this(id);
        add(r);
    }
    
    /**
     * Adds the.
     *
     * @param r the r
     */
    public synchronized void add(Reporter r) {
        reporters.put(r.getID(), r);
    }
    
    /**
     * Contains.
     *
     * @param r the r
     * @return true, if successful
     */
    public boolean contains(Reporter r) { return reporters.contains(r); }
    
    /**
     * Contains.
     *
     * @param id the id
     * @return true, if successful
     */
    public boolean contains(String id) { return reporters.containsKey(id); }
    
    /**
     * Removes the.
     *
     * @param r the r
     * @return the reporter
     */
    public synchronized Reporter remove(Reporter r) { return remove(r.getID()); }
    
    /**
     * Removes the.
     *
     * @param id the id
     * @return the reporter
     */
    public synchronized Reporter remove(String id) { return reporters.remove(id); }
    
    /* (non-Javadoc)
     * @see jnum.reporting.Reporter#info(java.lang.Object, java.lang.String)
     */
    @Override
    public synchronized void info(Object owner, String message) {
        for(Reporter r : reporters.values()) r.info(owner, message);
    }

    /* (non-Javadoc)
     * @see jnum.reporting.Reporter#notify(java.lang.Object, java.lang.String)
     */
    @Override
    public synchronized void notify(Object owner, String message) {
        for(Reporter r : reporters.values()) r.notify(owner, message);
    }
    
    /* (non-Javadoc)
     * @see jnum.reporting.Reporter#debug(java.lang.Object, java.lang.String)
     */
    @Override
    public synchronized void debug(Object owner, String message) {
        for(Reporter r : reporters.values()) r.debug(owner, message);
    }
    
    /* (non-Javadoc)
     * @see jnum.reporting.Reporter#warning(java.lang.Object, java.lang.String)
     */
    @Override
    public synchronized void warning(Object owner, String message) {
        for(Reporter r : reporters.values()) r.warning(owner, message);
        
    }

    /* (non-Javadoc)
     * @see jnum.reporting.Reporter#error(java.lang.Object, java.lang.String)
     */
    @Override
    public synchronized void error(Object owner, String message) {
        for(Reporter r : reporters.values()) r.error(owner, message);
    }

    /* (non-Javadoc)
     * @see jnum.reporting.Reporter#trace(java.lang.Throwable)
     */
    @Override
    public synchronized void trace(Throwable e) {
        for(Reporter r : reporters.values()) r.trace(e);
    }

    /* (non-Javadoc)
     * @see jnum.reporting.Reporter#status(java.lang.Object, java.lang.String)
     */
    @Override
    public synchronized void status(Object owner, String message) {
        for(Reporter r : reporters.values()) r.status(owner, message);
    }

    /* (non-Javadoc)
     * @see jnum.reporting.Reporter#result(java.lang.Object, java.lang.String)
     */
    @Override
    public synchronized void result(Object owner, String message) {
        for(Reporter r : reporters.values()) r.result(owner, message);
    }

    /* (non-Javadoc)
     * @see jnum.reporting.Reporter#detail(java.lang.Object, java.lang.String)
     */
    @Override
    public synchronized void detail(Object owner, String message) {
        for(Reporter r : reporters.values()) r.detail(owner, message);
    }

    /* (non-Javadoc)
     * @see jnum.reporting.Reporter#values(java.lang.Object, java.lang.String)
     */
    @Override
    public synchronized void values(Object owner, String message) {
        for(Reporter r : reporters.values()) r.values(owner, message);
    }

    /* (non-Javadoc)
     * @see jnum.reporting.Reporter#suggest(java.lang.Object, java.lang.String)
     */
    @Override
    public synchronized void suggest(Object owner, String message) {
        for(Reporter r : reporters.values()) r.suggest(owner, message);
    }
    
    
    
  
}
