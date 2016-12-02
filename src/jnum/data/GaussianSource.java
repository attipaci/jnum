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
// Copyright (c) 2009 Attila Kovacs 

package jnum.data;

import java.text.ParseException;
import java.util.Collection;
import java.util.StringTokenizer;

import jnum.Constant;
import jnum.ExtraMath;
import jnum.Unit;
import jnum.Util;
import jnum.astro.CoordinateEpoch;
import jnum.astro.Precessing;
import jnum.math.Coordinate2D;
import jnum.math.Vector2D;
import jnum.util.DataTable;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCardException;




// TODO: Auto-generated Javadoc
/**
 * The Class GaussianSource.
 *
 * @param <CoordinateType> the generic type
 */
public class GaussianSource<CoordinateType extends Coordinate2D> extends CircularRegion<CoordinateType> {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 786127030179333921L;

	/** The peak. */
	private DataPoint peak;
	
	/** The is corrected. */
	private boolean isCorrected = false;
	
	/**
	 * Instantiates a new gaussian source.
	 */
	public GaussianSource() { }

	/**
	 * Instantiates a new gaussian source.
	 *
	 * @param line the line
	 * @param format the format
	 * @param forImage the for image
	 * @throws ParseException the parse exception
	 */
	public GaussianSource(String line, int format, GridImage2D<CoordinateType> forImage) throws ParseException {
		super(line, format, forImage);
	}
	
	/**
	 * Instantiates a new gaussian source.
	 *
	 * @param map the map
	 * @param offset the offset
	 * @param r the r
	 */
	public GaussianSource(GridImage2D<CoordinateType> map, Vector2D offset, double r) {
		super(map, offset, r);
	}
	
	/**
	 * Instantiates a new gaussian source.
	 *
	 * @param coords the coords
	 * @param r the r
	 */
	public GaussianSource(CoordinateType coords, double r) {
		super(coords, r);
	}
	
	/* (non-Javadoc)
	 * @see jnum.data.CircularRegion#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = super.hashCode() ^ (isCorrected ? 1 : 0);
		if(peak != null) hash ^= peak.hashCode();
		return hash;
	}
	
	/* (non-Javadoc)
	 * @see jnum.data.CircularRegion#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if(o == this) return true;
		if(!super.equals(o)) return false;
		GaussianSource<?> s = (GaussianSource<?>) o;
		if(isCorrected != s.isCorrected) return false;
		if(!Util.equals(peak, s.peak)) return false;
		return true;
	}
	
	/**
	 * Gets the peak.
	 *
	 * @return the peak
	 */
	public DataPoint getPeak() { return peak; }
	
	/**
	 * Sets the peak.
	 *
	 * @param value the new peak
	 */
	public void setPeak(DataPoint value) { peak = value; }
	
	/**
	 * Sets the peak.
	 *
	 * @param value the new peak
	 */
	public void setPeak(double value) { 
		if(peak == null) peak = new DataPoint();
		else peak.setWeight(0.0);
		peak.setValue(value);
	}
	
	/**
	 * Checks if is corrected.
	 *
	 * @return true, if is corrected
	 */
	public boolean isCorrected() { return isCorrected; }
	
	/**
	 * Sets the corrected.
	 *
	 * @param value the new corrected
	 */
	public void setCorrected(boolean value) { isCorrected = value; }
	
	/**
	 * Gets the fwhm.
	 *
	 * @return the fwhm
	 */
	public DataPoint getFWHM() { return getRadius(); }
	
	/**
	 * Sets the fwhm.
	 *
	 * @param value the new fwhm
	 */
	public void setFWHM(double value) { setRadius(value); }
	
	/**
	 * Sets the fwhm.
	 *
	 * @param value the new fwhm
	 */
	public void setFWHM(DataPoint value) { setRadius(value); }
	
