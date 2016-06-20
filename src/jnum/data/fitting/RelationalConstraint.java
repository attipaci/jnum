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

package jnum.data.fitting;

public class RelationalConstraint extends Constraint {
    private Parametric<Double> expression;
    private int relation;
    private Parametric<Double> target;
    private double tolerance;
    
    public RelationalConstraint(String id, Parametric<Double> expression, int relation, double targetValue, double tolerance) {
        this(id, expression, relation, tolerance);
        setTarget(targetValue); 
    }

    public RelationalConstraint(String id, Parametric<Double> expression, int relation, Parametric<Double> targetValue, double tolerance) {
        this(id, expression, relation, tolerance);
        setTarget(targetValue);
    }
    
    private RelationalConstraint(String id, Parametric<Double> expression, int relation, double tolerance) {
        super(id);
        this.expression = expression;
        this.relation = relation;
        setTolerance(tolerance);
    }
    
    public void setTarget(final double value) { 
        this.target = new Parametric<Double>() {
            @Override
            public Double evaluate() { return value; }
        };
    }
    
    public void setTarget(Parametric<Double> value) {
        this.target = value;
    }
    
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
    
    public static final int EQUALS = 0;
    
    public static final int LESS_THAN = -1;
    
    public static final int GREATER_THAN = 1;
    
}
