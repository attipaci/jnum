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
import java.util.StringTokenizer;

import jnum.Util;

// TODO: Auto-generated Javadoc
/**
 * The Class SmartTokenizer.
 */
public class SmartTokenizer extends StringTokenizer { 
    
    /**
     * Instantiates a new smart tokenizer.
     *
     * @param str the str
     * @param delim the delim
     * @param returnDelims the return delims
     */
    public SmartTokenizer(String str, String delim, boolean returnDelims) {
        super(str, delim, returnDelims);
    }

    /**
     * Instantiates a new smart tokenizer.
     *
     * @param str the str
     * @param delim the delim
     */
    public SmartTokenizer(String str, String delim) {
        super(str, delim);
    }

    /**
     * Instantiates a new smart tokenizer.
     *
     * @param str the str
     */
    public SmartTokenizer(String str) {
        super(str);
    }
    
    /**
     * Skip.
     */
    public final void skip() {
        nextToken();
    }
    
    /**
     * Skip.
     *
     * @param n the n
     */
    public void skip(int n) {
        for( ;--n >=0; ) {
            if(!hasMoreTokens()) return;
            nextToken();
        }
    }
    
    /**
     * Next boolean.
     *
     * @return true, if successful
     */
    public boolean nextBoolean() {
        return Util.parseBoolean(nextToken());
    }
    
    /**
     * Next byte.
     *
     * @return the byte
     */
    public byte nextByte() {
        return Byte.decode(nextToken());
    }

    /**
     * Next short.
     *
     * @return the short
     */
    public short nextShort() {
        return Short.decode(nextToken());
    }
    
    /**
     * Next int.
     *
     * @return the int
     */
    public int nextInt() {
        return Integer.decode(nextToken());
    }
    
    /**
     * Next long.
     *
     * @return the long
     */
    public long nextLong() {
        return Long.decode(nextToken());
    }
    
    /**
     * Next float.
     *
     * @return the float
     */
    public float nextFloat() {
        return Float.parseFloat(nextToken());
    }
    
    /**
     * Next double.
     *
     * @return the double
     */
    public double nextDouble() {
        return Double.parseDouble(nextToken());
    }
    
    /**
     * Next time value.
     *
     * @param separatorType the separator type
     * @return the double
     * @throws ParseException the parse exception
     */
    public double nextTimeValue(int separatorType) throws ParseException {
        TimeFormat tf = new TimeFormat();
        tf.setSeparator(separatorType);
        return tf.parse(nextToken()).doubleValue();
    }
    
    /**
     * Next angle.
     *
     * @param separatorType the separator type
     * @return the double
     * @throws ParseException the parse exception
     */
    public double nextAngle(int separatorType) throws ParseException {
        AngleFormat af = new AngleFormat();
        af.setSeparator(separatorType);
        return af.parse(nextToken()).doubleValue();
    }
    
}
