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

package jnum.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import jnum.Unit;
import jnum.Util;
import jnum.Verbosity;
import jnum.fits.FitsToolkit;
import jnum.math.Range;
import jnum.parallel.ParallelObject;
import jnum.parallel.ParallelPointOp;
import jnum.parallel.ParallelTask;
import jnum.parallel.PointOp;
import jnum.text.TableFormatter;
import jnum.util.CompoundUnit;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;
import nom.tam.util.Cursor;

public abstract class Data<IndexType> extends ParallelObject implements Verbosity, Value,  Iterable<Number>, TableFormatter.Entries {

    private Number blankingValue;

    private boolean isVerbose;

    private int interpolationType;    
  
    private Unit unit;  

    private ArrayList<String> history;
    
    
    private boolean logNewData;
    
 
 
    
    public Data() {
        
        setVerbose(false);
        setBlankingValue(Double.NaN);
        setInterpolationType(SPLINE);
        history = new ArrayList<String>(); 
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
        if(!getClass().isAssignableFrom(o.getClass())) return false;
        
        @SuppressWarnings("unchecked")
        Data<IndexType> data = (Data<IndexType>) o;
             
        if(getInterpolationType() != data.getInterpolationType()) return false;
        if(!Util.equals(getBlankingValue(), data.getBlankingValue())) return false;
        if(!Util.equals(getUnit(), data.getUnit())) return false;
         
        return contentEquals(data);
    }
    
    public abstract boolean contentEquals(Data<IndexType> data);
    
    
    @SuppressWarnings("unchecked")
    @Override
    public Data<IndexType> clone() {
        Data<IndexType> clone = (Data<IndexType>) super.clone();
        if(history != null) clone.history = (ArrayList<String>) history.clone();
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
        else if(history == null) history = new ArrayList<String>();
        
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
        history = new ArrayList<String>(entries.size());
        history.addAll(entries);
    }


    public final Number getBlankingValue() {
        return blankingValue;
    }

    
    public void setBlankingValue(final Number value) {
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
        setUnit(spec, null);
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
 
    
    public abstract <ReturnType> ReturnType loopValid(PointOp<Number, ReturnType> op);
    
    public abstract <ReturnType> ReturnType forkValid(final ParallelPointOp<Number, ReturnType> op);
    
    public <ReturnType> ReturnType smartForkValid(final ParallelPointOp<Number, ReturnType> op) {
        if(capacity() * (2 + op.numberOfOperations()) > 2 * ParallelTask.minExecutorBlockSize) return loopValid(op);
        else return forkValid(op);  
    }
    
    public abstract int capacity();
    
    public abstract String getSizeString();
    
    public abstract Number get(IndexType index);
    
    public abstract void set(IndexType index, Number value);
    
    public abstract boolean isValid(IndexType index);
    
    public abstract void discard(IndexType index);
    
    public abstract void clear(IndexType index);
    
    public abstract void add(IndexType index, Number value);
    
    public abstract void scale(IndexType index, double factor);
    
    public abstract IndexType indexOfMin();
    
    public abstract IndexType indexOfMax();

    public abstract IndexType indexOfMaxDev();
    
      
    public int countPoints() {
        return smartForkValid(new ParallelPointOp.Count<Number>() {
            @Override
            public final double getCount(Number point) {
                return 1;
            }
        }).intValue();
    }
    
    public Number getMin() {
        return smartForkValid(new ParallelPointOp<Number, Number>() {
            Number min;
            
            @Override
            public void init() {
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
            public void init() {
                max = getHighestCompareValue();
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
            public void mergeResult(Number localMin) {
                if(compare(localMin, max) > 0) max = localMin; 
            }    
        });
    }
    
    public Range getRange() {
        return smartForkValid(new ParallelPointOp<Number, Range>() {
            Range range;
            
            @Override
            public void init() {
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
    
    public double mean() {
        return smartForkValid(new ParallelPointOp.Average<Number>() {
            @Override
            public final double getValue(Number point) {
                return point.doubleValue();
            }

            @Override
            public final double getWeight(Number point) {
                return 1.0;
            }  
        }).value();
    }

    private double[] getValidSortingArray(final boolean isSquared) {
        final double[] temp = new double[countPoints()];
        if(temp.length == 0) return temp;
        
        loopValid(new PointOp<Number, Integer>() {
            private int k;
            
            @Override
            public void init() {
                k = 0;
            }
            
            @Override
            public void process(Number point) {
                temp[k++] = isSquared ? point.doubleValue() * point.doubleValue() : point.doubleValue();
            }

            @Override
            public Integer getResult() {
                return k;
            }            
        });
        
        return temp;
    }
    
    public double median() {
        final double[] temp = getValidSortingArray(false);
        if(temp.length == 0) return Double.NaN;
        return Statistics.median(temp, 0, temp.length);      
    }
    
    public double select(double fraction) {
        final double[] temp = getValidSortingArray(false);
        if(temp.length == 0) return Double.NaN;
        return Statistics.select(temp, fraction, 0, temp.length);
    }
    
    
    public final double getRMS(boolean isRobust) {
        return isRobust ? getRobustRMS() : getRMS();
    }

   
    public double getRMS() {
        return Math.sqrt(smartForkValid(new ParallelPointOp.Average<Number>() {
            @Override
            public final double getValue(Number point) {
                return point.doubleValue() * point.doubleValue();
            }

            @Override
            public final double getWeight(Number point) {
                return 1.0;
            }      
        }).value());     
    }

    
    public double getRobustRMS() {
        final double[] chi2 = getValidSortingArray(true);
        if(chi2.length == 0) return 0.0;
        return Math.sqrt(Statistics.median(chi2) / Statistics.medianNormalizedVariance);    
    }

    
    public double getSum() {
        return forkValid(new ParallelPointOp.Sum<Number>() {
            @Override
            public final double getValue(Number point) {
                return point.doubleValue();
            }
        });
    }
    
    
    public double getAbsSum() {
        return forkValid(new ParallelPointOp.Sum<Number>() {
            @Override
            public final double getValue(Number point) {
               return point.doubleValue();
            }   
        });
    }
    
    
    public double getSquareSum() {  
        return forkValid(new ParallelPointOp.Sum<Number>() {
            @Override
            public final double getValue(Number point) {
                return point.doubleValue() * point.doubleValue();
            }        
        });
        
    }
   
       
    
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
    }

    
    protected void parseHeader(Header header) {
        setUnit(header.containsKey("BUNIT") ? new Unit(header.getStringValue("BUNIT")) : Unit.unity);
    }

    @Override
    public Object getTableEntry(String name) {
        if(name.equals("points")) return Integer.toString(countPoints());
        else if(name.equals("size")) return getSizeString();
        else if(name.equals("min")) return getMin().doubleValue();
        else if(name.equals("max")) return getMax().doubleValue();
        else if(name.equals("mean")) return mean();
        else if(name.equals("median")) return median();
        else if(name.equals("rms")) return getRMS(true);
        else return TableFormatter.NO_SUCH_DATA;
    }

      

 
    
    public final static int NEAREST = 0;
    public final static int LINEAR = 1;
    public final static int QUADRATIC = 2;
    public final static int SPLINE = 3;
    
    
    
    
   
    
    public abstract class AbstractLoop<ReturnType> {

        public abstract ReturnType process();

        protected ReturnType getResult() { return null; }
    }


    


}
