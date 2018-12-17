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

import jnum.Symbol;


public class QuoteParser extends EnclosureParser {

    private boolean isGermanStyle = false;
    
    /* (non-Javadoc)
     * @see jnum.text.EnclosureParser#isOpening(char)
     */
    @Override
    protected boolean isOpening(char c) {
        switch(c) {
        case '"' :
        case '\'' : 
        case '`' :
        case QuotationMarks.singleQuote :
        case QuotationMarks.doubleQuote :
        case QuotationMarks.invertedDoubleQuote :
        case QuotationMarks.invertedSingleQuote :
        case QuotationMarks.singleAngleQuoteBegin :
        case QuotationMarks.doubleAngleQuoteBegin :
        case QuotationMarks.doubleAngleQuoteEnd : // Hungarian-style inner quote...
        case QuotationMarks.lowerSingleQuote :
        case QuotationMarks.lowerDoubleQuote :
        case Symbol.invertedExclamation :
        case Symbol.invertedQuestionMark : return true;    
        default : return false;
        }
    }
    
    /* (non-Javadoc)
     * @see jnum.text.EnclosureParser#isClosing(char)
     */
    @Override
    protected boolean isClosing(char c) {
        return false; 
    }
    
    
    /* (non-Javadoc)
     * @see jnum.text.EnclosureParser#getCloserFor(char)
     */
    @Override
    protected char getCloserFor(char opener) {
        switch(opener) {
        case '"' : return '"';
        case '\'' : return '\'';
        case '`' : return '`';
        case QuotationMarks.doubleQuote :
        case QuotationMarks.invertedDoubleQuote : return QuotationMarks.doubleQuote;
        case QuotationMarks.lowerDoubleQuote : return isGermanStyle ? QuotationMarks.invertedDoubleQuote : QuotationMarks.doubleQuote;
        case QuotationMarks.singleQuote : return isGermanStyle ? QuotationMarks.invertedSingleQuote : QuotationMarks.singleQuote;
        case QuotationMarks.invertedSingleQuote : return QuotationMarks.singleQuote;
        case QuotationMarks.lowerSingleQuote : 
        case QuotationMarks.singleAngleQuoteBegin : return QuotationMarks.singleAngleQuoteEnd;
        case QuotationMarks.doubleAngleQuoteBegin : return QuotationMarks.doubleAngleQuoteEnd;
        case QuotationMarks.doubleAngleQuoteEnd : return QuotationMarks.doubleAngleQuoteBegin; // Hungarian-style
        case Symbol.invertedExclamation : return '!';
        case Symbol.invertedQuestionMark : return '?';
        default : return (char) -1;
        }
    }
    

    public boolean isGermanStyle() { return isGermanStyle; }
    

    public void setGermanStyle(boolean value) { isGermanStyle = value; }
    
    
    
}
