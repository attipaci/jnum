/* *****************************************************************************
 * Copyright (c) 2021 Attila Kovacs <attila[AT]sigmyne.com>.
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
	/** &permil; (per-mil) part per thousand */
	public static final char permil = '\u0089';
	
	/** &frac14; (1/4). */
	public static final char quarter = '\u00bc';
    
    /** &frac12; (1/2). */
    public static final char half = '\u00bd';
	
    /** &frac34; (3/4). */
    public static final char twoThirds  ='\u00be';
    
	// Business
	/** &trade; (trademark). */
	public static final char trademark = '\u0099';
	
	/** &copy; (copyright). */
	public static final char copyright = '\u00a9';
    
    /** &reg; (registered). */
    public static final char circleR = '\u00ae'; 
	
	// Currencies
    /** &euro; (currency: euro). */
	public static final char euro = '\u0080';
    
    /** &cent; (currency: cent). */
    public static final char cent = '\u00a2';
    
    /** &pound; (currency: pound). */
    public static final char pound = '\u00a3';
    
    /** &yen; (currency: yen). */
    public static final char yen = '\u00a5';
    
    
    // typographical
    /** &para; (paragraph marker). */
    public static final char paragraph = '\u00a7';
    
 
    
    /** ellipsis (...). */
    public static final char ellipsis = '\u0085';
     
    /** &fnof; (script 'f'). */
    public static final char scriptf = '\u0083';    
    
    /** &dagger; (dagger). */
    public static final char dagger = '\u0086';
    
    /** &Dagger; (double dagger). */
    public static final char doubleDagger = '\u0087';
    
    /** The combined upper-case letter 'CE'. */
    public static final char CE = '\u008c';
    
    /** The combined lower-case letter 'ce'. */
    public static final char ce = '\u009c';
    
    /** &AElig; (combined upper-case letter 'AE'). */
    public static final char AE = '\u00c6';
    
    /** &aelig; (combined lower-case letter 'ae'). */
    public static final char ae = '\u00e6';
    
    /** inverted exclamation mark. */
    public static final char invertedExclamation = '\u00a1';
    
    /** inverted question mark. */
    public static final char invertedQuestionMark = '\u00bf';
    
    
     
    
    // Math
    
    /** &radic; (square root). */
    public static final char squareRoot = '\u221a';
    
    /** cubic root. */
    public static final char cubicRoot = '\u221b';
    
    /** fourth root. */
    public static final char fourthRoot = '\u221c';
  
    
    /** &plusmn; (plus or minus, +/-) */
    public static final char plusminus = '\u00b1';
    
    /** mnplus; (minus or plus, -/+) */
    public static final char minusplus = '\u2213';
    
    /** Upper minus */
    public static final char upperminus = '\u00af';
    
    /** &sup2; (square or superscript 2) */
    public static final char square = '\u00b2';
    
    /** &sup3; (cube or superscript 3). */
    public static final char cube = '\u00b3';
    
    /** &middot; (dot product). */
    public static final char dot = '\u00b7';
    
    /** big dot. */
    public static final char bigdot = '\u0095';
    
    /** &uml; (double dot). */
    public static final char doubledot = '\u00a8';
    
    /** &times; (cross product). */
    public static final char cross = '\u00d7';
    
    /** &times; (times, or cross product). */
    public static final char times = '\u00d7';
    
    /** &infin; (infinity). */
    public static final char infinity = '\u221e';
    
    /** &oplus; (circled plus). */
    public static final char circlePlus = '\u2295';
    
    /** circled minus. */
    public static final char circleMinus = '\u2296';
    
    /** &otimes; (circled cross product, or external direct product). */
    public static final char circleTimes = '\u2297';
    
    /** circled division. */
    public static final char circleDivide = '\u2298';
    
    /** circled dot product. */
    public static final char circleDot = '\u2299';
    
    /** star operator */
    public static final char starOperator = '\u22c6';
 
    /** &part; (partial derivative). */
    public static final char partial = '\u2202';
    
    /** &nabla; (nabla). */
    public static final char nabla = '\u2207';
    
    /** &int; (integral sign). */
    public static final char integral = '\u222b';
    
    /** double integral signs. */
    public static final char doubleIntegral = '\u222c';
    
    /** triple integral signs. */
    public static final char tripleIntegral = '\u222d';
    
    /** cross product. */
    public static final char crossProduct = '\u2a2f';
    
    /** &prime; (prime). */
    public static final char prime = '\u2032';
    
    /** &Prime; (double prime). */
    public static final char doublePrime = '\u2033';
    
    /** triple prime. */
    public static final char triplePrime = '\u2034';
    
    /** &sum; (summation). */
    public static final char summation = '\u2211';
    
    /** &prod; (product). */
    public static final char product = '\u220f';
    
    /** &ne; (not equal). */
    public static final char notEqual = '\u2260';
    
    /** &prop; (proportional to). */
    public static final char proportionalTo = '\u221d';
    
    /** &asymp; (approximately). */
    public static final char approximately = '\u2248';
    
    
    // Physics
    /** &deg; (degree). */
    public static final char degree = '\u00b0';
    
    /** degree celsius. */
    public static final char celsius = '\u2103';
    
    /** degree farenheit. */
    public static final char farenheit = '\u2109';
    
    /** hbar (Dirac's constant). */
    public static final char hbar = '\u0127';
    
    /** &mu; (greek letter: mu). */
    public static final char mu = '\u00b5';
    
    /** &Aring; (circled A, as in Angstrom). */
    public static final char Acircle = '\u00c5';
    
   
           
    // Astronomy
    
    /** astronomy: Sun. */
    public static final char Sun = '\u2609';
    
    /** planet Mercury. */
    public static final char Mercury = '\u263f';
    
    /** planet Venus. */
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
    
    /** ex-planet Pluto. */
    public static final char Pluto = '\u2647';
    
    
}
