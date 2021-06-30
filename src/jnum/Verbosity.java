/*******************************************************************************
 * Copyright (c) 2016 Attila Kovacs <attila[AT]sigmyne.com>.
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

package jnum;

/**
 * A simple interface for basic verbosity control. If you are looking for something more elaborate, check out the
 * {@link jnum.reporting.BasicMessaging} interface. And, for something even more elaborate, you can see 
 * {@link jnum.reporting.Reporter} abstract class. 
 * 
 * 
 * @author Attila Kovacs
 * 
 * @see jnum.reporting.BasicMessaging
 * @see jnum.reporting.Reporter
 *
 */
public interface Verbosity {

    /**
     * Turns verbosity on or off.
     * 
     * @param value     <code>true</code> to turn on verbosity, or <code>false</code> to turn verbosity off.
     */
    public void setVerbose(boolean value);
    
    /**
     * Checks the current verbosity setting.
     * 
     * @return      <code>true</code> if verbosity is enabled, otherwise <code>false</code>.
     */
    public boolean isVerbose();
    
}
