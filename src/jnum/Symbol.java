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

package jnum;

/**
 * A selection of commonly used unicode character symbols, especially for mathermatics, physics and astronomy.
 * 
 *  For more symbols, check out {@link jnum.text.GreekLetter} and {@link jnum.text.Brackets}, {@jnum.textQuotationMarks}.
 * 
 * 
 * @author Attila Kovacs
 * 
 * @see jnum.text.GreekLetter
 * @see jnum.text.Brackets
 * @see jnum.text.QuotationMarks
 * @see jnum.text.Subscript
 * @see jnum.text.Superscript
 *
 */
public final class Symbol {

	// Numbers
	/** Part per thousand */
	public final static char permil = '\u0089';
	
	/** 1/4. */
	public final static char quarter = '\u00bc';
    
    /** 1/2. */
    public final static char half = '\u00bd';
    
    /** 1/3. */
    public final static char third  ='\u00be';
	
    
	// Business
	/** trademark. */
	public final static char trademark = '\u0099';
	
	/** copyright. */
	public final static char copyright = '\u00a9';
    
    /** circled R. */
    public final static char circleR = '\u00ae'; 
	
	// Currencies
    /** currency: euro. */
	public final static char euro = '\u0080';
    
    /** currency: cent. */
    public final static char cent = '\u00a2';
    
    /** currency: pound. */
    public final static char pound = '\u00a3';
    
    /** currency: yen. */
    public final static char yen = '\u00a5';
    
    
    // typographical
    /** paragraph marker. */
    public final static char paragraph = '\u00a7';
    
 
    
    /** ellipses (...). */
    public final static char ellipses = '\u0085';
     
    /** script 'f'. */
    public final static char scriptf = '\u0083';    
    
    /** dagger. */
    public final static char dagger = '\u0086';
    
    /** double dagger. */
    public final static char doubleDagger = '\u0087';
    
    /** The combined upper-case letter 'CE'. */
    public final static char CE = '\u008c';
    
    /** The combined lower-case letter 'ce'. */
    public final static char ce = '\u009c';
    
    /** The combined upper-case letter 'AE'. */
    public final static char AE = '\u00c6';
    
    /** The combined lower-case letter 'ae'. */
    public final static char ae = '\u00e6';
    
    /** inverted exclamation mark. */
    public final static char invertedExclamation = '\u00a1';
    
    /** inverted question mark. */
    public final static char invertedQuestionMark = '\u00bf';
    
    
     
    
    // Math
    
    /** square root. */
    public final static char squareRoot = '\u221a';
    
    /** cubic root. */
    public final static char cubicRoot = '\u221b';
    
    /** fourth root. */
    public final static char fourthRoot = '\u221c';
  
    
    /** plus or minus (+/-) */
    public final static char plusminus = '\u00b1';
    
    /** minus or plus (-/+) */
    public final static char minusplus = '\u2213';
    
    /** Upper minus */
    public final static char upperminus = '\u00af';
    
    /** Square, i.e. superscript 2 */
    public final static char square = '\u00b2';
    
    /** Cube, i.e. superscript 3. */
    public final static char cube = '\u00b3';
    
    /** dot product. */
    public final static char dot = '\u00b7';
    
    /** big dot. */
    public final static char bigdot = '\u0095';
    
    /** double dot. */
    public final static char doubledot = '\u00a8';
    
    /** cross product. */
    public final static char cross = '\u00d7';
    
    /** times. Same as cross. */
    public final static char times = '\u00d7';
    
    /** infinity. */
    public final static char infinity = '\u221e';
    
    /** circled plus. */
    public final static char circlePlus = '\u2295';
    
    /** circled minus. */
    public final static char circleMinus = '\u2296';
    
    /** circled cross product (e.g. external direct product). */
    public final static char circleTimes = '\u2297';
    
    /** circled division. */
    public final static char circleDivide = '\u2298';
    
    /** circled dot product. */
    public final static char circleDot = '\u2299';
    
    /** star operator */
    public final static char starOperator = '\u22c6';
 
    /** partial derivative. */
    public final static char partial = '\u2202';
    
    /** Nabla. */
    public final static char nabla = '\u2207';
    
    /** integral sign. */
    public final static char integral = '\u222b';
    
    /** double integral signs. */
    public final static char doubleIntegral = '\u222c';
    
    /** triple integral signs. */
    public final static char tripleIntegral = '\u222d';
    
    /** cross product. */
    public final static char crossProduct = '\u2a2f';
    
    /** prime. */
    public final static char prime = '\u2032';
    
    /** double prime. */
    public final static char doublePrime = '\u2033';
    
    /** triple prime. */
    public final static char triplePrime = '\u2034';
    
    /** summation. */
    public final static char summation = '\u2211';
    
    /** product. */
    public final static char product = '\u220f';
    
    /** not equal. */
    public final static char notEqual = '\u2260';
    
    /** proportional to. */
    public final static char proportionalTo = '\u221d';
    
    /** approximately. */
    public final static char approximately = '\u2248';
    
    
    // Physics
    /** degree. */
    public final static char degree = '\u00b0';
    
    /** degree celsius. */
    public final static char celsius = '\u2103';
    
    /** degree farenheit. */
    public final static char farenheit = '\u2109';
    
    /** hbar (Dirac's constant). */
    public final static char hbar = '\u0127';
    
    /** greek letter: mu. */
    public final static char mu = '\u00b5';
    
    /** Circled A, as in Angstrom. */
    public final static char Acircle = '\u00c5';
    
   
           
    // Astronomy
    
    /** astronomy: Sun. */
    public final static char Sun = '\u2609';
    
    /** planet Mercury. */
    public final static char Mercury = '\u263f';
    
    /** planer Venus. */
    public final static char Venus = '\u2640';
    
    /** planet Earth. */
    public final static char Earth = '\u2641';
    
    /** planet Mars. */
    public final static char Mars = '\u2642';
    
    /** planet Jupiter. */
    public final static char Jupiter = '\u2643';
    
    /** planet Saturn. */
    public final static char Saturn = '\u2644';
    
    /** planet Uranus. */
    public final static char Uranus = '\u2645';
    
    /** planet Neptune. */
    public final static char Neptune = '\u2646';
    
    /** planet Pluto. */
    public final static char Pluto = '\u2647';
    
    
}
