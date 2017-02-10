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

// TODO: Auto-generated Javadoc
/**
 * The Class VMTest.
 */
public class VMTest {

	/** The properties. */
	static String[] properties = { "java.vendor", "java.version", "java.home", "java.runtime.name",
		"java.runtime.version", "java.vm.name", "java.vm.version", "os.name", "os.version", "os.arch",
		"sun.arch.data.model", "sun.cpu.endian", "user.country", "user.language" };
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		for(String property : properties) System.out.println(property + "\r\t\t\t" + System.getProperty(property));		
	}
	
}
