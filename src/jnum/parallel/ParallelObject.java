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

import jnum.Util;

/**
 * A base class for objects that implement parallel processing of their content, using the jnum
 * approach to generalized parallel processing. This is somewhat analogous to Java streams
 * (introduced in Java 8), but with a different approach an implementation, which offers
 * unique tunability and flexibility beyond what is offered by Java streams.
 * 
 * @author Attila Kovacs
 *
 */
public abstract class ParallelObject implements Cloneable, Parallelizable {
    /** the executor service (if any) to use for parallel processing */
    private ExecutorService executor;
    
    /** The targeted number of parallel threads to use */
    private int parallelism;
    
    
    @Override
    public ParallelObject clone() {
        try { return (ParallelObject) super.clone(); }
        catch(CloneNotSupportedException e) { return null; }
    }
    
    @Override
    public final ExecutorService getExecutor() {
        return executor;
    }

    @Override
    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    @Override
    public final int getParallel() {
        /*
        if(parallelism == 1) return 1;
        
        final StackTraceElement[] trace = new Throwable().getStackTrace();
        boolean isSubTask = false;
        
        for(int i=0; i<trace.length; i++) if(trace[i].getClassName().equals(ParallelTask.class.getName())) {
            isSubTask = true;
            break;
        }    
        
        return isSubTask ? 1 : parallelism;
        */
        
        return parallelism;
    }

    @Override
    public void setParallel(int n) {
        parallelism = n;
    }

    @Override
    public void noParallel() {
        setParallel(1);
    }
    
    /**
     * Copy the parallelization properties from another jnum parallel processing environment.
     * 
     * @param processor
     * 
     * @see #setExecutor(ExecutorService)
     * @see #setParallel(int)
     */
    public void copyParallel(Parallelizable processor) {
        setExecutor(processor.getExecutor());
        setParallel(processor.getParallel());
    }
    
    /**
     * Shuts down the executor service (if any) used for parallel processing this object.
     * 
     * @see ExecutorService#shutdown()
     */
    public void shutdown() { 
        if(executor == null) return;
        executor.shutdown(); 
        executor = null;
    }

    
    /** A task that can be performed in parallel on the parent object. */
    public abstract class Task<ReturnType> extends ParallelTask<ReturnType> {           

        /**
         * Parallel processes the parent object with the number
         * of threads returned by {@link ParallelObject#getParallel()} using the executor
         * service (if any) returned by {@link ParallelObject#getExecutor()} of the parent object.
         * 
         */
        public final void process() {
            process(getParallel(), getExecutor());
        }
        
        @Override
        public void process(int threads, ExecutorService executor) { 
            try { super.process(threads, executor); }
            catch(Exception e) { Util.error(this, e); }
        }     
    }
        

}
