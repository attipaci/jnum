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

// TODO: Auto-generated Javadoc
/**
 * The Interface BasicMessaging.
 */
public interface BasicMessaging {

       /**
        * Info.
        *
        * @param message the message
        */
       public void info(String message);
       
       /**
        * Notify.
        *
        * @param message the message
        */
       public void notify(String message);
       
       /**
        * Debug.
        *
        * @param message the message
        */
       public void debug(String message);
       
       /**
        * Warning.
        *
        * @param message the message
        */
       public void warning(String message);
       
       /**
        * Warning.
        *
        * @param e the e
        * @param debug the debug
        */
       public void warning(Exception e, boolean debug);
       
       /**
        * Warning.
        *
        * @param e the e
        */
       public void warning(Exception e);
    
       /**
        * Error.
        *
        * @param message the message
        */
       public void error(String message);
       
       /**
        * Error.
        *
        * @param e the e
        * @param debug the debug
        */
       public void error(Throwable e, boolean debug);
       
       /**
        * Error.
        *
        * @param e the e
        */
       public void error(Throwable e);
    
}
