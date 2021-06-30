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

package jnum.text;

import java.text.ParseException;
import java.text.ParsePosition;
import java.util.ArrayList;


public abstract class EnclosureParser {

    public boolean autoClose = true;
    
    public String parse(String text, ParsePosition pos) throws ParseException {
        ArrayList<Character> expectedClosing = new ArrayList<>();
        
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
    

    private static boolean isEmptySpace(char c) {
        switch(c) {
        case ' ' :
        case '\t' :
        case '\n' :
        case '\r' : return true;
        default: return false;
        }
    }
    

    protected abstract boolean isOpening(char c);
    

    protected abstract boolean isClosing(char c);
    

    protected abstract char getCloserFor(char c);
    
}
