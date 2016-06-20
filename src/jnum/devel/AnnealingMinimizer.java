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

package jnum.devel;

import java.util.Collection;
import java.util.Random;

import jnum.data.fitting.DownhillSimplex;
import jnum.data.fitting.Parameter;
import jnum.data.fitting.Parametric;

public class AnnealingMinimizer extends DownhillSimplex {
    private Random random = new Random();
    
  
    private double T, coolingSteps;
    
    public AnnealingMinimizer(Parametric function, Collection<Parameter> parameters) {
        super(function, parameters);
        // TODO Auto-generated constructor stub
    }

    public AnnealingMinimizer(Parametric function, Parameter[] parameters) {
        super(function, parameters);
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public double evaluate() { return super.evaluate() + T * random.nextGaussian(); }
    
    public void setTemperature(double T) { this.T = T; }
    
    public double getTemperature() { return T; }
    
    public void setCoolingSteps(double steps) { 
        coolingSteps = steps; 
    }
   
    public double getCoolingSteps() { return coolingSteps; }
}
