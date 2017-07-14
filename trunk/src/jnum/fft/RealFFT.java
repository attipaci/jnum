/*******************************************************************************
 * Copyright (c) 2017 Attila Kovacs <attila[AT]sigmyne.com>.
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
package jnum.fft;

// TODO: Auto-generated Javadoc
/**
 * The Interface RealFFT.
 *
 * @param <Type> the generic type
 */
public interface RealFFT<Type> {
	
	/**
	 * Real transform.
	 *
	 * @param data the data
	 * @param isForward true if forward transform, false for back transform.
	 */
	public void realTransform(Type data, boolean isForward);	
	
		
	/**
	 * sequential real transform.
	 *
	 * @param data the data
	 * @param isForward true if forward transform, false for back transform.
	 */
	public void sequentialRealTransform(Type data, boolean isForward);
	
	/**
	 * Transform real-valued data to complex amplitudes at non-negative frequencies. 
	 * Same as forward transform followed by a normalization of 2/N.
	 *
	 * @param data the data
	 */
	public void real2Amplitude(Type data);
	
	
	/**
	 * Transform the complex amplitudes at non-negative frequencies back to real values.
	 *
	 * @param spectrum the spectrum
	 */
	public void amplitude2Real(Type spectrum); 

}
