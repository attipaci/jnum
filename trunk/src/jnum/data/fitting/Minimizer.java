/*******************************************************************************
 * Copyright (c) 2016 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
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
 *     Attila Kovacs <attila_kovacs[AT]post.harvard.edu> - initial API and implementation
 ******************************************************************************/

package jnum.data.fitting;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;

import jnum.Util;
import jnum.Verbosity;
import jnum.data.PrecisionControl;

// TODO: Auto-generated Javadoc
/**
 * The Class Minimizer.
 */
public abstract class Minimizer implements PrecisionControl, Verbosity, Penalty {
    
    /** The function. */
    private Parametric<Double> function;
    
    /** The parameters. */
    private Parameter[] parameters;
    
    /** The precision. */
    private double precision;
    
    /** The verbose. */
    private boolean verbose = false; 
    
    /** The c. */
    private CovarianceMatrix C;
    
    /** The constraints. */
    private ArrayList<Constraint> constraints = new ArrayList<Constraint>();
     
    /** The cost function. */
    private Parametric<Double> costFunction = new Parametric<Double>() {
        @Override
        public Double evaluate() { return Minimizer.this.evaluate(); } 
    };
      
    /**
     * Instantiates a new minimizer for a specified function using a set of variable parameters.
     *
     * @param function the parametric function that is to be minimized
     * @param parameters the parameters to vary during the minimization.
     */
    public Minimizer(Parametric<Double> function, Collection<? extends Parameter> parameters) {
        this(function);
        this.parameters = new Parameter[parameters.size()];
        parameters.toArray(this.parameters);
        init();
    }
   
    /**
     * Instantiates a new minimizer for a specified function using a set of variable parameters.
     *
     * @param function the parametric function that is to be minimized
     * @param parameters the parameters to vary during the minimization.
     */
    public Minimizer(Parametric<Double> function, Parameter[] parameters) {
        this(function);
        this.parameters = parameters;
        init();
    }   
    
  
    /**
     * Instantiates a new minimizer.
     *
     * @param function the function
     */
    private Minimizer(Parametric<Double> function) {
        this.function = function;
    }  
    
    public Parameter[] getParameters() { return parameters; }
    
    protected synchronized void arm() {}
    
    /**
     * The actual minimum-finding core procedure, that is the engine of the minimizer. 
     * Subclasses should implement this method with their particular minimum-finding approaches.
     * 
     * @see {@link #minimize()}
     */
    protected abstract void findMinimum();
    
    /**
     * Gets the minimum reached.
     *
     * @return the lowest value of the function encountered during the minimization process. Implemented by subclasses.
     * 
     * @see {@link #minimize()}
     */
    public abstract double getMinimum();
    
    
    /**
     * Minimize. This is a wrapper around {@link #findMinimum()} exposed to the user, which calls {@link #reset()} before it, 
     * and calculates the covariance matrix and the standard errors (when doing a {@link ChiSquared} fit) after. 
     *
     * @throws ConvergenceException if the fit did not converge.
     * @see {@link #reset()}, {@link #findMinimum()}, {@link #calcCovarianceMatrix()}, 
     *      {@link CovarianceMatrix#setParameterErrors()}
     */
    public final void minimize() throws ConvergenceException {
        minimize(1);
    }
    
    public void minimize(int attempts) throws ConvergenceException {
        double min = Double.POSITIVE_INFINITY;
        double[] bestValues = new double[parameters()];
        int successes = 0;
        
        for(int i=0; i<attempts; i++) {
            reset();
            arm();
        
            try { findMinimum(); }
            catch(ConvergenceException e) {
                if(attempts == 1) throw e; 
                continue;
            }
            
            double value = getMinimum();
            if(value < min) {
                min = value;
                for(int p=parameters(); --p >= 0; ) bestValues[p] = getParameter(p).value();
                successes++;
            }
            
        }
        
        if(successes == 0) throw new ConvergenceException(getClass().getSimpleName() + " has not converged in " + attempts + " attempt(s).");
        
        if(attempts > 1) for(int p=parameters(); --p >= 0; ) getParameter(p).setValue(bestValues[p]);
            
        try { calcCovarianceMatrix(); }
        catch(IllegalArgumentException e) {}
        
        if(getFunction() instanceof ChiSquared) calcStandardErrors();
    }
    
    
    /**
     * Initializes the minimizer. This methods is called by the constructors. Subclasses can use it to prepare their
     * own initial states. It calls {@link #reset()} by default.
     */
    protected void init() {
        setPrecision(DEFAULT_PRECISION);
        reset();
    }
    
    /**
     * Resets the minimizer to it's pre-fit default state.
     */
    protected void reset() {
        C = null;
    }
    
    /**
     * Evaluates the specified (in constructor) function, adding penalties for any constraints that are violated 
     * and/or any parameters that exceed their specified fitting range.
     *
     * @return the value, including penalties, of the function to be minimized.
     */
    public double evaluate() {  
        return getFunction().evaluate() * (1.0 + penalty());
    }
    
    /* (non-Javadoc)
     * @see jnum.data.fitting.Penalty#penalty()
     */
    @Override
    public double penalty() {
        double penalty = 0.0;
        for(Constraint constraint : getConstraints()) penalty += constraint.penalty();
        for(Parameter p : getParameters()) penalty += p.penalty();
        return penalty;
    }
    
