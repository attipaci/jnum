/* *****************************************************************************
 * Copyright (c) 2016 Attila Kovacs <attila[AT]sigmyne.com>.
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

package jnum.data.fitting;

/**
 * Simple relational constraints between two quantities, one of which must be of {@link Parametric} type.
 */
public class RelationalConstraint extends Constraint {
    
    /** The expression. */
    private Parametric<Double> expression;
    
    /** The relation. */
    private int relation;
    
    /** The target. */
    private Parametric<Double> target;
    
    /** The tolerance for enforcing the constraint. */
    private double tolerance;
    
    /**
     * Instantiates a new relational constraint.
     *
     * @param id the identified of the constraint
     * @param expression the parametric expression that is constrained
     * @param relation the relation (e.g. {@link #EQUALS}, {@link #GREATER_THAN} or {@link #LESS_THAN}).
     * @param targetValue the fixed control value
     * @param tolerance the tolerance for enforcing the constraint.
     */
    public RelationalConstraint(String id, Parametric<Double> expression, int relation, double targetValue, double tolerance) {
        this(id, expression, relation, tolerance);
        setTarget(targetValue); 
    }

    /**
     * Instantiates a new relational constraint.
     *
     * @param id the identified of the constraint
     * @param expression the parametric expression on the left side.
     * @param relation the relation (e.g. {@link #EQUALS}, {@link #GREATER_THAN} or {@link #LESS_THAN}).
     * @param targetValue the parametric expression on the right side.
     * @param tolerance the tolerance for enforcing the constraint.
     */
    public RelationalConstraint(String id, Parametric<Double> expression, int relation, Parametric<Double> targetValue, double tolerance) {
        this(id, expression, relation, tolerance);
        setTarget(targetValue);
    }
    
    /**
     * Partially instantiates a new relational constraint. This constructor is called by other constructors only.
     *
     * @param id the identified of the constraint
     * @param expression the parametric expression on the left side.
     * @param relation the relation (e.g. {@link #EQUALS}, {@link #GREATER_THAN} or {@link #LESS_THAN}).
     * @param tolerance the tolerance for enforcing the constraint.
     */
    private RelationalConstraint(String id, Parametric<Double> expression, int relation, double tolerance) {
        super(id);
        this.expression = expression;
        this.relation = relation;
        setTolerance(tolerance);
    }
    
    /**
     * Sets the target value to a fixed value.
     *
     * @param value the new target value for the constraint.
     */
    public void setTarget(final double value) { 
        this.target = new Parametric<Double>() {
            @Override
            public Double evaluate() { return value; }
        };
    }
    
    /**
     * Sets the target to a parametric right-side expression.
     *
     * @param value the new parametric right-side expression.
     */
    public void setTarget(Parametric<Double> value) {
        this.target = value;
    }
    
    /**
     * Sets the tolerance to which the constraint is to be enforced.
     *
     * @param dx the new tolerance for enforcing the constraint.
     */
    public void setTolerance(double dx) {
        if(dx == 0.0) throw new IllegalArgumentException("tolerance room must be non-zero.");
        this.tolerance = Math.abs(dx);
    }
    
    
    @Override
    public double penalty() {
        final double dev = (expression.evaluate() - target.evaluate()) / tolerance;
       
        if(relation == 0) return dev * dev;
        else if(relation < 0) return dev <= 0.0 ? 0.0 : dev * dev;
        else return dev >= 0.0 ? 0.0 : dev * dev;
    }
    
    /** The Constant EQUALS. */
    public static final int EQUALS = 0;
    
    /** The Constant LESS_THAN. */
    public static final int LESS_THAN = -1;
    
    /** The Constant GREATER_THAN. */
    public static final int GREATER_THAN = 1;
    
}
