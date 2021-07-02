/* *****************************************************************************
 * Copyright (c) 2020 Attila Kovacs <attila[AT]sigmyne.com>.
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


package jnum.data;

import jnum.Unit;
import jnum.Util;

/**
 * A class representing a measured value with a corresponding RMS uncertainty. The uncertainty
 * is represented by an appropriate noise weight w = 1/rms<sup>2</sup>. The class allows 
 * accumulation and mathematical operations (including most common math functions) with
 * proper error propagation. I.e. the rms (weight) of the datum will be propagated as
 * appropriate as the datum is being operated on.
 * 
 * @author Attila Kovacs
 *
 */
public class DataPoint extends WeightedPoint {

    private static final long serialVersionUID = -7893241481449777111L;

    /**
     * Construct a new empty datum, with zero value and infinite uncertainty (i.e. zero weight).
     * 
     */
    public DataPoint() { super(); }

    /**
     * Constructs a new datum with the specified measured value and corresponding RMS uncertainty.
     * 
     * @param value     the measured value
     * @param rms       the RMS uncertainty of the measurement.
     */
    public DataPoint(double value, double rms) {
        super(value, 1.0/(rms*rms));
    }

    /**
     * Construct a new datum from a weighted value, assuming that it is noise-weighted with
     * w = 1/rms<sup>2</sup>.
     * 
     * @param template  the noise weighted value.
     */
    public DataPoint(WeightedPoint template) {
        super(template);
    }

    @Override
    public DataPoint clone() {
        return (DataPoint) super.clone();
    }

    @Override
    public DataPoint copy() {
        return (DataPoint) super.copy();
    }

    /**
     * Gets the RMS uncertainty of this datum. 
     * 
     * @return  the RMS uncertainty.
     */
    public double rms() { return 1.0/Math.sqrt(weight()); }

    /**
     * Sets the RMS uncertainty of this datum to the specified new value.
     * 
     * @param value     the new RMS uncertainty to assign to the datum.
     */
    public void setRMS(final double value) { setWeight(1.0 / (value * value)); }

    /**
     * Gets a string representation of this datum, in the format of
     * <code>value +- rms unitname</code>, after casting into the specified unit.
     * For example, 
     * 
     * <pre>
     *  DataPoint p = new DataPoint(Math.PI, 0.01 * Math.PI);
     *  System.out.println(p, Unit.get("deg"));
     * </pre>
     * 
     * will print an output something like:
     * 
     * <pre>
     *  180.0 +- 1.8 deg
     * </pre>
     * 
     * @param unit  the physical unit in which the datum is to be represented.
     * @return      the string representation of the datum in the specified units.
     */
    public String toString(Unit unit) { 
        return toString(unit, " +- ", " ");
    }
    


    @Override
    public String toString(String before, String after) {
        return toString((Unit) null, before, after);
    }


    /**
     * Like {@link #toString(Unit)} but with the additional option of specifying the
     * strings that are added before and after the reported rms value.
     *    
     * @param unit      the physical unit in which the datum is to be represented.
     * @param rmsSep    the string that separates the value and the rms, such as " +- ".
     * @param unitSep   the string that separates the rms and unit such as " ".
     * @return          the string representation of the datum in the specified units and separators.
     */
    public String toString(Unit unit, String rmsSep, String unitSep) {
        double u = unit == null ? 1.0 : unit.value();
        double value = value() / u;
        double rms = rms() / u;
        double res = Math.pow(10.0, 2 - errorFigures + Math.floor(Math.log10(rms)));

        return Util.getDecimalFormat(Math.abs(value) / res, 6).format(value() / u) 
                + rmsSep + Util.s[errorFigures].format(rms) + unitSep + (unit == null ? "" : unit.name());   
    }
    
    /**
     * Gets the significance (value / rms) of this datum.
     * 
     * @return  the significance of this measurement.
     */
    public final double significance() { return significanceOf(this); }

    
    /**
     * Gets the significance of a weighted value, assuming it is noise weighted with w = 1/rms<sup>2</sup>.
     * 
     * @param point     the noise weighted value
     * @return          the significance of the noise weighted value.
     */
    public static double significanceOf(final WeightedPoint point) {
        return Math.abs(point.value()) * Math.sqrt(point.weight());
    }


    /**
     * Creates an initialized array of data points of the specified size. All elements of the
     * array are set to empty data (weight 0) initially.
     * 
     * @param size      Number of elements in the new array of data points.
     * @return          an initialized new array of data points of the specified size.
     */
    public static DataPoint[] createArray(int size) {
        final DataPoint[] p = new DataPoint[size];
        for(int i=size; --i >= 0; ) p[i] = new DataPoint();
        return p;
    }

   
   
    /**
     * The number of significant figures to print for the RMS term for the default string representations.
     * 
     */
    public static int errorFigures = 2;
}
