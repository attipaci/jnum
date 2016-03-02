/*******************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
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

package jnum.devel;

import java.util.*;

import jnum.ExtraMath;
import jnum.Util;
import jnum.math.*;

// TODO: Auto-generated Javadoc
/**
 * The Class Expression.
 */
public class Expression {
	
	/** The spec. */
	String spec;
	
	/** The op. */
	String op;
	
	/** The arguments. */
	ArrayList<Expression> arguments;
	
	/**
	 * Instantiates a new expression.
	 *
	 * @param text the text
	 */
	public Expression(String text) {
		// TODO parse here into hierarchical operators and sub-expressions...
		// hierarchy: http://introcs.cs.princeton.edu/java/11precedence/
		
		
	}
	
	/**
	 * First value.
	 *
	 * @return the object
	 */
	public Object firstValue() { return arguments.get(0).getValue(); }
	
	/**
	 * Second value.
	 *
	 * @return the object
	 */
	public Object secondValue() { return arguments.get(1).getValue(); }
	
	
	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public Object getValue() {
		if(op == null) return getDirectValue();
		else if(op.length() == 1) return getByteOpValue();
		else return getFunctionValue();
	}
		
		
	/**
	 * Gets the direct value.
	 *
	 * @return the direct value
	 */
	protected Object getDirectValue() {
		if(variables.containsKey(spec)) return variables.get(spec).getValue();
		
		try { return Util.parseBoolean(spec); }
		catch(Exception e) {}
		
		try { return Long.decode(spec); }
		catch(Exception e) {}
		
		try { return Double.parseDouble(spec); }
		catch(Exception e) {}
		
		// TODO parse Arrays (Vectors, Matrixes, Tensors): {a,b...}, {{a,b...},{...},...}
		// TODO parse Complex (Vector2D): a,b
		//throw new ParseException("Undefined value: " + spec);
		
		return null;
	}
		
	/**
	 * Gets the byte op value.
	 *
	 * @return the byte op value
	 */
	protected Object getByteOpValue() {	
		char c = op.charAt(0);

		// TODO function definitions as well as variables...
		if(c == '=') variables.put(arguments.get(0).spec, arguments.get(1));

		Object a = firstValue();

		if(c == '~') return bitwiseNOT(a);
		else if(c == '!') return booleanNOT(a);
			
		Object b = secondValue();

		if(c == ' ') {
			try { return product(a, b); }
			catch(Exception e) {
				throw new IllegalStateException("No default operation for: " + a.getClass().getSimpleName() + ", " + b.getClass().getSimpleName());
			}
		}

		if(c == '+') return sum(a, b);
		else if(c == '-') return difference(a, b);
		else if(c == '*') return product(a, b);
		else if(c == '/') return ratio(a, b);
		else if(c == '%') return modulus(a, b);
		else if(c == '&') return bitwiseAND(a, b);
		else if(c == '|') return bitwiseOR(a, b);	
		else if(c == '^') return bitwiseXOR(a, b);
		else if(c == '<') return compare(a, b);
		else if(c == '>') return compare(b, a);
		else if(c == '?') {	
			
		}
		
		throw new IllegalArgumentException("Unknown operator '" + c + "'");
	}
	
