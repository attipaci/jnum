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


public class BracketParser extends EnclosureParser {

	@Override
    protected boolean isOpening(char c) {
	    switch(c) {
	    case '(' :
	    case '{' :
	    case '[' :
	    case Brackets.doubleLeft :
	    case Brackets.angledLeft :
	    case Brackets.doubleAngledLeft :
	    case Brackets.flattenedLeft :
	    case Brackets.fullwidthCurvedLeft :
	    case Brackets.fullwidthCurlyLeft :
	    case Brackets.fullwidthSquareLeft :
	    case Brackets.fullwidthDoubleLeft :
	    case Brackets.superscriptLeft :
	    case Brackets.subscriptLeft : return true;
	    default : return false;
	    }
	}

	@Override
    protected boolean isClosing(char c) {
        switch(c) {
        case ')' :
        case '}' :
        case ']' : 
        case Brackets.doubleRight :
        case Brackets.angledRight :
        case Brackets.doubleAngledRight :
        case Brackets.flattenedRight :
        case Brackets.fullwidthCurvedRight :
        case Brackets.fullwidthCurlyRight :
        case Brackets.fullwidthSquareRight :
        case Brackets.fullwidthDoubleRight :
        case Brackets.superscriptRight :
        case Brackets.subscriptRight : return true;
        default: return false;
        }   
    }

	@Override
    protected char getCloserFor(char opener) {
	    switch(opener) {
        case '(' : return ')';
        case '{' : return '}';
        case '[' : return ']';
        case Brackets.doubleLeft : return Brackets.doubleRight;
        case Brackets.angledLeft : return Brackets.angledRight;
        case Brackets.doubleAngledLeft : return Brackets.doubleAngledRight;
        case Brackets.flattenedLeft : return Brackets.flattenedRight;
        case Brackets.fullwidthCurvedLeft : return Brackets.fullwidthCurvedRight;
        case Brackets.fullwidthCurlyLeft : return Brackets.fullwidthCurlyRight;
        case Brackets.fullwidthSquareLeft : return Brackets.fullwidthSquareRight;
        case Brackets.fullwidthDoubleLeft : return Brackets.fullwidthDoubleRight;
        case Brackets.superscriptLeft : return Brackets.superscriptLeft;
        case Brackets.subscriptLeft : return Brackets.subscriptRight;
        default : return (char) -1;
        }
	}
	

}
