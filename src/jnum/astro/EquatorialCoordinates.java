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

package jnum.astro;


import jnum.Constant;
import jnum.SafeMath;
import jnum.Unit;
import jnum.Util;
import jnum.math.CoordinateAxis;
import jnum.math.CoordinateSystem;
import jnum.math.Vector2D;
import jnum.text.GreekLetter;
import jnum.text.HourAngleFormat;


// TODO: Auto-generated Javadoc
// x, y kept in longitude,latitude form
// use RA(), DEC(), setRA() and setDEC(functions) to for RA, DEC coordinates...

public class EquatorialCoordinates extends PrecessingCoordinates {

	private static final long serialVersionUID = 3445122576647034180L;
	


    public EquatorialCoordinates() { }
    
    
    public EquatorialCoordinates(CoordinateEpoch epoch) { 
        super(epoch);
    }


	public EquatorialCoordinates(String text) { super(text); }


	public EquatorialCoordinates(double ra, double dec) { 
	    super(ra, dec); 
	}


	public EquatorialCoordinates(double ra, double dec, double epochYear) { 
	    super(ra, dec, epochYear);
	}


	public EquatorialCoordinates(double ra, double dec, String epochSpec) { 
	    super(ra, dec, epochSpec); 
	 
	}
	

	public EquatorialCoordinates(double ra, double dec, CoordinateEpoch epoch) { 
	    super(ra, dec, epoch); 
	}
		

	public EquatorialCoordinates(CelestialCoordinates from) { super(from); }
	
	
	/* (non-Javadoc)
	 * @see jnum.math.SphericalCoordinates#getFITSLongitudeStem()
	 */
	@Override
	public String getFITSLongitudeStem() { return "RA--"; }
	
	/* (non-Javadoc)
	 * @see jnum.math.SphericalCoordinates#getFITSLatitudeStem()
	 */
	@Override
	public String getFITSLatitudeStem() { return "DEC-"; }
	
	
	@Override
    public String getTwoLetterCode() { return "EQ"; }
	
	
	  
    @Override
    public CoordinateSystem getCoordinateSystem() {
        return defaultCoordinateSystem;
    }
     
    @Override
    public CoordinateSystem getLocalCoordinateSystem() {
        return defaultLocalCoordinateSystem;
    }
    	

	public final double RA() { return zeroToTwoPi(longitude()); }


	public final double rightAscension() { return RA(); }


	public final double DEC() { return latitude(); }


	public final double declination() { return DEC(); }


	public final void setRA(double RA) { setLongitude(RA); }


	public final void setDEC(double DEC) { setLatitude(DEC); }


	public double getParallacticAngle(GeodeticCoordinates site, double LST) {
		final double H = LST * Unit.timeAngle - RA();
		return Math.atan2(site.cosLat() * Math.sin(H), site.sinLat() * cosLat() - site.cosLat() * sinLat() * Math.cos(H));
	}
	
	/* (non-Javadoc)
	 * @see jnum.astro.CelestialCoordinates#getEquatorialPositionAngle()
	 */
	@Override
	public final double getEquatorialPositionAngle() {
		return 0.0;
	}
	
	
	/* (non-Javadoc)
	 * @see jnum.astro.CelestialCoordinates#toEquatorial(jnum.astro.EquatorialCoordinates)
	 */
	@Override
	public void toEquatorial(EquatorialCoordinates equatorial) {
	    equatorial.copy(this);	
	}
	
	/* (non-Javadoc)
	 * @see jnum.astro.CelestialCoordinates#fromEquatorial(jnum.astro.EquatorialCoordinates)
	 */
	@Override
	public void fromEquatorial(EquatorialCoordinates equatorial) {	
		copy(equatorial);
	}
	

	public HorizontalCoordinates toHorizontal(GeodeticCoordinates site, double LST) {
		HorizontalCoordinates horizontal = new HorizontalCoordinates();
		toHorizontal(this, horizontal, site, LST);
		return horizontal;
	}
	

	public void toHorizontal(HorizontalCoordinates toCoords, GeodeticCoordinates site, double LST) { toHorizontal(this, toCoords, site, LST); }
	
	
	public void toHorizontalOffset(Vector2D offset, GeodeticCoordinates site, double LST) {
		toHorizontalOffset(offset, getParallacticAngle(site, LST));
	}


