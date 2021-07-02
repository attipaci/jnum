/* *****************************************************************************
 * Copyright (c) 2021 Attila Kovacs <attila[AT]sigmyne.com>.
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

package jnum.math;

import java.io.Serializable;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.stream.IntStream;

import jnum.Copiable;
import jnum.Util;
import jnum.ViewableAsDoubles;
import jnum.data.index.IndexedValues;
import jnum.data.samples.Index1D;
import jnum.fits.FitsToolkit;
import jnum.text.NumberFormating;
import jnum.text.Parser;
import jnum.text.StringParser;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;
import nom.tam.util.Cursor;

/**
 * A base class for 3D coordinates of all types. That is basically anything with a triplet of real values.
 * 
 * @author Attila Kovacs
 *
 */
public class Coordinate3D implements Coordinates<Double>, Serializable, Cloneable, Copiable<Coordinate3D>, 
ViewableAsDoubles, Parser, NumberFormating {

	private static final long serialVersionUID = 4670218761839380720L;

	private double x, y, z;

	public Coordinate3D() {}
	
	public Coordinate3D(double x, double y, double z) {
	    this();
	    set(x, y, z);
	}
	
	public Coordinate3D(Coordinates<Double> v) {
	    this(v.x(), v.y(), v.z());
	}
	
    @Override
    public final Class<Double> getComponentType() {
        return Double.class;
    }
    

	@Override
    public Coordinate3D clone() {
	    try { return (Coordinate3D) super.clone(); }
	    catch(CloneNotSupportedException e) { return null; }
	}
	
	@Override
    public Coordinate3D copy() {
	    return clone();
	}
	
	@Override
    public void copy(Coordinates<? extends Double> other) {
	    set(other.x(), other.y(), other.z());
	}
	
	public void zero() {
	    set(0.0, 0.0, 0.0);
	}
	
	public boolean isNull() {
	    if(x != 0.0) return false;
	    if(y != 0.0) return false;
	    if(z != 0.0) return false;
	    return true;
	}
	
	@Override
    public final Double x() { return x; }

	@Override
    public final Double y() { return y; }

	@Override
    public final Double z() { return z; }
	
	public final void set(final double x, final double y, final double z) {
	    this.x = x;
	    this.y = y;
	    this.z = z;
	}

	public final void setX(final double value) { this.x = value; }

	public final void setY(final double value) { this.y = value; }

	public final void setZ(final double value) { this.z = value; }
	
	
	public final void addX(final double value) { this.x += value; }

    public final void addY(final double value) { this.y += value; }

    public final void addZ(final double value) { this.z += value; }
    
	  
    public final void subtractX(final double value) { this.x -= value; }

    public final void subtractY(final double value) { this.y -= value; }

    public final void subtractZ(final double value) { this.z -= value; }
    
    
    public final void scaleX(double factor) { x *= factor; }
    
    public final void scaleY(double factor) { y *= factor; }
    
    public final void scaleZ(double factor) { z *= factor; }
	
    @Override
    public String toString(NumberFormat nf) {
        return "(" + nf.format(x) + "," + nf.format(y) + "," + nf.format(z) + ")";
    }

    @Override
    public String toString() { 
        return "(" + x + "," + y + "," + z + ")";
    }
    
    
    public final void parse(String spec) {
        parse(spec, new ParsePosition(0));
    }

   
    @Override
    public final void parse(String text, ParsePosition pos) throws IllegalArgumentException {
        parse(new StringParser(text, pos));
    }
    
    /**
     * Parses text x,y,z 3D coordinate representations that are in the format(s) of:
     * 
     * <pre>
     * {@code
     *     x,y,z / x y z
     * }
     * </pre>
     * 
     * or
     * 
     * <pre>
     * {@code
     *     (x,y,z) / (x y z)
     * }
     * </pre>
     * 
     * More specifically, the x, y, and z values may be separated either by comma(s) or white space(s) (including 
     * tabs line breaks, carriage returns), or a combination of both. The pair of values may be bracketed (or 
     * not). Any number of white spaces may exists between the elements (brackets and pair of values), or 
     * precede the text element. Thus, the following will parse as a proper x,y,z (1.0,-2.0,3.0) pair:
     * 
     * <pre>
     * {@code
     *     (  \t\n 1.0 ,,, \r , \t -2.0 \t 3.0   \n  )
     * }
     * </pre>
     * 
     * @param parser  The string parsing helper object.
     * @throws NumberFormatException
     */
    public void parse(StringParser parser) throws NumberFormatException {
        boolean isBracketed = false;
        
        parser.skipWhiteSpaces();
           
        if(parser.peek() == '(') {
            isBracketed = true;
            parser.skip(1);
            parser.skipWhiteSpaces();
        }
            
        parseX(parser.nextToken(Util.getWhiteSpaceChars() + ","));
        parseY(parser.nextToken(Util.getWhiteSpaceChars() + ","));
        parseZ(parser.nextToken(Util.getWhiteSpaceChars() + "," + (isBracketed ? "" : ")")));
    }

    protected void parseX(String token) throws NumberFormatException {
        setX(Double.parseDouble(token));
    }
    
    protected void parseY(String token) throws NumberFormatException {
        setY(Double.parseDouble(token));
    }
    
    protected void parseZ(String token) throws NumberFormatException {
        setY(Double.parseDouble(token));
    }

    @Override
    public final void createFromDoubles(Object array) throws IllegalArgumentException {
        if(!(array instanceof double[])) throw new IllegalArgumentException("argument is not a double[].");
        double[] components = (double[]) array;
        if(components.length != 3) throw new IllegalArgumentException("argument double[] array is to small.");
        set(components[0], components[1], components[2]);
    }

  
    @Override
    public final void viewAsDoubles(Object view) throws IllegalArgumentException {
        if(!(view instanceof double[])) throw new IllegalArgumentException("argument is not a double[].");
        double[] components = (double[]) view;
        if(components.length != 3) throw new IllegalArgumentException("argument double[] array is to small.");
        components[0] = x;
        components[1] = y;
        components[3] = z;
    }
    
  
    @Override
    public final double[] viewAsDoubles() {
        return new double[] { x, y, z };       
    }
    
    
   
    public double getValue(int field) throws NoSuchFieldException {
        switch(field) {
        case X: return x;
        case Y: return y;
        case Z: return z;
        default: throw new NoSuchFieldException(getClass().getSimpleName() + " has no field for " + field);
        }
    }
    
  
    public void setValue(int field, double value) throws NoSuchFieldException {
        switch(field) {
        case X: x = value; break;
        case Y: y = value; break;
        case Z: z = value; break; 
        default: throw new NoSuchFieldException(getClass().getSimpleName() + " has no field for " + field);
        }
    }
    
    
    public void editHeader(Header header, String keyStem, String alt) throws HeaderCardException {
        Cursor<String, HeaderCard> c = FitsToolkit.endOf(header);
        c.add(new HeaderCard(keyStem + "1" + alt, x, "The reference x coordinate in SI units."));
        c.add(new HeaderCard(keyStem + "2" + alt, y, "The reference y coordinate in SI units."));
        c.add(new HeaderCard(keyStem + "3" + alt, z, "The reference z coordinate in SI units."));
    }
    

    public void parseHeader(Header header, String keyStem, String alt, Coordinate3D defaultValue) {
        x = header.getDoubleValue(keyStem + "1" + alt, defaultValue == null ? 0.0 : defaultValue.x());
        y = header.getDoubleValue(keyStem + "2" + alt, defaultValue == null ? 0.0 : defaultValue.y());
        z = header.getDoubleValue(keyStem + "3" + alt, defaultValue == null ? 0.0 : defaultValue.z());
    }
    

    @Override
    public final int size() {
        return 3;
    }

    @Override
    public final int capacity() {
        return 3;
    }


    @Override
    public final int dimension() {
        return 1;
    }


    @Override
    public final Index1D getSize() {
        return size;
    }


    @Override
    public final Double get(Index1D index) {
        return getComponent(index.i());
    }


    @Override
    public void set(Index1D index, Double value) {
        setComponent(index.i(), value);
    }


    @Override
    public final Index1D getIndexInstance() {
        return new Index1D();
    }


    @Override
    public final Index1D copyOfIndex(Index1D index) {
        return index.copy();
    }


    @Override
    public final boolean conformsTo(Index1D size) {
        return size.i() == 3;
    }


    @Override
    public final boolean conformsTo(IndexedValues<Index1D, ?> data) {
        return data.getSize().i() == 3;
    }


    @Override
    public final String getSizeString() {
        return "[3]";
     }


    @Override
    public final boolean containsIndex(Index1D index) {
        int i = index.getValue(0);
        if(i == X) return true;
        if(i == Y) return true;
        return false;
    }

    
    
    @Override
    public final Double getComponent(final int index) {
        switch(index) {
        case X: return x;
        case Y: return y;
        case Z: return z;
        default: return 0.0;
        }
    }

    @Override
    public final void setComponent(final int index, final Double value) {
        switch(index) {
        case X: x = value; break;
        case Y: y = value; break;
        case Z: z = value; break;
        default: throw new IndexOutOfBoundsException(getClass().getSimpleName() + " has no component " + index);
        }
    }
    
    public static Coordinate3D[] copyOf(Coordinate3D[] array) {
        Coordinate3D[] copy = new Coordinate3D[array.length];
        IntStream.range(0, array.length).parallel().filter(i -> array[i] != null).forEach(i -> copy[i] = array[i].copy());
        return copy;
    }
    
    private static final Index1D size = new Index1D(2);
    
    public static final int X = 0;    
    public static final int Y = 1;
    public static final int Z = 2;    

}
