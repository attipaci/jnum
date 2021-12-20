/* *****************************************************************************
 * Copyright (c) 2021 Attila Kovacs <attila[AT]sigmyne.com>.
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

package jnum.data;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jnum.CopiableContent;
import jnum.ExtraMath;
import jnum.Function;
import jnum.PointOp;
import jnum.Unit;
import jnum.Util;
import jnum.data.index.Index;
import jnum.data.index.IndexedEntries;
import jnum.data.index.IndexedValues;
import jnum.fits.FitsHeaderEditing;
import jnum.fits.FitsHeaderParsing;
import jnum.fits.FitsToolkit;
import jnum.math.LinearAlgebra;
import jnum.math.Range;
import jnum.math.RealAlgebra;
import jnum.parallel.ParallelObject;
import jnum.parallel.ParallelPointOp;
import jnum.text.TableFormatter;
import jnum.util.CompoundUnit;
import nom.tam.fits.BasicHDU;
import nom.tam.fits.Fits;
import nom.tam.fits.FitsException;
import nom.tam.fits.FitsFactory;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;
import nom.tam.util.Cursor;

/**
 * A an indexed dataset of some kind, the base class for 1D, 2D, 3D, or higher dimensional dataset implementations.
 * 
 * @author Attila Kovacs
 *
 * @param <IndexType>       the generic type of index by which elements are located, and iterated over, in this dataset.
 */
