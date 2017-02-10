/*******************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila[AT]sigmyne.com>.
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
package jnum.data;

import java.util.Arrays;
import java.util.Collection;

import jnum.math.Coordinate2D;

// TODO: Auto-generated Javadoc
/**
 * The Class Statistics.
 */
public final class Statistics {

	/**
	 * Median.
	 *
	 * @param data the data
	 * @return the double
	 */
	public static double median(final double[] data) { return median(data, 0, data.length); }

	/**
	 * Median.
	 *
	 * @param data the data
	 * @param fromIndex the from index
	 * @param toIndex the to index
	 * @return the double
	 */
	public static double median(final double[] data, final int fromIndex, final int toIndex) {
		Arrays.sort(data, fromIndex, toIndex);
		final int n = toIndex - fromIndex;
		return n % 2 == 0 ? 0.5 * (data[fromIndex + n/2-1] + data[fromIndex + n/2]) : data[fromIndex + (n-1)/2];
	}

	/**
	 * Median.
	 *
	 * @param data the data
	 * @return the float
	 */
	public static float median(final float[] data) { return median(data, 0, data.length); }

	/**
	 * Median.
	 *
	 * @param data the data
	 * @param fromIndex the from index
	 * @param toIndex the to index
	 * @return the float
	 */
	public static float median(final float[] data, final int fromIndex, final int toIndex) {
		Arrays.sort(data, fromIndex, toIndex);
		final int n = toIndex - fromIndex;
		return n % 2 == 0 ? 0.5F * (data[fromIndex + n/2-1] + data[fromIndex + n/2]) : data[fromIndex + (n-1)/2];
	}

	/**
	 * Median.
	 *
	 * @param data the data
	 * @return the weighted point
	 */
	public static WeightedPoint median(final WeightedPoint[] data) { return median(data, 0, data.length); }
	
	/**
	 * Median.
	 *
	 * @param data the data
	 * @param result the result
	 */
	public static void median(final WeightedPoint[] data, final WeightedPoint result) { median(data, 0, data.length, result); }

	/**
	 * Median.
	 *
	 * @param data the data
	 * @param fromIndex the from index
	 * @param toIndex the to index
	 * @return the weighted point
	 */
	public static WeightedPoint median(final WeightedPoint[] data, final int fromIndex, final int toIndex) {
		return smartMedian(data, fromIndex, toIndex, 1.0);		
	}
	
	/**
	 * Median.
	 *
	 * @param data the data
	 * @param fromIndex the from index
	 * @param toIndex the to index
	 * @param result the result
	 */
	public static void median(final WeightedPoint[] data, final int fromIndex, final int toIndex, final WeightedPoint result) {
		smartMedian(data, fromIndex, toIndex, 1.0, result);		
	}
	
	/**
	 * Smart median.
	 *
	 * @param data the data
	 * @param fromIndex the from index
	 * @param toIndex the to index
	 * @param maxDependence the max dependence
	 * @return the weighted point
	 */
	public static WeightedPoint smartMedian(final WeightedPoint[] data, final int fromIndex, final int toIndex, final double maxDependence) {
		final WeightedPoint result = new WeightedPoint();
		smartMedian(data, fromIndex, toIndex, maxDependence, result);
		return result;		
	}