	public static void toHorizontalOffset(Vector2D offset, double PA) {
		offset.scaleX(-1.0);
		offset.rotate(-PA);
	}
	

	/*
	public static void toHorizontal(EquatorialCoordinates equatorial, HorizontalCoordinates horizontal, GeodeticCoordinates site, double LST) {
		double H = LST * Unit.timeAngle - equatorial.RA();
		double cosH = Math.cos(H);
		horizontal.setNativeLatitude(asin(equatorial.sinLat() * site.sinLat() + equatorial.cosLat() * site.cosLat() * cosH));
		double asinA = -Math.sin(H) * equatorial.cosLat();
		double acosA = site.cosLat() * equatorial.sinLat() - site.sinLat() * equatorial.cosLat() * cosH;
		horizontal.setLongitude(Math.atan2(asinA, acosA));
	}
	*/
	
	public static void toHorizontal(EquatorialCoordinates equatorial, HorizontalCoordinates horizontal, GeodeticCoordinates site, double LST) {	
		double H = LST * Unit.timeAngle - equatorial.RA();	
		double cosH = Math.cos(H);
		horizontal.setLatitude(SafeMath.asin(equatorial.sinLat() * site.sinLat() + equatorial.cosLat() * site.cosLat() * cosH));
		double asinA = -Math.sin(H) * equatorial.cosLat() * site.cosLat();
		double acosA = equatorial.sinLat() - site.sinLat() * horizontal.sinLat();
		horizontal.setLongitude(Math.atan2(asinA, acosA));
	}
	

	@Override
	public void precessUnchecked(CoordinateEpoch newEpoch) {
	    if(epoch.equals(newEpoch)) return;
		Precession precession = new Precession(epoch, newEpoch);
		precession.precess(this);
	}
	
	/* (non-Javadoc)
	 * @see jnum.SphericalCoordinates#toString()
	 */
	@Override
	public String toString() {
		haf.setDecimals(getDefaultDecimals() + 1);
		return super.toString();	
	}
	
	/* (non-Javadoc)
	 * @see jnum.math.SphericalCoordinates#toString(int)
	 */
	@Override
	public String toString(int decimals) {
		return Util.hf[decimals+1].format(longitude()) + " " + Util.af[decimals].format(latitude()) +
		        (epoch == null ? "" : " (" + epoch + ")");	
	}
	

	/* (non-Javadoc)
	 * @see jnum.astro.CelestialCoordinates#getEquatorialPole()
	 */
	@Override
	public EquatorialCoordinates getEquatorialPole() { return equatorialPole; }

	/* (non-Javadoc)
	 * @see jnum.astro.CelestialCoordinates#getZeroLongitude()
	 */
	@Override
	public double getZeroLongitude() { return 0.0; }
	

    @SuppressWarnings("hiding")
    public static CoordinateSystem defaultCoordinateSystem, defaultLocalCoordinateSystem;


    private static HourAngleFormat haf = new HourAngleFormat(2);
    

    
    static {
        defaultCoordinateSystem = new CoordinateSystem("Equatorial Coordinates");
        defaultLocalCoordinateSystem = new CoordinateSystem("Equatorial Offsets");
        
        CoordinateAxis rightAscentionAxis = createAxis("Right Ascension", "RA", GreekLetter.alpha + "", haf);
        rightAscentionAxis.setReverse(true);

        CoordinateAxis declinationAxis = createAxis("Declination", "DEC", GreekLetter.delta + "", af);
   
        CoordinateAxis rightAscentionOffsetAxis = createOffsetAxis("Right Ascension Offset", "dRA", GreekLetter.Delta + " " + GreekLetter.alpha);
        rightAscentionOffsetAxis.setReverse(true);
         
        CoordinateAxis declinationOffsetAxis = createOffsetAxis("Declination Offset", "dDEC", GreekLetter.Delta + " " + GreekLetter.delta);
        
        defaultCoordinateSystem.add(rightAscentionAxis);
        defaultCoordinateSystem.add(declinationAxis);
        
        defaultLocalCoordinateSystem.add(rightAscentionOffsetAxis);
        defaultLocalCoordinateSystem.add(declinationOffsetAxis);        
    }	
	
  

    private static EquatorialCoordinates equatorialPole = new EquatorialCoordinates(0.0, Constant.rightAngle);



    public final static int NORTH = 1;

    public final static int SOUTH = -1;

    public final static int EAST = -1;

    public final static int WEST = 1;

}
