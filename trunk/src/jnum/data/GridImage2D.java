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
package jnum.data;

import java.io.IOException;

import jnum.Constant;
import jnum.ExtraMath;
import jnum.Unit;
import jnum.Util;
import jnum.fft.MultiFFT;
import jnum.io.fits.FitsExtras;
import jnum.math.Coordinate2D;
import jnum.math.SphericalCoordinates;
import jnum.math.Vector2D;
import jnum.math.specialfunctions.CumulativeNormalDistribution;
import jnum.projection.Projection2D;
import jnum.text.TableFormatter;
import jnum.util.HashCode;
import nom.tam.fits.BasicHDU;
import nom.tam.fits.FitsException;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;
import nom.tam.fits.ImageHDU;
import nom.tam.util.Cursor;

// TODO: Auto-generated Javadoc
/**
 * The Class GridImage.
 *
 * @param <CoordinateType> the generic type
 */
public class GridImage2D<CoordinateType extends Coordinate2D> extends Data2D {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -3974721346587627074L;

	/** The grid. */
	private Grid2D<CoordinateType> grid;
	
	/** The underlying beam. */
	private GaussianPSF underlyingBeam;
	
	/** The smoothing. */
	private GaussianPSF smoothing;
	
	/** The ext filter fwhm. */
	private double extFilterFWHM = Double.NaN;
	
	/** The correcting fwhm. */
	private double correctingFWHM = Double.NaN;	

	/** The beam area. */
	private Unit beamArea = new BeamArea();
	
	/** The pixel area. */
	private Unit pixelAreaUnit = new PixelAreaUnit();
	
	/** The preferred grid unit. */
	private Unit preferredGridUnit;
	
	/**
	 * Instantiates a new grid image2 d.
	 */
	/*
	 * Instantiates a new grid image.
	 */
	public GridImage2D() {}
	
	
	/**
	 * Instantiates a new grid map.
	 *
	 * @param fileName the file name
	 * @throws Exception the exception
	 */
	public GridImage2D(String fileName) throws Exception { 
		super(fileName);		
	}

	
	/**
	 * Instantiates a new grid image.
	 *
	 * @param grid the grid
	 */
	public GridImage2D(Grid2D<CoordinateType> grid) {
		this();
		setGrid(grid);
	}
	

	/**
	 * Instantiates a new grid image.
	 *
	 * @param sizeX the size x
	 * @param sizeY the size y
	 */
	public GridImage2D(int sizeX, int sizeY) {
		super(sizeX, sizeY);
	}
	
	/**
	 * Instantiates a new grid image.
	 *
	 * @param data the data
	 */
	public GridImage2D(double[][] data) {
		super(data);
	}
	
	/**
	 * Instantiates a new grid image.
	 *
	 * @param data the data
	 * @param flag the flag
	 */
	public GridImage2D(double[][] data, int[][] flag) {
		super(data, flag);
	}
	
	
	/* (non-Javadoc)
	 * @see jnum.data.Data2D#defaults()
	 */
	@Override
	public void defaults() {
		super.defaults();
		underlyingBeam = new GaussianPSF("I", "instrument");
		smoothing = new GaussianPSF("S", "smoothing");
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if(o == this) return true;
		if(!(o instanceof GridImage2D)) return false;
		if(!super.equals(o)) return false;
		GridImage2D<?> i = (GridImage2D<?>) o;
		
		if(!Util.equals(grid, i.grid)) return false;
		if(!Util.equals(underlyingBeam, i.underlyingBeam)) return false;
		if(!Util.equals(smoothing, i.smoothing)) return false;
		if(Double.compare(extFilterFWHM, i.extFilterFWHM) != 0) return false;
		if(Double.compare(correctingFWHM, i.correctingFWHM) != 0) return false;
		
		return true;
	}
	
	/* (non-Javadoc)
	 * @see kovacs.data.Data2D#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = super.hashCode() ^ underlyingBeam.hashCode() ^ smoothing.hashCode() 
				^ HashCode.from(extFilterFWHM) ^ HashCode.from(correctingFWHM);
		if(grid != null) hash ^= grid.hashCode();
		return hash;
	} 
	
	
	
	/* (non-Javadoc)
	 * @see kovacs.util.data.Data2D#copy(boolean)
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	@Override
	public Data2D copy(boolean copyContent) {
		GridImage2D<CoordinateType> copy = (GridImage2D<CoordinateType>) super.copy(copyContent);
		
		if(grid != null) copy.grid = (Grid2D<CoordinateType>) grid.copy();
		if(underlyingBeam != null) copy.underlyingBeam = underlyingBeam.copy();
		if(smoothing != null) copy.smoothing = smoothing.copy();
		if(beamArea != null) copy.beamArea = (BeamArea) beamArea.copy();
		if(pixelAreaUnit != null) copy.pixelAreaUnit = (PixelAreaUnit) pixelAreaUnit.copy();
		return copy;
	}
	
	/**
	 * Gets the grid.
	 *
	 * @return the grid
	 */
	public Grid2D<CoordinateType> getGrid() { return grid; }
	
	
	/**
	 * Sets the grid.
	 *
	 * @param grid the new grid
	 */
	// TODO non-rectilinear grids.
	public void setGrid(Grid2D<CoordinateType> grid) { 
		this.grid = grid; 
		Vector2D resolution = grid.getResolution();
		smoothing.encompass(new GaussianPSF(resolution.x() / fwhm2size, resolution.y() / fwhm2size, 0.0));
	
		// TODO default beam unit for non-spherical grids. Same as grid unit?
		Unit u = grid instanceof SphericalGrid ? Unit.get("deg") : rawUnit;
			
		smoothing.setSizeUnit(u);
		underlyingBeam.setSizeUnit(u);	
	}
	
	
	/**
	 * Reset smoothing.
	 */
	public void resetSmoothing() {
		Vector2D resolution = grid.getResolution();
		smoothing.set(resolution.x() / fwhm2size, resolution.y() / fwhm2size, 0.0);
	}
	
	/**
	 * Gets the beam area unit.
	 *
	 * @return the beam area unit
	 */
	public Unit getBeamAreaUnit() { return beamArea; }
	
	/**
	 * Gets the pixel area unit.
	 *
	 * @return the pixel area unit
	 */
	public Unit getPixelAreaUnit() { return pixelAreaUnit; }
	
	/**
	 * Sets the resolution.
	 *
	 * @param value the new resolution
	 */
	public final void setResolution(double value) { 
		setResolution(value, value);
	}
	
