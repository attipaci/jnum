/* *****************************************************************************
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
 *     Attila Kovacs  - initial API and implementation
 ******************************************************************************/
package jnum.util;

import java.util.Map;

import jnum.Unit;
import jnum.Util;
import jnum.math.InverseValue;


public class ExponentUnit extends Unit implements InverseValue<ExponentUnit> {

    /** */
    private static final long serialVersionUID = -5909400335570195212L;

    private Unit base;

    private double exponent;

    private boolean isEnclosed;


    public ExponentUnit(Unit base) {
        this(base, 1.0);
    }
    
    public ExponentUnit(Unit base, double power) {
        set(base, power);
    }

    @Override
    public ExponentUnit copy() {
        ExponentUnit u = (ExponentUnit) super.copy();
        u.base = base.copy();
        return u;
    }

    public Unit getBase() { return base; }

    public void set(Unit base, double exponent) {
        this.base = base;
        this.exponent = exponent;
        isEnclosed = CompoundUnit.class.isAssignableFrom(base.getClass());
    }

    @Override
    public String name() {		

        if(exponent == 1.0) return base.name();
        else if(exponent == 0.0) return "";

        StringBuffer buf = new StringBuffer();


        if(useSlash && exponent < 0.0) {
            buf.append("/");
            if(exponent == -1.0) {
                buf.append(base.name());
                return new String(buf);
            }
        }

        buf.append((isEnclosed ? "(" + base.name() + ")" : base.name()) + exponentSymbol);

        int iExponent = (int)Math.round(exponent);
        if(Util.equals(exponent, iExponent)) {
            if(useSlash && iExponent < 0) buf.append(-iExponent);
            else buf.append(iExponent < 0 ? "(" + iExponent + ")" : iExponent);
        }

        else {
            if(useSlash && exponent < 0) buf.append(exponent);
            else buf.append(exponent < 0.0 ? "(" + exponent + ")" : exponent);
        }

        return new String(buf);
    }

    @Override
    public double value() {
        return Math.pow(base.value(), exponent);
    }

    public double getExponent() { return exponent; }

    public void setExponent(double value) { this.exponent = value; }

    public static ExponentUnit get(String id) throws IllegalArgumentException {
        return get(id, standardUnits);
    }

    public static ExponentUnit get(String id, Map<String, Unit> baseUnits) throws IllegalArgumentException {
        for(String marker : parseExponentSymbols) {
            if(id.contains(marker)) return get(id, id.indexOf(marker), baseUnits);
        }

        return new ExponentUnit(Unit.get(id, baseUnits), 1.0);
    }

    private static ExponentUnit get(String value, int index, Map<String, Unit> baseUnits) throws IllegalArgumentException {
        ExponentUnit u = new ExponentUnit(Unit.get(value.substring(0, index), baseUnits), 1.0);
        index += exponentSymbol.length();
        char c = value.charAt(index);

        if(c == '{' || c == '(') 
            u.exponent = Double.parseDouble(value.substring(index + 1, value.length()-1));
        else u.exponent = Double.parseDouble(value.substring(index));
        return u;
    }

    @Override
    public ExponentUnit getInverse() {
        ExponentUnit u = copy();
        u.inverse();
        return u;
    }

    @Override
    public void inverse() {
        exponent *= -1;
    }

    /** The exponent symbol. */
    public static String exponentSymbol = "**";

    public static String[] parseExponentSymbols = new String[] { "**", "^" };

    /** Whether to use a slash '/' for units that have negative exponent (i.e. "1/s**2" instead of "s**{-2}. */
    private static boolean useSlash = true;
    

}