	/**
	 * Sets the peak pixel.
	 *
	 * @param map the new peak pixel
	 */
	public void setPeakPixel(GridImage2D<CoordinateType> map) {
		Index2D index = map instanceof GridMap2D ? ((GridMap2D<?>) map).getS2NImage().indexOfMax() : map.indexOfMax();
		@SuppressWarnings("unchecked")
		CoordinateType coords = (CoordinateType) map.getReference().clone();
		map.getGrid().getCoords(new Vector2D(index.i(), index.j()), coords);
		setID(map.getName());
		setCoordinates(coords);
		setRadius(new DataPoint(map.getImageBeam().getCircularEquivalentFWHM(), Math.sqrt(map.getPixelArea())));	
	}
	
	/**
	 * Sets the peak.
	 *
	 * @param map the new peak
	 */
	public void setPeak(GridImage2D<CoordinateType> map) {
		setPeakPixel(map);
		finetunePeak(map);
	}
	
	/**
	 * Sets the peak centroid.
	 *
	 * @param map the new peak centroid
	 */
	public void setPeakCentroid(GridImage2D<CoordinateType> map) {
		setPeak(map);
		centroid(map);
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.data.CircularRegion#finetunePeak(kovacs.util.data.GridImage)
	 */
	@Override
	public DataPoint finetunePeak(GridImage2D<CoordinateType> image) {
		Data2D.InterpolatorData ipolData = new Data2D.InterpolatorData();
		peak = super.finetunePeak(image);
		Vector2D centerIndex = getIndex(image.getGrid());

		if(peak == null) {
			peak.setValue(image.valueAtIndex(centerIndex, ipolData));
			if(image instanceof GridMap2D)
				peak.setRMS(((GridMap2D<?>) image).getRMSImage().valueAtIndex(centerIndex, ipolData));
			else peak.setWeight(0.0);
		}
		return peak;
	}
	
	/**
	 * Scale.
	 *
	 * @param factor the factor
	 */
	public void scale(double factor) {
		peak.scale(factor);		
	}
	
	/**
	 * Gets the chi2.
	 *
	 * @param map the map
	 * @param chi2 the chi2
	 * @param level the level
	 * @return the chi2
	 */
	public void getChi2(GridImage2D<CoordinateType> map, WeightedPoint chi2, double level) {
		chi2.noData();
		IndexBounds2D bounds = getBounds(map);
		Vector2D centerIndex = getIndex(map.getGrid());
		final Vector2D resolution = map.getResolution();
		final double sigmaX = getRadius().value() / Constant.sigmasInFWHM / resolution.x();
		final double sigmaY = getRadius().value() / Constant.sigmasInFWHM / resolution.y();
		final double Ax = -0.5 / (sigmaX*sigmaX);
		final double Ay = -0.5 / (sigmaY*sigmaY);

		for(int i=bounds.fromi; i<=bounds.toi; i++) for(int j=bounds.fromj; j<=bounds.toj; j++) if(map.isUnflagged(i, j)) {
			final double di = i-centerIndex.x();
			final double dj = j-centerIndex.y();
			final double dev = (map.getValue(i, j) - level - peak.value() * Math.exp(Ax*di*di + Ay*dj*dj)) / map.getRMS(i, j);
			chi2.add(dev * dev);
			chi2.addWeight(1.0);
		}
	}
	
	/**
	 * Adds the gaussian.
	 *
	 * @param image the image
	 * @param FWHM the fwhm
	 * @param scaling the scaling
	 */
	public void addGaussian(GridImage2D<CoordinateType> image, double FWHM, double scaling) {
		IndexBounds2D bounds = getBounds(image, 3.0 * FWHM);
			
		Vector2D centerIndex = getIndex(image.getGrid());
				
		final Vector2D resolution = image.getResolution();
		final double sigmaX = FWHM / Constant.sigmasInFWHM / resolution.x();
		final double sigmaY = FWHM / Constant.sigmasInFWHM / resolution.y();
		final double Ax = -0.5 / (sigmaX*sigmaX);
		final double Ay = -0.5 / (sigmaY*sigmaY);
	
		for(int i=bounds.fromi; i<=bounds.toi; i++) for(int j=bounds.fromj; j<=bounds.toj; j++) {
			final double di = i-centerIndex.x();
			final double dj = j-centerIndex.y();
			image.increment(i, j, scaling * peak.value() * Math.exp(Ax*di*di + Ay*dj*dj));
		}
		
	}
	
	/**
	 * Adds the.
	 *
	 * @param image the image
	 */
	public void add(GridImage2D<CoordinateType> image) { add(image, null); }
	
	/**
	 * Adds the.
	 *
	 * @param image the image
	 * @param others the others
	 */
	public void add(GridImage2D<CoordinateType> image, Collection<Region<CoordinateType>> others) { add(image, getRadius().value(), 1.0, others); }
	
	/**
	 * Adds the point.
	 *
	 * @param image the image
	 */
	public void addPoint(GridImage2D<CoordinateType> image) { addPoint(image, null); }
	
	/**
	 * Adds the point.
	 *
	 * @param image the image
	 * @param others the others
	 */
	public void addPoint(GridImage2D<CoordinateType> image, Collection<Region<CoordinateType>> others) { add(image, image.getImageBeam().getCircularEquivalentFWHM(), 1.0, others); }	
	
	/**
	 * Subtract.
	 *
	 * @param image the image
	 */
	public void subtract(GridImage2D<CoordinateType> image) { subtract(image, null); }
	
	/**
	 * Subtract.
	 *
	 * @param image the image
	 * @param others the others
	 */
	public void subtract(GridImage2D<CoordinateType> image, Collection<Region<CoordinateType>> others) { add(image, getRadius().value(), -1.0, others); }
	
	/**
	 * Subtract point.
	 *
	 * @param image the image
	 */
	public void subtractPoint(GridImage2D<CoordinateType> image) { subtractPoint(image, null); }
	
	/**
	 * Subtract point.
	 *
	 * @param image the image
	 * @param others the others
	 */
	public void subtractPoint(GridImage2D<CoordinateType> image, Collection<Region<CoordinateType>> others) { add(image, image.getImageBeam().getCircularEquivalentFWHM(), -1.0, others); }
	
	
	/**
	 * Adds the.
	 *
	 * @param image the image
	 * @param FWHM the fwhm
	 * @param scaling the scaling
	 */
	public void add(GridImage2D<CoordinateType> image, final double FWHM, final double scaling) {
		add(image, FWHM, scaling, null);
	}
	
	/**
	 * Adds the.
	 *
	 * @param image the image
	 * @param FWHM the fwhm
	 * @param scaling the scaling
	 * @param others the others
	 */
	public void add(GridImage2D<CoordinateType> image, final double FWHM, final double scaling, final Collection<Region<CoordinateType>> others) {
		// Remove the Gaussian main beam...
		addGaussian(image, ExtraMath.hypot(FWHM, image.getSmoothing().getCircularEquivalentFWHM()), scaling);
		
		// If an LSS filter was used, also correct for the Gaussian bowl around the source...
		if(Double.isNaN(image.getExtFilterFWHM())) return;
		
		final double filterFWHM = ExtraMath.hypot(FWHM, image.getExtFilterFWHM());			

		// Correct for filtering.
		double filterFraction = 1.0 - 1.0 / image.getFilterCorrectionFactor(FWHM);
		
		// Consider that only the tip of the source might escape the filter...	
		if(image instanceof GridMap2D) {
			GridMap2D<?> map = (GridMap2D<?>) image;
			filterFraction *= Double.isNaN(map.filterBlanking) ? 1.0 : Math.min(1.0, map.filterBlanking / peak.significance());	
		}

		// Add the filter bowl to the image
		addGaussian(image, filterFWHM, -scaling * filterFraction);

		// Now adjust prior detections for the bias caused by this source's filtering...
		final Vector2D resolution = image.getResolution();
		final double sigmai = filterFWHM / Constant.sigmasInFWHM / resolution.x();
		final double sigmaj = filterFWHM / Constant.sigmasInFWHM / resolution.y();
		final double Ai = -0.5 / (sigmai*sigmai);
		final double Aj = -0.5 / (sigmaj*sigmaj);	

		double filterPeak = -filterFraction * scaling * peak.value();
		
		Vector2D centerIndex = getIndex(image.getGrid());

		
		// Adjust prior detections.for the filtering around this one.
		if(others != null) for(Region<CoordinateType> region : others) if(region instanceof GaussianSource) if(region != this) {
			GaussianSource<CoordinateType> source = (GaussianSource<CoordinateType>) region;
			Vector2D sourceIndex = source.getIndex(image.getGrid());
			final double di = sourceIndex.x() - centerIndex.x();
			final double dj = sourceIndex.y() - centerIndex.y();
			
			if(!Double.isNaN(image.getExtFilterFWHM()))
				source.peak.subtract(filterPeak * Math.exp(Ai*di*di + Aj*dj*dj));
		}
		
	}
	
	/**
	 * Gets the correction factor.
	 *
	 * @param map the map
	 * @param FWHM the fwhm
	 * @return the correction factor
	 */
	public double getCorrectionFactor(GridMap2D<CoordinateType> map, double FWHM) {	
		double correction = 1.0;	
		
		// Correct for filtering.
		// Consider that only the tip of the source might escape the filter...
		if(!Double.isNaN(map.getExtFilterFWHM())) {
			double filterFraction = Double.isNaN(map.filterBlanking) ? 1.0 : Math.min(1.0, map.filterBlanking / peak.significance());
			double filtering = 1.0 - 1.0 / map.getFilterCorrectionFactor(FWHM);
			correction *= 1.0 / (1.0 - filtering * filterFraction);
		}
		
		return correction;
	}
	
	
	/**
	 * Correct.
	 *
	 * @param map the map
	 * @param FWHM the fwhm
	 */
	public void correct(GridMap2D<CoordinateType> map, double FWHM) {	
		if(isCorrected) throw new IllegalStateException("Source is already corrected.");
		double correction = getCorrectionFactor(map, FWHM);
		peak.scale(correction);
		isCorrected = true;
	}
	

	/**
	 * Uncorrect.
	 *
	 * @param map the map
	 * @param FWHM the fwhm
	 */
	public void uncorrect(GridMap2D<CoordinateType> map, double FWHM) {
		if(!isCorrected) throw new IllegalStateException("Source is already uncorrected.");
		double correction = getCorrectionFactor(map, FWHM);
		peak.scale(1.0 / correction);
		isCorrected = false;
	}
	
	
	

	
	/* (non-Javadoc)
	 * @see kovacs.util.data.CircularRegion#parse(java.lang.String, int, kovacs.util.data.GridImage)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void parse(String line, int format, GridImage2D<CoordinateType> forImage) throws ParseException {
		if(line == null) return;
		if(line.length() == 0) return;
		if(line.charAt(0) == '#' || line.charAt(0) == '!') return;

		StringTokenizer tokens = new StringTokenizer(line);

		if(tokens.countTokens() < 5) return;

		setID(tokens.nextToken());

		CoordinateType coords = (CoordinateType) forImage.getReference().clone();
		setCoordinates(coords);
		if(coords instanceof Precessing) {
			coords.parse(tokens.nextToken() + " " + tokens.nextToken() + " " + tokens.nextToken());
			CoordinateEpoch epoch = ((Precessing) forImage.getReference()).getEpoch();
			((Precessing) coords).precess(epoch);
		}
		else coords.parse(tokens.nextToken() + " " + tokens.nextToken());
		
		setRadius(new DataPoint(Double.parseDouble(tokens.nextToken()) * Unit.arcsec, 0.0));
		if(tokens.hasMoreTokens()) peak = new DataPoint(Double.parseDouble(tokens.nextToken()), 0.0);
		
		String nextArg = tokens.hasMoreTokens() ? tokens.nextToken() : null;
		if(nextArg != null) {
			if(nextArg.equals("+-")) nextArg = tokens.nextToken();
			try { 
				peak.setRMS(Double.parseDouble(nextArg));
				nextArg = tokens.hasMoreTokens() ? tokens.nextToken() : null;
			}
			catch(NumberFormatException e) {}
		}

		if(nextArg == null) forImage.setUnit("uno");
		else {
			forImage.setUnit(nextArg);
			peak.scale(forImage.getUnit().value());
		}
			
		while(tokens.hasMoreTokens()) addComment(tokens.nextToken() + " ");
		setComment(getComment().trim());
		
	}

	/* (non-Javadoc)
	 * @see kovacs.util.data.CircularRegion#toCrushString(kovacs.util.data.GridImage)
	 */
	@Override
	public String toCrushString(GridImage2D<CoordinateType> image) {
		return getID() + "\t" + super.toCrushString(image) + "  " + DataPoint.toString(peak, image.getUnit());
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.data.Region#getComment()
	 */
	@Override
	public String getComment() {
		return "s/n=" + Util.f2.format(peak.significance()) + " " + super.getComment();
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.data.CircularRegion#parseCrush(java.lang.String, kovacs.util.data.GridImage)
	 */
	@Override
	public StringTokenizer parseCrush(String line, GridImage2D<CoordinateType> forImage) {
		StringTokenizer tokens = super.parseCrush(line, forImage);
		
		peak.setValue(Double.parseDouble(tokens.nextToken()));
		String next = tokens.nextToken();
		if(next.equals("+-")) {
			peak.setRMS(Double.parseDouble(tokens.nextToken()));
			next = tokens.nextToken();
		}
		
		forImage.setUnit(next);
		peak.scale(forImage.getUnit().value());
		
		return tokens;
	}
	
	
	/**
	 * Centroid.
	 *
	 * @param map the map
	 */
	public void centroid(GridImage2D<CoordinateType> map) {	
		IndexBounds2D bounds = getBounds(map, 2.0 * map.getImageBeam().getCircularEquivalentFWHM());
		Vector2D index = new Vector2D();
		double sumw = 0.0;
		
		for(int i=bounds.fromi; i<=bounds.toi; i++) for(int j=bounds.fromj; j<=bounds.toj; j++) if(map.isUnflagged(i, j)) {
			double w = Math.abs(map.getS2N(i,j));
			index.addX(w * i);
			index.addY(w * j);
			sumw += w;
		}
		index.scale(1.0/sumw);
		
		moveTo(map, index);		
	}
	
	
	/**
	 * Spread.
	 *
	 * @param map the map
	 * @return the double
	 */
	public double getSpread(GridImage2D<CoordinateType> map) {	
		WeightedPoint I = getAdaptiveIntegral(map);	
		return Math.sqrt(I.value() / (Constant.twoPi * peak.value()) * map.getPixelArea());
	}
	
	/**
	 * Measure shape.
	 *
	 * @param map the map
	 */
	public void measureShape(GridImage2D<CoordinateType> map) {	
		getRadius().setValue(getSpread(map) * Constant.sigmasInFWHM);
		getRadius().setRMS(getRadius().value() / peak.significance());
		
		// the FWHM scales inversely with sqrt(peak), so sigma(FWHM)^2 ~ 0.5 sigma(peak)^2
		// but FWHM0 = sqrt(FWHM^2 - smooth^2)
		// so sigma(FWHM0)^2 ~ (0.5 / FWHM0 * 2 FWHM)^2  sigma(FWHM)^2
		//                   ~ F2 / (F^2 - S^2) * sigmaF^2
		//                   ~ 1 + (S^2 / F0^2) * sigmaF^2
		double SF0 = Math.min(1.0, map.getUnderlyingBeam().getCircularEquivalentFWHM() / getRadius().value());
		getRadius().scaleWeight(2.0 / (1.0 + SF0 * SF0));
	}
	
	// Formula from Kovacs et al. (2006)
	/**
	 * Sets the search radius.
	 *
	 * @param image the image
	 * @param pointingRMS the pointing rms
	 */
	public void setSearchRadius(GridImage2D<CoordinateType> image, double pointingRMS) {
		double beamSigma = image.getUnderlyingBeam().getCircularEquivalentFWHM() / Constant.sigmasInFWHM;
		setRadius(Math.sqrt(4.0 * pointingRMS * pointingRMS - 2.0 * beamSigma * beamSigma * Math.log(1.0 - 2.0 / peak.significance())));
	}
	
	/**
	 * Gets the data.
	 *
	 * @param map the map
	 * @param sizeUnit the size unit
	 * @return the data
	 */
	public DataTable getData(GridImage2D<CoordinateType> map, Unit sizeUnit) {
		DataTable data = new DataTable();
		
		double mapUnitValue = map.getUnit().value();
		String mapUnitName = map.getUnit().name();
			
		data.new Entry("peak", peak.value() / mapUnitValue, mapUnitName);
		data.new Entry("dpeak", peak.rms() / mapUnitValue, mapUnitName);
		data.new Entry("peakS2N", peak.significance(), "");
		
		DataPoint F = new DataPoint(getAdaptiveIntegral(map));
		F.scale(map.getPixelArea() / map.getImageBeamArea());
		
		data.new Entry("int", F.value() / mapUnitValue, mapUnitName);
		data.new Entry("dint", F.rms() / mapUnitValue, mapUnitName);
		data.new Entry("intS2N", F.significance(), "");
		
		data.new Entry("FWHM", getRadius().value() / sizeUnit.value(), sizeUnit.name());
		data.new Entry("dFWHM", getRadius().rms() / sizeUnit.value(), sizeUnit.name());
		
		return data;
	}
	
	/**
	 * Pointing info.
	 *
	 * @param map the map
	 * @param sizeUnit the size unit
	 * @return the string
	 */
	public String pointingInfo(GridImage2D<CoordinateType> map, Unit sizeUnit) {

		double beamScaling = map.getUnderlyingBeam().getArea() / map.getImageBeamArea();
		
		peak.scale(beamScaling);
		
		StringBuffer info = new StringBuffer();
		//info.append("  [" + getID() + "]\n");
		info.append("  Peak: " + DataPoint.toString(peak, map.getUnit())
			+ " (S/N ~ " + Util.f1.format(peak.significance()) + ")\n");
	
		peak.scale(1.0 / beamScaling);
		
		DataPoint F = new DataPoint(getAdaptiveIntegral(map));
		F.scale(map.getPixelArea() / map.getImageBeamArea());
		F.scale(1.0 / map.getUnit().value());
		
		info.append("  Int.: " + F.toString() + "\n");
		
		info.append("  FWHM: " + Util.f1.format(getRadius().value() / sizeUnit.value()) 
				+ (getRadius().weight() > 0.0 ? " +- " + Util.f1.format(getRadius().rms() / sizeUnit.value()) : "")
				+ " " + sizeUnit.name());
		
		
		
		return new String(info);
	}


    @Override
    public void editHeader(Header header, GridImage2D<CoordinateType> map, Unit sizeUnit) throws HeaderCardException {
        super.editHeader(header, map, sizeUnit);
        
        double beamScaling = map.getUnderlyingBeam().getArea() / map.getImageBeamArea();
        
        peak.scale(beamScaling);
        
        header.addValue("SRCPEAK", peak.value() / map.getUnit().value(), "(" + map.getUnit().name() + ") source peak flux.");
        header.addValue("SRCPKERR", peak.rms() / map.getUnit().value(), "(" + map.getUnit().name() + ") peak flux error.");

        peak.scale(1.0 / beamScaling);
        
        DataPoint F = new DataPoint(getAdaptiveIntegral(map));
        F.scale(map.getPixelArea() / map.getImageBeamArea());
        F.scale(1.0 / map.getUnit().value());
        
        header.addValue("SRCINT", F.value(), "Source integrated flux.");
        header.addValue("SRCIERR", F.rms(), "Integrated flux error.");
        
        header.addValue("SRCFWHM", getRadius().value() / sizeUnit.value(), "(" + sizeUnit.name() + ") source FWHM.");
        if(getRadius().weight() > 0.0) {
            header.addValue("SRCWERR", getRadius().rms() / sizeUnit.value(), "(" + sizeUnit.name() + ") FWHM error.");
        }
        
    }
    
	
}
