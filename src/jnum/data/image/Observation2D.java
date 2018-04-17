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

package jnum.data.image;

import java.util.ArrayList;
import java.util.Map;

import jnum.NonConformingException;
import jnum.Unit;
import jnum.Util;
import jnum.data.IndexedObservations;
import jnum.data.Observations;
import jnum.data.RegularData;
import jnum.data.Transforming;
import jnum.data.WeightedPoint;
import jnum.data.image.overlay.Flagged2D;
import jnum.data.image.overlay.Overlay2D;
import jnum.data.image.overlay.Referenced2D;
import jnum.fits.FitsToolkit;
import jnum.math.Vector2D;
import nom.tam.fits.BasicHDU;
import nom.tam.fits.FitsException;
import nom.tam.fits.ImageHDU;

public class Observation2D extends Map2D implements Observations<Data2D>, IndexedObservations<Index2D> {
    /**
     * 
     */
    private static final long serialVersionUID = -5595106427134511945L;

    private Flagged2D weight;
    private Flagged2D exposure;
   
    public boolean isZeroWeightValid = false;
    
    public Observation2D(Class<? extends Number> dataType, int flagType) {
        this(dataType, dataType, flagType);
    }
   
    public Observation2D(Class<? extends Number> dataType, Class<? extends Number> weightType, int flagType) {
        super(dataType, flagType); 
        setWeightImage(Image2D.createType(weightType));
        setExposureImage(Image2D.createType(weightType));
        
    }

    @Override
    protected void init() {    
        super.init();
        weight = new Flagged2D();
        exposure = new Flagged2D();
    }
     
