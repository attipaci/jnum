/*******************************************************************************
 * Copyright (c) 2017 Attila Kovacs <attila[AT]sigmyne.com>.
 * All rights reserved. 
 * 
 * This file is part of crush.
 * 
 *     crush is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     crush is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with crush.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Attila Kovacs <attila[AT]sigmyne.com> - initial API and implementation
 ******************************************************************************/

package jnum.data.samples;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Map;

import jnum.CopiableContent;
import jnum.Unit;
import jnum.fits.FitsToolkit;
import jnum.math.IntRange;
import nom.tam.fits.Fits;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;
import nom.tam.fits.ImageHDU;
import nom.tam.util.Cursor;

/**
* A generic light-weight 1D data class implementation with FITS I/O support.
* 
* 
* @author Attila Kovacs <attila@sigmyne.com>
*
*/
public abstract class Samples1D extends Data1D implements Serializable, Resizable1D, CopiableContent<Samples1D> {
    /**
     * 
     */
    private static final long serialVersionUID = -150948566995935566L;

    private String id;
    
    
    protected abstract void setDataSize(int size);
   
    
    public String getID() { return id; }
    
    public void setID(String id) { this.id = id; }
   
    @Override
    public Samples1D copy() {
        return copy(true);
    }

    
    @Override
    public Samples1D copy(boolean withContent) {   
        Samples1D copy = (Samples1D) clone();
        
        if(capacity() > 0) {
            copy.setSize(size());
            if(withContent) copy.paste(this, true);
        }
        
        return copy;
    }

    @Override
    public Samples1D newImage() { return copy(false); }
    
    
    @Override
    public synchronized void setSize(int size) {
        setDataSize(size);
        clearHistory();
        addHistory("new size " + getSizeString());
    }
    
    public synchronized void destroy() { 
        setSize(0); 
        clearHistory();
    }

    public void setData(final Values1D values) {
        setSize(values.size());
        new Fork<Void>() {
            @Override
            protected void processElementAt(int i) {
                if(values.isValid(i)) set(i, values.get(i));
                else discard(i);
            }
        }.process();
    }
    
    
    public void setData(Object image) {   
        if(image instanceof double[]) setData((double[]) image);
        else if(image instanceof float[]) setData((float[]) image);
        else if(image instanceof long[]) setData((long[]) image);
        else if(image instanceof int[]) setData((int[]) image);
        else if(image instanceof short[]) setData((short[]) image);
        else if(image instanceof byte[]) setData((byte[]) image);
        else throw new IllegalArgumentException("Cannot set image content to type " + image.getClass().getSimpleName());
    }
    
    
  
    
    public synchronized void setData(final double[] data) { 
        setSize(data.length);
        new Fork<Void>() {
            @Override
            protected void processElementAt(int i) {
                set(i, data[i]);
            }
        }.process();
        recordNewData("double[]");
    }


    public synchronized void setData(final float[] data) {
        setSize(data.length);
        new Fork<Void>() {
            @Override
            protected void processElementAt(int i) {
                set(i, data[i]);
            }
        }.process();
        recordNewData("float[]");
    }

    public synchronized void setData(final long[] data) {
        setSize(data.length);
        new Fork<Void>() {
            @Override
            protected void processElementAt(int i) {
                set(i, data[i]);
            }
        }.process();
        recordNewData("long[]");
    }


    public synchronized void setData(final int[] data) {
        setSize(data.length);
        new Fork<Void>() {
            @Override
            protected void processElementAt(int i) {
                set(i, data[i]);
            }
        }.process();
        recordNewData("int[]");
    }

    public synchronized void setData(final short[] data) {
        setSize(data.length);
        new Fork<Void>() {
            @Override
            protected void processElementAt(int i) {
                set(i, data[i]);
            }
        }.process();
        recordNewData("short[]");
    }

    public synchronized void setData(final byte[] data) {
        setSize(data.length);
        new Fork<Void>() {
            @Override
            protected void processElementAt(int i) {
                set(i, data[i]);
            }
        }.process();
        recordNewData("byte[]");
    }
    
    

    protected synchronized void crop(int imin, int imax) {
        addHistory("cropped " + imin + " : " + imax);
        silentNextNewData();
        setData(getCropped(imin, imax).getCore());
    }


    public void autoCrop() {
        IntRange r = getIndexRange();
        if(r == null) return; 

        this.crop((int) r.min(), (int) r.max());
    }

    
    @Override
    protected void editHeader(Header header) throws HeaderCardException {          
        Cursor<String, HeaderCard> c = FitsToolkit.endOf(header);
        if(getID() != null) c.add(new HeaderCard("EXTNAME", getID(), "Content identifier.")); 
        
        super.editHeader(header);     
    }
   

    @Override
    protected void parseHeader(Header header) {
        super.parseHeader(header);
        setID(header.containsKey("EXTNAME") ? header.getStringValue("EXTNAME") : null);
    }
    
