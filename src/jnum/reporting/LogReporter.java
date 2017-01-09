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

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class LogReporter.
 */
public class LogReporter extends Reporter {
    
    /** The logger. */
    private Logger logger;
    
    /** The is type labeled. */
    private boolean isTypeLabeled = true;
    
    /** The is object labeled. */
    private boolean isObjectLabeled = false;
    
    /**
     * Instantiates a new log reporter.
     *
     * @param logger the logger
     */
    public LogReporter(Logger logger) {
        super(logger.getName());
        this.logger = logger;
    }

    /**
     * Checks if the logged messages are labeled by message type.
     *
     * @return true, if messages are labeled by type.
     * @see #setTypeLabeled(boolean)
     */
    public boolean isTypeLabeled() { return isTypeLabeled; }
    
    /**
     * Enables/disables message labeling by type in the log.
     *
     * @param value the new type labeling policy.
     * @see #isTypeLabeled()
     */
    public void setTypeLabeled(boolean value) { isTypeLabeled = value; }
  
    /**
     * Checks if the logged messages are labeled by the sender object.
     *
     * @return true, if messages are labeled by the sender object.
     * @see #setObjectLabeled(boolean)
     */
    public boolean isObjectLabeled() { return isObjectLabeled; }
    
    /**
     * Enables/disables message labeling by sender object in the log.
     *
     * @param value the new object labeling policy.
     * @see #isObjectLabeled
     */
    public void setObjectLabeled(boolean value) { isObjectLabeled = value; }
    
    
    /**
     * Gets the message label that is prepended before the actual message in the log. By default, the label comprises
     * of an object id (Class{@link #getSimpleName()) when labeling by object is enabled (#setObjectLabeled(boolean)),
     * and the message type, when labeling by type is enabled (#setTypeLabeled(boolean)).
     *
     * @param owner the object from which the message originated from or belongs to....
     * @param type the type descriptor of the message
     * @return the header that is constructed from the inputs based on current policy.
     * 
     * @see #setObjectLabeled(boolean)
     * @see #setTypeLabeled(boolean)
     * @see #isObjectLabeled()
     * @see #isTypeLabeled()
     */
    protected String getLabel(Object owner, String type) {
        StringBuffer header = new StringBuffer();
        if(isObjectLabeled) if(owner != null) header.append(owner.getClass().getSimpleName()); 
        if(isTypeLabeled) if(type != null) if(type.length() > 0) header.append((header.length() > 0 ? "." : "") + type);
        if(header.length() > 0) header.append("> ");
        return new String(header);
    }
    
    /* (non-Javadoc)
     * @see jnum.reporting.Reporter#info(java.lang.Object, java.lang.String)
     */
    @Override
    public void info(Object owner, String message) {
        logger.log(Level.INFO, getLabel(owner, "INFO") + message);
    }

    /* (non-Javadoc)
     * @see jnum.reporting.Reporter#notify(java.lang.Object, java.lang.String)
     */
    @Override
    public void notify(Object owner, String message) {
        logger.log(Level.INFO, getLabel(owner, "NOTE") + message);
    }

    /* (non-Javadoc)
     * @see jnum.reporting.Reporter#debug(java.lang.Object, java.lang.String)
     */
    @Override
    public void debug(Object owner, String message) {
        logger.log(Level.FINEST, getLabel(owner, "DEBUG") + message);
    }

    /* (non-Javadoc)
     * @see jnum.reporting.Reporter#warning(java.lang.Object, java.lang.String)
     */
    @Override
    public void warning(Object owner, String message) {
        logger.log(Level.WARNING, getLabel(owner, null) + message);
    }

    /* (non-Javadoc)
     * @see jnum.reporting.Reporter#error(java.lang.Object, java.lang.String)
     */
    @Override
    public void error(Object owner, String message) {
        logger.log(Level.SEVERE, getLabel(owner, null) + message);
    }

    /* (non-Javadoc)
     * @see jnum.reporting.Reporter#trace(java.lang.Throwable)
     */
    @Override
    public void trace(Throwable e) {
       String s = new String();
       
       try {
           PrintStream out = new PrintStream(s);
           e.printStackTrace(out);
           logger.log(Level.WARNING, getLabel(null, "TRACE") + s);
           out.close();
       } catch (FileNotFoundException e1) {
           logger.log(Level.WARNING, "Trace error in " + getClass().getSimpleName() + ".");
       }
      
    }

    /* (non-Javadoc)
     * @see jnum.reporting.Reporter#status(java.lang.Object, java.lang.String)
     */
    @Override
    public void status(Object owner, String message) {
        logger.log(Level.INFO, getLabel(owner, "STATUS") + message);
    }

    /* (non-Javadoc)
     * @see jnum.reporting.Reporter#result(java.lang.Object, java.lang.String)
     */
    @Override
    public void result(Object owner, String message) {
        logger.log(Level.INFO, getLabel(owner, "RESULT") + message);
    }

    /* (non-Javadoc)
     * @see jnum.reporting.Reporter#detail(java.lang.Object, java.lang.String)
     */
    @Override
    public void detail(Object owner, String message) {
        logger.log(Level.FINE, getLabel(owner, "DETAIL") + message);
    }

    /* (non-Javadoc)
     * @see jnum.reporting.Reporter#values(java.lang.Object, java.lang.String)
     */
    @Override
    public void values(Object owner, String message) {
        logger.log(Level.INFO, getLabel(owner, "VALUES") + message);
    }

    /* (non-Javadoc)
     * @see jnum.reporting.Reporter#suggest(java.lang.Object, java.lang.String)
     */
    @Override
    public void suggest(Object owner, String message) {
        logger.log(Level.FINE, getLabel(owner, "SUGGEST") + message);
    }
    
}
