/*******************************************************************************
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
 *     Attila Kovacs <attila[AT]sigmyne.com> - initial API and implementation
 ******************************************************************************/

package jnum.data.fitting;

import java.util.Collection;
import java.util.Random;

import jnum.Util;
import jnum.math.Scalable;


/**
 * An implementation of the downhill simplex method of minimization.
 */
public class DownhillSimplex extends Minimizer implements Scalable {        
    
    /** The point. */
    private double[][] point;   // The simplex vertexes...
    
    /** The psum. */
    private double[] psum;      // The midpoint of the simplex.

    /** The value. */
    private double[] value;     // The function values at the simplex points...
    
    /** The ilo. */
    private int ihi, ilo;

    /** The steps. */
    private int steps;
    
    /** The max steps. */
    private int maxSteps = DEFAULT_MAXSTEPS;
    
    /** The tiny value. */
    private double tinyValue;

    /** The scale size. */
    private double scaleSize = 1.0;

    /** The random. */
    private Random random;

    /**
     * Instantiates a new downhill simplex minimizer for a specified function using a set of variable parameters.
     *
     * @param function the parametric function that is to be minimized
     * @param parameters the parameters to vary during the minimization.
     */
    public DownhillSimplex(Parametric<Double> function, Collection<? extends Parameter> parameters) {
        super(function, parameters);
    }

    /**
     * Instantiates a new downhill simplex minimizer for a specified function using a set of variable parameters.
     *
     * @param function the parametric function that is to be minimized
     * @param parameters the parameters to vary during the minimization.
     */
    public DownhillSimplex(Parametric<Double> function, Parameter[] parameters) {
        super(function, parameters);
    }

    /**
     * Gets the number of steps that were required before the simplex converged to the desired precision.
     *
     * @return the steps taken to reach the minimum to the specified precision.
     * 
     * @see {@link #setMaxSteps(int)}, {@link #setPrecision(double)}
     */
    public int getSteps() { return steps; }

    /**
     * Sets the maximum number of steps allowed for the minimization. If the simplex reaches this limit {@link #minimize()}
     * will thrw a {@link ConvergenceException}. 
     *
     * @param N the maximum number steps before giving up...
     * 
     * @see {@link #getMaxSteps()}
     */
    public void setMaxSteps(int N) { maxSteps = N; }

    /**
     * Gets the maximum number of steps allowed for minimization..
     *
     * @return the max steps
     * 
     * @see {@link #setMaxSteps(int)}
     */
    public int getMaxSteps() { return maxSteps; }

    /* (non-Javadoc)
     * @see jnum.math.Scalable#scale(double)
     */
    @Override
    public void scale(double factor) { scaleSize *= factor; }

    /**
     * Sets the scale size of the initial simplex (default is 1.0), allowing the user to control just how big the
     * simplex is initially.
     *
     * @param x the new scale size
     * 
     * @see {@link #getScaleSize()}
     */
    public void setScaleSize(double x) { scaleSize = x; }
    
    /**
     * Gets the current scale size of the initial simplex.
     *
     * @return the scale size
     * 
     * @see {@link #setScaleSize(double)}
     */
    public double getScaleSize() { return scaleSize; }

    /* (non-Javadoc)
     * @see jnum.data.fitting.Minimizer#setPrecision(double)
     */
    @Override
    public void setPrecision(double x) {
        super.setPrecision(x);
        tinyValue = 1e-25 * x;
    }

    /* (non-Javadoc)
     * @see jnum.data.fitting.Minimizer#init()
     */
    @Override
    protected void init() {
        super.init();
        random = new Random();    
    }

    /**
     * Prepares the simplex for an upcoming minimization.
     */
    @Override
    protected synchronized void arm() {
        int N = parameters();

        point = new double[N+1][N];
        value = new double[N+1];
        psum = new double[N];

        initSimplex(scaleSize);  
        calcPSum();
    }

    /**
     * Initializes the simplex, creating vertexes at random positions in the parameter space and evaluating
     * the specified function at these points.
     *
     * @param scaleSize the scale size
     * 
     * @see {@link #createVertex(double[], double)}
     */
    private synchronized void initSimplex(double scaleSize) {
        for(int i=parameters()+1; --i >= 0 ; ) {
            createVertex(point[i], scaleSize);
            value[i] = evaluate(point[i]);
        }
    }

    /**
     * Creates a new vertex for the simplex at a randomized position.
     *
     * @param pValues the local storage of the vertex parameter values.
     * @param scaleSize the scale size to use for random initialization.
     */
    private synchronized void createVertex(double[] pValues, double scaleSize) {
        for(int i=parameters(); --i >= 0; ) {
            Parameter p = getParameter(i);
            pValues[i] = p.value() + scaleSize * p.getStepSize() * random.nextGaussian();
        }

    }

