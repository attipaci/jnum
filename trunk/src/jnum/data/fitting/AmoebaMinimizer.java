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
package jnum.data.fitting;

import java.text.DecimalFormat;
import java.util.Random;
import java.util.Vector;

import jnum.Util;


// TODO: Auto-generated Javadoc
/**
 * The Class AmoebaMinimizer.
 */
public abstract class AmoebaMinimizer extends Minimizer implements Cloneable {
	
	/** The parameter. */
	double[] parameter; // The full parameter list (fitted or not).
	
	/** The start size. */
	double[] startSize; // The target box size for the initial amoeba...
	
	/** The fit list. */
	public Vector<Integer> fitList = new Vector<Integer>(); // The indexes of parameters that are fitted.
	
	/** The point. */
	protected double point[][];
	
	/** The value. */
	protected double value[];
	
	/** The ilo. */
	protected int ihi, ilo;
	
	/** The fit parm. */
	protected double[] fitParm;

	/** The steps. */
	public int steps = 0;
	
	/** The max steps. */
	public int maxSteps = 10000;
	
	/** The small. */
	public double small;
	
	/** The verbose. */
	public boolean verbose = false;	
	
	/** The converged. */
	public boolean converged = false;
	
	/** The retry. */
	public boolean retry = false;
	

	/**
	 * Instantiates a new amoeba minimizer.
	 */
	public AmoebaMinimizer() {}

	/**
	 * Instantiates a new amoeba minimizer.
	 *
	 * @param setparms the setparms
	 */
	public AmoebaMinimizer(double[] setparms) {
		setParameters(setparms);
	}
	
	/**
	 * Instantiates a new amoeba minimizer.
	 *
	 * @param setparms the setparms
	 * @param fitList the fit list
	 */
	public AmoebaMinimizer(double[] setparms, Vector<Integer> fitList) {
		setParameters(setparms, fitList);
	}
	
	/**
	 * Sets the parameters.
	 *
	 * @param setparms the new parameters
	 */
	public void setParameters(double[] setparms) {
		parameter = setparms;
	}
	
	/**
	 * Sets the parameters.
	 *
	 * @param setparms the setparms
	 * @param fitList the fit list
	 */
	public void setParameters(double[] setparms, Vector<Integer> fitList) {
		parameter = setparms;
		this.fitList = fitList;
	}
	
	/**
	 * Sets the start size.
	 *
	 * @param size the new start size
	 */
	public void setStartSize(double[] size) { startSize = size; }
	
	/**
	 * Fit all.
	 */
	public void fitAll() {
		fitList.clear();
		for(int i=0; i<parameter.length; i++) fitList.add(i);
	}
	

	/**
	 * Inits the.
	 *
	 * @param setparms the setparms
	 */
	public void init(double[] setparms) {
		parameter = setparms;
		small = 1e-25 / precision;
		reset();
	}

	/**
	 * Reset.
	 */
	public void reset() {
		steps = -1;
		ilo = 0;
		ihi = 0;
	
		savevalue = null;
		saveparm = null;
		saveilo = -1;
		saveihi = -1;
			
		ptry = copyOf(parameter);
		psum = new double[parameter.length];		
	}
	