    protected void parseHeader(Header header, Map<String, Unit> extraCoreUnits) {
        setID(header.getStringValue("EXTNAME"));
        // The image data unit must be parsed after the instrument beam (underlying + smoothing)
        // and the coordinate grid are established as it may contain 'beam' or 'pixel' type units.
        if(header.containsKey("BUNIT")) setUnit(header.getStringValue("BUNIT"), extraCoreUnits);
        parseHistory(header);
    }
    
    
    public synchronized void read(ImageHDU hdu, Map<String, Unit> extraCoreUnits) throws Exception {
        parseHeader(hdu.getHeader(), extraCoreUnits);
        setData(hdu.getData().getData());   
        scale(getUnit().value());
    }
    
    
    
    

    public static Samples1D createType(Class<? extends Number> type) {       
        if(type.equals(Double.class)) return new Double1D();
        else if(type.equals(Float.class)) return new Float1D();
        else if(type.equals(Long.class)) return new Long1D();
        else if(type.equals(Integer.class)) return new Integer1D();
        else if(type.equals(Short.class)) return new Short1D();
        else if(type.equals(Byte.class)) return new Byte1D();
        else return null;
    }
    
    
    public static Samples1D createType(Class<? extends Number> type, int size) {
        Samples1D samples = createType(type);
        if(samples == null) return null;
        samples.setSize(size);
        return samples;
    }
    
    public static Samples1D createFrom(final Values1D values) { return createFrom(values, null); }
   
    
    public static Samples1D createFrom(final Values1D values, final Number blankingValue) {
        return createFrom(values, blankingValue, values.getElementType());
    }
    
    public static Samples1D createFrom(final Values1D values, final Number blankingValue, Class<? extends Number> elementType) {
        final Samples1D samples = createType(elementType);
        samples.setBlankingValue(blankingValue);
        samples.setData(values);
        return samples;
    }
   
    public static Samples1D createBitpixType(int bitpix) {        
        switch(bitpix) {
        case FitsToolkit.BITPIX_FLOAT: return createType(Float.class);
        case FitsToolkit.BITPIX_DOUBLE: return createType(Double.class);
        case FitsToolkit.BITPIX_LONG: return createType(Long.class);
        case FitsToolkit.BITPIX_INT: return createType(Integer.class);
        case FitsToolkit.BITPIX_SHORT: return createType(Short.class);
        case FitsToolkit.BITPIX_BYTE: return createType(Byte.class);
        }
        
        return null;
    }
    
    public static Samples1D read(Fits fits, int hduIndex) throws Exception {
        return read(fits, hduIndex, null);
    }
    
    public static Samples1D read(Fits fits, int hduIndex, Hashtable<String, Unit> extraUnits) throws Exception {
        ImageHDU hdu = (ImageHDU) fits.getHDU(hduIndex);
        Samples1D samples = createBitpixType(hdu.getBitPix());
        samples.read(hdu, extraUnits);
        return samples;
    }


    
    
    
    
    
    
    
    
    
    
    
   
    public static class Double1D extends Samples1D {       
        /**
         * 
         */
        private static final long serialVersionUID = 2366940573012663433L;
  
        private double[] data;

        
        @Override
        public Number getLowestCompareValue() { return Double.NEGATIVE_INFINITY; }
        
        @Override
        public Number getHighestCompareValue() { return Double.POSITIVE_INFINITY; }
        
        @Override
        public int compare(Number a, Number b) {
            return Double.compare(a.doubleValue(), b.doubleValue());
        }
        
        @Override
        public Class<? extends Number> getElementType() {
            return Double.class;
        }
        
        @Override
        protected synchronized void setDataSize(int size) {
            if(size <= 0) data = null;
            else data = new double[size];
        }

        @Override
        public final int size() {
            return data == null ? 0 : data.length;
        }


        @Override
        public synchronized double[] getCore() {
            return data;
        }
        
        
        @Override
        public synchronized void setData(double[] image) {
            this.data = image;
            recordNewData("double[] (no copy)");
        }

        @Override
        public final Number get(int i) {
            return data[i];
        }

        @Override
        public final void set(int i, Number value) {
            data[i] = value.doubleValue();
        }
        
        @Override
        public final void add(int i, Number value) {
           data[i] += value.doubleValue();
        }
            
        @Override
        public final boolean isValid(Number value) {
            if(Double.isNaN(value.doubleValue())) return false;
            return super.isValid(value);
        }

    }
    
    
    public static class Float1D extends Samples1D {
        /**
         * 
         */
        private static final long serialVersionUID = -5993773243990361454L;
  
        private float[] data;

        
        @Override
        public Number getLowestCompareValue() { return Float.NEGATIVE_INFINITY; }
        
        @Override
        public Number getHighestCompareValue() { return Float.POSITIVE_INFINITY; }
        
