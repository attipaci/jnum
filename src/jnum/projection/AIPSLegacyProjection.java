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
// Copyright (c) 2007 Attila Kovacs 

package jnum.projection;

import jnum.math.Coordinate2D;
import jnum.math.SphericalCoordinates;
import jnum.math.Vector2D;



public class AIPSLegacyProjection extends SphericalProjection {

	private static final long serialVersionUID = -3813183672541201623L;

	SphericalProjection baseProjection;

	String name;

	String fitsID;

	private Vector2D referenceOffsets;
	
	
	public AIPSLegacyProjection(SphericalProjection baseProjection, String name, String fitsID) {
		this.baseProjection = baseProjection;
		this.referenceOffsets = new Vector2D();
		this.name = name;
		this.fitsID = fitsID;
	}
	
	@Override
    public AIPSLegacyProjection clone() {
	    AIPSLegacyProjection clone = (AIPSLegacyProjection) super.clone();
	    clone.referenceOffsets = (Vector2D) referenceOffsets.clone();
	    return clone;
	}
	
	/* (non-Javadoc)
	 * @see jnum.Projection2D#getFitsID()
	 */
	@Override
	public String getFitsID() {
		return fitsID;
	}

	/* (non-Javadoc)
	 * @see jnum.Projection2D#getFullName()
	 */
	@Override
	public String getFullName() {
		return name;
	}
	
	/* (non-Javadoc)
	 * @see jnum.SphericalProjection#setReference(jnum.SphericalCoordinates, jnum.SphericalCoordinates)
	 */
	@Override
	public void setReference(SphericalCoordinates coords) {
		baseProjection.project(coords, referenceOffsets);
		super.setReference(coords);
	}
		
	/* (non-Javadoc)
	 * @see jnum.SphericalProjection#project(jnum.SphericalCoordinates, jnum.Coordinate2D)
	 */
	@Override
	public final void project(SphericalCoordinates coords, Coordinate2D toProjected) {
	    if(referenceOffsets == null) throw new IllegalStateException("null reference in " + getClass().getSimpleName());
		baseProjection.project(coords, toProjected);
		toProjected.subtractX(referenceOffsets.x());
		toProjected.subtractY(referenceOffsets.y());
	}
	
	/* (non-Javadoc)
	 * @see jnum.SphericalProjection#deproject(jnum.Coordinate2D, jnum.SphericalCoordinates)
	 */
	@Override
	public final void deproject(Coordinate2D projected, SphericalCoordinates toCoords) {
	    if(referenceOffsets == null) throw new IllegalStateException("null reference in " + getClass().getSimpleName());
		projected.addX(referenceOffsets.x());
		projected.addY(referenceOffsets.y());
		baseProjection.deproject(projected, toCoords);		
	}
	
	
	/* (non-Javadoc)
	 * @see jnum.SphericalProjection#getOffsets(double, double, jnum.Coordinate2D)
	 */
	@Override
	public final void getOffsets(double theta, double phi, Coordinate2D toOffset) {
		baseProjection.getOffsets(theta, phi, toOffset);
	}

	/* (non-Javadoc)
	 * @see jnum.SphericalProjection#phi(jnum.Coordinate2D)
	 */
	@Override
	public final void getPhiTheta(Coordinate2D offset, SphericalCoordinates phiTheta) {
		baseProjection.getPhiTheta(offset, phiTheta);
	}

}
