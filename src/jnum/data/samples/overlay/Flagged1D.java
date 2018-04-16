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

package jnum.data.samples.overlay;

import jnum.Util;
import jnum.data.samples.Flag1D;
import jnum.data.samples.Values1D;
import jnum.util.HashCode;

public class Flagged1D extends Overlay1D {
    private Flag1D flag;
    private long criticalFlags = ~0L;
    
    public Flagged1D() {}
    
    public Flagged1D(Values1D base, Flag1D flag) {
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
        if(!(o instanceof Flagged1D)) return false;
        
        Flagged1D flagged = (Flagged1D) o;
        if(getCriticalFlags() != flagged.getCriticalFlags()) return false;
        if(!Util.equals(flag, flagged.flag)) return false;
       
        return super.equals(o);
    }
    
    @Override
    public void setParallel(int threads) {
        super.setParallel(threads);
        flag.setParallel(threads);
    }
   
    
    public void setFlags(Flag1D flag) { this.flag = flag; }
    
    public Flag1D getFlags() { return flag; }
    
    public void setCriticalFlags(long pattern) { criticalFlags = pattern; }
    
    public final long getCriticalFlags() { return criticalFlags; }
    
    @Override
    public boolean isValid(int i) {
        if(isFlagged(i, getCriticalFlags())) return false;
        return super.isValid(i);
    }
    
    @Override
    public void clear(int i) {
        super.clear(i);
        flag.clear(i);
    }
    
    @Override
    public void discard(int i) {
        super.clear(i);
        flag.set(i, FLAG_DISCARD);
    }
    
    @Override
    public void set(int i, Number value) {
        super.set(i, value);
        unflag(i);
    }
    
    @Override
    public void add(int i, Number value) {
        super.add(i, value);
        unflag(i);
    }
    
    
    public void flag(final long pattern) {
        new Fork<Void>() {
            @Override
            protected void processElementAt(int i) {
                flag(i, pattern);
            }
        }.process();
    }
    
    public void flag() {
        flag(FLAG_DEFAULT);
    }
    
    public void unflag(final long pattern) {
        new Fork<Void>() {
            @Override
            protected void processElementAt(int i) {
                unflag(i, pattern);
            }
        }.process();
    }
    
    public void unflag() {
        unflag(~0);
    }
    
     
    public final void flag(int i) { flag(i, FLAG_DEFAULT); }
       
    public void flag(int i, long pattern) { flag.setBits(i, pattern); }
      
    public final void unflag(int i) { unflag(i, FLAG_DEFAULT); }
     
    public void unflag(int i, long pattern) { flag.clearBits(i, pattern); }
     
    public final boolean isFlagged(int i, long pattern) { return !flag.isClear(i, pattern); }
      
    public final boolean isUnflagged(int i, long pattern) { return flag.isClear(i, pattern); }
    
    
    
    protected void createFlags(int flagType) {
        Flag1D flags = new Flag1D(flagType);
        flags.setSize(getSize());
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