        @Override
        public int compare(Number a, Number b) {
            return Float.compare(a.floatValue(), b.floatValue());
        }
        
        
        @Override
        public Class<? extends Number> getElementType() {
            return Float.class;
        }

        @Override
        protected synchronized void setDataSize(int size) {
            if(size <= 0) data = null;
            else data = new float[size];
        }

        @Override
        public final int size() {
            return data == null ? 0 : data.length;
        }

      
        @Override
        public synchronized float[] getCore() {
            return data;
        }
        
        @Override
        public synchronized void setData(float[] image) {
            this.data = image;
            recordNewData("float[] (no copy)");
        }

        @Override
        public final Number get(int i) {
            return data[i];
        }

        @Override
        public final void set(int i, Number value) {
            data[i] = value.floatValue();
        }
        
        @Override
        public final void add(int i, Number value) {
           data[i] += value.floatValue();
        }
        
           
        @Override
        public final boolean isValid(Number value) {
            if(Float.isNaN(value.floatValue())) return false;
            return super.isValid(value);
        }


    }
    
    
    public static class Long1D extends Samples1D {
        /**
         * 
         */
        private static final long serialVersionUID = -1541121271559319412L;
        
        private long[] data;


        @Override
        public Class<? extends Number> getElementType() {
            return Long.class;
        }
       

        @Override
        protected synchronized void setDataSize(int size) {
            if(size <= 0) data = null;
            else data = new long[size];
        }

        @Override
        public final int size() {
            return data == null ? 0 : data.length;
        }


        @Override
        public synchronized long[] getCore() {
            return data;
        }
        
        @Override
        public synchronized void setData(long[] image) {
            this.data = image;
            recordNewData("long[] (no copy)");
        }

        @Override
        public final Number get(int i) {
            return data[i];
        }

        @Override
        public final void set(int i, Number value) {
            data[i] = value.longValue();
        }
        
        @Override
        public final void add(int i, Number value) {
           data[i] += value.longValue();
        }
        
  
    }
    
    
    public static class Integer1D extends Samples1D {
        /**
         * 
         */
        private static final long serialVersionUID = 7454521582169682582L;
    
        private int[] data;


        @Override
        public Class<? extends Number> getElementType() {
            return Integer.class;
        }

        @Override
        protected synchronized void setDataSize(int size) {
            if(size <= 0) data = null;
            else data = new int[size];
        }

        @Override
        public final int size() {
            return data == null ? 0 : data.length;
        }

     
        @Override
        public synchronized int[] getCore() {
            return data;
        }
        
        @Override
        public synchronized void setData(int[] image) {
            this.data = image;
            recordNewData("int[] (no copy)");
        }

        @Override
        public final Number get(int i) {
            return data[i];
        }

        @Override
        public final void set(int i, Number value) {
            data[i] = value.intValue();
        }

        @Override
        public final void add(int i, Number value) {
           data[i] += value.intValue();
        }
           
 
        
    }
    
    
    public static class Short1D extends Samples1D {
        /**
         * 
         */
        private static final long serialVersionUID = 6435456779270257246L;
        
        private short[] data;


        @Override
        public Class<? extends Number> getElementType() {
            return Short.class;
        }

        @Override
        protected synchronized void setDataSize(int size) {
            if(size <= 0) data = null;
            else data = new short[size];
        }

        @Override
        public final int size() {
            return data == null ? 0 : data.length;
        }

        @Override
        public synchronized short[] getCore() {
            return data;
        }
        
        @Override
        public synchronized void setData(short[] image) {
            this.data = image;
            recordNewData("short[] (no copy)");
        }

        @Override
        public final Number get(int i) {
            return data[i];
        }

        @Override
        public final void set(int i, Number value) {
            data[i] = value.shortValue();
        }
        
        @Override
        public final void add(int i, Number value) {
           data[i] += value.shortValue();
        }
        
    }
    
    
    
    public static class Byte1D extends Samples1D {
        /**
         * 
         */
        private static final long serialVersionUID = -20394547035236222L;
        
        private byte[] data;


        @Override
        public Class<? extends Number> getElementType() {
            return Byte.class;
        }

        @Override
        protected synchronized void setDataSize(int size) {
            if(size <= 0) data = null;
            else data = new byte[size];
        }

        @Override
        public final int size() {
            return data == null ? 0 : data.length;
        }

        @Override
        public  synchronized byte[] getCore() {
            return data;
        }
        
        @Override
        public synchronized void setData(byte[] image) {
            this.data = image;
            recordNewData("byte[] (no copy)");
        }

        @Override
        public Number get(int i) {
            return data[i];
        }

        @Override
        public void set(int i, Number value) {
            data[i] = value.byteValue();
        }
        
        @Override
        public void add(int i, Number value) {
           data[i] += value.byteValue();
        }

       
    }
    

    
}
