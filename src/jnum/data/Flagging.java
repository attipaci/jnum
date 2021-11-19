/* *****************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila[AT]sigmyne.com>.
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
package jnum.data;

/**
 * Bit-wise flagging support in a 64-bit integer domain. For something more elaborate,
 * with support for other domains, and labeling, see {@link jnum.util.FlagSpace}.
 * 
 * 
 * @author Attila Kovacs
 *
 * @see jnum.util.FlagSpace
 *
 */
public interface Flagging {

    /**
     * Checks if the implementing object has any of the bit-wise flags set from <code>pattern</code>.
     * 
     * @param pattern   The bit-wise flag pattern to check for.
     * @return          <code>true</code> if the implementing object has any of the <code>pattern</code> flags set, or
     *                  <code>false</code> in none of the flags checked for are enabled.
     *                  
     * @see #isUnflagged(long)
     * @see #flag(long)
     * @see #unflag(long)
     * @see #isFlagged()
     */
	public boolean isFlagged(long pattern);	

    /**
     * Checks if the implementing object has all of the bit-wise flags unset from <code>pattern</code>.
     * 
     * @param pattern   The bit-wise flag pattern to check for.
     * @return          <code>true</code> if the implementing object has all of the <code>pattern</code> flags disabled, or
     *                  <code>false</code> in any of the flags checked for are enabled.
     *                  
     * @see #isFlagged(long)
     * @see #flag(long)
     * @see #unflag(long)
     * @see #isUnflagged()
     */
	public default boolean isUnflagged(long pattern) {
	    return !isFlagged(pattern);
	}

    /**
     * Checks if the implementing object has any of the flags set.
     * 
     * @return          <code>true</code> if the implementing object has any flags set, or
     *                  <code>false</code> in none of the flags are enabled.
     *                  
     * @see #isUnflagged()
     * @see #flag(long)
     * @see #unflag(long)
     * @see #isFlagged(long)
     */
	public boolean isFlagged();

	
    /**
     * Checks if the implementing object has all of the bit-wise flags unset.
     * 
     * @return          <code>true</code> if the implementing object has all flags disabled, or
     *                  <code>false</code> in any of the flags are enabled.
     *                  
     * @see #isFlagged()
     * @see #flag(long)
     * @see #unflag(long)
     * @see #isUnflagged(long)
     */
	public default boolean isUnflagged() {
	    return !isFlagged();
	}
	
	/**
	 * Sets the specified bit-wise flags on the implementing object.
	 * 
	 * 
	 * @param pattern      The bit-wise pattern of flags to enable.
	 * 
	 * @see #unflag(long)
	 * @see #isFlagged(long)
	 * @see #isUnflagged(long)
	 * 
	 */
	public void flag(long pattern);

	/**
     * Unsets the specified bit-wise flags on the implementing object.
     * 
     * 
     * @param pattern      The bit-wise pattern of flags to disable.
     * 
     * @see #flag(long)
     * @see #isFlagged(long)
     * @see #isUnflagged(long)
     * 
     */
	public void unflag(long pattern);

	/**
	 * Unsets all bit-wise flags on the implementing object.
	 * 
	 * @see #unflag(long)
	 * @see #isFlagged()
	 * 
	 */
	public default void unflag() {
	    unflag(~0L);
	}
	
	/**
	 * Return all the bitwise flags
	 * 
	 * @return     All flags (up to 64 bitwise flags). 
	 */
	public long getFlags();
}