	/**
	 * Smart median.
	 *
	 * @param data the data
	 * @param from the from
	 * @param to the to
	 * @param maxDependence the max dependence
	 * @param result the result
	 */
	public static void smartMedian(final WeightedPoint[] data, final int from, final int to, final double maxDependence, WeightedPoint result) {
		// If no data, then 
		if(to == from) {
			result.noData();
			return;
		}
		
		if(to - from == 1) {
			result.copy(data[from]);
			return;
		}
	
		Arrays.sort(data, from, to);
	
		// wt is the sum of all weights
		// wi is the integral sum including the current point.
		double wt = 0.0, wmax = 0.0;
		
		for(int i=to; --i >= from; ) {
			final double w = data[i].weight();
			if(w > 0.0) {
				wt += w;
				if(w > wmax) wmax = w;
			}
		}
	
		// If a single datum dominates, then return the weighted mean...
		if(wmax >= maxDependence * wt) {
			double sum=0.0, sumw=0.0;
			for(int i = to; --i >= from; ) {
				final double w = data[i].weight();
				if(w > 0.0) {
					sum += w * data[i].value();
					sumw += w;
				}
			}
			result.setValue(sum/sumw);
			result.setWeight(sumw);
			return;
		}
		
		// If all weights are zero return the arithmetic median...
		// This should never happen, but just in case...
		if(wt == 0.0) {
			final int n = to - from;
			result.setValue(n % 2 == 0 ? 
					0.5F * (data[from + n/2-1].value() + data[from + n/2].value()) 
					: data[from + (n-1)/2].value());
			result.setWeight(0.0);
			return;
		}
	
	
		final double midw = 0.5 * wt; 
		int ig = from; 
		
		WeightedPoint last = WeightedPoint.NaN;
		WeightedPoint point = data[from];
	
		double wi = point.weight();
		
		while(wi < midw) if(data[++ig].weight() > 0.0) {
			last = point;
			point = data[ig];	    
			wi += 0.5 * (last.weight() + point.weight());    
		}
		
		final double wplus = wi;
		final double wminus = wi - 0.5 * (last.weight() + point.weight());
		
		final double w1 = (wplus - midw) / (wplus + wminus);
		result.setValue(w1 * last.value() + (1.0-w1) * point.value());
		result.setWeight(wt);
	}

	/**
	 * Select.
	 *
	 * @param data the data
	 * @param fraction the fraction
	 * @param fromIndex the from index
	 * @param toIndex the to index
	 * @return the double
	 */
	public static double select(double[] data, double fraction, int fromIndex, int toIndex) {
		Arrays.sort(data, fromIndex, toIndex);
		return data[fromIndex + (int)Math.round(fraction * (toIndex - fromIndex - 1))];
	}

	/**
	 * Select.
	 *
	 * @param data the data
	 * @param fraction the fraction
	 * @param fromIndex the from index
	 * @param toIndex the to index
	 * @return the float
	 */
	public static float select(float[] data, double fraction, int fromIndex, int toIndex) {
		Arrays.sort(data, fromIndex, toIndex);
		return data[fromIndex + (int)Math.floor(fraction * (toIndex - fromIndex - 1))];
	}
	
	/**
	 * Robust mean.
	 *
	 * @param data the data
	 * @param tails the tails
	 * @return the float
	 */
	public static float robustMean(final float[] data, final double tails) {
		return robustMean(data, 0, data.length, tails);
	}
	
	/**
	 * Robust mean.
	 *
	 * @param data the data
	 * @param from the from
	 * @param to the to
	 * @param tails the tails
	 * @return the float
	 */
	public static float robustMean(final float[] data, int from, int to, final double tails) {
		Arrays.sort(data, from, to);
		
		// Ignore the tails on both sides of the distribution...
		final int dn = (int) Math.round(tails * (to - from));
	
		to -= dn;
		from += dn;
		if(from >= to) return Float.NaN;

		// Average over the middle section of values...
		double sum = 0.0;
		for(int i = to; --i >= from; ) sum += data[i];
		return (float) (sum / (to - from));
	}
	
	/**
	 * Robust mean.
	 *
	 * @param data the data
	 * @param tails the tails
	 * @return the double
	 */
	public static double robustMean(final double[] data, final double tails) {
		return robustMean(data, 0, data.length, tails);
	}
	
	/**
	 * Robust mean.
	 *
	 * @param data the data
	 * @param from the from
	 * @param to the to
	 * @param tails the tails
	 * @return the double
	 */
	public static double robustMean(final double[] data, int from, int to, final double tails) {
		Arrays.sort(data, from, to);
		
		// Ignore the tails on both sides of the distribution...
		final int dn = (int) Math.round(tails * (to - from));
	
		to -= dn;
		from += dn;
		if(from >= to) return Double.NaN;

		// Average over the middle section of values...
		double sum = 0.0;
		for(int i = to; --i >= from; ) sum += data[i];
		return sum / (to - from);
	}
	
