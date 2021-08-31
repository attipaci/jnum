package jnum.fits;

import nom.tam.fits.FitsException;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCardException;

/**
 * Interface for objects that can describe themselves in FITS headers.
 * 
 * @author Attila Kovacs
 *
 */
public interface FitsHeaderEditing {

    /**
     * Adds a description of the implementing object instance into the specified FITS header.
     * 
     * @param header            the FITS header in which to describe the object
     * @throws FitsException        if there was an issue with accessing the header in FITS.
     * @throws HeaderCardException  if there was an issue creating or adding a header card into the FITS header.
     * @throws Exception            if an exception of another kind occured, depending
     *                              on the class that implements this call.
     */
    public void editHeader(Header header) throws FitsException, HeaderCardException, Exception;
    
}
