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

package jnum.devel;


// TODO java operators, plus:
//   ** : power

public abstract class Operation {
   
    public String ascii;
    public char unicode;
    
    protected Operation(String notation) { setNotation(notation, (char) 0); }
    
    protected Operation(String notation, char unicode) { setNotation(notation, unicode); }
    
    public void setNotation(String ascii, char unicode) { 
        this.ascii = ascii;
        this.unicode = unicode;
    }
      
    
    public static Operation increment = new Increment();
    public static Operation decrement = new Decrement();
    public static Operation negation = new Negation();
    public static Operation power = new Power();
    public static Operation multiplication = new Multiplication();
    public static Operation division = new Division();
    public static Operation modulus = new Modulus();
    public static Operation addition = new Addition();
    public static Operation subtraction = new Subtraction();
    // TODO ...
   
    
    private static class Increment extends Operation implements IntegerStepping {
        public Increment() { super("++"); }

        @Override
        public long update(VariableLookup variables, String id, Long leftSide, Long rightSide) {
            // TODO check that called with single non-null argument... 
            
            Variable var = variables.get(id);
            
            if(leftSide == null) {
                long value = var.asLong() + 1L;
                var.setValue(value);
                return value;
            }
            
            else {
                long value = var.asLong();
                var.setValue(value + 1L);
                return value;
            }
        }
    }
    
    private static class Decrement extends Operation implements IntegerStepping {
        public Decrement() { super("--"); }

        @Override
        public long update(VariableLookup variables, String id, Long leftSide, Long rightSide) {
            // TODO check that called with single non-null argument... 
            Variable var = variables.get(id);
            
            
            if(leftSide == null) {
                long value = var.asLong() - 1L;
                var.setValue(value);
                return value;
            }
            
            else {
                long value = var.asLong();
                var.setValue(value - 1L);
                return value;
            }
        }
    }
    
    private static class Negation extends Operation implements FloatingMath, IntegerMath {
        public Negation() { super("-"); }

        @Override
        public long getLongValue(Long leftSide, Long rightSide) {
            if(rightSide == null) throw new IllegalArgumentException("missing argument");
            if(leftSide != null) throw new IllegalArgumentException("unexpected left-side argument");
            return -rightSide;
        }

        @Override
        public double getDoubleValue(Double leftSide, Double rightSide) {
            if(rightSide == null) throw new IllegalArgumentException("missing argument");
            if(leftSide != null) throw new IllegalArgumentException("unexpected left-side argument");
            return -rightSide;
        }    
    }
    
   
    
    /*
    // TODO +expr, -expr.
    LOGICAL_NOT("!", ParseType.BOOLEAN),
    BINARY_NOT("~", ParseType.LONG),
    */
     
    // power
    private static class Power extends Operation implements FloatingMath {
        public Power() { super("**"); }

        @Override
        public double getDoubleValue(Double leftSide, Double rightSide) {
            if(leftSide == null || rightSide == null) throw new IllegalArgumentException("missing argument");
            return Math.pow(leftSide, rightSide);
        }
    }
    
    private static class Multiplication extends Operation implements FloatingMath, IntegerMath {
        public Multiplication() { super("*"); }

        @Override
        public double getDoubleValue(Double leftSide, Double rightSide) {
            if(leftSide == null || rightSide == null) throw new IllegalArgumentException("missing argument");
            return leftSide * rightSide;
        }

        @Override
        public long getLongValue(Long leftSide, Long rightSide) {
            if(leftSide == null || rightSide == null) throw new IllegalArgumentException("missing argument");
            return leftSide * rightSide;
        }
    }
    
    private static class Division extends Operation implements FloatingMath, IntegerMath {
        public Division() { super("/"); }

        @Override
        public double getDoubleValue(Double leftSide, Double rightSide) {
            if(leftSide == null || rightSide == null) throw new IllegalArgumentException("missing argument");
            return leftSide / rightSide;
        }

        @Override
        public long getLongValue(Long leftSide, Long rightSide) {
            if(leftSide == null || rightSide == null) throw new IllegalArgumentException("missing argument");
            return leftSide / rightSide;
        }
    }
    
    public static class Modulus extends Operation implements FloatingMath, IntegerMath {
        public Modulus() { super("%"); }

        @Override
        public double getDoubleValue(Double leftSide, Double rightSide) {
            if(leftSide == null || rightSide == null) throw new IllegalArgumentException("missing argument");
            return leftSide % rightSide;
        }

        @Override
        public long getLongValue(Long leftSide, Long rightSide) {
            if(leftSide == null || rightSide == null) throw new IllegalArgumentException("missing argument");
            return leftSide % rightSide;
        }
    }
  
