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

public interface Accumulating<T> {

    public void noData();

    public void accumulate(T x);
    
    public void accumulate(T x, double w);

    public void accumulate(T x, double w, double G);

    public void startAccumulation();

    public void endAccumulation();


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