public abstract class Data<IndexType extends Index<IndexType>> extends ParallelObject implements 
    CopiableContent<Data<IndexType>>, IndexedValues<IndexType, Number>, Iterable<Number>, RealAlgebra, LinearAlgebra<Data<IndexType>>, 
    TableFormatter.Entries, FitsHeaderEditing, FitsHeaderParsing {

    static {
        Locale.setDefault(Locale.US);
    }

    /** The value to use to mark invalid or blanked data */
    private Number invalidValue;

    /** The physical unit in which data is represented */
    private Unit unit;  

    /** The history of operations performed on this data */
    private ArrayList<String> history;

    /** Physical units that are defined locally for this data */
    private Hashtable<String, Unit> localUnits;

    private boolean preserveHistory;

    /** 
     * Instantiates a new data object. It should be called by all subclass contructors.
     */
    protected Data() { 
        setInvalidValue(Double.NaN);
        history = new ArrayList<>();
        preserveHistory = false;
        setDefaultUnit();
    }

    /**
     * Copy the set of policies from another data object. These policies (options) determine how data
     * is handled by specific operations. Some examples are: parallelization environment and options,
     * physical unit, invalid value marker, interpolation type, smoothing policies etc.
     * 
     * @param other     the data object to inherit policies from.
     */
    @SuppressWarnings("unchecked")
    public void copyPoliciesFrom(Data<?> other) {
        copyParallel(other);
        setUnit(other.getUnit());
        setInvalidValue(other.getInvalidValue());
        preserveHistory = other.preserveHistory;
        if(other.localUnits != null) localUnits = (Hashtable<String, Unit>) other.localUnits.clone();
    }
    
  
    
    /**
     * Creates a new empty instance that mimics this data object. Unlike {{@link #newImage()}, which just returns
     * a conforming shallow (single-layer) image object, this method returns a new data object of the same 
     * type of as this one, complete with all layering.
     * 
     * @return  a new data object of the same type and size as this one.
     * 
     * @see #newInstance(Index)
     * @see #newImage()
     */
    public Data<IndexType> newInstance() {
        return newInstance(getSize());
    }
    
    /**
     * Creates a new empty (zeroed) instance that mimics this data object, but with a different size. Unlike 
     * {{@link #newImage(Index, Class)}, which returns a conforming shallow (single-layer) image object, this 
     * method returns a new data object of the same type of as this one, complete with all layering.
     *
     * @param size  the size of the new object instance we need. If the object is multi-layered, the
     *              size sets only the size of the top-level layer. The layers below will be sized the same
     *              only if the top layer's size is tied to the layer below it. For example, a new instance
     *              of {@link Windowed}, will create a new viewport of the specified size, but with the 
     *              the same sized underlying layers as this one.
     *              
     * @return  a new data object of the same type and size as this one.
     * 
     * @see #newInstance()
     * @see #newImage(Index, Class)
     */
    public abstract Data<IndexType> newInstance(IndexType size);
    
    
    @Override
    public int hashCode() {
        int hash = super.hashCode() ^ getInvalidValue().hashCode(); 
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

        if(!Util.equals(getInvalidValue(), data.getInvalidValue())) return false;
        if(!Util.equals(getUnit(), data.getUnit())) return false;

        return contentEquals(data);
    }

    /**
     * Checks if this data object has the same content as another data object of the same index type.
     * 
     * @param data  the other data object with the same type of index.
     * @return      <code>true</code>if the two data objects contain the same data, or <code>false</code> otherwise.
     *              The two data objects must match in size, and their elements must be the same with
     *              {@link Number#equals(Object)}.
     *              
     * @see #getSize()
     */
    public boolean contentEquals(final Data<IndexType> data) {   
        if(!getSize().equals(data.getSize())) return false;

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

        return clone;
    }
    
    
    /** 
     * Returns the list of history entries for this data object, as a list of strings.
     * 
     * @return  the ordered list of history (of operations performed) on this data.
     * 
     * @see #addHistory(String)
     * @see #addHistory(Header)
     * @see #setHistory(List)
     * @see #clearHistory()
     */
    public List<String> getHistory() { return history; }

    /**
     * Wipes the prior history of operations on this data clean, as if starting a new
     * pristine data object.
     * 
     * @see #getHistory()
     */
    public void clearHistory() {
        if(history != null) history.clear();
    }

    /**
     * Adds a new enty to the end of the history of operations performed to this data.
     * 
     * @param entry     a human-readable string describing the last operation on this data object.
     * 
     * @see #getHistory()
     * @see #addHistory(String)
     * @see #clearHistory()
     */
    public void addHistory(String entry) {
        if(history == null) return;
        history.add(entry); 
        // TODO verbose history?
        //Util.detail(this, entry);
    }

    /**
     * Sets the history record (of operations performed on this data object) to the
     * specified list of strings. Any prior history dereferenced, and a new history
     * list is started with the specified values.
     * 
     * @param entries   the list of human-readable operations performed on this data object
     * 
     * @see #getHistory()
     * @see #addHistory(String)
     * @see #clearHistory()
     */
    public void setHistory(List<String> entries) {
        history = new ArrayList<>(entries);
    }


    /**
     * <p>
     * Adds a new non-standard physical unit for used with this data object locally, using the
     * canonical name of the unit only. The local units of
     * this data may be applied to the data with {@link #setUnit(String)}, or can 
     * be retrieved with all other locally defined units via {@link #getLocalUnits()}.
     * </p>
     * 
     * <p>
     * Standard units, that is those returned by {@link Unit#get(String)}, specify just
     * about all commonly (and also rarely) used units, assuming that the underlying data
     * is numerically represented as standard S.I. values. However, a data object may diverge
     * from S.I. internally, and store values in some other (non-SI) units that are
     * more natural for the partiucular data object (e.g. "Jy/beam", where "beam" is a 
     * dynamically defined quantity of the data object itself). In such cases, the
     * user of the data object may want to define the relevant physical units for use
     * locally with the data object. (The standard physical units will remain available
     * also, so be aware!)
     * </p>
     * 
     * @param u     a custom (non-standard) physical unit for use with this data object.
     * 
     * @see #addLocalUnit(Unit, String)
     * @see #setUnit(String)
     * @see #getLocalUnits() 
     */
    public void addLocalUnit(Unit u) {
        if(localUnits == null) localUnits = new Hashtable<>();
        u.registerTo(localUnits);
    }

    /**
     * Like {@link #addLocalUnit(Unit)}, but the custom (non-standard) unit is registered
     * also with the list of the supplied name alternatives, all of which can be used to
     * retrieve the specified unit e.g. with {@link #setUnit(String)}.
     * 
     * @param u         a custom (non-standard) physical unit for use with this data object.
     * @param altNames  a comma-separated alternative (case-sensitive) names by which the 
     *                  specified custom unit may be referred to as.
     * 
     * @see #addLocalUnit(Unit)
     * @see #setUnit(String)
     * @see #getLocalUnits()
     */
    public void addLocalUnit(Unit u, String altNames) {
        if(localUnits == null) localUnits = new Hashtable<>();
        u.registerTo(localUnits, altNames);
        addLocalUnit(u);
    }


    /**
     * Returns the list of locally defined custom (non-stndard) physical units for this
     * data object.
     * 
     * @return  the list of lically defined custom (non-standard) physical units for this object.
     * 
     * @see #addLocalUnit(Unit)
     * @see #setUnit(String)
     */
    public Hashtable<String, Unit> getLocalUnits() { return localUnits; }


    /**
     * Returns the number value that is used by this data object to mark invalid data, such 
     * as {@link Double#NaN} or -1, or -999.0.
     * 
     * @return  the number value that this data object uses to mark invalid data.
     * 
     * @see #setInvalidValue(Number)
     */
    public final Number getInvalidValue() {
        return invalidValue;
    }

    
    /**
     * Changes the number value used by this data object to mark invalid data. All data
     * marked invalid previously will be changed to the new ionvalid value.
     * 
     * @param value     the new number value that this data object will use from now on to mark invalid data.
     * 
     * @see #getInvalidValue()
     * @see #discard(Index)
     */
    public final void setInvalidValue(final Number value) {


        // Replace old blanking values in image with the new value, as needed...
        if(invalidValue != null) if(!invalidValue.equals(value)) {
            smartFork(new ParallelPointOp.Simple<IndexType>() {

                @Override
                public void process(IndexType index) {
                    if(invalidValue.equals(get(index))) set(index, value);
                }

            });
        }

        invalidValue = (value == null) ? Double.NaN : value;    

    }

    @Override
    public int compare(Number a, Number b) {
        if(a.doubleValue() == b.doubleValue()) return 0;
        return a.doubleValue() < b.doubleValue() ? -1 : 1;
    }

    @Override
    public boolean isValid(IndexType index) {
        return isValid(get(index));
    }
    
    /**
     * Checks if a number value is considered valid by this data object.
     * 
     * @param value     a number value to check.
     * @return          <code>true</code> if the specified number is a valid value for this object, or <code>false</code>
     *                  if it is invalid.
     * 
     * @see #getInvalidValue()
     * @see #setInvalidValue(Number)
     */
    public boolean isValid(Number value) {
        return !value.equals(invalidValue);
    }


    /** 
     * Set the data unit to the default physical unit of this data object.
     * 
     * @see #setUnit(Unit)
     */
    protected void setDefaultUnit() { setUnit(Unit.unity); }

    /**
     * Returns the physical unit in which the data of this object is represented.
     * 
     * @return      the physical unit used for representing values in this data object.
     * 
     * @see #setUnit(String)
     * @see #setUnit(Unit)
     * @see #setUnit(String, Map)
     * @see #setDefaultUnit()
     */
    public Unit getUnit() {
        return unit;
    }

    /**
     * Sets a new physical unit for this data object. Values stored in this data are assumed
     * to be represented in the new unit.
     * 
     * @param u     the new physical unit used for representing values in this data object.
     * 
     * @see #setUnit(String)
     * @see #setDefaultUnit()
     * @see #getUnit()
     * 
     */
    public void setUnit(Unit u) { this.unit = u; }

    /**
     * Sets a new physical unit for this data object. Values stored in this data are assumed
     * to be represented in the new unit.
     * 
     * @param spec  the string specification for the new physical unit used for representing values in this data object.
     * 
     * @see #setUnit(String, Map)
     * @see #setUnit(Unit)
     * @see #setDefaultUnit()
     * @see #getUnit()
     */
    public void setUnit(String spec) {
        setUnit(spec, getLocalUnits());
    }

    /**
     * Sets a new physical unit for this data object, including custom physical base units defined
     * in by the caller. Values stored in this data are assumed to be represented in the new unit.
     * The specification can rfefer to any compount unit that is composed of standard and the specified
     * non-standard base units, e.g. "W / m**2 / Hz**{0.5}", where "W" is redefined as a non-stamndard
     * custom base unit in the second argument, and "m" and "Hz" are the standard S.I. units.
     * 
     * @param spec              the string specification for the new physical unit used for representing values in this data object.
     * @param extraBaseUnits    the lookup table for custom (non-standard) base units that may be used also.
     * 
     * @see #setUnit(String)
     * @see #setUnit(Unit)
     * @see #setDefaultUnit()
     * @see #getUnit()
     */
    public void setUnit(String spec, Map<String, Unit> extraBaseUnits) {
        CompoundUnit u = new CompoundUnit();
        u.parse(spec, extraBaseUnits);
        setUnit(u); 
    }

    /**
     * Preserve prior history when recording the next new data, for example because the new data this object is
     * being set to is a logical continuation of the old data. The preservation affect only the next call to
     * {@link #recordNewData(String)}.
     * 
     * @see #recordNewData(String)
     */
    public void preserveHistory() {
        preserveHistory = true;
    }

    /**
     * Starts a new history for this data, with the specified data description (if any).
     * 
     * @param description       the description of the new data, or <code>null</code> to leave it nondescript.
     * 
     * @see #preserveHistory()
     * @see #clearHistory()
     */
    protected void recordNewData(String description) {
        if(preserveHistory) preserveHistory = false;
        else {
            clearHistory();
            addHistory("set new image " + getSizeString() + (description == null ? "" : " " + description));
        }
    }

    @Override
    public abstract DataCrawler<Number> iterator();




    @Override
    public final boolean conformsTo(IndexedValues<IndexType, ?> data) { return conformsTo(data.getSize()); }

    /**
     * Copies content from the another object into this data.
     * 
     * @param source    The indexed number values to copy into this data. 
     * @param report    Whether to report this operation in the data history log.
     */
    public void copyOf(final IndexedEntries<IndexType, ? extends Number> source, boolean report) {
        if(source == this) return;

        smartFork(new ParallelPointOp.Simple<IndexType>() {
            @Override
            public void process(IndexType index) {
                if(source.isValid(index)) set(index, source.get(index));
            }
        });

        if(report) addHistory("pasted new content: " + source.getSizeString());
    }
    
    /**
     * Returns the number value at the specified data index (if valid), or the the specified default value if
     * there is no valid datum at the specified index.
     * 
     * @param index         the data index
     * @param defaultValue  the default value to return if the data at the specified index is invalid.
     * @return              the valid datum at the specified index, or else the specified default value if
     *                      the datum at the index is invalid.
     *                      
     * @see #isValid(Index)
     * @see #getInvalidValue()
     * @see #setInvalidValue(Number)
     */
    public abstract Number getValid(final IndexType index, final Number defaultValue);

    /**
     * Discards the datum at the specified index setting, marking it as invalid.
     * 
     * @param index     the data index          
     * 
     * @see #isValid(Index)
     * @see #discardRange(Range)
     * @see #setInvalidValue(Number)
     * 
     * 
     */
    public abstract void discard(IndexType index);
    

    /**
     * Clears all values inthis data object, by calling {@link #clear(Index)} on every element. It also
     * starts a new hostory for this data object.
     * 
     * @see #clearHistory()
     * @see #clear(Index)
     */
    public final void clear() {
        smartFork(new ParallelPointOp.Simple<IndexType>() {
            @Override
            public void process(IndexType index) { clear(index); }
        });
        clearHistory();
        addHistory("clear " + getSizeString());
    }

    /**
     * Fills this data object with the specified number value, setting every element in this object to that
     * value.
     * 
     * @param value     the value that all elements of this data object are to be set to.
     * 
     * @see #clear()
     * @see #add(Number)
     */
    public final void fill(final Number value) {
        smartFork(new ParallelPointOp.Simple<IndexType>() {
            @Override
            public void process(IndexType index) { set(index, value); }
        });
        clearHistory();
        addHistory("fill " + getSizeString() + " with " + value);
    }

    /**
     * Adds a value to every valid element of this data object.
     * 
     * @param value     the increment to add to all valid data elements.
     * 
     * @see #add(Index, Number)
     */
    public final void add(final Number value) {
        smartFork(new ParallelPointOp.Simple<IndexType>() {
            @Override
            public void process(IndexType index) { if(isValid(index)) add(index, value); }
        });
        addHistory("add " + value);
    }


    @Override
    public final void add(double x) {
        add(new Double(x));
    }

    @Override
    public void scale(final double factor) {
        smartFork(new ParallelPointOp.Simple<IndexType>() {
            @Override
            public void process(IndexType index) { if(isValid(index)) scale(index, factor); }
        });
        addHistory("scale by " + factor);
    }

    /**
     * Validates this data object, by checking through all elements, and calling {@link #discard(Index)}
     * on those values for which {@link #isValid(Index)} returns <code>false</code>. This is
     * especially useful for data objects which have compound validation, e.g. because they implement
     * explicit flagging, beside simply having invalid number values as markers for bad or missing
     * data.
     * 
     * @see #isValid(Index)
     * @see #discard(Index)
     * @see #validate(Validating)
     */
    public final void validate() {
        smartFork(new ParallelPointOp.Simple<IndexType>() {
            @Override
            public void process(IndexType index) { if(!isValid(index)) discard(index); }
        });
        addHistory("validate");
    }

    /**
     * Validates this data object, using the specified data validator on all elements, and calling {@link #discard(Index)}
     * on any data deemed invalid by the validator. This call provides external validation of the data
     * that is independent of the built-in validation method.
     * 
     * @param validator     the external validator the checks if data at specific indices is valid or not.
     * 
     * @see Validating#isValid(Object)
     * @see #discard(Index)
     * @see #validate()
     */
    public void validate(final Validating<IndexType> validator) {
        smartFork(new ParallelPointOp.Simple<IndexType>() {
            @Override
            public void process(IndexType index) { if(!validator.isValid(index)) validator.discard(index); }
        });
        addHistory("validate via " + validator);
    }

    /**
     * Validates this data agains another. Entries whose counterparts in the supplied data
     * are invalid, are discarded (marked invalid) from this data too.
     * 
     * @param data      the data used to invalidate points in this data instance. 
     */
    public void validateTo(IndexedEntries<IndexType, ?> data) {
        validate(new Validating<IndexType>() {
            @Override
            public boolean isValid(IndexType index) {
                return data.isValid(index);
            }

            @Override
            public void discard(IndexType index) {
                Data.this.discard(index);
            }    
        });
    }
    
    /**
     * Discards all data elements that have values inside the specified range, by calling {@link #discard(Index)} on
     * the affected points.
     * 
     * @param range     The range of number values to discard. All data elements in this range will be
     *                  discarded via {@link #discard(Index)}.
     *                  
     * @see #discard(Index)
     * @see #restrictRange(Range)
     */
    public final void discardRange(final Range range) {
        smartFork(new ParallelPointOp.Simple<IndexType>() {
            @Override
            public void process(IndexType index) { 
                if(range.contains(get(index).doubleValue())) discard(index);
            }
        });
    }

    /**
     * Restricts all data elements to the specified range, by calling {@link #discard(Index)} on
     * the outliers.
     * 
     * @param range     The range of number values to discard. All data elements in this range will be
     *                  discarded via {@link #discard(Index)}.
     *                  
     * @see #discard(Index)
     * @see #discardRange(Range)
     */
    public final void restrictRange(final Range range) {
        smartFork(new ParallelPointOp.Simple<IndexType>() {
            @Override
            public void process(IndexType index) { 
                if(!range.contains(get(index).doubleValue())) discard(index);
            }
        });
    }


    /**
     * Discards outliers above the specified deviation level (from zero), by calling {@link #discard(Index)} on
     * any point that is deemed to be an outlier at the specified level. The default implementation is to 
     * call {@link #despikeAbsolute(double, IndexedValues)} with <code>null</code> for the <code>noiseWeight</code>
     * argument.
     * 
     * @param threshold     the despike threshold level. All data elements with absolute values larger than this
     *                      threshold will be discarded.
     *                      
     * @see #despikeAbsolute(double, IndexedValues)
     */
    public void despike(double threshold) {
        despikeAbsolute(threshold, null);
    }

    /**
     * Discards outliers above the specified deviation level (from zero), by calling {@link #discard(Index)} on
     * any point that is deemed to be an outlier at the specified level. The second optional argument can
     * be used to specify noise weights data (<i>w</i> = 1/%sigma;<sup>2</sup>), in which case the threshold is
     * interpreted as a signal-to-noise threshold, s.t. data <i>x</i><sub>i</sub> for which |<i>x</i><sub>i</sub>| 
     * &gt; <code>significance</code> * %sigma;<sub>i</sub> is removed. Leaving the second argument <code>null</code>
     * is equivalent to all data weight being 1.
     * 
     * @param significance   the despike threshold level. All data elements with absolute values or standardized
     *                       deviations (if noise weights are given) larger than this threshold will be discarded.
     * @param noiseWeight    (optional) Noise weights (<i>w</i> = 1/%sigma;<sup>2</sup>) to use with the data,
     *                       or <code>null</code> to use uniform weights of 1.
     *                      
     * @see #despikeAbsolute(double, IndexedValues)
     */
    public synchronized void despikeAbsolute(final double significance, final IndexedValues<IndexType, ?> noiseWeight) {

        final double s2 = significance * significance;

        smartFork(new ParallelPointOp.Simple<IndexType>() {
            @Override
            public void process(IndexType idx) {
                double w = noiseWeight == null ? 1.0 : noiseWeight.get(idx).doubleValue();
                double x = get(idx).doubleValue();
                if(w * x * x > s2) discard(idx);
            }
        });

        addHistory("despiked (absolute) at " + Util.S3.format(significance));
    }

    /**
     * Returns a humant-readable string (typically multiple lines) describing this data object. Its purpose
     * s to report metadata for this data object, not the actual data content.
     * 
     * @return  a comprehensive human-readable description of what a user might want to know about this
     *          data object or its state.
     */
    public abstract String getInfo();

    /**
     * Checks if this data object is empty, i.e. if it contains no valid elements.
     * 
     * @return  <code>true</code> if this data object is empty, containing no valid data elements, otherwise
     *          <code>false</code>.
     *         
     * @see #isValid(Index)        
     * @see #countPoints()
     */
    public boolean isEmpty() {
        return countPoints() == 0;
    }

    /**
     * Counts the number of valid data points in this data object.
     * 
     * @return  the number of valid data points in this data object.
     * 
     * @see #isValid(Index)
     * @see #isEmpty()
     */
    public int countPoints() {
        return smartForkValid(new ParallelPointOp.ElementCount<Number>()).intValue();
    }

    /**
     * Returns the minimum valid value contained in this data object.
     * 
     * @return      the minimum valid value contained in this data object or {@link Double#POSITIVE_INFINITY}
     *              if the data contains no valid values at all.
     *              
     * @see #getMax()
     * @see #getRange()
     * @see #isEmpty()
     * @see #indexOfMin()
     */
    public Number getMin() {
        return smartForkValid(new ParallelPointOp<Number, Number>() {
            Number min;

            @Override
            protected void init() {
                min = Double.POSITIVE_INFINITY;
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

    /**
     * Returns the maximum valid value contained in this data object.
     * 
     * @return      the maximum valid value contained in this data object or {@link Double#NEGATIVE_INFINITY}
     *              if the data contains no valid values at all.
     *              
     * @see #getMin()
     * @see #getRange()
     * @see #isEmpty()
     * @see #indexOfMax()
     */
    public Number getMax() {
        return smartForkValid(new ParallelPointOp<Number, Number>() {
            Number max;

            @Override
            protected void init() {
                max = Double.NEGATIVE_INFINITY;
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

    /**
     * Returns the range of valid values contained in this data object.
     * 
     * @return      the range of values contained in this data object. (it may be an empty range
     *              if this data object is empty.
     *              
     * @see #getMin()
     * @see #getMax()
     * @see #isEmpty()
     * @see #indexOfMaxDev()
     */
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
     * Returns the index of the highest value.
     * If there are multiple points with the same maximum value, it is undefined (and possibly random) whose 
     * index is returned. 
     * 
     * @return the index corresponding to the highest value.
     * 
     * @see #indexOfMax()
     * @see #indexOfMaxDev()
     * @see #getMin()
     */
    public final IndexType indexOfMin() { 
        return smartFork(new ParallelPointOp<IndexType, IndexType>() {
            private IndexType minIndex;
            private Number min;

            @Override
            protected void init() {
                minIndex = null;
                min = Double.MAX_VALUE;
            }

            @Override
            public void process(IndexType index) {
                if(!isValid(index)) return;
                if(compare(get(index), min) >= 0) return;
                min = get(index);
                minIndex = index.copy();

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
     * Returns the index of the lowest value.
     * If there are multiple points with the same minimum value, it is undefined (and possibly random) whose 
     * index is returned.
     * 
     * @return the index corresponding to the lowest value.
     * 
     * @see #indexOfMin()
     * @see #indexOfMaxDev()
     * @see #getMax()
     */
    public final IndexType indexOfMax() { 
        return smartFork(new ParallelPointOp<IndexType, IndexType>() {
            private IndexType maxIndex;
            private Number max;

            @Override
            protected void init() {
                maxIndex = null;
                max = Double.MIN_VALUE;
            }

            @Override
            public void process(IndexType index) {
                if(!isValid(index)) return;
                if(compare(get(index), max) <= 0) return;
                max = get(index);
                maxIndex = index.copy();

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
     * Returns the index of the datum with the largest absolute deviation from zero.
     * Uses cast to double, so may not work perfectly on long types. If there are multiple points with the
     * same maximum value, it is undefined (and possibly random) whose index is returned.
     * 
     * @return the index corresponding to the largest absolute deviation from zero.
     * 
     * @see #indexOfMin()
     * @see #indexOfMax()
     * @see #getRange()
     */
    public final IndexType indexOfMaxDev() { 
        return smartFork(new ParallelPointOp<IndexType, IndexType>() {
            private IndexType maxIndex;
            private Number max;

            @Override
            protected void init() {
                maxIndex = null;
                max = Double.MIN_VALUE;
            }

            @Override
            public void process(IndexType index) {
                if(!isValid(index)) return;
                if(compare(get(index), max) <= 0) return;
                max = Math.abs(get(index).doubleValue());
                maxIndex = index.copy();

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

    /**
     * Levels this data, by removing its mean or median data value from all valid elements.
     * 
     * @param isRobust      if <code>true</code> a median will be calculated an removed, otherwise a mean
     *                      value will be used for levelling. Note, that the calculation of a mean is
     *                      an O(<i>N</i>) operation, whereas a median is an O(<i>N</i>log<i>N</i>)
     *                      operation requiring an extra O(<i>N</i>) temporary storage. As such,
     *                      medians can be very expensive computationally. Use carefully!
     * @return  the mean or median level that was removed from the data.
     * 
     * @see #getMean()
     * @see #getMedian()
     * @see #add(Number)
     */
    public final double level(boolean isRobust) {
        double level = isRobust ? getMedian().value() : getMean().value(); 
        add(-level);
        return level;
    }

    /**
     * Returns the mean valid data value contained in this data object.
     * 
     * @return      the mean valid value in this data object.
     * 
     * @see #getMedian()
     * @see #getWeightedMean(IndexedValues)
     * @see #level(boolean)
     */
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

    /**
     * Returns the weighted mean valid data value contained in this data object, using the
     * supplied external data weights.
     * 
     * @param weights   the external data weights to use when calculating the weighted mean.
     * @return          the weighted mean valid value in this data object.
     * 
     * @see #getMean()
     * @see #getWeightedMedian(IndexedValues)
     */
    public final WeightedPoint getWeightedMean(final IndexedValues<IndexType, ?> weights) {
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

    /**
     * Returns the median valid data value contained in this data object. Note, that the calculation
     * of the median is an O(<i>N</i>log<i>N</i>) operation requiring an extra O(<i>N</i>) temporary 
     * storage. As such, medians can be very expensive computationally. Use carefully!
     * 
     * @return      the median valid value in this data object.
     * 
     * @see #getMean()
     * @see #getWeightedMedian(IndexedValues)
     * @see #level(boolean)
     * @see #select(double)
     * @see Statistics.Destructive#median(double[])
     */
    public WeightedPoint getMedian() {
        final double[] temp = getValidSortingArray();
        if(temp.length == 0) return new WeightedPoint(Double.NaN, 0.0);
        return new WeightedPoint(Statistics.Destructive.median(temp, 0, temp.length), temp.length);      
    }

    /**
     * Returns the weighted median valid data value contained in this data object, using the
     * supplied external data weights. Note, that the calculation
     * of the median is an O(<i>N</i>log<i>N</i>) operation requiring an extra O(<i>N</i>) temporary 
     * storage. As such, medians can be very expensive computationally. Use carefully!
     * 
     * @param weights   the external data weights to use when calculating the weighted median.
     * @return          the weighted median valid value in this data object.
     * 
     * @see #getMedian()
     * @see #getWeightedMean(IndexedValues)
     * @see Statistics.Destructive#median(WeightedPoint[], WeightedPoint)
     */
    public final WeightedPoint getWeightedMedian(final IndexedValues<IndexType, ?> weights) {   
        final WeightedPoint[] temp = getValidSortingArray(weights);
        if(temp.length == 0) return new WeightedPoint(Double.NaN, 0.0);
        return Statistics.Destructive.median(temp, 0, temp.length);      
    }

    /**
     * Selects the specified percentile of the sorted data. The current implementation does use sorting.
     * There are other, marginally more efficient methods for calculating selection, which we
     * do not currently use...
     * 
     * @param fraction      the selection fraction [0:1] from lowest to highest.
     * @return              the selected data value distribution. 
     * 
     * @see Statistics.Destructive#select(double[], double)
     */
    public double select(double fraction) {
        if(fraction == 0.0) return getMin().doubleValue();
        else if(fraction == 1.0) return getMax().doubleValue();

        final double[] temp = getValidSortingArray();
        if(temp.length == 0) return Double.NaN;
        return Statistics.Destructive.select(temp, fraction, 0, temp.length);
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

    private WeightedPoint[] getValidSortingArray(final IndexedValues<IndexType, ?> weights) {    
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

    /**
     * Returns the RMS (root-mean-square) value of the valid elements in this data object. This implementation
     * will not compensate for a non-zero mean of the data.
     * 
     * @param isRobust  if <code>true</code> the mean variance will be calculated using a robust methods
     *                  based on a median, otherwise it will use the standard arithmetic mean variance.
     * @return          the RMS value of this data.
     * 
     * @see #getRMSScatter(boolean)
     * @see #getRMS()
     * @see #getRobustRMS()
     * @see #getVariance(boolean)
     * @see #level(boolean)
     */
    public final double getRMS(boolean isRobust) {
        return isRobust ? getRobustRMS() : getRMS();
    }

    /**
     * Returns the RMS (root-mean-square) scatter of the valid elements in this data object. This implementation
     * compensates for a non-zero mean of the data, and returns a true measure of the scatter around that mean.
     * 
     * @param isRobust  if <code>true</code> the scatter will be calculated using a robust methods
     *                  based on a medians, otherwise it will use the standard arithmetic means.
     * @return          the RMS scatter of this data around iuts mean or median.
     * 
     * @see #getRMS()
     * @see #getVariance(boolean)
     * @see #getMean()
     * @see #getMedian()
     */
    public final double getRMSScatter(boolean isRobust) {
        double mu = isRobust ? getMedian().value() : getMean().value();
        return Math.sqrt(getVariance(isRobust) - mu * mu);
    }

    /**
     * Returns the variance of the valid elements in this data object. This implementation
     * will not compensate for a non-zero mean of the data.
     * 
     * @param isRobust  if <code>true</code> the variance will be calculated using a robust methods
     *                  based on a median, otherwise it will use the standard arithmetic mean of the squares.
     * @return          the RMS value of this data.
     * 
     * @see #getRMS()
     * @see #getRobustRMS()
     * @see #getVariance(boolean)
     * @see #level(boolean)
     */
    public final double getVariance(boolean isRobust) {
        return isRobust ? getRobustVariance() : getVariance();
    }

    /**
     * Returns the standard root-mean-square (RMS) value of this data. This implementation
     * will not compensate for a non-zero mean of the data.
     * 
     * @return      the RMS value of this data.
     * 
     * @see #getRobustRMS()
     * @see #getRMSScatter(boolean)
     * @see #level(boolean)
     */
    public double getRMS() { return Math.sqrt(getVariance()); }

    /**
     * Returns the standard variance of this data. This implementation
     * will not compensate for a non-zero mean of the data.
     * 
     * @return      the variance (i.e. mean-square value) of this data.
     * 
     * @see #getRobustVariance()
     * @see #getRMS()
     * @see #level(boolean)
     */
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

    /**
     * Returns an estimate of the root-mean-square (RMS) value of this data, using a median of squares. 
     * This implementation will not compensate for a non-zero mean of the data.
     * 
     * @return      the robust RMS estimate for this data, which uses a median of squares.
     * 
     * @see #getRMS()
     * @see #getRMSScatter(boolean)
     * @see #level(boolean)
     */
    public double getRobustRMS() { return Math.sqrt(getRobustVariance()); }

    /**
     * Returns an estimate of the variance of this data, using a median of squares. 
     * This implementation will not compensate for a non-zero mean of the data.
     * 
     * @return      the robust estimate of the variance (i.e. mean square value) of this data, 
     *              which uses a median of squares.
     * 
     * @see #getVariance()
     * @see #level(boolean)
     */
    public double getRobustVariance() {
        final double[] variance = getValidSortingArray();
        if(variance.length == 0) return Double.NaN;

        for(int i=variance.length; --i >= 0; ) variance[i] *= variance[i];
        return Statistics.Destructive.median(variance) / Statistics.medianNormalizedVariance;
    }

    /**
     * Returns the sum of all valid data points in this data object.
     * 
     * @return      the sum of valid data points contained.
     * 
     * @see #getAbsSum()
     * @see #getSquareSum()
     * @see #getMean()
     */
    public double getSum() {
        return smartForkValid(new ParallelPointOp.Sum<Number>() {
            @Override
            public final double getValue(Number point) {
                return point.doubleValue();
            }
        });
    }

    /**
     * Returns the sum of absolute values of all valid data points in this data object.
     * 
     * @return      the sum of absolute values over all valid data points contained.
     * 
     * @see #getSum()
     * @see #getSquareSum()
     */
    public double getAbsSum() {
        return smartForkValid(new ParallelPointOp.Sum<Number>() {
            @Override
            public final double getValue(Number point) {
                return point.doubleValue();
            }   
        });
    }

    /**
     * Returns the sum of squares over all valid data points in this data object.
     * 
     * @return      the sum of squares in this data.
     * 
     * @see #getSum()
     * @see #getAbsSum()
     * @see #getVariance()
     */
    public double getSquareSum() {  
        return smartForkValid(new ParallelPointOp.Sum<Number>() {
            @Override
            public final double getValue(Number point) {
                return point.doubleValue() * point.doubleValue();
            }        
        });      
    }

    /**
     * Returns the covariance measure between this data and the specified other data, as the
     * sum of the element-by-element cross products. 
     * 
     * @param data      the data to which to measure covariances
     * @return          the covariance measure between this data and the specified other data.
     * 
     * @see #correlationTo(Data)
     */
    public final double covarianceTo(final Data<IndexType> data) {
        return smartFork(new ParallelPointOp.Sum<IndexType>() {
            @Override
            protected double getValue(IndexType point) {
                if(!isValid(point)) return 0.0;
                if(!data.isValid(point)) return 0.0;
                return get(point).doubleValue() * data.get(point).doubleValue();
            }
        });     
    }

    /**
     * Returns the correlation coefficient to the specified other data. The correlation coefficient
     * is defined as the covariance to the other data, divided by the geometric mean of the
     * autocovariances of both data objects.
     * 
     * @param data      the data to which to measure correlation.
     * @return          the correlation coefficient [-1:1] to the other data.
     * 
     * @see #covarianceTo(Data)
     */
    public final double correlationTo(final Data<IndexType> data) {
        return covarianceTo(data) / Math.sqrt(covarianceTo(this) * data.covarianceTo(data));   
    }

    /**
     * Returns a new data object of the same type and size as this intance with 
     * elements that are remapped from this data instance by the specified function.
     * 
     * @param f     the function that maps values from this instance to the new data object
     * @return      a new data, of identical type and size to this one, but with the values
     *              re-mapped by the specified function.
     *              
     * @see #apply(Function)
     */
    public Data<IndexType> getMapped(final Function<Number, Number> f) {
        Data<IndexType> data = newImage(getSize(), getElementType());
        smartFork(new ParallelPointOp.Simple<IndexType>() {
            @Override
            public void process(IndexType point) {
                if(isValid(point)) data.set(point, f.valueAt(get(point)));
                else data.discard(point);
            }
        });
        
        return data;
    }
    
    /**
     * Applies a function tp every element of this data instance. 
     * 
     * @param f     the function to apply to each point value in this data.
     * 
     * @see #getMapped(Function)
     */
    public void apply(final Function<Number, Number> f) {
        smartFork(new ParallelPointOp.Simple<IndexType>() {
            @Override
            public void process(IndexType point) {
                if(isValid(point)) set(point, f.valueAt(get(point)));
            }
        });
    }
    
    /**
     * Performs a point-by-point multiplication of the elements of this data with the corresponding
     * elements of the specified other data.
     * 
     * @param data      the point values to multiply the elements of this data with.
     */
    public final void multiplyByComponentsOf(final Data<IndexType> data) {
        smartFork(new ParallelPointOp.Simple<IndexType>() {
            @Override
            public void process(IndexType point) {
                if(data.isValid(point)) set(point, get(point).doubleValue() * data.get(point).doubleValue());
            }
        });  
    }


    @Override
    public final void add(final Data<IndexType> data) {
        smartFork(new ParallelPointOp.Simple<IndexType>() {
            @Override
            public void process(IndexType index) {
                if(data.isValid(index)) add(index, data.get(index));
            }      
        });

        addHistory("added " + getClass().getSimpleName());
    }


    @Override
    public final void addScaled(final Data<IndexType> data, final double scaling) {
        smartFork(new ParallelPointOp.Simple<IndexType>() {
            @Override
            public void process(IndexType index) {
                if(data.isValid(index)) add(index, scaling * data.get(index).doubleValue());
            }      
        });

        addHistory("added scaled " + getClass().getSimpleName() + " (" + scaling + "x).");
    }

    @Override
    public final void subtract(final Data<IndexType> data) {
        addScaled(data, -1.0);
    }

    @Override
    public void setSum(final Data<IndexType> a, final Data<IndexType> b) {
        clear();
        copyOf(a, false);
        add(b);
    }

    @Override
    public void setDifference(final Data<IndexType> a, final Data<IndexType> b) {
        clear();
        copyOf(a, false);
        subtract(b);
    }

    @Override
    public void zero() {
        fill(0);
    }


    @Override
    public boolean isNull() {
        return smartForkValid(new ParallelPointOp<Number, Boolean>() {
            boolean isNonZero;

            @Override
            public void mergeResult(Boolean localResult) {
                if(localResult) isNonZero = true;
            }

            @Override
            protected void init() {
                isNonZero = false;
            }

            @Override
            public void process(Number point) {
                if(isNonZero) return;
                if(point.doubleValue() != 0.0) isNonZero = true;
            }

            @Override
            public Boolean getResult() {
                return isNonZero;
            }

        });
    }

    // TODO Make default method in Observations
    /**
     * Apply a maximum-entropy correction to this data, given some prior model and measurement noise.  
     * 
     * @param model     the prior model for this data
     * @param noise     the corresponding (rms) noise for this data
     * @param lambda    the MEM correction coefficient. The larger the coefficient, the more the data is 'pulled'
     *                  towards the model.
     */
    public void memCorrect(final IndexedValues<IndexType, ?> model, final IndexedValues<IndexType, ?> noise, final double lambda) {
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

   



    /**
     * Returns the underlying Java data object, such as a <code>float[]</code>, <code>double[][]</code>, or
     * <code>int[][][]</code>, or whatever object this data uses to hold its values natively.
     * 
     * @return      the underlying data container object.
     */
    public abstract Object getCore();

    /**
     * Returns a new FITS image object created from this data alone. FITS (Flexible Image Transport System) 
     * is a commonly used image transport format in astronomy.
     * 
     * @param dataType      The underlying data type for the image, which may be different from the underlying type of
     *                      this data object, e.g. <code>Float.class</code>.
     * @return              A new FITS image object created from this data, with all the requisite image or binary
     *                      HDUs and header keywords for describing this data as fully as possible.
     * @throws FitsException    if there was an error creating the FITS data.
     * 
     * @see #writeFits(String, Class)
     * @see #getHDUs(Class)
     */
    public Fits createFits(Class<? extends Number> dataType) throws FitsException {
        FitsFactory.setLongStringsEnabled(FitsToolkit.isUseOGIPLongStrings());
        FitsFactory.setUseHierarch(true);
        Fits fits = new Fits(); 

        ArrayList<BasicHDU<?>> hdus = getHDUs(dataType);
        for(int i=0; i<hdus.size(); i++) fits.addHDU(hdus.get(i));

        return fits;
    }

    /**
     * Writes this data into a FITS file. FITS (Flexible Image Transport System) 
     * is a commonly used image transport format in astronomy.
     * 
     * @param fileName      The name/path of the (new) FITS file
     * @param dataType      The underlying data type for the image, which may be different from the underlying type of
     *                      this data object, e.g. <code>Float.class</code>.
     * @throws FitsException    if there was an error creating the FITS data.
     * @throws IOException      if there was an I/O error trying to write the file.
     * 
     * @see #createFits(Class)
     */
    public void writeFits(String fileName, Class<? extends Number> dataType) throws FitsException, IOException {
        try(Fits fits = createFits(Float.class)) {
            fits.write(new File(fileName));
            fits.close();
        }
    }

    /**
     * Returns the FITS header-data units (HDUs) that describe this data as completely as possible. 
     * FITS (Flexible Image Transport System) is a commonly used image transport format in astronomy.
     * 
     * @param dataType      The underlying data type for the image, which may be different from the underlying type of
     *                      this data object, e.g. <code>Float.class</code>.
     * @return          An array of HDUs that capture this data as fully as possible in the FITS convention.
     * @throws FitsException    if there was an error creating the FITS data.
     */
    public abstract ArrayList<BasicHDU<?>> getHDUs(Class<? extends Number> dataType) throws FitsException;


    @Override
    public void editHeader(Header header) throws HeaderCardException {
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


    @Override
    public void parseHeader(Header header) {
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


    /** 
     * A point location and value in the parent data object.
     * 
     * @author Attila Kovacs
     *
     */
    public class Point {
        private IndexType index;
        private double value;

        @SuppressWarnings("cast")
        protected Point(IndexType index, double value) {
            this.index = (IndexType) index.copy();
            this.value = value;
        }

        /**
         * Returns the data value for this point.
         * 
         * @return  the data value at the location location of this point.
         * 
         * @see #index()
         */
        public final double value() {
            return value;
        }

        /**
         * Returns the data index for this point.
         * 
         * @return  the data index of the point.
         * 
         * @see #value()
         */
        public final IndexType index() {
            return index;
        }
    }


    /**
     * A class for iterating over generic any-dimensional data indices. Typically, this is not the most efficient
     * way to crawl though data. It is generally preferred to use {@link #loop(jnum.PointOp)} or 
     * {@link #fork(jnum.parallel.ParallelPointOp)} for processing indexed data entries, 
     * {@link #loopValid(jnum.PointOp)} or {@link #forkValid(jnum.parallel.ParallelPointOp)}, or their variants, 
     * or even {@link #iterator()} for processing indexless entries.
     * 
     * However, in case when none of the above suffice, this class provides an explicit way to cycle though
     * multidimensional indices.
     * 
     * @author Attila Kovacs
     *
     */
    public class IndexIterator implements Iterator<IndexType> {
        private IndexType from, to, idx, limit;

        /**
         * Constructor.
         */
        public IndexIterator() {
            this(getIndexInstance(), getSize());
        }

        /**
         * Constructor for iterating though a sub-section of the 
         * 
         * @param from   The inclusive starting data index for the iterator
         * @param to     The exclusive ending data index for the iterator
         */
        public IndexIterator(IndexType from, IndexType to) {
            setRange(from, to);
            idx = getIndexInstance();
            this.limit = getSize();
        }


        private void setRange(IndexType from, IndexType to) {
            this.from = from.copy();
            this.to = to.copy();

            for(int i=from.dimension(); --i >= 0; ) {
                if(from.getComponent(i) < 0) this.from.setComponent(i, 0);
                else if(to.getComponent(i) >= limit.getComponent(i)) this.to.setComponent(i, limit.getComponent(i)); 
            } 
        }

        @Override
        public boolean hasNext() {
            for(int i=0; i<idx.dimension(); i++) if(idx.getComponent(i) < to.getComponent(i)) return true;
            return false;
        }

        @Override
        public IndexType next() {
            for(int i=idx.dimension(); --i >= 0; ) {
                if(idx.increment(i) < limit.getComponent(i)) break;
                if(i > 0) idx.setComponent(i,  from.getComponent(i));
            }
            return from;
        }

    }



}