	/**
	 * Robust mean.
	 *
	 * @param data the data
	 * @param tails the tails
	 * @return the weighted point
	 */
	public static WeightedPoint robustMean(final WeightedPoint[] data, final double tails) {
		return robustMean(data, 0, data.length, tails);
	}
	
	/**
	 * Robust mean.
	 *
	 * @param data the data
	 * @param tails the tails
	 * @param result the result
	 */
	public static void robustMean(final WeightedPoint[] data, final double tails, final WeightedPoint result) {
		robustMean(data, 0, data.length, tails, result);
	}
	
	/**
	 * Robust mean.
	 *
	 * @param data the data
	 * @param from the from
	 * @param to the to
	 * @param tails the tails
	 * @return the weighted point
	 */
	public static WeightedPoint robustMean(final WeightedPoint[] data, int from, int to, final double tails) {
		WeightedPoint result = new WeightedPoint();
		robustMean(data, from, to, tails, result);
		return result;
	}
	
	/**
	 * Robust mean.
	 *
	 * @param data the data
	 * @param from the from
	 * @param to the to
	 * @param tails the tails
	 * @param result the result
	 */
	public static void robustMean(final WeightedPoint[] data, int from, int to, final double tails, final WeightedPoint result) {
		if(from >= to) {
			result.noData();
			return;
		}
		
		if(to-from == 1) {
			result.copy(data[from]);
			return;
		}
		
		Arrays.sort(data, from, to);
		
		// Ignore the tails on both sides of the distribution...
		final int dn = (int) Math.round(tails * (to - from));
	
		to -= dn;
		from += dn;
		if(from >= to) {
			result.noData();
			return;
		}

		// Average over the middle section of values...
		double sum = 0.0, sumw = 0.0;
		while(--to >= from) {
			final WeightedPoint point = data[to];
			sum += point.weight() * point.value();
			sumw += point.weight();
		}
		result.setValue(sum / sumw);
		result.setWeight(sumw);
	}

	/**
	 * Line fit.
	 *
	 * @param y the y
	 * @param w the w
	 * @return the double[]
	 */
	public static double[] lineFitIndexed(final double[] y, final double[] w) {
		double s=0.0, sx=0.0, sy=0.0, sxx=0.0, sxy=0.0;
	
		for(int i=y.length; --i >= 0; ) {
			final double wi = w[i];
			s += wi;
			sy += wi*y[i];
			
			final double wx = wi * i;	
			sx += wx;
			sxx += wx*i;
			sxy += wx*y[i];
		}
		final double D = s*sxx - sx*sx;
	
		return new double[] { (sxx*sy - sx*sxy) / D, (s*sxy - sx*sy) / D };
	}
	
	/**
	 * Line fit indexed.
	 *
	 * @param y the y
	 * @param w the w
	 * @return the double[]
	 */
	public static double[] lineFitIndexed(final float[] y, final float[] w) {
		double s=0.0, sx=0.0, sy=0.0, sxx=0.0, sxy=0.0;
	
		for(int i=y.length; --i >= 0; ) {
			final double wi = w[i];
			s += wi;
			sy += wi*y[i];
			
			final double wx = wi * i;	
			sx += wx;
			sxx += wx*i;
			sxy += wx*y[i];
		}
		final double D = s*sxx - sx*sx;
	
		return new double[] { (sxx*sy - sx*sxy) / D, (s*sxy - sx*sy) / D };
	}


	/**
	 * Line fit.
	 *
	 * @param x the x
	 * @param y the y
	 * @param w the w
	 * @return the double[]
	 */
	public static double[] lineFit(final double[] x, final double[] y, final double[] w) {
		double s=0.0, sx=0.0, sy=0.0, sxx=0.0, sxy=0.0;
	
		for(int i=y.length; --i >= 0; ) {
			final double wi = w[i];
			s += wi;
			sy += wi * y[i];
			
			final double wx = w[i] * x[i];
			
			sx += wx;
			sxx += wx * x[i];
			sxy += wx * y[i];
		}
		final double D = s*sxx - sx*sx;
	
		return new double[] { (sxx*sy - sx*sxy) / D, (s*sxy - sx*sy) / D };
	}
	
