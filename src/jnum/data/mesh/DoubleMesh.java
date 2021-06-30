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

package jnum.data.mesh;

import java.lang.Double;


public class DoubleMesh extends NumberMesh.FloatingType<Double> {

    private static final long serialVersionUID = -6960364610546704958L;


    public DoubleMesh(int[] dimensions) {
        super(double.class, dimensions);
    }


    public DoubleMesh() {
        super(double.class);
    }


    public DoubleMesh(Object data) {
        this();
        setData(data);
    }   

    @Override
    public final Double cast(Number x) { return x.doubleValue(); }
    
    @Override
    protected final Double zeroValue() {
        return 0.0;
    }

    @Override
    protected final Double linearElementAt(Object simpleArray, int index) {
        return ((double[]) simpleArray)[index];
    }

    @Override
    protected final void setLinearElementAt(Object simpleArray, int index, Double value) {
        ((double[]) simpleArray)[index] = value;
    }

    @Override
    public Double parseElement(String text) throws Exception {
        return Double.parseDouble(text);
    }

    @Override
    public Mesh<Double> newInstance() {
        return new DoubleMesh();
    }

    @Override
    public final Double getSumOf(final Number a, final Number b) {
        return a.doubleValue() + b.doubleValue();
    }
    
    @Override
    public final Double getDifferenceOf(final Number a, final Number b) {
        return a.doubleValue() - b.doubleValue();
    }
    
   

}
