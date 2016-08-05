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

package jnum.text;

import jnum.Util;

public class TextWrapper {
    private int width;
    
    public TextWrapper(int width) {
        setWidth(width);
    }
  
    public void setWidth(int n) { this.width = n; }
    
    public int getWidth() { return width; }
    
    public String wrap(String text) {
        return wrap(text, 0);
    }

    public String wrap(String text, String pretext) {
        return wrap(text, pretext, 0);
    }

    public String wrap(String text, int indent) {
        return wrap(text, "", indent);
    }

    public String wrap(String text, String pretext, int indent) {
      
        // Remove trailing spaces...
        text = Util.trimEnd(text);
        
        if(pretext == null) pretext = "";    

        if(width < pretext.length()) throw new IllegalStateException("Negative wrapping space.");

        // If the text contains a line break, then wrap split...
        if(text.contains("\n")) {
            int i = text.indexOf('\n');
            return wrap(text.substring(0, i), pretext, indent) + "\n" 
                + wrap(text.substring(i+1), pretext + Util.spaces(indent), 0);
        }

        final int tlength = width - pretext.length();
        
        // If the text is shorter than the limit, then use as is..
        if(text.length() < tlength) return pretext + text;
        
        // Try to break around a breakable point...
        for(int i=tlength; --i > 0; ) if(isBreakableAt(text, i)) {
            // For wrapping skip ahead to the first non-white space character...
            for(int j=i; j<text.length(); j++) if(!isWhiteSpace(text.charAt(j))) 
                return pretext + text.substring(0, i) + "\n" + wrap(text.substring(j), pretext + Util.spaces(indent), 0);
            return pretext + text.substring(0, i); // We should never reach here, but just in case...
        }

        // If there is no space to wrap around, then just cut mid-word...
        return pretext + text.substring(0, tlength) + "\n" + wrap(text.substring(tlength), pretext + Util.spaces(indent), 0);

    }
    
    protected boolean isBreakableAt(String text, int index) {
        if(isBreakableBefore(text.charAt(index))) return true;
        if(index > 0) if(isBreakableAfter(text.charAt(index-1))) return true;
        return false;
    }
    
    protected boolean isWhiteSpace(char c) { return Util.isWhiteSpace(c); }
     
    protected boolean isBreakableBefore(char c) {
        switch(c) {
        case '(' :
        case '{' :
        case '[' : return true;
        default: return isWhiteSpace(c);
        }
    }

    protected boolean isBreakableAfter(char c) {
        switch(c) {
        case ')' :
        case '}' :
        case ']' : return true;
        default: return isWhiteSpace(c);
        }
    }
}
