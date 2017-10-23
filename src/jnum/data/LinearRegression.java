/*******************************************************************************
 * Copyright (c) 2017 Attila Kovacs <attila[AT]sigmyne.com>.
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


public class LinearRegression {

    private int n = 0;
    private double sumw = 0.0, sumwx = 0.0, sumwy = 0.0, sumwxy = 0.0, sumwxx = 0.0; 

    private double currentDenominator;
    private boolean isCurrent;
    
    public final void add(double x, WeightedPoint point) {
        add(x, point.value(), point.weight());
    }

    public synchronized void add(final double x, final double y, final double w) {
        isCurrent = false;
        n++;
        sumw += w;
        sumwx += w * x;
        sumwy += w * y;
        sumwxy += w * x * y;
        sumwxx += w * x * x;
    }

    public final void remove(double x, WeightedPoint point) {
        remove(x, point.value(), point.weight());
    }

    public synchronized void remove(final double x, final double y, final double w) {
        isCurrent = false;
        n--;
        sumw -= w;
        sumwx -= x;
        sumwy -= y;
        sumwxy -= x * y;
        sumwxx -= x * x;
    }

    public int points() { return n; }


    public synchronized double getDenominator() {
        if(isCurrent) return currentDenominator;
        isCurrent = true;
        return currentDenominator = sumw * sumwxx - sumwx * sumwx;
    }

    public synchronized DataPoint getIntercept() {
        double delta = getDenominator();
        return new DataPoint((sumwxx * sumwy - sumwx * sumwxy) / delta, delta / sumwxx);
    }


    public synchronized DataPoint getSlope() {
        double delta = getDenominator();
        return new DataPoint((sumw * sumwxy - sumwx * sumwy) / delta, delta / sumw);
    }

    public synchronized Coefficients getCoefficients() {
        Coefficients coeff = new Coefficients();
        coeff.a = getSlope();
        coeff.b = getIntercept();
        return coeff;
    }
    
    public double getChi2() {
        // TODO
        return Double.NaN;
    }
    
    public synchronized double getParameterCovariance() { return -sumwx / getDenominator(); }

    public synchronized double getParameterCorrelation() { return -sumwx / Math.sqrt(sumwxx); }

   
    
    public class Coefficients {
        DataPoint a, b;
    }
    
    
    
  
    
    
    
}
