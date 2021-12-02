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
 * A set of policies that control the tradeoff between speed and accuracy when smoothing data.
 * 
 * @author Attila Kovacs
 *
 */
public enum SmoothingPolicy {

    /** 
     * Smooth using FFTs to perform the convolutions. For larger datasets or larger smoothing kernels this is
     * generally the fastest. However, the FFT based method does not deal well with uneven coverage (weights)
     * quite as well as methods based in configuration space, which can compensate for localized holes or
     * coverage variations.
     */
    ALWAYS_FFT("fft"),
    
    /**
     * Use whatevewr is deemed the fastest, be it FFT based or configuration space based method.
     */
    FASTEST("fastest"),
    
    /**
     * Like {@link #FASTEST}, but with a slight bias towards the more accurate configuration-space based method.
     * It switches to FFT only if it's at least an order of magnitude faster than the configuration space based method.
     */
    BALANCED("balanced"),
    
    /**
     * Always use a configuration-space based method, never FFT. This may be slower but more accurate when the
     * coverage (weights) is strongly location variable or has localized holes. However, depending on the
     * type of smoothing kernel, it might use a coarse smoothing, and interpolate on the finer scales.
     * 
     */
    IN_CONFIGURATION_SPACE("integral"),
    
    /**
     * Smooth strictly in configuration space, calculating a precised smoothed value at every data point.
     * This is the most accruare of the smoothing methods when coverage is uneven. However, it can get 
     * computationally very expensive for large data and/or large kernels.
     * 
     */
    METICULOUS("precise");
   
    private String name;
    
    SmoothingPolicy(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    public static SmoothingPolicy forName(String name, SmoothingPolicy defaultPolicy) {
        name = name.toLowerCase();
        
        if(name.equals("fft")) return ALWAYS_FFT;
        if(name.equals("fastest")) return FASTEST;
        if(name.equals("balanced")) return BALANCED;
        if(name.equals("integral")) return IN_CONFIGURATION_SPACE;
        if(name.equals("precise")) return METICULOUS;
        
        if(name.equals("fast")) return FASTEST;
        if(name.equals("in-space")) return IN_CONFIGURATION_SPACE;
        if(name.equals("meticulous")) return METICULOUS;   
    
        // Default policy
        return defaultPolicy;
    }
    
    
}
