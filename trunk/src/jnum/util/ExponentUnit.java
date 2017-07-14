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
package jnum.util;

import java.util.Map;

import jnum.Unit;
import jnum.math.InverseValue;

// TODO: Auto-generated Javadoc
/**
 * The Class PowerUnit.
 */
public class ExponentUnit extends Unit implements InverseValue<ExponentUnit> {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -5909400335570195212L;

    /** The base. */
    private Unit base;

    /** The exponent. */
    private double exponent;

    /** The bracket base. */
    private boolean bracketBase;



    /**
     * Instantiates a new unit with an exponent.
     */
    public ExponentUnit() { this(null); }

    public ExponentUnit(Unit base) {
        this(base, 1.0);
    }
    
    /**
     * Instantiates a new power unit.
     *
     * @param base the base
     * @param power the power
     */
    public ExponentUnit(Unit base, double power) {
        set(base, power);
    }

    /* (non-Javadoc)
     * @see jnum.Unit#copy()
     */
    @Override
    public ExponentUnit copy() {
        ExponentUnit u = (ExponentUnit) super.copy();
        u.base = base.copy();
        return u;
    }

    /**
     * Gets the base.
     *
     * @return the base
     */
    public Unit getBase() { return base; }

    /**
     * Sets the.
     *
     * @param base the base
     * @param exponent the exponent
     */
    public void set(Unit base, double exponent) {
        this.base = base;
        this.exponent = exponent;
        bracketBase = CompoundUnit.class.isAssignableFrom(base.getClass());

    }

    /* (non-Javadoc)
     * @see jnum.Unit#setMultiplier(jnum.Unit.Multiplier)
     */
    @Override
    public void setMultiplier(Multiplier m) { base.setMultiplier(m); }

    /* (non-Javadoc)
     * @see jnum.Unit#getMultiplier()
     */
    @Override
    public Multiplier getMultiplier() { return base.getMultiplier(); }

    /* (non-Javadoc)
     * @see jnum.Unit#name()
     */
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

        buf.append((bracketBase ? "(" + base.name() + ")" : base.name()) + exponentSymbol);

        int iExponent = (int)Math.round(exponent);
        if(exponent == iExponent) {
            if(useSlash && iExponent < 0) buf.append(-iExponent);
            else buf.append(iExponent < 0 ? "(" + iExponent + ")" : iExponent);
        }

        else {
            if(useSlash && exponent < 0) buf.append(exponent);
            else buf.append(exponent < 0.0 ? "(" + exponent + ")" : exponent);
        }

        return new String(buf);
    }

    /* (non-Javadoc)
     * @see jnum.Unit#value()
     */
    @Override
    public double value() {
        return Math.pow(base.value(), exponent);
    }

    /**
     * Gets the exponent.
     *
     * @return the exponent
     */
    public double getExponent() { return exponent; }

    /**
     * Sets the exponent.
     *
     * @param value the new exponent
     */
    public void setExponent(double value) { this.exponent = value; }

    /**
     * Gets the.
     *
     * @param id the id
     * @return the power unit
     * @throws IllegalArgumentException the illegal argument exception
     */
    public static ExponentUnit get(String id) throws IllegalArgumentException {
        return get(id, standardUnits);
    }

    /**
     * Gets the.
     *
     * @param id the id
     * @param baseUnits the base units
     * @return the power unit
     * @throws IllegalArgumentException the illegal argument exception
     */
    public static ExponentUnit get(String id, Map<String, Unit> baseUnits) throws IllegalArgumentException {
        //System.err.println("### fetching: '" + id + "'");

        for(String marker : parseExponentSymbols) {
            if(id.contains(marker)) return get(id, id.indexOf(marker), baseUnits);
        }

        return new ExponentUnit(Unit.get(id, baseUnits), 1.0);
    }

    /**
     * Gets the.
     *
     * @param value the value
     * @param index the index
     * @param baseUnits the base units
     * @return the power unit
     * @throws IllegalArgumentException the illegal argument exception
     */
    private static ExponentUnit get(String value, int index, Map<String, Unit> baseUnits) throws IllegalArgumentException {
        ExponentUnit u = new ExponentUnit(Unit.get(value.substring(0, index), baseUnits), 1.0);
        index += exponentSymbol.length();
        char c = value.charAt(index);

        if(c == '{' || c == '(') 
            u.exponent = Double.parseDouble(value.substring(index + 1, value.length()-1));
        else u.exponent = Double.parseDouble(value.substring(index));
        return u;
    }

    /* (non-Javadoc)
     * @see kovacs.math.InverseValue#getInverse()
     */
    @Override
    public ExponentUnit getInverse() {
        ExponentUnit u = (ExponentUnit) copy();
        u.inverse();
        return u;
    }

    /* (non-Javadoc)
     * @see kovacs.math.InverseValue#inverse()
     */
    @Override
    public void inverse() {
        exponent *= -1;
    }

    /** The exponent symbol. */
    public static String exponentSymbol = "**";

    public static String[] parseExponentSymbols = new String[] { "**", "^" };

    /** The use slash. */
    private static boolean useSlash = true;

}