	/**
	 * Sum.
	 *
	 * @param a the a
	 * @param b the b
	 * @return the object
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Object sum(Object a, Object b) {
		if(a instanceof Long && b instanceof Long) return (Long) a + (Long) b;
		else if(a instanceof Number && b instanceof Number) return ((Number) a).doubleValue() + ((Number) b).doubleValue(); 
		else if(a instanceof Additive && a.getClass().isAssignableFrom(b.getClass())) { ((Additive) a).add(b); return a; }
		else if(b instanceof Additive && b.getClass().isAssignableFrom(a.getClass())) { ((Additive) b).add(a); return b; }
		else if(a instanceof RealAddition && b instanceof Number) { ((RealAddition) a).addReal(((Number) b).doubleValue()); return a; }
		else if(b instanceof RealAddition && a instanceof Number) { ((RealAddition) b).addReal(((Number) a).doubleValue()); return b; }
		else if(a instanceof ComplexAddition && b instanceof Complex) { ((ComplexAddition) a).addComplex((Complex) b); return a; }
		else if(b instanceof ComplexAddition && a instanceof Complex) { ((ComplexAddition) b).addComplex((Complex) a); return b; }
		else throw new IllegalStateException("No addition for types: " + a.getClass().getSimpleName() + ", " + b.getClass().getSimpleName());		
	}
	
	/**
	 * Difference.
	 *
	 * @param a the a
	 * @param b the b
	 * @return the object
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Object difference(Object a, Object b) {
		if(a instanceof Long && b instanceof Long) return (Long) a - (Long) b;
		else if(a instanceof Number && b instanceof Number) return ((Number) a).doubleValue() - ((Number) b).doubleValue(); 
		else if(a instanceof Additive && a.getClass().isAssignableFrom(b.getClass())) { ((Additive) a).subtract(b); return a; }
		else if(b instanceof Additive && b.getClass().isAssignableFrom(a.getClass()) && b instanceof Scalable) { ((Additive) b).subtract(a); ((Scalable) b).scale(-1.0); return b; } 
		else if(a instanceof RealAddition && b instanceof Number) { ((RealAddition) a).subtractReal(((Number) b).doubleValue()); return a; }
		else if(b instanceof RealAddition && a instanceof Number && b instanceof Scalable) { ((RealAddition) b).subtractReal(((Number) a).doubleValue()); ((Scalable) b).scale(-1.0); return b; }
		else if(a instanceof ComplexAddition && b instanceof Complex) { ((ComplexAddition) a).subtractComplex((Complex) b); return a; }
		else if(b instanceof ComplexAddition && a instanceof Complex && b instanceof Scalable) { ((ComplexAddition) b).subtractComplex((Complex) a); ((Scalable) b).scale(-1.0); return b; }
		else throw new IllegalStateException("No subtraction for types: " + a.getClass().getSimpleName() + ", " + b.getClass().getSimpleName());
	}

	/**
	 * Product.
	 *
	 * @param a the a
	 * @param b the b
	 * @return the object
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Object product(Object a, Object b) {
		if(a instanceof Long && b instanceof Long) return (Long) a * (Long) b;
		else if(a instanceof Number && b instanceof Number) return ((Number) a).doubleValue() * ((Number) b).doubleValue(); 
		else if(a instanceof Multiplicative && a.getClass().isAssignableFrom(b.getClass())) { ((Multiplicative) a).multiplyBy(b); return a; }
		else if(b instanceof Multiplicative && b.getClass().isAssignableFrom(a.getClass())) { ((Multiplicative) b).multiplyBy(a); return b; }
		else if(a instanceof Scalable && b instanceof Number) { ((Scalable) a).scale(((Number) b).doubleValue()); return a; }
		else if(b instanceof Scalable && a instanceof Number) { ((Scalable) b).scale(((Number) a).doubleValue()); return b; }
		else if(a instanceof ComplexScaling && b instanceof Complex) { ((ComplexScaling) a).scale((Complex) b); return a; }
		else if(b instanceof ComplexScaling && a instanceof Complex) { ((ComplexScaling) b).scale((Complex) a); return b; }
		else throw new IllegalStateException("No multiplication for types: " + a.getClass().getSimpleName() + ", " + b.getClass().getSimpleName());	
	}
	
	/**
	 * Ratio.
	 *
	 * @param a the a
	 * @param b the b
	 * @return the object
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Object ratio(Object a, Object b) {
		if(a instanceof Number && b instanceof Number) return ((Number) a).doubleValue() / ((Number) b).doubleValue(); 
		else if(a instanceof Scalable && b instanceof Number) { ((Scalable) a).scale(1.0 / ((Number) b).doubleValue()); return a; }
		else if(a instanceof ComplexScaling && b instanceof Complex) { ((Complex) b).inverse(); ((ComplexScaling) a).scale((Complex) b); return a; }
		else if(a instanceof Division && a.getClass().isAssignableFrom(b.getClass())) { ((Division) a).divideBy(b); return a; }
		else throw new IllegalStateException("No division for types: " + a.getClass().getSimpleName() + ", " + b.getClass().getSimpleName());
	}
	
	/**
	 * Modulus.
	 *
	 * @param a the a
	 * @param b the b
	 * @return the object
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Object modulus(Object a, Object b) {
		if(a instanceof Long && b instanceof Long) return (Long) a % (Long) b;
		else if(a instanceof Number && b instanceof Number) return ((Number) a).doubleValue() % ((Number) b).doubleValue();
		else if(a instanceof Modulus && a.getClass().isAssignableFrom(b.getClass())) { ((Modulus) a).modulo(b); return a; }
		else throw new IllegalStateException("No modulus for types: " + a.getClass().getSimpleName() + ", " + b.getClass().getSimpleName());
	}
	
	/**
	 * Compare.
	 *
	 * @param a the a
	 * @param b the b
	 * @return the int
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected int compare(Object a, Object b) {
		if(a instanceof Comparable && a.getClass().isAssignableFrom(b.getClass())) return ((Comparable) a).compareTo(b);
		else if(b instanceof Comparable && b.getClass().isAssignableFrom(a.getClass())) return -((Comparable) b).compareTo(a);
		else if(a instanceof Number && b instanceof Number) return Double.compare(((Number) a).doubleValue(), ((Number) b).doubleValue()); 
		else throw new IllegalStateException("No comparison for types: " + a.getClass().getSimpleName() + ", " + b.getClass().getSimpleName());		
	}
	
	/**
	 * Boolean not.
	 *
	 * @param a the a
	 * @return true, if successful
	 */
	protected boolean booleanNOT(Object a) {
		if(a instanceof Long) return (Long) a == 0L ? true : false;
		else if(a instanceof Boolean) return !(Boolean) a;
		else throw new IllegalStateException("No boolean NOT for type: " + a.getClass().getSimpleName());

	}
	
