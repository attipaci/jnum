/*******************************************************************************
 * Copyright (c) 2017 Attila Kovacs <attila[AT]sigmyne.com>.
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
 *     Attila Kovacs <attila[AT]sigmyne.com> - initial API and implementation
 ******************************************************************************/

package test;

import jnum.data.mesh.DoubleMesh;
import jnum.data.mesh.Mesh;
import jnum.data.mesh.MeshCrawler;
import jnum.data.mesh.ObjectMesh;

public class DoubleMeshTest {

    public static void main(String[] args) {
        double[][] x = new double[][] { { 0, 1, 2 }, { 3, 4, 5 } }; 
        DoubleMesh A = new DoubleMesh(x);
     
        
        //A.add(A);
        
        MeshCrawler<Double> i = A.iterator();
        int[] coords = new int[2];
        
        while(i.hasNext()) {
            double element = i.next();
            i.getPosition(coords);
            System.out.println(coords[0] + "," + coords[1] + ":" + element);
            i.setCurrent(-element);
        }
        
        System.out.println();
        
        Mesh<Double> oA = new ObjectMesh<Double>(Double.class, new int[] {3,2});
        
        
        MeshCrawler<Double> oi = oA.iterator();
        double n = 0.0;
        while(oi.hasNext()) { 
            oi.setNext(n);
            n += 1.0; 
        }
        
        oi.reset();
        
        while(oi.hasNext()) {
            double element = oi.next();
            oi.getPosition(coords);
            System.out.println(coords[0] + "," + coords[1] + ":" + element);
        }
        
        System.out.println();
       
        i.reset();
        while(i.hasNext()) {
            double element = i.next();
            System.out.println(" " + element);
        }
        
        i.reset();
        while(i.hasNext()) i.setNext(1.0);
         
        
        System.out.println();
        
        i.reset();
        while(i.hasNext()) {
            double element = i.next();
            System.out.println(" " + element);
        }
        
    }
    
}
