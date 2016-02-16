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
import java.util.Arrays;

import jnum.ExtraMath;
import jnum.Unit;
import jnum.Util;
import jnum.math.Coordinate2D;
import jnum.math.Vector2D;
import jnum.text.TableFormatter;
import jnum.util.HashCode;
import nom.tam.fits.BasicHDU;
import nom.tam.fits.Fits;
import nom.tam.fits.FitsException;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;
import nom.tam.fits.ImageHDU;
import nom.tam.util.Cursor;

// TODO: Auto-generated Javadoc
/**
 * The Class GridMap.
 *
 * @param <CoordinateType> the generic type
 */
public class GridMap2D<CoordinateType extends Coordinate2D> extends GridImage2D<CoordinateType> implements Noise2D, Timed2D {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2331308850176820380L;

	/** The count. */
	private double[][] weight, count;
	
	/** The weight factor. */
	private double weightFactor = 1.0;
	
	/** The filter blanking. */
	public double filterBlanking = Double.NaN;
	
	/** The clipping s2 n. */
	private double clippingS2N = Double.NaN;
	
	/**
	 * Instantiates a new grid map.
	 */
	public GridMap2D() { 
		setContentType("Signal");
	}
	
	/* (non-Javadoc)
	 * @see kovacs.data.GridImage#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = super.hashCode() ^ HashCode.get(weightFactor) ^ HashCode.get(filterBlanking) ^ HashCode.get(clippingS2N);
		if(weight != null) HashCode.sampleFrom(weight);
		if(count != null) HashCode.sampleFrom(count);
		return hash;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == this) return true;
		if(!(o instanceof GridMap2D)) return false;
		if(!super.equals(o)) return false;
		GridMap2D<?> map = (GridMap2D<?>) o;
		if(Double.compare(clippingS2N, map.clippingS2N) != 0) return false;
		if(Double.compare(filterBlanking, map.filterBlanking) != 0) return false;
		if(Double.compare(weightFactor, map.weightFactor) != 0) return false;
		if(!Arrays.equals(count, map.count)) return false;
		if(!Arrays.equals(weight, map.weight)) return false;
		return true;
	}
	
	/**
	 * Instantiates a new grid map.
	 *
	 * @param fileName the file name
	 * @throws Exception the exception
	 */
	public GridMap2D(String fileName) throws Exception { 
		super(fileName);		
	}

	/**
	 * Instantiates a new grid map.
	 *
	 * @param i the i
	 * @param j the j
	 */
	public GridMap2D(int i, int j) { 
		super(i, j);
	}
	
	/**
	 * Gets the weight scale.
	 *
	 * @return the weight scale
	 */
	public double getWeightScale() { return weightFactor; }
	
	/**
	 * Gets the s2 n clip level.
	 *
	 * @return the s2 n clip level
	 */
	public double getS2NClipLevel() { return clippingS2N; }
	
	
	/* (non-Javadoc)
	 * @see kovacs.util.data.Data2D#copy(kovacs.util.data.Data2D, int)
	 */
	@Override
	protected void copy(Data2D other, int i) {
		super.copy(other, i); 
		
		if(!(other instanceof GridMap2D)) return;
		
		final GridMap2D<?> map = (GridMap2D<?>) other;
		System.arraycopy(map.weight[i], 0, weight[i], 0, sizeY()); 
		System.arraycopy(map.count[i], 0, count[i], 0, sizeY()); 
	}
	

