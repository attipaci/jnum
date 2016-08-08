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

// TODO: Auto-generated Javadoc
/**
 * The Class TextWrapper.
 */
public class TextWrapper {
    
    /** The width. */
    private int width;
    
    /** The white spaces. */
    private String whiteSpaces;
    
    /** The breakables before. */
    private String breakablesBefore;
    
    /** The breakables after. */
    private String breakablesAfter;
    
    
    /**
     * Instantiates a new text wrapper.
     *
     * @param width the number of characters available before wrapping is required.
     */
    public TextWrapper(int width) {
        this(width, "", "");
    }
    
    /**
     * Instantiates a new text wrapper.
     *
     * @param width the number of characters available before wrapping is required.
     * @param breakBefore the list of characters before which a line
     * @param breakAfter the break after
     */
    public TextWrapper(int width, String breakBefore, String breakAfter) {
        this(width, " \t", breakBefore, breakAfter);
    }
  
    /**
     * Instantiates a new text wrapper.
     *
     * @param width the number of characters available before wrapping is required.
     * @param whiteSpaces the white spaces
     * @param breakBefore the break before
     * @param breakAfter the break after
     */
    public TextWrapper(int width, String whiteSpaces, String breakBefore, String breakAfter) {
        setWidth(width);
        setWhiteSpaces(whiteSpaces);
        setBreakBefore(breakBefore);
        setBreakAfter(breakAfter);
    }
    
    /**
     * Sets the number of characters available before wrapping is required.
     *
     * @param n the new width
     */
    public void setWidth(int n) { this.width = n; }
    
    /**
     * Gets the number of characters available before wrapping is required.
     *
     * @return the width
     */
    public int getWidth() { return width; }
    
    /**
     * Sets the list of characters, beyond the white space characters, before which a line can be broken (wrapped). 
     * For example, you can set the "({[" to allow wrapping lines before opening brackets as well as white spaces.
     *
     * @param chars the new break before
     */
    public void setBreakBefore(String chars) { breakablesBefore = chars; }
    
    /**
     * Gets the list of characters, beyond the white space characters, before which a line can be broken (wrapped).
     *
     * @return the list of non-whitespace characters before which a line can be wrapped.
     */
    public String getBreakBefore() { return breakablesBefore; }

    /**
     * Sets the list of characters, beyond the white space characters, before which a line can be broken (wrapped)
     * For example, you can set the ")}]" to allow wrapping lines after closing brackets as well as white spaces.
     *
     * @param chars the new break after
     */
    public void setBreakAfter(String chars) { breakablesAfter = chars; }
    
    /**
     * Gets the list of characters, beyond the white space characters, after which a line can be broken (wrapped).
     *
     * @return the list of non-whitespace characters before which a line can be wrapped.
     */
    public String getBreakAfter() { return breakablesAfter; }
    
    /**
     * Sets the white space characters. Lines can always be wrapped around white spaces, and white spaces can be
     * ignored at the beginning of new lines.
     *
     * @param chars the list of new white space characters
     */
    public void setWhiteSpaces(String chars) { whiteSpaces = chars; }
    
    /**
     * Gets the white space characters.
     *
     * @return the list of white space characters.
     */
    public String getWhiteSpaces() { return whiteSpaces; }
     
    
    /**
     * Wrap the specified input text.
     *
     * @param text the input text
     * @return the text after wrapping.
     */
    public String wrap(String text) {
        return wrap(text, 0);
    }

    /**
     * Wrap.
     *
     * @param text the input text
     * @param lineHeader the line header for all output lines.
     * @return the string
     */
    public String wrap(String text, String lineHeader) {
        return wrap(text, lineHeader, 0);
    }

    /**
     * Wrap.
     *
     * @param text the text
     * @param indent the indentation for wrapped lines (not the first line).
     * @return the string
     */
    public String wrap(String text, int indent) {
        return wrap(text, "", indent);
    }

    /**
     * Wrap.
     *
     * @param text the text
     * @param lineHeader the line header for all output lines.
     * @param indent the indentation for wrapped lines (not the first line).
     * @return the string
     */
    public String wrap(String text, String lineHeader, int indent) {
      
        // Remove trailing spaces...
        text = Util.trimEnd(text);
        
        if(lineHeader == null) lineHeader = "";    

        if(width < lineHeader.length()) throw new IllegalStateException("Negative wrapping space.");

        // If the text contains a line break, then wrap split...
        if(text.contains("\n")) {
            int i = text.indexOf('\n');
            return wrap(text.substring(0, i), lineHeader, indent) + "\n" 
                + wrap(text.substring(i+1), lineHeader + Util.spaces(indent), 0);
        }

        final int tlength = width - lineHeader.length();
        
        // If the text is shorter than the limit, then use as is..
        if(text.length() < tlength) return lineHeader + text;
        
        // Try to break around a breakable point...
        for(int i=tlength; --i > 0; ) if(isBreakableAt(text, i)) {
            // For wrapping skip ahead to the first non-white space character...
            for(int j=i; j<text.length(); j++) if(!isWhiteSpace(text.charAt(j))) 
                return lineHeader + text.substring(0, i) + "\n" + wrap(text.substring(j), lineHeader + Util.spaces(indent), 0);
            return lineHeader + text.substring(0, i); // We should never reach here, but just in case...
        }

        // If there is no space to wrap around, then just cut mid-word...
        return lineHeader + text.substring(0, tlength) + "\n" + wrap(text.substring(tlength), lineHeader + Util.spaces(indent), 0);
    }
    
    /**
     * Checks if is breakable at.
     *
     * @param text the text
     * @param index the index
     * @return true, if is breakable at
     */
    protected boolean isBreakableAt(String text, int index) {
        if(isBreakableBefore(text.charAt(index))) return true;
        if(index > 0) if(isBreakableAfter(text.charAt(index-1))) return true;
        return false;
    }
    
    /**
     * Contains.
     *
     * @param s the s
     * @param c the c
     * @return true, if successful
     */
    private boolean contains(String s, char c) {
        for(int i = s.length(); --i >= 0; ) if(s.charAt(i) == c) return true;
        return false;
    }
    
    /**
     * Checks if is white space.
     *
     * @param c the c
     * @return true, if is white space
     */
    private boolean isWhiteSpace(char c) { return contains(getWhiteSpaces(), c); }
     
    /**
     * Checks if is breakable before.
     *
     * @param c the c
     * @return true, if is breakable before
     */
    private boolean isBreakableBefore(char c) {
        if(contains(getBreakBefore(), c)) return true;
        return isWhiteSpace(c);
    }

    /**
     * Checks if is breakable after.
     *
     * @param c the c
     * @return true, if is breakable after
     */
    private boolean isBreakableAfter(char c) {
        if(contains(getBreakAfter(), c)) return true;
        return isWhiteSpace(c);
    }
}