	/**
	 * Sets the resolution.
	 *
	 * @param dx the dx
	 * @param dy the dy
	 */
	// TODO non rectilinear grids
	public void setResolution(double dx, double dy) { 
		getGrid().setResolution(dx, dy);
		smoothing.encompass(new GaussianPSF(dx / fwhm2size, dy / fwhm2size, 0.0));
	}
	
	
	/**
	 * Gets the resolution.
	 *
	 * @return the resolution
	 */
	public Vector2D getResolution() {
		return getGrid().getResolution();
	}
	
	/**
	 * Gets the smooth fwhm.
	 *
	 * @return the smooth fwhm
	 */
	public GaussianPSF getSmoothing() { return smoothing; } 
	
	/**
	 * Gets the smooth area.
	 *
	 * @return the smooth area
	 */
	public double getSmoothArea() { 
		return smoothing.getArea();
	}
	
	/**
	 * Gets the ext filter fwhm.
	 *
	 * @return the ext filter fwhm
	 */
	public double getExtFilterFWHM() { return extFilterFWHM; }
	
	/**
	 * Gets the correcting fwhm.
	 *
	 * @return the correcting fwhm
	 */
	public double getCorrectingFWHM() { return correctingFWHM; }
	
	/**
	 * No ext filter.
	 */
	public void noExtFilter() { extFilterFWHM = Double.NaN; }

	/**
	 * Gets the projection.
	 *
	 * @return the projection
	 */
	public Projection2D<CoordinateType> getProjection() { return getGrid().getProjection(); }
	
	/**
	 * Sets the projection.
	 *
	 * @param projection the new projection
	 */
	public void setProjection(Projection2D<CoordinateType> projection) { getGrid().setProjection(projection); }
	
	/**
	 * Gets the reference.
	 *
	 * @return the reference
	 */
	public CoordinateType getReference() { return getGrid().getReference(); }
	
	/**
	 * Sets the reference.
	 *
	 * @param reference the new reference
	 */
	public void setReference(CoordinateType reference) { getGrid().setReference(reference); }
		
	/**
	 * Gets the image.
	 *
	 * @param data the data
	 * @param contentType the content type
	 * @param unit the unit
	 * @return the image
	 */
	protected GridImage2D<CoordinateType> getImage(double[][] data, String contentType, Unit unit) {
		@SuppressWarnings("unchecked")
		GridImage2D<CoordinateType> image = (GridImage2D<CoordinateType>) clone();
		image.setData(data);
		image.setContentType(contentType);
		image.setUnit(unit);
		return image;		
	}

	/**
	 * Gets the flux image.
	 *
	 * @return the flux image
	 */
	public GridImage2D<CoordinateType> getFluxImage() {
		return getImage(getData(), "Flux", getUnit());
	}
	
	/**
	 * Gets the pixel area.
	 *
	 * @return the pixel area
	 */
	public double getPixelArea() {
		return getGrid().getPixelArea();
	}
	
	
	/**
	 * Gets the points per smoothing beam.
	 *
	 * @return the points per smoothing beam
	 */
	public double getPointsPerSmoothingBeam() {
		return Math.max(1.0, smoothing.getArea() / getGrid().getPixelArea());
	}
	
	/**
	 * Gets the underlying fwhm.
	 *
	 * @return the underlying fwhm
	 */
	public final GaussianPSF getUnderlyingBeam() { return underlyingBeam; }
	
	/**
	 * Sets the underlying beam.
	 *
	 * @param psf the new underlying beam
	 */
	public void setUnderlyingBeam(GaussianPSF psf) { underlyingBeam = psf; }
	
	/**
	 * Sets the underlying beam.
	 *
	 * @param fwhm the new underlying beam
	 */
	public void setUnderlyingBeam(double fwhm) { underlyingBeam.set(fwhm); }
	
	/**
	 * Gets the image fwhm.
	 *
	 * @return the image fwhm
	 */
	public GaussianPSF getImageBeam() {
		GaussianPSF beam = new GaussianPSF();
		getImageBeam(beam);
		return beam;
	}
	
	/**
	 * Gets the image beam.
	 *
	 * @param toPSF the to psf
	 * @return the image beam
	 */
	public void getImageBeam(GaussianPSF toPSF) {
		toPSF.set(underlyingBeam);
		toPSF.convolveWith(smoothing);
	}
	
	
	/**
	 * Gets the image beam area.
	 *
	 * @return the image beam area
	 */
	public double getImageBeamArea() {
		return underlyingBeam.getArea() + smoothing.getArea();
	}
	
	/**
	 * Gets the filter correction factor.
	 *
	 * @param underlyingFWHM the underlying fwhm
	 * @return the filter correction factor
	 */
	public double getFilterCorrectionFactor(double underlyingFWHM) {
		if(Double.isNaN(extFilterFWHM)) return 1.0;
	
		double effectiveFWHM2 = underlyingFWHM * underlyingFWHM + smoothing.getMajorFWHM() * smoothing.getMinorFWHM();
		double effectiveFilterFWHM2 = underlyingFWHM * underlyingFWHM + extFilterFWHM * extFilterFWHM;
		return 1.0 / (1.0 - effectiveFWHM2/effectiveFilterFWHM2);
	}
	
	/**
	 * Reset.
	 *
	 * @param clearContent the clear content
	 */
	// TODO non-rectilinear grids...
	public void reset(boolean clearContent) {
		resetSmoothing();
		extFilterFWHM = Double.NaN;
		correctingFWHM = Double.NaN;
		if(clearContent) clear();
		clearHistory();
	}

	// In 1-D at least 3 points per beam are needed to separate a positioned point
	// source from an extended source...
	// Similarly 9 points per beam are necessary for 2-D...
	/**
	 * Count independent points.
	 *
	 * @param area the area
	 * @return the double
	 */
	public double countIndependentPoints(double area) {
		double smoothArea = smoothing.getArea();
		double filterArea = fwhm2size * fwhm2size * extFilterFWHM * extFilterFWHM;
		double beamArea = this.getImageBeamArea();
		
		// Account for the filtering correction.
		double eta = 1.0;
		if(Double.isNaN(extFilterFWHM) && extFilterFWHM > 0.0) eta -= smoothArea / filterArea;
		double iPointsPerBeam = eta * Math.min(9.0, smoothArea / getPixelArea());
		
		return Math.ceil((1.0 + area/beamArea) * iPointsPerBeam);
	}
	
	
	/**
	 * Sets the preferred grid unit.
	 *
	 * @param u the new preferred grid unit
	 */
	public void setPreferredGridUnit(Unit u) {
		preferredGridUnit = u;
	}
	
