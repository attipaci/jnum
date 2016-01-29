package test;

import jnum.Unit;
import jnum.math.*;
import jnum.projection.*;
import jnum.util.*;

public class ProjectionTest {

	public static void main(String[] args) {
		SphericalCoordinates NE = new SphericalCoordinates(1.0 * Unit.deg, 1.0 * Unit.deg);
		SphericalCoordinates NW = new SphericalCoordinates(-1.0 * Unit.deg, 1.0 * Unit.deg);
		SphericalCoordinates SE = new SphericalCoordinates(1.0 * Unit.deg, -1.0 * Unit.deg);
		SphericalCoordinates SW = new SphericalCoordinates(-1.0 * Unit.deg, -1.0 * Unit.deg);
		
		Vector2D offset = new Vector2D(10.0 * Unit.arcsec, 10.0 * Unit.arcsec);
		Vector2D projected = new Vector2D();
		SphericalCoordinates coords = new SphericalCoordinates();
		
		SphericalProjection projection = new SansonFlamsteed();
		
		projection.setReference(NE);
		coords.copy(NE);
		coords.addOffset(offset);
		projection.project(coords, projected);
		projection.deproject(projected, coords);
		projected.scale(1.0 / Unit.arcsec);
		System.err.println("### NE: " + projected.toString() + "\t" + coords);
		
		projection.setReference(NW);
		coords.copy(NW);
		coords.addOffset(offset);
		projection.project(coords, projected);
		projection.deproject(projected, coords);
		projected.scale(1.0 / Unit.arcsec);
		System.err.println("### NW: " + projected.toString() + "\t" + coords);
		
		projection.setReference(SE);
		coords.copy(SE);
		coords.addOffset(offset);
		projection.project(coords, projected);
		projection.deproject(projected, coords);
		projected.scale(1.0 / Unit.arcsec);
		System.err.println("### SE: " + projected.toString() + "\t" + coords);
		
		projection.setReference(SW);
		coords.copy(SW);
		coords.addOffset(offset);
		projection.project(coords, projected);
		projection.deproject(projected, coords);
		projected.scale(1.0 / Unit.arcsec);
		System.err.println("### SW: " + projected.toString() + "\t" + coords);
		
		
	}
	
}