	/**
	 * Bitwise not.
	 *
	 * @param a the a
	 * @return the object
	 */
	protected Object bitwiseNOT(Object a) {
		if(a instanceof Long) return ~(Long) a;
		else if(a instanceof Boolean) return !(Boolean) a;
		else throw new IllegalStateException("No bitwise NOT for type: " + a.getClass().getSimpleName());
	}
	
	/**
	 * Bitwise and.
	 *
	 * @param a the a
	 * @param b the b
	 * @return the object
	 */
	protected Object bitwiseAND(Object a, Object b) {
		if(a instanceof Long && b instanceof Long) return (Long) a & (Long) b;
		else if(a instanceof Boolean && b instanceof Boolean) return (Boolean) a & (Boolean) b;
		else throw new IllegalStateException("No bitwise AND for types: " + a.getClass().getSimpleName() + ", " + b.getClass().getSimpleName());
	}
	
	/**
	 * Bitwise or.
	 *
	 * @param a the a
	 * @param b the b
	 * @return the object
	 */
	protected Object bitwiseOR(Object a, Object b) {
		if(a instanceof Long && b instanceof Long) return (Long) a | (Long) b;
		else if(a instanceof Boolean && b instanceof Boolean) return (Boolean) a | (Boolean) b;
		else throw new IllegalStateException("No bitwise OR for types: " + a.getClass().getSimpleName() + ", " + b.getClass().getSimpleName());
	}
	
	/**
	 * Bitwise xor.
	 *
	 * @param a the a
	 * @param b the b
	 * @return the object
	 */
	protected Object bitwiseXOR(Object a, Object b) {
		if(a instanceof Long && b instanceof Long) return (Long) a ^ (Long) b;
		else if(a instanceof Boolean && b instanceof Boolean) return (Boolean) a ^ (Boolean) b;
		else throw new IllegalStateException("No bitwise XOR for types: " + a.getClass().getSimpleName() + ", " + b.getClass().getSimpleName());
	}
	
	/**
	 * Gets the string op value.
	 *
	 * @return the string op value
	 */
	protected Object getStringOpValue() {
		
		// operators: ++, --, ==, !=, <=, >=, &&, ||, 
		// +=, -=, *=, /=, %=, &=, |=, ~=, ^= ???
		// functions...
		//   - built-in
		//	 - integer cast!
		//   - definitions	
		
		return null;
		
	}
	
