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

package jnum.data.image.overlay;

import java.util.concurrent.ExecutorService;

import jnum.Util;
import jnum.data.Data;
import jnum.data.FlagCompanion;
import jnum.data.image.Flag2D;
import jnum.data.image.Values2D;
import jnum.data.index.Index2D;
import jnum.parallel.ParallelPointOp;
import jnum.util.FlagBlock;
import jnum.util.FlagSpace;
import jnum.util.HashCode;

public class Flagged2D extends Overlay2D {
    private Flag2D flag;
    private long validatingFlags;
    
    public Flagged2D(Values2D base, Flag2D flag) {
        super(base);
        setFlags(flag);
    }
    
    @Override
    public Flagged2D newInstance() {
        return newInstance(getSize());
    }
    
    @Override
    public Flagged2D newInstance(Index2D size) {
        Flagged2D f = (Flagged2D) super.newInstance(size);
        f.flag = new Flag2D(flag.type(), size.i(), size.j());
        f.validatingFlags = validatingFlags;
        return f;
    }
    
    @Override
    public void copyPoliciesFrom(Data<?> other) {
        super.copyPoliciesFrom(other);
        if(other instanceof Flagged2D) validatingFlags = ((Flagged2D) other).validatingFlags;
    }
    
    @Override
    public int hashCode() {
        int hash = super.hashCode() ^ flag.hashCode() ^ HashCode.from(validatingFlags);
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof Flagged2D)) return false;

        Flagged2D flagged = (Flagged2D) o;
        if(getValidatingFlags() != flagged.getValidatingFlags()) return false;
        if(!Util.equals(flag, flagged.flag)) return false;

        return super.equals(o);
    }
    
    @Override
    public Flagged2D copy() {
        return (Flagged2D) super.copy();
    }
    
    @Override
    public Flagged2D copy(boolean withContent) {
        Flagged2D copy = (Flagged2D) super.copy(withContent);
        if(flag != null) copy.flag = flag.copy(withContent);
        return copy;
    }


    @Override
    public void setParallel(int threads) {
        super.setParallel(threads);
        if(flag != null) flag.setParallel(threads);
    }

    @Override
    public void setExecutor(ExecutorService executor) {
        super.setExecutor(executor);
        if(flag != null) flag.setExecutor(executor);
    }

    public void setFlags(Flag2D flag) {
        this.flag = flag; 
        if(flag == null) return;
        this.flag.setParallel(getParallel());
        this.flag.setExecutor(getExecutor());
    }

    public Flag2D getFlags() { return flag; }

    public void setValidatingFlags(long pattern) { 
        validatingFlags = pattern; 
    }

    public final long getValidatingFlags() { return validatingFlags; }
    
    @Override
    public boolean isValid(int i, int j) {
        if(isFlagged(i, j, getValidatingFlags())) return false;
        return super.isValid(i, j);
    }

    @Override
    public void discard(int i, int j) {
        super.clear(i, j);
        flag.set(i, j, FLAG_DISCARD);
    }

    @Override
    public void clear(int i, int j) {
        super.clear(i, j);
        flag.clear(i, j);
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
        smartFork(new ParallelPointOp.Simple<Index2D>() {
            @Override
            public void process(Index2D index) {
                flag(index, pattern);
            }
        });
    }

    public void flag() {
        flag(FLAG_DEFAULT);
    }

    public void unflag(final long pattern) {
        smartFork(new ParallelPointOp.Simple<Index2D>() {
            @Override
            public void process(Index2D index) {
                unflag(index, pattern);
            }
        });
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


    protected void createFlags(FlagCompanion.Type flagType) {
        Flag2D flags = new Flag2D(flagType);
        flags.setSize(sizeX(), sizeY());
        setFlags(flags);
        initFlags();
    }

    
    public long countFlags(final long pattern) {
        return smartFork(new ParallelPointOp.Count<Index2D>() {
            @Override
            public long getCount(Index2D point) {
                return isFlagged(point, pattern) ? 1 : 0;
            }
        });
    }

    protected void initFlags() {
        getFlags().fill(FLAG_DEFAULT);
    }


    @Override
    public void destroy() {
        super.destroy();
        getFlags().destroy();
    }
    
    public static final FlagSpace.Long flagSpace = new FlagSpace.Long(Flagged2D.class.getSimpleName());
    public static final FlagBlock<Long> flags = flagSpace.getDefaultFlagBlock(); 

    public static final long FLAG_DISCARD = flags.next('X', "discarded").value();

    public static final long FLAG_DEFAULT = FLAG_DISCARD;

}
