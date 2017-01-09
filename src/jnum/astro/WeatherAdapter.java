/*******************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
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
package jnum.astro;


// TODO: Auto-generated Javadoc
/**
 * The Class WeatherAdapter.
 */
public class WeatherAdapter implements Weather {

	/* (non-Javadoc)
	 * @see jnum.astro.Weather#getAmbientHumidity()
	 */
	@Override
	public double getAmbientHumidity() {
		return Double.NaN;
	}

	/* (non-Javadoc)
	 * @see jnum.astro.Weather#getAmbientPressure()
	 */
	@Override
	public double getAmbientPressure() {
		return Double.NaN;
	}

	/* (non-Javadoc)
	 * @see jnum.astro.Weather#getAmbientTemperature()
	 */
	@Override
	public double getAmbientKelvins() {
		return Double.NaN;
	}

	/* (non-Javadoc)
	 * @see jnum.astro.Weather#getWindDirection()
	 */
	@Override
	public double getWindDirection() {
		return Double.NaN;
	}

	/* (non-Javadoc)
	 * @see jnum.astro.Weather#getWindPeak()
	 */
	@Override
	public double getWindPeak() {
		return Double.NaN;
	}

	/* (non-Javadoc)
	 * @see jnum.astro.Weather#getWindSpeed()
	 */
	@Override
	public double getWindSpeed() {
		return Double.NaN;
	}

}
