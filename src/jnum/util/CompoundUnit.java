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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import jnum.Unit;
import jnum.math.Division;
import jnum.math.Multiplicative;

// TODO: Auto-generated Javadoc
/**
 * The Class CompoundUnit.
 */
public class CompoundUnit extends Unit implements Multiplicative<Unit>, Division<Unit> {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8635925466445072139L;
	/** The factors. */
	public ArrayList<ExponentUnit> factors = new ArrayList<ExponentUnit>();
		
	/**
	 * Instantiates a new compound unit.
	 */
	public CompoundUnit() {}
	
	/**
	 * Instantiates a new compound unit.
	 *
	 * @param spec the spec
	 * @param template the template
	 */
	public CompoundUnit(String spec) { 
	    this();
	    parse(spec); 
	}
	
	public CompoundUnit(String spec, Hashtable<String, Unit> baseUnits) { 
        this();
        parse(spec, baseUnits); 
    }
    
	
	@SuppressWarnings("unchecked")
    @Override
    public CompoundUnit clone() {
	    CompoundUnit clone = (CompoundUnit) super.clone();
	    if(factors != null) clone.factors = (ArrayList<ExponentUnit>) factors.clone();
	    return clone;
	}
	
	/* (non-Javadoc)
	 * @see jnum.Unit#copy()
	 */
	@Override
	public CompoundUnit copy() {
		CompoundUnit copy = (CompoundUnit) super.copy();
		factors = new ArrayList<ExponentUnit>(factors.size());
		for(ExponentUnit factor : factors) copy.factors.add(factor.copy());
		return copy;
	}
	
	/* (non-Javadoc)
	 * @see jnum.Unit#name()
	 */
	@Override
	public String name() {
		StringBuffer name = new StringBuffer();
		
		if(factors.isEmpty()) name.append(Unit.unity.name());
		else for(int i=0; i<factors.size(); i++) {
		    ExponentUnit factor = factors.get(i);
		    if(factor.getExponent() == 0.0) continue; // Skip factors with zero exponent;
		    
			String uName = factor.name();
			
			if(uName.length() == 0) continue;
		
			if(uName.charAt(0) == '/') {
			    if(i == 0) name.append("1");
			}
			else {
			    if(i != 0) name.append(" ");
			}
			
			name.append(uName);
		}
		
		return new String(name);
	}
	
	/**
	 * Multiply by.
	 *
	 * @param u the u
	 */
	
	@Override
	public void multiplyBy(Unit u) {
		multiplyBy(u, false);
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.Division#divideBy(java.lang.Object)
	 */
	@Override
	public void divideBy(Unit u) {
		multiplyBy(u, true);
	}
	
	/**
	 * Multiply by.
	 *
	 * @param u the u
	 * @param inverse the inverse
	 */
	private void multiplyBy(Unit u, boolean inverse) {
		if(u.equals(Unit.unity)) return;
		
		if(u instanceof CompoundUnit) {
			CompoundUnit cu = (CompoundUnit) u;
			for(ExponentUnit f : cu.factors)  multiplyBy(f);
			return;
		}
		
		double exp = 1.0;

		if(u instanceof ExponentUnit) {
			exp = ((ExponentUnit) u).getExponent();
			u = ((ExponentUnit) u).getBase();
		}
		
		if(inverse) exp *= -1.0;
		
		for(ExponentUnit factor : factors) if(factor.getBase().equals(u)) {	
			double priorExp = factor.getExponent();
			if(priorExp == -exp) factors.remove(factor);
			else factor.setExponent(priorExp + exp);
			return;
		}
		
		factors.add(new ExponentUnit(u, exp));
	}
	
	
	
	
	/* (non-Javadoc)
	 * @see jnum.Unit#value()
	 */
	@Override
	public double value() {
		double product = 1.0;
		for(Unit u : factors) product *= u.value();
		return product;
	}

	
	public void parse(String spec) {
		parse(spec, standardUnits);
	}
	
	/**
	 * Parses the.
	 *
	 * @param spec the spec
	 * @param baseUnits the base units
	 */
	public void parse(String spec, Map<String, Unit> baseUnits) {		
		if(factors == null) factors = new ArrayList<ExponentUnit>();
		else factors.clear();
			
		StringBuffer buf = new StringBuffer();
		boolean invert = false;
		int pos = 0;
		
		while(pos < spec.length()) {
			char c = spec.charAt(pos++);
			if(c == '/') {
				if(buf.length() > 0) {
					addFactor(new String(buf), invert, baseUnits);
					buf = new StringBuffer();
				}
				invert = true;
			}
			else if(c == ' ') {
				if(buf.length() > 0) {
					addFactor(new String(buf), invert, baseUnits);
					buf = new StringBuffer();
					invert = false;
				}
			}
			else buf.append(c);
		}
		if(buf.length() > 0) addFactor(new String(buf), invert, baseUnits);
	}

	/**
	 * Adds the factor.
	 *
	 * @param spec the spec
	 * @param invert the invert
	 * @param baseUnits the base units
	 */
	private void addFactor(String spec, boolean invert, Map<String, Unit> baseUnits) {
		ExponentUnit u = ExponentUnit.get(spec, baseUnits);
		if(invert) u.setExponent(-u.getExponent());
		factors.add(u);	
	}
		
	/* (non-Javadoc)
	 * @see kovacs.math.Multiplicative#setProduct(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setProduct(Unit a, Unit b) {
		factors.clear();
		multiplyBy(a);
		multiplyBy(b);
	}

	/* (non-Javadoc)
	 * @see kovacs.math.Division#setRatio(java.lang.Object, java.lang.Object)
	 */
	/**
	 * Sets the ratio.
	 *
	 * @param a the a
	 * @param b the b
	 */
	public void setRatio(Unit a, Unit b) {
		factors.clear();
		multiplyBy(a);
		divideBy(b);
	}
	
}
