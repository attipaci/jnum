/*******************************************************************************
 * Copyright (c) 2018 Attila Kovacs <attila[AT]sigmyne.com>.
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

package jnum.data;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jnum.ExtraMath;
import jnum.PointOp;
import jnum.Unit;
import jnum.Util;
import jnum.Verbosity;
import jnum.fits.FitsToolkit;
import jnum.math.Range;
import jnum.parallel.ParallelObject;
import jnum.parallel.ParallelPointOp;
import jnum.parallel.ParallelTask;
import jnum.text.TableFormatter;
import jnum.util.CompoundUnit;
import nom.tam.fits.BasicHDU;
import nom.tam.fits.Fits;
import nom.tam.fits.FitsException;
import nom.tam.fits.FitsFactory;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;
import nom.tam.fits.ImageHDU;
import nom.tam.util.Cursor;

public abstract class Data<IndexType extends Index<IndexType>>
extends ParallelObject implements Verbosity, IndexedValues<IndexType>, Iterable<Number>, TableFormatter.Entries {

    static {
        Locale.setDefault(Locale.US);
    }
    
   
    private Number blankingValue;

    private boolean isVerbose;

    private int interpolationType;    
  
    private Unit unit;  

    private ArrayList<String> history;
    
    private Hashtable<String, Unit> localUnits;
    



    
    private boolean logNewData;
 
    
    public Data() { 
        setVerbose(false);
        setBlankingValue(Double.NaN);
        history = new ArrayList<>();
        setDefaultUnit();
        logNewData = true;
    }
    
    @Override
    public int hashCode() {
        int hash = super.hashCode() ^ getInterpolationType() ^ getBlankingValue().hashCode(); 
        if(getUnit() != null) hash ^= getUnit().hashCode();
        return hash;
    }
    
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null) return false;
        if(!getClass().isAssignableFrom(o.getClass())) return false;
        
        @SuppressWarnings("unchecked")
        Data<IndexType> data = (Data<IndexType>) o;
             
        if(getInterpolationType() != data.getInterpolationType()) return false;
        if(!Util.equals(getBlankingValue(), data.getBlankingValue())) return false;
        if(!Util.equals(getUnit(), data.getUnit())) return false;
         
        return contentEquals(data);
    }
    

    public boolean contentEquals(final Data<IndexType> data) {   
        if(!getSizeString().equals(data.getSizeString())) return false;
        
        PointOp.Simple<IndexType> comparison = new PointOp.Simple<IndexType>() {
            @Override
            public void process(IndexType index) {
                if(isValid(index) != data.isValid(index)) exception = new IllegalStateException("Mismatched validity");
                if(!get(index).equals(data.get(index))) exception = new IllegalStateException("Mismatched content");
            }
        };
        
        loop(comparison);
        if(comparison.exception != null) return false;

        return true;
    }
  
    
    @SuppressWarnings("unchecked")
    @Override
    public Data<IndexType> clone() {
        Data<IndexType> clone = (Data<IndexType>) super.clone();
        if(history != null) clone.history = (ArrayList<String>) history.clone();
        
        if(localUnits != null) {
            clone.localUnits = new Hashtable<>(localUnits.size());
            clone.localUnits.putAll(localUnits);
        }
         
        clone.logNewData = true;
        
        return clone;
    }
   
    
    
    @Override
    public final boolean isVerbose() { return isVerbose; }

    @Override
    public void setVerbose(boolean value) { isVerbose = value; }


    public List<String> getHistory() { return history; }

    public void keepHistory(boolean value) {
        if(!value) history = null;
        else if(history == null) history = new ArrayList<>();
        
    }
    
    public void clearHistory() {
        if(history != null) history.clear();
    }
    
    public void addHistory(String entry) {
        if(history == null) return;
        history.add(entry); 
        if(isVerbose()) Util.info(this, entry);
    }

    public void setHistory(List<String> entries) {
        history = new ArrayList<>(entries.size());
        history.addAll(entries);
    }

    
    

    public void addLocalUnit(Unit u) {
        if(localUnits == null) localUnits = new Hashtable<>();
        u.registerTo(localUnits);
    }

    public void addLocalUnit(Unit u, String altNames) {
        if(localUnits == null) localUnits = new Hashtable<>();
        u.registerTo(localUnits, altNames);
        addLocalUnit(u);
    }


    public Hashtable<String, Unit> getLocalUnits() { return localUnits; }


    

    public final Number getBlankingValue() {
        return blankingValue;
    }

    
    public final void setBlankingValue(final Number value) {
         
   
        // Replace old blanking values in image with the new value, as needed...
        if(blankingValue != null) if(!blankingValue.equals(value)) {
            smartFork(new ParallelPointOp.Simple<IndexType>() {

                @Override
                public void process(IndexType index) {
                    if(blankingValue.equals(get(index))) set(index, value);
                }
                
            });
        }
        
        blankingValue = (value == null) ? Double.NaN : value;    

    }

    

    
    @Override
    public Number getLowestCompareValue() { return Long.MIN_VALUE; }
    
    @Override
    public Number getHighestCompareValue() { return Long.MAX_VALUE; }
    
    
    @Override
    public int compare(Number a, Number b) {
        if(a.longValue() == b.longValue()) return 0;
        return a.longValue() < b.longValue() ? -1 : 1;
    }
   

  

    public boolean isValid(Number value) {
        return !value.equals(blankingValue);
    }


    public int getInterpolationType() { return interpolationType; }

    public void setInterpolationType(int value) { this.interpolationType = value; }

    
    
    protected void setDefaultUnit() { setUnit(Unit.unity); }
    
    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit u) { this.unit = u; }
    
    
    public void setUnit(String spec) {
        setUnit(spec, getLocalUnits());
    }

    public void setUnit(String spec, Map<String, Unit> extraBaseUnits) {
        CompoundUnit u = new CompoundUnit();
        u.parse(spec, extraBaseUnits);
        setUnit(u); 
    }
    
    
    protected void silentNextNewData() {
        logNewData = false;
    }
    
    protected void recordNewData(String detail) {
        if(!logNewData) logNewData = false;
        else {
            clearHistory();
            addHistory("set new image " + getSizeString() + (detail == null ? "" : " " + detail));
        }
    }
     
    @Override
    public abstract DataCrawler<Number> iterator();
 
    public final <ReturnType> ReturnType loop(PointOp<IndexType, ReturnType> op) {
        return loop(op, getIndexInstance(), getSize());
    }
    
    public final <ReturnType> ReturnType loopValid(PointOp<Number, ReturnType> op) {
        return loopValid(op, getIndexInstance(), getSize());
    }
    
    public abstract <ReturnType> ReturnType loop(PointOp<IndexType, ReturnType> op, IndexType from, IndexType to);
    
    public abstract <ReturnType> ReturnType loopValid(PointOp<Number, ReturnType> op, IndexType from, IndexType to);
    
    
    public final <ReturnType> ReturnType fork(final ParallelPointOp<IndexType, ReturnType> op) {
        return fork(op, getIndexInstance(), getSize());
    }
    
    public final <ReturnType> ReturnType forkValid(final ParallelPointOp<Number, ReturnType> op) {
        return forkValid(op, getIndexInstance(), getSize());
    }
    
    public abstract <ReturnType> ReturnType fork(final ParallelPointOp<IndexType, ReturnType> op, IndexType from, IndexType to);
    
    public abstract <ReturnType> ReturnType forkValid(final ParallelPointOp<Number, ReturnType> op, IndexType from, IndexType to);
   
    public final <ReturnType> ReturnType smartFork(final ParallelPointOp<IndexType, ReturnType> op) {
        return smartFork(op, getIndexInstance(), getSize());
    }
    
    public final <ReturnType> ReturnType smartFork(final ParallelPointOp<IndexType, ReturnType> op, IndexType from, IndexType to) {
        if(getParallel() < 2) return loop(op, from, to);
        IndexType span = getIndexInstance();
        span.setDifference(to, from);
        if(span.getVolume() * (2 + op.numberOfOperations()) < 2 * ParallelTask.minExecutorBlockSize) return loop(op, from, to);
        return fork(op, from, to);
    }
    
    public final <ReturnType> ReturnType smartForkValid(final ParallelPointOp<Number, ReturnType> op) {
        return smartForkValid(op, getIndexInstance(), getSize());
    }
    
    public final <ReturnType> ReturnType smartForkValid(final ParallelPointOp<Number, ReturnType> op, IndexType from, IndexType to) {
        if(getParallel() < 2) return loopValid(op, from, to);
        IndexType span = getIndexInstance();
        span.setDifference(to, from);
        if(span.getVolume() * (2 + op.numberOfOperations()) < 2 * ParallelTask.minExecutorBlockSize) return loopValid(op, from, to);
        return forkValid(op, from, to);  
    }
    
    
    /**
     * 
     * @return a instance of the index type, initialized to zeroes.
     */
     
    @Override
    public final boolean conformsTo(IndexedValues<IndexType> data) { return conformsTo(data.getSize()); }

    @Override
    public final boolean conformsTo(IndexType size) {
        return getSize().equals(size);
    }
    
    public abstract Number getValid(final IndexType index, final Number defaultValue);
    
    public abstract boolean isValid(IndexType index);
    
    public abstract void discard(IndexType index);
    
    public abstract String toString(IndexType index);
   
   
    
    public final void clear() {
        smartFork(new ParallelPointOp.Simple<IndexType>() {
            @Override
            public void process(IndexType index) { clear(index); }
        });
        clearHistory();
        addHistory("clear " + getSizeString());
    }
    
    public final void fill(final Number value) {
        smartFork(new ParallelPointOp.Simple<IndexType>() {
            @Override
            public void process(IndexType index) { set(index, value); }
        });
        clearHistory();
        addHistory("fill " + getSizeString() + " with " + value);
    }
    
    public final void add(final Number value) {
        smartFork(new ParallelPointOp.Simple<IndexType>() {
            @Override
            public void process(IndexType index) { add(index, value); }
        });
        addHistory("add " + value);
    }
    
    public void scale(final double factor) {
        smartFork(new ParallelPointOp.Simple<IndexType>() {
            @Override
            public void process(IndexType index) { scale(index, factor); }
        });
        addHistory("scale by " + factor);
    }
    
    public final void validate() {
        smartFork(new ParallelPointOp.Simple<IndexType>() {
            @Override
            public void process(IndexType index) { if(!isValid(index)) discard(index); }
        });
        addHistory("validate");
    }
    

    public void validate(final Validating<IndexType> validator) {
        smartFork(new ParallelPointOp.Simple<IndexType>() {
            @Override
            public void process(IndexType index) { if(!validator.isValid(index)) validator.discard(index); }
        });
        addHistory("validate via " + validator);
    }
    
    public final void discardRange(final Range discard) {
        smartFork(new ParallelPointOp.Simple<IndexType>() {
            @Override
            public void process(IndexType index) { 
                if(discard.contains(get(index).doubleValue())) discard(index);
            }
        });
    }
    
    public final void restrictRange(final Range keep) {
        smartFork(new ParallelPointOp.Simple<IndexType>() {
            @Override
            public void process(IndexType index) { 
                if(!keep.contains(get(index).doubleValue())) discard(index);
            }
        });
    }
   
    public final void paste(final Data<IndexType> source, boolean report) {
        if(source == this) return;

        source.smartFork(new ParallelPointOp.Simple<IndexType>() {
            @Override
            public void process(IndexType index) {
                if(source.isValid(index)) set(index, source.get(index));
                else discard(index); 
            }     
        });

        if(report) addHistory("pasted new content: " + source.getSizeString());
    }

    
    
    public abstract void despike(double level);
      
    public abstract String getInfo();
    
    
      
    public int countPoints() {
        return smartForkValid(new ParallelPointOp.ElementCount<Number>()).intValue();
    }
    
    public Number getMin() {
        return smartForkValid(new ParallelPointOp<Number, Number>() {
            Number min;
            
            @Override
            protected void init() {
                min = getHighestCompareValue();
            }
            
            @Override
            public final void process(Number point) {
                if(compare(point, min) < 0) min = point;
            }

            @Override
            public Number getResult() {
                return min;
            }
    
            @Override
            public void mergeResult(Number localMin) {
                if(compare(localMin, min) < 0) min = localMin; 
            }        
        });
    }

    public Number getMax() {
        return smartForkValid(new ParallelPointOp<Number, Number>() {
            Number max;
            
            @Override
            protected void init() {
                max = getLowestCompareValue();
            }
            
            @Override
            public final void process(Number point) {
                if(compare(point, max) > 0) max = point;
            }

            @Override
            public Number getResult() {
                return max;
            }
    
            @Override
            public void mergeResult(Number localMax) {
                if(compare(localMax, max) > 0) max = localMax; 
            }    
        });
    }
    
    public Range getRange() {
        return smartForkValid(new ParallelPointOp<Number, Range>() {
            Range range;
            
            @Override
            protected void init() {
                range = new Range();
            }
            
            @Override
            public final void process(Number point) {
                range.include(point.doubleValue());
            }

            @Override
            public Range getResult() {
                return range;
            } 
            
            @Override
            public void mergeResult(Range localRange) {
                range.include(localRange);
            }  
        });
    }
    


    /**
     * Return the index of the highest value.
     * If there are multiple points with the same maximum value, it is undefined (and possibly random) whose 
     * index is returned.
     * 
     * 
     * @return the index corresponding to the highest value.
     */
    public final IndexType indexOfMin() { 
        return smartFork(new ParallelPointOp<IndexType, IndexType>() {
            private IndexType minIndex;
            private Number min;
          
            @Override
            protected void init() {
                minIndex = null;
                min = getHighestCompareValue();
            }

            @Override
            public void process(IndexType index) {
                if(!isValid(index)) return;
                if(compare(get(index), min) >= 0) return;
                min = get(index);
                minIndex = copyOfIndex(index);
                
            }

            @Override
            public IndexType getResult() {
                return minIndex;
            }
            
            @Override
            public void mergeResult(IndexType localMinIndex) {
                if(minIndex == null) minIndex = localMinIndex;
                if(localMinIndex == null) return;
                if(compare(get(localMinIndex), get(minIndex)) < 0) minIndex = localMinIndex; 
            }            
        });
    }
      
    
    /**
     * Return the index of the lowest value.
     * If there are multiple points with the same minimum value, it is undefined (and possibly random) whose 
     * index is returned.
     * 
     * 
     * @return the index corresponding to the lowest value.
     */
    public final IndexType indexOfMax() { 
        return smartFork(new ParallelPointOp<IndexType, IndexType>() {
            private IndexType maxIndex;
            private Number max;
          
            @Override
            protected void init() {
                maxIndex = null;
                max = getLowestCompareValue();
            }

            @Override
            public void process(IndexType index) {
                if(!isValid(index)) return;
                if(compare(get(index), max) <= 0) return;
                max = get(index);
                maxIndex = copyOfIndex(index);
                
            }

            @Override
            public IndexType getResult() {
                return maxIndex;
            }
            
            @Override
            public void mergeResult(IndexType localMaxIndex) {
                if(maxIndex == null) maxIndex = localMaxIndex;
                if(localMaxIndex == null) return;
                if(compare(get(localMaxIndex), get(maxIndex)) > 0) maxIndex = localMaxIndex; 
            }            
        });
    }
    
    
    /**
     * Return the index of the datum with the largest absolute deviation from zero.
     * Uses cast to double, so may not work perfectly on long types. If there are multiple points with the
     * same maximum value, it is undefined (and possibly random) whose index is returned.
     * 
     * 
     * @return the index corresponding to the largest absolute deviation from zero.
     */
    public final IndexType indexOfMaxDev() { 
        return smartFork(new ParallelPointOp<IndexType, IndexType>() {
            private IndexType maxIndex;
            private Number max;
          
            @Override
            protected void init() {
                maxIndex = null;
                max = getLowestCompareValue();
            }

            @Override
            public void process(IndexType index) {
                if(!isValid(index)) return;
                if(compare(get(index), max) <= 0) return;
                max = Math.abs(get(index).doubleValue());
                maxIndex = copyOfIndex(index);
                
            }

            @Override
            public IndexType getResult() {
                return maxIndex;
            }
            
            @Override
            public void mergeResult(IndexType localMaxIndex) {
                if(maxIndex == null) maxIndex = localMaxIndex;
                if(localMaxIndex == null) return;
                if(Math.abs(get(localMaxIndex).doubleValue()) > Math.abs(get(maxIndex).doubleValue())) maxIndex = localMaxIndex; 
            }            
        });
    }

    public final double level(boolean isRobust) {
        double level = isRobust ? getMedian().value() : getMean().value(); 
        add(-level);
        return level;
    }
    
    public WeightedPoint getMean() {
        return smartForkValid(new ParallelPointOp.Average<Number>() {
            @Override
            public final double getValue(Number point) {
                return point.doubleValue();
            }

            @Override
            public final double getWeight(Number point) {
                return 1.0;
            }  
        });
    }
    
    public final WeightedPoint getWeightedMean(final IndexedValues<IndexType> weights) {
        return smartFork(new ParallelPointOp.Average<IndexType>() {
            @Override
            public final double getValue(IndexType index) {
                return get(index).doubleValue();
            }

            @Override
            public final double getWeight(IndexType index) {
                if(!isValid(index)) return 0.0;
                return weights.get(index).doubleValue();
            }  
        });
    }
    
    public WeightedPoint getMedian() {
        final double[] temp = getValidSortingArray();
        if(temp.length == 0) return new WeightedPoint(Double.NaN, 0.0);
        return new WeightedPoint(Statistics.Inplace.median(temp, 0, temp.length), temp.length);      
    }
    
    public final WeightedPoint getWeightedMedian(final IndexedValues<IndexType> weights) {   
        final WeightedPoint[] temp = getValidSortingArray(weights);
        if(temp.length == 0) return new WeightedPoint(Double.NaN, 0.0);
        return Statistics.Inplace.median(temp, 0, temp.length);      
    }
    
    public double select(double fraction) {
        if(fraction == 0.0) return getMin().doubleValue();
        else if(fraction == 1.0) return getMax().doubleValue();
        
        final double[] temp = getValidSortingArray();
        if(temp.length == 0) return Double.NaN;
        return Statistics.Inplace.select(temp, fraction, 0, temp.length);
    }
    

    private double[] getValidSortingArray() {    
        final double[] sorter = new double[countPoints()];
        
        if(sorter.length == 0) return sorter;
        
        loopValid(new PointOp.Simple<Number>() {
            private int k;
            
            @Override
            protected void init() {
                k = 0;
            }
            
            @Override
            public void process(Number point) {
                sorter[k++] = point.doubleValue();
            }   
        });
           
        return sorter;
    }
    
    private WeightedPoint[] getValidSortingArray(final IndexedValues<IndexType> weights) {    
        final WeightedPoint[] sorter = new WeightedPoint[countPoints()];
         
        if(sorter.length == 0) return sorter;
        
        loop(new PointOp.Simple<IndexType>() {
            private int k;
            
            @Override
            protected void init() {
                k = 0;
            }
            
            @Override
            public void process(IndexType index) {
                if(!isValid(index)) return;
                sorter[k++] = new WeightedPoint(get(index).doubleValue(), weights.get(index).doubleValue());
            }   
        });
           
        return sorter;
    }
   
    public final double getRMS(boolean isRobust) {
        return isRobust ? getRobustRMS() : getRMS();
    }

    public final double getVariance(boolean isRobust) {
        return isRobust ? getRobustVariance() : getVariance();
    }
   
    public double getRMS() { return Math.sqrt(getVariance()); }
    
    public double getVariance() {
        return smartForkValid(new ParallelPointOp.Average<Number>() {
            @Override
            public final double getValue(Number point) {
                return point.doubleValue() * point.doubleValue();
            }

            @Override
            public final double getWeight(Number point) {
                return 1.0;
            }      
        }).value();     
    }

    
    public double getRobustRMS() { return Math.sqrt(getRobustVariance()); }
    
    public double getRobustVariance() {
        final double[] var = getValidSortingArray();
        if(var.length == 0) return Double.NaN;
        
        for(int i=var.length; --i >= 0; ) var[i] *= var[i];
        return Statistics.Inplace.median(var) / Statistics.medianNormalizedVariance;
    }

    
    public double getSum() {
        return smartForkValid(new ParallelPointOp.Sum<Number>() {
            @Override
            public final double getValue(Number point) {
                return point.doubleValue();
            }
        });
    }
    
    
    public double getAbsSum() {
        return smartForkValid(new ParallelPointOp.Sum<Number>() {
            @Override
            public final double getValue(Number point) {
               return point.doubleValue();
            }   
        });
    }
    
    
    public double getSquareSum() {  
        return smartForkValid(new ParallelPointOp.Sum<Number>() {
            @Override
            public final double getValue(Number point) {
                return point.doubleValue() * point.doubleValue();
            }        
        });      
    }
    
    
    public final void add(final Data<IndexType> data) {
        smartFork(new ParallelPointOp.Simple<IndexType>() {
            @Override
            public void process(IndexType index) {
                if(data.isValid(index)) add(index, data.get(index));
            }      
        });
        
        addHistory("added " + getClass().getSimpleName());
    }


    public final void addScaled(final Data<IndexType> data, final double scaling) {
        smartFork(new ParallelPointOp.Simple<IndexType>() {
            @Override
            public void process(IndexType index) {
                if(data.isValid(index)) add(index, scaling * data.get(index).doubleValue());
            }      
        });
       
        addHistory("added scaled " + getClass().getSimpleName() + " (" + scaling + "x).");
    }

    public final void subtract(final Data<IndexType> data) {
        addScaled(data, -1.0);
    }


  

    
    // TODO Make default method in Observations
    public void memCorrect(final IndexedValues<IndexType> model, final IndexedValues<IndexType> noise, final double lambda) {
        smartFork(new ParallelPointOp.Simple<IndexType>() {

         @Override
         public void process(IndexType index) {
             if(isValid(index)) {
                 final double noiseValue = noise.get(index).doubleValue();
                 final double target = model == null ? 0.0 : model.get(index).doubleValue();
                 final double memValue = ExtraMath.hypot(get(index).doubleValue(), noiseValue) / ExtraMath.hypot(target, noiseValue);
                 add(index, -Math.signum(get(index).doubleValue()) * lambda * noiseValue * Math.log(memValue));
             }
         }            
        });          
     }  
    
   
    public abstract Object getCore();
       
    public Fits createFits(Class<? extends Number> dataType) throws FitsException {
        FitsFactory.setLongStringsEnabled(true);
        FitsFactory.setUseHierarch(true);
        Fits fits = new Fits(); 
        
        ArrayList<BasicHDU<?>> hdus = getHDUs(dataType);
        for(int i=0; i<hdus.size(); i++) fits.addHDU(hdus.get(i));

        return fits;
    }
    
    public ArrayList<BasicHDU<?>> getHDUs(Class<? extends Number> dataType) throws FitsException {
        ArrayList<BasicHDU<?>> hdus = new ArrayList<>();
        hdus.add(createHDU(dataType));
        return hdus;
    }

    public final ImageHDU createHDU(Class<? extends Number> dataType) throws FitsException {  
        ImageHDU hdu = (ImageHDU) Fits.makeHDU(getFitsData(dataType));
        editHeader(hdu.getHeader());
        return hdu;
    }
    
   
    public abstract Object getFitsData(Class<? extends Number> dataType);
   
    
    protected void editHeader(Header header) throws HeaderCardException {
        Cursor<String, HeaderCard> c = FitsToolkit.endOf(header);
        Range range = getRange();
        
        if(!range.isEmpty()) {
            if(range.isLowerBounded()) c.add(new HeaderCard("DATAMIN", range.min() / getUnit().value(), "The lowest value in the image"));
            if(range.isUpperBounded()) c.add(new HeaderCard("DATAMAX", range.max() / getUnit().value(), "The highest value in the image"));
        }

        c.add(new HeaderCard("BZERO", 0.0, "Zeroing level of the image data"));
        c.add(new HeaderCard("BSCALE", 1.0, "Scaling of the image data"));

        if(getUnit() != null) c.add(new HeaderCard("BUNIT", getUnit().name(), "Data unit specification."));
        
        
        addHistory(header);
    }

    
    protected void parseHeader(Header header) {
        setUnit(header.containsKey("BUNIT") ? new Unit(header.getStringValue("BUNIT")) : Unit.unity);
    }



    protected void parseHistory(Header header) {
        setHistory(FitsToolkit.parseHistory(header));  
    }

    protected void addHistory(Header header) throws HeaderCardException {  
        if(getHistory() != null) FitsToolkit.addHistory(header, getHistory());
    }

    
    @Override
    public Object getTableEntry(String name) {
        if(name.equals("points")) return Integer.toString(countPoints());
        else if(name.equals("size")) return getSizeString();
        else if(name.equals("min")) return getMin().doubleValue();
        else if(name.equals("max")) return getMax().doubleValue();
        else if(name.equals("mean")) return getMean();
        else if(name.equals("median")) return getMedian();
        else if(name.equals("rms")) return getRMS(true);
        else return TableFormatter.NO_SUCH_DATA;
    }
   
    
    
    public abstract class AbstractLoop<ReturnType> {
        protected IndexType from, to;
        
        public AbstractLoop() { this(getIndexInstance(), getSize()); }
        
        public AbstractLoop(IndexType from, IndexType to) { 
            this.from = from;
            this.to = to;
        }
        
        public abstract ReturnType process();

        protected ReturnType getResult() { return null; }
    }

    public abstract class AbstractFork<ReturnType> extends Task<ReturnType> {
        protected IndexType from, to;
        
        public AbstractFork() { this(getIndexInstance(), getSize()); }
        
        public AbstractFork(IndexType from, IndexType to) { 
            this.from = from;
            this.to = to;
        }
    }
  
}
