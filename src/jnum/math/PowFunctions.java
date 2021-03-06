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
package jnum.math;


public interface PowFunctions {

	/**
	 * abs(x).
	 *
	 * @return the absolute value
	 */
	public double abs();
	
	/**
	 * x^n.
	 *
	 * @param n the n
	 */
	public void pow(double n);

	/**
	 * 1/x.
	 */
	public void invert();
	
	/**
	 * x^2.
	 */
	public void square();
	
	/**
	 * sqrt(x).
	 */
	public void sqrt();
	
	/**
	 * exp(x).
	 */
	public void exp();
	
	/**
	 * exp(x-1).
	 */
	public void expm1();
	
	/**
	 * log(x).
	 */
	public void log();
	
	/**
	 * log(1+x).
	 */
    public void log1p();
	
}
