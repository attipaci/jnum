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

public class SmartTokenizer extends StringTokenizer { 
    public SmartTokenizer(String str, String delim, boolean returnDelims) {
        super(str, delim, returnDelims);
    }

    public SmartTokenizer(String str, String delim) {
        super(str, delim);
    }

    public SmartTokenizer(String str) {
        super(str);
    }
    
    public final void skip() {
        nextToken();
    }
    
    public void skip(int n) {
        for( ;--n >=0; ) {
            if(!hasMoreTokens()) return;
            nextToken();
        }
    }
    
    public boolean nextBoolean() {
        return Util.parseBoolean(nextToken());
    }
    
    public byte nextByte() {
        return Byte.decode(nextToken());
    }

    public short nextShort() {
        return Short.decode(nextToken());
    }
    
    public int nextInt() {
        return Integer.decode(nextToken());
    }
    
    public long nextLong() {
        return Long.decode(nextToken());
    }
    
    public float nextFloat() {
        return Float.parseFloat(nextToken());
    }
    
    public double nextDouble() {
        return Double.parseDouble(nextToken());
    }
    
    public double nextTimeValue(int separatorType) throws ParseException {
        TimeFormat tf = new TimeFormat();
        tf.setSeparator(separatorType);
        return tf.parse(nextToken()).doubleValue();
    }
    
    public double nextAngle(int separatorType) throws ParseException {
        AngleFormat af = new AngleFormat();
        af.setSeparator(separatorType);
        return af.parse(nextToken()).doubleValue();
    }
    
}
