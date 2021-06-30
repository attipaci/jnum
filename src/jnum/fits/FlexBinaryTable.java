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
package jnum.fits;

import java.util.Vector;

import jnum.Util;
import nom.tam.fits.BasicHDU;
import nom.tam.fits.BinaryTable;
import nom.tam.fits.BinaryTableHDU;
import nom.tam.fits.Fits;
import nom.tam.fits.FitsException;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;
import nom.tam.util.Cursor;


public class FlexBinaryTable {

	Vector<Column> columns = new Vector<>();

	int rows;

	Header header;
	

	public static void main(String[] args) {
		try(Fits fits = new Fits(args[0]); Fits out = new Fits()) {		
			BasicHDU<?>[] HDU = fits.read();
			for(int i=0; i<HDU.length; i++) {
				if(HDU[i] instanceof BinaryTableHDU) {
					FlexBinaryTable flex = new FlexBinaryTable((BinaryTableHDU) HDU[i]);
					flex.columns.remove(0);
					out.addHDU(flex.createHDU());
				}
				else out.addHDU(HDU[i]);
			}
			fits.close();
			
			FitsToolkit.write(out, "test.fits");
			out.close();
		}
		catch(Exception e) { Util.error(FlexBinaryTable.class, e); }
	}
	

	public FlexBinaryTable(BinaryTableHDU hdu) throws FitsException {
		this.header = hdu.getHeader();
		rows = hdu.getNRows();
		int cols = hdu.getNCols();
		for(int c=0; c<cols; c++) columns.add(new Column(c, hdu.getColumn(c)));
	}
	

	public BinaryTableHDU createHDU() throws FitsException {
		
		for(int c=columns.size(); --c >= 0; ) columns.get(c).index = c;
		
		// Create the new column data
		Object[] data = new Object[columns.size()];
		for(int c=columns.size(); --c >= 0; ) data[c] = columns.get(c).data;
	
		// Make the HDU from the data.
		// This should create the TFORM/TDIM keys...
		BinaryTableHDU hdu = (BinaryTableHDU) Fits.makeHDU(new BinaryTable(data));
		
		Cursor<String, HeaderCard> cursor = hdu.getHeader().iterator();
			
		
		// Now add in all the remaining column descriptors...
		for(Column col : columns) col.addKeys(cursor);
		
		while(cursor.hasNext()) cursor.next();
		Cursor<String, HeaderCard> old = header.iterator();
		old.setKey("TFIELDS");
		old.next();
			
		// Copy over the old header keys, to just after TFIELDS...
		while(old.hasNext()) cursor.add(old.next());
		
		return hdu;
	}
	

	public BinaryTableHDU createHDU(int fromRow, int toRow) throws FitsException {
		BinaryTableHDU hdu = createHDU();
	
		if(toRow < hdu.getNRows()) hdu.deleteRows(toRow, hdu.getNRows() - toRow);
		if(fromRow > 0 ) hdu.deleteRows(0, fromRow);
		
		return hdu;
	}
	

	public Column getColumn(int i) { return columns.get(i); }
	

	public Column getColumn(String name) throws IllegalArgumentException {
		int index = findColumn(name);
		if(index < 0) throw new IllegalArgumentException("No such column: " + name);
		return columns.get(index);
	}
	

	public int findColumn(String name) {
		for(int c = columns.size(); --c >= 0; ) if(columns.get(c).getName().equals(name)) return c;
		return -1;
	}
	

	public void deleteColumn(String name) throws IllegalArgumentException {
		int index = findColumn(name);
		if(index < 0) throw new IllegalArgumentException("No such column: " + name);
		columns.remove(index);
	}
	

	public static boolean isColumnKey(String key, int index) {
		if(key.charAt(0) != 'T') return false;
		if(key.length() > 8) return false;
		
		String N = Integer.toString(index+1);
		int n = N.length();
		
		if(key.length() < 2 + n) return false;	
		if(key.length() > 6 + n) return false;
		
		char c = key.charAt(key.length() - 1);
		if(!(c >= '0' && c <= 'Z')) return false;
		int m = (c >= 'A' && c <= 'Z') ? 1 : 0;
		
		c = key.charAt(key.length() - (n+m+1));
		if(c >= '0' && c <= '9') return false;
			
		if(!key.substring(key.length() - (n + m), key.length() - m).equals(N)) return false;
		
		return true;
		
	}
	

	public class Column {

		private int index;

		private Vector<ColumnCard> cards = new Vector<>();

		private Object data;
		

		public Column(int index, Object data) {
			this.index = index;
			this.data = data;
			extractKeys();
		}
		

		public Object getData() { return data; }
		

		public void setData(Object data) { this.data = data; }
		

		public void setName(String name, String comment) throws HeaderCardException {
			setName(name, comment, "");
		}
		

		public void setName(String name, String comment, String alt) throws HeaderCardException {
			setMeta("TTYPE", name, comment, alt);
		}
			

		public String getName() {
			for(ColumnCard card : cards) if(card.stem.equals("TTYPE")) if(card.alt.length() == 0) 
				return card.getHeaderCard(index).getValue();
			return null;
		}
		

		public void setMeta(String stem, String name, String comment, String alt) throws HeaderCardException {
			if(alt == null) alt = "";
			
			HeaderCard hc =  new HeaderCard(stem + (index+1) + alt, name, comment);
			
			// Check if there is an existing meta descriptor by this stem + alt to update
			for(ColumnCard card : cards) {
				if(card.stem.equals(stem) && card.alt.equals(alt)) {
					card.tail = hc.toString().substring(8);
					return;
				}
			}
			// Else create a new card...
			cards.add(new ColumnCard(hc, index));
		}
		

		public void deleteMetas(String stem) {
			for(int i=cards.size(); --i >=0; ) if(cards.get(i).stem.equals(stem)) cards.remove(i);			
		}
		
		
		// Find all keys for the form T????nnA
		// (includes TTYPE, TFORM, TUNIT...)
		// Remove them from the header, and store card under the stems.
		public void extractKeys() {
			Cursor<String, HeaderCard> cursor = header.iterator();
			while(cursor.hasNext()) {
				HeaderCard card = cursor.next();
				if(isColumnKey(card.getKey(), index)) {
					try { cards.add(new ColumnCard(card, index)); }
					catch(Exception e) {}
					
					cursor.remove();
				}				
			}
		}	
		

		public void addKeys(Cursor<String, HeaderCard> cursor) {
			for(ColumnCard card : cards) card.add(cursor, index); 
		}
	}
	

	class ColumnCard {

		String stem;

		String tail;

		String alt = "";
		

		public ColumnCard(HeaderCard card, int index) throws IllegalArgumentException {
			String key = card.getKey();
			
			if(key.startsWith("TFORM")) throw new IllegalArgumentException("Column format");
			if(key.startsWith("TDIM")) throw new IllegalArgumentException("Column shape");
			
			String entry = card.toString();
			
			tail = entry.substring(8);
			char c = key.charAt(key.length() - 1);
			if(c >= 'A' && c <= 'Z') alt = c + "";
			int n = new String(alt + (index+1)).length();
			stem = key.substring(0, key.length() - n);	
		}
		

		public HeaderCard getHeaderCard(int index) {
			String key = stem + (index+1) + alt;
			while(key.length() < 8) key += ' ';
			return HeaderCard.create(key + tail);
		}
		

		public void add(Cursor<String, HeaderCard> cursor, int index) {
			cursor.setKey("TFORM" + (index + 1));
			cursor.add(getHeaderCard(index));
		}
	
	}
}



