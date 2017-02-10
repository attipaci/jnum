/*******************************************************************************
 * Copyright (c) 2017 Attila Kovacs <attila[AT]sigmyne.com>.
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

package jnum.math;

import java.math.BigDecimal;

public class Arithmetic {
    
    public static Number sumOf(Number a, Number b) {
        if(isDecimalType(a)) return a.doubleValue() + b.doubleValue();
        if(isDecimalType(b)) return a.doubleValue() + b.doubleValue();
        return a.longValue() + b.longValue();
    }
    
    public static Number differenceOf(Number a, Number b) {
        if(isDecimalType(a)) return a.doubleValue() - b.doubleValue();
        if(isDecimalType(b)) return a.doubleValue() - b.doubleValue();
        return a.longValue() - b.longValue();
    }
    
    public static Number productOf(Number a, Number b) {
        if(isDecimalType(a)) return a.doubleValue() * b.doubleValue();
        if(isDecimalType(b)) return a.doubleValue() * b.doubleValue();
        return a.longValue() * b.longValue();
    }
    
    public static Number ratioOf(Number a, Number b) {
        if(isDecimalType(a)) return a.doubleValue() / b.doubleValue();
        if(isDecimalType(b)) return a.doubleValue() / b.doubleValue();
        return a.longValue() / b.longValue();
    }
    
    public static boolean isDecimalType(Number x) {
        // ordered by commonality for fast lookups...
        if(x instanceof Float) return true;
        if(x instanceof Double) return true;
        if(x instanceof BigDecimal) return true;
        return false;
    }

    
}
