/* *****************************************************************************
 * Copyright (c) 2016 Attila Kovacs <attila[AT]sigmyne.com>.
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

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Set;

import jnum.fits.FitsHeaderEditing;
import jnum.fits.FitsToolkit;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;
import nom.tam.util.Cursor;


public abstract class FlagSpace<Type extends Number> implements Serializable, FitsHeaderEditing {
    /** */
    private static final long serialVersionUID = -1742300725746047436L;

    private String name;

    private Flag<Type> unknownFlag;

    private Hashtable<Type, Flag<Type>> values = new Hashtable<>();

    private Hashtable<Character, Flag<Type>> codes = new Hashtable<>();

    private Hashtable<String, Flag<Type>> names = new Hashtable<>();
    

    private FlagSpace(String name) throws FlagConflictException {
        if(registry.containsKey(name))
            throw new FlagConflictException("A " + getClass().getSimpleName() + " already exists for '" + name + "'.");
       
        this.name = name;
        registry.put(name, this);
        
        unknownFlag = createFlag(1L<<(getBits() - 1), '?', "Unknown");
    }
    
    
    protected abstract Flag<Type> createFlag(long value, char letterCode, String name);

    @Override
    public int hashCode() { return name.hashCode(); }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof FlagSpace)) return false;

        FlagSpace<?> f = (FlagSpace<?>) o;
        if(!f.name.equals(name)) return false;
        return true;
    }
    

    public final String getName() { return name; }
    
    public abstract int getBits();
    
    public abstract long getMask();

    @Override
    public String toString() { return getClass().getSimpleName() + "(" + name + ")"; }
    

    public synchronized void put(Flag<Type> flag) throws FlagConflictException {
        if(values.containsKey(flag.value())) 
            throw new FlagConflictException("Flag value " + flag.toHexString() + "already in use by " + values.get(flag.value()) + ".");
        if(codes.containsKey(flag.letterCode())) 
            throw new FlagConflictException("Flag letter code'" + flag.letterCode() + "'already in use by " + codes.get(flag.letterCode()) + ".");
        if(names.containsKey(flag.name())) 
            throw new FlagConflictException("Flag name '" + flag.name() + "' already in use.");
        
        
        values.put(flag.value(), flag);
        codes.put(flag.letterCode(), flag);
        names.put(flag.name(), flag);
    }
    

    public final boolean contains(long value) {
        return values.containsKey(value);
    }
    

    public final boolean contains(char letterCode) {
        return codes.containsKey(letterCode);
    }
    

    public final boolean contains(String name) {
        return names.containsKey(name);
    }
    

    public final Flag<Type> get(Type value) {
        return values.get(value);
    }

    
    public final Flag<Type> get(char letterCode) {
        return codes.get(letterCode);
    }
    

    public final Flag<Type> get(String name) {
        return names.get(name);
    }
    
    
    public Type parse(String text) {
        try { return decode(text); }
        catch(NumberFormatException e) { return parseLetterCodes(text); }
    }
    

    public abstract Type decode(String text);
    

    public abstract Type parseLetterCodes(String text);
    

    public long parseLongLetterCodes(String text) {
        long lvalue = 0L;
       
        for(int i=text.length(); --i >= 0; ) {
            char c = text.charAt(i);
            switch(c) {
            case ' ':
            case '\t':
            case '\r':
            case '\n':
            case '0':
            case '.':
            case '-': continue;
            default: 
                Flag<Type> flag = codes.get(c);
                lvalue |= flag == null ? unknownFlag.value().longValue() : flag.value().longValue();
            }
        }
        return lvalue;
    }
    

    public String toString(int flag) {
        StringBuffer buf = new StringBuffer();
        
        for(int bit = 0; bit<getBits(); bit++) {
            long lValue = 1L<<bit;
            Type value = getValue(lValue);
            if((flag & lValue) != 0) buf.append(values.containsKey(value) ? values.get(value).letterCode() : unknownFlag.letterCode());
        }
        
        return buf.length() > 0 ? new String(buf) : "-";
    }
    

    public abstract Type getValue(long lValue);
    

    public final Set<Type> getValues() { return values.keySet(); }
    

    public final Set<String> getNames() { return names.keySet(); }
    

    public final Set<Character> getLetterCodes() { return codes.keySet(); }


    public final FlagBlock<Type> getFlagBlock(int startBit, int endBit) {
        return new FlagBlock<>(this, startBit, endBit);
    }
    

    public final FlagBlock<Type> getDefaultFlagBlock() {
        return new FlagBlock<>(this, 0, getBits()-1);
    }
    

    public void editHeader(Header header, char id) throws HeaderCardException {
        editHeader(header, id + "");
    }
    

    @Override
    public void editHeader(Header header) throws HeaderCardException {
        editHeader(header, "");
    }
    

    protected void editHeader(Header header, String id) throws HeaderCardException {
        Cursor<String, HeaderCard> c = FitsToolkit.endOf(header);
        id = id.toUpperCase();
        for(int bit = 0; bit < getBits(); bit++) {
            Type value = getValue(1L<<bit);
            if(values.containsKey(value)) c.add(new HeaderCard(id + "FLAG" + bit, values.get(value).name(), name + " bit " + bit));
        }
    }
    

    static FlagSpace<?> forName(String name) {
        return registry.get(name);
    }

    private static Hashtable<String, FlagSpace<?>> registry = new Hashtable<>();
  

    public static class Byte extends FlagSpace<java.lang.Byte> {
        /** */
        private static final long serialVersionUID = -6767408270247285330L;


        public Byte(String name) throws IllegalArgumentException {
            super(name);
        }

        @Override
        public Flag<java.lang.Byte> createFlag(long value, char letterCode, String name) {
            return new Flag.Byte(this, (byte) value, letterCode, name);
        }

        @Override
        public long getMask() {
           return 0xFF;
        }

        @Override
        public java.lang.Byte decode(String text) {
            return java.lang.Byte.decode(text);
        }

        @Override
        public java.lang.Byte parseLetterCodes(String text) {
            return (byte) parseLongLetterCodes(text);
        }

        @Override
        public final int getBits() {
            return 8;
        }

        @Override
        public java.lang.Byte getValue(long lValue) {
            return (byte) (lValue & getMask());
        }
        
    }
    
    

    public static class Short extends FlagSpace<java.lang.Short> {
        /** */
        private static final long serialVersionUID = 1846041379339852234L;

        public Short(String name) throws IllegalArgumentException {
            super(name);
        }

        @Override
        public Flag<java.lang.Short> createFlag(long value, char letterCode, String name) {
            return new Flag.Short(this, (short) value, letterCode, name);
        }

        @Override
        public long getMask() {
           return 0xFFFF;
        }

        @Override
        public java.lang.Short decode(String text) {
            return java.lang.Short.decode(text);
        }

        @Override
        public java.lang.Short parseLetterCodes(String text) {
            return (short) parseLongLetterCodes(text);
        }

        @Override
        public final int getBits() {
            return 16;
        }

        @Override
        public java.lang.Short getValue(long lValue) {
            return (short) (lValue & getMask());
        }
        
    }
    

    public static class Integer extends FlagSpace<java.lang.Integer> {
        /** */
        private static final long serialVersionUID = -6014042418773380578L;


        public Integer(String name) throws IllegalArgumentException {
            super(name);
        }

        @Override
        public Flag<java.lang.Integer> createFlag(long value, char letterCode, String name) {
            return new Flag.Integer(this, (int) value, letterCode, name);
        }

        @Override
        public long getMask() {
           return ~0L;
        }

        @Override
        public java.lang.Integer decode(String text) {
            return java.lang.Integer.decode(text);
        }

        @Override
        public java.lang.Integer parseLetterCodes(String text) {
            return (int) parseLongLetterCodes(text);
        }

        @Override
        public int getBits() {
            return 32;
        }

        @Override
        public java.lang.Integer getValue(long lValue) {
            return (int) (lValue & getMask());
        }
        
    }
    
    
    public static class Long extends FlagSpace<java.lang.Long> {
        /** */
        private static final long serialVersionUID = -1479352044112682464L;


        public Long(String name) throws IllegalArgumentException {
            super(name);
        }

        @Override
        public Flag<java.lang.Long> createFlag(long value, char letterCode, String name) {
            return new Flag.Long(this, value, letterCode, name);
        }

        @Override
        public long getMask() {
           return ~0L;
        }

        @Override
        public java.lang.Long decode(String text) {
            return java.lang.Long.decode(text);
        }

        @Override
        public java.lang.Long parseLetterCodes(String text) {
            return parseLongLetterCodes(text);
        }

        @Override
        public int getBits() {
            return 64;
        }

        @Override
        public java.lang.Long getValue(long lValue) {
            return lValue;
        }
        
    }
    
}

