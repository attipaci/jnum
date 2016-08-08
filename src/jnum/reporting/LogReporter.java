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

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogReporter extends Reporter {
    private Logger logger;
    private boolean isTypeLabeled = true;
    private boolean isObjectLabeled = false;
    
    public LogReporter(Logger logger) {
        super(logger.getName());
        this.logger = logger;
    }

    public boolean isTypeLabeled() { return isTypeLabeled; }
    
    public void setTypeLabeled(boolean value) { isTypeLabeled = value; }
  
    public boolean isObjectLabeled() { return isObjectLabeled; }
    
    public void setObjectLabeled(boolean value) { isObjectLabeled = value; }
    
    
    protected String getHeader(Object owner, String type) {
        StringBuffer header = new StringBuffer();
        if(isObjectLabeled) if(owner != null) header.append(owner.getClass().getSimpleName()); 
        if(isTypeLabeled) if(type != null) if(type.length() > 0) header.append((header.length() > 0 ? "." : "") + type);
        if(header.length() > 0) header.append("> ");
        return new String(header);
    }
    
    @Override
    public void info(Object owner, String message) {
        logger.log(Level.INFO, getHeader(owner, "INFO") + message);
    }

    @Override
    public void notify(Object owner, String message) {
        logger.log(Level.INFO, getHeader(owner, "NOTE") + message);
    }

    @Override
    public void debug(Object owner, String message) {
        logger.log(Level.FINEST, getHeader(owner, "DEBUG") + message);
    }

    @Override
    public void warning(Object owner, String message) {
        logger.log(Level.WARNING, getHeader(owner, null) + message);
    }

    @Override
    public void error(Object owner, String message) {
        logger.log(Level.SEVERE, getHeader(owner, null) + message);
    }

    @Override
    public void trace(Throwable e) {
       String s = new String();
       
       try {
           PrintStream out = new PrintStream(s);
           e.printStackTrace(out);
           logger.log(Level.WARNING, getHeader(null, "TRACE") + s);
           out.close();
       } catch (FileNotFoundException e1) {
           logger.log(Level.WARNING, "Trace error in " + getClass().getSimpleName() + ".");
       }
      
    }

    @Override
    public void status(Object owner, String message) {
        logger.log(Level.INFO, getHeader(owner, "STATUS") + message);
    }

    @Override
    public void result(Object owner, String message) {
        logger.log(Level.INFO, getHeader(owner, "RESULT") + message);
    }

    @Override
    public void detail(Object owner, String message) {
        logger.log(Level.FINE, getHeader(owner, "DETAIL") + message);
    }

    @Override
    public void values(Object owner, String message) {
        logger.log(Level.INFO, getHeader(owner, "VALUES") + message);
    }

    @Override
    public void suggest(Object owner, String message) {
        logger.log(Level.FINE, getHeader(owner, "SUGGEST") + message);
    }
    
}
