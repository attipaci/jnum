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
package jnum.util;

import java.text.NumberFormat;
import java.util.Hashtable;
import jnum.text.TableFormatter;


// TODO: Auto-generated Javadoc
/**
 * The Class DataTable.
 */
public class DataTable extends Hashtable<String, DataTable.Entry> implements TableFormatter.Entries {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2131139489959923852L;
	
	/* (non-Javadoc)
	 * @see jnum.text.TableFormatter.Entries#getFormattedEntry(java.lang.String, java.lang.String)
	 */
	@Override
	public Object getTableEntry(String name) {
		if(!containsKey(name)) return TableFormatter.NO_SUCH_DATA;
		return get(name).getValue();
	}
	
	/**
	 * The Class Entry.
	 */
	public class Entry {
		
		/** The value. */
		private double value;
		
		/** The unit name. */
		private String name, unitName;
		
		/** The comment. */
		private String comment;
		
		/**
		 * Instantiates a new entry.
		 *
		 * @param name the name
		 * @param value the value
		 * @param unitName the unit name
		 */
		public Entry(String name, double value, String unitName) {
			this(name, value, unitName, "");
		}
		
		/**
		 * Instantiates a new entry.
		 *
		 * @param name the name
		 * @param value the value
		 * @param unitName the unit name
		 * @param comment the comment
		 */
		public Entry(String name, double value, String unitName, String comment) {
			this.name = name;
			this.value = value;
			this.unitName = unitName;	
			this.comment = comment;
			put(name, Entry.this);
		}

		/**
		 * Gets the value.
		 *
		 * @return the value
		 */
		public double getValue() { return value; }
		
		/**
		 * Gets the name.
		 *
		 * @return the name
		 */
		public String getName() { return name; }
		
		/**
		 * Gets the unit name.
		 *
		 * @return the unit name
		 */
		public String getUnitName() { return unitName; }
		
		/**
		 * Gets the comment.
		 *
		 * @return the comment
		 */
		public String getComment() { return comment; }
		
		/**
		 * Sets the value.
		 *
		 * @param x the new value
		 */
		public void setValue(double x) { this.value = x; }
		
		/**
		 * Sets the name.
		 *
		 * @param value the new name
		 */
		public void setName(String value) { this.name = value; }
		
		/**
		 * Sets the unit name.
		 *
		 * @param value the new unit name
		 */
		public void setUnitName(String value) { this.unitName = value; }
		
		/**
		 * Sets the comment.
		 *
		 * @param value the new comment
		 */
		public void setComment(String value) { this.comment = value; }
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return name + " = " + value + " " + unitName + (comment.length() > 0 ? " (" + comment + ")" : "");
		}
		
		/**
		 * To string.
		 *
		 * @param nf the nf
		 * @return the string
		 */
		public String toString(NumberFormat nf) {
			return name + " = " + nf.format(value) + " " + unitName + (comment.length() > 0 ? " (" + comment + ")" : "");
		}
	}
}
