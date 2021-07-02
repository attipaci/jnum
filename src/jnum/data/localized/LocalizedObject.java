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

import java.io.Serializable;

import jnum.data.RegularData;
import jnum.math.Metric;


/**
 * An class for data that is tagged by some irregularly distributed topological location marker.
 * If the data is taken from regular intervals you should consider using the more efficient and
 * feature-filled  {@link RegularData} object instead to represent your regularly sampled values. This
 * data class is really for data that does not lend itself to a representation on a regular grid.
 * 
 * @author Attila Kovacs
 *
 * @param <L>   the generic type of objects that represents a location, such as a number or a vector.
 * @param <D>   the generic type of the object that accompanies the location.
 */
public class LocalizedObject<L extends Locality, D> 
implements Serializable, Cloneable, Comparable<LocalizedObject<L, D>>, Metric<LocalizedObject<L, D>> {
    private L location;
    private D value;
    private Class<D> type;

    private static final long serialVersionUID = 2776764506885561864L;

    /**
     * Construct as new localized object at the given locality and with the given object datum. 
     * 
     * @param location      the locality where this datum was measured or referred to.
     * @param datum         the object that represents the value at that location.
     */
    @SuppressWarnings("unchecked")
    public LocalizedObject(L location, D datum) {
        this.location = location;
        this.value = datum;
        this.type = (Class<D>) datum.getClass();
    }

    @Override
    @SuppressWarnings("unchecked")
    public LocalizedObject<L, D> clone() {
        try { return (LocalizedObject<L, D>) super.clone(); }
        catch(CloneNotSupportedException e) { return null; }
    }
    
    /**
     * Gets the class of the data element contained in this object.
     * 
     * @return  The class of the data element.
     */
    public final Class<D> getType() { return type; }
    
    /**
     * Returns the reference to the underlying data element contained in this object.
     * 
     * @return  the underlying data element.
     */
    public final D getData() {
        return value;
    }

    /**
     * Sets a new data element for this object.
     * 
     * @param value the new datum to use with the tagged location.
     */
    public void setData(D value) {
        this.value = value;
    }


    /**
     * Gets the topological locality of this datum.
     * 
     * @return     the locality at which this datum was measured is is referred to.
     */
    public final L getLocality() {
        return location;
    }

    /**
     * Sets a new topological locality for this datum.
     * 
     * @param loc  the new locality to which this datum will be referred to.
     */
    public final void setLocality(L loc) {
        this.location = loc;
    }


    /**
     * Compares the location of this object with another location of the same type in
     * order to establish a sorting order between the two. By sorting data along
     * one direction of the location space can significanly speed up the calculation
     * of local averages.
     * 
     * @param loc   the location we want to compare.
     * @return      0 if the two locations sort to the same value, -1 if this object
     *              should come before the argument, and +1 if this object should
     *              come after the argument.
     */
    public int compareTo(L loc) { 
        return Double.compare(getLocality().getSortingValue(), loc.getSortingValue());
    }

    @Override
    public final int compareTo(LocalizedObject<L, D> other) { 
        return compareTo(other.getLocality()); 
    }

    /**
     * Gets the distance to another locality in the same space where this object
     * is localized.
     * 
     * @param loc   the locality
     * @return      the distance from this object to the specified locality.
     */
    public double distanceTo(L loc) {
        return getLocality().distanceTo(loc);
    }


    @Override
    public double distanceTo(LocalizedObject<L, D> other) {
        return distanceTo(other.getLocality());
    }


    /**
     * Gets the distance of this object to another object along the 1-dimensional
     * sorting space (usually along a direction in the locartion space). 
     * 
     * @param other the other localized object
     * @return      the distance to the other object along the sorting dimension / space.
     */
    public double sortingDistanceTo(LocalizedObject<L, D> other) {
        return sortingDistanceTo(other);
    }

    /**
     * Gets the distance of this object to a location along the 1-dimensional
     * sorting space (usually along a direction in the locartion space). 
     * 
     * @param loc   a locality
     * @return      the distance to the specified locality along the sorting dimension / space.
     */
    public double sortingDistanceTo(L loc) {
        return Math.abs(getLocality().getSortingValue() - loc.getSortingValue());
    }


    @Override
    public String toString() { 
        return getLocality() + ": " + getData();
    }


}