	/**
	 * Gets the function value.
	 *
	 * @return the function value
	 */
	protected Object getFunctionValue() {
		if(arguments.size() == 1) {
			Object a = firstValue();
			if(a instanceof Number) return getBuiltinFunctionValue(((Number) a).doubleValue());
		}
		if(arguments.size() == 2) {
			Object a = firstValue();
			Object b = secondValue();
			if(a instanceof Number && b instanceof Number) return getBuiltinFunctionValue(((Number) a).doubleValue(), ((Number) b).doubleValue());
		}
	
		return null;
	}
	
	/**
	 * Gets the builtin function value.
	 *
	 * @param x the x
	 * @return the builtin function value
	 */
	protected Object getBuiltinFunctionValue(double x) {
		char c = op.charAt(0);
		
		switch(c) {
		case 'a' :
			if(op.equals("abs")) return Math.abs(x);
			else if(op.equals("asin")) return Math.asin(x);
			else if(op.equals("acos")) return Math.acos(x);
			else if(op.equals("atan")) return Math.atan(x);
			else if(op.equals("acot")) return Math.atan(1.0 / x);
			else if(op.equals("asinh")) return ExtraMath.asinh(x);
			else if(op.equals("acosh")) return ExtraMath.acosh(x);
			else if(op.equals("atanh")) return ExtraMath.atanh(x);
			else if(op.equals("acoth")) return ExtraMath.atanh(1.0 / x);
			break;
		case 'c' :
			if(op.equals("ceil")) return Math.ceil(x);
			else if(op.equals("cos")) return Math.cos(x);
			else if(op.equals("cosh")) return Math.cosh(x);
			else if(op.equals("cot")) return 1.0 / Math.tan(x);
			else if(op.equals("coth")) return 1.0 / Math.tanh(x);
			else if(op.equals("cbrt")) return Math.cbrt(x);
			break;
		case 'e' :
			if(op.equals("exp")) return Math.exp(x);
			if(op.equals("expm1")) return Math.expm1(x);
			break;
		case 'f' :
			if(op.equals("floor")) return Math.floor(x);
		case 'i' :
			if(op.equals("int")) return (long) x;
			break;
		case 'l' : 
			if(op.equals("log")) return Math.log(x);
			else if(op.equals("ln")) return Math.log(x);
			else if(op.equals("log2")) return ExtraMath.log2(x);
			else if(op.equals("log10")) return Math.log10(x);
			else if(op.equals("log1p")) return Math.log1p(x);
			break;
		case 'r' :
			if(op.equals("round")) return Math.round(x);
			break;
		case 's' :
			if(op.equals("sin")) return Math.sin(x);
			else if(op.equals("sinh")) return Math.sinh(x);
			else if(op.equals("sinc")) return ExtraMath.sinc(x);
			else if(op.equals("sqrt")) return Math.sqrt(x);
			break;
		case 't' :
			if(op.equals("tan")) return Math.tan(x);
			else if(op.equals("tan")) return Math.tanh(x);
			break;
		}
		
		throw new UnsupportedOperationException("Undefined operation '" + op + "' for double argument.");
	}
	
	/**
	 * Gets the builtin function value.
	 *
	 * @param x the x
	 * @param y the y
	 * @return the builtin function value
	 */
	protected Object getBuiltinFunctionValue(double x, double y) {
		if(op.equals("max")) return Math.max(x, y);
		if(op.equals("min")) return Math.min(x, y);
		if(op.equals("pow")) return Math.pow(x, y);
		else if(op.equals("hypot")) return ExtraMath.hypot(x, y);
		else if(op.equals("mod")) return Math.IEEEremainder(x, y);
		else if(op.equals("atan2")) return Math.atan2(x, y);
		throw new UnsupportedOperationException("Undefined operation '" + op + "' for 2 double arguments.");
	}
	
	
	/**
	 * Gets the user function value.
	 *
	 * @return the user function value
	 */
	protected Object getUserFunctionValue() {
		
		return null;
	}
	
	
	/** The variables. */
	public static Hashtable<String, Expression> variables = new Hashtable<String, Expression>();
	
}