	/**
	 * Gets the preferred grid unit.
	 *
	 * @return the preferred grid unit
	 */
	public final Unit getPreferredGridUnit() {
		return getPreferredGridUnit(false);
	}
	
	/**
	 * Gets the preferred grid unit.
	 *
	 * @param ignoreUserSpecified the ignore user specified
	 * @return the preferred grid unit
	 */
	public Unit getPreferredGridUnit(boolean ignoreUserSpecified) {
		if(!ignoreUserSpecified) if(preferredGridUnit != null) return preferredGridUnit;
		Grid2D<CoordinateType> grid = getGrid();
		if(grid == null) return rawUnit;
		if(grid.xUnit.equals(grid.yUnit)) return grid.xUnit;
		if(grid.getReference() instanceof SphericalCoordinates) return Unit.get("arcsec");
		return rawUnit;	
	}
	
	/**
	 * Crop.
	 *
	 * @param dXmin the d xmin
	 * @param dYmin the d ymin
	 * @param dXmax the d xmax
	 * @param dYmax the d ymax
	 */
	public void crop(double dXmin, double dYmin, double dXmax, double dYmax) {
		if(dXmin > dXmax) { double temp = dXmin; dXmin = dXmax; dXmax=temp; }
		if(dYmin > dYmax) { double temp = dYmin; dYmin = dYmax; dYmax=temp; }

		Unit sizeUnit = getPreferredGridUnit();
		
		if(isVerbose()) System.err.println("Will crop to " + ((dXmax - dXmin)/sizeUnit.value()) + "x" + ((dYmax - dYmin)/sizeUnit.value()) + " " + sizeUnit.name() + ".");
				
		Index2D c1 = getIndex(new Vector2D(dXmin, dYmin));
		Index2D c2 = getIndex(new Vector2D(dXmax, dYmax));
		
		crop(c1.i(), c1.j(), c2.i(), c2.j());
	}

	/* (non-Javadoc)
	 * @see kovacs.util.data.Data2D#crop(int, int, int, int)
	 */
	@Override
	protected void crop(int imin, int jmin, int imax, int jmax) {
		super.crop(imin, jmin, imax, jmax);
		Vector2D refIndex = getGrid().getReferenceIndex();
		
		refIndex.subtractX(imin);
		refIndex.subtractY(jmin);
	}
	
	/**
	 * Grow flags.
	 *
	 * @param radius the radius
	 * @param pattern the pattern
	 */
	public void growFlags(final double radius, final int pattern) {
		if(isVerbose()) System.err.println("Growing flagged areas.");
		
		final double dx = getGrid().pixelSizeX();
		final double dy = getGrid().pixelSizeY();
		
		final int di = (int)Math.ceil(radius / dx);
		final int dj = (int)Math.ceil(radius / dy);
		
		final int sizeX = sizeX();
		final int sizeY = sizeY();

		new Task<Void>() {
			@Override
			protected void process(int i, int j) {
				if(isFlagged(i, j, pattern)) {
					final int fromi1 = Math.max(0, i-di);
					final int fromj1 = Math.max(0, j-dj);
					final int toi1 = Math.max(sizeX, i+di+1);
					final int toj1 = Math.max(sizeY, j+dj+1);
					final int matchPattern = getFlag(i, j) & pattern;
					
					// TODO for sheared grids...
					for(int i1 = toi1; --i1 >= fromi1; ) for(int j1 = toj1; --j1 >= fromj1; ) 
						if(ExtraMath.hypot((i-i1) * dx, (j-j1) * dy) <= radius) flag(i1, j1, matchPattern);
				}
			}
		}.process();
	}
	
	
		
	/**
	 * Count beams.
	 *
	 * @return the double
	 */
	public double countBeams() { return getArea() / getImageBeamArea(); }

	/**
	 * Gets the area.
	 *
	 * @return the area
	 */
	public double getArea() { return countPoints() * getPixelArea(); }

	/* (non-Javadoc)
	 * @see jnum.data.Data2D#mergePropertiesWith(jnum.data.Data2D)
	 */
	@Override
	protected synchronized void mergePropertiesWith(final Data2D data) {
		super.mergePropertiesWith(data);
		
		if(!(data instanceof GridImage2D)) return;
		
		GridImage2D<?> image = (GridImage2D<?>) data;
		
		smoothing.encompass(image.smoothing);
		extFilterFWHM = Math.min(extFilterFWHM, image.extFilterFWHM);
	}
	

	/**
	 * Smooth.
	 *
	 * @param beam the beam
	 * @param equivalentSmoothFWHM the equivalent smooth fwhm
	 */
	public void smooth(double[][] beam, double equivalentSmoothFWHM) {
		smooth(beam);
		smoothing.convolveWith(new GaussianPSF(equivalentSmoothFWHM));
	}
	
	
	// Convolves image to the specified beam resolution
	// by a properly chosen convolving beam...
	/**
	 * Smooth to.
	 *
	 * @param FWHM the fwhm
	 */
	public final void smoothTo(double FWHM) {
		smoothTo(new GaussianPSF(FWHM));
	}	
	
	/**
	 * Smooth to.
	 *
	 * @param psf the psf
	 */
	public void smoothTo(GaussianPSF psf) {
		if(smoothing.isEncompassing(psf)) return;
		psf.deconvolveWith(smoothing);
		smooth(psf);
	}
	
	
	
	/**
	 * Smooth.
	 *
	 * @param FWHM the fwhm
	 */
	public final void smooth(double FWHM) {
		smooth(new GaussianPSF(FWHM));
			
		// The correcting FWHM is underlying FWHM...
		//if(!Double.isNaN(correctingFWHM)) correctingFWHM = ExtraMath.hypot(correctingFWHM, FWHM);
	}
	
	/**
	 * Smooth.
	 *
	 * @param psf the psf
	 */
	// TODO
	public final void smooth(GaussianPSF psf) {
		int stepX = (int)Math.ceil(psf.extentInX()/(5.0 * getGrid().pixelSizeX()));
		int stepY = (int)Math.ceil(psf.extentInX()/(5.0 * getGrid().pixelSizeY()));

		fastSmooth(psf.getBeam(getGrid()), stepX, stepY);	
		
		smoothing.convolveWith(psf);
	}
	
