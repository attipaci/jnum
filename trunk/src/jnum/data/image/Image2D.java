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

package jnum.data.image;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Map;

import jnum.CopiableContent;
import jnum.Unit;
import jnum.Util;
import jnum.data.image.overlay.Transposed2D;
import jnum.fits.FitsToolkit;
import nom.tam.fits.Fits;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;
import nom.tam.fits.ImageHDU;
import nom.tam.util.Cursor;


/**
 * A generic light-weight 2D image class implementation with FITS I/O support.
 * 
 * 
 * @author pumukli
 *
 */
public abstract class Image2D extends Data2D implements Resizable2D, Serializable, CopiableContent<Image2D> {    
    /**
     * 
     */
    private static final long serialVersionUID = -7200384936045773744L;
     
    private String id;
 
    @Override
    public int hashCode() {
        int hash = super.hashCode();
        if(id != null) hash ^= id.hashCode();
      
        return hash;
    }
    
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof Image2D)) return false;       
        
        Image2D image = (Image2D) o;
        if(!Util.equals(id, image.id)) return false;
      

        return super.equals(o);
    }
    
    

    @Override
    public Image2D copy() {
        return copy(true);
    }

    
    @Override
    public Image2D copy(boolean withContent) {   
        Image2D copy = (Image2D) clone();
        
        if(capacity() > 0) {
            copy.setSize(sizeX(), sizeY());
            if(withContent) copy.paste(this, true);
        }
        
        return copy;
    }
    
    protected void addProprietaryLocalUnits() {
        
    }
    
   
    
    @Override
    public Image2D getEmptyImage() { return copy(false); }
    
    public String getID() { return id; }
    
    public void setID(String id) { this.id = id; }
   
    
    
 
    @Override
    public void setSize(int sizeX, int sizeY) {
        setDataSize(sizeX, sizeY);
        clearHistory();
        addHistory("new size " + getSizeString());
    }
    
    public void destroy() {
        setDataSize(0, 0);
        clearHistory();
    }
    
    protected abstract void setDataSize(int sizeX, int sizeY);

    public abstract Object getData();

    public void setData(final Values2D values) {
        setSize(values.sizeX(), values.sizeY());
        new Fork<Void>() {
            @Override
            protected void process(int i, int j) {
                if(values.isValid(i, j)) set(i, j, values.get(i, j));
                else discard(i, j);
            }
        }.process();
    }
    
    
    public synchronized void setData(Object image) {   
        if(image instanceof double[][]) setData((double[][]) image);
        else if(image instanceof float[][]) setData((float[][]) image);
        else if(image instanceof long[][]) setData((long[][]) image);
        else if(image instanceof int[][]) setData((int[][]) image);
        else if(image instanceof short[][]) setData((short[][]) image);
        else if(image instanceof byte[][]) setData((byte[][]) image);
        else throw new IllegalArgumentException("Cannot set image content to type " + image.getClass().getSimpleName());
    }
    
    
  
    
    public synchronized void setData(final double[][] data) { 
        setSize(data.length, data[0].length);
        new Fork<Void>() {
            @Override
            protected void process(int i, int j) {
                set(i, j, data[i][j]);
            }
        }.process();
        recordNewData("double[][]");
    }


    public synchronized void setData(final float[][] data) {
        setSize(data.length, data[0].length);
        new Fork<Void>() {
            @Override
            protected void process(int i, int j) {
                set(i, j, data[i][j]);
            }
        }.process();
        recordNewData("float[][]");
    }

    public synchronized void setData(final long[][] data) {
        setSize(data.length, data[0].length);
        new Fork<Void>() {
            @Override
            protected void process(int i, int j) {
                set(i, j, data[i][j]);
            }
        }.process();
        recordNewData("long[][]");
    }


    public synchronized void setData(final int[][] data) {
        setSize(data.length, data[0].length);
        new Fork<Void>() {
            @Override
            protected void process(int i, int j) {
                set(i, j, data[i][j]);
            }
        }.process();
        recordNewData("int[][]");
    }

    public synchronized void setData(final short[][] data) {
        setSize(data.length, data[0].length);
        new Fork<Void>() {
            @Override
            protected void process(int i, int j) {
                set(i, j, data[i][j]);
            }
        }.process();
        recordNewData("short[][]");
    }

    public synchronized void setData(final byte[][] data) {
        setSize(data.length, data[0].length);
        new Fork<Void>() {
            @Override
            protected void process(int i, int j) {
                set(i, j, data[i][j]);
            }
        }.process();
        recordNewData("byte[][]");
    }

    public void setRowColData(Object data) {
        Image2D image = null;
        
        if(data instanceof Values2D) {
            setRowColData((Values2D) data);
            return;
        }
        else if(data instanceof double[][][]) image = Image2D.createType(Double.class);
        else if(data instanceof float[][][]) image = Image2D.createType(Float.class);
        else if(data instanceof long[][][]) image = Image2D.createType(Long.class);
        else if(data instanceof int[][][]) image = Image2D.createType(Integer.class);
        else if(data instanceof short[][][]) image = Image2D.createType(Short.class);
        else if(data instanceof byte[][][]) image = Image2D.createType(Byte.class);
        
        if(image != null) image.setData(data);
    }
    
    public void setRowColData(Values2D image) {
        setSize(image.sizeY(), image.sizeX());
        paste(new Transposed2D(image), true);
    }
    
    public Transposed2D getTransposed() {
        return new Transposed2D(this);
    }
    
    public synchronized void transpose() {
        silentNextNewData();
        setRowColData(getData());
        addHistory("transposed");
    }
    
    
    @Override
    public final synchronized Image2D getImage() { return copy(true); }

    @Override
    public synchronized Image2D getImage(Class<? extends Number> elementType, Number blankingValue) {
        if(elementType.equals(this.getElementType())) {
            if(Util.equals(blankingValue, getBlankingValue())) return copy(true);
            Image2D image = copy(false);
            image.paste(this, true);
            return image;
        }
        
        Image2D image = super.getImage(elementType, blankingValue);
        image.id = id;
        
        return image;
    }
    



    public synchronized Image2D getRowColImage(Class<? extends Number> dataType) {
        Image2D result = Image2D.createType(dataType);
        if(result == null) throw new IllegalArgumentException("Unsupported data type: " + dataType.getSimpleName());
        result.setRowColData(getData());
        return result;
    }


    protected synchronized void crop(int imin, int jmin, int imax, int jmax) {
        addHistory("cropped " + imin + "," + jmin + " : " + imax + "," + jmax);
        silentNextNewData();
        setData(getCropped(imin, jmin, imax, jmax).getData());
    }


    public void autoCrop() {
        int[] xRange = getXIndexRange();
        if(xRange == null) return; 

        int[] yRange = getYIndexRange();
        if(yRange == null) return;

        this.crop(xRange[0], yRange[0], xRange[1], yRange[1]);
    }

     
  

    @Override
    protected void editHeader(Header header) throws HeaderCardException {          
        Cursor<String, HeaderCard> c = FitsToolkit.endOf(header);
        if(getID() != null) c.add(new HeaderCard("EXTNAME", getID(), "Content identifier.")); 
        
        super.editHeader(header);     
    }

    @Override
    protected final void parseHeader(Header header) {
        parseHeader(header, null);
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
        setRowColData(hdu.getData().getData());   
        scale(getUnit().value());
    }
  

    public static Image2D createType(Class<? extends Number> type) {       
        if(type.equals(Double.class)) return new Double2D();
        else if(type.equals(Float.class)) return new Float2D();
        else if(type.equals(Long.class)) return new Long2D();
        else if(type.equals(Integer.class)) return new Integer2D();
        else if(type.equals(Short.class)) return new Short2D();
        else if(type.equals(Byte.class)) return new Byte2D();
        else return null;
    }
    
    
    public static Image2D createType(Class<? extends Number> type, int sizeX, int sizeY) {
        Image2D image = createType(type);
        if(image == null) return null;
        image.setSize(sizeX, sizeY);
        return image;
    }
    
    public static Image2D createFrom(final Values2D values) { return createFrom(values, null); }
   
    
    public static Image2D createFrom(final Values2D values, final Number blankingValue) {
        return createFrom(values, blankingValue, values.getElementType());
    }
    
    public static Image2D createFrom(final Values2D values, final Number blankingValue, Class<? extends Number> elementType) {
        final Image2D image = createType(elementType);
        image.setBlankingValue(blankingValue);
        image.setData(values);
        return image;
    }
   
    public static Image2D createBitpixType(int bitpix) {        
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
    
    public static Image2D read(Fits fits, int hduIndex) throws Exception {
        return read(fits, hduIndex, null);
    }
    
    public static Image2D read(Fits fits, int hduIndex, Hashtable<String, Unit> extraUnits) throws Exception {
        ImageHDU hdu = (ImageHDU) fits.getHDU(hduIndex);
        Image2D image = createBitpixType(hdu.getBitPix());
        image.read(hdu, extraUnits);
        return image;
    }


    public static class Double2D extends Image2D {
        /**
         * 
         */
        private static final long serialVersionUID = -3072712180016848106L;
        private double[][] data;

        
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
        protected synchronized void setDataSize(int sizeX, int sizeY) {
            if(sizeX <= 0 || sizeY <= 0) data = null;
            else data = new double[sizeX][sizeY];
        }

        @Override
        public final int sizeX() {
            return data == null ? 0 : data.length;
        }

        @Override
        public final int sizeY() {
            return data == null ? 0 : data[0].length;
        }

        @Override
        public  synchronized double[][] getData() {
            return data;
        }
        
        
        @Override
        public synchronized void setData(double[][] image) {
            this.data = image;
            recordNewData("double[][] (no copy)");
        }

        @Override
        public final Number get(int i, int j) {
            return data[i][j];
        }

        @Override
        public final void set(int i, int j, Number value) {
            data[i][j] = value.doubleValue();
        }
        
        @Override
        public final void add(int i, int j, Number value) {
           data[i][j] += value.doubleValue();
        }
            
        @Override
        public final boolean isValid(Number value) {
            if(Double.isNaN(value.doubleValue())) return false;
            return super.isValid(value);
        }

    }
    
    
    public static class Float2D extends Image2D {
        /**
         * 
         */
        private static final long serialVersionUID = 2228491380717964808L;
        private float[][] data;

        
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
        protected synchronized void setDataSize(int sizeX, int sizeY) {
            if(sizeX <= 0 || sizeY <= 0) data = null;
            else data = new float[sizeX][sizeY];
        }

        @Override
        public final int sizeX() {
            return data == null ? 0 : data.length;
        }

        @Override
        public final int sizeY() {
            return data == null ? 0 : data[0].length;
        }

        @Override
        public synchronized float[][] getData() {
            return data;
        }
        
        @Override
        public synchronized void setData(float[][] image) {
            this.data = image;
            recordNewData("float[][] (no copy)");
        }

        @Override
        public final Number get(int i, int j) {
            return data[i][j];
        }

        @Override
        public final void set(int i, int j, Number value) {
            data[i][j] = value.floatValue();
        }
        
        @Override
        public final void add(int i, int j, Number value) {
           data[i][j] += value.floatValue();
        }
        
           
        @Override
        public final boolean isValid(Number value) {
            if(Float.isNaN(value.floatValue())) return false;
            return super.isValid(value);
        }


    }
    
    
    public static class Long2D extends Image2D {
        /**
         * 
         */
        private static final long serialVersionUID = 1979862332617630672L;
        private long[][] data;


        @Override
        public Class<? extends Number> getElementType() {
            return Long.class;
        }
       

        @Override
        protected synchronized void setDataSize(int sizeX, int sizeY) {
            if(sizeX <= 0 || sizeY <= 0) data = null;
            else data = new long[sizeX][sizeY];
        }

        @Override
        public final int sizeX() {
            return data == null ? 0 : data.length;
        }

        @Override
        public final int sizeY() {
            return data == null ? 0 : data[0].length;
        }

        @Override
        public synchronized long[][] getData() {
            return data;
        }
        
        @Override
        public synchronized void setData(long[][] image) {
            this.data = image;
            recordNewData("long[][] (no copy)");
        }

        @Override
        public final Number get(int i, int j) {
            return data[i][j];
        }

        @Override
        public final void set(int i, int j, Number value) {
            data[i][j] = value.longValue();
        }
        
        @Override
        public final void add(int i, int j, Number value) {
           data[i][j] += value.longValue();
        }
        
  
    }
    
    
    public static class Integer2D extends Image2D {
        /**
         * 
         */
        private static final long serialVersionUID = -1613251283928531890L;
        private int[][] data;


        @Override
        public Class<? extends Number> getElementType() {
            return Integer.class;
        }

        @Override
        protected synchronized void setDataSize(int sizeX, int sizeY) {
            if(sizeX <= 0 || sizeY <= 0) data = null;
            else data = new int[sizeX][sizeY];
        }

        @Override
        public final int sizeX() {
            return data == null ? 0 : data.length;
        }

        @Override
        public final int sizeY() {
            return data == null ? 0 : data[0].length;
        }

        @Override
        public synchronized int[][] getData() {
            return data;
        }
        
        @Override
        public synchronized void setData(int[][] image) {
            this.data = image;
            recordNewData("int[][] (no copy)");
        }

        @Override
        public final Number get(int i, int j) {
            return data[i][j];
        }

        @Override
        public final void set(int i, int j, Number value) {
            data[i][j] = value.intValue();
        }

        @Override
        public final void add(int i, int j, Number value) {
           data[i][j] += value.intValue();
        }
           
 
        
    }
    
    
    public static class Short2D extends Image2D {
        /**
         * 
         */
        private static final long serialVersionUID = 3468091255612665137L;
        private short[][] data;


        @Override
        public Class<? extends Number> getElementType() {
            return Short.class;
        }

        @Override
        protected synchronized void setDataSize(int sizeX, int sizeY) {
            if(sizeX <= 0 || sizeY <= 0) data = null;
            else data = new short[sizeX][sizeY];
        }

        @Override
        public final int sizeX() {
            return data == null ? 0 : data.length;
        }

        @Override
        public final int sizeY() {
            return data == null ? 0 : data[0].length;
        }

        @Override
        public synchronized short[][] getData() {
            return data;
        }
        
        @Override
        public synchronized void setData(short[][] image) {
            this.data = image;
            recordNewData("short[][] (no copy)");
        }

        @Override
        public final Number get(int i, int j) {
            return data[i][j];
        }

        @Override
        public final void set(int i, int j, Number value) {
            data[i][j] = value.shortValue();
        }
        
        @Override
        public final void add(int i, int j, Number value) {
           data[i][j] += value.shortValue();
        }
        
    }
    
    
    
    public static class Byte2D extends Image2D {
        /**
         * 
         */
        private static final long serialVersionUID = 3696844865717998980L;
        private byte[][] data;


        @Override
        public Class<? extends Number> getElementType() {
            return Byte.class;
        }

        @Override
        protected synchronized void setDataSize(int sizeX, int sizeY) {
            if(sizeX <= 0 || sizeY <= 0) data = null;
            else data = new byte[sizeX][sizeY];
        }

        @Override
        public final int sizeX() {
            return data == null ? 0 : data.length;
        }

        @Override
        public final int sizeY() {
            return data == null ? 0 : data[0].length;
        }

        @Override
        public synchronized byte[][] getData() {
            return data;
        }
        
        @Override
        public synchronized void setData(byte[][] image) {
            this.data = image;
            recordNewData("byte[][] (no copy)");
        }

        @Override
        public Number get(int i, int j) {
            return data[i][j];
        }

        @Override
        public void set(int i, int j, Number value) {
            data[i][j] = value.byteValue();
        }
        
        @Override
        public void add(int i, int j, Number value) {
           data[i][j] += value.byteValue();
        }

        
    }
    
    
    
}
