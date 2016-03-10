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

package jnum.data.mesh;

import java.lang.Double;

public class DoubleMesh extends PrimitiveMesh<Double> {
    /**
     * 
     */
    private static final long serialVersionUID = -6960364610546704958L;

    public DoubleMesh(int[] dimensions) {
        super(Double.class, dimensions);
    }

    public DoubleMesh() {
        super(Double.class);
    }

    public DoubleMesh(Object data) {
        this();
        setData(data);
    }

    
    @Override
    protected Double getScaled(Double value, double factor) {
        return factor * value;
    }

  
    @Override
    protected Double getSum(Number a, Number b) {
        return a.doubleValue() + b.doubleValue();
    }

    @Override
    protected Double getDifference(Number a, Number b) {
        return a.doubleValue() - b.doubleValue();
    }

    @Override
    protected Double zeroValue() {
        return 0.0;
    }

    @Override
    protected Double baseLineElementAt(Object simpleArray, int index) {
        return ((double[]) simpleArray)[index];
    }

    @Override
    protected void setBaseLineElementAt(Object simpleArray, int index, Double value) {
        ((double[]) simpleArray)[index] = value;
    }

    @Override
    public Double parseElement(String text) throws Exception {
        return Double.parseDouble(text);
    }

    @Override
    public Mesh<Double> subArrayAt(int[] index) {
        return new DoubleMesh(subarrayDataAt(index));
    }


}
