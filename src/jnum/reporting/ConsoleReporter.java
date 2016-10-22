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

import jnum.text.TextWrapper;

// TODO: Auto-generated Javadoc
/**
 * The Class ConsoleReporter.
 */
public class ConsoleReporter extends Reporter {
    
    /** The wrapper. */
    private TextWrapper wrapper = new TextWrapper(79, "({[", ")}]");
    
    /** The level. */
    private int level = LEVEL_DETAIL;
    
    
    /**
     * Instantiates a new console reporter.
     *
     * @param id the id
     */
    public ConsoleReporter(String id) {
        super(id);
        // TODO Auto-generated constructor stub
    }

    /**
     * Adds the line.
     */
    public void addLine() { System.err.println(); }
    
    /**
     * Gets the prefix.
     *
     * @param owner the owner
     * @return the prefix
     */
    public String getPrefix(Object owner) { return " "; }
       
    /**
     * Gets the result prefix.
     *
     * @param owner the owner
     * @return the result prefix
     */
    public String getResultPrefix(Object owner) { return " "; }
 
    
    /**
     * Gets the object ID.
     *
     * @param owner the owner
     * @return the object ID
     */
    public String getObjectID(Object owner) {
        return owner.getClass().getSimpleName();
    }
    
    /**
     * Gets the wrap.
     *
     * @return the wrap
     */
    public int getWrap() { return wrapper.getWidth(); }
    
    /**
     * Sets the wrap.
     *
     * @param chars the new wrap
     */
    public void setWrap(int chars) { wrapper.setWidth(chars); }
    
    /**
     * Sets the level.
     *
     * @param level the new level
     */
    public void setLevel(int level) { this.level = level; }
    
    /**
     * Gets the level.
     *
     * @return the level
     */
    public int getLevel() { return level; } 
    
    /* (non-Javadoc)
     * @see jnum.reporting.Reporter#info(java.lang.Object, java.lang.String)
     */
    @Override
    public void info(Object owner, String message) { 
        if(level >= LEVEL_INFO) System.err.println(wrapper.wrap(message, getPrefix(owner), getIndentInfo()));
    }
    
    /* (non-Javadoc)
     * @see jnum.reporting.Reporter#notify(java.lang.Object, java.lang.String)
     */
    @Override
    public void notify(Object owner, String message) { 
        if(level >= LEVEL_NOTIFY) System.err.println(wrapper.wrap(message, " ", getIndentNotify()));
    }

    /* (non-Javadoc)
     * @see jnum.reporting.Reporter#debug(java.lang.Object, java.lang.String)
     */
    @Override
    public void debug(Object owner, String message) { 
        if(level >= LEVEL_DEBUG) System.err.println(wrapper.wrap(message, "DEBUG> ", getIndentDebug()));
    }
    
    /* (non-Javadoc)
     * @see jnum.reporting.Reporter#warning(java.lang.Object, java.lang.String)
     */
    @Override
    public void warning(Object owner, String message) {
        if(level >= LEVEL_WARNING) System.err.println(wrapper.wrap("WARNING! " + message, getPrefix(owner), getIndentWarning()));
    }

    /* (non-Javadoc)
     * @see jnum.reporting.Reporter#error(java.lang.Object, java.lang.String)
     */
    @Override
    public void error(Object owner, String message) {
        if(level >= LEVEL_ERROR) System.err.println(wrapper.wrap("ERROR! " + message, getPrefix(owner), getIndentErrors()));
    }

    /* (non-Javadoc)
     * @see jnum.reporting.Reporter#status(java.lang.Object, java.lang.String)
     */
    @Override
    public void status(Object owner, String message) {
        if(level >= LEVEL_STATUS) System.err.println(wrapper.wrap(getObjectID(owner) + "> " + message, getIndentStatus()));
    }

