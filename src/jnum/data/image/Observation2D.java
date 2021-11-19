/* *****************************************************************************
 * Copyright (c)2020 Attila Kovacs <attila[AT]sigmyne.com>.
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

package jnum.data.image;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import jnum.NonConformingException;
import jnum.Unit;
import jnum.Util;
import jnum.data.ComponentType;
import jnum.data.FlagCompanion;
import jnum.data.Observations;
import jnum.data.RegularData;
import jnum.data.WeightedPoint;
import jnum.data.image.overlay.Flagged2D;
import jnum.data.image.overlay.Overlay2D;
import jnum.data.image.overlay.Referenced2D;
import jnum.data.index.Index2D;
import jnum.data.index.IndexedExposures;
import jnum.data.index.IndexedUncertainties;
import jnum.fits.FitsToolkit;
import jnum.math.CoordinateTransform;
import jnum.math.Vector2D;
import nom.tam.fits.BasicHDU;
import nom.tam.fits.FitsException;
import nom.tam.fits.ImageHDU;


public class Observation2D extends Map2D implements Observations<Data2D>, IndexedExposures<Index2D>, IndexedUncertainties<Index2D> {
    /**
     * 
     */
    private static final long serialVersionUID = -5595106427134511945L;

    private Image2D weight;
    private Image2D exposure;
   
    
    private double noiseRescale = 1.0;
    
    public boolean isZeroWeightValid = false;
    
    
    public Observation2D(Class<? extends Number> dataType, FlagCompanion.Type flagType) {
        this(dataType, dataType, flagType);
    }
   
    public Observation2D(Class<? extends Number> dataType, Class<? extends Number> weightType, FlagCompanion.Type flagType) {
        super(dataType, flagType); 
        setWeightImage(Image2D.createType(weightType));
        setExposureImage(Image2D.createType(weightType));
    }
     
    @Override
    public int hashCode() { 
        int hash = super.hashCode();
        if(weight != null) hash ^= weight.hashCode();
        if(exposure != null) hash ^= exposure.hashCode();
        return hash;
    }
    
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof Observation2D)) return false;
         
        Observation2D obs = (Observation2D) o;
        if(!Util.equals(weight, obs.weight)) return false;
        if(!Util.equals(exposure, obs.exposure)) return false;
        
        return super.equals(o);
    }
       
    @Override
    public Observation2D copy() { return (Observation2D) super.copy(); }
    
    @Override
    public Observation2D copy(boolean withContents) {
        Observation2D copy = (Observation2D) super.copy(withContents);  
        
        if(weight != null) copy.weight = weight.copy(withContents);
        if(exposure != null) copy.exposure = exposure.copy(withContents);
        
        return copy;
    }
    
    public void copyProcessingFrom(Observation2D template) {
        super.copyProcessingFrom(template);
        noiseRescale = template.noiseRescale;        
    }
    
    @Override
    public void resetProcessing() {
        super.resetProcessing();
        noiseRescale = 1.0;
    }
    
    @Override
    public String getInfo() {
        return super.getInfo() +
                (noiseRescale == 1.0 ? "" : 
                    "Noise Re-scaling: " + Util.f2.format(noiseRescale) + "x (from image variance).\n"); 
    }
    
    @Override
    public boolean isValid(int i, int j) {
        if(!super.isValid(i, j)) return false;
        return isZeroWeightValid ? weightAt(i, j) >= 0.0 : weightAt(i, j) > 0.0;
    }
    
    @Override
    public void clear(int i, int j) {
        super.clear(i, j);
        weight.clear(i, j);
        exposure.clear(i, j);
    }
    
    @Override
    public void discard(int i, int j) {
        super.discard(i, j);
        weight.clear(i, j);
        exposure.clear(i, j);
    }
    

    @Override
    public void destroy() {
        super.destroy();
        getWeightImage().destroy();
        getExposureImage().destroy();
    }


    @Override
    public Flagged2D getWeights() { 
        return new Flagged2D(weight, getFlags()) {
            @Override
            public boolean isValid(int i, int j) { return Observation2D.this.isValid(i, j); }
            @Override
            public void discard(int i, int j) { Observation2D.this.discard(i, j); }
        };
    }
    
    public final Image2D getWeightImage() {
        return weight;
    }

    public void setWeightImage(Image2D image) { 
        weight = (image == null) ? Image2D.createType(getElementType()) : image;
        claim(weight);
    }

    @Override
    public Flagged2D getExposures() { 
        return new Flagged2D(exposure, getFlags()) {
            @Override
            public boolean isValid(int i, int j) { return Observation2D.this.isValid(i, j); }
            @Override
            public void discard(int i, int j) { Observation2D.this.discard(i, j); }
        };
    }
    
    public final Image2D getExposureImage() {
        return exposure;
    }

    public void setExposureImage(Image2D image) {  
        exposure = (image == null) ? Image2D.createType(getElementType()) : image; 
        claim(exposure);
    }

    @Override
    public Overlay2D getNoise() {
          
        return new Overlay2D(this) {   
         
            @Override
            public Number get(int i, int j) {
                return noiseAt(i, j);
            }

            @Override
            public void set(int i, int j, Number value) {
                super.set(i, j, 1.0 / (value.doubleValue() * value.doubleValue()));
            }    
            
            @Override
            public void add(int i, int j, Number value) {
                set(i, j, get(i, j).doubleValue() + value.doubleValue());
            }
            
            @Override
            protected void setDefaultUnit() { setUnit(Observation2D.this.getUnit()); }
        };
    }

    @Override
    public Overlay2D getSignificance() {
        return new Overlay2D(this) {

            @Override
            public Number get(int i, int j) {
                return significanceAt(i, j);
            }

            @Override
            public void set(int i, int j, Number value) {
                super.set(i, j, value.doubleValue() * noiseAt(i, j));
            }  
            
            @Override
            public void add(int i, int j, Number value) {
                set(i, j, get(i, j).doubleValue() + value.doubleValue());
            }    
        
            @Override
            protected void setDefaultUnit() { super.setUnit(Unit.unity); }
          
            @Override
            public void setUnit(Unit u) {
                throw new UnsupportedOperationException("Cannot change units of S/N image.");
            }
        };
    }
    
   
    @Override
    public void setSize(int sizeX, int sizeY) {
        super.setSize(sizeX, sizeY);
        
        // Create brand new images, so that setSize on a clone does not change the original...
        setWeightImage(Image2D.createType(getWeightImage().getElementType(), sizeX, sizeY));
        setExposureImage(Image2D.createType(getExposureImage().getElementType(), sizeX, sizeY));
    }

    
    @Override
    public void setParallel(int n) {
        super.setParallel(n);
        
        if(weight != null) weight.setParallel(n);
        if(exposure != null) exposure.setParallel(n);
    }


    @Override
    public void setExecutor(ExecutorService executor) {
        super.setExecutor(executor);
        
        if(weight != null) weight.setExecutor(executor);
        if(exposure != null) exposure.setExecutor(executor);
    }
    
    
    @Override
    public final double weightAt(Index2D index) {
        return noiseAt(index.i(), index.j());
    }

    public final double weightAt(int i, int j) {
        return weight.get(i, j).doubleValue();
    }
    
    @Override
    public final void setWeightAt(Index2D index, double value) {
       setWeightAt(index.i(), index.j(), value);
    }

    public void setWeightAt(int i, int j, double value) {
        weight.set(i, j, value);
    }

    @Override
    public final double exposureAt(Index2D index) {
        return exposureAt(index.i(), index.j());
    }
    
    public final double exposureAt(int i, int j) {
        return exposure.get(i, j).doubleValue();
    }

    public void setExposureAt(int i, int j, double value) {
        exposure.set(i, j, value);
    }

    @Override
    public final double noiseAt(Index2D index) {
        return noiseAt(index.i(), index.j());
    }
    
    public double noiseAt(int i, int j) { 
        return 1.0 / Math.sqrt(weightAt(i, j));
    }

    @Override
    public final void setNoiseAt(Index2D index, double value) {
        setNoiseAt(index.i(), index.j(), value);
    }

    public void setNoiseAt(int i, int j, double value) {
        setWeightAt(i, j, 1.0 / (value * value));
    }
    
    @Override
    public final double significanceAt(Index2D index) {
        return noiseAt(index.i(), index.j());
    }
    
    public double significanceAt(int i, int j) {
        return get(i, j).doubleValue() * Math.sqrt(weightAt(i, j));
    }

    @Override
    public void scale(int i, int j, double factor) {
        super.scale(i, j, factor);
        getWeightImage().scale(i, j, 1.0 / (factor * factor));
    }

    @Override
    public void scale(double factor) {
        super.scale(factor);
        getWeightImage().scale(1.0 / (factor * factor));
    }


    @Override
    public synchronized void crop(Index2D from, Index2D to) {
        getWeightImage().crop(from, to);
        getExposureImage().crop(from, to);
        super.crop(from, to);
    }


    public final void accumulate(final Observation2D image) {
        accumulate(image, 1.0, 1.0);
     }
    
    
    public void accumulate(final Observation2D image, final double gain, final double weight) {
        new Fork<Void>() {
            @Override
            protected void process(int i, int j) {
                if(image.isValid(i, j)) accumulateAt(i, j, image.get(i, j).doubleValue(), gain, weight * image.weightAt(i, j), image.exposureAt(i, j));
            }
        }.process();
    }
    
    @Override
    public void endAccumulation() {
        new Fork<Void>() {
            @Override
            protected void process(int i, int j) { endAccumulation(i, j); }
        }.process();
    }


    public void endAccumulation(int i, int j) {
        super.scale(i, j, 1.0 / weight.get(i,j).doubleValue());
    }

    public final void mergeAccumulate(final Observation2D image) {
        super.add(image);

        mergePropertiesFrom(image);
        
        getWeightImage().add(image.getWeights());
        getExposureImage().add(image.getExposures());
    }
    


    public final synchronized void accumulateAt(final Vector2D index, final double value, final double gain, final double w, final double time) {
        accumulateAt((int)Math.round(index.x()), (int)Math.round(index.y()), value, gain, w, time);
    }


    public final synchronized void accumulateAt(final Index2D index, final double value, final double gain, final double w, final double time) {
        accumulateAt(index.i(), index.j(), value, gain, w, time);
    }


    public final void accumulateAt(final int i, final int j, final double value, final double gain, double w, final double time) {
        add(i, j, w * gain * value);
        getWeightImage().add(i, j, w * gain * gain);
        getExposureImage().add(i, j, time);
    }


    public void reweight(boolean robust) {
        double weightCorrection = 1.0 / getChi2(robust);
        getWeightImage().scale(weightCorrection);
        noiseRescale *= 1.0 / Math.sqrt(weightCorrection);
    }


    public void unscaleWeights() {
        getWeightImage().scale(1.0/(noiseRescale * noiseRescale));
        noiseRescale = 1.0;
    }
    
    public final double getNoiseRescale() { return noiseRescale; }
    
    public void setNoiseRescale(double value) { noiseRescale = value; }
    
    public void noiseRescaleBy(double factor) { noiseRescale *= factor; }

    
    @Override
    public WeightedPoint getMean() { return getWeightedMean(weight); }

    @Override
    public WeightedPoint getMedian() { return getWeightedMedian(weight); }
    
    // TODO Make default method in Observations
    public double getChi2(boolean robust) {
        return robust ? getSignificance().getRobustVariance() : getSignificance().getVariance();
    }

    
    // TODO Make default method in Observations
    public final void memCorrect(final Values2D model, final double lambda) {
        memCorrect(model, this.getNoise(), lambda);
    }  


    @Override
    public void smooth(RegularData<Index2D, Vector2D> beam, Vector2D refIndex) {
        Image2D smoothWeights = getWeightImage().copy(false);
        
        setImage((Image2D) getSmoothed(beam, refIndex, weight, smoothWeights));
        setExposureImage((Image2D) getExposures().getSmoothed(beam, refIndex, weight, null));
        setWeightImage(smoothWeights);

        addSmoothing(Gaussian2D.getEquivalent(beam, getGrid().getResolution()));
    }

    @Override
    public void fastSmooth(RegularData<Index2D, Vector2D> beam, Vector2D refIndex, Index2D step) {
        Image2D smoothWeights = getWeightImage().copy(false);
        
        setImage((Image2D) getFastSmoothed(beam, refIndex, step, weight, smoothWeights));
        setExposureImage((Image2D) getExposures().getFastSmoothed(beam, refIndex, step, weight, null));
        setWeightImage(smoothWeights);
        
        addSmoothing(Gaussian2D.getEquivalent(beam, getGrid().getResolution()));
    }
    
    @Override
    public void filterCorrect(double underlyingFWHM) {
        filterCorrect(underlyingFWHM, getSignificance());
    }
    
    @Override
    public void undoFilterCorrect() {
        undoFilterCorrect(getSignificance());
    }

    @Override
    public void fftFilterAbove(double FWHM) {
        super.fftFilterAbove(FWHM, weight);
    }

    @Override
    public void fftFilterAbove(double FWHM, final Validating2D skip) {
        super.fftFilterAbove(FWHM, skip, weight);
    }


    public void resampleFrom(Observation2D map) {
        Referenced2D beam = getAntialiasingBeamImageFor(map);
        
        CoordinateTransform<Vector2D> toSourceIndex = getIndexTransformTo(map);
        
        resampleFrom(map, toSourceIndex, beam, weight);        
        getExposureImage().resampleFrom(map.getExposures(), toSourceIndex, beam, weight);
        getWeightImage().resampleFrom(map.getWeights(), toSourceIndex, beam, null);
        
        copyProcessingFrom(map);
    }

    @Override
    public void resampleFrom(Map2D map, final Values2D externalWeight) {
        if(map instanceof Observation2D) { resampleFrom((Observation2D) map); }
        else throw new UnsupportedOperationException(getClass().getSimpleName() + " cannot be resampled from type " + map.getClass().getSimpleName()); 
    }

    public void clean() { clean(0.1, 1.0, null); }


    public void clean(double gain, double significanceThreshold, Gaussian2D replacementPSF) {
        Gaussian2D psf = getImageBeam();

        final Data2D s2n = getSignificance();
        final Referenced2D beam = psf.getBeam(getGrid());
        final Image2D cleanS2N = (Image2D) s2n.clean(beam, beam.getReferenceIndex(), gain, significanceThreshold);

        if(replacementPSF == null) {
            replacementPSF = psf.copy();
            replacementPSF.scale(0.5);
        }

        replacementPSF.deconvolveWith(getPixelSmoothing());

        // Smooth to replacementResolution;
        cleanS2N.smooth(replacementPSF.getBeam(getGrid()));

        cleanS2N.new Fork<Void>() {
            @Override
            protected void process(int i, int j) {
                if(isValid(i,j)) {
                    set(i, j, weightAt(i, j) * (cleanS2N.get(i, j).doubleValue() + s2n.get(i, j).doubleValue()));
                }
            }
        }.process();

        getWeightImage().scale(1.0 + getSmoothingBeam().getArea() / replacementPSF.getArea());

        setSmoothingBeam(replacementPSF);
    }

    
 



    @Override
    public Object getTableEntry(String name) {
        if(name.equals("depth")) return Math.sqrt(1.0 / getWeights().getMean().value()) / getUnit().value();
        return super.getTableEntry(name);
    }
    
    @Override
    public ArrayList<BasicHDU<?>> getHDUs(Class<? extends Number> dataType) throws FitsException {   
        ArrayList<BasicHDU<?>> hdus = super.getHDUs(dataType);
  
        BasicHDU<?> hdu = getExposures().createPrimaryHDU(dataType);
        FitsToolkit.setName(hdu, "Exposure");
        editHeader(hdu.getHeader());
        hdus.add(hdu);
        
        hdu = getNoise().createPrimaryHDU(dataType);
        editHeader(hdu.getHeader());
        FitsToolkit.setName(hdu, "Noise");
        hdus.add(hdu);

        hdu = getSignificance().createPrimaryHDU(dataType);
        FitsToolkit.setName(hdu, "S/N");
        editHeader(hdu.getHeader());
        hdus.add(hdu);

        return hdus;
    }


    /**
     * Tries to assemble to signal / weight / exposure planes from the supplied HDUs
     * The signal is set to the content of the first ImageHDU. Successive Image HDUs are
     * then used based on EXTNAME to supply weights, rms noise, noise variance, or exposure time
     * information. The method returns true as soon all the required planes have been populated,
     * or false, if all HDUs were considered but not all planes were populated.
     * 
     * 
     * @param hdu
     * @return true if all image planes were successfully populated...
     * @throws Exception
     */
    @Override
    public boolean read(BasicHDU<?>[] hdu) throws Exception {
        int hasTypes = 0;
        int completeTypes = ComponentType.SIGNAL.mask() | ComponentType.WEIGHT.mask() | ComponentType.EXPOSURE.mask();

        Map<String, Unit> localUnits = null;

        for(int i=0; i<hdu.length; i++) {
            if(!(hdu[i] instanceof ImageHDU)) continue;

            // Set the data to the first image HDU...
            if(hasTypes == 0) {
                readData((ImageHDU) hdu[i]);
                Util.detail(this, "Assuming HDU[" + i + "] provides: " + ComponentType.SIGNAL.description());
                hasTypes |= ComponentType.SIGNAL.mask();
                continue;
            }

            // Try to set the weight & exposure
            ComponentType hduType = ComponentType.guessType(hdu[i].getHeader().getStringValue("EXTNAME"));
           
            if(hduType == ComponentType.UNKNOWN) continue;
            
            // If we already found a HDU of that type, then do nothing...
            if((hasTypes & hduType.mask()) != 0) continue;
            
            Util.detail(this, "Assuming HDU[" + i + "] provides: " + hduType.description());
            
            Image2D image = Image2D.createType(Double.class);
            image.read((ImageHDU) hdu[i], localUnits); 

            hasTypes |= setImage(image, hduType).mask();

            if(hasTypes == completeTypes) return true;
        }  

        return false;
    }


    public ComponentType setImage(final Image2D image, ComponentType type) {
        if(!conformsTo(image.sizeX(), image.sizeY())) throw new NonConformingException("Image size mismatch");

        switch(type) {
        case SIGNAL: setImage(image); return ComponentType.SIGNAL;
        case WEIGHT: setWeightImage(image); return ComponentType.WEIGHT;
        case EXPOSURE: setExposureImage(image); return ComponentType.EXPOSURE;
        case NOISE: 
            if(!weight.conformsTo(sizeX(), sizeY())) getWeightImage().setSize(sizeX(), sizeY());
            weight.new Fork<Void>() {
                @Override
                protected void process(int i, int j) {
                    double noise = image.get(i,j).doubleValue();
                    weight.set(i, j, 1.0 / (noise * noise));
                }      
            }.process();
            return ComponentType.WEIGHT;
        case VARIANCE: 
            if(!weight.conformsTo(sizeX(), sizeY())) getWeightImage().setSize(sizeX(), sizeY());
            weight.new Fork<Void>() {
                @Override
                protected void process(int i, int j) {
                    weight.set(i, j, 1.0 / image.get(i,j).doubleValue());
                }      
            }.process();
            return ComponentType.WEIGHT;
        case S2N: 
            if(!weight.conformsTo(sizeX(), sizeY())) getWeightImage().setSize(sizeX(), sizeY());
            weight.new Fork<Void>() {
                @Override
                protected void process(int i, int j) {
                    double noise = get(i, j).doubleValue() / image.get(i,j).doubleValue();
                    weight.set(i, j, 1.0 / (noise * noise));
                }      
            }.process();
            return ComponentType.WEIGHT;
        case UNKNOWN: 
            return type;
        }

        return ComponentType.UNKNOWN;
    }


}
