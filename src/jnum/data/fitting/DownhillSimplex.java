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

import java.util.Collection;
import java.util.Random;

import jnum.Util;
import jnum.math.Scalable;

public class DownhillSimplex extends Minimizer implements Scalable {        
    private double[][] point;   // The simplex vertexes...
    private double[] psum;      // The midpoint of the simplex.

    private double[] value;     // The function values at the simplex points...
    private int ihi, ilo;

    private int steps;
    private int maxSteps = DEFAULT_MAXSTEPS;
    private double tinyValue;

    private double scaleSize = 1.0;

    private Random random;

    public DownhillSimplex(Parametric<Double> function, Collection<? extends Parameter> parameters) {
        super(function, parameters);
    }

    public DownhillSimplex(Parametric<Double> function, Parameter[] parameters) {
        super(function, parameters);
    }

    public int getSteps() { return steps; }

    public void setMaxSteps(int N) { maxSteps = N; }

    public int getMaxSteps() { return maxSteps; }

    @Override
    public void scale(double factor) { scaleSize *= factor; }

    public void setScaleSize(double x) { scaleSize = x; }
    
    public double getScaleSize() { return scaleSize; }

    @Override
    public void setPrecision(double x) {
        super.setPrecision(x);
        tinyValue = 1e-25 * x;
    }

    @Override
    public boolean hasConverged() {
        if(steps <= 0) return false;
        if(steps >= maxSteps) return false;
        return true;
    }

    @Override
    protected void init() {
        super.init();
        random = new Random();    
    }

    private synchronized void arm() {
        int N = parameters();

        point = new double[N+1][N];
        value = new double[N+1];
        psum = new double[N];

        initSimplex(scaleSize);  
        calcPSum();
    }

    private synchronized void initSimplex(double scaleSize) {
        for(int i=parameters()+1; --i >= 0 ; ) {
            initSimplex(point[i], scaleSize);
            value[i] = evaluate(point[i]);
        }

        ilo = ihi = 0;
        for(int i=parameters()+1; --i > 0; ) {
            if(value[i] < value[ilo]) ilo = i;
            else if(value[i] > value[ihi]) ihi = i;   
        }
    }

    private synchronized void initSimplex(double[] pValues, double scaleSize) {
        for(int i=parameters(); --i >= 0; ) {
            Parameter p = getParameter(i);
            pValues[i] = p.value() + scaleSize * p.getStepSize() * random.nextGaussian();
        }

    }

    @Override
    protected synchronized void reset() {
        super.reset();
        steps = 0;
        ilo = 0;
        ihi = 0;
    }

    protected double evaluate(double[] values) {
        setValues(values);
        return evaluate();
    }

    private void setValues(double[] values) {
        for(int i=values.length; --i >= 0; ) getParameter(i).setValue(values[i]);
    }


    @Override
    protected synchronized void findMinimum() throws ConvergenceException {
        arm();

        if(isVerbose()) System.err.println(" Initial --> " + Util.e6.format(value[ilo]) + "     ");

        while(step()) if(isVerbose()) System.err.print("\r  " + steps + " --> " + Util.e6.format(value[ilo]) + "     ");
        setValues(point[ilo]);

        if(isVerbose()) System.err.println("\r " + steps + " --> " + Util.e6.format(value[ilo]) + "     ");

    }

    @Override
    public double getMinimum() {
        if(ilo >= 0) if(hasConverged()) return value[ilo];
        return Double.NaN;
    }


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

    private void calcPSum() {
        for(int i=parameters(); --i >= 0; ) {
            double sum = 0.0;
            for(int j=parameters()+1; --j >= 0; ) sum += point[j][i];
            psum[i] = sum;
        }
    }

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

    @Override
    public String toString(String lead) { 
        return super.toString(lead) + "\n  " + lead + (hasConverged() ? "converged in " + steps + " steps" : "not converged!");
    }


    public static int DEFAULT_MAXSTEPS = 10000;
}
