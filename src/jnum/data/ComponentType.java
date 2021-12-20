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

/**
 * Type-safe enumeration of noisy data components.
 * 
 * @author Attila Kovacs
 *
 */
public enum ComponentType {
    /** Undefined / unknown component */
    UNKNOWN(0, "Unknown"),
    
    /** The signal component */
    SIGNAL(1, "Signal"),
    
    /** A weight component, such as 1/&sigma<sup>2</sup> noise weights. A weight component has units that are the inverse square of the signal units. */
    WEIGHT(2, "Weight"),
    
    /** A data component containing exposure times */
    EXPOSURE(3, "Exposure"),
    
    /** A data component containing noise or uncertainty, such as 1&sigma deviations. Noise components have the same untis as the signal. */
    NOISE(4, "Noise"),
    
    /** A noise variance components, such as a &sigma<sup>2</sup> measure. Variances have units that the the square of the signal unit. */
    VARIANCE(5, "Variance"),
    
    /** A signal-to-noise ratio component (dimensionless).  */
    S2N(6, "S/N");
    
    private int bitMask;
    private String description;
    
    ComponentType(int bit, String desc) {
        this.bitMask = 1 << bit;
        this.description = desc;
    }
    
    /**
     * Returns a unique bit mask for this component type.
     * 
     * @return  the unique bit in a 32-bit integer that can be used to represents this component type.
     */
    public int mask() {
        return bitMask;
    }
    
    /**
     * Returns a human-redable concise description of this type of component.
     * 
     * @return  a human readable brief description, such as "signal", or "noise".
     */
    public String description() {
        return description;
    }

    /**
     * Returns the component type that best matches the supplied textual description.
     * 
     * @param text      a string that contains a description of the data type, in commonly understood terms
     *                  such as "noise" or "uncertainty". The algorithm looks for decrtiptive terms in any case
     *                  and any location in the specified string.
     * @return          The best guess component type or {@link #UNKNOWN} if it's unclear from the string.
     */
    public static ComponentType guessType(String text) {
        text = text.toLowerCase();     

        if(text.contains("weight")) return WEIGHT;
      
        // "Signal-to-noise" and variants...
        if(text.contains("to-noise")) return S2N;
        if(text.contains("to noise")) return S2N;
        if(text.contains("/noise")) return S2N;
        if(text.contains("/ noise")) return S2N;
       
        if(text.contains("noise")) return NOISE;               // noise weight -> weight
        if(text.contains("rms")) return NOISE;
        if(text.contains("error")) return NOISE;
        if(text.contains("uncertainty")) return NOISE;
        if(text.contains("sensitivity")) return NOISE;
        if(text.contains("depth")) return NOISE;
        if(text.contains("scatter")) return NOISE;
        if(text.contains("sigma")) return NOISE;

        if(text.contains("variance")) return VARIANCE;
        if(text.equals("var")) return VARIANCE;
        
        if(text.contains("s/n")) return S2N;
        if(text.contains("s2n")) return S2N;
        
        if(text.contains("coverage")) return EXPOSURE;         // depth coverage -> noise
        if(text.contains("time")) return EXPOSURE;
        if(text.contains("exposure")) return EXPOSURE;

        if(text.contains("signal")) return SIGNAL;
        if(text.contains("flux")) return SIGNAL;
        if(text.contains("intensity")) return SIGNAL;
        if(text.contains("brightness")) return SIGNAL; 

        return UNKNOWN;
    }
    
}
