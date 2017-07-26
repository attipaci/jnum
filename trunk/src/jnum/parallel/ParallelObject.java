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

package jnum.parallel;

import java.util.concurrent.ExecutorService;

import jnum.Util;

public abstract class ParallelObject implements Cloneable, Parallelizable {
    private ExecutorService executor;
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
    
    public void copyParallel(Parallelizable processor) {
        setExecutor(processor.getExecutor());
        setParallel(processor.getParallel());
    }
    
    
    public void shutdown() { 
        if(executor == null) return;
        executor.shutdown(); 
        executor = null;
    }

    
    public abstract class Task<ReturnType> extends ParallelTask<ReturnType> {           

        public void process() {
            process(getParallel());
        }
        
        @Override
        public void process(int threads) { 
            try { super.process(threads, executor); }
            catch(Exception e) { Util.error(this, e); }
        }     
    }
        

}