	/**
	 * Gets the smoothed to.
	 *
	 * @param FWHM the fwhm
	 * @return the smoothed to
	 */
	public double[][] getSmoothedTo(double FWHM) {
		return getSmoothedTo(new GaussianPSF(FWHM));
	}
	
	/**
	 * Gets the smoothed to.
	 *
	 * @param psf the psf
	 * @return the smoothed to
	 */
	public double[][] getSmoothedTo(GaussianPSF psf) {
		if(smoothing.isEncompassing(psf)) return getData();
		psf.deconvolveWith(smoothing);
		return getSmoothed(psf);
	}
	
	/**
	 * Gets the smoothed.
	 *
	 * @param FWHM the fwhm
	 * @return the smoothed
	 */
	public double[][] getSmoothed(double FWHM) {
		return getSmoothed(new GaussianPSF(FWHM));
	}   

	/**
	 * Gets the smoothed.
	 *
	 * @param psf the psf
	 * @return the smoothed
	 */
	public double[][] getSmoothed(GaussianPSF psf) {
		int stepX = (int)Math.ceil(psf.extentInX()/(5.0 * getGrid().pixelSizeX()));
		int stepY = (int)Math.ceil(psf.extentInY()/(5.0 * getGrid().pixelSizeY()));
		return getFastSmoothed(psf.getBeam(getGrid()), null, stepX, stepY);
	}
	
	
	/**
	 * Filter above.
	 *
	 * @param FWHM the fwhm
	 */
	public void filterAbove(double FWHM) { filterAbove(FWHM, getFlag()); }

	/**
	 * Filter above.
	 *
	 * @param FWHM the fwhm
	 * @param skip the skip
	 */
	public void filterAbove(double FWHM, int[][] skip) {
		final GridImage2D<?> extended = (GridImage2D<?>) copy(true);
		extended.setFlag(skip);
		extended.smoothTo(FWHM);
		
		new Task<Void>() {
			@Override
			protected void process(int i, int j) {
				decrement(i, j, extended.getValue(i, j));
			}
		}.process();
		
		if(Double.isNaN(extFilterFWHM)) extFilterFWHM = FWHM;
		else extFilterFWHM = 1.0/Math.sqrt(1.0/(extFilterFWHM * extFilterFWHM) + 1.0/(FWHM*FWHM));
		
	}
	
	//	 8/20/07 Changed to use blanking 
	//         Using Gaussian taper.
	//         Robust re-levelling at the end.
	/**
	 * Fft filter above.
	 *
	 * @param FWHM the fwhm
	 * @param skip the skip
	 */
	public void fftFilterAbove(double FWHM, final int[][] skip) {
		// sigma_x sigma_w = 1
		// FWHM_x sigma_w = 2.35
		// FWHM_x * 2Pi sigma_f = 2.35
		// sigma_f = 2.35/2Pi * 1.0/FWHM_x
		// delta_f = 1.0/(Nx * delta_x);
		// sigma_nf = sigma_f / delta_x = 2.35 * Nx * delta_x / (2Pi * FWHM_x)
		
		// Try to get an honest estimate of the extended structures using FFT (while blanking bright sources).
		// Then remove it from the original image...
		final double[][] extended = new double[sizeX()][sizeY()];
		
		Task<WeightedPoint> ecalc = new AveragingTask() {
			private double sumw = 0.0;
			private int n = 0;
			@Override
			protected void process(final int i, final int j) {
				if(getFlag(i, j) != 0) return;	
				if(skip[i][j] > 0) extended[i][j] = 0.0;
				else {
					final double w = getWeight(i, j);
					extended[i][j] = w * getValue(i, j);
					sumw += w;
					n++;
				}
			}
			@Override
			public WeightedPoint getPartialResult() { return new WeightedPoint(sumw, n); }
		};
		ecalc.process();
		
		final int nx = ExtraMath.pow2ceil(sizeX());
		final int ny = ExtraMath.pow2ceil(sizeY());
		final int nx2 = nx>>1;
		
		final double[][] transformer = new double[nx][ny+2];
		for(int i=sizeX(); --i >= 0; ) System.arraycopy(extended[i], 0, transformer[i], 0, sizeY()); 	
				
		final MultiFFT fft = new MultiFFT();
		fft.setThreads(getParallel());
		fft.real2Amplitude(transformer);

		final double sigmax = Constant.sigmasInFWHM * nx * getGrid().pixelSizeX() / (Constant.twoPi * FWHM);
		final double sigmay = Constant.sigmasInFWHM * ny * getGrid().pixelSizeY() / (Constant.twoPi * FWHM);
		
		final double ax = -0.5/(sigmax*sigmax);
		final double ay = -0.5/(sigmay*sigmay);

		for(int fx=nx2; --fx>0; ) {
			final double axfx2 = ax*fx*fx;
			final double[] r1 = transformer[fx];			// The positive frequencies
			final double[] r2 = transformer[nx - fx - 1];	// The negative frequencies
			
			// The unrolled real spectrum...
			for(int fy=0; fy <= ny; fy += 2) {
				final double A = Math.exp(axfx2 + ay*fy*fy);
				
				r1[fy] *= A;
				r1[fy+1] *= A;
				
				r2[fy] *= A;
				r2[fy+1] *= A;
			}
		}
			
		fft.amplitude2Real(transformer);
		
		for(int i=sizeX(); --i >= 0; ) System.arraycopy(transformer[i], 0, extended[i], 0, sizeY()); 	
	
		final double avew = ecalc.getResult().value();
		if(avew > 0.0) {
			final double norm = 1.0 / avew;
			new Task<Void>() {
				@Override
				public void process(int i, int j) {
					decrement(i, j, norm * extended[i][j]);
				}
			}.process();
		}
		
		if(Double.isNaN(extFilterFWHM)) extFilterFWHM = FWHM;
		else extFilterFWHM = 1.0/Math.sqrt(1.0/(extFilterFWHM * extFilterFWHM) + 1.0/(FWHM*FWHM));	
	}
	
	
	/**
	 * Filter correct.
	 *
	 * @param underlyingFWHM the fwhm
	 * @param skip the skip
	 */
	public void filterCorrect(double underlyingFWHM, final int[][] skip) {	
		// Undo prior corrections if necessary
		if(!Double.isNaN(correctingFWHM)) { 
			if(underlyingFWHM == correctingFWHM) return;
			else undoFilterCorrect(skip);
		}
		final double filterC = getFilterCorrectionFactor(underlyingFWHM);
				
		new Task<Void>() {
			@Override
			protected void process(int i, int j) {
				if(skip[i][j] == 0) scale(i, j, filterC);
			}
		}.process();
		
		correctingFWHM = underlyingFWHM;
	}
	
