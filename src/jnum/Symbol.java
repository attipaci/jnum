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

package jnum;

/**
 * A selection of commonly used unicode character symbols, especially for mathermatics, physics and astronomy.
 * 
 *  For more symbols, check out {@link jnum.text.GreekLetter} and {@link jnum.text.Brackets}, {@link jnum.text.QuotationMarks}.
 * 
 * 
 * @author Attila Kovacs
 * 
 * @see jnum.text.GreekLetter
 * @see jnum.text.Brackets
 * @see jnum.text.QuotationMarks
 * @see jnum.text.SubScript
 * @see jnum.text.SuperScript
 *
 */
public final class Symbol {

	// Numbers
	/** Part per thousand */
	public static final char permil = '\u0089';
	
	/** 1/4. */
	public static final char quarter = '\u00bc';
    
    /** 1/2. */
    public static final char half = '\u00bd';
    
    /** 1/3. */
    public static final char third  ='\u00be';
	
    
	// Business
	/** trademark. */
	public static final char trademark = '\u0099';
	
	/** copyright. */
	public static final char copyright = '\u00a9';
    
    /** circled R. */
    public static final char circleR = '\u00ae'; 
	
	// Currencies
    /** currency: euro. */
	public static final char euro = '\u0080';
    
    /** currency: cent. */
    public static final char cent = '\u00a2';
    
    /** currency: pound. */
    public static final char pound = '\u00a3';
    
    /** currency: yen. */
    public static final char yen = '\u00a5';
    
    
    // typographical
    /** paragraph marker. */
    public static final char paragraph = '\u00a7';
    
 
    
    /** ellipses (...). */
    public static final char ellipses = '\u0085';
     
    /** script 'f'. */
    public static final char scriptf = '\u0083';    
    
    /** dagger. */
    public static final char dagger = '\u0086';
    
    /** double dagger. */
    public static final char doubleDagger = '\u0087';
    
    /** The combined upper-case letter 'CE'. */
    public static final char CE = '\u008c';
    
    /** The combined lower-case letter 'ce'. */
    public static final char ce = '\u009c';
    
    /** The combined upper-case letter 'AE'. */
    public static final char AE = '\u00c6';
    
    /** The combined lower-case letter 'ae'. */
    public static final char ae = '\u00e6';
    
    /** inverted exclamation mark. */
    public static final char invertedExclamation = '\u00a1';
    
    /** inverted question mark. */
    public static final char invertedQuestionMark = '\u00bf';
    
    
     
    
    // Math
    
    /** square root. */
    public static final char squareRoot = '\u221a';
    
    /** cubic root. */
    public static final char cubicRoot = '\u221b';
    
    /** fourth root. */
    public static final char fourthRoot = '\u221c';
  
    
    /** plus or minus (+/-) */
    public static final char plusminus = '\u00b1';
    
    /** minus or plus (-/+) */
    public static final char minusplus = '\u2213';
    
    /** Upper minus */
    public static final char upperminus = '\u00af';
    
    /** Square, i.e. superscript 2 */
    public static final char square = '\u00b2';
    
    /** Cube, i.e. superscript 3. */
    public static final char cube = '\u00b3';
    
    /** dot product. */
    public static final char dot = '\u00b7';
    
    /** big dot. */
    public static final char bigdot = '\u0095';
    
    /** double dot. */
    public static final char doubledot = '\u00a8';
    
    /** cross product. */
    public static final char cross = '\u00d7';
    
    /** times. Same as cross. */
    public static final char times = '\u00d7';
    
    /** infinity. */
    public static final char infinity = '\u221e';
    
    /** circled plus. */
    public static final char circlePlus = '\u2295';
    
    /** circled minus. */
    public static final char circleMinus = '\u2296';
    
    /** circled cross product (e.g. external direct product). */
    public static final char circleTimes = '\u2297';
    
    /** circled division. */
    public static final char circleDivide = '\u2298';
    
    /** circled dot product. */
    public static final char circleDot = '\u2299';
    
    /** star operator */
    public static final char starOperator = '\u22c6';
 
    /** partial derivative. */
    public static final char partial = '\u2202';
    
    /** Nabla. */
    public static final char nabla = '\u2207';
    
    /** integral sign. */
    public static final char integral = '\u222b';
    
    /** double integral signs. */
    public static final char doubleIntegral = '\u222c';
    
    /** triple integral signs. */
    public static final char tripleIntegral = '\u222d';
    
    /** cross product. */
    public static final char crossProduct = '\u2a2f';
    
    /** prime. */
    public static final char prime = '\u2032';
    
    /** double prime. */
    public static final char doublePrime = '\u2033';
    
    /** triple prime. */
    public static final char triplePrime = '\u2034';
    
    /** summation. */
    public static final char summation = '\u2211';
    
    /** product. */
    public static final char product = '\u220f';
    
    /** not equal. */
    public static final char notEqual = '\u2260';
    
    /** proportional to. */
    public static final char proportionalTo = '\u221d';
    
    /** approximately. */
    public static final char approximately = '\u2248';
    
    
    // Physics
    /** degree. */
    public static final char degree = '\u00b0';
    
    /** degree celsius. */
    public static final char celsius = '\u2103';
    
    /** degree farenheit. */
    public static final char farenheit = '\u2109';
    
    /** hbar (Dirac's constant). */
    public static final char hbar = '\u0127';
    
    /** greek letter: mu. */
    public static final char mu = '\u00b5';
    
    /** Circled A, as in Angstrom. */
    public static final char Acircle = '\u00c5';
    
   
           
    // Astronomy
    
    /** astronomy: Sun. */
    public static final char Sun = '\u2609';
    
    /** planet Mercury. */
    public static final char Mercury = '\u263f';
    
    /** planer Venus. */
    public static final char Venus = '\u2640';
    
    /** planet Earth. */
    public static final char Earth = '\u2641';
    
    /** planet Mars. */
    public static final char Mars = '\u2642';
    
    /** planet Jupiter. */
    public static final char Jupiter = '\u2643';
    
    /** planet Saturn. */
    public static final char Saturn = '\u2644';
    
    /** planet Uranus. */
    public static final char Uranus = '\u2645';
    
    /** planet Neptune. */
    public static final char Neptune = '\u2646';
    
    /** planet Pluto. */
    public static final char Pluto = '\u2647';
    
    
}