    /* (non-Javadoc)
     * @see jnum.data.fitting.Minimizer#reset()
     */
    @Override
    protected synchronized void reset() {
        super.reset();
        steps = 0;
        ilo = 0;
        ihi = 0;
    }

    /**
     * Evaluate the function that is to be minimized at the specified set (vertex) of parameter values.
     *
     * @param values the parameter values to use for the evaluation
     * @return the function value, including penalties, at the given point in parameter space.
     * 
     * @see {@link #getCostFunction()}, {@link #penalty()}
     */
    protected double evaluate(double[] values) {
        setValues(values);
        return evaluate();
    }

    /**
     * Sets the parameters to the specified values.
     *
     * @param values the new values
     */
    private void setValues(double[] values) {
        for(int i=values.length; --i >= 0; ) getParameter(i).setValue(values[i]);
    }


    /* (non-Javadoc)
     * @see jnum.data.fitting.Minimizer#findMinimum()
     */
    @Override
    protected synchronized void findMinimum() throws ConvergenceException {
        arm();

        if(isVerbose()) Util.info(this, "Initial --> " + Util.e6.format(value[ilo]));

        while(step()) if(isVerbose()) System.err.print("\r  " + steps + " --> " + Util.e6.format(value[ilo]) + "     ");
        setValues(point[ilo]);

        if(isVerbose()) System.err.println("\r " + steps + " --> " + Util.e6.format(value[ilo]) + "     ");
        
        if(isVerbose()) Util.info(this, "Final --> " + Util.e6.format(value[ilo]));
    }

    /* (non-Javadoc)
     * @see jnum.data.fitting.Minimizer#getMinimum()
     */
    @Override
    public double getMinimum() {
        return value[ilo];
    }


    /**
     * Perform a single minimization step.
     *
     * @return true, if successful
     */
    private boolean step() {
        final int N = parameters();
        
        ilo = 0;
        int inhi;
        if(value[0] > value[1]) { ihi = 0; inhi = 1; } 
        else { ihi = 1; inhi = 0; }
        for(int j=N+1; --j >= 0; ) {
            if(value[j] <= value[ilo]) ilo = j;
            if(value[j] > value[ihi]) {
                inhi = ihi;
                ihi = j;
            } 
            else if(value[j] > value[inhi] && j != ihi) inhi = j;
        }
        final double spread = 2.0 * Math.abs(value[ihi] - value[ilo]) / (tinyValue + Math.abs(value[ihi]) + Math.abs(value[ilo]));
        if(spread < getPrecision()) {
            // This just moves the best vertex first. We don't care...
            //double temp = value[0]; value[0] = value[ilo]; value[ilo] = temp;
            //for(int i=N; --i >= 0; ) { temp = point[0][i]; point[0][i] = point[ilo][i]; point[ilo][i] = temp; }
            return false;
        }
        if(steps >= maxSteps) throw new ConvergenceException("Convergence not achieved in maximum allowed steps.");
        steps += 2;

        double ytry = trySum(-1.0);

        if(ytry <= value[ilo]) ytry = trySum(2.0);
        else if(ytry >= value[inhi]) {
            final double ysave = value[ihi];
            ytry = trySum(0.5);

            if(ytry >= ysave) {
                for(int i=N+1; --i >= 0; ) if(i != ilo) {
                    for(int j=N; --j >= 0; ) point[i][j] = psum[j] = 0.5 * (point[i][j] + point[ilo][j]);
                    value[i] = evaluate(psum);
                }
                steps += N;
                calcPSum();
            }
        } 
        else --steps;

        return true;
    }

    /**
     * Calculate the sum (i.e. unnormalized midpoint) of the simplex.
     */
    private void calcPSum() {
        for(int i=parameters(); --i >= 0; ) {
            psum[i] = 0.0;
            for(int j=parameters()+1; --j >= 0; ) psum[i] += point[j][i];
        }
    }

    /**
     * Explore a potential new location for a vertex.
     *
     * @param scale the scale value
     * @return the value of the function (incl. penalties) at the requested location.
     */
    private double trySum(final double scale) {
        final int N = parameters();

        final double a = (1.0 - scale) / N;
        final double b = scale - a;

        for(int j=N; --j >= 0; ) getParameter(j).setValue(a * psum[j] + b * point[ihi][j]);

        final double ytry = evaluate();

        if(ytry < value[ihi]) {
            value[ihi] = ytry;
            for (int j=N; --j >= 0; ) {
                Parameter p = getParameter(j);
                psum[j] += p.value() - point[ihi][j];
                point[ihi][j] = p.value();
            }
        }
        return ytry;
    }   

    /* (non-Javadoc)
     * @see jnum.data.fitting.Minimizer#toString(java.lang.String)
     */
    @Override
    public String toString(String lead) { 
        return super.toString(lead) + "\n  " + lead + (steps < maxSteps ? "converged in " + steps + " steps" : "not converged!");
    }


    /** The default maxsteps. */
    public static int DEFAULT_MAXSTEPS = 10000;
}
