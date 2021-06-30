/* *****************************************************************************
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
 *     Attila Kovacs  - initial API and implementation
 ******************************************************************************/
package jnum.astro;



public class WeatherAdapter implements Weather {


	@Override
	public double getAmbientHumidity() {
		return Double.NaN;
	}

	@Override
	public double getAmbientPressure() {
		return Double.NaN;
	}

	@Override
	public double getAmbientKelvins() {
		return Double.NaN;
	}

	@Override
	public double getWindDirection() {
		return Double.NaN;
	}

	@Override
	public double getWindPeak() {
		return Double.NaN;
	}

	@Override
	public double getWindSpeed() {
		return Double.NaN;
	}

}
