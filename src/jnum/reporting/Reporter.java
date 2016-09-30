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

// TODO: Auto-generated Javadoc
/**
 * The Class Reporter.
 */
public abstract class Reporter {
    
    /** The id. */
    private String id;
    
    /**
     * Instantiates a new reporter.
     *
     * @param id the id
     */
    public Reporter(String id) {
        this.id = id;
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public String getID() { return id; }
    
    
    /**
     * Info.
     *
     * @param owner the owner
     * @param message the message
     */
    public abstract void info(Object owner, String message);
   
    /**
     * Notify.
     *
     * @param owner the owner
     * @param message the message
     */
    public abstract void notify(Object owner, String message);
    
    /**
     * Debug.
     *
     * @param owner the owner
     * @param message the message
     */
    public abstract void debug(Object owner, String message);
    
    /**
     * Warning.
     *
     * @param owner the owner
     * @param message the message
     */
    public abstract void warning(Object owner, String message);
    
    /**
     * Error.
     *
     * @param owner the owner
     * @param message the message
     */
    public abstract void error(Object owner, String message);
    
    /**
     * Trace.
     *
     * @param e the e
     */
    public abstract void trace(Throwable e);
    
    
    /**
     * Error.
     *
     * @param owner the owner
     * @param e the e
     * @param debug the debug
     */
    public void error(Object owner, Throwable e, boolean debug) {
        error(owner, e.getMessage());
        if(debug) trace(e);
    }
    
    /**
     * Error.
     *
     * @param owner the owner
     * @param e the e
     */
    public void error(Object owner, Throwable e) {
        error(owner, e, true);
    }
    
    /**
     * Warning.
     *
     * @param owner the owner
     * @param e the e
     * @param debug the debug
     */
    public void warning(Object owner, Exception e, boolean debug) {
        warning(owner, e.getMessage());
        if(debug) trace(e);
    }
    
    /**
     * Warning.
     *
     * @param owner the owner
     * @param e the e
     */
    public void warning(Object owner, Exception e) {
        warning(owner, e, false);
    }
    
    /**
     * Status.
     *
     * @param owner the owner
     * @param message the message
     */
    public abstract void status(Object owner, String message);
    
    /**
     * Result.
     *
     * @param owner the owner
     * @param message the message
     */
    public abstract void result(Object owner, String message);
    
    
    /**
     * Detail.
     *
     * @param owner the owner
     * @param message the message
     */
    public abstract void detail(Object owner, String message);
    
    /**
     * Values.
     *
     * @param owner the owner
     * @param message the message
     */
    public abstract void values(Object owner, String message); 
    
    /**
     * Suggest.
     *
     * @param owner the owner
     * @param message the message
     */
    public abstract void suggest(Object owner, String message);
    
}
