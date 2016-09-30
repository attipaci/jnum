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

import java.text.ParseException;
import java.text.ParsePosition;
import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The Class EnclosureParser.
 */
public abstract class EnclosureParser {
  
    /** The auto close. */
    public boolean autoClose = true;
    
    /**
     * Parses the.
     *
     * @param text the text
     * @param pos the pos
     * @return the string
     * @throws ParseException the parse exception
     */
    public String parse(String text, ParsePosition pos) throws ParseException {
        ArrayList<Character> expectedClosing = new ArrayList<Character>();
        
        // Skip to the first bracket.
        int i = pos.getIndex();
        int N = text.length();
        for( ; i<N; i++) if(!isEmptySpace(text.charAt(i))) break;
        if(i >= N) return null;
        
        char c = text.charAt(i++);
        if(!isOpening(c)) throw new ParseException("string not enclosed.", i);
        
        char expecting = getCloserFor(c);
        expectedClosing.add(expecting); 
        int from = i;
        
        boolean isEscaped = false;
        
        for( ; i<N; i++) {
            if(isEscaped) continue;
            
            c = text.charAt(i);
            
            if(c == expecting) {
                expectedClosing.remove(expectedClosing.size() - 1);
                if(expectedClosing.isEmpty()) {
                    pos.setIndex(i+1);
                    return text.substring(from, i);
                }
                expecting = expectedClosing.get(expectedClosing.size() - 1);
            }
            else if(c == '\\') isEscaped = true;
            else if(isOpening(c)) expectedClosing.add(getCloserFor(c));
            else if(isClosing(c)) throw new ParseException("Unexpected " + c + ".", i);
        }
        
        pos.setIndex(text.length());
        
        String closers = "";
        if(autoClose) if(!expectedClosing.isEmpty()) {
            StringBuffer buf = new StringBuffer();
            for(int k=expectedClosing.size(); --k >= 0; ) buf.append(expectedClosing.remove(k));
            closers = new String(buf);
        }
        
        return text.substring(from) + closers;
    }
    
    /**
     * Checks if is empty space.
     *
     * @param c the c
     * @return true, if is empty space
     */
    private static boolean isEmptySpace(char c) {
        switch(c) {
        case ' ' :
        case '\t' :
        case '\n' :
        case '\r' : return true;
        default: return false;
        }
    }
    
    /**
     * Checks if is opening.
     *
     * @param c the c
     * @return true, if is opening
     */
    protected abstract boolean isOpening(char c);
    
    /**
     * Checks if is closing.
     *
     * @param c the c
     * @return true, if is closing
     */
    protected abstract boolean isClosing(char c);
    
    /**
     * Gets the closer for.
     *
     * @param c the c
     * @return the closer for
     */
    protected abstract char getCloserFor(char c);
    
}
