/* *****************************************************************************
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
 *     Attila Kovacs  - initial API and implementation
 ******************************************************************************/

package jnum.text;

import java.text.ParsePosition;

import jnum.Util;

public class StringParser {
    private String source;
    private ParsePosition pos;
    
    
    public StringParser(String source) {
        this(source, new ParsePosition(0));
    }
    
    public StringParser(String source, ParsePosition pos) {
        this.source = source;
        this.pos = pos;
    }
    
    public String getString() { return source; }
    
    public ParsePosition getPosition() { return pos; }
    
    public char peek() { 
        if(pos.getIndex() >= source.length()) return 0;
        return source.charAt(pos.getIndex());
    }
    
    public void skip(int n) {
        if(n < 0) throw new IllegalStateException("negative skip argument");
        pos.setIndex(Math.min(source.length(), pos.getIndex() + n));
    }
    
    public int getIndex() { return pos.getIndex(); }
    
    public void setIndex(int index) { pos.setIndex(index); }
    
    public void skipWhiteSpaces() {
        skipChars(Util.getWhiteSpaceChars());
    }
    
    public void skipChars(CharSequence chars) {
        int from = pos.getIndex();
         
        for(; from<source.length(); from++) {
            boolean skip = false;
            
            for(int k=chars.length(); --k >=0; ) if(chars.charAt(k) == source.charAt(from)) {
                skip = true;
                break;
            }
          
            if(skip == false) break;
        }
        
        pos.setIndex(from);
    }
    
    public int nextIndexOf(CharSequence chars) {
        int from = pos.getIndex();
        for(; from<source.length(); from++) 
            for(int k=chars.length(); --k >=0; ) if(chars.charAt(k) == source.charAt(from)) return from;
        return -1;
    }
    
    public String nextToken() {
        return nextToken(Util.getWhiteSpaceChars());
    }
    
    public String nextToken(String delim) { 
        skipChars(delim);
        
        int from = pos.getIndex();
        if(pos.getIndex() >= source.length()) return null;
        
        int to = nextIndexOf(delim);
        if(to < 0) to = source.length();
        pos.setIndex(to);
        
        return source.substring(from, to);
    }

   
    public boolean hasMoreTokens() {
        return hasMoreTokens(Util.getWhiteSpaceChars());
    }
    
    public boolean hasMoreTokens(String delim) {
        int i = pos.getIndex();
        
        if(i >= source.length()) return false;
        String token = nextToken(delim);
        
        pos.setIndex(i);
        
        if(token == null) return false;
        if(token.length() == 0) return false;
        
        return true;
    }
    
    
}
