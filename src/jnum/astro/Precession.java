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


package jnum.astro;

import java.io.Serializable;

import jnum.ExtraMath;
import jnum.Unit;
import jnum.Util;
import jnum.util.SimpleMatrix;


//TODO precess with proper motion...



public class Precession implements Serializable {

	private static final long serialVersionUID = 5730425237393070503L;

	private CoordinateEpoch fromEpoch, toEpoch;

	private float[][] P = new float[3][3];

	private static float year2Century = (float) (Unit.year / Unit.julianCentury);

	private static float arcsec = (float) Unit.arcsec;
	

	public Precession(double fromJulianEpoch, double toJulianEpoch) {
		this(new JulianEpoch(fromJulianEpoch), new JulianEpoch(toJulianEpoch));
	}
	

	public Precession(CoordinateEpoch from, CoordinateEpoch to) {
		fromEpoch = from;
		toEpoch = to;
		if(fromEpoch.equals(toEpoch)) P = null;
		else calcMatrix();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = super.hashCode();
		if(fromEpoch != null) hash ^= fromEpoch.hashCode();
		if(toEpoch != null) hash ^= toEpoch.hashCode();
		return hash;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if(o == this) return true;
		if(!(o instanceof Precession)) return false;
		
		Precession p = (Precession) o;
		
		if(!Util.equals(fromEpoch, p.fromEpoch)) return false;
		if(!Util.equals(toEpoch, p.toEpoch)) return false;
		
		return true;
	}
	
	// Precession from Lederle & Schwan, Astronomy and Astrophysics, 134, 1-6 (1984)
	// TODO update precession to IERS 2003 convention...
	private void calcMatrix() {
		final float fromJulianYear = (float) fromEpoch.getJulianYear();
		final float toJulianYear = (float) toEpoch.getJulianYear();
		
		final float tau = (fromJulianYear - 2000.0F) * year2Century;
		final float t = (toJulianYear - fromJulianYear) * year2Century;

		final float eta = (2305.6997F + (1.39744F + 0.000060F * tau) * tau 
				+ (0.30201F - 0.000270F * tau + 0.017996F * t) * t) * t * arcsec;

		final float z = (2305.6997F + (1.39744F + 0.000060F * tau) * tau 
				+ (1.09543F + 0.000390F * tau + 0.018326F * t) * t) * t * arcsec;

		final float theta = (2003.8746F - (0.85405F + 0.000370F * tau) * tau
				- (0.42707F + 0.000370F * tau + 0.041803F * t) * t) * t * arcsec;	

		P = R3(-z).dot(R2(theta)).dot(R3(-eta)).value;
	}
	
	
	public void precess(final EquatorialCoordinates equatorial) {		
		if(P == null) return;
		
		
		final float v0 = (float) (equatorial.cosLat() * Math.cos(equatorial.RA())); 
		final float v1 = (float) (equatorial.cosLat() * Math.sin(equatorial.RA()));
		final float v2 = (float) equatorial.sinLat();

		float[] R = P[0];
		final double l0 = R[0] * v0 + R[1] *  v1 + R[2] * v2;
		R = P[1];
		final double l1 = R[0] * v0 + R[1] *  v1 + R[2] * v2;
		R = P[2];
		final double l2 = R[0] * v0 + R[1] *  v1 + R[2] * v2;

		equatorial.setRA(Math.atan2(l1, l0));
		equatorial.setDEC(Math.atan2(l2, ExtraMath.hypot(l0, l1)));

		equatorial.epoch = toEpoch;
	}


	private SimpleMatrix R2(final double phi) {
		final float c = (float) Math.cos(phi);
		final float s = (float) Math.sin(phi);

		float[][] R = { {  c, 0,-s }, 
				{  0, 1, 0 },
				{  s, 0, c } };	
		
		return new SimpleMatrix(R);
	}


	private SimpleMatrix R3(double phi) {
		final float c = (float) Math.cos(phi);
		final float s = (float) Math.sin(phi);

		float[][] R = { {  c, s, 0 }, 
				{ -s, c, 0 },
				{  0, 0, 1 } };	
		
		return new SimpleMatrix(R);		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
    public String toString() {
	    return fromEpoch + " --> " + toEpoch;
	}
	
}
