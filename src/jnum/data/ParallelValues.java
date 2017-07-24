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
import jnum.util.CompoundUnit;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;
import nom.tam.util.Cursor;

public abstract class ParallelValues extends ParallelObject implements Verbosity {

    private Number blankingValue;

    private boolean isVerbose;

    private int interpolationType;    
  
    private Unit unit;  

    private ArrayList<String> history;
 
    
    public ParallelValues() {
        setVerbose(false);
        setBlankingValue(Double.NaN);
        setInterpolationType(SPLINE);
        history = new ArrayList<String>(); 
        setDefaultUnit();
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
        
        ParallelValues data = (ParallelValues) o;
             
        if(getInterpolationType() != data.getInterpolationType()) return false;
        if(!Util.equals(getBlankingValue(), data.getBlankingValue())) return false;
        if(!Util.equals(getUnit(), data.getUnit())) return false;
         
        return contentEquals(data);
    }
    
    public abstract boolean contentEquals(ParallelValues data);
    
    
    @SuppressWarnings("unchecked")
    @Override
    public ParallelValues clone() {
        ParallelValues clone = (ParallelValues) super.clone();
        if(history != null) clone.history = (ArrayList<String>) history.clone();
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
    
    public abstract Range getRange();
    
    
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

    
   


 
    
    public final static int NEAREST = 0;
    public final static int LINEAR = 1;
    public final static int QUADRATIC = 2;
    public final static int SPLINE = 3;

}