    public static class Addition extends Operation implements FloatingMath, IntegerMath {
        public Addition() { super("+"); }

        @Override
        public double getDoubleValue(Double leftSide, Double rightSide) {
            if(leftSide == null || rightSide == null) throw new IllegalArgumentException("missing argument");
            return leftSide + rightSide;
        }

        @Override
        public long getLongValue(Long leftSide, Long rightSide) {
            if(leftSide == null || rightSide == null) throw new IllegalArgumentException("missing argument");
            return leftSide + rightSide;
        }
    }
    
    public static class Subtraction extends Operation implements FloatingMath, IntegerMath {
        public Subtraction() { super("-"); }

        @Override
        public double getDoubleValue(Double leftSide, Double rightSide) {
            if(leftSide == null || rightSide == null) throw new IllegalArgumentException("missing argument");
            return leftSide - rightSide;
        }

        @Override
        public long getLongValue(Long leftSide, Long rightSide) {
            if(leftSide == null || rightSide == null) throw new IllegalArgumentException("missing argument");
            return leftSide - rightSide;
        }
    }
    
    public static class BitShiftLeft extends Operation implements IntegerMath {
        public BitShiftLeft() { super("<<"); }

        @Override
        public long getLongValue(Long leftSide, Long rightSide) {
            if(leftSide == null || rightSide == null) throw new IllegalArgumentException("missing argument");
            return leftSide << rightSide;
        }
    }
    
    public static class BitShiftRight extends Operation implements IntegerMath {
        public BitShiftRight() { super(">>"); }

        @Override
        public long getLongValue(Long leftSide, Long rightSide) {
            if(leftSide == null || rightSide == null) throw new IllegalArgumentException("missing argument");
            return leftSide >> rightSide;
        }
    }
    
    public static class BitShiftUnsignedRight extends Operation implements IntegerMath {
        public BitShiftUnsignedRight() { super(">>"); }

        @Override
        public long getLongValue(Long leftSide, Long rightSide) {
            if(leftSide == null || rightSide == null) throw new IllegalArgumentException("missing argument");
            return leftSide >>> rightSide;
        }
    }
    
    
    /*
    // postfix expr++, expr--
    // unary: ++expr, --expr, +expr, -expr, ~expr, !expr;
    
    
    // multiplicative
    MULTIPLICATION("*", ParseType.COMPLEX),
    DIVISION("/", ParseType.COMPLEX),
    MODULUS("%", ParseType.COMPLEX),
    
    // additive
    ADDITION("+", ParseType.COMPLEX),
    SUBTRACTION("-", ParseType.COMPLEX),
    
    // shift
    BITSHIFT_LEFT("<<", ParseType.LONG),
    BITSHIFT_RIGHT(">>", ParseType.LONG),
    BITSHIFT_UNSIGNED_RIGHT(">>>", ParseType.LONG),
    
    // relational
    LESS("<", ParseType.DOUBLE),
    GREATER(">", ParseType.DOUBLE),
    LESS_EQUAL("<=", ParseType.DOUBLE),
    GREATER_EQUAL(">=", ParseType.DOUBLE),
    
    // equality
    EQUALS("==", ParseType.COMPLEX),
    NOT_EQUALS("!=", ParseType.COMPLEX),
   
    // bitwise
    BITWISE_AND("&", ParseType.LONG),
    BITWISE_XOR("^", ParseType.LONG),
    BITWISE_OR("|", ParseType.LONG),
   
    // Logical
    LOGICAL_AND("&&", ParseType.BOOLEAN),
    LOGICAL_OR("||", ParseType.BOOLEAN),
    
    // ternary
    // TODO ? :
    
    // Assignment
    ASSIGN("=", ParseType.COMPLEX),
    REASSIGN_INCREMENT("+=", ParseType.COMPLEX),
    REASSIGN_DECREMENT("-=", ParseType.COMPLEX),
    REASSIGN_MULTIPLIED("*=", ParseType.COMPLEX),
    REASSIGN_DIVIDED("/=", ParseType.COMPLEX),
    REASSIGN_MODULUS("%=", ParseType.COMPLEX),
    REASSIGN_BINARY_NOT("~=", ParseType.LONG),
    REASSIGN_BINARY_AND("&=", ParseType.LONG),
    REASSIGN_BINARY_OR("|=", ParseType.LONG),
    REASSIGN_BINARY_XOR("^=", ParseType.LONG),
    REASSIGN_SHIFT_LEFT("<<=", ParseType.LONG),
    REASSIGN_SHIFT_RIGHT(">>=", ParseType.LONG),
    REASSIGN_SHIFT_UNSIGNED_RIGHT(">>>=", ParseType.LONG);
    
    */
    
    
    
}
