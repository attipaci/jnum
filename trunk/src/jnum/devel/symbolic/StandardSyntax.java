/*******************************************************************************
 * Copyright (c) 2016 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
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
 *     Attila Kovacs <attila_kovacs[AT]post.harvard.edu> - initial API and implementation
 ******************************************************************************/

package jnum.devel.symbolic;


public class StandardSyntax implements Syntax {

   
    @Override
    public Bracketing getFunctionBrackets() {
        return Bracketing.curved;
    }
    
    @Override
    public String getArgumentListSeparator() { return ","; }

    @Override
    public Bracketing getDimensionBrackets() {
        return Bracketing.square; 
    }

    @Override
    public Bracketing getListBrackets() {
        return Bracketing.curly; 
    }
    
    @Override
    public String getListSeparator() { return ","; }
    
    @Override
    public Bracketing[] getGroupingBrackets() {
        return groupBrackets;
    }

    @Override
    public Operation[] getPrecedence() {
        return precedence;
    }

    
    private Bracketing[] groupBrackets = { Bracketing.curved, Bracketing.square };
    
    private static final Operation[] precedence = new Operation[] { 
            Operation.increment, Operation.decrement, 
            Operation.negation,
            Operation.power,
            Operation.multiplication, Operation.division, Operation.modulus,
            Operation.addition, Operation.subtraction
            // TODO ...
    };

    
}