	/**
	 * Undo filter correct.
	 *
	 * @param skip the skip
	 */
	public void undoFilterCorrect(final int[][] skip) {
		if(Double.isNaN(correctingFWHM)) return;
		
		final double iFilterC = 1.0 / getFilterCorrectionFactor(correctingFWHM);
			
		new Task<Void>() {
			@Override
			protected void process(int i, int j) {
				if(skip[i][j] == 0) scale(i, j, iFilterC);
			}
		}.process();
		
		correctingFWHM = Double.NaN;
	}
	
	
	/**
	 * Gets the beam.
	 *
	 * @return the beam
	 */
	public double[][] getBeam() {
		return getImageBeam().getBeam(getGrid());
	}


	/**
	 * Resample.
	 *
	 * @param from the from
	 */
	@SuppressWarnings("unchecked")
	public void resample(GridImage2D<CoordinateType> from) {
		if(isVerbose()) System.err.println(" Resampling image to "+ sizeX() + "x" + sizeY() + ".");
		
		reset(false);
		
		// Antialias filter first...
		if(!from.smoothing.isEncompassing(smoothing)) {
			from = (GridImage2D<CoordinateType>) from.copy(true);
			from.smoothTo(smoothing);
		}
		
		final GridImage2D<?> antialiased = from;
		
		new InterpolatingTask() {
			private Vector2D v;
			
			@Override
			protected void init() {
				super.init();
				v = new Vector2D();
			}
			
			@Override
			protected void process(int i, int j) { 
				v.set(i, j);
				toOffset(v);
				antialiased.toIndex(v);
				
				setValue(i, j, antialiased.valueAtIndex(v.x(), v.y(), getInterpolatorData()));
				
				if(isNaN(i, j)) flag(i, j);
				else unflag(i, j);			
			}
		}.process();
	}
	
	/**
	 * Gets the regrid.
	 *
	 * @param resolution the resolution
	 * @return the regrid
	 * @throws IllegalStateException the illegal state exception
	 */
	public GridImage2D<CoordinateType> getRegrid(final double resolution) throws IllegalStateException {
		return getRegrid(new Vector2D(resolution, resolution));
	}

	/**
	 * Gets the regrid.
	 *
	 * @param resolution the resolution
	 * @return the regrid
	 * @throws IllegalStateException the illegal state exception
	 */
	public GridImage2D<CoordinateType> getRegrid(final Vector2D resolution) throws IllegalStateException {	
		Vector2D dRes = new Vector2D(resolution.x() / getGrid().pixelSizeX(), resolution.y() / getGrid().pixelSizeY());
		Grid2D<CoordinateType> toGrid = getGrid().copy();
		
		Vector2D refIndex = toGrid.getReferenceIndex();
		
		if(isVerbose()) System.err.print(" Reference index: " + refIndex.toString(Util.f1));
		
		refIndex.scaleX(1.0 / dRes.x());
		refIndex.scaleY(1.0 / dRes.y());
		
		if(isVerbose()) System.err.println(" --> " + refIndex.toString(Util.f1));
		
		double[][] M = getGrid().getTransform();
		M[0][0] *= dRes.x();
		M[0][1] *= dRes.y();
		M[1][0] *= dRes.x();
		M[1][1] *= dRes.y();
		toGrid.setTransform(M);
		
		//System.err.println(" M = {{" + M[0][0] + ", " + M[0][1] + "}, {" + M[1][0] + "," + M[1][1] + "}}");

		return getRegrid(toGrid);
	}

	
	/**
	 * Gets the regrid.
	 *
	 * @param toGrid the to grid
	 * @return the regrid
	 * @throws IllegalStateException the illegal state exception
	 */
	public GridImage2D<CoordinateType> getRegrid(final Grid2D<CoordinateType> toGrid) throws IllegalStateException {		
		// Check if it is an identical grid...
		// Add directly if it is...

		if(toGrid.equals(getGrid())) {
			if(isVerbose()) System.err.println(" Matching grids.");
			return this;
		}

		final int nx = (int) Math.ceil(sizeX() * getGrid().pixelSizeX() / toGrid.pixelSizeX());
		final int ny = (int) Math.ceil(sizeY() * getGrid().pixelSizeY() / toGrid.pixelSizeY());
		
		Unit sizeUnit = getPreferredGridUnit();
		
		if(isVerbose()) {
			System.err.println(" Regrid size: " + nx + "x" + ny);
			Vector2D resolution = toGrid.getResolution();
			System.err.println(" Resolution = " + (resolution.x() / sizeUnit.value()) + " x " + (resolution.y() / sizeUnit.value()) + " " + sizeUnit.name());
		}
		
		return getRegrid(toGrid, nx, ny);
	}
	
	/**
	 * Gets the regrid.
	 *
	 * @param toGrid the to grid
	 * @param nx the nx
	 * @param ny the ny
	 * @return the regrid
	 */
	@SuppressWarnings("unchecked")
	protected GridImage2D<CoordinateType> getRegrid(Grid2D<CoordinateType> toGrid, int nx, int ny) {	
		GridImage2D<CoordinateType> regrid = (GridImage2D<CoordinateType>) clone();
			
		regrid.setSize(nx, ny);
		regrid.setGrid(toGrid);
			
		regrid.resample(this);
		regrid.sanitize();
		
		return regrid;
	}
	

	/**
	 * Regrid.
	 *
	 * @param resolution the resolution
	 */
	public void regrid(double resolution) {		
		GridImage2D<CoordinateType> regrid = getRegrid(resolution);
		setImage(regrid);
		setGrid(regrid.getGrid());
		smoothing = regrid.smoothing.copy();
	}

