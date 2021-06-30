/*******************************************************************************
 * Copyright (c) 2018 Attila Kovacs <attila[AT]sigmyne.com>.
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

package jnum.reporting;

import java.util.Hashtable;


/**
 * Broadcasters are reporters that distribute incoming messages to a list of connected reporter objects. They are
 * useful for managing message consumers which work independently, but process the same messages.
 * 
 * 
 * @author Attila Kovacs
 *
 */
public class Broadcaster extends Reporter {

    private Hashtable<String, Reporter> reporters = new Hashtable<>();

    /**
     * Instantiates a new broadcaster.
     *
     * @param id    the identifier by which this broadcaster can be referenced
     */
    public Broadcaster(String id) {
        super(id);
    }

    /**
     * Instantiates a new broadcaster, and an initial reporter connected to it.
     *
     * @param id    the identifier by which this broadcaster can be referenced
     * @param r     the initial reporter to which incoming messages will be sent to.     
     */
    public Broadcaster(String id, Reporter r) {
        this(id);
        add(r);
    }
    
    /**
     * Adds the specified reporter to the broadcast.
     *
     * @param r     the reporter that is to be added to the broadcast.
     * @return      The prior reporter with the same ID as the newly added one, or <code>null</code> if 
     *              there was no prior reporter by the same ID.
     */
    public synchronized Reporter add(Reporter r) {
        return reporters.put(r.getID(), r);
    }
    
    /**
     * Remove all reporters from the broadcast... 
     */
    public void clear() {
        reporters.clear();
    }
    
    /**
     * Check if a given Reporter object is already part of this broadcast.
     *
     * @param r     the reporter to be checked for.
     * @return      <code>true</code>, if the specified Reporter is already included.
     */
    public boolean contains(Reporter r) { return reporters.contains(r); }
    
    /**
     * Check if a Reporter with the given String ID is already part of this broadcast.
     *
     * @param id the id of the Reporter to check
     * @return true, if a reporter with the specified ID is already included.
     */
    public boolean contains(String id) { return reporters.containsKey(id); }
    
    /**
     * Removes the specified reporter from this broadcast.
     *
     * @param r     the reporter object to remove from the message broadcast.
     * @return the reporter
     */
    public synchronized Reporter remove(Reporter r) { return remove(r.getID()); }
    
    /**
     * Removes the reporter with the specified ID from the broadcast.
     *
     * @param id    the ID of the reporter to remove from the broadcast
     * @return the  The reporter that was removed from the broadcast, or <code>null</code> if no 
     *              reporter with the given ID was part of the broadcast. 
     */
    public synchronized Reporter remove(String id) { return reporters.remove(id); }

    @Override
    public synchronized void info(Object owner, String message) {
        for(Reporter r : reporters.values()) r.info(owner, message);
    }

    @Override
    public synchronized void notify(Object owner, String message) {
        for(Reporter r : reporters.values()) r.notify(owner, message);
    }

    @Override
    public synchronized void debug(Object owner, String message) {
        for(Reporter r : reporters.values()) r.debug(owner, message);
    }

    @Override
    public synchronized void warning(Object owner, String message) {
        for(Reporter r : reporters.values()) r.warning(owner, message);     
    }

    @Override
    public synchronized void error(Object owner, String message) {
        for(Reporter r : reporters.values()) r.error(owner, message);
    }

    @Override
    public synchronized void trace(Throwable e) {
        for(Reporter r : reporters.values()) r.trace(e);
    }

    @Override
    public synchronized void status(Object owner, String message) {
        for(Reporter r : reporters.values()) r.status(owner, message);
    }

    @Override
    public synchronized void result(Object owner, String message) {
        for(Reporter r : reporters.values()) r.result(owner, message);
    }

    @Override
    public synchronized void detail(Object owner, String message) {
        for(Reporter r : reporters.values()) r.detail(owner, message);
    }

    @Override
    public synchronized void values(Object owner, String message) {
        for(Reporter r : reporters.values()) r.values(owner, message);
    }

    @Override
    public synchronized void suggest(Object owner, String message) {
        for(Reporter r : reporters.values()) r.suggest(owner, message);
    }
      
}
