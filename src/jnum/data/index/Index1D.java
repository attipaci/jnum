/* *****************************************************************************
 * Copyright (c) 2021 Attila Kovacs <attila[AT]sigmyne.com>.
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

package jnum.data.index;

import jnum.PointOp;
import jnum.parallel.ParallelPointOp;



/**
 * An index in 1D space. Essentially a wrapped integer.
 * 
 * @author Attila Kovacs
 *
 */
public class Index1D extends Index<Index1D> {
    /**
     * 
     */
    private static final long serialVersionUID = 6394209570805373325L;
 
    
    /**
     * Instantiates a new 1D index with the default zero value.
     */
    public Index1D() { super(1); }
    
    /**
     * Instantiates a new 1D index with the specified initial value.
     * 
     * @param i     the initial value for the new index instance.
     */
    public Index1D(int i) {
        this();
        set(i); 
    }
    
    /**
     * Returns the index location, for the first (and only) component in this index.
     * 
     * @return  the index value
     * 
     * @see #set(int...)
     */
    public int i() { return getComponent(0); }
    

    @Override  
    public <ReturnType> ReturnType loop(final PointOp<Index1D, ReturnType> op, Index1D to) {
        final int i = i();
        final Index1D index = new Index1D();
        for(int i1=to.i(); --i1 >= i; ) {
            index.set(i1);
            op.process(index);
            if(op.exception != null) return null;
        }
        return op.getResult();
    }
    
    @Override
    public <ReturnType> ReturnType fork(final ParallelPointOp<Index1D, ReturnType> op, Index1D to) {
        Fork<ReturnType> f = new Fork<>(op, to);
        f.process();
        return f.getResult();
    }
    
    public class Fork<ReturnType> extends Task<ReturnType> { 
        private Index1D point; 
        private int to;
        private ParallelPointOp<Index1D, ReturnType> op;
        
        public Fork(ParallelPointOp<Index1D, ReturnType> op, Index1D to) { 
            this(op, to.i());
        }
        
        public Fork(ParallelPointOp<Index1D, ReturnType> op, int to) { 
            this.op = op;
            this.to = to;    
        }
        
        @Override
        public void init() {
            super.init();
            point = new Index1D();
        }
        
        @Override
        protected void processChunk(int index, int threadCount) {
            for(int i=i() + index; i<to; i += threadCount) {
                point.set(i);
                op.process(point);
            }
        }

        @Override
        protected int getRevisedChunks(int chunks, int minBlockSize) {
            return super.getRevisedChunks(getPointOps(), minBlockSize);
        }

        @Override
        protected int getTotalOps() {
            return 3 + (to - i()) * getPointOps();
        }

        protected int getPointOps() {
            return 10;
        }
        
        @Override
        public ReturnType getResult() {
            return op.getResult();
        }
    } 
    
}
