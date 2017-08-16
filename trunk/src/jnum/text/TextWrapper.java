/*******************************************************************************
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
 *     Attila Kovacs <attila[AT]sigmyne.com> - initial API and implementation
 ******************************************************************************/

package jnum.text;

import java.text.ParsePosition;
import java.util.StringTokenizer;

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
    private String wrapBefore;
    
    /** The breakables after. */
    private String wrapAfter;
    
    /** If wrapping at hyphens is allowed. */
    private boolean isHyphenating = true;
    
    /** If wrapped lines should be justified, i.e. fill the allotted 'width' of characters. */
    private boolean isJustified = false;
        
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
        setWhiteSpaceChars(whiteSpaces);
        setWrapBeforeChars(breakBefore);
        setWrapAfterChars(breakAfter);
    }
    
    /**
     * Sets the number of characters available before wrapping is required.
     *
     * @param n the new width
     * @see #getWidth()
     */
    public void setWidth(int n) { this.width = n; }
    
    /**
     * Gets the number of characters available before wrapping is required.
     *
     * @return the width
     * @see #setWidth(int)
     */
    public int getWidth() { return width; }
    
    
    /**
     * Sets whether hyphenating is enabled. If so, the lines can be wrapped after hyphens, which are defined
     * as '-' characters that are otherwise non-breakable...
     *
     * @param value the new hyphenating
     */
    public void setHyphenating(boolean value) {
        isHyphenating = value;
    }
    
    /**
     * Checks if is hyphenating is enabled.
     *
     * @return true, if wrapping after hyphens is enabled.
     */
    public boolean isHyphenating() {
        return isHyphenating;
    }
    
    /**
     * Sets whether justification is enabled. If so, the lines will be justified to the specified width, by inserting
     * a uniformly distributed spaces between words, with random variations to fill lines fully.
     *
     * @param value the new justification
     */
    public void setJustified(boolean value) {
        isJustified = value;
    }
    
    /**
     * Checks if is justification is enabled.
     *
     * @return true, if justifying lines enabled.
     */
    public boolean isJustified() {
        return isJustified;
    }
    
    
    
    /**
     * Sets the list of characters, beyond the white space characters, before which a line can be wrapped. 
     * For example, you can set the "({[" to allow wrapping lines before opening brackets as well as white spaces.
     *
     * @param chars the new break before
     * @see #getWrapBeforeChars()
     * @see #setWrapAfterChars(String)
     * @see #setWhiteSpaceChars(String)
     */
    public void setWrapBeforeChars(String chars) { wrapBefore = chars; }
    
    /**
     * Gets the list of characters, beyond the white space characters, before which a line can be wrapped.
     *
     * @return the list of non-whitespace characters before which a line can be wrapped.
     * @see #setWrapBeforeChars(String)
     * @see #getWrapAfterChars()
     * @see #getWhiteSpaceChars()
     */
    public String getWrapBeforeChars() { return wrapBefore; }

    /**
     * Sets the list of characters, beyond the white space characters, before which a line can be wrapped
     * For example, you can set the ")}]" to allow wrapping lines after closing brackets as well as white spaces.
     *
     * @param chars the new break after
     * @see #getWrapAfterChars()
     * @see #setWrapBeforeChars(String)
     * @see #setWhiteSpaceChars(String)
     */
    public void setWrapAfterChars(String chars) { wrapAfter = chars; }
    
    /**
     * Gets the list of characters, beyond the white space characters, after which a line can be wrapped.
     *
     * @return the list of non-whitespace characters before which a line can be wrapped.
     * @see #setWrapAfterChars(String)
     * @see #getWrapBeforeChars()
     * @see #getWhiteSpaceChars()
     */
    public String getWrapAfterChars() { return wrapAfter; }
    
    /**
     * Sets the white space characters. Lines can always be wrapped around white spaces, and white spaces can be
     * ignored at the beginning of new lines.
     *
     * @param chars the list of new white space characters
     * @see #getWhiteSpaceChars()
     * @see #setWrapBeforeChars(String)
     * @see #setWrapAfterChars(String)
     */
    public void setWhiteSpaceChars(String chars) { whiteSpaces = chars; }
    
    /**
     * Gets the white space characters.
     *
     * @return the list of white space characters.
     * @see #setWhiteSpaceChars(String)
     * @see #getWrapBeforeChars()
     * @see #getWrapAfterChars()
     */
    public String getWhiteSpaceChars() { return whiteSpaces; }
     
    
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
        ParsePosition pos = new ParsePosition(0);
        text = trimEnd(text);
        if(lineHeader == null) lineHeader = "";
        
        final StringBuffer buf = new StringBuffer(text.length() << 1);
        final String wrappedHeader = lineHeader + Util.spaces(indent);
        
        int i;
        while((i = pos.getIndex()) < text.length()) {
            boolean isWrapped = false;
            if(i > 0) if(text.charAt(i-1) != '\n') isWrapped = true;    
            final String header = isWrapped ? wrappedHeader : lineHeader;
            buf.append(header + wrapNext(text, width - header.length(), isWrapped, pos));
            if(pos.getIndex() < text.length()) buf.append('\n');
        }
        
        return new String(buf);
    }
    
    
    
    /**
     * Get the next wrapped line from the input text, starting at the marked position, and fitting in the allotted 
     * space, and skipping leading spaces as desired.
     *
     * @param text the text
     * @param size the space (number of characters) allowed for the next line.
     * @param skipLeadingSpaces whether to skip over white spaces at the beginning of the line.
     * @param pos the current parse position where the next line begins. The position will be updated to the beginning
     *      of the next line in the text before returning.
     * @return the next line of text that fits within the allotted space.
     */
    public String wrapNext(final String text, final int size, final boolean skipLeadingSpaces, final ParsePosition pos) {
       
        int fromi = pos.getIndex();
        
        // If wrapped line, skip leading spaces...
        if(skipLeadingSpaces) for( ; fromi < text.length(); fromi++) if(!isWhiteSpace(text.charAt(fromi))) break; 
        
        final int toi = Math.min(text.length(), pos.getIndex() + size);
        if(toi < fromi) throw new IllegalStateException("Negative wrapping space.");
        
        // Check if there is a natural line-break before the line length...
        for(int i=fromi; i < toi; i++) if(text.charAt(i) == '\n') {
            pos.setIndex(i+1);
            return text.substring(fromi, i);
        }
        
        // If the remaining text is shorter than then space, then return all of it.
        if(toi == text.length()) {
            pos.setIndex(text.length());
            return text.substring(fromi);
        }
        
        // Try to break around a breakable point...
        for(int i=toi; --i > fromi; ) if(canWrapAt(text, i)) {
            pos.setIndex(i); 
            String line = text.substring(fromi, i);
            return isJustified ? justify(line, size) : line;
        }

        // If there is no space to wrap around, then just cut mid-word...
        pos.setIndex(toi);
        return text.substring(fromi, toi);
        
    }
   
    /**
     * Justify.
     *
     * @param text the text
     * @param size the size
     * @return the string
     */
    private String justify(String text, int size) {
        StringTokenizer tokens = new StringTokenizer(text, whiteSpaces);
        final int words = tokens.countTokens();
        
        // Nothing to do...
        if(words < 2) return text;
        
        final StringBuffer buf = new StringBuffer(size);
        
        int i=0;
        for( ; i<text.length(); i++) if(!isWhiteSpace(text.charAt(i))) break;
         
        buf.append(text.substring(0, i));
        int nonspaces = i;
        for( ; i<text.length(); i++) if(!isWhiteSpace(text.charAt(i))) nonspaces++;
        
        buf.append(tokens.nextToken());
        
        final String regularSpaces = Util.spaces((size - nonspaces) / (words-1));    
        int extraSpaces = (size - nonspaces) % (words-1);
    
        i = words-1;
               
        while(tokens.hasMoreTokens()) {
            boolean addExtraSpace = Math.random() < (double) extraSpaces / i--;
            if(addExtraSpace) extraSpaces--;
            buf.append(regularSpaces + (addExtraSpace ? " " : "") + tokens.nextToken()); 
        }
        
        return new String(buf);
    }
        
    /**
     * Checks if is breakable at the given position.
     *
     * @param text the text
     * @param index the position in the string
     * @return true, if is breakable at the given position.
     * 
     * @see #getWrapBeforeChars()
     * @see #getWrapAfterChars()
     * @see #getWhiteSpaceChars()
     */
    protected boolean canWrapAt(String text, int index) {
        if(canWrapBefore(text.charAt(index))) return true;
        if(index > 0) {
            if(canWrapAfter(text.charAt(index-1))) return true;
            // Allow breaks after a hyphen. A hyphen is a '-' which is not otherwise breakable.
            if(isHyphenating) if(text.charAt(index-1) == '-') 
                if(index > 1) return !canWrapAfter(text.charAt(index-2)); 
        }
        return false;
    }
    
    /**
     * Checks if a string contains a given character.
     *
     * @param s the string
     * @param c the character
     * @return true, if the string contains the character.
     */
    private boolean contains(String s, char c) {
        for(int i = s.length(); --i >= 0; ) if(s.charAt(i) == c) return true;
        return false;
    }
    
    /**
     * Remove the trailing white spaces from a String.
     *
     * @param text the text
     * @return the text with the trailing white spaces removed, or the input string itself it it has no trailing spaces.
     */
    private String trimEnd(String text) {
        int n = text.length();
        for( ; --n >= 0; ) if(!isWhiteSpace(text.charAt(n))) return n == text.length()-1 ? text : text.substring(0, n+1);
        return "";
    }
    
    
    /**
     * Checks if the given character is a white space.
     *
     * @param c the character
     * @return true, if the character is a white space.
     * 
     * @see #getWhiteSpaceChars()
     */
    private boolean isWhiteSpace(char c) { return contains(getWhiteSpaceChars(), c); }
     
    /**
     * Checks if is breakable before the given character.
     *
     * @param c the character
     * @return true, if is breakable before the given character
     * 
     * @see #getWrapBeforeChars()
     */
    private boolean canWrapBefore(char c) {
        if(contains(getWrapBeforeChars(), c)) return true;
        return isWhiteSpace(c);
    }

    /**
     * Checks if text is breakable after the given character.
     *
     * @param c the character
     * @return true, if is breakable after the given character
     * 
     * @see #getWrapAfterChars()
     */
    private boolean canWrapAfter(char c) {
        if(contains(getWrapAfterChars(), c)) return true;
        return isWhiteSpace(c);
    }
    
 
}
