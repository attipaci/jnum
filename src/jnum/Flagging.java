/*******************************************************************************
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
 *     Attila Kovacs <attila[AT]sigmyne.com> - initial API and implementation
 ******************************************************************************/
package jnum;

/**
 * The interface for providing bit-wise flagging support in a 32-bit integer flag domain. For something more elaborate,
 * with support for other domains, and labeling see {@link jnum.util.FlagSpace}.
 * 
 * 
 * @author Attila Kovacs <attila@sigmyne.com>
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
     * @see #isUnflagged(pattern)
     * @see #flag(int)
     * @see #unflag(int)
     * @see #isFlagged()
     */
	public boolean isFlagged(int pattern);	

    /**
     * Checks if the implementing object has all of the bit-wise flags unset from <code>pattern</code>.
     * 
     * @param pattern   The bit-wise flag pattern to check for.
     * @return          <code>true</code> if the implementing object has all of the <code>pattern</code> flags disabled, or
     *                  <code>false</code> in any of the flags checked for are enabled.
     *                  
     * @see #isFlagged(int)
     * @see #flag(int)
     * @see #unflag(int)
     * @see #isUnflagged()
     */
	public boolean isUnflagged(int pattern);

    /**
     * Checks if the implementing object has any of the flags set.
     * 
     * @param pattern   The bit-wise flag pattern to check for.
     * @return          <code>true</code> if the implementing object has any flags set, or
     *                  <code>false</code> in none of the flags are enabled.
     *                  
     * @see #isUnflagged()
     * @see #flag(int)
     * @see #unflag(int)
     * @see #isFlagged(int)
     */
	public boolean isFlagged();

	
    /**
     * Checks if the implementing object has all of the bit-wise flags unset.
     * 
     * @param pattern   The bit-wise flag pattern to check for.
     * @return          <code>true</code> if the implementing object has all flags disabled, or
     *                  <code>false</code> in any of the flags are enabled.
     *                  
     * @see #isFlagged()
     * @see #flag(int)
     * @see #unflag(int)
     * @see #isUnflagged(int)
     */
	public boolean isUnflagged();
	
	/**
	 * Sets the specified bit-wise flags on the implementing object.
	 * 
	 * 
	 * @param pattern      The bit-wise pattern of flags to enable.
	 * 
	 * @see #unflag(int)
	 * @see #isFlagged(int)
	 * @see #isUnflagged(int)
	 * 
	 */
	public void flag(int pattern);

	/**
     * Unsets the specified bit-wise flags on the implementing object.
     * 
     * 
     * @param pattern      The bit-wise pattern of flags to disable.
     * 
     * @see #flag(int)
     * @see #isFlagged(int)
     * @see #isUnflagged(int)
     * 
     */
	public void unflag(int pattern);

	/**
	 * Unsets all bit-wise flags on the implementing object.
	 * 
	 * @see #unflag(int)
	 * @see #isFlagged()
	 * 
	 */
	public void unflag();
	
}

