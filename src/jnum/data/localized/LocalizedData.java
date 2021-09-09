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
package jnum.data.localized;

import jnum.Copiable;
import jnum.data.RegularData;
import jnum.math.LinearAlgebra;
import jnum.math.Metric;


/**
 * Data taken at irregular topological locations, and which can be weighted-averaged.
 * If the data is taken from regular intervals you should consider using the more efficient and
 * feature-filled  {@link RegularData} object instead to represent your regularly sampled values. This
 * data class is really for data that does not lend itself to a representation on a regular grid.
 * 
 * @author Attila Kovacs
 *
 * @param <L>   the generic type of objects that represents a location, such as a number or a vector.
 * @param <D>       the generic type of the object that accompanies the location.
 */
public class LocalizedData<L extends Locality, D extends LinearAlgebra<? super D> & Copiable<? super D> & Metric<? super D>> 
extends LocalizedObject<L, D>  {
    /** */
    private static final long serialVersionUID = 2776764506885561864L;
        
    /** Assumed to be a proper noise weight. */
    private double weight;      
    
    /** The number of measurements aggregated in this datum */
    private int measurements;

    
    /**
     * Construct a new localized data object with data at the specified locality.
     *  
     * @param location  the locality at which data was measured or is referenced at.
     * @param datum     the data at the locality.
     * @param rms       The typical RMS of for the data in distance, both for establishing a natural noise weight for it, 
     *                  and for discriminating outliers (at 5-sigma) from a 
     */
    public LocalizedData(L location, D datum, double rms) {
        super(location, datum);
        this.weight = 1.0 / (rms * rms);
        this.measurements = 1;
    }
   
    
    /**
     * Returns a new empty localized data instance of the same type as this one, tagged with the specified location marker.
     * 
     * @param loc       the locality at which the new data was measured or is referenced at.
     * @return          a new localized data, at the specified locality, of the same type as this.
     */
    @SuppressWarnings("unchecked")
    public LocalizedData<L, D> newInstanceAt(L loc) {
        
        LocalizedData<L, D> l = (LocalizedData<L, D>) clone();
        l.setLocality(loc);
        l.setData((D) getData().copy());
        l.noData();        
        
        return l;
    }

    /**
     * Empties the undelying data, e.g. so that one can begin averaging new data into this object.
     * 
     */
    public void noData() {
        getData().zero();
        weight = 0.0;
        measurements = 0;
    }

    
    /**
     * Checks if this data is consistent with the reference. The default implementation is to
     * deem them consistent if the distance between this datum and the reference value is 
     * less than 5 times the rms of this 
     * 
     * @param reference
     * @return      <code>true</code> if this data is consistent with the reference, otherwise <code>false</code>.
     */
    public boolean isConsistentWith(D reference) {
        if(reference == null) return true;
        return getData().distanceTo(reference) * Math.sqrt(weight) < 5.0;
    }
    
    /**
     * Gets the number of measurements for this datum. The measurement count is accumulated as
     * localized data is averaged together.
     * 
     * @return the total number of averaged localized data instances represented by this object 
     * 
     * @see #averageWith(LocalizedData, double)
     */
    public int getCount() {
        return measurements;
    }

    /**
     * Returns the noise weight for this datum.
     * 
     * @return      1/rms<sup>2</sup> for this datum.
     */
    public double weight() {
        return weight;
    }
    
    /**
     * Gets the RMS uncertainty of this datum, either as it was defined at creation or what resulted from
     * averaging with other data.
     * 
     * @return      The RMS uncertainty of this value.
     */
    public double rms() {
        return 1.0 / Math.sqrt(weight);
    }
    

    
    /**
     * Averages localized datum into this value, with the specified weight.
     * 
     * @param other         another localized datum whose underlying data is to be averaged with this one.
     * @param weight        a weighting for the other datum, typically a noise weight with some additional relative
     *                      weight adjustment factor.
     */
    public void averageWith(LocalizedData<L, D> other, double weight) {
        averageWith(other.getData(), weight * other.weight());
        measurements += other.measurements;
    }

    /**
     * Averages another datum of the same underlying data type into this one, using the specified weight.
     * 
     * @param other         The other datum
     * @param weight        a weighting for the other datum, typically a noise weight with some additional relative
     *                      weight adjustment factor.
     */
    protected final void averageWith(D other, double weight) {
        double w = this.weight + weight;

        D value = getData();
        value.scale(this.weight / w);
        value.addScaled(other, weight / w);

        this.weight = w;
    }
}
