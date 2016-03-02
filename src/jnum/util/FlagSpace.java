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

public class FlagSpace implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -1742300725746047436L;
    private String name;
    private int bits;
   
    private Flag unknownFlag;
      
    private Hashtable<Integer, Flag> values = new Hashtable<Integer, Flag>();
    private Hashtable<Character, Flag> codes = new Hashtable<Character, Flag>();
    private Hashtable<String, Flag> names = new Hashtable<String, Flag>();
    
  
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
    
    public FlagSpace(String name, int bits) throws IllegalArgumentException, FlagConflictException {
        this(name);
        setBits(bits);
    }
    
   
    private FlagSpace(String name) throws FlagConflictException {
        if(groups.containsKey(name))
            throw new FlagConflictException("A " + getClass().getSimpleName() + " already exists for '" + name + "'.");
       
        this.name = name;
        groups.put(name, this);
    }
    
    private void setBits(int n) {
        this.bits = n;
        unknownFlag = new Flag(this, 1<<(bits - 1), '?', "Unknown");
    }
    
    @Override
    public int hashCode() { return super.hashCode() ^ name.hashCode(); }
    
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof FlagSpace)) return false;
        if(!super.equals(o)) return false;
        FlagSpace f = (FlagSpace) o;
        if(!f.name.equals(name)) return false;
        return true;
    }
    
    public final String getName() { return name; }
    
    public final int getBits() { return bits; }
    
    @Override
    public String toString() { return getClass().getSimpleName() + "(" + name + ")"; }
    
    public synchronized void put(Flag flag) throws FlagConflictException {
        if(values.containsKey(flag.value())) 
            throw new FlagConflictException("Flag value " + Integer.toHexString(flag.value()) + "already in use by " + values.get(flag.value()) + ".");
        if(codes.containsKey(flag.letterCode())) 
            throw new FlagConflictException("Flag letter code'" + flag.letterCode() + "'already in use by " + codes.get(flag.letterCode()) + ".");
        if(names.containsKey(flag.name())) 
            throw new FlagConflictException("Flag name '" + flag.name() + "' already in use.");
        
        
        values.put(flag.value(), flag);
        codes.put(flag.letterCode(), flag);
        names.put(flag.name(), flag);
    }
    
    public final Flag get(int value) {
        return values.get(value);
    }
    
    public final Flag get(char letterCode) {
        return codes.get(letterCode);
    }
    
    public final Flag get(String name) {
        return names.get(name);
    }
    
    public int parse(String text) {
        try { return Integer.decode(text); }
        catch(NumberFormatException e) { return parseLetterCodes(text); }
    }
    
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
    
    public String toString(int flag) {
        StringBuffer buf = new StringBuffer();
        
        for(int bit = 0; bit<bits; bit++) {
            int value = 1<<bit;
            if((flag & value) != 0) buf.append(values.containsKey(value) ? values.get(value).letterCode() : unknownFlag.letterCode());
        }
        
        return buf.length() > 0 ? new String(buf) : "-";
    }
    
    public final Set<Integer> getValues() { return values.keySet(); }
    
    public final Set<String> getNames() { return names.keySet(); }
    
    public final Set<Character> getLetterCodes() { return codes.keySet(); }

    public final FlagBlock getFlagBlock(int startBit, int endBit) {
        return new FlagBlock(this, startBit, endBit);
    }
    
    public final FlagBlock getFullFlagBlock() {
        return new FlagBlock(this, 0, bits-1);
    }
    
    public void editHeader(char id, Header header) throws HeaderCardException {
        editHeader(id + "", header);
    }
    
    public void editHeader(Header header) throws HeaderCardException {
        editHeader("", header);
    }
    
    protected void editHeader(String id, Header header) throws HeaderCardException {
        id = id.toUpperCase();
        for(int bit = 0; bit < 32; bit++) {
            int value = 1<<bit;
            if(values.containsKey(value)) header.addValue(id + "FLAG" + bit, values.get(value).name(), name + " bit " + bit);
        }
    }
    
    static FlagSpace forName(String name) {
        return groups.get(name);
    }
    
    private static Hashtable<String, FlagSpace> groups = new Hashtable<String, FlagSpace>();
   
}