	/**
	 * Copy of.
	 *
	 * @param values the values
	 * @return the double[]
	 */
	public double[] copyOf(double[] values) {
		double[] copy = new double[values.length];
		System.arraycopy(values, 0, copy, 0, values.length);
		return copy;
	}
	
	
	/**
	 * Shrink init size.
	 *
	 * @param factor the factor
	 */
	public void shrinkInitSize(double factor) {
		for(int i=0; i<startSize.length; i++) startSize[i] *= factor;	
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() {
		try { 
			AmoebaMinimizer clone = (AmoebaMinimizer) super.clone();
			clone.psum = null;
			clone.ptry = null;
			clone.saveparm = null;
			clone.savevalue = null;
			clone.steps = 0;
			return clone;
		}
		catch(CloneNotSupportedException e) { return null; }
	}
	
	/** The saveparm. */
	protected double[] saveparm;
	
	/** The savevalue. */
	private double[] savevalue;
	
	/** The saveihi. */
	private int saveilo = -1, saveihi = -1;
	
	/**
	 * Save.
	 */
	public void save() {
		saveparm = copyOf(parameter);
		savevalue = copyOf(value);
		saveilo = ilo;
		saveihi = ihi;
	}
	
	/**
	 * Restore.
	 */
	public void restore() {
		parameter = copyOf(saveparm);
		value = copyOf(savevalue);
		ihi = saveihi;
		ilo = saveilo;
	}
	
	/**
	 * Copy.
	 *
	 * @return the amoeba minimizer
	 */
	public AmoebaMinimizer copy() {
		AmoebaMinimizer copy = (AmoebaMinimizer) clone();
		
		if(parameter != null) copy.parameter = copyOf(parameter);
		copy.fitList = new Vector<Integer>();
		copy.fitList.addAll(fitList);
		
		copy.fitParm = null;
		copy.point = null;
		copy.value = null;
		copy.ilo = 0;
		copy.ihi = 0;
		
		return copy;
	}
		
	/**
	 * Gets the standard error.
	 *
	 * @param i the i
	 * @param delta the delta
	 * @return the standard error
	 */
	public double getStandardError(int i, double delta) {
		double[] p = point[ilo];
		double y0 = value[ilo];	
		
		p[i] += delta;
		double yp = evaluate(p);
		p[i] -= 2.0 * delta;
		double ym = evaluate(p);
		
		p[i] += delta;
		
		return delta / Math.sqrt(0.5*(yp + ym) - y0);
	}
	
	/**
	 * Gets the total error.
	 *
	 * @param i the i
	 * @param delta the delta
	 * @return the total error
	 */
	public double getTotalError(int i, double delta) {
		//if(true) return getStandardError(i, epsilon);
		
		AmoebaMinimizer copy = copy();
		
		copy.fitList.clear();
		for(int j=0; j<fitList.size(); j++) if(fitList.get(j) != i) copy.fitList.add(fitList.get(j)); 
		
		double dchi2 = -getChi2();

		copy.parameter[i] = parameter[i] + delta;
		if(copy.fitList.isEmpty()) dchi2 += 0.5 * copy.evaluate(copy.parameter);
		else {
			copy.minimize();
			dchi2 += 0.5 * copy.getChi2();
		}

		copy.parameter[i] = parameter[i] - delta;
		if(copy.fitList.isEmpty()) dchi2 += 0.5 * copy.evaluate(copy.parameter);
		else {
			copy.minimize();
			dchi2 += 0.5 * copy.getChi2();
		}
		
		return delta / Math.sqrt(dchi2);
	}

	/**
	 * Arm.
	 */
	public void arm() {
		steps = 0;
		
		if(fitList.isEmpty()) fitAll();
		
		fitParm = new double[fitList.size()];
		if(verbose) System.err.println("Fitting " + fitParm.length + " parameters.");
		for(int i=0; i<fitParm.length; i++) fitParm[i] = parameter[fitList.get(i)];
		
		value = new double[fitParm.length+1];
		if(startSize == null) {
			startSize = new double[parameter.length];
			for(int i=0; i<parameter.length; i++) startSize[i] = 0.01 * parameter[i];
		}
		
		Random random = new Random();
		
		point = new double[fitParm.length+1][fitParm.length];
		for(int i=0; i<point.length; i++) for(int j=0; j<fitParm.length; j++)
			point[i][j] = fitParm[j] + startSize[fitList.get(j)] * random.nextGaussian();
	
		ptry = copyOf(parameter);
		psum = new double[parameter.length];
		
		for(int i=0; i<point.length; i++) {
			for(int j=0; j<fitParm.length; j++) ptry[fitList.get(j)] = point[i][j];
			value[i] = evaluate(ptry);
		}

		for(int i=1; i<point.length; i++) {
			if(value[i] < value[ilo]) ilo = i;
			if(value[i] > value[ihi]) ihi = i;
		}

		if(verbose) System.err.println("Minimizer> Initial chi^2=" + new DecimalFormat("0.00E0").format(value[0]));
		
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.data.Minimizer#minimize()
	 */
	@Override
	public double minimize() throws IllegalStateException {
		return minimize(1);
	}
	

	
	/**
	 * Minimize.
	 *
	 * @param iter the iter
	 * @return the double
	 * @throws IllegalStateException the illegal state exception
	 */
	public double minimize(int iter) throws IllegalStateException {	
		reset();

		int totalSteps = 0;
		converged = false;
		
		for(int i=0; i<iter; i++) {			
			try { 
				arm();
				amoeba();
				
				converged = true;	
				
				totalSteps += steps;
				
				// Set the parameters of the amoeba to their minimum value
				fitParm = point[ilo];			
				for(int k=0; k<fitList.size(); k++) parameter[fitList.get(k)] = fitParm[k];
				
				if(savevalue != null) {
					if(value[ilo] > small) if(value[ilo] < savevalue[saveilo]) save();
					else restore();
				}
				else save();
				
				postFit();
			}
			catch(IllegalStateException e) {
				if(verbose) System.err.println(e.getMessage());
				if(retry) i--; 
			}
		}

		if(!converged) throw new IllegalStateException("WARNING! downhill simplex has not converged.");
		
		if(verbose) System.err.println("\rMinimizer> Final   chi^2=" + new DecimalFormat("0.00E0").format(getChi2()) + " in " + totalSteps + " steps.");
	
		// Apply the final fit parameters by evaluating them, and return the associated chi^2
		return evaluate(parameter);
	}
	
	/**
	 * Post fit.
	 */
	public void postFit() {}
	
	/**
	 * Amoeba.
	 *
	 * @throws IllegalStateException the illegal state exception
	 */
	private void amoeba() throws IllegalStateException {
		final int fitParms = fitParm.length;

		for(int j=0; j<fitParms; j++) {
			double sum = 0.0;
			for (int i=0;i<=fitParms;i++) sum += point[i][j];
			psum[fitList.get(j)]=sum;
		}

		for(;;) {
			ilo = 0;
			int inhi;
			if(value[0] > value[1]) { ihi = 0; inhi = 1; } 
			else { ihi = 1; inhi = 0; }
			for(int i=0; i <= fitParms; i++) {
				if(value[i] <= value[ilo]) ilo = i;
				if(value[i] > value[ihi]) {
					inhi = ihi;
					ihi = i;
				} 
				else if(value[i] > value[inhi] && i != ihi) inhi = i;
			}
			final double spread = 2.0 * Math.abs(value[ihi] - value[ilo]) / (small + Math.abs(value[ihi]) + Math.abs(value[ilo]));
			if (spread < precision) {
				double temp = value[0]; value[0] = value[ilo]; value[ilo] = temp;
				for (int i=0; i < fitParms; i++) { temp = point[0][i]; point[0][i] = point[ilo][i]; point[ilo][i] = temp; }
				break;
			}
			if (steps >= maxSteps) { throw new IllegalStateException("Convergence not achieved in maximum allowed steps."); }
			steps += 2;
			
			double ytry = trySum(ihi,-1.0);
	
			if (ytry <= value[ilo]) ytry = trySum(ihi,2.0);
			else if (ytry >= value[inhi]) {
				final double ysave = value[ihi];
				ytry = trySum(ihi,0.5);
				
				if (ytry >= ysave) {
					for (int i=0; i <= fitParms; i++) if (i != ilo) {
						for(int j=0; j < fitParms; j++)
							point[i][j] = psum[fitList.get(j)] = 0.5 * (point[i][j] + point[ilo][j]);
						value[i] = evaluate(psum);
					}
					steps += fitParms;

					for(int j=0; j < fitParms; j++) {
						double sum = 0.0;
						for(int i=0; i <= fitParms; i++) sum += point[i][j];
						psum[j] = sum;
					}

				}
			} 
			else --steps;
			
			if(verbose) System.err.print("\r" + steps + " --> " + Util.e6.format(value[ilo]) + "     ");
		}
	}

	
	/** The ptry. */
	private double[] ptry;
	
	/** The psum. */
	private double[] psum;
	
	/**
	 * Try sum.
	 *
	 * @param ihi the ihi
	 * @param scale the scale
	 * @return the double
	 */
	private final double trySum(final int ihi, final double scale)
	{
		final double a = (1.0 - scale) / fitParm.length;
		final double b = scale - a;
		
		for(int j=0; j < fitParm.length; j++) {
			final int origj = fitList.get(j);
			ptry[origj] = a * psum[origj] + b * point[ihi][j];
		}
		
		final double ytry = evaluate(ptry);
		
		if (ytry < value[ihi]) {
			value[ihi] = ytry;
			for (int j=0; j < fitParm.length; j++) {
				final int origj = fitList.get(j);
				psum[origj] += ptry[origj] - point[ihi][j];
				point[ihi][j] = ptry[origj];
			}
		}
		return ytry;
	}	

	/**
	 * Rescale chi2.
	 *
	 * @param factor the factor
	 */
	public void rescaleChi2(double factor) {
		for(int i=0; i<value.length; i++) value[i] *= factor;
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.data.Minimizer#getChi2()
	 */
	@Override
	public double getChi2() {
		return value[ilo];
	}

	/* (non-Javadoc)
	 * @see kovacs.util.data.Minimizer#getFitParameters()
	 */
	@Override
	public double[] getFitParameters() {
		return fitParm;
	}
}


