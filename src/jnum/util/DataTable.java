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
package jnum.util;

import java.text.NumberFormat;
import java.util.Hashtable;

import jnum.Unit;
import jnum.text.NumberFormating;
import jnum.text.TableFormatter;


public class DataTable extends Hashtable<String, DataTable.Entry> implements TableFormatter.Entries {

	private static final long serialVersionUID = 2131139489959923852L;

	@Override
	public Object getTableEntry(String name) {
		if(!containsKey(name)) return TableFormatter.NO_SUCH_DATA;
		return get(name).getValue();
	}
	

	public class Entry implements NumberFormating {

		private double value;

		private String name, unitName;

		private String comment;
		

		public Entry(String name, double value, String unitName) {
			this(name, value, unitName, "");
		}
		
		public Entry(String name, double value, String unitName, String comment) {
			this.name = name;
			this.value = value;
			this.unitName = unitName;	
			this.comment = comment;
			put(name, Entry.this);
		}

		public Entry(String name, double value, Unit unit) {
            this(name, value / unit.value(), unit.name());
        }
        

		public double getValue() { return value; }
		

		public String getName() { return name; }
		

		public String getUnitName() { return unitName; }
		
		
		public String getComment() { return comment; }
		

		public void setValue(double x) { this.value = x; }
		

		public void setName(String value) { this.name = value; }
		

		public void setUnitName(String value) { this.unitName = value; }
		

		public void setComment(String value) { this.comment = value; }

		@Override
		public String toString() {
			return name + " = " + value + " " + unitName + (comment.length() > 0 ? " (" + comment + ")" : "");
		}	

		@Override
        public String toString(NumberFormat nf) {
			return name + " = " + nf.format(value) + " " + unitName + (comment.length() > 0 ? " (" + comment + ")" : "");
		}
	}

}
