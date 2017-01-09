/*******************************************************************************
 * Copyright (c) 2016 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
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
 *     Attila Kovacs <attila_kovacs[AT]post.harvard.edu> - initial API and implementation
 ******************************************************************************/

package jnum.util;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Set;

import nom.tam.fits.Header;
import nom.tam.fits.HeaderCardException;

// TODO: Auto-generated Javadoc
/**
 * The Class FlagSpace.
 *
 * @param <Type> the generic type
 */
public abstract class FlagSpace<Type extends Number> implements Serializable {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1742300725746047436L;
    
    /** The name. */
    private String name;
   
    /** The unknown flag. */
    private Flag<Type> unknownFlag;
      
    /** The values. */
    private Hashtable<Type, Flag<Type>> values = new Hashtable<Type, Flag<Type>>();
    
    /** The codes. */
    private Hashtable<Character, Flag<Type>> codes = new Hashtable<Character, Flag<Type>>();
    
    /** The names. */
    private Hashtable<String, Flag<Type>> names = new Hashtable<String, Flag<Type>>();
    
  
    /**
     * Instantiates a new flag space.
     *
     * @param name the name
     * @throws FlagConflictException the flag conflict exception
     */
    private FlagSpace(String name) throws FlagConflictException {
        if(registry.containsKey(name))
            throw new FlagConflictException("A " + getClass().getSimpleName() + " already exists for '" + name + "'.");
       
        this.name = name;
        registry.put(name, this);
        
        unknownFlag = createFlag(1L<<(getBits() - 1), '?', "Unknown");
    }
    
    
    /**
     * Creates the flag.
     *
     * @param value the value
     * @param letterCode the letter code
     * @param name the name
     * @return the flag
     */
    protected abstract Flag<Type> createFlag(long value, char letterCode, String name);

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() { return super.hashCode() ^ name.hashCode(); }
    
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof FlagSpace)) return false;
        if(!super.equals(o)) return false;
        FlagSpace<?> f = (FlagSpace<?>) o;
        if(!f.name.equals(name)) return false;
        return true;
    }
    
    /**
     * Gets the name.
     *
     * @return the name
     */
    public final String getName() { return name; }
    
    /**
     * Gets the bits.
     *
     * @return the bits
     */
    public abstract int getBits();
    
    /**
     * Gets the mask.
     *
     * @return the mask
     */
    public abstract long getMask();
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() { return getClass().getSimpleName() + "(" + name + ")"; }
    
    /**
     * Put.
     *
     * @param flag the flag
     * @throws FlagConflictException the flag conflict exception
     */
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
    
    /**
     * Contains.
     *
     * @param value the value
     * @return true, if successful
     */
    public final boolean contains(long value) {
        return values.containsKey(value);
    }
    
    /**
     * Contains.
     *
     * @param letterCode the letter code
     * @return true, if successful
     */
    public final boolean contains(char letterCode) {
        return codes.containsKey(letterCode);
    }
    
    /**
     * Contains.
     *
     * @param name the name
     * @return true, if successful
     */
    public final boolean contains(String name) {
        return names.containsKey(name);
    }
    
    /**
     * Gets the.
     *
     * @param value the value
     * @return the flag
     */
    public final Flag<Type> get(Type value) {
        return values.get(value);
    }
    
    /**
     * Gets the.
     *
     * @param letterCode the letter code
     * @return the flag
     */
    public final Flag<Type> get(char letterCode) {
        return codes.get(letterCode);
    }
    
    /**
     * Gets the.
     *
     * @param name the name
     * @return the flag
     */
    public final Flag<Type> get(String name) {
        return names.get(name);
    }
    
    /**
     * Parses the.
     *
     * @param text the text
     * @return the int
     */
    public Type parse(String text) {
        try { return decode(text); }
        catch(NumberFormatException e) { return parseLetterCodes(text); }
    }
    
    /**
     * Decode.
     *
     * @param text the text
     * @return the type
     */
    public abstract Type decode(String text);
    
    /**
     * Parses the letter codes.
     *
     * @param text the text
     * @return the type
     */
    public abstract Type parseLetterCodes(String text);
    
    /**
     * Parses the letter codes.
     *
     * @param text the text
     * @return the flag value as a 64-bit long.
     */
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
    
    /**
     * To string.
     *
     * @param flag the flag
     * @return the string
     */
    public String toString(int flag) {
        StringBuffer buf = new StringBuffer();
        
        for(int bit = 0; bit<getBits(); bit++) {
            long lValue = 1L<<bit;
            Type value = getValue(lValue);
            if((flag & lValue) != 0) buf.append(values.containsKey(value) ? values.get(value).letterCode() : unknownFlag.letterCode());
        }
        
        return buf.length() > 0 ? new String(buf) : "-";
    }
    
    /**
     * Gets the value.
     *
     * @param lValue the l value
     * @return the value
     */
    public abstract Type getValue(long lValue);
    
    /**
     * Gets the values.
     *
     * @return the values
     */
    public final Set<Type> getValues() { return values.keySet(); }
    
    /**
     * Gets the names.
     *
     * @return the names
     */
    public final Set<String> getNames() { return names.keySet(); }
    
    /**
     * Gets the letter codes.
     *
     * @return the letter codes
     */
    public final Set<Character> getLetterCodes() { return codes.keySet(); }

    /**
     * Gets the flag block.
     *
     * @param startBit the start bit
     * @param endBit the end bit
     * @return the flag block
     */
    public final FlagBlock<Type> getFlagBlock(int startBit, int endBit) {
        return new FlagBlock<Type>(this, startBit, endBit);
    }
    
    /**
     * Gets the default flag block.
     *
     * @return the default flag block
     */
    public final FlagBlock<Type> getDefaultFlagBlock() {
        return new FlagBlock<Type>(this, 0, getBits()-1);
    }
    
    /**
     * Edits the header.
     *
     * @param id the id
     * @param header the header
     * @throws HeaderCardException the header card exception
     */
    public void editHeader(char id, Header header) throws HeaderCardException {
        editHeader(id + "", header);
    }
    
    /**
     * Edits the header.
     *
     * @param header the header
     * @throws HeaderCardException the header card exception
     */
    public void editHeader(Header header) throws HeaderCardException {
        editHeader("", header);
    }
    
    /**
     * Edits the header.
     *
     * @param id the id
     * @param header the header
     * @throws HeaderCardException the header card exception
     */
    protected void editHeader(String id, Header header) throws HeaderCardException {
        id = id.toUpperCase();
        for(int bit = 0; bit < getBits(); bit++) {
            Type value = getValue(1L<<bit);
            if(values.containsKey(value)) header.addValue(id + "FLAG" + bit, values.get(value).name(), name + " bit " + bit);
        }
    }
    
    /**
     * For name.
     *
     * @param name the name
     * @return the flag space
     */
    static FlagSpace<?> forName(String name) {
        return registry.get(name);
    }
    
    /** The groups. */
    private static Hashtable<String, FlagSpace<?>> registry = new Hashtable<String, FlagSpace<?>>();
  
    /**
     * The Class Byte.
     */
    public static class Byte extends FlagSpace<java.lang.Byte> {
        
        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = -6767408270247285330L;

        /**
         * Instantiates a new byte.
         *
         * @param name the name
         * @throws IllegalArgumentException the illegal argument exception
         * @throws FlagConflictException the flag conflict exception
         */
        public Byte(String name) throws IllegalArgumentException, FlagConflictException {
            super(name);
        }

        /* (non-Javadoc)
         * @see jnum.util.FlagSpace#createFlag(long, char, java.lang.String)
         */
        @Override
        public Flag<java.lang.Byte> createFlag(long value, char letterCode, String name) {
            return new Flag.Byte(this, (byte) value, letterCode, name);
        }

        /* (non-Javadoc)
         * @see jnum.util.FlagSpace#getMask()
         */
        @Override
        public long getMask() {
           return (long) 0xFF;
        }

        /* (non-Javadoc)
         * @see jnum.util.FlagSpace#decode(java.lang.String)
         */
        @Override
        public java.lang.Byte decode(String text) {
            return java.lang.Byte.decode(text);
        }

        /* (non-Javadoc)
         * @see jnum.util.FlagSpace#parseLetterCodes(java.lang.String)
         */
        @Override
        public java.lang.Byte parseLetterCodes(String text) {
            return (byte) parseLongLetterCodes(text);
        }

        /* (non-Javadoc)
         * @see jnum.util.FlagSpace#getBits()
         */
        @Override
        public final int getBits() {
            return 8;
        }

        /* (non-Javadoc)
         * @see jnum.util.FlagSpace#getValue(long)
         */
        @Override
        public java.lang.Byte getValue(long lValue) {
            return (byte) (lValue & getMask());
        }
        
    }
    
    
    /**
     * The Class Short.
     */
    public static class Short extends FlagSpace<java.lang.Short> {
        
        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = 1846041379339852234L;

        /**
         * Instantiates a new short.
         *
         * @param name the name
         * @throws IllegalArgumentException the illegal argument exception
         * @throws FlagConflictException the flag conflict exception
         */
        public Short(String name) throws IllegalArgumentException, FlagConflictException {
            super(name);
        }

        /* (non-Javadoc)
         * @see jnum.util.FlagSpace#createFlag(long, char, java.lang.String)
         */
        @Override
        public Flag<java.lang.Short> createFlag(long value, char letterCode, String name) {
            return new Flag.Short(this, (short) value, letterCode, name);
        }

        /* (non-Javadoc)
         * @see jnum.util.FlagSpace#getMask()
         */
        @Override
        public long getMask() {
           return (long) 0xFFFF;
        }

        /* (non-Javadoc)
         * @see jnum.util.FlagSpace#decode(java.lang.String)
         */
        @Override
        public java.lang.Short decode(String text) {
            return java.lang.Short.decode(text);
        }

        /* (non-Javadoc)
         * @see jnum.util.FlagSpace#parseLetterCodes(java.lang.String)
         */
        @Override
        public java.lang.Short parseLetterCodes(String text) {
            return (short) parseLongLetterCodes(text);
        }

        /* (non-Javadoc)
         * @see jnum.util.FlagSpace#getBits()
         */
        @Override
        public final int getBits() {
            return 16;
        }

        /* (non-Javadoc)
         * @see jnum.util.FlagSpace#getValue(long)
         */
        @Override
        public java.lang.Short getValue(long lValue) {
            return (short) (lValue & getMask());
        }
        
    }
    
    /**
     * The Class Integer.
     */
    public static class Integer extends FlagSpace<java.lang.Integer> {
        
        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = -6014042418773380578L;

        /**
         * Instantiates a new integer.
         *
         * @param name the name
         * @throws IllegalArgumentException the illegal argument exception
         * @throws FlagConflictException the flag conflict exception
         */
        public Integer(String name) throws IllegalArgumentException, FlagConflictException {
            super(name);
        }

        /* (non-Javadoc)
         * @see jnum.util.FlagSpace#createFlag(long, char, java.lang.String)
         */
        @Override
        public Flag<java.lang.Integer> createFlag(long value, char letterCode, String name) {
            return new Flag.Integer(this, (int) value, letterCode, name);
        }

        /* (non-Javadoc)
         * @see jnum.util.FlagSpace#getMask()
         */
        @Override
        public long getMask() {
           return (long) ~0;
        }

        /* (non-Javadoc)
         * @see jnum.util.FlagSpace#decode(java.lang.String)
         */
        @Override
        public java.lang.Integer decode(String text) {
            return java.lang.Integer.decode(text);
        }

        /* (non-Javadoc)
         * @see jnum.util.FlagSpace#parseLetterCodes(java.lang.String)
         */
        @Override
        public java.lang.Integer parseLetterCodes(String text) {
            return (int) parseLongLetterCodes(text);
        }

        /* (non-Javadoc)
         * @see jnum.util.FlagSpace#getBits()
         */
        @Override
        public int getBits() {
            return 32;
        }

        /* (non-Javadoc)
         * @see jnum.util.FlagSpace#getValue(long)
         */
        @Override
        public java.lang.Integer getValue(long lValue) {
            return (int) (lValue & getMask());
        }
        
    }
    
    
    /**
     * The Class Long.
     */
    public static class Long extends FlagSpace<java.lang.Long> {
        
        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = -1479352044112682464L;

        /**
         * Instantiates a new long.
         *
         * @param name the name
         * @throws IllegalArgumentException the illegal argument exception
         * @throws FlagConflictException the flag conflict exception
         */
        public Long(String name) throws IllegalArgumentException, FlagConflictException {
            super(name);
        }

        /* (non-Javadoc)
         * @see jnum.util.FlagSpace#createFlag(long, char, java.lang.String)
         */
        @Override
        public Flag<java.lang.Long> createFlag(long value, char letterCode, String name) {
            return new Flag.Long(this, value, letterCode, name);
        }

        /* (non-Javadoc)
         * @see jnum.util.FlagSpace#getMask()
         */
        @Override
        public long getMask() {
           return ~0L;
        }

        /* (non-Javadoc)
         * @see jnum.util.FlagSpace#decode(java.lang.String)
         */
        @Override
        public java.lang.Long decode(String text) {
            return java.lang.Long.decode(text);
        }

        /* (non-Javadoc)
         * @see jnum.util.FlagSpace#parseLetterCodes(java.lang.String)
         */
        @Override
        public java.lang.Long parseLetterCodes(String text) {
            return parseLongLetterCodes(text);
        }

        /* (non-Javadoc)
         * @see jnum.util.FlagSpace#getBits()
         */
        @Override
        public int getBits() {
            return 64;
        }

        /* (non-Javadoc)
         * @see jnum.util.FlagSpace#getValue(long)
         */
        @Override
        public java.lang.Long getValue(long lValue) {
            return lValue;
        }
        
    }
    
}