    @Override
    public int hashCode() { 
        int hash = super.hashCode();
        hash ^= weight.hashCode();
        hash ^= exposure.hashCode();
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
    protected ObsProperties getPropertiesInstance() { return new ObsProperties(); }

    @Override
    public ObsProperties getProperties() { return (ObsProperties) super.getProperties(); }
    
    @Override
    public Observation2D clone() {
        Observation2D clone = (Observation2D) super.clone();
        
        // Treat weight and exposure as if their fields were fields at the root of this object...
        // I.e. make weight and exposure independent in clones always (otherwise, bad things may happen...)
        clone.weight = new Flagged2D(getFlags());
        if(getWeightImage() != null) clone.setWeightImage(getWeightImage());
        
        clone.exposure = new Flagged2D(getFlags());
        if(getExposureImage() != null) clone.setExposureImage(getExposureImage());
        
        return clone;
    }
    
    @Override
    public Observation2D copy() { return (Observation2D) super.copy(); }
    
    @Override
    public Observation2D copy(boolean withContents) {
        Observation2D copy = (Observation2D) super.copy(withContents);  
        
        copy.weight.setFlags(copy.getFlags());
        if(getWeightImage() != null) copy.setWeightImage(getWeightImage().copy(withContents));
        
        copy.exposure.setFlags(copy.getFlags());
        if(getExposureImage() != null) copy.setExposureImage(getExposureImage().copy(withContents));
        
        return copy;
    }

    @Override
    public String toString(int i, int j) {
        return super.toString(i, j) + " weight=" + Util.S3.format(weightAt(i, j)) + 
                " exp=" + Util.S3.format(exposureAt(i, j));
    }
    
    @Override
    public boolean isValid(int i, int j) {
        if(!super.isValid(i, j)) return false;
        final double w = weight.get(i, j).doubleValue();
        if(Double.isNaN(w)) return false;
        return isZeroWeightValid ? w >= 0.0 : w > 0.0;
    }
    
    @Override
    public void clear(int i, int j) {
        super.clear(i, j);
        getWeightImage().clear(i, j);
        getExposureImage().clear(i, j);
    }
    

    @Override
    public void destroy() {
        super.destroy();
        getWeightImage().destroy();
        getExposureImage().destroy();
    }

    
    @Override
    public void setFlags(Flag2D flags) {
        super.setFlags(flags);
        weight.setFlags(flags);
        exposure.setFlags(flags);
    }

    @Override
    public Data2D getWeights() { 
        return weight; 
    }
    
    public Image2D getWeightImage() {
        return (Image2D) weight.getBasis();
    }

    public void setWeightImage(Image2D image) { 
        weight.setBasis(image == null ? Image2D.createType(getElementType()) : image); 
        claim(weight);
        claim(getWeightImage());
    }

    @Override
    public Data2D getExposures() { 
        return exposure;
    }
    
    public Image2D getExposureImage() {
        return (Image2D) exposure.getBasis();
    }

    public void setExposureImage(Image2D image) {  
        exposure.setBasis(image == null ? Image2D.createType(getElementType()) : image); 
        claim(exposure);
        claim(getExposureImage());
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
        getWeightImage().setSize(sizeX, sizeY);
        getExposureImage().setSize(sizeX, sizeY);
    }


    @Override
    public void setUnit(Unit u) {
        super.setUnit(u);
        
        if(weight != null) {
            weight.setUnit(u);
            if(getWeightImage() != null) getWeightImage().setUnit(u);
        }
        if(exposure != null) {
            exposure.setUnit(u);
            if(getExposureImage() != null) getExposureImage().setUnit(u);
        }      
    }

    
    @Override
    public void setParallel(int n) {
        super.setParallel(n);
        
        if(weight != null) {
            weight.setParallel(n);
            if(getWeightImage() != null) getWeightImage().setParallel(n);
        }
        if(exposure != null) {
            exposure.setParallel(n);
            if(getExposureImage() != null) getExposureImage().setParallel(n);
        }
    }

    @Override
    public boolean isConsistent() {
        if(!super.isConsistent()) return false;
        
        if(weight == null) return false;
        if(exposure == null) return false;
        
        if(!weight.conformsTo(getImage())) return false;
        if(!exposure.conformsTo(getImage())) return false;
        return true;
    }


    @Override
    public String diagnoseInconsistency() {
        String value = super.diagnoseInconsistency();
        if(value != null) return value;

        if(weight == null) return "null weight";
        if(exposure == null) return "null exposure";
        
        if(!weight.conformsTo(getImage())) return "weight size mismatch";
        if(!exposure.conformsTo(getImage())) return "exposure size mismatch";
        
        return null;

    }
    
    @Override
    public final double weightAt(Index2D index) {
        return noiseAt(index.i(), index.j());
    }

    public double weightAt(int i, int j) {
        return weight.get(i, j).doubleValue();
    }
    
    @Override
    public final void setWeightAt(Index2D index, double value) {
       setWeightAt(index.i(), index.j(), value);
    }

    public void setWeightAt(int i, int j, double value) {
        weight.getBasis().set(i, j, value);
    }

    @Override
    public final double exposureAt(Index2D index) {
        return exposureAt(index.i(), index.j());
    }
    
    public double exposureAt(int i, int j) {
        return exposure.get(i, j).doubleValue();
    }

    public void setExposureAt(int i, int j, double value) {
        exposure.getBasis().set(i, j, value);
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

  
    
    public void accumulate(final Observation2D image, final double weight) {
        new Fork<Void>() {
            @Override
            protected void process(int i, int j) {
                if(image.isValid(i, j)) accumulateAt(i, j, image.get(i, j).doubleValue(), 1.0, weight * image.weightAt(i, j), image.exposureAt(i, j));
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
        getProperties().noiseRescaledBy(1.0 / Math.sqrt(weightCorrection)) ;
    }


    public void unscaleWeights() {
        double noiseRescale = getProperties().getNoiseRescale();
        getWeightImage().scale(1.0/(noiseRescale * noiseRescale));
        getProperties().setNoiseRescale(1.0);
    }

    
    @Override
    public WeightedPoint getMean() { return getWeightedMean(getWeights()); }

    @Override
    public WeightedPoint getMedian() { return getWeightedMedian(getWeights()); }
    
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
        setExposureImage((Image2D) exposure.getSmoothed(beam, refIndex, weight, null));
        setWeightImage(smoothWeights);

        getProperties().addSmoothing(Gaussian2D.getEquivalent(beam, getGrid().getResolution()));
    }

    @Override
    public void fastSmooth(RegularData<Index2D, Vector2D> beam, Vector2D refIndex, Index2D step) {
        Image2D smoothWeights = getWeightImage().copy(false);
        
        setImage((Image2D) getFastSmoothed(beam, refIndex, step, weight, smoothWeights));
        setExposureImage((Image2D) exposure.getFastSmoothed(beam, refIndex, step, weight, null));
        setWeightImage(smoothWeights);
      
        getProperties().addSmoothing(Gaussian2D.getEquivalent(beam, getGrid().getResolution()));
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

        Transforming<Vector2D> toSourceIndex = getIndexTransformTo(map);

        getImage().resampleFrom(map.getImage(), toSourceIndex, beam, getWeights());
        getExposureImage().resampleFrom(map.getExposures(), toSourceIndex, beam, getWeights());
        getWeightImage().resampleFrom(map.getWeights(), toSourceIndex, beam, null);

        getProperties().copyProcessingFrom(map.getProperties());
    }

    @Override
    public void resampleFrom(Map2D map, final Values2D externalWeight) {
        if(map instanceof Observation2D) { resampleFrom((Observation2D) map); }
        else throw new UnsupportedOperationException(getClass().getSimpleName() + " cannot be resampled from type " + map.getClass().getSimpleName()); 
    }

    public void clean() { clean(0.1, 1.0, null); }


    public void clean(double gain, double significanceThreshold, Gaussian2D replacementPSF) {
        Gaussian2D psf = getProperties().getImageBeam();

        final Data2D s2n = getSignificance();
        final Referenced2D beam = psf.getBeam(getGrid());
        final Image2D cleanS2N = (Image2D) s2n.clean(beam, beam.getReferenceIndex(), gain, significanceThreshold);

        if(replacementPSF == null) {
            replacementPSF = psf.copy();
            replacementPSF.scale(0.5);
        }

        replacementPSF.deconvolveWith(getProperties().getPixelSmoothing());

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

        getWeightImage().scale(1.0 + getSmoothing().getArea() / replacementPSF.getArea());

        getProperties().setSmoothingBeam(replacementPSF);

    }




    @Override
    public Object getTableEntry(String name) {
        if(name.equals("depth")) return Math.sqrt(1.0 / weight.getMean().value()) / getUnit().value();
        return super.getTableEntry(name);
    }
    
    @Override
    public ArrayList<BasicHDU<?>> getHDUs(Class<? extends Number> dataType) throws FitsException {   
        ArrayList<BasicHDU<?>> hdus = super.getHDUs(dataType);
  
        BasicHDU<?> hdu = exposure.createHDU(dataType);
        FitsToolkit.setName(hdu, "Exposure");
        editHeader(hdu.getHeader());
        hdus.add(hdu);
        
        hdu = getNoise().createHDU(dataType);
        editHeader(hdu.getHeader());
        FitsToolkit.setName(hdu, "Noise");
        hdus.add(hdu);

        hdu = getSignificance().createHDU(dataType);
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
        int completeTypes = TYPE_SIGNAL | TYPE_WEIGHT | TYPE_EXPOSURE;

        Map<String, Unit> localUnits = null;

        for(int i=0; i<hdu.length; i++) {
            if(!(hdu[i] instanceof ImageHDU)) continue;

            // Set the data to the first image HDU...
            if(hasTypes == 0) {
                readData((ImageHDU) hdu[i]);
                Util.detail(this, "Assuming HDU[" + i + "] provides: " + typeNames[TYPE_SIGNAL]);
                hasTypes |= TYPE_SIGNAL;
                continue;
            }

            // Try to set the weight & exposure
            int hduType = guessType(hdu[i].getHeader().getStringValue("EXTNAME"));
           
            if(hduType == TYPE_UNKNOWN) continue;
            
            // If we already found a HDU of that type, then do nothing...
            if((hasTypes & hduType) != 0) continue;
            
            Util.detail(this, "Assuming HDU[" + i + "] provides: " + typeNames[hduType]);
            
            Image2D image = Image2D.createType(Double.class);
            image.read((ImageHDU) hdu[i], localUnits); 

            hasTypes |= setImage(image, hduType);

            if(hasTypes == completeTypes) return true;
        }  

        return false;
    }


    public int setImage(final Image2D image, int type) {
        if(type == TYPE_SIGNAL) {
            setImage(image);
            return TYPE_SIGNAL;
        }

        if(!conformsTo(image.sizeX(), image.sizeY())) throw new NonConformingException("Image size mismatch");

        switch(type) {
        case TYPE_WEIGHT: setWeightImage(image); return TYPE_WEIGHT;
        case TYPE_EXPOSURE: setExposureImage(image); return TYPE_EXPOSURE;
        case TYPE_NOISE: 
            if(!weight.conformsTo(sizeX(), sizeY())) getWeightImage().setSize(sizeX(), sizeY());
            weight.new Fork<Void>() {
                @Override
                protected void process(int i, int j) {
                    double noise = image.get(i,j).doubleValue();
                    weight.set(i, j, 1.0 / (noise * noise));
                }      
            }.process();
            return TYPE_WEIGHT;
        case TYPE_VARIANCE: 
            if(!weight.conformsTo(sizeX(), sizeY())) getWeightImage().setSize(sizeX(), sizeY());
            weight.new Fork<Void>() {
                @Override
                protected void process(int i, int j) {
                    weight.set(i, j, 1.0 / image.get(i,j).doubleValue());
                }      
            }.process();
            return TYPE_WEIGHT;
        case TYPE_S2N: 
            if(!weight.conformsTo(sizeX(), sizeY())) getWeightImage().setSize(sizeX(), sizeY());
            weight.new Fork<Void>() {
                @Override
                protected void process(int i, int j) {
                    double noise = get(i, j).doubleValue() / image.get(i,j).doubleValue();
                    weight.set(i, j, 1.0 / (noise * noise));
                }      
            }.process();
            return TYPE_WEIGHT;
        }

        return TYPE_UNKNOWN;
    }


    public static int guessType(String name) {
        name = name.toLowerCase();     

        if(name.contains("weight")) return TYPE_WEIGHT;
      
        // "Signal-to-noise" and variants...
        if(name.contains("to-noise")) return TYPE_S2N;
        if(name.contains("to noise")) return TYPE_S2N;
        if(name.contains("/noise")) return TYPE_S2N;
        if(name.contains("/ noise")) return TYPE_S2N;
       
        if(name.contains("noise")) return TYPE_NOISE;               // noise weight -> weight
        if(name.contains("rms")) return TYPE_NOISE;
        if(name.contains("error")) return TYPE_NOISE;
        if(name.contains("uncertainty")) return TYPE_NOISE;
        if(name.contains("sensitivity")) return TYPE_NOISE;
        if(name.contains("depth")) return TYPE_NOISE;
        if(name.contains("scatter")) return TYPE_NOISE;
        if(name.contains("sigma")) return TYPE_NOISE;

        if(name.contains("variance")) return TYPE_VARIANCE;
        if(name.equals("var")) return TYPE_VARIANCE;
        
        if(name.contains("s/n")) return TYPE_S2N;
        if(name.contains("s2n")) return TYPE_S2N;
        
        if(name.contains("coverage")) return TYPE_EXPOSURE;         // depth coverage -> noise
        if(name.contains("time")) return TYPE_EXPOSURE;
        if(name.contains("exposure")) return TYPE_EXPOSURE;

        if(name.contains("signal")) return TYPE_SIGNAL;
        if(name.contains("flux")) return TYPE_SIGNAL;
        if(name.contains("intensity")) return TYPE_SIGNAL;
        if(name.contains("brightness")) return TYPE_SIGNAL; 

        return TYPE_UNKNOWN;
    }

   
    public final static int TYPE_UNKNOWN = 0;
    public final static int TYPE_SIGNAL = 1<<1;
    public final static int TYPE_WEIGHT = 1<<2;
    public final static int TYPE_EXPOSURE = 1<<3;
    public final static int TYPE_NOISE = 1<<4;
    public final static int TYPE_VARIANCE = 1<<5;
    public final static int TYPE_S2N = 1<<6;
    
    public static String[] typeNames = { "Unknown", "Signal", "Weight", "Exposure", "Noise", "Variance", "S/N" };


   

}