	/**
	 * Regrid to.
	 *
	 * @param image the image
	 * @throws IllegalStateException the illegal state exception
	 */
	public void regridTo(final GridImage2D<CoordinateType> image) throws IllegalStateException {
		GridImage2D<CoordinateType> regrid = getRegrid(image.getGrid(), image.sizeX(), image.sizeY());

		Vector2D corner1 = new Vector2D();
		Vector2D corner2 = new Vector2D(image.sizeX() - 1.0, image.sizeY() - 1.0);
		image.toOffset(corner1);
		image.toOffset(corner2);
	
		regrid.crop(corner1.x(), corner1.y(), corner2.x(), corner2.y()); 
		
		image.setImage(regrid);
		
		image.smoothing = regrid.smoothing.copy();
		image.correctingFWHM = regrid.correctingFWHM;
		image.extFilterFWHM = regrid.extFilterFWHM;
	}

	/**
	 * Clean.
	 */
	public void clean() {
		GaussianPSF psf = getImageBeam();
		GaussianPSF replacementPSF = psf.copy();
		replacementPSF.scale(0.5);
		clean(psf.getBeam(getGrid()), 0.1, replacementPSF);
	}

	/**
	 * Clean.
	 *
	 * @param FWHM the fwhm
	 * @param gain the gain
	 * @param replacementFWHM the replacement fwhm
	 */
	public void clean(double FWHM, double gain, double replacementFWHM) {
		clean(GaussianPSF.getBeam(FWHM, getGrid()), gain, new GaussianPSF(replacementFWHM)); 
	}

	/**
	 * Clean.
	 *
	 * @param beam the beam
	 * @param gain the gain
	 * @param replacementBeam the replacement beam
	 * @return the int
	 */
	public int clean(double[][] beam, double gain, GaussianPSF replacementBeam) {
		return clean(this, beam, gain, replacementBeam);
	}

	/**
	 * Clean.
	 *
	 * @param image the search
	 * @param beam the beam
	 * @param gain the gain
	 * @param replacementBeam the replacement beam
	 * @return the int
	 */
	public int clean(GridImage2D<CoordinateType> image, double[][] beam, double gain, GaussianPSF replacementBeam) {
		if(isVerbose()) {
			Unit sizeUnit = getPreferredGridUnit();
			System.err.println("Deconvolving to " + replacementBeam.toString(sizeUnit) + " resolution.");
		}
		
		final int ic = beam.length / 2;
		final int jc = beam[0].length / 2;

		final double[][] clean = new double[sizeX()][sizeY()];

		// Normalize to beam to center
		final double norm = beam[ic][jc];
		for(int ib=beam.length; --ib >= 0; ) for(int jb=beam[0].length; --jb >= 0; ) beam[ib][jb] /= norm;
		
		// Peak to replacement beam integral conversion.
		final double beamInt = replacementBeam.getArea() / getGrid().getPixelArea();

		// Remove until there is an 70% chance that the peak is real
		final double critical = CumulativeNormalDistribution.inverseComplementAt(0.3 / (getImageBeam().getArea() / smoothing.getArea()));
		final int maxComponents = (int) Math.ceil(countBeams() / gain);			

		int components = 0;

		// Find the peak
		Index2D index = image.indexOfMaxDev();
		double peakValue = image.getValue(index.i(), index.j());
		double ave = Math.abs(peakValue);
		
		do {			    
			// Get the peak value	 
			final int i = index.i();
			final int j = index.j();
			final int i0 = i - ic;	// The map index where the patch would start on...
			final int j0 = j - jc;

			final int imin = Math.max(0, i0);
			final int jmin = Math.max(0, j0);
			final int imax = Math.min(i0 + beam.length, sizeX());
			final int jmax = Math.min(j0 + beam[0].length, sizeY());

			final double decrement = gain * getValue(i, j);
			final double searchDecrement = gain * peakValue;

			// Pole is the peak value times the beam integral to conserve flux	    
			clean[i][j] += decrement * beamInt; // est. intergal flux in beam

			for(int i1=imin; i1 < imax; i1++) for(int j1=jmin; j1 < jmax; j1++) if(isUnflagged(i1, j1)) {
				final double B = beam[i1 - i0][j1 - j0];
				decrement(i1, j1, B * decrement);
				if(image != this) image.decrement(i, j, B * searchDecrement);
			}

			components++;

			peakValue = image.getValue(i, j);         

			// The moving average value of the peak...
			ave *= Math.exp(-0.03);
			ave += 0.03 * Math.abs(peakValue);

			index = image.indexOfMaxDev();

		} while(Math.abs(peakValue) > critical && components < maxComponents);

	
		if(isVerbose()) System.err.println(" " + components + " components removed. (Last: " + Util.f2.format(peakValue) + "-sigma, Ave: " + Util.f2.format(ave) + "-sigma)   ");

		GridImage2D<?> cleanImage = (GridImage2D<?>) clone();
		cleanImage.setData(clean);
	
		// Add deconvolved components back to the residual noise...
		// TODO scaling of uncleaned to cleaned, and weight rescaling...
		addImage(cleanImage.getSmoothed(replacementBeam));
		
		resetSmoothing();

		if(isVerbose()) System.err.println();

		return components;
	}
	
	/**
	 * To index.
	 *
	 * @param offset the offset
	 */
	public final void toIndex(final Vector2D offset) { getGrid().toIndex(offset); }
	
	/**
	 * Offset to index.
	 *
	 * @param offset the offset
	 * @param index the index
	 */
	public final void offsetToIndex(final Vector2D offset, final Vector2D index) { getGrid().offsetToIndex(offset, index); }
	
	/**
	 * To offset.
	 *
	 * @param index the index
	 */
	public final void toOffset(final Vector2D index) { getGrid().toOffset(index); }
	
	/**
	 * Index to offset.
	 *
	 * @param index the index
	 * @param offset the offset
	 */
	public final void indexToOffset(final Vector2D index, final Vector2D offset) { getGrid().indexToOffset(index, offset); }

	/**
	 * Gets the index.
	 *
	 * @param offset the offset
	 * @param index the index
	 * @return the index
	 */
	public final void getIndex(final Vector2D offset, final Index2D index) {
		final double x = offset.x();
		final double y = offset.y();
		toIndex(offset);
		index.set((int) Math.round(offset.x()), (int) Math.round(offset.y()));
		offset.set(x, y);
	}
	
	/**
	 * Gets the offset.
	 *
	 * @param index the index
	 * @param offset the offset
	 * @return the offset
	 */
	public final void getOffset(final Index2D index, final Vector2D offset) {
		offset.set(index.i(), index.j());
		toOffset(offset);		
	}

	/**
	 * Gets the index.
	 *
	 * @param offset the offset
	 * @return the index
	 */
	public final Index2D getIndex(final Vector2D offset) {
		Index2D index = new Index2D();
		getIndex(offset, index);
		return index;
	}
	