    public final Parametric<Double> getFunction() { return function; }
    
    /**
     * Gets the parametric cost function (which returns the same value as {@link #evaluate()}), which is the 
     * specified function plus any penalties incurred for violating constraints and or exceeding parameter ranges.
     *
     * @return the cost function, including any penalties applied.
     * 
     * @see {@link #evaluate()}, {@link #penalty()}
     */
    public Parametric<Double> getCostFunction() { return costFunction; }

    
    
    /**
     * Gets the parameter with index i from the list.
     *
     * @param i the parameter index.
     * @return the parameter at the specified index.
     */
    public final Parameter getParameter(int i) {
        return getParameters()[i];
    }
    
    /**
     * Gets the number of parameters in the fit.
     *
     * @return the number of variable parameters.
     */
    public int parameters() { return getParameters().length; }
    
    /**
     * Adds an explicit constraint to the fit.
     *
     * @param constraint the applicable constraint
     * 
     * @see {@link #getConstraints()}, {@link #clearConstraints()}
     */
    public synchronized void addConstraint(Constraint constraint) {
        getConstraints().add(constraint);
    }
    
    /**
     * Remove all explicit constraints on the fit.
     * 
     * @see {@link #addConstraint(Constraint)}
     */
    public synchronized void clearConstraints() { getConstraints().clear(); }
    
    /**
     * Gets the current list of external constraints that are applied to the fit.
     *
     * @return the list of constraints
     * 
     * @see {@link #addConstraint(Constraint)}
     */
    public ArrayList<Constraint> getConstraints() { return constraints; }
    
    /* (non-Javadoc)
     * @see jnum.data.PrecisionControl#setPrecision(double)
     */
    @Override
    public void setPrecision(double x) { precision = x; }
    
    /* (non-Javadoc)
     * @see jnum.data.PrecisionControl#getPrecision()
     */
    @Override
    public double getPrecision() { return precision; }
    
    /* (non-Javadoc)
     * @see jnum.Verbosity#setVerbose(boolean)
     */
    @Override
    public void setVerbose(boolean value) { verbose = value; }
    
    /* (non-Javadoc)
     * @see jnum.Verbosity#isVerbose()
     */
    @Override
    public boolean isVerbose() { return verbose; }
     
    /**
     * Calculates covariance matrix via the numerical second derivatives (i.e. Hessian).
     */
    protected void calcCovarianceMatrix() {
        C = new CovarianceMatrix(getCostFunction(), getParameters(), 1e2 * Math.sqrt(precision));
    }
    
    /**
     * Gets the covariance matrix of the fitted parameters.
     *
     * @return the covariance matrix of the fitted parameters.
     */
    public CovarianceMatrix getCovarianceMatrix() {
       return C;
    }
    
    /**
     * Gets the correlation matrix of the fitted parameters.
     *
     * @return the correlation matrix
     */
    public CorrelationMatrix getCorrelationMatrix() {
        CovarianceMatrix C = getCovarianceMatrix();
        if(C == null) return null;
        return C.getCorrelationMatrix();
    }
    
    /**
     * Calculates standard errors for each parameter in the fit, and assigns them as uncertainties to the parameters.
     */
    public void calcStandardErrors() {
        getCovarianceMatrix().setParameterErrors();
    }
    
    /**
     * Prints the result of the fit to the standard output with the specified lead string in front of every line.
     * Useful for producing output in which the fit results are commented.
     *
     * @param lead the leading phrase that will appear at the beginning of every line of output.
     */
    public void print(String lead) { print(System.out, lead); }
    
    /**
     * Prints the result of the fit to the standard output.
     */
    public void print() { print(System.out); }
    
    /**
     * Prints the result of the fit to the specified output stream.
     *
     * @param out the output stream to print to.
     */
    public void print(PrintStream out) {
        out.println(toString());
    }
    
    /**
     * Prints the result of the fit to the specified output stream with the specified lead string in front of every line.
     * Useful for producing output in which the fit results are commented.
     *
     * @param out the output stream to print to.
     * @param lead the leading phrase that will appear at the beginning of every line of output.
     */
    public void print(PrintStream out, String lead) {
        out.println(toString(lead));
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public final String toString() {
        return toString("");
    }
    
    /**
     * Convert the result of the fit to a human readable text description, in which all lines begin with the
     * specified lead phrase. The default summary includes the minimum value reached by the fit as well as
     * the correspondign values of each parameter.
     *
     * @param lead the lead phrase to appeat in front of every line in the fit summary.
     * @return the human readable summary of the fit.
     */
    public String toString(String lead) {
        StringBuffer info = new StringBuffer(); 
                
        info.append(lead + getClass().getSimpleName() + " --> " + getFunction().getClass().getSimpleName() + ":\n\n");
        info.append(lead + "  min: " + Util.s4.format(getMinimum()) + "\n\n");
        
        for(int i=0; i<parameters(); i++) info.append(lead + "  " + getParameter(i).toString() + "\n");
        
        return new String(info);
    }
    
    /** The default precision. */
    public static double DEFAULT_PRECISION = 1e-8;
    
}
