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

import jnum.Util;

public class ConsoleReporter extends Reporter {
    private int wrap = 79;
    private int level = LEVEL_DETAIL;
    
    
    public ConsoleReporter(String id) {
        super(id);
        // TODO Auto-generated constructor stub
    }

    public void addLine() { System.err.println(); }
    
    public String getPrefix(Object owner) { return " "; }
       
    public String getResultPrefix(Object owner) { return " "; }
 
    
    public String getObjectID(Object owner) {
        return owner.getClass().getSimpleName();
    }
    
    public int getWrap() { return wrap; }
    
    public void setWrap(int chars) { wrap = chars; }
    
    public void setLevel(int level) { this.level = level; }
    
    public int getLevel() { return level; } 
    
    @Override
    public void info(Object owner, String message) { 
        if(level >= LEVEL_INFO) System.err.println(Util.wrap(message, getPrefix(owner), getWrap(), getIndentInfo()));
    }
    
    @Override
    public void notify(Object owner, String message) { 
        if(level >= LEVEL_NOTIFY) System.err.println(Util.wrap(message, " ", getWrap(), getIndentNotify()));
    }

    @Override
    public void debug(Object owner, String message) { 
        if(level >= LEVEL_DEBUG) System.err.println(Util.wrap(message, "DEBUG> ", getWrap(), getIndentDebug()));
    }
    
    @Override
    public void warning(Object owner, String message) {
        if(level >= LEVEL_WARNING) System.err.println(Util.wrap("WARNING! " + message, getPrefix(owner), getWrap(), getIndentWarning()));
    }

    @Override
    public void error(Object owner, String message) {
        if(level >= LEVEL_ERROR) System.err.println(Util.wrap("ERROR! " + message, getPrefix(owner), getWrap(), getIndentErrors()));
    }

    @Override
    public void status(Object owner, String message) {
        if(level >= LEVEL_STATUS) System.err.println(Util.wrap(getObjectID(owner) + "> " + message, getWrap(), getIndentStatus()));
    }

    @Override
    public void result(Object owner, String message) {
        if(level >= LEVEL_RESULT) System.out.println(Util.wrap("\n" + message + "\n", getResultPrefix(owner), getWrap(), getIndentResult()));
        //System.out.println("\n" + message + "\n");
    }

    @Override
    public void detail(Object owner, String message) {
        if(level >= LEVEL_DETAIL) System.err.println("..." + Util.wrap(message, getPrefix(owner), getWrap(), getIndentDetail()));
    }
    
    @Override
    public void values(Object owner, String message) {
        if(level >= LEVEL_VALUES) System.err.println(Util.wrap(message, getPrefix(owner), getWrap(), getIndentValues())); 
    }

    @Override
    public void suggest(Object owner, String message) {
        if(level >= LEVEL_SUGGEST) System.err.println("\n" + Util.wrap(message, getPrefix(owner), getWrap(), getIndentSuggest())); 
    }

    @Override
    public void trace(Throwable e) {
        e.printStackTrace();
    }
    
    public int getIndentInfo() { return 3; }
    
    public int getIndentNotify() { return 3; }
    
    public int getIndentDebug() { return 5; }
    
    public int getIndentWarning() { return 5; }
    
    public int getIndentErrors() { return 5; }
 
    public int getIndentStatus() { return 5; }
    
    public int getIndentResult() { return 0; }
    
    public int getIndentSuggest() { return 3; }
    
    public int getIndentDetail() { return 3; }
   
    public int getIndentValues() { return 3; }
     
     
    public final static int LEVEL_NONE = -1;
    public final static int LEVEL_ERROR = 0;
    public final static int LEVEL_WARNING = 1;
    public final static int LEVEL_NOTIFY = 2;
    public final static int LEVEL_STATUS = 3;
    public final static int LEVEL_SUGGEST = 4;
    public final static int LEVEL_RESULT = 5;
    public final static int LEVEL_VALUES = 6;
    public final static int LEVEL_INFO = 6;
    public final static int LEVEL_DETAIL = 8;
    public final static int LEVEL_DEBUG = 9;
}
