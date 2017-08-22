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

package jnum.data.cube;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Map;

import jnum.CopiableContent;
import jnum.Unit;
import jnum.Util;
import jnum.data.cube.overlay.Transposed3D;
import jnum.fits.FitsToolkit;
import nom.tam.fits.Fits;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;
import nom.tam.fits.ImageHDU;
import nom.tam.util.Cursor;


/**
 * A generic light-weight 3D datacube class implementation with FITS I/O support.
 * 
 * 
 * @author pumukli
 *
 */
public abstract class Cube3D extends Data3D implements Resizable3D, Serializable, CopiableContent<Cube3D> {    
       
    /**
     * 
     */
    private static final long serialVersionUID = 3932177524565875808L;
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
        if(!(o instanceof Cube3D)) return false;       
        
        Cube3D image = (Cube3D) o;
        if(!Util.equals(id, image.id)) return false;
     
        return super.equals(o);
    }
    
    

    @Override
    public Cube3D copy() {
        return copy(true);
    }

    
    @Override
    public Cube3D copy(boolean withContent) {   
        Cube3D copy = (Cube3D) clone();
        
        if(capacity() > 0) {
            copy.setSize(sizeX(), sizeY(), sizeZ());
            if(withContent) copy.paste(this, true);
        }
        
        return copy;
    }
    
    protected void addProprietaryUnits() {
        
    }
    
   
    
    @Override
    public Cube3D getEmptyCube() { return copy(false); }
    
    public String getID() { return id; }
    
    public void setID(String id) { this.id = id; }
   
    
    
 
    @Override
    public void setSize(int sizeX, int sizeY, int sizeZ) {
        setDataSize(sizeX, sizeY, sizeZ);
        clearHistory();
        addHistory("new size " + getSizeString());
    }
    
    public void destroy() {
        setDataSize(0, 0, 0);
        clearHistory();
    }
    
    protected abstract void setDataSize(int sizeX, int sizeY, int sizeZ);

   
    public abstract Object getData();
     
  


    public void setData(final Values3D values) {
        setSize(values.sizeX(), values.sizeY(), values.sizeZ());
        new Fork<Void>() {
            @Override
            protected void process(int i, int j, int k) {
                if(values.isValid(i, j, k)) set(i, j, k, values.get(i, j, k));
                else discard(i, j, k);
            }
        }.process();
    }
    
    
    public void setData(Object image) {   
        if(image instanceof double[][][]) setData((double[][][]) image);
        else if(image instanceof float[][][]) setData((float[][][]) image);
        else if(image instanceof long[][][]) setData((long[][][]) image);
        else if(image instanceof int[][][]) setData((int[][][]) image);
        else if(image instanceof short[][][]) setData((short[][][]) image);
        else if(image instanceof byte[][][]) setData((byte[][][]) image);
        else throw new IllegalArgumentException("Cannot set image content to type " + image.getClass().getSimpleName());
    }
    
    
  
    
    public synchronized void setData(final double[][][] data) { 
        setSize(data.length, data[0].length, data[0][0].length);
        new Fork<Void>() {
            @Override
            protected void process(int i, int j, int k) {
                set(i, j, k, data[i][j][k]);
            }
        }.process();
        recordNewData("double[][][]");
    }


    public synchronized void setData(final float[][][] data) {
        setSize(data.length, data[0].length, data[0][0].length);
        new Fork<Void>() {
            @Override
            protected void process(int i, int j, int k) {
                set(i, j, k, data[i][j][k]);
            }
        }.process();
        recordNewData("float[][][]");
    }

    public synchronized void setData(final long[][][] data) {
        setSize(data.length, data[0].length, data[0][0].length);
        new Fork<Void>() {
            @Override
            protected void process(int i, int j, int k) {
                set(i, j, k, data[i][j][k]);
            }
        }.process();
        recordNewData("long[][][]");
    }


    public synchronized void setData(final int[][][] data) {
        setSize(data.length, data[0].length, data[0][0].length);
        new Fork<Void>() {
            @Override
            protected void process(int i, int j, int k) {
                set(i, j, k, data[i][j][k]);
            }
        }.process();
        recordNewData("int[][][]");
    }

    public synchronized void setData(final short[][][] data) {
        setSize(data.length, data[0].length, data[0][0].length);
        new Fork<Void>() {
            @Override
            protected void process(int i, int j, int k) {
                set(i, j, k, data[i][j][k]);
            }
        }.process();
        recordNewData("short[][][]");
    }

    public synchronized void setData(final byte[][][] data) {
        setSize(data.length, data[0].length, data[0][0].length);
        new Fork<Void>() {
            @Override
            protected void process(int i, int j, int k) {
                set(i, j, k, data[i][j][k]);
            }
        }.process();
        recordNewData("byte[][][]");
    }

    
    public void setTransposedData(Object data) {
        Cube3D cube = null;
        
        if(data instanceof Values3D) {
            setTransposedData((Values3D) data);
            return;
        }
        else if(data instanceof double[][][]) cube = Cube3D.createType(Double.class);
        else if(data instanceof float[][][]) cube = Cube3D.createType(Float.class);
        else if(data instanceof long[][][]) cube = Cube3D.createType(Long.class);
        else if(data instanceof int[][][]) cube = Cube3D.createType(Integer.class);
        else if(data instanceof short[][][]) cube = Cube3D.createType(Short.class);
        else if(data instanceof byte[][][]) cube = Cube3D.createType(Byte.class);
        
        if(cube != null) cube.setData(data);
    }
    
    public void setTransposedData(Values3D image) {
        setSize(image.sizeZ(), image.sizeY(), image.sizeX());
        paste(new Transposed3D(image), true);
    }

   
    
    
    @Override
    public final synchronized Cube3D getCube() { return copy(true); }
    

    protected synchronized void crop(int imin, int jmin, int kmin, int imax, int jmax, int kmax) {
        addHistory("cropped " + imin + "," + jmin + "," + kmin + " : " + imax + "," + jmax + "," + kmax);
        silentNextNewData();
        setData(getCropped(imin, jmin, kmin, imax, jmax, kmax).getData());
    }


    public void autoCrop() {
        int[] xRange = getXIndexRange();
        if(xRange == null) return; 

        int[] yRange = getYIndexRange();
        if(yRange == null) return;
        
        int[] zRange = getZIndexRange();
        if(zRange == null) return;

        this.crop(xRange[0], yRange[0], zRange[0], xRange[1], yRange[1], zRange[1]);
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
        Cube3D c = Cube3D.createBitpixType(hdu.getBitPix());
        c.setData(hdu.getData().getData());   
        setTransposedData(c);   
        scale(getUnit().value());
    }
  

    public static Cube3D createType(Class<? extends Number> type) {       
        if(type.equals(Double.class)) return new Double3D();
        else if(type.equals(Float.class)) return new Float3D();
        else if(type.equals(Long.class)) return new Long3D();
        else if(type.equals(Integer.class)) return new Integer3D();
        else if(type.equals(Short.class)) return new Short3D();
        else if(type.equals(Byte.class)) return new Byte3D();
        else return null;
    }
    
    
    public static Cube3D createType(Class<? extends Number> type, int sizeX, int sizeY, int sizeZ) {
        Cube3D image = createType(type);
        if(image == null) return null;
        image.setSize(sizeX, sizeY, sizeZ);
        return image;
    }
    
    public static Cube3D createFrom(final Values3D values) { return createFrom(values, null); }
   
    
    public static Cube3D createFrom(final Values3D values, final Number blankingValue) {
        return createFrom(values, blankingValue, values.getElementType());
    }
    
    public static Cube3D createFrom(final Values3D values, final Number blankingValue, Class<? extends Number> elementType) {
        final Cube3D image = createType(elementType);
        image.setBlankingValue(blankingValue);
        image.setData(values);
        return image;
    }
   
    public static Cube3D createBitpixType(int bitpix) {        
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
    
    public static Cube3D read(Fits fits, int hduIndex) throws Exception {
        return read(fits, hduIndex, null);
    }
    
    public static Cube3D read(Fits fits, int hduIndex, Hashtable<String, Unit> extraUnits) throws Exception {
        ImageHDU hdu = (ImageHDU) fits.getHDU(hduIndex);
        Cube3D image = createBitpixType(hdu.getBitPix());
        image.read(hdu, extraUnits);
        return image;
    }


    public static class Double3D extends Cube3D {
  
        /**
         * 
         */
        private static final long serialVersionUID = -1851330061955233536L;
        private double[][][] data;

        
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
        protected synchronized void setDataSize(int sizeX, int sizeY, int sizeZ) {
            if(sizeX <= 0 || sizeY <= 0 || sizeZ <= 0) data = null;
            else data = new double[sizeX][sizeY][sizeZ];
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
        public final int sizeZ() {
            return data == null ? 0 : data[0][0].length;
        }

        @Override
        public synchronized double[][][] getData() {
            return data;
        }
        
        
        @Override
        public synchronized void setData(double[][][] image) {
            this.data = image;
            recordNewData("double[][][] (no copy)");
        }

        @Override
        public final Number get(int i, int j, int k) {
            return data[i][j][k];
        }

        @Override
        public final void set(int i, int j, int k, Number value) {
            data[i][j][k] = value.doubleValue();
        }
        
        @Override
        public final void add(int i, int j, int k, Number value) {
           data[i][j][k] += value.doubleValue();
        }
            
        @Override
        public final boolean isValid(Number value) {
            if(Double.isNaN(value.doubleValue())) return false;
            return super.isValid(value);
        }

    }
    
    
    public static class Float3D extends Cube3D {
       
        /**
         * 
         */
        private static final long serialVersionUID = 5524602044246595852L;
        private float[][][] data;

        
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
        protected synchronized void setDataSize(int sizeX, int sizeY, int sizeZ) {
            if(sizeX <= 0 || sizeY <= 0 || sizeZ <= 0) data = null;
            else data = new float[sizeX][sizeY][sizeZ];
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
        public final int sizeZ() {
            return data == null ? 0 : data[0][0].length;
        }

        @Override
        public synchronized float[][][] getData() {
            return data;
        }
        
        @Override
        public synchronized void setData(float[][][] image) {
            this.data = image;
            recordNewData("float[][][] (no copy)");
        }

        @Override
        public final Number get(int i, int j, int k) {
            return data[i][j][k];
        }

        @Override
        public final void set(int i, int j, int k, Number value) {
            data[i][j][k] = value.floatValue();
        }
        
        @Override
        public final void add(int i, int j, int k, Number value) {
           data[i][j][k] += value.floatValue();
        }
        
           
        @Override
        public final boolean isValid(Number value) {
            if(Float.isNaN(value.floatValue())) return false;
            return super.isValid(value);
        }


    }
    
    
    public static class Long3D extends Cube3D {
        
        /**
         * 
         */
        private static final long serialVersionUID = -1089709320438532730L;
        private long[][][] data;


        @Override
        public Class<? extends Number> getElementType() {
            return Long.class;
        }
       

        @Override
        protected synchronized void setDataSize(int sizeX, int sizeY, int sizeZ) {
            if(sizeX <= 0 || sizeY <= 0 || sizeZ <= 0) data = null;
            else data = new long[sizeX][sizeY][sizeZ];
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
        public final int sizeZ() {
            return data == null ? 0 : data[0][0].length;
        }

        @Override
        public synchronized long[][][] getData() {
            return data;
        }
        
        @Override
        public synchronized void setData(long[][][] image) {
            this.data = image;
            recordNewData("long[][][] (no copy)");
        }

        @Override
        public final Number get(int i, int j, int k) {
            return data[i][j][k];
        }

        @Override
        public final void set(int i, int j, int k, Number value) {
            data[i][j][k] = value.longValue();
        }
        
        @Override
        public final void add(int i, int j, int k, Number value) {
           data[i][j][k] += value.longValue();
        }
        
  
    }
    
    
    public static class Integer3D extends Cube3D {
       
        /**
         * 
         */
        private static final long serialVersionUID = 7901163608821110764L;
        private int[][][] data;


        @Override
        public Class<? extends Number> getElementType() {
            return Integer.class;
        }

        @Override
        protected synchronized void setDataSize(int sizeX, int sizeY, int sizeZ) {
            if(sizeX <= 0 || sizeY <= 0 || sizeZ <= 0) data = null;
            else data = new int[sizeX][sizeY][sizeZ];
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
        public final int sizeZ() {
            return data == null ? 0 : data[0][0].length;
        }

        @Override
        public synchronized int[][][] getData() {
            return data;
        }
        
        @Override
        public synchronized void setData(int[][][] image) {
            this.data = image;
            recordNewData("int[][][] (no copy)");
        }

        @Override
        public final Number get(int i, int j, int k) {
            return data[i][j][k];
        }

        @Override
        public final void set(int i, int j, int k, Number value) {
            data[i][j][k] = value.intValue();
        }

        @Override
        public final void add(int i, int j, int k, Number value) {
           data[i][j][k] += value.intValue();
        }
           
 
   
    }
    
    
    public static class Short3D extends Cube3D {
       
        /**
         * 
         */
        private static final long serialVersionUID = 5345158448122817685L;
        private short[][][] data;


        @Override
        public Class<? extends Number> getElementType() {
            return Short.class;
        }

        @Override
        protected synchronized void setDataSize(int sizeX, int sizeY, int sizeZ) {
            if(sizeX <= 0 || sizeY <= 0 || sizeZ <= 0) data = null;
            else data = new short[sizeX][sizeY][sizeZ];
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
        public final int sizeZ() {
            return data == null ? 0 : data[0][0].length;
        }

        @Override
        public synchronized short[][][] getData() {
            return data;
        }
        
        @Override
        public synchronized void setData(short[][][] image) {
            this.data = image;
            recordNewData("short[][][] (no copy)");
        }

        @Override
        public final Number get(int i, int j, int k) {
            return data[i][j][k];
        }

        @Override
        public final void set(int i, int j, int k, Number value) {
            data[i][j][k] = value.shortValue();
        }
        
        @Override
        public final void add(int i, int j, int k, Number value) {
           data[i][j][k] += value.shortValue();
        }
        
    }
    
    
    
    public static class Byte3D extends Cube3D {
       
        /**
         * 
         */
        private static final long serialVersionUID = -1246950984899249067L;
        private byte[][][] data;


        @Override
        public Class<? extends Number> getElementType() {
            return Byte.class;
        }

        @Override
        protected synchronized void setDataSize(int sizeX, int sizeY, int sizeZ) {
            if(sizeX <= 0 || sizeY <= 0 || sizeZ <= 0) data = null;
            else data = new byte[sizeX][sizeY][sizeZ];
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
        public final int sizeZ() {
            return data == null ? 0 : data[0][0].length;
        }

        @Override
        public synchronized byte[][][] getData() {
            return data;
        }
        
        @Override
        public synchronized void setData(byte[][][] image) {
            this.data = image;
            recordNewData("byte[][][] (no copy)");
        }

        @Override
        public Number get(int i, int j, int k) {
            return data[i][j][k];
        }

        @Override
        public void set(int i, int j, int k, Number value) {
            data[i][j][k] = value.byteValue();
        }
        
        @Override
        public void add(int i, int j, int k, Number value) {
           data[i][j][k] += value.byteValue();
        }

        
    }
    
    
    
}