	/**
	 * Line fit.
	 *
	 * @param x the x
	 * @param y the y
	 * @param w the w
	 * @return the double[]
	 */
	public static double[] lineFit(final float[] x, final float[] y, final float[] w) {
		double s=0.0, sx=0.0, sy=0.0, sxx=0.0, sxy=0.0;
	
		for(int i=y.length; --i >= 0; ) {
			final double wi = w[i];
			s += wi;
			sy += wi * y[i];
			
			final double wx = w[i] * x[i];
			
			sx += wx;
			sxx += wx * x[i];
			sxy += wx * y[i];
		}
		final double D = s*sxx - sx*sx;
	
		return new double[] { (sxx*sy - sx*sxy) / D, (s*sxy - sx*sy) / D };
	}
	
	/**
	 * Line fit.
	 *
	 * @param x the x
	 * @param y the y
	 * @return the double[]
	 */
	public static double[] lineFit(final double[] x, final WeightedPoint[] y) {
		double s=0.0, sx=0.0, sy=0.0, sxx=0.0, sxy=0.0;
	
		for(int i=y.length; --i >= 0; ) {
			final WeightedPoint yi = y[i];
			final double w = yi.weight();
			s += w;
			sy += w * yi.value();
			
			final double wx = w * x[i];
			sx += wx;	
			sxx += wx * x[i];
			sxy += wx * yi.value();
		}
		final double D = s*sxx - sx*sx;
	
		return new double[] { (sxx*sy - sx*sxy) / D, (s*sxy - sx*sy) / D };
	}

	/**
	 * Line fit.
	 *
	 * @param x the x
	 * @param y the y
	 * @return the double[]
	 */
	public static double[] lineFit(final double[] x, final double[] y) {
		double s=0.0, sx=0.0, sy=0.0, sxx=0.0, sxy=0.0;
	
		for(int i=y.length; --i >= 0; ) {
			sy += y[i];
			sx += x[i];	
			sxx += x[i] * x[i];
			sxy += x[i] * y[i];
		}
		final double D = y.length*sxx - sx*sx;
	
		return new double[] { (sxx*sy - sx*sxy) / D, (s*sxy - sx*sy) / D };
	}
	
	/**
	 * Line fit.
	 *
	 * @param x the x
	 * @param y the y
	 * @return the double[]
	 */
	public static double[] lineFit(final float[] x, final float[] y) {
		double s=0.0, sx=0.0, sy=0.0, sxx=0.0, sxy=0.0;
	
		for(int i=y.length; --i >= 0; ) {
			sy += y[i];
			sx += x[i];	
			sxx += x[i] * x[i];
			sxy += x[i] * y[i];
		}
		final double D = y.length*sxx - sx*sx;
	
		return new double[] { (sxx*sy - sx*sxy) / D, (s*sxy - sx*sy) / D };
	}
	
	
	/**
	 * Line fit.
	 *
	 * @param v the v
	 * @return the double[]
	 */
	public static double[] lineFit(final Coordinate2D[] v) {
		double s=0.0, sx=0.0, sy=0.0, sxx=0.0, sxy=0.0;
	
		for(int i=v.length; --i >= 0; ) {
			Coordinate2D c = v[i];
			sy += c.y();
			sx += c.x();	
			sxx += c.x() * c.x();
			sxy += c.x() * c.y();
		}
		final double D = v.length*sxx - sx*sx;
	
		return new double[] { (sxx*sy - sx*sxy) / D, (s*sxy - sx*sy) / D };
	}
	
	

	/**
	 * Line fit.
	 *
	 * @param v the v
	 * @return the double[]
	 */
	public static double[] lineFit(final Collection<Coordinate2D> v) {
		double s=0.0, sx=0.0, sy=0.0, sxx=0.0, sxy=0.0;
	
		for(Coordinate2D c : v) {
			sy += c.y();
			sx += c.x();	
			sxx += c.x() * c.x();
			sxy += c.x() * c.y();
		}
		final double D = v.size()*sxx - sx*sx;
	
		return new double[] { (sxx*sy - sx*sxy) / D, (s*sxy - sx*sy) / D };
	}
	
	/** The Constant medianNormalizedVariance. */
	// median(x^2) = 0.454937 * sigma^2 
	public static final double medianNormalizedVariance = 0.454937;
	
}
