/*******************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
 * All rights reserved. 
 * 
 * This file is part of jnum.
 * 
 *     kovacs.util is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     kovacs.util is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with kovacs.util.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Attila Kovacs <attila_kovacs[AT]post.harvard.edu> - initial API and implementation
 ******************************************************************************/
// TODO precess with proper motion...
// TODO nutation correction...


package jnum.astro;

import java.io.Serializable;

import jnum.ExtraMath;
import jnum.Unit;
import jnum.Util;
import jnum.util.SimpleMatrix;

// TODO: Auto-generated Javadoc
/**
 * The Class Precession.
 */
public class Precession implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 5730425237393070503L;

	/** The to epoch. */
	private CoordinateEpoch fromEpoch, toEpoch;
	
	/** The p. */
	private float[][] P = new float[3][3];
	
	/** The year2 century. */
	private static float year2Century = (float) (Unit.year / Unit.julianCentury);
	
	/** The arcsec. */
	private static float arcsec = (float) Unit.arcsec;
	
	/**
	 * Instantiates a new precession.
	 *
	 * @param fromJulianEpoch the from julian epoch
	 * @param toJulianEpoch the to julian epoch
	 */
	public Precession(double fromJulianEpoch, double toJulianEpoch) {
		this(new JulianEpoch(fromJulianEpoch), new JulianEpoch(toJulianEpoch));
	}
	
	/**
	 * Instantiates a new precession.
	 *
	 * @param from the from
	 * @param to the to
	 */
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
		if(!super.equals(o)) return false;
		Precession p = (Precession) o;
		
		if(!Util.equals(fromEpoch, p.fromEpoch)) return false;
		if(!Util.equals(toEpoch, p.toEpoch)) return false;
		
		return true;
	}
	
	//  Precession from Lederle & Schwan, Astronomy and Astrophysics, 134, 1-6 (1984)
	/**
	 * Calculates the matrix.
	 *
	 * @return the matrix
	 */
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
	
	
	/**
	 * Precess.
	 *
	 * @param equatorial the equatorial
	 */
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

	
	
	/**
	 * R2.
	 *
	 * @param phi the phi
	 * @return the simple matrix
	 */
	private SimpleMatrix R2(final double phi) {
		final float c = (float) Math.cos(phi);
		final float s = (float) Math.sin(phi);

		float[][] R = { {  c, 0,-s }, 
				{  0, 1, 0 },
				{  s, 0, c } };	
		
		return new SimpleMatrix(R);
	}

	/**
	 * R3.
	 *
	 * @param phi the phi
	 * @return the simple matrix
	 */
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
	    return fromEpoch.toString() + " --> " + toEpoch.toString();
	}
	
}
