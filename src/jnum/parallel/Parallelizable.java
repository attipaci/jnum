/* *****************************************************************************
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
 *     Attila Kovacs  - initial API and implementation
 ******************************************************************************/

package jnum.parallel;

import java.util.concurrent.ExecutorService;


/**
 * Interface for objects that implement parallel processing using the jnum parallel architecture. The jnum
 * parallel model is similar to Java streams introduced with Java 8, but offers a slightly different
 * approach and feature set.
 * 
 * @author Attila Kovacs
 *
 */
public interface Parallelizable {
    
    /**
     * Get the executor service (if any) associated with processing the implementing object in parallel.
     * 
     * @return      the executor service that is responsible for parallel processing this object.
     */
    public ExecutorService getExecutor();
    
    /**
     * Sets the executor service to use for parallel processing this object.
     * 
     * @param executor  the executor service to use.
     * 
     * @see #setParallel(int)
     */
    public void setExecutor(ExecutorService executor);
     
    /**
     * Gets the number of parallel threads used for processing this object.
     * 
     * @return      the number of parallel threads used for processing this object.
     */
    public int getParallel();
    
    /**
     * Sets the targeted number of parallel threads to use when processing this object in parallel. 
     * Typically, the data will be split into at least as many parallel blocks for processing in parallel.
     * If the data cannot be split as many ways, then it will be processed in a smaller number of
     * threads depending on what level of splitting the data allows for parallelization.
     * If an executor service was also defined via {@link #setExecutor(ExecutorService)} those
     * chunks will get processed via the specified service. Otherwise, ad-hoc Java threads
     * will be used for the same effect. 
     * 
     * @param n     the number of threads to be used for parallel processing.
     */
    public void setParallel(int n);
    
    /**
     * Disables parallel processing on this object.
     * 
     * 
     * @see #setParallel(int)
     */
    public void noParallel();
  
}
