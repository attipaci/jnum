/*******************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila[AT]sigmyne.com>.
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
package jnum.astro;

// TODO: Auto-generated Javadoc
/**
 * The Interface Weather.
 */
public interface Weather {

	/**
	 * Gets the ambient temperature.
	 *
	 * @return the ambient temperature
	 */
	public double getAmbientKelvins();
	
	/**
	 * Gets the ambient pressure.
	 *
	 * @return the ambient pressure
	 */
	public double getAmbientPressure();
	
	/**
	 * Gets the ambient humidity.
	 *
	 * @return the ambient humidity
	 */
	public double getAmbientHumidity();
	
	/**
	 * Gets the wind speed.
	 *
	 * @return the wind speed
	 */
	public double getWindSpeed();
	
	/**
	 * Gets the wind direction.
	 *
	 * @return the wind direction
	 */
	public double getWindDirection();
	
	/**
	 * Gets the wind peak.
	 *
	 * @return the wind peak
	 */
	public double getWindPeak();
	
}
