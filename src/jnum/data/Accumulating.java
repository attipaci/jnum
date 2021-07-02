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

import java.util.stream.Collector;

/**
 * Efficient averaging of data (with the least amount of expensive operations).
 * 
 * @author Attila Kovacs
 *
 * @param <T>   the generic type of object that can be accumulated
 */
public interface Accumulating<T> {

    /**
     * Resets the data (e.g. from prior accumulation) in this object s.t. it contains
     * no data, and that a subsequent ammumulation of value will result in this object
     * taking up the accumulated value.
     * 
     */
    public void noData();

    /**
     * Accumulates an object of the supported generic type into this one. Typically
     * this involves a summation of values and corresponding weights.
     * 
     * @param x     The value to accumulate into this one.
     */
    public void accumulate(T x);
    
    /**
     * Accumulates an object of the supported generic type into this one with the specified
     * additional (relative) weight. Typically this involves a summation of values and 
     * corresponding aggregated weights.
     * 
     * @param x     the value to accumulate into this one. 
     * @param w     the external multiplicative weight (in addition to any weight that the
     *              value might carry by itself).
     */
    public void accumulate(T x, double w);

    /**
     * Accumulates an object of the supported generic type into this one with the specified
     * additional (relative) weight, and gain normalization. Typically this involves a 
     * summation of gain-corrected values and corresponding aggregated weights.
     * 
     * @param x     the value to accumulate into this one. 
     * @param w     the external multiplicative weight (in addition to any weight that the
     *              value might carry by itself).
     * @param G     gain to divide x by, to make it properly normalized for accumulation.
     */
    public void accumulate(T x, double w, double G);

    /**
     * Starts a new accumulation in this object, clearing any prior data by calling
     * {@link #noData()}. It may also perform other initialization steps for a
     * successive new accumulation.
     * 
     */
    public void startAccumulation();

    /**
     * Ends the current accumulation, and normalizes the data in this object as appropriate
     * Since {@link #accumulate(Object)} usually does summing only, this method would
     * then divide the value sums by the accumulated weights, such that it represents
     * a proper weighted average. It is critical that one calls this method on this
     * object <b>exactly once</b> at the end of the accumulation round, and before using the 
     * accumulated value, or else one may end up with non-sensical
     * results.
     * 
     */
    public void endAccumulation();

    /**
     * The summing collector for classes that implement this interface, for use
     * with streams.
     * 
     * @param <T>   the generic type of object that can be accumulated
     * @param cl    the class of the generic type
     * @return      the collector that will give back the sum of elements from the stream
     *              on which it collects.
     */
    public static <T extends Accumulating<T>> Collector<T, T, T> sum(Class<T> cl) {
        return Collector.of(
                () -> {
                    try { return cl.getDeclaredConstructor().newInstance(); }
                    catch(Exception e) { return null; }
                },
                (partial, point) -> partial.accumulate(point, 1.0),
                (sum, partial) -> { sum.accumulate(partial, 1.0); return sum; },
                Collector.Characteristics.UNORDERED,
                Collector.Characteristics.IDENTITY_FINISH
                );
    }

    
    /**
     * The averaging collector for classes that implement this interface, for use
     * with streams.
     * 
     * @param <T>   the generic type of object that can be accumulated
     * @param cl    the class of the generic type
     * @return      the collector that will give back the weighted average of elements from the stream
     *              on which it collects.
     */
    public static <T extends Accumulating<T>> Collector<? extends T, T, T> average(Class<T> cl) {
        return Collector.of(
                () -> {
                    try { return cl.getDeclaredConstructor().newInstance(); }
                    catch(Exception e) { return null; }
                },
                (partial, point) -> partial.accumulate(point, 1.0),
                (sum, partial) -> { sum.accumulate(partial, 1.0); return sum; },
                sum -> { sum.endAccumulation(); return sum; },
                Collector.Characteristics.UNORDERED
                );
    }
}
