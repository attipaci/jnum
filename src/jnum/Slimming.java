/*******************************************************************************
 * Copyright (c) 2014 Attila Kovacs <attila[AT]sigmyne.com>.
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
// (C)2007 Attila Kovacs <attila[AT]sigmyne.com>

package jnum;

/**
 * An interface that allows slimming, i.e. purging unused, invalid, or irrelevant data, in order to reduce the
 * volume of data under processing.
 * 
 * @author Attila Kovacs
 *
 */
public interface Slimming {

    /**
     * The single method that purges unused, invalid, or irrelevant data from this object, making it potentially smaller (slimmer)
     * as a result.
     * 
     */
	public void slim();
}
