/*******************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila[AT]sigmyne.com>.
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
 *     Attila Kovacs <attila[AT]sigmyne.com> - initial API and implementation
 ******************************************************************************/
package test;

import jnum.Symbol;
import jnum.Unit;
import jnum.Unit.Multiplier;
import jnum.util.CompoundUnit;
import jnum.util.ExponentUnit;

// TODO: Auto-generated Javadoc
/**
 * The Class UnitTest.
 */
public class UnitTest {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		Unit u = null;
		
		u = ExponentUnit.get("aJ");
		System.err.println(u + " = " + u.value());
		
		u = ExponentUnit.get("GA");
		System.err.println(u + " = " + u.value());
		
		u = ExponentUnit.get("mg^2");
		System.err.println(u + " = " + u.value());
		
		u = ExponentUnit.get(Symbol.Acircle + "");
		System.err.println(u + " = " + u.value());
		
		CompoundUnit cu = new CompoundUnit();
		
		cu.parse("kg m / s^2");
		System.err.println(cu + " = " + cu.value());
		
		cu.multiplyBy(Unit.get("s"));
		System.err.println("* s: " + cu + " = " + cu.value());
		
		cu.multiplyBy(Unit.get("s"));
		System.err.println("* s: " + cu + " = " + cu.value());
		
		cu.divideBy(Unit.get("s"));
		System.err.println("/ s: " + cu + " = " + cu.value());
		
		cu.multiplyBy(Multiplier.micro);
		System.err.println("micro: " + cu + " = " + cu.value());
	}
	
}
