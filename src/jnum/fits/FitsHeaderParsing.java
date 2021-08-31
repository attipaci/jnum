package jnum.fits;

import nom.tam.fits.FitsException;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCardException;

/**
 * Interface for objects that can configure themselves based on FITS header values.
 * 
 * @author Attila Kovacs
 *
 */
public interface FitsHeaderParsing {

    /**
     * (Re)cofigures the implementing object instance based on the values described in a FITS header, if possible.
     * If not all configuration parameters of the implementing class are present in the header, the object may throw
     * an appropriate exception to indicate an incomplete configuration state, or else go with the 
     * parameters present, while leaving other parameters as is, or setting them to some default values.  
     * It is up to the implementing class to decide which type of action is most appropriate.
     * 
     * @param header                the FITS header
     * @throws FitsException        if there was an issue accessing the header in the FITS.
     * @throws HeaderCardException  if there was an issue with accessing a specific header value in the FITS.
     * @throws Exception            if any other exception occured, depending on the implementing class.
     */
    public void parseHeader(Header header) throws FitsException, HeaderCardException, Exception;
    
}