    /* (non-Javadoc)
     * @see jnum.reporting.Reporter#result(java.lang.Object, java.lang.String)
     */
    @Override
    public void result(Object owner, String message) {
        if(level >= LEVEL_RESULT) System.out.println(wrapper.wrap("\n" + message + "\n", getResultPrefix(owner), getIndentResult()));
        //System.out.println("\n" + message + "\n");
    }

    /* (non-Javadoc)
     * @see jnum.reporting.Reporter#detail(java.lang.Object, java.lang.String)
     */
    @Override
    public void detail(Object owner, String message) {
        if(level >= LEVEL_DETAIL) System.err.println(wrapper.wrap("..." + message, getPrefix(owner), getIndentDetail()));
    }
    
    /* (non-Javadoc)
     * @see jnum.reporting.Reporter#values(java.lang.Object, java.lang.String)
     */
    @Override
    public void values(Object owner, String message) {
        if(level >= LEVEL_VALUES) System.err.println(wrapper.wrap(message, getPrefix(owner), getIndentValues())); 
    }

    /* (non-Javadoc)
     * @see jnum.reporting.Reporter#suggest(java.lang.Object, java.lang.String)
     */
    @Override
    public void suggest(Object owner, String message) {
        if(level >= LEVEL_SUGGEST) System.err.println("\n" + wrapper.wrap(message, getPrefix(owner), getIndentSuggest())); 
    }

    /* (non-Javadoc)
     * @see jnum.reporting.Reporter#trace(java.lang.Throwable)
     */
    @Override
    public void trace(Throwable e) {
        e.printStackTrace();
    }
    
    /**
     * Gets the indent info.
     *
     * @return the indent info
     */
    public int getIndentInfo() { return 3; }
    
    /**
     * Gets the indent notify.
     *
     * @return the indent notify
     */
    public int getIndentNotify() { return 3; }
    
    /**
     * Gets the indent debug.
     *
     * @return the indent debug
     */
    public int getIndentDebug() { return 3; }
    
    /**
     * Gets the indent warning.
     *
     * @return the indent warning
     */
    public int getIndentWarning() { return 3; }
    
    /**
     * Gets the indent errors.
     *
     * @return the indent errors
     */
    public int getIndentErrors() { return 3; }
 
    /**
     * Gets the indent status.
     *
     * @return the indent status
     */
    public int getIndentStatus() { return 3; }
    
    /**
     * Gets the indent result.
     *
     * @return the indent result
     */
    public int getIndentResult() { return 0; }
    
    /**
     * Gets the indent suggest.
     *
     * @return the indent suggest
     */
    public int getIndentSuggest() { return 3; }
    
    /**
     * Gets the indent detail.
     *
     * @return the indent detail
     */
    public int getIndentDetail() { return 3; }
   
    /**
     * Gets the indent values.
     *
     * @return the indent values
     */
    public int getIndentValues() { return 3; }
     
     
    /** The Constant LEVEL_NONE. */
    public final static int LEVEL_NONE = -1;
    
    /** The Constant LEVEL_ERROR. */
    public final static int LEVEL_ERROR = 0;
    
    /** The Constant LEVEL_WARNING. */
    public final static int LEVEL_WARNING = 1;
    
    /** The Constant LEVEL_NOTIFY. */
    public final static int LEVEL_NOTIFY = 2;
    
    /** The Constant LEVEL_STATUS. */
    public final static int LEVEL_STATUS = 3;
    
    /** The Constant LEVEL_SUGGEST. */
    public final static int LEVEL_SUGGEST = 4;
    
    /** The Constant LEVEL_RESULT. */
    public final static int LEVEL_RESULT = 5;
    
    /** The Constant LEVEL_VALUES. */
    public final static int LEVEL_VALUES = 6;
    
    /** The Constant LEVEL_INFO. */
    public final static int LEVEL_INFO = 7;
    
    /** The Constant LEVEL_DETAIL. */
    public final static int LEVEL_DETAIL = 8;
    
    /** The Constant LEVEL_DEBUG. */
    public final static int LEVEL_DEBUG = 9;
}