	/* (non-Javadoc)
	 * @see kovacs.util.data.Data2D#setImage(kovacs.util.data.Data2D)
	 */
	@Override
	public void setImage(Data2D other) {
		super.setImage(other);
		if(!(other instanceof GridMap2D)) return;
		
		GridMap2D<?> image = (GridMap2D<?>) other;

		// Make a copy of the fundamental data
		setWeight(image.getWeight());
		setTime(image.getTime());
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.data.Data2D#annihilate()
	 */
	@Override
	public void destroy() {
		super.destroy();
		weight = null;
		count = null;
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.data.Data2D#setSize(int, int)
	 */
	@Override
	public void setSize(int i, int j) {
		super.setSize(i, j);
		setWeight(new double[i][j]);
		setTime(new double[i][j]);
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.data.Data2D#getPixelInfo(int, int)
	 */
	@Override
	public String getPixelInfo(int i, int j) {
		if(!isValid(i, j)) return "";
		String type = "";
		if(getContentType() != null) if(getContentType().length() != 0) type = getContentType() + "> ";
		return type + Util.getDecimalFormat(getS2N(i, j)).format(getValue(i, j)) + " +- " + Util.s2.format(getRMS(i, j)) + " " + getUnit().name();
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.data.GridImage#crop(int, int, int, int)
	 */
	@Override
	protected void crop(int imin, int jmin, int imax, int jmax) {
		final double[][] oldweight = getWeight();
		final double[][] oldcount = getTime();
		
		final int fromi = Math.max(0, imin);
		final int fromj = Math.max(0, jmin);
		final int toi = Math.min(imax, sizeX()-1);
		final int toj = Math.min(jmax, sizeY()-1);
		
		super.crop(imin, jmin, imax, jmax);
		
		for(int i=fromi, i1=fromi-imin; i<=toi; i++, i1++) for(int j=fromj, j1=fromj-jmin; j<=toj; j++, j1++) {
			setWeight(i1, j1, oldweight[i][j]);
			setTime(i1, j1, oldcount[i][j]);
		}
		
	}


	/* (non-Javadoc)
	 * @see kovacs.util.data.Data2D#clear(int, int)
	 */
	@Override
	public void zero(int i, int  j) {
		super.zero(i, j);
		setWeight(i, j, 0.0);
		setTime(i, j, 0.0);
	}
	
	@Override
	public void scale(int i, int j, double factor) {
		super.scale(i,  j,  factor);
		scaleWeight(i, j, 1.0 / (factor * factor));
	}

	/**
	 * Adds the point at.
	 *
	 * @param mapOffset the map offset
	 * @param value the value
	 * @param g the g
	 * @param w the w
	 * @param time the time
	 */
	public final synchronized void addPointAt(final Vector2D index, final double value, final double g, final double w, final double time) {
		addPointAt((int)Math.round(index.x()), (int)Math.round(index.y()), value, g, w, time);
	}
	
	/**
	 * Adds the point at.
	 *
	 * @param mapOffset the map offset
	 * @param value the value
	 * @param g the g
	 * @param w the w
	 * @param time the time
	 */
	public final synchronized void addPointAt(final Index2D index, final double value, final double g, final double w, final double time) {
		addPointAt(index.i(), index.j(), value, g, w, time);
	}
	
	
	/**
	 * Adds the point at.
	 *
	 * @param i the i
	 * @param j the j
	 * @param value the value
	 * @param g the g
	 * @param w the w
	 * @param time the time
	 */
	public final void addPointAt(final int i, final int j, final double value, final double g, double w, final double time) {
		w *= g;
		increment(i, j, w * value);
		incrementWeight(i, j, w * g);
		incrementTime(i, j, time);
	}
	
	
	
	@Override
	public synchronized void mergePropertiesWith(final Data2D data) {
		super.mergePropertiesWith(data);
	}
	
	
	@Override
	protected void merge(int i, int j, Data2D src, double w) {	
		super.merge(i,  j,  src, w);
		incrementWeight(i, j, w);
		
		if(!(src instanceof GridMap2D)) return;
		GridMap2D<?> map = (GridMap2D<?>) src;
		incrementTime(i, j, map.getTime(i,  j));
	}
	
	
	@Override
	protected void addWeightedDirect(int i, int j, Data2D src, double w) {	
		super.addWeightedDirect(i,  j,  src, w);
		incrementWeight(i, j, w);
		
		if(!(src instanceof GridMap2D)) return;
		GridMap2D<?> map = (GridMap2D<?>) src;
		incrementTime(i, j, map.getTime(i,  j));
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.data.Data2D#sanitize(int, int)
	 */
	@Override
	protected void sanitize(int i, int j) {
		super.sanitize(i, j);
		setWeight(i, j, 0.0);
	}
	

	/**
	 * Apply correction.
	 *
	 * @param filtering the filtering
	 * @param significance the significance
	 */
	public void applyCorrection(double filtering, final double[][] significance) {
		final double ifiltering = 1.0 / filtering;
		final double filtering2 = filtering * filtering;
		
		new Task<Void>() {
			@Override
			protected void process(int i, int j) {
				if(getWeight(i, j) > 0.0) if(significance[i][j] <= clippingS2N) {
					scaleValue(i, j, ifiltering);
					scaleWeight(i, j, filtering2);
				}
			}
		}.process();
	}

	// It's important to completely reset clipped points, otherwise they can be used...
	// One possibility for future is to raise flag only, then call sanitize()
	/**
	 * Clip above relative rms.
	 *
	 * @param maxRelativeRMS the max relative rms
	 */
	public void clipAboveRelativeRMS(double maxRelativeRMS) {
		clipAboveRelativeRMS(maxRelativeRMS, 0.0);
	}
	
	/**
	 * Clip above relative rms.
	 *
	 * @param maxRelativeRMS the max relative rms
	 * @param refPercentile the ref percentile
	 */
	public void clipAboveRelativeRMS(double maxRelativeRMS, double refPercentile) {
		final double[][] rms = getRMSImage().getData();
		final double maxRMS = maxRelativeRMS * new Data2D(rms).select(refPercentile);
		
		new Task<Void>() {
			@Override
			protected void process(int i, int j) {
				if(isUnflagged(i, j)) if(rms[i][j] > maxRMS) flag(i, j);
			}
		}.process();
	}
	
	/**
	 * Clip above rms.
	 *
	 * @param value the value
	 */
	public void clipAboveRMS(final double value) {
		final double[][] rms = getRMSImage().getData();
		
		new Task<Void>() {
			@Override
			protected void process(int i, int j) {
				if(isUnflagged(i, j)) if(rms[i][j] > value) flag(i, j);
			}
		}.process();
	}
	
	/**
	 * Clip below relative exposure.
	 *
	 * @param minRelativeExposure the min relative exposure
	 */
	public void clipBelowRelativeExposure(double minRelativeExposure) {
		clipBelowRelativeExposure(minRelativeExposure, 1.0);
	}
	
	/**
	 * Clip below relative exposure.
	 *
	 * @param minRelativeExposure the min relative exposure
	 * @param refPercentile the ref percentile
	 */
	public void clipBelowRelativeExposure(double minRelativeExposure, double refPercentile) {
		double minIntTime = minRelativeExposure * new Data2D(getTime()).select(refPercentile);
		clipBelowExposure(minIntTime);
	}
	
	/**
	 * Clip below exposure.
	 *
	 * @param minIntTime the min int time
	 */
	public void clipBelowExposure(final double minIntTime) {
		new Task<Void>() {
			@Override
			protected void process(int i, int j) {
				if(isUnflagged(i, j)) if(getTime(i, j) < minIntTime) flag(i, j);
			}
		}.process();
	}

	/**
	 * S2n clip below.
	 *
	 * @param level the level
	 * @param sign the sign
	 */
	public void s2nClipBelow(final double level, final int sign) {
		new Task<Void>() {
			@Override
			protected void process(int i, int j) {
				if(isUnflagged(i, j)) {
					double s2n = getS2N(i,j);
					if(sign < 0.0) s2n *= -1;
					else if(sign == 0.0) s2n = Math.abs(s2n);
					
					if(s2n < level) flag(i, j);
				}
			}
		}.process();
	}

	/**
	 * S2n clip above.
	 *
	 * @param level the level
	 * @param sign the sign
	 */
	public void s2nClipAbove(final double level, final int sign) {
		new Task<Void>() {
			@Override
			protected void process(int i, int j) {
				if(isUnflagged(i, j)) {
					double s2n = getS2N(i,j);
					if(sign < 0.0) s2n *= -1;
					else if(sign == 0.0) s2n = Math.abs(s2n);
					
					if(s2n > level) flag(i, j);
				}
			}
		}.process();
		
		clippingS2N = level;
	}


	/**
	 * Gets the mask.
	 *
	 * @param minS2N the min s2 n
	 * @param minNeighbours the min neighbours
	 * @param signedness the signedness
	 * @return the mask
	 */
	public boolean[][] getMask(final double minS2N, final int minNeighbours, final int signedness) {
		final boolean[][] mask = new boolean[sizeX()][sizeY()];
		
		new Task<Void>() {
			@Override
			protected void process(int i, int j) {
				if(isUnflagged(i, j)) {
					double s2n = getS2N(i,j);
					if(signedness < 0) s2n *= -1.0;
					else if(signedness == 0) s2n = Math.abs(s2n);
					
					if(s2n > minS2N) mask[i][j] = true;
				}
			}
		}.process();
			
		if(minNeighbours > 0) {
			final boolean[][] cleaned = new boolean[sizeX()][sizeY()];
		
			new Task<Void>() {
				@Override
				protected void process(int i, int j) {
					if(mask[i][j]) {
						int neighbours = -1;
						final int fromi = Math.max(0, i-1);
						final int toi = Math.min(sizeX(), i+1);
						final int fromj = Math.max(0, j-1);
						final int toj = Math.min(sizeY(), j+1);
						for(int i1=toi; --i1 >= fromi; ) for(int j1=toj; --j1 >= fromj; ) if(mask[i1][j1]) neighbours++;
						if(neighbours >= minNeighbours) cleaned[i][j] = true;
					}
				}
			}.process();
			return cleaned;
		}
		
		return mask;
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.data.Data2D#smooth(double[][])
	 */
	@Override
	public void smooth(double[][] beam) {
		double[][] beamw = new double[sizeX()][sizeY()];
		setData(getSmoothed(beam, beamw));
		setTime(getTimeImage().getSmoothed(beam, null));
		setWeight(beamw);
	}
	
	
	
	/* (non-Javadoc)
	 * @see kovacs.util.data.Data2D#fastSmooth(double[][], int, int)
	 */
	@Override
	public void fastSmooth(double[][] beam, int stepX, int stepY) {
		final double[][] beamw = new double[sizeX()][sizeY()];
		setData(getFastSmoothed(beam, beamw, stepX, stepY));
		setTime(getTimeImage().getFastSmoothed(beam, null, stepX, stepY));
		setWeight(beamw);
		
		// Rescale weights to account for prior smoothing...
		if(getSmoothArea() > getGrid().getPixelArea()) 
			scaleWeight(getGrid().getPixelArea() / getSmoothArea());
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.data.GridImage#resample(kovacs.util.data.GridImage)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void resample(GridImage2D<CoordinateType> from) {
			
		if(!(from instanceof GridMap2D)) {
			super.resample(from);
			setWeight(1.0);
			return;
		}
		
		reset(false);
		
		// Antialias filter first...
		if(!from.getSmoothing().isEncompassing(getSmoothing())) {
			from = (GridImage2D<CoordinateType>) from.copy(true);
			from.smoothTo(getSmoothing());
		}
		
		final GridMap2D<CoordinateType> fromMap = (GridMap2D<CoordinateType>) from;
		final GridImage2D<CoordinateType> fromWeight = fromMap.getWeightImage();
		final GridImage2D<CoordinateType> fromCount = fromMap.getTimeImage();
		
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
				fromMap.toIndex(v);
				
				final InterpolatorData ipolData = getInterpolatorData();
				
				setValue(i, j, fromMap.valueAtIndex(v.x(), v.y(), ipolData));
				setWeight(i, j, fromWeight.valueAtIndex(v.x(), v.y(), ipolData));
				setTime(i, j, fromCount.valueAtIndex(v.x(), v.y(), ipolData));					
				
				if(isNaN(i, j)) flag(i, j);
				else { unflag(i, j); }
			}
		}.process();
	
	
	}
	
	/**
	 * Gets the weight image.
	 *
	 * @return the weight image
	 */
	public GridImage2D<CoordinateType> getWeightImage() { 
		double unit = getUnit().value();
		return getImage(getWeight(), "Weight", new Unit("[" + getUnit().name() + "]**(-2)", 1.0 / (unit * unit)));
	}
	
	/**
	 * Gets the time image.
	 *
	 * @return the time image
	 */
	public GridImage2D<CoordinateType> getTimeImage() { 
		return getImage(getTime(), "Exposure", new Unit("s/pixel", Unit.s));
	}
	
	/**
	 * Gets the rMS image.
	 *
	 * @return the rMS image
	 */
	public GridImage2D<CoordinateType> getRMSImage() {
		return getImage(getRMS(), "Noise", getUnit());
	}
	
	/**
	 * Gets the s2 n image.
	 *
	 * @return the s2 n image
	 */
	public GridImage2D<CoordinateType> getS2NImage() { 
		return getImage(getS2N(), "S/N", Unit.unity);
	}
	
	
	/* (non-Javadoc)
	 * @see kovacs.util.data.Data2D#getRMS(int, int)
	 */
	@Override
	public final double getRMS(final int i, final int j) {
		return 1.0 / Math.sqrt(getWeight(i, j));		
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.data.Data2D#getS2N(int, int)
	 */
	@Override
	public final double getS2N(final int i, final int j) {
		return getValue(i, j) / getRMS(i,j);		
	}
	
	/**
	 * Gets the typical rms.
	 *
	 * @return the typical rms
	 */
	public double getTypicalRMS() {
		Task<WeightedPoint> avew = new AveragingTask() {
			private double sumw = 0.0;
			private int n = 0;
			@Override
			protected void process(int i, int j) {
				if(isUnflagged(i, j)) {
					sumw += getWeight(i, j);
					n++;
				}
			}
			@Override
			public WeightedPoint getPartialResult() { return new WeightedPoint(sumw, n); }
		};
			
		avew.process();		
		return Math.sqrt(1.0 / avew.getResult().value());
	}
	
	
		
	/* (non-Javadoc)
	 * @see kovacs.util.data.Data2D#getSkip(double)
	 */
	@Override
	public int[][] getSkip(final double blankingValue) {
		final int[][] skip = (int[][]) copyOf(getFlag());
		
		new Task<Void>() {
			@Override
			protected void process(int i, int j) {
				if(getS2N(i,j) > blankingValue) skip[i][j] = 1;
			}
		}.process();
		
		return skip;
	}
	
	/**
	 * Filter above.
	 *
	 * @param extendedFWHM the extended fwhm
	 * @param blankingValue the blanking value
	 */
	public void filterAbove(double extendedFWHM, double blankingValue) {
		filterAbove(extendedFWHM, getSkip(blankingValue));
		filterBlanking = blankingValue;
	}


	/**
	 * Fft filter above.
	 *
	 * @param extendedFWHM the extended fwhm
	 * @param blankingValue the blanking value
	 */
	public void fftFilterAbove(double extendedFWHM, double blankingValue) {
		fftFilterAbove(extendedFWHM, getSkip(blankingValue));
		filterBlanking = blankingValue;
	}
	
	
	/**
	 * Gets the mean integration time.
	 *
	 * @return the mean integration time
	 */
	public double getMeanIntegrationTime() {
		Task<WeightedPoint> meanIntTime = new AveragingTask() {
			private double sum = 0.0, sumw = 0.0;	
			@Override
			protected void process(final int i, final int j) {
				if(isUnflagged(i, j)) {
					final double w = getWeight(i, j);
					sum += w * getTime(i, j);
					sumw += w;
				}
			}
			@Override
			public WeightedPoint getPartialResult() { return new WeightedPoint(sum, sumw); }
		};
		
		meanIntTime.process();
		return meanIntTime.getResult().value();
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.data.Data2D#median()
	 */
	@Override
	public double median() {
		WeightedPoint[] point = new WeightedPoint[countPoints()];

		if(point.length == 0) return 0.0;

		int k=0;
		for(int i=sizeX(); --i >= 0; ) for(int j=sizeY(); --j >= 0; ) if(isUnflagged(i, j))
			point[k++] = new WeightedPoint(getValue(i, j), getWeight(i, j));
		
		return Statistics.median(point).value();
	}

	/**
	 * Reweight.
	 *
	 * @param robust the robust
	 */
	public void reweight(boolean robust) {
		double weightCorrection = 1.0 / (robust ? getRobustChi2() : getChi2());
		scaleWeight(weightCorrection);
		weightFactor *= weightCorrection;
	}

	// Return to calculated weights...
	/**
	 * Data weight.
	 */
	public void dataWeight() {
		scaleWeight(1.0/weightFactor);
		weightFactor = 1.0;
	}

	/**
	 * Gets the chi2.
	 *
	 * @param robust the robust
	 * @return the chi2
	 */
	public double getChi2(boolean robust) {
		return robust ? getRobustChi2() : getChi2();
	}
	
	/**
	 * Gets the robust chi2.
	 *
	 * @return the robust chi2
	 */
	protected double getRobustChi2() {
		float[] chi2 = new float[sizeX() * sizeY()];
		if(chi2.length == 0) return 0.0;
		
		int k=0;
		for(int i=sizeX(); --i >= 0; ) for(int j=sizeY(); --j >= 0; ) if(isUnflagged(i, j)) {
			final float s2n = (float) getS2N(i,j);
			chi2[k++] = s2n * s2n;
		}
	
		// median(x^2) = 0.454937 * sigma^2 
		return k > 0 ? Statistics.median(chi2, 0, k) / 0.454937 : 0.0;	
	}

	/**
	 * Gets the chi2.
	 *
	 * @return the chi2
	 */
	protected double getChi2() {
		Task<WeightedPoint> rChi2 = new AveragingTask() {
			private double chi2 = 0.0;
			private int n = 0;	
			@Override
			protected void process(final int i, final int j) {
				if(isUnflagged(i, j)) {
					final double s2n = getS2N(i,j);
					chi2 += s2n * s2n;
					n++;
				}
			}
			@Override
			public WeightedPoint getPartialResult() { return new WeightedPoint(chi2, n); }
		};
		
		rChi2.process();
		return rChi2.getResult().value();
	}
	
	
	// TODO redo with parallel... Need global interrupt (static interrupt()?)
	/**
	 * Contains na n.
	 *
	 * @return true, if successful
	 */
	public boolean containsNaN() {
		boolean hasNaN = false;
		for(int i=sizeX(); --i >= 0; ) for(int j=sizeY(); --j >= 0; ) {
			if(isNaN(i, j)) hasNaN = true;
			if(Double.isNaN(getWeight(i, j))) hasNaN = true;
		}
		return hasNaN;
	}


	// derive the MEM correction if MEM modeling
	
	/**
	 * Mem.
	 *
	 * @param lambda the lambda
	 * @param forceNonNegative the force non negative
	 */
	public void MEM(double lambda, boolean forceNonNegative) {
		//double[][] smoothed = getConvolvedTo(data, 5.0*instrument.resolution);
		double[][] smoothed = new double[sizeX()][sizeY()];
		//if(forceNonNegative) for(int x=0; x<sizeX(); x++) for(int y=0; y<sizeY(); y++) if(smoothed[x][y] < 0.0) smoothed[x][y] = 0.0;
		MEM(smoothed, lambda);
	}
		
	/**
	 * Mem.
	 *
	 * @param model the model
	 * @param lambda the lambda
	 */
	public void MEM(final double[][] model, final double lambda) {
		new Task<Void>() {
			@Override
			protected void process(int i, int j) {
				if(isUnflagged(i, j)) {
					final double sigma = getRMS(i, j);
					final double memValue = ExtraMath.hypot(sigma, getValue(i, j)) / ExtraMath.hypot(sigma, model[i][j]) ;
					decrement(i, j, Math.signum(getValue(i, j)) * lambda * sigma * Math.log(memValue));
				}
			}
		}.process();
	}	
	
	/* (non-Javadoc)
	 * @see kovacs.util.data.Data2D#createFits()
	 */
	@Override
	public Fits createFits() throws HeaderCardException, FitsException, IOException {
		Fits fits = super.createFits();
		fits.addHDU(getTimeImage().createHDU());
		fits.addHDU(getRMSImage().createHDU());
		fits.addHDU(getS2NImage().createHDU());
		return fits;
	}

	
	/* (non-Javadoc)
	 * @see kovacs.util.data.GridImage#parseHeader(nom.tam.fits.Header)
	 */
	@Override
	protected void parseHeader(Header header, String alt) throws Exception {			
		super.parseHeader(header, alt);
		
		weightFactor =  header.getDoubleValue("XWEIGHT", 1.0);
		filterBlanking = header.getDoubleValue("FLTRBLNK", header.getDoubleValue("MAP_XBLK", Double.NaN));
		clippingS2N = header.getDoubleValue("CLIPS2N", header.getDoubleValue("MAP_CLIP", Double.NaN));
	}


	/**
	 * Read data.
	 *
	 * @param HDU the hdu
	 * @throws FitsException the fits exception
	 */
	private void readData(BasicHDU<?>[] HDU) throws FitsException {
		super.readData(HDU[0]);
		
		BasicHDU<?> timeHDU = null;
		BasicHDU<?> noiseHDU = null;
		BasicHDU<?> weightHDU = null;
		BasicHDU<?> varHDU = null;
		
		// Try find image HDUs with noise or exposure time information...	
		for(int i=1; i<HDU.length; i++) if(HDU[i] instanceof ImageHDU) {
			final String extName = HDU[i].getHeader().getStringValue("EXTNAME").toLowerCase();
			
			if(extName.contains("exposure")) timeHDU = HDU[i];
			else if(extName.contains("time")) { if(timeHDU == null) timeHDU = HDU[i]; }
			else if(extName.contains("weight")) weightHDU = HDU[i];	
			else if(extName.contains("to-noise")) continue;	// e.g. signal-to-noise
			else if(extName.contains("to noise")) continue;  // e.g. signal to noise
			else if(extName.contains("/noise")) continue;  // e.g. signal to noise
			else if(extName.contains("/ noise")) continue;  // e.g. signal to noise
			else if(extName.contains("noise")) noiseHDU = HDU[i];							// noise weight -> weight
			else if(extName.contains("rms")) { if(noiseHDU == null) noiseHDU = HDU[i]; }
			else if(extName.contains("error")) { if(noiseHDU == null) noiseHDU = HDU[i]; }
			else if(extName.contains("uncertainty")) { if(noiseHDU == null) noiseHDU = HDU[i]; }
			else if(extName.contains("sensitivity")) { if(noiseHDU == null) noiseHDU = HDU[i]; }
			else if(extName.contains("depth")) { if(noiseHDU == null) noiseHDU = HDU[i]; }
			else if(extName.contains("scatter")) { if(noiseHDU == null) noiseHDU = HDU[i]; }
			
			else if(extName.contains("coverage")) { if(timeHDU == null) timeHDU = HDU[i]; } // depth coverage -> noise
			else if(extName.contains("variance")) varHDU = HDU[i];
			else if(extName.equals("var")) { if(varHDU == null) varHDU = HDU[i]; }
		}
		
		if(timeHDU != null) getTimeImage().setImage(timeHDU);
		
		final Data2D image = getWeightImage();
		
		// Read weights, or caluclate from rms noise, variance, or use time, if available.
		if(weightHDU != null) image.setImage(weightHDU);
		else if(noiseHDU != null) {
			//image.setUnit(getUnit());
			image.setImage(noiseHDU);
			new Task<Void>() {
				@Override
				protected void process(int i, int j) {
					final double rms = image.valueAtIndex(i, j);
					setWeight(i, j, 1.0 / (rms * rms)); 
				}
			}.process();
		}
		else if(varHDU != null) {
			image.setImage(varHDU);

			new Task<Void>() {
				@Override
				protected void process(int i, int j) {
					setWeight(i, j, 1.0 / image.valueAtIndex(i, j)); 
				}
			}.process();
		}
		else if(timeHDU != null) getWeightImage().setImage(timeHDU);
		else setWeight(1.0);
	}

	
	/* (non-Javadoc)
	 * @see kovacs.util.data.GridImage#editHeader(nom.tam.util.Cursor)
	 */
	@Override
	public void editHeader(Header header, Cursor<String, HeaderCard> cursor) throws HeaderCardException, FitsException, IOException {
		super.editHeader(header, cursor);
		
		if(!Double.isNaN(filterBlanking))
			cursor.add(new HeaderCard("FLTRBLNK", filterBlanking, "The S/N blanking of LSS filter."));
		if(!Double.isNaN(clippingS2N))
			cursor.add(new HeaderCard("CLIPS2N", clippingS2N, "The S/N clipping level used in reduction."));
		
	}
	
	    

	/**
	 * Despike.
	 *
	 * @param significance the significance
	 */
	public void despike(final double significance) {
		final double[][] neighbours = {{ 0, 1, 0 }, { 1, 0, 1 }, { 0, 1, 0 }};
		final GridMap2D<?> diff = (GridMap2D<?>) copy(true);
		diff.smooth(neighbours);
		
		new Task<Void>() {	
			private WeightedPoint point, surrounding;
			@Override
			protected void init() {
				point = new WeightedPoint();
				surrounding = new WeightedPoint();
			}
			@Override
			protected void process(final int i, final int j) {
				if(isUnflagged(i, j)) {
					point.setValue(getValue(i, j));
					point.setWeight(getWeight(i, j));
					surrounding.setValue(diff.getValue(i, j));
					surrounding.setWeight(diff.getWeight(i, j));
					point.subtract(surrounding);
					if(DataPoint.significanceOf(point) > significance) flag(i, j);			
				}	
			}
		}.process();
	}
	
	/**
	 * Gets the flux.
	 *
	 * @param region the region
	 * @return the flux
	 */
	public DataPoint getFlux(Region<CoordinateType> region) {
		final IndexBounds2D bounds = region.getBounds(this);
		double flux = 0.0, var = 0.0;

		double A = 1.0 / getPointsPerSmoothingBeam();

		for(int i=bounds.fromi; i<=bounds.toi; i++) for(int j=bounds.fromj; j<=bounds.toj; j++)
			if(isUnflagged(i, j)) if(region.isInside(getGrid(), i, j))  {
				flux += getValue(i, j);
				var += 1.0 / getWeight(i, j);
			}
		
		return new DataPoint(A * flux, A * Math.sqrt(var));
	}

	/**
	 * Gets the rms.
	 *
	 * @param region the region
	 * @return the rms
	 */
	public double getRMS(Region<CoordinateType> region) {
		final IndexBounds2D bounds = region.getBounds(this);
		double var = 0.0;
		int n = 0;
		double level = getLevel(region);

		for(int i=bounds.fromi; i<=bounds.toi; i++) for(int j=bounds.fromj; j<=bounds.toj; j++)
			if(isUnflagged(i, j)) if(region.isInside(getGrid(), i, j))  {
				double value = getValue(i, j) - level;
				var += value * value;
				n++;
			}
		var /= (n-1);

		return Math.sqrt(var);
	}	
	
	
	/**
	 * Gets the mean noise.
	 *
	 * @param region the region
	 * @return the mean noise
	 */
	public double getMeanNoise(Region<CoordinateType> region) {
		final IndexBounds2D bounds = region.getBounds(this);
		double var = 0.0;
		int n = 0;

		for(int i=bounds.fromi; i<=bounds.toi; i++) for(int j=bounds.fromj; j<=bounds.toj; j++)
			if(isUnflagged(i, j))  if(region.isInside(getGrid(), i, j)) {
				final double rms = getRMS(i,j);
				var += rms * rms;
				n++;
			}
		var /= (n-1);
		
		return Math.sqrt(var);
	}
	
	/**
	 * Gets the mean exposure.
	 *
	 * @param region the region
	 * @return the mean exposure
	 */
	public double getMeanExposure(Region<CoordinateType> region) {	
		final IndexBounds2D bounds = region.getBounds(this);
		double sum = 0.0;
		int n = 0;

		for(int i=bounds.fromi; i<=bounds.toi; i++) for(int j=bounds.fromj; j<=bounds.toj; j++)
			if(isUnflagged(i, j)) if(region.isInside(getGrid(), i, j)) {
				sum += getTime(i, j);
				n++;
			}
		
		return n > 0 ? sum / n : 0.0;
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.data.GridImage#clean(double[][], double, double)
	 */
	@Override
	public int clean(double[][] beam, double gain, GaussianPSF replacementBeam) {
	    return clean(getS2NImage(), beam, gain, replacementBeam);
	}
	
	
	/* (non-Javadoc)
	 * @see kovacs.util.data.GridImage#toString()
	 */
	@Override
	public String toString() {	
	
		String info = super.toString() +
			(weightFactor == 1.0 ? 
					"" : 
					"  Noise Re-scaling: " + Util.f2.format(Math.sqrt(1.0 / weightFactor)) + "x (from image variance).\n"); 

		return info;
	}

	/* (non-Javadoc)
	 * @see kovacs.util.data.Timed2D#getTime()
	 */
	@Override
	public final double[][] getTime() {
		return count;
	}

	/* (non-Javadoc)
	 * @see kovacs.util.data.Timed2D#setTime(double[][])
	 */
	@Override
	public final void setTime(final double[][] image) {
		count = image;
	}

	/* (non-Javadoc)
	 * @see kovacs.util.data.Timed2D#getTime(int, int)
	 */
	@Override
	public final double getTime(final int i, final int j) {
		return count[i][j];
	}

	/* (non-Javadoc)
	 * @see kovacs.util.data.Timed2D#setTime(int, int, double)
	 */
	@Override
	public final void setTime(final int i, final int j, final double t) {
		count[i][j] = t;
	}

	/* (non-Javadoc)
	 * @see kovacs.util.data.Timed2D#incrementTime(int, int, double)
	 */
	@Override
	public final void incrementTime(final int i, final int j, final double dt) {
		count[i][j] += dt;
	}

	
	/**
	 * Scale.
	 *
	 * @param value the value
	 */
	public final void setTime(final double value) {	
		new Task<Void>() {
			@Override
			protected void process(int i, int j) { setTime(i, j, value); }
		}.process();
	}

	
	/* (non-Javadoc)
	 * @see kovacs.util.data.Weighted2D#getWeight()
	 */
	@Override
	public final double[][] getWeight() {
		return weight;
	}

	/* (non-Javadoc)
	 * @see kovacs.util.data.Weighted2D#setWeight(double[][])
	 */
	@Override
	public final void setWeight(final double[][] image) {
		weight = image;
	}

	/* (non-Javadoc)
	 * @see kovacs.util.data.Data2D#getWeight(int, int)
	 */
	@Override
	public final double getWeight(final int i, final int j) {
		return weight[i][j];
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.data.Weighted2D#scaleWeight(double)
	 */
	@Override
	public void scaleWeight(final double scalar) {
		if(scalar == 1.0) return;
		
		new Task<Void>() {
			@Override
			protected void process(int i, int j) { scaleWeight(i, j, scalar); }
		}.process();
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.data.Weighted2D#setWeight(int, int, double)
	 */
	@Override
	public final void setWeight(final int i, final int j, final double w) {
		weight[i][j] = w;
	}

	/* (non-Javadoc)
	 * @see kovacs.util.data.Weighted2D#incrementWeight(int, int, double)
	 */
	@Override
	public final void incrementWeight(final int i, final int j, final double dw) {
		weight[i][j] += dw;
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.data.Weighted2D#scaleWeight(int, int, double)
	 */
	@Override
	public void scaleWeight(final int i, final int j, final double factor) {
		weight[i][j] *= factor;
	}

	
	/**
	 * Scale.
	 *
	 * @param value the value
	 */
	public final void setWeight(final double value) {	
		new Task<Void>() {
			@Override
			protected void process(int i, int j) { setWeight(i, j, value); }
		}.process();
	}

	/* (non-Javadoc)
	 * @see kovacs.util.data.Noise2D#getRMS()
	 */
	@Override
	public double[][] getRMS() {
		final double[][] rms = new double[sizeX()][sizeY()];
		
		new Task<Void>() {
			@Override
			protected void process(int i, int j) { rms[i][j] = getRMS(i,j); }
		}.process();
		
		return rms;
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.data.Noise2D#setRMS(double[][])
	 */
	@Override
	public void setRMS(final double[][] image) {
		new Task<Void>() {
			@Override
			protected void process(int i, int j) { setRMS(i,j, image[i][j]); }
		}.process();
	}

	/* (non-Javadoc)
	 * @see kovacs.util.data.Noise2D#scaleRMS(double)
	 */
	@Override
	public void scaleRMS(final double scalar) {
		scaleWeight(1.0 / (scalar*scalar));
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.data.Noise2D#setRMS(int, int, double)
	 */
	@Override
	public void setRMS(int i, int j, double sigma) {
		setWeight(i, j, 1.0 / (sigma * sigma));	
	}

	/* (non-Javadoc)
	 * @see kovacs.util.data.Noise2D#scaleRMS(int, int, double)
	 */
	@Override
	public void scaleRMS(int i, int j, double factor) {
		scaleWeight(i, j, 1.0 / (factor * factor));
	}
	
	
	/**
	 * Scale.
	 *
	 * @param value the value
	 */
	public final void setRMS(final double value) {	
		final double w = 1.0 / (value * value);
		new Task<Void>() {
			@Override
			protected void process(int i, int j) { setWeight(i, j, w); }
		}.process();
	}


	/* (non-Javadoc)
	 * @see kovacs.util.data.Noise2D#getS2N()
	 */
	@Override
	public double[][] getS2N() {
		final double[][] s2n = new double[sizeX()][sizeY()];
		
		new Task<Void>() {
			@Override
			protected void process(int i, int j) { s2n[i][j] = getS2N(i,j); }
		}.process();
		
		return s2n;
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.data.GridImage#getFormattedEntry(java.lang.String, java.lang.String)
	 */
	@Override
	public String getFormattedEntry(String name, String formatSpec) {
		if(name.equals("depth")) return TableFormatter.getNumberFormat(formatSpec).format(getTypicalRMS() / getUnit().value());
		else return super.getFormattedEntry(name, formatSpec);
	}
	
	public static GridMap2D<?> fromHeader(Header header) throws Exception {
		return fromHeader(header, "");
	}
	
	public static GridMap2D<?> fromHeader(Header header, String alt) throws Exception {	
		GridMap2D<?> map = new GridMap2D<Coordinate2D>();
		map.parseHeader(header, alt);
		return map;
	}
	
	public static GridMap2D<?> fromHDUs(BasicHDU<?>[] hdu) throws Exception {
		return fromHDUs(hdu, "");
	}
	
	public static GridMap2D<?> fromHDUs(BasicHDU<?>[] hdu, String alt) throws Exception {	
		GridMap2D<?> map = fromHeader(hdu[0].getHeader(), alt);
		map.readData(hdu);
		return map;
	}
	
}

