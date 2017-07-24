package test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


import nom.tam.fits.Fits;
import nom.tam.fits.FitsException;
import nom.tam.fits.FitsFactory;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.util.BufferedDataOutputStream;


public class LongCommentCardTest {
    
    public static void main(String[] args) {
        try { new LongCommentCardTest().test(200, true); }
        catch(Exception e) { e.printStackTrace(); }
    }
    
    public void test(int length, boolean enableLongKeywords) throws FitsException, IOException {
        
        // Enable/disable the OGIP 1.0 convention for long entries as requested...
        FitsFactory.setLongStringsEnabled(enableLongKeywords);
         
        // Create a header only HDU (no data)
        Header header = new Header();
        header.setSimple(true);
        header.setBitpix(32);
        header.setNaxes(0);
        
        // Add a regular keyword of the desired length...
        c.add(new HeaderCard("BLABERY", counts(length), ""));
   
        // Add a non-nullable HISTORY entry with the desired length...
        c.add(new HeaderCard("HISTORY", counts(length), false));
        
        // Add a non-nullable COMMENT entry with the desired length...
        c.add(new HeaderCard("COMMENT", counts(length), false)); 
        
        // Add a nullable HISTORY entry with the desired length...
        c.add(new HeaderCard("HISTORY", counts(length), true)); 
        
        // Add a nullable COMMENT entry with the desired length...
        c.add(new HeaderCard("COMMENT", counts(length), true));
        
        
        // Write the result to 'longcommenttest.fits' in the user's home...
        Fits fits = new Fits();
        fits.addHDU(Fits.makeHDU(header));
        BufferedDataOutputStream stream = new BufferedDataOutputStream(
                new FileOutputStream(System.getProperty("user.home") + File.separator + "longcommenttest.fits")
        );
        
        try { fits.write(stream); }
        catch(FitsException e) { throw e; }
        finally { stream.close(); }   
        
    }
    
    
    public String counts(int n) {
        StringBuffer buf = new StringBuffer();
        for(int i=1; i <= n; i++) buf.append((i % 10));
        return new String(buf);
    }
    
    
    
}