	/**
	 * Gets the offset.
	 *
	 * @param index the index
	 * @return the offset
	 */
	public final Vector2D getOffset(final Index2D index) {
		Vector2D offset = new Vector2D();
		getOffset(index, offset);
		return offset;
	}
	
	
	/* (non-Javadoc)
	 * @see kovacs.util.data.Data2D#editHeader(nom.tam.util.Cursor)
	 */
	@Override
	public void editHeader(Header header, Cursor<String, HeaderCard> cursor) throws HeaderCardException, FitsException, IOException {
		super.editHeader(header, cursor);
		
		getGrid().editHeader(header, cursor);
		
		GaussianPSF psf = getImageBeam();
		Unit sizeUnit = getPreferredGridUnit();
		
		psf.editHeader(header, cursor);
		if(psf.isCircular()) cursor.add(new HeaderCard("RESOLUTN", psf.getMajorFWHM() / sizeUnit.value(), "{Deprecated} Effective image FWHM (" + sizeUnit.name() + ")."));	

		underlyingBeam.editHeader(header, cursor);
		smoothing.editHeader(header, cursor);
		if(smoothing.isCircular()) cursor.add(new HeaderCard("SMOOTH", smoothing.getMajorFWHM() / sizeUnit.value(), "{Deprecated} FWHM (" + sizeUnit.name() + ") of the smoothing applied."));	

		// TODO convert extended filter and corrections to proper Gaussian beams...
		if(!Double.isNaN(extFilterFWHM)) {
			GaussianPSF filterBeam = new GaussianPSF("X", extFilterFWHM);
			filterBeam.setName("EXTENDED STRUCURE FILTER");
			filterBeam.editHeader(header, cursor);
		}
		
		if(!Double.isNaN(correctingFWHM)) {
			GaussianPSF correctionBeam = new GaussianPSF("C", correctingFWHM);
			correctionBeam.setName("PEAK CORRECTED");
			correctionBeam.editHeader(header, cursor);
		}
			
		cursor.add(new HeaderCard("SMTHRMS", true, "Is the Noise (RMS) image smoothed?"));
		
	}
	

	/* (non-Javadoc)
	 * @see jnum.data.Data2D#parseHeader(nom.tam.fits.Header)
	 */
	@Override
	protected final void parseHeader(Header header) throws Exception {
		parseHeader(header, "");
	}

	/**
	 * Parses the grid.
	 *
	 * @param header the header
	 * @param alt the alt
	 * @throws HeaderCardException the header card exception
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 */
	private void parseGrid(Header header, String alt) throws HeaderCardException, InstantiationException, IllegalAccessException {
		setGrid((Grid2D<CoordinateType>) Grid2D.fromHeader(header, alt));
	}

