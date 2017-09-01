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

package jnum.fits;

import java.io.Serializable;

import jnum.Copiable;
import jnum.CopyCat;
import jnum.Util;
import jnum.text.TableFormatter;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;
import nom.tam.util.Cursor;

public class FitsProperties implements Cloneable, Copiable<FitsProperties>, CopyCat<FitsProperties>, Serializable, TableFormatter.Entries, 
FitsHeaderEditing, FitsHeaderParsing {

    /**
     * 
     */
    private static final long serialVersionUID = 6489647113389674884L;

    private String creator = defaultCreator;
    
    private String copyright = defaultCopyright;
    
    private String fileName;
   
    private String objectName;  

    private String telescopeName;

    private String instrumentName;

    private String observerName;
    
    private String observationDateString;
  
    
    @Override
    public int hashCode() {
        int hash = super.hashCode();
        if(creator != null) hash ^= creator.hashCode();
        if(copyright != null) hash ^= copyright.hashCode();
        if(fileName != null) hash ^= fileName.hashCode();
        if(objectName != null) hash ^= objectName.hashCode();
        if(telescopeName != null) hash ^= telescopeName.hashCode();
        if(instrumentName != null) hash ^= instrumentName.hashCode();
        if(observerName != null) hash ^= observerName.hashCode();
        if(observationDateString != null) hash ^= observationDateString.hashCode();
        return hash;
    }
    
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof FitsProperties)) return false;
        if(!super.equals(o)) return false;
        
        
        FitsProperties p = (FitsProperties) o;
        
        if(!Util.equals(creator, p.creator)) return false;
        if(!Util.equals(copyright, p.copyright)) return false;
        if(!Util.equals(fileName, p.fileName)) return false;
        if(!Util.equals(objectName, p.objectName)) return false;
        if(!Util.equals(telescopeName, p.telescopeName)) return false;
        if(!Util.equals(instrumentName, p.instrumentName)) return false;
        if(!Util.equals(observerName, p.observerName)) return false;
        if(!Util.equals(observationDateString, p.observationDateString)) return false;
        
        return true;        
    }
    

    @Override
    public void copy(FitsProperties template) {
        creator = template.creator;
        copyright = template.copyright;
        fileName = template.fileName;
        objectName = template.objectName;
        telescopeName = template.telescopeName;
        instrumentName = template.instrumentName;
        observerName = template.observerName;
        observationDateString = template.observationDateString;
    }

    
    @Override
    public FitsProperties clone() {
        try { return (FitsProperties) super.clone(); }
        catch(CloneNotSupportedException e) { return null; }
    }


    @Override
    public FitsProperties copy() {
        return clone();
    }


    public void resetProcessing() {}
        

    public String getFileName() { return fileName; }

    public void setFileName(String value) { this.fileName = value; }
    
    public String getCreatorName() { return creator; }

    public void setCreatorName(String value) { this.creator = value; }
    
    public String getCopyright() { return copyright; }

    public void setCopyright(String value) { this.copyright = value; }
    
    public String getObjectName() { return objectName; }

    public void setObjectName(String value) { this.objectName = value; }

    public String getTelescopeName() { return telescopeName; }

    public void setTelescopeName(String value) { this.telescopeName = value; }

    public String getInstrumentName() { return instrumentName; }

    public void setInstrumentName(String value) { this.instrumentName = value; }

    public String getObserverName() { return observerName; }

    public void setObserverName(String value) { this.observerName = value; }
    
    public String getObservationDateString() { return observationDateString; }

    public void setObservationDateString(String value) { this.observationDateString = value; }
 
    @Override
    public void parseHeader(Header header) {    
        setObjectName(header.getStringValue("OBJECT"));
        setTelescopeName(header.getStringValue("TELESCOP"));
        setInstrumentName(header.getStringValue("INSTRUME"));
        setObserverName(header.getStringValue("OBSERVER"));
        setObservationDateString(header.getStringValue("DATE_OBS"));
    }

    @Override
    public void editHeader(Header header) throws HeaderCardException {  
        Cursor<String, HeaderCard> c = FitsToolkit.endOf(header);
        FitsToolkit.add(c, "OBJECT", getObjectName(), "Observed object's name.");
        FitsToolkit.add(c, "TELESCOP", getTelescopeName(), "Name of telescope.");
        FitsToolkit.add(c, "INSTRUME", getInstrumentName(), "Name of instrument used.");        
        FitsToolkit.add(c, "OBSERVER", getObserverName(), "Name of obserer(s).");
        FitsToolkit.add(c, "DATE-OBS", getObservationDateString(), "Start of observation.");
        FitsToolkit.add(c, "CREATOR", getCreatorName(), getCopyright());
    }
  
        
    @Override
    public Object getTableEntry(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    // TODO add more content?
    public String info(String header) {
        StringBuffer buf = new StringBuffer();
        if(getFileName() != null) buf.append(" Image File: " + getFileName() + ". ->" + "\n\n"); 
        return new String(buf) + brief(header);
    }
    
   
    public String brief(String header) {
        StringBuffer buf = new StringBuffer();
        if(getObjectName() != null) buf.append("[" + getObjectName() + "]\n" + header);   
        return new String(buf);
    }
    
    
    public static String defaultCreator = "jnum " + Util.getFullVersion();
    public static String defaultCopyright = Util.copyright;




}
