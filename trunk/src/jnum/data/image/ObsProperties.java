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


import jnum.Util;
import jnum.fits.FitsProperties;
import jnum.util.HashCode;

public class ObsProperties extends MapProperties {
    
    /**
     * 
     */
    private static final long serialVersionUID = -3669505513556810418L;
    
    private double noiseRescale = 1.0;
   
 
    @Override
    public int hashCode() {
        return super.hashCode() ^ HashCode.from(noiseRescale);
    }
 
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof ObsProperties)) return false;
       
        ObsProperties p = (ObsProperties) o;
        if(noiseRescale != p.noiseRescale) return false;
        return super.equals(o);
    }
    
    @Override
    public void copy(FitsProperties template) {
        super.copy(template);
        if(!(template instanceof ObsProperties)) return;
        
        ObsProperties p = (ObsProperties) template;
        noiseRescale = p.noiseRescale;
    }
    
    
    public double getNoiseRescale() { return noiseRescale; }
 
    public void setNoiseRescale(double value) { noiseRescale = value; }
    
    public void noiseRescaledBy(double value) { noiseRescale *= value; }
    
 
    
    @Override
    public void copyProcessingFrom(MapProperties other) {
        super.copyProcessingFrom(other);
        
        if(other instanceof ObsProperties) noiseRescale = ((ObsProperties) other).noiseRescale;
    }
    
    @Override
    public void resetProcessing() {
        super.resetProcessing();
        setNoiseRescale(1.0);
    }     
    
    @Override
    public String brief(String header) {
       return super.brief(header) +
                (noiseRescale == 1.0 ? "" : 
                "Noise Re-scaling: " + Util.f2.format(noiseRescale) + "x (from image variance).\n");
    }
    
}
