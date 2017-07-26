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


public abstract class Samples1D extends Data1D implements Resizable1D {


    public abstract Object getData();
    
    protected abstract void setDataSize(int size);
    

    @Override
    public void setSize(int size) {
        setDataSize(size);
        clearHistory();
        addHistory("new size " + getSizeString());
    }

    public void setData(final Value1D values) {
        setSize(values.size());
        new Fork<Void>() {
            @Override
            protected void processElementAt(int i) {
                if(values.isValid(i)) set(i, values.get(i));
                else discard(i);
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
    
    
   
    public static class Double1D extends Samples1D {
        
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
        public double[] getData() {
            return data;
        }
        
        
        @Override
        public synchronized void setData(double[] image) {
            this.data = image;
            recordNewData("double[] (no copy)");
        }

        @Override
        public final Number get(Integer i) {
            return data[i];
        }

        @Override
        public final void set(Integer i, Number value) {
            data[i] = value.doubleValue();
        }
        
        @Override
        public final void add(Integer i, Number value) {
           data[i] += value.doubleValue();
        }
            
        @Override
        public final boolean isValid(Number value) {
            if(Double.isNaN(value.doubleValue())) return false;
            return super.isValid(value);
        }

    }
    
    
    public static class Float1D extends Samples1D {

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
        public float[] getData() {
            return data;
        }
        
        @Override
        public synchronized void setData(float[] image) {
            this.data = image;
            recordNewData("float[] (no copy)");
        }

        @Override
        public final Number get(Integer i) {
            return data[i];
        }

        @Override
        public final void set(Integer i, Number value) {
            data[i] = value.floatValue();
        }
        
        @Override
        public final void add(Integer i, Number value) {
           data[i] += value.floatValue();
        }
        
           
        @Override
        public final boolean isValid(Number value) {
            if(Float.isNaN(value.floatValue())) return false;
            return super.isValid(value);
        }


    }
    
    
    public static class Long1D extends Samples1D {

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
        public long[] getData() {
            return data;
        }
        
        @Override
        public synchronized void setData(long[] image) {
            this.data = image;
            recordNewData("long[] (no copy)");
        }

        @Override
        public final Number get(Integer i) {
            return data[i];
        }

        @Override
        public final void set(Integer i, Number value) {
            data[i] = value.longValue();
        }
        
        @Override
        public final void add(Integer i, Number value) {
           data[i] += value.longValue();
        }
        
  
    }
    
    
    public static class Integer1D extends Samples1D {

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
        public int[] getData() {
            return data;
        }
        
        @Override
        public synchronized void setData(int[] image) {
            this.data = image;
            recordNewData("int[] (no copy)");
        }

        @Override
        public final Number get(Integer i) {
            return data[i];
        }

        @Override
        public final void set(Integer i, Number value) {
            data[i] = value.intValue();
        }

        @Override
        public final void add(Integer i, Number value) {
           data[i] += value.intValue();
        }
           
 
        
    }
    
    
    public static class Short1D extends Samples1D {

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
        public short[] getData() {
            return data;
        }
        
        @Override
        public synchronized void setData(short[] image) {
            this.data = image;
            recordNewData("short[] (no copy)");
        }

        @Override
        public final Number get(Integer i) {
            return data[i];
        }

        @Override
        public final void set(Integer i, Number value) {
            data[i] = value.shortValue();
        }
        
        @Override
        public final void add(Integer i, Number value) {
           data[i] += value.shortValue();
        }
        
    }
    
    
    
    public static class Byte1D extends Samples1D {

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
        public byte[] getData() {
            return data;
        }
        
        @Override
        public synchronized void setData(byte[] image) {
            this.data = image;
            recordNewData("byte[] (no copy)");
        }

        @Override
        public Number get(Integer i) {
            return data[i];
        }

        @Override
        public void set(Integer i, Number value) {
            data[i] = value.byteValue();
        }
        
        @Override
        public void add(Integer i, Number value) {
           data[i] += value.byteValue();
        }

        
    }
    

    
}
