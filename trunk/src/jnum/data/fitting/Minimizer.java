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

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;

import jnum.Util;
import jnum.Verbosity;
import jnum.data.PrecisionControl;

public abstract class Minimizer implements PrecisionControl, Verbosity, Penalty {
    private Parametric<Double> function;
    private Parameter[] parameters;
    private double precision;
    private boolean verbose = false; 
    private CovarianceMatrix C;
    private ArrayList<Constraint> constraints = new ArrayList<Constraint>();
     
    private Parametric<Double> costFunction = new Parametric<Double>() {
        @Override
        public Double evaluate() { return Minimizer.this.evaluate(); } 
    };
    
    public Minimizer(Parametric<Double> function, Collection<? extends Parameter> parameters) {
        this(function);
        this.parameters = new Parameter[parameters.size()];
        parameters.toArray(this.parameters);
        init();
    }
   
    public Minimizer(Parametric<Double> function, Parameter[] parameters) {
        this(function);
        this.parameters = parameters;
        init();
    }    
    
    private Minimizer(Parametric<Double> function) {
        this.function = function;
        setPrecision(DEFAULT_PRECISION);
    }  
    
    protected abstract void findMinimum();
    
    public abstract double getMinimum();
    
    public abstract boolean hasConverged();
    
    public void minimize() throws ConvergenceException {
        reset();
        findMinimum();
       
        try { calcCovarianceMatrix(); }
        catch(IllegalArgumentException e) {}
        
        if(function instanceof ChiSquared) calcStandardErrors();
    }
    
    protected void init() {
        reset();
    }
    
    protected void reset() {
        C = null;
    }
    
    public double evaluate() {  
        return function.evaluate() * (1.0 + penalty());
    }
    
    @Override
    public double penalty() {
        double penalty = 0.0;
        for(Constraint constraint : constraints) penalty += constraint.penalty();
        for(Parameter p : parameters) penalty += p.penalty();
        return penalty;
    }
    
    public Parametric<Double> getCostFunction() { return costFunction; }

    public Parameter getParameter(int i) {
        return parameters[i];
    }
    
    public int parameters() { return parameters.length; }
    
    public synchronized void addConstraint(Constraint constraint) {
        constraints.add(constraint);
    }
    
    public synchronized void clearConstraints() { constraints.clear(); }
    
    public ArrayList<Constraint> getConstraints() { return constraints; }
    
    @Override
    public void setPrecision(double x) { precision = x; }
    
    @Override
    public double getPrecision() { return precision; }
    
    @Override
    public void setVerbose(boolean value) { verbose = value; }
    
    @Override
    public boolean isVerbose() { return verbose; }
     
    public void calcCovarianceMatrix() {
        if(!hasConverged()) throw new IllegalStateException(getClass().getSimpleName() + " has not converged.");
        C = new CovarianceMatrix(costFunction, parameters, 1e6 * precision);
    }
    
    public CovarianceMatrix getCovarianceMatrix() {
       return C;
    }
    
    public CorrelationMatrix getCorrelationMatrix() {
        CovarianceMatrix C = getCovarianceMatrix();
        if(C == null) return null;
        return C.getCorrelationMatrix();
    }
    
    public void calcStandardErrors() {
        getCovarianceMatrix().setParameterErrors();
    }
    
    public void print(String lead) { print(System.out, lead); }
    
    public void print() { print(System.out); }
    
    public void print(PrintStream out) {
        out.println(toString());
    }
    
    public void print(PrintStream out, String lead) {
        out.println(toString(lead));
    }
    
    @Override
    public final String toString() {
        return toString("");
    }
    
    public String toString(String lead) {
        StringBuffer info = new StringBuffer(); 
                
        info.append(lead + getClass().getSimpleName() + " --> " + function.getClass().getSimpleName() + ":\n\n");    
        info.append(lead + "  min: " + Util.s4.format(getMinimum()) + "\n\n");
        
        for(int i=0; i<parameters(); i++) info.append(lead + "  " + getParameter(i).toString() + "\n");
        
        return new String(info);
    }
    
    public static double DEFAULT_PRECISION = 1e-8;
    public static int DEFAULT_ATTEMPTS = 3;
    
}
