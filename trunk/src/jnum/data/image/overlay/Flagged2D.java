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

package jnum.data.image.overlay;

import jnum.Util;
import jnum.data.image.Flag2D;
import jnum.data.image.Index2D;
import jnum.data.image.Values2D;
import jnum.util.HashCode;

public class Flagged2D extends Overlay2D {
    private Flag2D flag;
    private long criticalFlags = ~0L;
    
    public Flagged2D() {}
    
    public Flagged2D(Values2D base, Flag2D flag) {
        super(base);
        setFlags(flag);
    }
    
    @Override
    public int hashCode() {
        int hash = super.hashCode() ^ flag.hashCode() ^ HashCode.from(criticalFlags);
        return hash;
    }
    
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof Flagged2D)) return false;
        
        Flagged2D flagged = (Flagged2D) o;
        if(getCriticalFlags() != flagged.getCriticalFlags()) return false;
        if(!Util.equals(flag, flagged.flag)) return false;
       
        return super.equals(o);
    }
    
    @Override
    public void setParallel(int threads) {
        super.setParallel(threads);
        flag.setParallel(threads);
    }
   
    
    public void setFlags(Flag2D flag) { this.flag = flag; }
    
    public Flag2D getFlags() { return flag; }
    
    public void setCriticalFlags(long pattern) { criticalFlags = pattern; }
    
    public final long getCriticalFlags() { return criticalFlags; }
    
    @Override
    public boolean isValid(int i, int j) {
        if(isFlagged(i, j, getCriticalFlags())) return false;
        return super.isValid(i, j);
    }
    
    @Override
    public void discard(int i, int j) {
        super.discard(i, j);
        flag.setBits(i, j, FLAG_DISCARD);
    }
    
    @Override
    public void clear(int i, int j) {
        super.clear(i, j);
        flag.set(i, j, FLAG_DISCARD);
    }
   
    @Override
    public void set(int i, int j, Number value) {
        super.set(i, j, value);
        unflag(i, j);
    }
    
    @Override
    public void add(int i, int j, Number value) {
        super.add(i, j, value);
        unflag(i, j);
    }
    
    
    public void flag(final long pattern) {
        new Fork<Void>() {
            @Override
            protected void process(int i, int j) {
                flag(i, j, pattern);
            }
        }.process();
    }
    
    public void flag() {
        flag(FLAG_DEFAULT);
    }
    
    public void unflag(final long pattern) {
        new Fork<Void>() {
            @Override
            protected void process(int i, int j) {
                unflag(i, j, pattern);
            }
        }.process();
    }
    
    public void unflag() {
        unflag(~0);
    }
    
    public final void flag(Index2D index) { flag(index.i(), index.j()); }
    
    public final void flag(int i, int j) { flag(i, j, FLAG_DEFAULT); }
    
    public final void flag(Index2D index, long pattern) { flag(index.i(), index.j(), pattern); }
    
    public void flag(int i, int j, long pattern) { flag.setBits(i, j, pattern); }
    
    public final void unflag(Index2D index) { unflag(index.i(), index.j()); }
    
    public final void unflag(int i, int j) { unflag(i, j, FLAG_DEFAULT); }
    
    public final void unflag(Index2D index, long pattern) { unflag(index.i(), index.j(), pattern); }
    
    public void unflag(int i, int j, long pattern) { flag.clearBits(i, j, pattern); }
   
    public final boolean isFlagged(Index2D index, long pattern) { return isFlagged(index.i(), index.j(), pattern); }
    
    public final boolean isFlagged(int i, int j, long pattern) { return !flag.isClear(i, j, pattern); }
    
    public final boolean isUnflagged(Index2D index, long pattern) { return isUnflagged(index.i(), index.j(), pattern); }
    
    public final boolean isUnflagged(int i, int j, long pattern) { return flag.isClear(i, j, pattern); }
    
    
    
    protected void createFlags(int flagType) {
        Flag2D flags = new Flag2D(flagType);
        flags.setSize(sizeX(), sizeY());
        setFlags(flags);
        initFlags();
    }
    
    
    protected void initFlags() {
        getFlags().fill(FLAG_DEFAULT);
    }
      
    
    public void destroy() {
        getFlags().destroy();        
    }
    
    public final static long FLAG_DISCARD = 1L<<0;
    public final static long FLAG_OPERATION = 1L<<1;

    public final static long FLAG_DEFAULT = FLAG_DISCARD;
    
}