	/**
	 * Parses the header.
	 *
	 * @param header the header
	 * @param alt the alt
	 * @throws Exception the exception
	 */
	// TODO elliptical filter and correcting beams...
	protected void parseHeader(Header header, String alt) throws Exception {		
		super.parseHeader(header);
				
		parseGrid(header, alt);
		
		double defaultUnit = getPreferredGridUnit().value();
		
		// TODO parse header via the PSF objects...
		
		// Use old CORRETN or new CBMAJ/CBMIN
		if(header.containsKey("CBMAJ")) {
			GaussianPSF correctingBeam = new GaussianPSF("C");
			correctingBeam.parseHeader(header);
			correctingFWHM = correctingBeam.getCircularEquivalentFWHM();		
		}
		else correctingFWHM = FitsExtras.getCommentedUnitValue(header, "CORRECTN", Double.NaN, defaultUnit);
		
		// Use old SMOOTH or new SBMAJ/SBMIN
		if(header.containsKey("SBMAJ")) smoothing.parseHeader(header);	
		else {
			double smoothFWHM = FitsExtras.getCommentedUnitValue(header, "SMOOTH", Double.NaN, defaultUnit);
			smoothing.set(smoothFWHM);
		}
		
		double pixelSmoothing = Math.sqrt(getGrid().getPixelArea()) / fwhm2size;
		smoothing.encompass(new GaussianPSF(pixelSmoothing));
		
		// Use old EXTFILTR or new XBMAJ, XBMIN
		if(header.containsKey("XBMAJ")) {
			GaussianPSF extFilterBeam = new GaussianPSF("X");
			extFilterBeam.parseHeader(header);
			extFilterFWHM = extFilterBeam.getCircularEquivalentFWHM();
		}
		extFilterFWHM = FitsExtras.getCommentedUnitValue(header, "EXTFLTR", Double.NaN, defaultUnit);
		
		// Use new IBMAJ/IBMIN if available
		// Otherwise calculate it based on BMAJ, BMIN
		// Else, use old BEAM, or calculate based on old RESOLUTN
		if(header.containsKey("IBMAJ")) underlyingBeam.parseHeader(header);
		else if(header.containsKey("BEAM")) 
			underlyingBeam.set(FitsExtras.getCommentedUnitValue(header, "BEAM", Double.NaN, defaultUnit));
		else if(header.containsKey("BMAJ")) {
			underlyingBeam.parseHeader(header);
			underlyingBeam.deconvolveWith(smoothing);
		}
		else if(header.containsKey("RESOLUTN")) {
			double resolution = FitsExtras.getCommentedUnitValue(header, "RESOLUTN", Double.NaN, defaultUnit);
			underlyingBeam.set(resolution > smoothing.getMajorFWHM() ? Math.sqrt(resolution * resolution - smoothing.getMajorFWHM() * smoothing.getMinorFWHM()) : 0.0);
		}
		else underlyingBeam.set(0.0);
		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {		
		Grid2D<?> grid = getGrid();
		
		Unit sizeUnit = getPreferredGridUnit();
		
		String info =
			"  Map Size: " + sizeX() + " x " + sizeY() + " pixels. (" 
			+ Util.f1.format(sizeX() * grid.pixelSizeX() / sizeUnit.value()) + " x " + Util.f1.format(sizeY() * grid.pixelSizeY() 
					/ sizeUnit.value()) + " " + sizeUnit.name() + ")." + "\n"
			+ grid.toString(sizeUnit)
			+ "  Instrument PSF: " + getUnderlyingBeam().toString(sizeUnit) + " FWHM.\n"
			+ "  Applied Smoothing: " + smoothing.toString(sizeUnit) + " FWHM (includes pixelization).\n"
			+ "  Image Resolution: " + getImageBeam().toString(sizeUnit) + " FWHM (includes smoothing).\n";
		
		
		return info;
	}
	
	/**
	 * Flag.
	 *
	 * @param region the region
	 */
	public void flag(Region<CoordinateType> region) { flag(region, 1); }

	/**
	 * Flag.
	 *
	 * @param region the region
	 * @param pattern the pattern
	 */
	public void flag(Region<CoordinateType> region, int pattern) {
		final IndexBounds2D bounds = region.getBounds(this);
		for(int i=bounds.fromi; i<=bounds.toi; i++) for(int j=bounds.fromj; j<=bounds.toj; j++) 
			if(region.isInside(getGrid(), i, j)) flag(i, j, pattern);
	}

	/**
	 * Unflag.
	 *
	 * @param region the region
	 * @param pattern the pattern
	 */
	public void unflag(Region<CoordinateType> region, int pattern) {
		final IndexBounds2D bounds = region.getBounds(this);
		for(int i=bounds.fromi; i<=bounds.toi; i++) for(int j=bounds.fromj; j<=bounds.toj; j++) 
			if(region.isInside(getGrid(), i, j)) unflag(i, j, pattern);
	}

	/* (non-Javadoc)
	 * @see kovacs.util.data.Data2D#addBaseUnits()
	 */
	@Override
	public void addBaseUnits() {
		super.addBaseUnits();
		addBaseUnit(beamArea, "beam, BEAM, bm, BM");
		addBaseUnit(pixelAreaUnit, "pixel, pixels, PIXEL, PIXELS, plx");
	}
	
	/**
	 * Gets the level.
	 *
	 * @param region the region
	 * @return the level
	 */
	public double getLevel(Region<CoordinateType> region) {
		final IndexBounds2D bounds = region.getBounds(this);
		double sum = 0.0, sumw = 0.0;
		for(int i=bounds.fromi; i<=bounds.toi; i++) for(int j=bounds.fromj; j<=bounds.toj; j++)
			if(isUnflagged(i, j)) if(region.isInside(getGrid(), i, j)) {
				final double w = getWeight(i, j);
				sum += w * getValue(i, j);
				sumw += w;
			}
		return sum / sumw;			
	}
	

	/**
	 * Gets the integral.
	 *
	 * @param region the region
	 * @return the integral
	 */
	public double getIntegral(Region<CoordinateType> region) {
		final IndexBounds2D bounds = region.getBounds(this);
		double sum = 0.0;
		for(int i=bounds.fromi; i<=bounds.toi; i++) for(int j=bounds.fromj; j<=bounds.toj; j++)
			if(isUnflagged(i, j)) if(region.isInside(getGrid(), i, j)) sum += getValue(i, j);	
		return sum;			
	}

	/**
	 * Gets the asymmetry.
	 *
	 * @param region the region
	 * @param angle the angle
	 * @param minr the minr
	 * @param maxr the maxr
	 * @return the asymmetry
	 */
	public Asymmetry2D getAsymmetry(CircularRegion<CoordinateType> region, double angle, double minr, double maxr) {
		if(region instanceof GaussianSource) ((GaussianSource<CoordinateType>) region).centroid(this);
		Asymmetry2D asym = new Asymmetry2D();
		asym.setX(region.getAsymmetry(this, angle, minr, maxr));
		asym.setY(region.getAsymmetry(this, angle + 90.0 * Unit.deg, minr, maxr));
		return asym;
	}
	
	/**
	 * The Class BeamArea.
	 */
	private class BeamArea extends Unit {
		
		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = -4496757466224266905L;

		/**
		 * Instantiates a new beam area.
		 */
		private BeamArea() { 
			super("beam", Double.NaN);
		}
		
		/* (non-Javadoc)
		 * @see kovacs.util.Unit#value()
		 */
		@Override
		public double value() { return getImageBeamArea(); }
	}
	
	/**
	 * The Class PixelArea.
	 */
	private class PixelAreaUnit extends Unit {
		
		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = -2483542207572304222L;

		/**
		 * Instantiates a new pixel area.
		 */
		private PixelAreaUnit() { super("pixel", Double.NaN); }
		
		/* (non-Javadoc)
		 * @see kovacs.util.Unit#value()
		 */
		@Override
		public double value() { return getPixelArea(); }
	}
	
	/**
	 * Coordinate system.
	 *
	 * @return the class<? extends coordinate2 d>
	 */
	public Class<? extends Coordinate2D> getCoordinateClass() { return getReference().getClass(); }
	
	/* (non-Javadoc)
	 * @see kovacs.util.data.Data2D#getFormattedEntry(java.lang.String, java.lang.String)
	 */
	@Override
	public String getFormattedEntry(String name, String formatSpec) {
		if(name.equals("beams")) return TableFormatter.getNumberFormat(formatSpec).format(countBeams());
		else return super.getFormattedEntry(name, formatSpec);
	}

	/**
	 * From header.
	 *
	 * @param header the header
	 * @return the grid image2 d
	 * @throws Exception the exception
	 */
	public static GridImage2D<?> fromHeader(Header header) throws Exception {
		return fromHeader(header, "");
	}
	
	/**
	 * From header.
	 *
	 * @param header the header
	 * @param alt the alt
	 * @return the grid image2 d
	 * @throws Exception the exception
	 */
	public static GridImage2D<?> fromHeader(Header header, String alt) throws Exception {	
		GridImage2D<?> image = new GridImage2D<Coordinate2D>();
		image.parseHeader(header, alt);
		return image;
	}
	
	/**
	 * From hdu.
	 *
	 * @param hdu the hdu
	 * @return the grid image2 d
	 * @throws Exception the exception
	 */
	public static GridImage2D<?> fromHDU(BasicHDU<?> hdu) throws Exception {
		return fromHDU(hdu, "");
	}
	
	/**
	 * From hdu.
	 *
	 * @param hdu the hdu
	 * @param alt the alt
	 * @return the grid image2 d
	 * @throws Exception the exception
	 */
	public static GridImage2D<?> fromHDU(BasicHDU<?> hdu, String alt) throws Exception {	
		GridImage2D<?> image = fromHeader(hdu.getHeader(), alt);
		image.readData(hdu);
		return image;
	}
	
	
	/** The Constant rawUnit. */
	private final static Unit rawUnit = new Unit("raw", 1.0, false);
	
}
