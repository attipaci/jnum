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
package jnum.text;

import java.io.Serializable;
import java.util.StringTokenizer;


public class VersionString implements Serializable, Comparable<VersionString> {	

	private static final long serialVersionUID = -3580136873936732038L;


	int major, minor, update, type;

	public VersionString(String text) { parse(text); }
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(VersionString other) {
		if(major > other.major) return 1;
		else if(major < other.major) return -1;
		
		if(minor > other.minor) return 1;
		else if(minor < other.minor) return -1;
		
		if(type > other.type) return 1;
		else if(type < other.type) return -1;
		
		if(update > other.update) return 1;
		else if(update < other.update) return -1;
		
		else return 0;		
	}
	

	public void parse(String text) {
		StringTokenizer tokens = new StringTokenizer(text, "-");
		parseVersion(tokens.nextToken());
		parseUpdate(tokens.nextToken());
	}
	

	private void parseVersion(String text) {
		StringTokenizer tokens = new StringTokenizer(text, ".");
		major = Integer.parseInt(tokens.nextToken());
		minor = tokens.hasMoreTokens() ? Integer.parseInt(tokens.nextToken()) : 0;
	}
	

	private void parseUpdate(String text) {
		text = text.toLowerCase();
		if(text.startsWith("alpha")) { type = TYPE_ALPHA; text = text.substring(5); }
		else if(text.startsWith("beta")) { type = TYPE_BETA; text = text.substring(4); }
		else if(text.startsWith("update")) { type = TYPE_RELEASE; text = text.substring(4); }
		else if(text.startsWith("rc")) { type = TYPE_RELEASE_CANDIDATE; text = text.substring(4); }
		else if(text.startsWith("pre")) { type = TYPE_RAWHIDE; text = text.substring(4); }
		else if(text.charAt(0) == 'b') { type = TYPE_BETA; text = text.substring(1); }
		else if(text.charAt(0) == 'a') { type = TYPE_ALPHA; text = text.substring(1); }
		else if(text.charAt(0) == 'u') { type = TYPE_ALPHA; text = text.substring(1); }	
		else type = TYPE_RELEASE;
		
		update = Integer.parseInt(text);		
	}
	

	public final static int TYPE_RAWHIDE = 0;
	
	public final static int TYPE_ALPHA = 1;

	public final static int TYPE_BETA = 2;

	public final static int TYPE_RELEASE_CANDIDATE = 3;

	public final static int TYPE_RELEASE = 4;

	public final static String[] typeShortString = { "pre", "a", "b", "rc", "" };

	public final static String[] typeLongString = { "pre-release", "alpha", "beta", "release candidate", "update" };
}
