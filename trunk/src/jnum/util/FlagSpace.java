/*******************************************************************************
 * Copyright (c) 2016 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
 * All rights reserved. 
 * 
 * This file is part of jnum.
 * 
 *     kovacs.util is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     kovacs.util is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with kovacs.util.  If not, see <http://www.gnu.org/licenses/>.
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
 */
public class FlagSpace implements Serializable {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1742300725746047436L;
    
    /** The name. */
    private String name;
    
    /** The bits. */
    private int bits;
    
    private int mask;
   
    /** The unknown flag. */
    private Flag unknownFlag;
      
    /** The values. */
    private Hashtable<Integer, Flag> values = new Hashtable<Integer, Flag>();
    
    /** The codes. */
    private Hashtable<Character, Flag> codes = new Hashtable<Character, Flag>();
    
    /** The names. */
    private Hashtable<String, Flag> names = new Hashtable<String, Flag>();
    
  
    /**
     * Instantiates a new flag space.
     *
     * @param name the name
     * @param numberType the number type
     * @throws IllegalArgumentException the illegal argument exception
     * @throws FlagConflictException the flag conflict exception
     */
    public FlagSpace(String name, Class<? extends Number> numberType) throws IllegalArgumentException, FlagConflictException {
        this(name);
        
        if(numberType.equals(Byte.class)) setBits(8);
        else if(numberType.equals(Short.class)) setBits(16);
        else if(numberType.equals(Integer.class)) setBits(32);
        else if(numberType.equals(Long.class)) setBits(64);
        else if(numberType.equals(Float.class)) setBits(32);
        else if(numberType.equals(Double.class)) setBits(64);
        else throw new IllegalArgumentException("Undefined flag space for class " + numberType.getSimpleName());
    }    
   
    /**
     * Instantiates a new flag space.
     *
     * @param name the name
     * @throws FlagConflictException the flag conflict exception
     */
    private FlagSpace(String name) throws FlagConflictException {
        if(groups.containsKey(name))
            throw new FlagConflictException("A " + getClass().getSimpleName() + " already exists for '" + name + "'.");
       
        this.name = name;
        groups.put(name, this);
    }
    
   
    /**
     * Sets the bits.
     *
     * @param n the new bits
     */
    private void setBits(int n) {
        this.bits = n;
        mask = 0;
        for(int i=bits; --i >= 0; ) mask |= 1L<<i;
        unknownFlag = new Flag(this, 1<<(bits - 1), '?', "Unknown");
    }

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
        FlagSpace f = (FlagSpace) o;
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
    public final int getBits() { return bits; }
    
    /**
     * Gets the mask.
     *
     * @return the mask
     */
    public final int getMask() { return mask; }
    
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
    public synchronized void put(Flag flag) throws FlagConflictException {
        if(values.containsKey(flag.value())) 
            throw new FlagConflictException("Flag value " + Long.toHexString(flag.value()) + "already in use by " + values.get(flag.value()) + ".");
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
    public final Flag get(long value) {
        return values.get(value);
    }
    
    /**
     * Gets the.
     *
     * @param letterCode the letter code
     * @return the flag
     */
    public final Flag get(char letterCode) {
        return codes.get(letterCode);
    }
    
    /**
     * Gets the.
     *
     * @param name the name
     * @return the flag
     */
    public final Flag get(String name) {
        return names.get(name);
    }
    
    /**
     * Parses the.
     *
     * @param text the text
     * @return the int
     */
    public int parse(String text) {
        try { return Integer.decode(text); }
        catch(NumberFormatException e) { return parseLetterCodes(text); }
    }
    
    /**
     * Parses the letter codes.
     *
     * @param text the text
     * @return the int
     */
    public int parseLetterCodes(String text) {
        int value = 0;
       
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
                Flag flag = codes.get(c);
                value |= flag == null ? unknownFlag.value() : flag.value();
            }
        }
        return value;
    }
    
    /**
     * To string.
     *
     * @param flag the flag
     * @return the string
     */
    public String toString(int flag) {
        StringBuffer buf = new StringBuffer();
        
        for(int bit = 0; bit<bits; bit++) {
            long value = 1L<<bit;
            if((flag & value) != 0) buf.append(values.containsKey(value) ? values.get(value).letterCode() : unknownFlag.letterCode());
        }
        
        return buf.length() > 0 ? new String(buf) : "-";
    }
    
    /**
     * Gets the values.
     *
     * @return the values
     */
    public final Set<Integer> getValues() { return values.keySet(); }
    
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
    public final FlagBlock getFlagBlock(int startBit, int endBit) {
        return new FlagBlock(this, startBit, endBit);
    }
    
    /**
     * Gets the default flag block.
     *
     * @return the default flag block
     */
    public final FlagBlock getDefaultFlagBlock() {
        return new FlagBlock(this, 0, bits-1);
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
        for(int bit = 0; bit < bits; bit++) {
            int value = 1<<bit;
            if(values.containsKey(value)) header.addValue(id + "FLAG" + bit, values.get(value).name(), name + " bit " + bit);
        }
    }
    
    /**
     * For name.
     *
     * @param name the name
     * @return the flag space
     */
    static FlagSpace forName(String name) {
        return groups.get(name);
    }
    
    /** The groups. */
    private static Hashtable<String, FlagSpace> groups = new Hashtable<String, FlagSpace>();
  
}

