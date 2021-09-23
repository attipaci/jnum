/* *****************************************************************************
 * Copyright (c) 2021 Attila Kovacs <attila[AT]sigmyne.com>.
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
package jnum.math.specialfunctions;


import jnum.ConsiderateFunction;
import jnum.Constant;
import jnum.Function;
import jnum.math.Complex;
import jnum.math.SphericalCoordinates;

/**
 * Spherical harmomincs Y<sub>l</sub><sup>m</sup>(&theta;, &phi;). Spherical harmonics constritute a set of orthogonal functions (under
 * the overlap integral) in spherical coordinates (&theta;, &phi;). They are commonly used in quantum mechanics.
 * 
 * @author Attila Kovacs
 *
 * @see LegendrePolynomial
 */
public class SphericalHarmonics implements Function<SphericalCoordinates, Complex>, ConsiderateFunction<SphericalCoordinates, Complex> {

    /** the index l for this instance. */
    private int l;
    
    /** the index m for this instance. */
    private int m;

    /** A precalculated normalization coefficient */
    private double K;

    /**
     * Instantiates new spherical harmonics Y<sub>l</sub><sup>m</sup>.
     * 
     * @param l    the harmonic index <i>l</i> for this instance.
     * @param m    the harmonic index <i>m</i> for this instance.
     * 
     * @throws IllegalArgumentException if <i>l</i> or <i>m</i> are invalid.
     */
    public SphericalHarmonics(int l, int m) throws IllegalArgumentException {
        if(l < 0) throw new IllegalArgumentException("l cannot be negative");
        if(Math.abs(m) > l) throw new IllegalArgumentException("|m| cannot be greater than l");
        this.l = l;
        this.m = m;
        K = Math.sqrt((2.0 * l + 1.0) / Constant.fourPi * Factorial.at(l - m) / Factorial.at(l + m));
    }

    /**
     * Gets the index l for this Y<sub>l</sub><sup>m</sup> instance.
     * 
     * @return     the index l for this Y<sub>l</sub><sup>m</sup> instance.
     */
    public final int getL() { return l; }

    /**
     * Gets the index m for this Y<sub>l</sub><sup>m</sup> instance.
     * 
     * @return     the index m for this Y<sub>l</sub><sup>m</sup> instance.
     */
    public final int getM() { return m; }

    /**
     * Evaluates this spherical harmonic instance at the spherical coordinates &theta;, &phi;.
     * 
     * @param theta (rad) spherical pole angle &theta;.
     * @param phi   (rad) spherical longitude angle &phi;. 
     * @return      Y<sub>l</sub><sup>m</sup>(&theta;, &phi;)
     */
    public final Complex valueAt(double theta, double phi) {
        Complex result = new Complex();
        evaluate(theta, phi, result);
        return result;
    }

    @Override
    public final Complex valueAt(SphericalCoordinates coords) {
        Complex result = new Complex();
        evaluate(coords, result);
        return result;
    }

    /**
     * Evaluates this spherical harmonic instance at the spherical coordinates &theta;, &phi; and returns
     * the result in the supplied complex number.
     * 
     * @param theta     (rad) spherical pole angle &theta;.
     * @param phi       (rad) spherical longitude angle &phi;. 
     * @param result    complex number in which to return Y<sub>l</sub><sup>m</sup>(&theta;, &phi;).
     */
    public final void evaluate(double theta, double phi, Complex result) {
        evaluateAt(l, m, theta, phi, K, result);
    }

    @Override
    public final void evaluate(SphericalCoordinates coords, Complex result) {
        evaluateAt(l, m, coords, K, result);
    }


    /**
     * Gets the value of Y<sub>l</sub><sup>m</sup>(&theta;, &phi;).
     * 
     * @param l     harmonic index <i>l</i>.
     * @param m     harmonic index <i>m</i>.
     * @param theta (rad) spherical pole angle &theta;.
     * @param phi   (rad) spherical longitude angle &phi;. 
     * @return      Y<sub>l</sub><sup>m</sup>(&theta;, &phi;).
     */
    public static Complex at(int l, int m, double theta, double phi) {
        Complex result = new Complex();
        evaluateAt(l, m, theta, phi, result);
        return result;      
    }

    /**
     * Gets the value of Y<sub>l</sub><sup>m</sup>(&theta;, &phi;), returning the result in
     * the supplied complex argument.
     * 
     * @param l     harmonic index <i>l</i>.
     * @param m     harmonic index <i>m</i>.
     * @param theta (rad) spherical pole angle &theta;.
     * @param phi   (rad) spherical longitude angle &phi;. 
     * @param result      complex number in which to return Y<sub>l</sub><sup>m</sup>(&theta;, &phi;).
     */
    public static void evaluateAt(int l, int m, double theta, double phi, Complex result) {
        final double K = Math.sqrt((2*l+1) / Constant.fourPi * Factorial.at(l-m)/Factorial.at(l+m));
        evaluateAt(l, m, theta, phi, K, result);
    }

    /**
     * Gets the value of Y<sub>l</sub><sup>m</sup>(&theta;, &phi;), returning the result in
     * the supplied complex argument.
     * 
     * @param l     harmonic index <i>l</i>.
     * @param m     harmonic index <i>m</i>.
     * @param theta (rad) spherical pole angle &theta;.
     * @param phi   (rad) spherical longitude angle &phi;. 
     * @param K     Normalization constant to use.
     * @param result      complex number in which to return Y<sub>l</sub><sup>m</sup>(&theta;, &phi;).
     */
    private static void evaluateAt(int l, int m, double theta, double phi, double K, Complex result) {
        if(m < 0) {
            evaluateAt(l, -m, theta, phi, result);
            if((m & 1) != 0) result.flip();
            result.conjugate();
            return;
        }

        final double r = K * LegendrePolynomial.at(l, m, Math.cos(theta));
        result.setPolar(r, m * phi);
    }


    /**
     * Gets the value of Y<sub>l</sub><sup>m</sup>(&theta;, &phi;), returning the result in
     * the supplied complex argument.
     * 
     * @param l     harmonic index <i>l</i>.
     * @param m     harmonic index <i>m</i>.
     * @param coords    spherical coordinates (lon, lat) at which to evaluate  Y<sub>l</sub><sup>m</sup>. 
     * @param result    complex number in which to return Y<sub>l</sub><sup>m</sup>(&theta;, &phi;).
     */
    public static void evaluateAt(int l, int m, SphericalCoordinates coords, Complex result) {
        final double K = Math.sqrt((2*l+1) / Constant.fourPi * Factorial.at(l-m)/Factorial.at(l+m));
        evaluateAt(l, m, coords, K, result);
    }

    /**
     * Gets the value of Y<sub>l</sub><sup>m</sup>(&theta;, &phi), returning the result in
     * the supplied complex argument.
     * 
     * @param l         harmonic index <i>l</i>.
     * @param m         harmonic index <i>m</i>.
     * @param coords    spherical coordinates (lon, lat) at which to evaluate  Y<sub>l</sub><sup>m</sup>. 
     * @param K         Normalization constant to use.
     * @param result    complex number in which to return Y<sub>l</sub><sup>m</sup>(&theta;, &phi/).
     */
    private static void evaluateAt(int l, int m, SphericalCoordinates coords, double K, Complex result) {
        if(m < 0) {
            evaluateAt(l, -m, coords, result);
            if((m & 1) != 0) result.flip();
            result.conjugate();
            return;
        }

        final double r = K * LegendrePolynomial.at(l, m, coords.sinLat());
        result.setPolar(r, m * coords.longitude());
    }


}
