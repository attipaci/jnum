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

import java.util.ArrayList;
import java.util.Map;

import jnum.Unit;
import jnum.math.Division;
import jnum.math.Multiplicative;

/**
 * A compound physical unit composed of a list of unit factors, each with a corresponding exponent.
 * For example nW m<sup>-2</sup> / &radic;GHz, composed of the base factors nW, m, and GHz with
 * exponents 1, -2, and -0.5 respectively.
 * 
 * @author Attila Kovacs
 *
 */
public class CompoundUnit extends Unit implements Multiplicative<Unit>, Division<Unit> {
    /** */
	private static final long serialVersionUID = -8635925466445072139L;

	public ArrayList<ExponentUnit> factors = new ArrayList<>();
		
	/**
	 * Instantiates a new compound unit, with no initial components
	 */
	public CompoundUnit() {}
	
	
	public CompoundUnit(String spec) { 
	    this();
	    parse(spec); 
	}
	
	public CompoundUnit(String spec, Map<String, Unit> baseUnits) { 
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

	@Override
	public CompoundUnit copy() {
		CompoundUnit copy = (CompoundUnit) super.copy();
		factors = new ArrayList<>(factors.size());
		for(ExponentUnit factor : factors) copy.factors.add(factor.copy());
		return copy;
	}

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
	

	@Override
	public void multiplyBy(Unit u) {
		multiplyBy(u, false);
	}

	@Override
	public void divideBy(Unit u) {
		multiplyBy(u, true);
	}
	

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
		
		if(inverse) exp = -exp;
		
		for(ExponentUnit factor : factors) if(factor.getBase().equals(u)) {	
			double priorExp = factor.getExponent();
			if(priorExp == -exp) factors.remove(factor);
			else factor.setExponent(priorExp + exp);
			return;
		}
		
		factors.add(new ExponentUnit(u, exp));
	}
	
	@Override
	public double value() {
		double product = 1.0;
		for(Unit u : factors) product *= u.value();
		return product;
	}

	
	public void parse(String spec) {
		parse(spec, standardUnits);
	}
	

	public void parse(String spec, Map<String, Unit> baseUnits) {		
		if(factors == null) factors = new ArrayList<>();
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


	private void addFactor(String spec, boolean invert, Map<String, Unit> baseUnits) {
		ExponentUnit u = ExponentUnit.get(spec, baseUnits);
		if(invert) u.setExponent(-u.getExponent());
		factors.add(u);	
	}

	@Override
	public void setProduct(Unit a, Unit b) {
		factors.clear();
		multiplyBy(a);
		multiplyBy(b);
	}

	public void setRatio(Unit a, Unit b) {
		factors.clear();
		multiplyBy(a);
		divideBy(b);
	}
	
}
